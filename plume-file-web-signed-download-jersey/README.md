Plume File Web Signed Download Jersey
=====================================

Serve temporary, cryptographically signed files using a Jersey-based JAX-RS web service.
This module validates incoming download request parameters (expiration timestamp and signature) before serving the file, preserving streaming downloads and client caching.

Part of the [Plume File](../) ecosystem.

Dependencies
------------

This module is a web layer that depends on:
- [plume-file-web-download-jersey-common](../plume-file-web-download-jersey-common/) for common download services and response builders.
- [plume-file-signed-url](../plume-file-signed-url/) for signed URL generation and validation. Refer to its [README](../plume-file-signed-url/README.md) for signing logic, API usage, and `file.signed-url.*` configuration.
- [plume-file-core](../plume-file-core/) for file retrieval. Refer to its [README](../plume-file-core/README.md) for core storage and metadata configurations.

Security Warning
----------------

This module is designed to be **mutually exclusive** with `plume-file-web-download-jersey`. 
You must not deploy both endpoints (`FileWs` and `SignedFileWs`) pointing to the same file storage path, as malicious users could easily bypass the signature checks by calling the plain non-signed `/files/{uniqueName}` endpoint.

Installation
------------

Add the Maven dependency:

```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-web-signed-download-jersey</artifactId>
</dependency>
```

Quick start
-----------

1) Install the Guice module in your application:

```java
// In your ApplicationModule
install(new GuiceSignedFileDownloadModule());
```
*Note: Installing `GuiceSignedFileDownloadModule` automatically installs the dependency service module `GuiceSignedFileUrlModule`.*

2) Register the signed file download web service:

```java
// In your JerseyConfigProvider
config.resource(SignedFileWs.class);
```

Configuration
-------------

### HTTP Caching

This module provides browser/client HTTP caching configurations for the served files:

```hocon
# Configures the "Cache-Control" max-age header for browser caching (default is 365 days)
file.cache.http.max-age = "365 days"
```

For cryptographic settings, URL base paths, and expiry durations, configure the properties of the underlying `plume-file-signed-url` module (see its [README](../plume-file-signed-url/README.md)).

How it works
------------

When a client calls `GET` on a signed URL (e.g. `/api/files/35a0da5e-1036-43ae-ae04-8c4076f5a9b3.pdf?expiresAt=1700000000&signature=YmFzZTY0aG1hYw`), the `SignedFileWs` endpoint:
1. Validates the signature and expiration using `SignedFileUrlService` from `plume-file-signed-url`.
2. If invalid or expired, returns a **403 Forbidden** status immediately.
3. If valid, streams the file using `FileService` from `plume-file-core` with correct MIME types, ETag headers, and content-disposition settings (**200 OK**).

Notes
-----

- Support for the `@QueryParam("attachment")` query parameter is preserved. If `attachment=true` is passed, the `Content-Disposition` header will be set to prompt a browser download.
- Clock skew or server sync issues may affect expiry. Ensure your servers have synchronized clocks or use a generous default validity duration.

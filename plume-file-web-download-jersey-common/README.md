Plume File Web Download Jersey Common
=====================================

Common services, configuration, and helpers for serving stored files with Jersey. This module has no public JAX-RS resources (`@Path`) to avoid path collisions, allowing both plain and signed download endpoints to safely share logic on the classpath without risk of duplicate registration.

Part of the [Plume File](../) ecosystem.

Installation
------------

Add the Maven dependency:

```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-web-download-jersey-common</artifactId>
</dependency>
```

Features
--------

- **`FileDownloadJerseyService`**: Interface for downloading stored files (fetching metadata and data).
- **`FileDownloadService`**: Implementation backed by core `FileService`.
- **`FileCacheDownloadService`**: Caching decorator for downloads.
- **`FileResponseService`**: Helper to construct standardized JAX-RS `Response` headers (ETag, MIME type, browser cache-control, and Content-Disposition attachments).
- **HOCON Configuration**: Shared properties under `file.cache.*` and `file.url.*`.

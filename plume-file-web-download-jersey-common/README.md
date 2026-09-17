Plume File Web Download Jersey Common
=====================================

Common services, configuration, and helpers for serving stored files with Jersey. This module has no public JAX-RS resources (`@Path`) to avoid path collisions, allowing both plain and signed download endpoints to safely share logic on the classpath without risk of duplicate registration.

Part of the [Plume File](../) ecosystem.

Installation
------------

This module is normally pulled in **transitively** by the two web modules
`plume-file-web-download-jersey` and `plume-file-web-signed-download-jersey`, so in most cases you
do **not** need to declare it as a direct dependency. You only add it directly when you write a
custom Guice module (e.g. to plug your own `FileCacheService` implementation):

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
- **`FileCacheConfigurationService`**: Dedicated configuration for the in-memory cache (`file.cache.data.*`, `file.cache.metadata.*`).
- **HOCON Configuration**: Shared properties under `file.cache.*` and `file.url.*`.

Guice wiring
------------

This module exposes the shared Guice modules that bind the download services:

- `com.coreoz.plume.file.download.jersey.guice.GuiceFileDownloadModule` — binds `FileDownloadJerseyService` to `FileDownloadService` (no in-memory cache).
- `com.coreoz.plume.file.download.jersey.guice.GuiceFileCacheDownloadModule` — binds `FileDownloadJerseyService` to `FileCacheDownloadService` and `FileCacheService` to the Guava implementation (with in-memory cache).


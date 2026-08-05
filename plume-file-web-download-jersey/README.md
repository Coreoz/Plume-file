Plume File Web Download (Jersey)
================================

Serve files over Jersey with optional in-memory caching. 
This module also provides a ready-to-use JAX-RS resource to expose your files via HTTP. 

Part of the [Plume File](../) ecosystem.

Installation
------------

Add the Maven dependency:

```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-web-download-jersey</artifactId>
</dependency>
```

Quick start
-----------

1) Choose your caching strategy (pick ONE):

- Without cache (simple passthrough)
  ```java
  // In your ApplicationModule
  install(new GuiceFileDownloadModule());
  ```

- With cache (Guava-backed `FileCacheServiceGuava`)
  ```java
  // In your ApplicationModule
  install(new GuiceFileCacheDownloadModule());
  ```

2) Register the file download web service:

```java
// In your JerseyConfigProvider
config.resource(FileWs.class);
```

Configuration
-------------

### HTTP caching for downloads

```hocon
# Configures the "Cache-Control" max-age header
file.cache.http.max-age = "365 days"
```

### In-memory cache (when using the cache module)

```hocon
# Data cache (file bytes)
file.cache.data.expires-after-access-duration = "1 day"
file.cache.data.max-cache-size = "10 MB"

# Metadata cache (file metadata only)
file.cache.metadata.expires-after-access-duration = "1 day"
file.cache.metadata.max-elements = 10000
```

Defaults are shown above. 
Override them in your application configuration file.

Use your own cache implementation
---------------------------------

You can plug your own `FileCacheService` implementation.

```java
// In your ApplicationModule
bind(FileDownloadJerseyService.class).to(FileCacheDownloadService.class);
bind(FileCacheService.class).to(YourFileCacheService.class);
```

Notes
-----

- Install only one of the two modules: `GuiceFileDownloadModule` (no cache) or `GuiceFileCacheDownloadModule` (with cache).
- The configuration examples use HOCON-style syntax; adjust durations/sizes to your needs.
- This module is part of the [Plume File](../) ecosystem.

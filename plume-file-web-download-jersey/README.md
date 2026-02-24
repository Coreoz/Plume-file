Plume File Web Download Jersey
==============================

A [Plume File](../) module to help serve file with Jersey. It can be done with or without a java data cache. 
This dependency also includes a Webservice that serves the file.

Setup
-----

1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-web-download-jersey</artifactId>
</dependency>
```
2. In the `ApplicationModule` class, install the following Guice module:
```java
install(new GuiceFileDownloadModule());
```
for serving files without a cache

3. Declare the FileWs in the `JerseyConfigProvider`
```java
config.resource(FileWs.class);
```

Using the provided FileWs
-------------------------

The provided `FileWs` webservice allows you to serve files with a simple API.

1. To serve a file, you just need to call the `GET /files/{uniqueName}` endpoint with the file unique name as path parameter.
2. The WS checks if the uid provided is 36 characters long, if not it returns a 404 Not Found error.
3. The ws checks the extension that could have been provided in the uid, and if it is not the same as the file extension, it returns a 404 Not Found error. This allows to avoid serving the file if the extension is not correct, which can be useful for security reasons.
4. The file is served with the following header : 
- Cache-Control
- Content-Type
- ETag

The WS returns a 404 Not Found error if the file is not found.

### If-None-Match Header

The `FileWs` also supports the `If-None-Match` header to avoid serving the file if it has not been modified since the last request.
The ETag is generated using the file checksum, so if the file content has not changed, the ETag will be the same and the WS will return a 304 Not Modified response.

### `attachment` parameter

The `FileWs` also supports an `attachment` query parameter to force the file to be downloaded instead of being displayed in the browser.
If the `attachment` parameter is set to `true`, the `Content-Disposition` header will be set to `attachment` with the file name, which will force the browser to download the file instead of displaying it.

Configuration
-------------

### Configure the file WS

You can override these values in your configuration file :
```
// configures the "Cache-Control" max-age header
file.cache.http.max-age = "365 days"
```
The default values are shown.

### Caching files and metadata

#### Use the provided cache

To use the GuiceFileCacheDownloadModule, add this code in your ApplicationModule :
```java
install(new GuiceFileCacheDownloadModule());
```
and remove the previous `GuiceFileDownloadModule` if you were using it before.

Then, you can configure the cache with these values in your configuration file :

```hocon
// configures the expiration of the java data cache
file.cache.data.expires-after-access-duration = "1 day"

// configures the maximum size of the java data cache in bytes
file.cache.data.max-cache-size = "10 MB"

// configures the expiration of the java metadata cache
file.cache.metadata.expires-after-access-duration = "1 day"

// configures the maximum elements in the java metadata cache
file.cache.metadata.max-elements = 10000
```

Then, when a file is served, it will be stored in the cache and served from the cache for subsequent requests until it expires or the cache size limit is reached. The same applies for the file metadata.

#### Create your own cache

This module can be used with your own implementation of the `FileCacheService` interface.

Then add this code in your ApplicationModule 
```java
bind(FileDownloadJerseyService.class).to(FileCacheDownloadService.class);
bind(FileCacheService.class).to(YourFileCacheService.class);
bind(FileUrlService.class).to(FileDownloadUrlService.class);
```


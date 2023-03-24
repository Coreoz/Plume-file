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
TODO Autant mettre juste config.resource(FileWs.class) non ? 
```java
config.packages("com.coreoz.plume.file.webservices");
```

```java
install(new GuiceFileCacheDownloadModule());
```
for serving files with the `FileCacheServiceGuava` cache service.

Configuration
-------------

### Configure the file WS

You can override these values in your configuration file :
```
// configures the "Cache-Control" max-age header
file.cache.http.max-age = "365 days"

// returns the file with its upload name if set to true, with the generated unique name if set to false
file.keep-original-name-on-download = "false"

// if a file identifier given through the webservice is not equals to this lenght, 404 is returned 
file.uid.length = 36
```
The default values are shown.

### Configure the file cache

```
// configures the expiration of the java data cache
file.cache.data.expires-after-access-duration = "1 day"

// configures the maximum size of the java data cache in bytes
file.cache.data.max-cache-size = "10 MB"

// configures the expiration of the java metadata cache
file.cache.metadata.expires-after-access-duration = "1 day"

// configures the maximum elements in the java metadata cache
file.cache.metadata.max-elements = 10000
```
The default values are shown.

### Create your own cache

This module can be used with your own implementation of the `FileCacheService` interface.

Then add this code in your ApplicationModule 
```java
bind(FileDownloadJerseyService.class).to(FileCacheDownloadService.class);
bind(FileCacheService.class).to(YourFileCacheService.class);
```


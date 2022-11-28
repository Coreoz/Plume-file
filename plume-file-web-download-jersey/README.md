Plume File Web Download Jersey
==============================

A [Plume File](../) module to help serve file with Jersey. It can be done with or without a java data cache. 
This dependency also includes a Webservice that serves the file.

Installation
------------

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
```java
install(new GuiceFileCacheDownloadModule());
```
for serving files with a implementable cache FileCacheService

Plume File Web Upload Jersey
==============================

A Plume File module to help serve file with Jersey.

Installation
------------
1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-web-upload-jersey</artifactId>
</dependency>
```
2. In the `ApplicationModule` class, install the following Guice module:
```java
install(new GuiceFileWebserviceModule());
```
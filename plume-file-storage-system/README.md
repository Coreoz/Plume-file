Plume File Storage System
===========================

A [Plume File](../) module to store file data on the system disk through java.io.File class.

Installation
------------
1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-storage-system</artifactId>
</dependency>
```
2. In the `ApplicationModule` class, install the following Guice module:
```java
install(new GuiceFileStorageSystemModule());
```

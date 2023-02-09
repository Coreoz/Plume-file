Plume File Storage Database
===========================

A [Plume File](../) module to store file data into database.

The file-storage-database module implements the `FileStorageService` of the [core module](../plume-file-core).

Setup
-----
1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-storage-database</artifactId>
</dependency>
```
2. In the `ApplicationModule` class, install the following Guice module:
```java
install(new GuiceFileStorageDatabaseModule());
```

3. Create the `plm_file_data` table by applying the correct [creation script](sql/)

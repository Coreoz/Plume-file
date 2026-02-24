Plume File Storage System
=========================

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

Configuration
-------------

Specify these values in your configuration file :
```hocon
# The directory in which the files are stored locally
file.storage.local-path = "plume-file-data/"
```

Usage
-----

### Using the FileStorageSystemService

The `FileStorageSystemService` is used through the `FileStorageService` interface of the core module, so you can use it in the same way as any other `FileStorageService` implementation.

### Using subdirectories

The `FileStorageSystemService` also supports storing files in subdirectories.
To store a file in a subdirectory, set the configuration value `file.storage.use-subdirectories` to true.

Then, the files will be stored in subdirectories based on the file type.
For example, if the file type is `USER_AVATAR`, the file will be stored in the `user_avatar` subdirectory.

If you want to use custom subdirectory names, you can use the `file.storage.subdirectories-file-type-mapping` configuration value to specify a mapping between file types and subdirectory names.
```hocon
# Whether to use subdirectories based on file type
file.storage.use-subdirectories = true
# Mapping between file types and subdirectory names
file.storage.subdirectories-file-type-mapping = {
    USER_AVATAR: "folder_for_user_avatars",
    DOCUMENT: "documents"
}
```




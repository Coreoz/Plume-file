Plume File Core
===============

Plume file core is the main composant of [Plume File](../).

Setup
-----
1. Install Maven dependency:
```xml
<dependency>
    <groupId>com.coreoz</groupId>
    <artifactId>plume-file-core</artifactId>
</dependency>
```

2. In the `ApplicationModule` class, install the following Guice module:
```java
install(new GuiceFileModule());
```

3. Add the required storage modules
One metadata storage module and one data storage module must be configured. See the main documentation to view the [available storage modules](../README.md/#plume-file-modules). 

Custom storage systems can be configured. See [Custom storage implementation](#custom-storage-implementation).

4. Create a FileType implementation

Each file must be associated to a `FileType` to ease file management.

This type is materialized by an enum that must implements `FileType` interface. For example:
```java
public enum ProjectFileType implements FileType {
    LANDSCAPES,
    PROFILE_PICTURES,
    ;
}
```

5. Schedule the file cleaning

The plume file core module provides a schedule job that will clean unreferenced files.
Unreferenced files are files that are awaiting deletion. This is specified in the file metadata module used.

Scheduling unreferenced files is important to make sure deleted files (data and metadata) are correctly removed.

In order to schedule it, execute in your `WebApplication` entry point:
```java
injector.getInstance(FileScheduledTasks.class).scheduleJobs();
```

The cleaning hour is configurable. The default value is :
```
file.cleaning-hour = "03:00"
```

If you need to create your own job to clean other data before, just call `fileService::deleteUnreferenced` in you job.

Usage
-----

### Uploading files

```java
String fileUniqueName = fileService.add(MyProjectFileType.LANDSCAPES, fileInputStream, "grand_canyon_2020.png", "png", "image/png");
```
If you do not have all these information, the file service will guess it for you:
```java
String fileUniqueName = fileService.add(MyProjectFileType.LANDSCAPES, fileInputStream, "grand_canyon_2020.png");
```

### Fetching files

You can either retrieve the file itself or only its information by its unique name :
```java
Optional<InputStream> file = fileService.fetchData("c70f9b94-30e2-4e10-b84d-b964ef972067");
Optional<FileMetadata> fileMetadata = fileService.fetchMetadata("c70f9b94-30e2-4e10-b84d-b964ef972067");
```

### Deleting files
The library does not implement file deletion by the unique name to avoid metadata de-synchronization.

However, the `deleteUnreferenced` method will delete all the files that are not referenced in the metadata.
If the reference to the file is deleted, then the file will be deleted with this method.

This way, the file metadata drives the file deletion and not the other way around

If some file types were lost along the way, the `deleteFilesForDeletedTypes` method will delete all 
the files which type is no longer in the `FileType` enum. This method will only delete the files with no known `FileType` and with no reference in the database.

Custom storage implementation
-----------------------------
As mentioned in the [main documentation](../), the core module needs implementation for the interfaces:
- `FileMetadataService`
- `FileStorageService`

To create use a custom storage implementation:
- It is generally best to use the provided [metadata database module](.../plume-file-metadata-database)
- Then the custom data storage will be configured by implementing `FileStorageService`.

Then you must reference these implementations in the `ApplicationModule` of your project

```java
bind(FileMetadataService.class).to(MyFileMetadataService.class); // Do not write this line if the standard metadata database module is used
bind(FileStorageService.class).to(MyFileStorageService.class);
```

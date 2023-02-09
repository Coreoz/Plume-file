Plume File Core
===============

Plume file core is at the center of the Plume File architecture as it is where all the common logic is implemented.
The core exposes the 2 interfaces to be implemented to match any usages.

Glossary
--------

- File unique identifier is the 36 chars long UID that identifies a file 

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

3. Implements the required interfaces

As mentioned before, the core cannot work on its own. You must provide implementation for interfaces :
- `FileMetadataService`
- `FileStorageService`

Then you must reference these implementations in the `ApplicationModule` of your project

```java
bind(FileMetadataService.class).to(MyFileMetadataService.class);
bind(FileStorageService.class).to(MyFileStorageService.class);
```

Some implementations are available in this repository.
See root project [README](../README.md) for more information on these service implementations.

4. Create a FileType implementation

Each file that goes through the core must have a type so it can be referenced.

This type is materialized by an enum that must implements `FileType` interface :
```java
public enum MyProjectFileType implements FileType {
    LANDSCAPES,
    PROFILE_PICTURES,
    ;
}
```

5. Schedule the file cleaning

The plume file core library provides a schedule job that will clean unreferenced files.
Unreferenced files are files which are found when searching for a unique name, but have no reference in the metadata.
These files should be cleaned to free space.

However, it is not mandatory.

In order to schedule it, execute in your scheduled jobs class :
```java
@Singleton
public class ScheduledJobs {

    private final Scheduler scheduler;
    private final MyService service1;

    @Inject
    public ScheduledJobs(Scheduler scheduler, MyService service1, FileScheduledTasks fileScheduledTasks) {
        this.scheduler = scheduler;
        this.service1 = service1;

        fileScheduledTasks.scheduleJobs();
    }

    public void scheduleJobs() {
        // add your own scheduled jobs here
    }

}
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
If you do not have all these information, `FileNameUtils` can help you guess the extension and the mime type from the file name. 
Then just call :
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

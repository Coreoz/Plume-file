Plume File Core
===============

Plume file core is at the center of the Plume File architecture as it is where all the common logic is implemented.
The core exposes the 2 interfaces to be implemented to match any usages.

Glossary
--------

- File unique identifier is the 36 chars long UID that identifies a file 

Setup
-----
```xml
<dependency>
    <groupId>com.coreoz</groupId>
    <artifactId>plume-file-core</artifactId>
</dependency>
```

Jackson
-------
The module `GuiceFileModule` provides a `GuiceSchedulerModule`.
Install the module in your ApplicationModule class :
```java
install(new GuiceFileModule());
```

Usage
-----

### Core
As mentioned before, the core cannot work on its own. You must provide implementation for interfaces :
- FileMetadataService
- FileStorageService

Then you must reference these implementations in the ApplicationModule of your project

```java
bind(FileMetadataService.class).to(MyFileMetadataService.class);
bind(FileStorageService.class).to(MyFileStorageService.class);
```

Some implementations are available in this repository.
See root project [README](../README.md) for more information on these service implementations.

### FileType

Each file that goes through the core must have a type so it can be referenced.

This type is personified by an enum that must implements `FileType` interface :
```java
public enum MyProjectFileType implements FileType {
    LANDSCAPES,
    PROFILE_PICTURES,
    ;
}
```

### Scheduler

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

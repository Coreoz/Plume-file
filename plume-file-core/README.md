Plume File Core
===============

Store uploaded files into database and serve them with a single web-service.

Installation
------------
1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-core</artifactId>
</dependency>
```
2. Create a package where the file module will be configured,
a good choice is something like: `project.package.services.file`
3. In the package that was just created, add the enum that will reference the file types:
```java
@AllArgsConstructor
@Getter
public enum <YourProject>FileType implements FileType {

	;

	private final EntityPath<?> fileEntity;
	private final NumberPath<Long> joinColumn;

}
```
4. In the package that was just created, add an implementation of `FileTypesProvider`
that will list the file types:
```java
@Singleton
public class <YourProject>FileTypesProvider implements FileTypesProvider {

	@Override
	public Collection<FileType> fileTypesAvailable() {
		return ImmutableList.copyOf(ProjectFileType.values());
	}

}
```
5. In the `ApplicationModule` class, install the file Guice module
and reference the implementation of `FileTypesProvider` that was just created:
```java
// file management
install(new GuiceFileModule());
bind(FileTypesProvider.class).to(ProjectFileTypesProvider.class);
```

6. Install your File Service

The file service is separated in 2 parts:
- FileServiceDisk to store your files on the server disk
- FileServiceDatabase to store your files directly into a blob in the database

a. FileServiceDisk

To use File Service Disk you must bind FileService to FileServiceDisk in the `ApplicationModule`:
 ```java
 // file management
 bind(FileService.class).to(FileServiceDisk.class);

 ```

b. FileServiceDatabase

To use File Service Database you must bind FileService to FileServiceDatabase in the `ApplicationModule`:
 ```java
 // file management
 bind(FileService.class).to(FileServiceDatabase.class);
 ```

You must bind FileDaoDatabase to its implementation for MySQL:
 ```java
 bind(FileDaoDatabase.class).to(FileDaoDatabaseQuerydsl.class);
```

7. Install Jersey web-service: `register(FileWs.class);`
8. Create the `plm_file` table by applying the correct [creation script](sql/) and the associated table (Disk or Database)
9. [Start using files](#usage)

Usage
-----
1. Add a file identifier column in the SQL table in which a file will be added.
This column must be a large number (`BIGINT`/`NUMBER(19,0)`) and should reference the file table.
For example in MySQL:
```sql
ALTER TABLE `prefix_city`
  ADD COLUMN `landscape_image_id` bigint(20),
  ADD CONSTRAINT `city_landscape_image_fk` FOREIGN KEY (`landscape_image_id`) REFERENCES `PLM_FILE` (`id`);
```
2. Update the `QuerydslGenerator` class to reference the `FileEntityQuerydsl` class
by uncommenting lines 51 to 53 ; then run the `QuerydslGenerator.main()` method.
3. Reference the new file type in the `ProjectFileType` enum. For Example:
```java
CITY_LANDSCAPE_IMAGE(QCity.city, QCity.city.landscapeImageId)
```
4. Start using `FileService`.

The instance `FileService` should be obtained by injection:
```java
@Inject
public MyService(FileService fileService) {
  this.fileService = fileService;
}
```
To store files and get files URL, these methods can be called:
```java
long fileId = fileService.upload(ProjectFileType.CITY_LANDSCAPE_IMAGE, fileData).getId();
String fileUrl = fileService.url(fileId).orElse(null);
```

A full example is available in the
[Plume Demo project](https://github.com/Coreoz/Plume-demo/tree/master/plume-demo-full-guice-jersey).

Files cleaning
--------------
Orphan files are being deleted each night at 03:00.
That means that entries in `PLM_FILE` **should not be deleted manually** by calling `fileService.delete(fileId)`.

Configuration
-------------
```
application.api-base-path = "/api" # should match swagger base path
file.ws-path = "/files"
file.max-age-cache = 365 days # should be put to 0 if a file with the same id is updated
file.cleaning-hour = 03:00 # the time of day at which the cleaning task is scheduled
file.media-local-path #local path on disk
```


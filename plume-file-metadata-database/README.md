Plume File Metadata Database
===========================

A [Plume File](../) module to store file metadata into database, with a strong reference to a database object through a foreign key.

The metadata-database module implements the `FileMetadataService` of the [core module](../plume-file-core). 

TODO expliquer comment utiliser FileTypeDatabase et FileTypesProvider 

Setup
-----
1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-metadata-database</artifactId>
</dependency>
```
2. In the `ApplicationModule` class, install the following Guice module:
```java
install(new GuiceFileMetadataDatabaseModule());
```

3. Create the `plm_file` table by applying the correct [creation script](sql/)

4. Create a foreign key between the plm_file table and your project table :
```sql
CREATE TABLE `PLM_USER_FILE`
(
    `user_id`          bigint(20)   NOT NULL,
    `file_unique_name` VARCHAR(255) NOT NULL,
    FOREIGN KEY (file_unique_name) REFERENCES PLM_FILE (unique_name)
)
    DEFAULT CHARSET = utf8;
```

5. Create a `FileTypeDatabase` enum

A `FileTypeDatabase` is an implementation of the `FileType` interface needed in the core.
The `FileTypeDatabase` will reference the QueryDsl `EntityPath` database column that references to the file metadata table.

For example, if my `plm_file` table is referenced by my project table column `plm_user_file.file_unique_name`,
my project file type will look like this :

```java
public enum MyProjectFileType implements FileTypeDatabase {
    ENUM(QUserFile.userFile, QUserFile.userFile.fileUniqueName)
    ;

    private final EntityPath<?> fileEntity;
    private final StringPath joinColumn;

    ShowcaseFileType(EntityPath<?> fileEntity, StringPath joinColumn) {
        this.fileEntity = fileEntity;
        this.joinColumn = joinColumn;
    }

    @Override
    public EntityPath<?> getFileEntity() {
        return fileEntity;
    }

    @Override
    public StringPath getJoinColumn() {
        return joinColumn;
    }
}
```

6. Create an implementation of `FileTypeProvider`

The `FileTypeProvider` will be used to get the `FileTypeDatabase` types directly injected in the classes.
```java 
public class MyProjectFileTypesProvider implements FileTypesProvider {
    @Override
    public Collection<FileTypeDatabase> fileTypesAvailable() {
        return List.of(MyProjectFileType.values());
    }
}
```

Don't forget to bind the implementation to the interface in your `ApplicationModule` :
```java
bind(FileTypesProvider.class).to(MyProjectFileTypesProvider.class);
```




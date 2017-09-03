Plume File Gallery
==================

Manage medias galleries.

Getting started
---------------
### Core component installation
1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-gallery</artifactId>
</dependency>
```
2. Install Guice module: `install(new GuiceFileGalleryModuleQuerydsl());`
3. Provide an implementation of `FileTypesProvider` in Guice
4. The implementation of `FileTypesProvider` should also return the values `GalleryFileTypeQuerydsl`
5. Provide an implementation of `FileGalleryTypesProvider` in Guice

### Jersey web-service installation
1. Provide an implementation of `FileGalleryTypesAdminProvider` in Guice
2. In Guice map the type `FileGalleryTypesProvider` with your implementation of `FileGalleryTypesAdminProvider`
3. Install Jersey web-service: `register(FileGalleryAdminWs.class);`
4. Make sure that this line is present in the Jersey configuration:
`bindFactory(WebSessionAdminFactory.class).to(WebSessionPermission.class).in(RequestScoped.class);`

An example is available in the [Plume Demo project](https://github.com/Coreoz/Plume-demo/tree/master/plume-demo-full-guice-jersey).

Base enum example
-----------------
To manage gallery types, an enum like this one should be provided:
```java
@AllArgsConstructor
@Getter
public enum ProjectGalleryType implements FileGalleryTypeAdmin {

	;

	private final RelationalPathBase<?> fileGalleryDataEntity;
	private final NumberPath<Long> joinColumn;
	private final String galleryPermission;
	private final BiPredicate<WebSessionPermission, Long> allowedToChangeGallery;
	private final Predicate<String> filenameAllowed;

}
```

Configuration
-------------
```
# this should be done before the file service cleaner,
# this way in the file service cleaner, deleted gallery files can be cleaned up
file.gallery.cleaning-hour = "02:30"
```

Hibernate
---------
To use Hibernate:
1. Replace the installation of `GuiceFileGalleryModuleQuerydsl` by `GuiceFileGalleryModuleHibernate`
2. `FileTypesProvider` must return the values of `GalleryFileTypeHibernate` instead of `GalleryFileTypeQuerydsl`


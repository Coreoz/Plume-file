Plume File Gallery
==================

Manage medias galleries.

Getting started
---------------
- **[Querydsl ONLY]** Guice: `install(new GuiceFileGalleryModuleQuerydsl());`
- **[Hibernate ONLY]** Guice: `install(new GuiceFileGalleryModuleHibernate());`
- Guice: an implementation of `FileTypesProvider` should be provided
- The implementation of `FileTypesProvider` should also return the values
of `GalleryFileTypeQuerydsl` or `GalleryFileTypeHibernate` (whether you are using Querydsl or Hibernate)
- Guice: an implementation of `FileGalleryTypesProvider` should be provided

Jersey
------
- Guice: an implementation of `FileGalleryTypesAdminProvider` should be provided and also mapped to 
`FileGalleryTypesProvider` type
- Jersey: `register(FileGalleryAdminWs.class);`
- Jersey: `bindFactory(WebSessionAdminFactory.class).to(WebSessionPermission.class).in(RequestScoped.class);`
- Base enum for gallery types:
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
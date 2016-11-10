Plume File Gallery
==================

Manage medias galleries.

Getting started
---------------
- **[Querydsl ONLY]** Guice : `install(new GuiceFileGalleryModuleQuerydsl());`
- **[Hibernate ONLY]** Guice : `install(new GuiceFileGalleryModuleHibernate());`
- Guice : an implementation of `FileTypesProvider` should be provided
- The implementation of `FileTypesProvider` should also return the values
of `GalleryFileTypeQuerydsl` or `GalleryFileTypeHibernate` (whether you are using Querydsl or Hibernate)
- Guice : an implementation of `FileGalleryTypesProvider` should be provided


Configuration
-------------
```
# this should be done before the file service cleaner,
# this way in the file service cleaner, deleted gallery files can be cleaned up
file.gallery.cleaning-hour = "02:30"
```
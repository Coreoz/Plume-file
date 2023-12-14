Plume File
==========
Plume File is a modular Java library that provides several modules to easily handle files in a project from storage to download. Its goal is to provide great performance with a great Java API: it can handle files of several gigabytes with a JVM that has an `Xmx` of only `128Mo`.

Plume File that has some small dependencies on the [Plume Framework](https://github.com/Coreoz/Plume), especially to the conf and scheduler modules.

Looking for a demo? Check out the file service implementation on the [Plume Demo project](https://github.com/Coreoz/Plume-showcase).

Architecture
------------
Plume file library follows a modular architecture to make it easy to change the storage engine or the API technology.

Some necessary file storage choices has been taken for this library:
- The storage of the files binary data is separated from the storage of files metadata
- Files are uniquely identified by an `UUID` where the file extension is appended. So for example, the file `cat.jpg` might be stored in the file storage as the file `35a0da5e-1036-43ae-ae04-8c4076f5a9b3.jpg`. The metadata will then enable to retrieve the original name.

To categorize file, each file is associated with a `FileType`. This consists of a user defined `enum` with values like `LANDSCAPES` or `PROFILE_PICTURE`.
This category is especially used by the [plume-file-metadata-database](plume-file-metadata-database) to provide a convenient way to delete files.

Getting started
---------------
The main composant is the module [plume-file-core](plume-file-core) which exposes file storage interfaces, `FileMetadataService` and `FileStorageService` that must be implemented. So to use the main entry point `FileService`:
- The module [plume-file-core](plume-file-core) must be installed
- One of the [metadata modules](#metadata-module) must be installed
- One of the [file storage modules](#storage-modules) must be installed

[Jersey connectors](#web-service-modules) are provided to upload/expose files through API.

File URL
--------
Some modules implement the `FileUrlService` to be able to retrieve the full URL of a file stored using Plume File.
That is the case of:
- [plume-file-web-download-jersey](plume-file-web-download-jersey)
- In the future, the AWS S3 file storage module

Plume file modules
------------------
These are modules to be used with the main [plume-file-core](plume-file-core) module.

#### Storage modules

The storage modules implements `FileStorageService`:

- [plume-file-storage-database](plume-file-storage-database): store uploaded file in a database
- [plume-file-storage-system](plume-file-storage-system): store uploaded file on a system disk

#### Metadata module

The metadata module implements `FileMetadataService`:

- [plume-file-metadata-database](plume-file-metadata-database): store file metadata in a database

#### Web-service modules

Those modules provide API connectors to serve and upload files. 

- [plume-file-web-download-jersey](plume-file-web-download-jersey): serve stored file through
  a Jersey based single web-service
- [plume-file-web-upload](plume-file-web-upload): upload file using HTTP multipart

Configuration and default values
--------------------------------
**[plume-file-core](plume-file-core)**
```hocon
# The time of day at which the cleaning task is scheduled
file.cleaning-hour = "03:00"
# The algorithm used for checksum file calculation
file.checksum-algorithm = "SHA-256"
```

**[plume-file-storage-system](plume-file-storage-system)**
```hocon
# The directory in which the files are stored locally
file.storage.local-path = "plume-file-data/"
```

**[plume-file-web-download-jersey](plume-file-web-download-jersey)**
```hocon
# The Cache-Control response header value for max-age, it represents the time files will be stored in cache on browsers
file.cache.http.max-age = "365 days"
# Internal server file content configuration: the duration after which a file will be removed from the cache
file.cache.data.expires-after-access-duration = "1 day"
# Internal server file content configuration: the maximum size of the cache
file.cache.data.max-cache-size = "64 MB"
# Internal server file metadata configuration: the duration after which metadata will be removed from the cache
file.cache.metadata.expires-after-access-duration = "1 day"
# Internal server file metadata configuration: the maximum number of metadata elements stored in the cache
file.cache.metadata.max-elements = 10000
# The base file URL used by FileUrlService.url(). If the config value application.base-url is defined, this can be replaced by file.url.base-path = ${application.base-url}"/api/files"
file.url.base-path = "/api/files"
```

Migrating guides
----------------
All migration guides are available in the [Plume File Github releases](https://github.com/Coreoz/Plume-file/releases).

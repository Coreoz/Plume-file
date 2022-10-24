Plume File
==========

Plume file is a modular Java library based on [Plume Framework](https://github.com/Coreoz/Plume),
it provides several modules to easily handle files in a project from the storage to the download.

Looking for a demo? Check out the file service implementation on [Plume Demo project](https://github.com/Coreoz/Plume-showcase).

Philosophy
----------
Plume file library is a core, and a set of Java modules to handle files in a Java project.

The core implementation is in the main module plume-file-core.

The core works with 2 classes:
- FileMetadataService
- FileStorageService

Those services can be implemented to match any usages.

Getting started
---------------
First you will need to install the core module of the library: [plume-file-core](plume-file-core).

From this point, you can use it as you want.

Plume file modules
------------------
Though the core can be used for any usages, you can also use pre-implemented modules,
implementing the services of the core.

There are 5 implemented modules.

#### Storage modules

The storage modules implements the FileStorageService:

- [plume-file-storage-database](plume-file-storage-database): store uploaded file in a database
- [plume-file-storage-system](plume-file-storage-system): store uploaded file on a system disk

#### Metadata module

The metadata module implements the FileStorageService:

- [plume-file-metadata-database](plume-file-metadata-database): store file metadata in a database

#### Web-service modules

Those modules are using the plume-file-core module to serve and upload files. 

- [plume-file-web-download-jersey](plume-file-web-download-jersey): serve stored file through
  a Jersey based single web-service
- [plume-file-web-upload-jersey](plume-file-web-upload-jersey): upload file through a jersey
  MultiPart object


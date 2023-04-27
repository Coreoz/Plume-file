Plume File
==========
Plume File is a modular Java library based on [Plume Framework](https://github.com/Coreoz/Plume).
It provides several modules to easily handle files in a project from storage to download.

Looking for a demo? Check out the file service implementation on [Plume Demo project](https://github.com/Coreoz/Plume-showcase).

Architecture
------------
Plume file library follows a modular architecture to make it easy to change the storage engine or the API technology.

Some necessary file storage choices has been taken for this library:
- The storage of the files binary data is separated from the storage of files metadata
- Files are uniquely identified by an `UUID` where the file extension is appended. So for example, the file `cat.jpg` might be stored in the file storage as the file `35a0da5e-1036-43ae-ae04-8c4076f5a9b3.jpg`. The metadata will then enable to retrieve the original name.

To categorize file, each file is associated with a `FileType`. This consists of a user defined `enum` with values like `LANDSCAPES` or `PROFILE_PICTURE`.
This category is especially used by the [plume-file-metadata-database](plume-file-metadata-database) to provide a convenient way to delete files.

TODO:
- ajouter un dossier `changelogs` et y mettre les scripts SQL de migration
- Dans la release sur Github faire référence à tout ça et les grandes étapes à suivre pour faire la migration : changer les dépendances du pom.xml etc.
- Dans le fichier principal README.md référencer la release dans une partie "migrating from v1 or v2 to v3"
- Dans le dossier "changelogs", avoir un fichier README.md qui référénce la release pour utiliser les scripts de migration

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
- [plume-file-web-upload-jersey](plume-file-web-upload-jersey): upload file through a Jersey
  MultiPart object


Plume File Web Upload Jersey
============================

A [Plume File](../) module to help upload file with Jersey, using `multipart/form-data`.

Setup
-----

0. Read the whole readme and especially the [What should be checked before implementing each upload](#what-should-be-checked-before-implementing-each-upload) part
1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-web-upload-jersey</artifactId>
</dependency>
```
2. Create the webservice file with the injected dependency `FileMimeTypeDetector` (it comes from Plume file Core)

Uploading with Multipart
------------------------

The fastest way to upload a file with [] is to use the Jersey Multipart API.
This API allows you to get the file InputStream directly from the webservice parameters, by reading the multipart content.

1. Add the Jersey Multipart to your dependencies if not already in your project
```xml
<dependency>
  <groupId>org.glassfish.jersey.media</groupId>
  <artifactId>jersey-media-multipart</artifactId>
  <version>3.1.3</version>
</dependency>
```
2. Register the MultiPart Feature on your `JerseyConfigProvider`:
```java
config.register(MultiPartFeature.class);
```
3. Create the webservice endpoint to upload a file (using `multipart/form-data`):
```java
@POST
@Operation(description = "Upload a file")
public void upload(
    @FormDataParam("file") FormDataBodyPart fileMetadata,
    @FormDataParam("file") InputStream fileData,
    @FormDataParam("part-2") String part2 // an other part of your multipart request
) {
    FileUploadData fileUploadData = FileUploadValidator
        .from(
            fileMetadata.getContentDisposition().getFileName(),
            fileMetadata.getFormDataContentDisposition().getSize(),
            fileData,
            fileMimeTypeDetector
        )
        .fileMaxSize(2_000_000)
        .fileNameNotEmpty()
        .fileNameMaxDefaultLength()
        .fileExtensionNotEmpty()
        .fileExtensions(Set.of("docx", "pdf"))
        .sanitizeFileName()
        .finish();
    return this.fileUploadWebJerseyService.add(MyProjectFileType.ENUM, fileUploadData);
}
```

With Jersey Multipart API, you can implement file uploading really quickly as the library preprocesses the multipart request 
to make it very accessible through parameters.

However, this request preprocessing means that the request is read before you can access it. 
When uploading large files (> 1Go), this can be a performance issue.

Uploading with Apache FileUpload
--------------------------------

The Apache [FileUpload](https://commons.apache.org/proper/commons-fileupload/) package ...
This will allow you to read the incoming Multipart request as you wish, getting rid of the file preprocessing.

1. Add the Apache Commons FileUpload to your dependencies if not already in your project
```xml
<dependency>
  <groupId>org.glassfish.jersey.media</groupId>
  <artifactId>jersey-media-multipart</artifactId>
  <version>3.1.3</version>
</dependency>
```
2. Create the webservice endpoint to upload a file (using `multipart/form-data`):
```java
@POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(description = "Upload a file")
    @SneakyThrows
    public String upload(
        @Context ContainerRequestContext request,
        @HeaderParam(HttpHeaders.CONTENT_LENGTH) long size
    ) {
        // handle the multipart object contained in the ContainerRequestContext ... 
    }
```
2. Create a FileItemIterator from the ContainerRequestContext
```java
FileUpload fileUpload = new FileUpload();
FileItemIterator fileIterator = fileUpload.getItemIterator(new RequestContext() {
    // create you own request context
})
```
3. Read the multipart object with the iterator
```java
while (iterator.hasNext()) {
    FileItemStream item = iterator.next();
    String name = item.getFieldName();
    if ("file".equals(name)) {
        // handle the file input stream
    } else if ("string-part-2".equals(name)) {
        String part2 = Streams.asString(item.openStream());
    } else if ("instant-part-3".equals(name)) {
        Instant part3 = Instant.parse(Streams.asString(item.openStream()));
    } else {
        logger.debug("File field {} with file name {} detected.", name, item.getName());
    }
}
```

The real advantage of this solution is to be free to handle the multipart.
When uploading large files (> 1 Go), this make a real difference on upload time, as the stream can be read only once.

Before using the library
------------------------

Before uploading a file with Plume File Web Upload Jersey,
one must be very careful with the consequences of allowing upload in an application.

A sum up of file upload security considerations is available on [OWASP](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html).

### What is in the library

The library helps you implement some of them with the FileUploadValidators. For example:
- Checks the maximum file size
- Checks the file extension from the HTTP headers
- Checks the media type from the HTTP headers

These validators should be used to helps you verify that the incoming files are what your application expected.

### What should be checked before implementing each upload

Using `FileUploadValidator` should help you verify many aspects of file uploading. On top of that, these elements should also be taken in consideration when implementing a file upload: 
- Only allow authorized users to upload files
- Run the files through an antivirus or a sandbox if available to validate that it doesn't contain malicious data
- Ensure that Jersey and Plume files libraries are securely configured and up to date
- Protect the file upload from CSRF attacks. This can be mitigated mainly:
  - Using configuring correct `Content-Security-Policy` HTTP headers
  - Using an authentication token that is not stored in cookies (like in [Plume admin](https://github.com/Coreoz/Plume-admin))
  - Using specific CSRF tokens

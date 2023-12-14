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
3. Choose your upload option between:
   - [Jersey Multipart](#uploading-with-multipart): easier to set up, but offers lower performance
   - [Apache FileUpload](#uploading-with-apache-fileupload): more difficult to set up, so it offers the best performance with no overhead

Uploading with Multipart
------------------------

Using the Jersey Multipart API is the easiest way to upload a file.
This API allows you to get the file `InputStream` directly from the webservice parameters, by reading the multipart content.

This method however has a performance drawback: it will first copy the uploaded file to a temporary file on disk, before giving access to this temporary file. 
It is preferably used for small files (< 100Mo).
When uploading large files (> 1Go), this can be a performance issue.

1. Add Jersey Multipart to the project dependencies if not already present
```xml
<dependency>
  <groupId>org.glassfish.jersey.media</groupId>
  <artifactId>jersey-media-multipart</artifactId>
  <version>3.1.3</version>
</dependency>
```
2. Register the MultiPart Feature in the `JerseyConfigProvider` class:
```java
config.register(MultiPartFeature.class);
```
3. Create the webservice endpoint to upload a file using `multipart/form-data`:
```java
@POST
@Operation(description = "Upload a file")
public void upload(
    @FormDataParam("file") FormDataBodyPart fileMetadata,
    @FormDataParam("file") InputStream fileData,
    @FormDataParam("otherValue") String otherValue // another value present in the multipart request
) {
    FileUploadData fileUploadData = FileUploadValidator
        .from(
            fileMetadata,
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

With Jersey Multipart API, you can implement file uploading quickly as the library preprocesses the multipart request 
to make it easy to access.

Uploading with Apache FileUpload
--------------------------------

The Apache [FileUpload Streaming](https://commons.apache.org/proper/commons-fileupload/streaming.html) package will allow you to read the incoming Multipart request, without waiting for the whole request body to have been received.

1. Add Apache Commons FileUpload to the project dependencies if not already present
```xml
<dependency>
  <groupId>commons-fileupload</groupId>
  <artifactId>commons-fileupload</artifactId>
  <version>1.5</version>
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
2. Create a `FileItemIterator` from the `ContainerRequestContext`:
```java
FileUpload fileUpload = new FileUpload();
upload.setHeaderEncoding(StandardCharsets.UTF_8.name());
FileItemIterator fileIterator = fileUpload.getItemIterator(new JaxRsFileItemIterator(request));
while (fileIterator.hasNext()) {
  FileItemStream item = iterator.next();
  // do the rest following https://commons.apache.org/proper/commons-fileupload/streaming.html
}
```

### Example of file upload using the streaming API
In this example:
- A file is uploaded alongside a password and an expiration date
- The file multipart item is the **last multipart item** of the incoming requests 

```java
@Data
public class UploadStreamingData {
    String fileName;
    InputStream fileInputStream;
    String password;
    Instant expirationDate;
}

@SneakyThrows
private static UploadStreamingData readMultipartStreamingData(ContainerRequestContext request) {
    FileItemIterator iterator = JerseyStreamingFileUpload.createIterator(request); // your newly created iterator
    UploadStreamingData uploadData = new UploadStreamingData();
    while (iterator.hasNext()) {
        FileItemStream item = iterator.next();
        switch (item.getFieldName()) {
            // file must be the last element of your HTTP Multipart request
            case  "file" -> {
                uploadData.setFileName(item.getName());
                uploadData.setFileInputStream(item.openStream());
                // Return the value directly to leave the function.
                // Otherwise, the JerseyStreamingFileUpload iterator will read the entire input stream before
                // returning the uploadData object
                return uploadData;
            }
            case "password" -> uploadData.setPassword(Streams.asString(item.openStream()));
            case "expirationDate" -> uploadData.setExpirationDate(Instant.parse(Streams.asString(item.openStream())));
            default -> throw new IllegalStateException("Unexpected value: " + item.getFieldName());
        }
    }
    return uploadData;
}
```

Before using the library
------------------------
Allowing file upload in an application has security implications.
Some of these implications are handled by [Plume File Upload Jersey](#what-validations-are-performed-in-plume-file-upload-jersey).

A sum up of file upload security considerations is available on [OWASP](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html).

### What validations are performed in Plume File Upload Jersey?

The library helps you implement some of them with `FileUploadValidators`. For example:
- Checks the maximum file size
- Checks the file extension from the HTTP headers
- Checks the media type from the HTTP headers

These validators should be used to help verify that the incoming files are what the application expects.

### What should be checked before implementing each upload

Using `FileUploadValidator` should help you verify many aspects of file uploading. On top of that, these elements should also be taken in consideration when implementing a file upload: 
- Only allow authorized users to upload files
- Run the files through an antivirus or a sandbox if available to validate that it doesn't contain malicious data
- Ensure that Jersey and Plume files libraries are up-to-date
- Protect the file upload from CSRF attacks. This can be mitigated mainly:
  - Using configuring correct `Content-Security-Policy` HTTP headers
  - Using an authentication token that is not stored in cookies (like in [Plume admin](https://github.com/Coreoz/Plume-admin))
  - Using specific CSRF tokens

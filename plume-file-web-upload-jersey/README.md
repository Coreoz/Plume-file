Plume File Web Upload Jersey
============================

A [Plume File](../) module to help upload file with Jersey, using Form Data Part.

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
2. Register the MultiPart Feature on your `JerseyConfigProvider`:
```java
config.register(MultiPartFeature.class);
```
3. Create the webservice file that will use the newly imported class
4. Create your webservice endpoint that will upload your file, using the FormData:
```java
@POST
@Operation(description = "Upload a file")
public void upload(
    @FormDataParam("file") FormDataBodyPart fileMetadata,
    @FormDataParam("file") InputStream fileData
) {
    Validators.checkRequired("file", fileData);
    FileUploadMetadata fileUploadMetadata = FileUploadValidator
        .from(fileMetadata)
        .fileMaxSize(2_000_000)
        .fileNameNotEmpty()
        .fileNameMaxDefaultLength()
        .fileExtensionNotEmpty()
        .fileExtensions(Set.of("docx", "pdf"))
        .finish();
    return this.fileUploadWebJerseyService.add(
        MyProjectFileType.ENUM,
        fileData,
        fileUploadMetadata
    );
}
```

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

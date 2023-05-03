Plume File Web Upload Jersey
============================

A [Plume File](../) module to help upload file with Jersey, using Form Data Part.

Setup
-----

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
4. Create your webservice endpoint that will upload your file, using the FormData :
```java
@POST
@Operation(description = "Upload a file")
public Response upload(
    @Context ContainerRequestContext context,
    @FormDataParam("file") FormDataBodyPart fileMetadata,
    @FormDataParam("file") InputStream fileData
) {
    Validators.checkRequired("fileMetadata", fileMetadata);
    Validators.checkRequired("file", fileData);
    FileUploadValidators.verifyFileNameLength(fileMetadata, 255);
    FileUploadValidators.verifyFileSize(fileMetadata, 2_000_000);
    FileUploadValidators.verifyFileExtension(fileMetadata, Set.of("docx", "pdf"));
    FileUploadValidators.verifyFileExtensionFormat(fileMetadata, 5);
    return Response.ok(
        this.fileUploadWebJerseyService.add(
            MyProjectFileType.ENUM,
            fileData,
            fileMetadata
        )
    )
    .build();
}
```

Before using the library
------------------------

Before uploading a file with Plume File Web Upload Jersey,
you must be very careful with the consequences of allowing upload on your application.

Here are the recommendations from [OWASP](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html) 
for you to be aware of the security of your application.

### What is in the library

The library helps you implement some of them with the FileUploadValidators:
- Checks the maximum file size
- Checks the file extension from the HTTP headers
- Checks the media type from the HTTP headers

These validators should be used to helps you verify that the incoming file is what your application expected.

### What you should check before implementing each upload

- List allowed extensions. Only allow safe and critical extensions for business functionality
- Ensure that input validation is applied before validating the extensions.
- Set a filename length limit. Restrict the allowed characters if possible
- Set a file size limit
- Only allow authorized users to upload files
- Run the file through an antivirus or a sandbox if available to validate that it doesn't contain malicious data
- Ensure that any libraries used are securely configured and kept up to date
- Protect the file upload from CSRF attacks

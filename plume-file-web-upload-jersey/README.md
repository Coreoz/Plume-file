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

Here is the recommendations from [OWASP](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html) 
for you to be aware of the security of your application.

### What is in the library

The library helps you implement some of them with the FileUploadValidators:
- Checks the maximum file size
- Checks the file extension from the HTTP headers
- Checks the media type from the HTTP headers

These validators should be used to helps you verify that the incoming file is what your application expected.

### What you should implement for your application

- Even if the library gives you helpers, you should implement them on your upload Web Service
- This library does not implement the user authentication, but your application should not authorize upload from non-authenticated users, 
this can be very dangerous as mentioned in the OWASP documentation
- This library does not implement an antivirus, but your application should run the incoming file through an antivirus or a Sandbox, 
as mentioned in the OWASP documentation

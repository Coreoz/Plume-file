Plume File Web Upload Jersey
==============================

A [Plume File](../) module to help upload file with Jersey, using Form Data Part.

TODO speak about Validators included that should be used
TODO reference OWASP file upload
TODO speak about anti virus
TODO speak about how dangerous it is to allow file upload from users

TODO ça me semble obligatoire de lister les choses à prendre en considération avant de permettre un upload de fichier :
- List allowed extensions. Only allow safe and critical extensions for business functionality
- Ensure that input validation is applied before validating the extensions.
- Set a filename length limit. Restrict the allowed characters if possible
- Set a file size limit
- Only allow authorized users to upload files
- Run the file through an antivirus or a sandbox if available to validate that it doesn't contain malicious data
- Ensure that any libraries used are securely configured and kept up to date
- Protect the file upload from CSRF attacks

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
    // TODO ça serait cool ici de montrer comment utiliser quelques validateurs. Par exemple :
    // FileUploadValidators.verifyFileNameLength(fileMetadata, 255);
    // FileUploadValidators.verifyFileSize(fileMetadata, 2_000_000);
    // FileUploadValidators.verifyFileExtension(fileMetadata, Set.of("docx", "pdf"));
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

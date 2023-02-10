Plume File Web Upload Jersey
==============================

A [Plume File](../) module to help upload file with Jersey, using Form Data Part.

TODO speak about Validators included that should be used
TODO reference OWASP file upload
TODO speak about anti virus
TODO speak about how dangerous it is to allow file upload from users

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

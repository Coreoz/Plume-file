Plume File Web Upload Jersey
==============================

A [Plume File](../) module to help upload file with Jersey, using Form Data Part.

Installation
------------

1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-web-upload-jersey</artifactId>
</dependency>
```
2. Create the webservice file that will use the newly imported class
3. Create your webservice endpoint that will upload your file, using the FormData :
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

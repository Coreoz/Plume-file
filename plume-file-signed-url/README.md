Plume File Signed URL
=====================

A pure Java service module to generate and validate cryptographically signed, time-limited file URLs. This module has no dependency on Jersey, making it suitable for generating signed URLs that might point to a web service or cloud storages (like AWS S3 or GCP Storage).

Part of the [Plume File](../) ecosystem.

Installation
------------

Add the Maven dependency:

```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-signed-url</artifactId>
</dependency>
```

Quick start
-----------

1) Install the Guice module:

```java
// In your ApplicationModule
install(new GuiceSignedFileUrlModule());
```
`GuiceSignedFileUrlModule` binds `FileUrlService` to `SignedFileUrlService`. Install it whenever you need to generate or validate signed file URLs — including outside of Jersey (e.g. for cloud storage). When you use [plume-file-web-signed-download-jersey](../plume-file-web-signed-download-jersey/), this module is installed automatically and must not be installed twice.

2) Configure the cryptographic secret:

In your `application.conf`:
```hocon
file.signed-url.secret-key = "a-very-secure-random-secret-key-at-least-32-characters"
```

Usage
-----

### Generating a signed URL

Inject `FileUrlService` (which is bound to `SignedFileUrlService` by the Guice module) or `SignedFileUrlService` directly to generate URLs:

```java
@Inject
private FileUrlService fileUrlService;

// Generates a URL with the default configured expiration duration (e.g., /api/files/xyz.pdf?expiresAt=1700000000&signature=...)
String url = fileUrlService.url("35a0da5e-1036-43ae-ae04-8c4076f5a9b3.pdf");
```

You can also specify a custom expiration date by using the `SignedFileUrlService` overload:

```java
@Inject
private SignedFileUrlService signedFileUrlService;

// Generates a URL expiring at a specific Instant
String customUrl = signedFileUrlService.url("35a0da5e-1036-43ae-ae04-8c4076f5a9b3.pdf", Instant.now().plus(Duration.ofMinutes(30)));
```

### Validating a signed URL

If you are not using Jersey (for which `plume-file-web-signed-download-jersey` is provided), you can manually validate the request parameters:

```java
@Inject
private SignedFileUrlService signedFileUrlService;

SignatureValidationResult result = signedFileUrlService.validate(uniqueName, expiresAt, signature);
switch (result) {
    case VALID:
        // Serve the file
        break;
    case EXPIRED:
        // Handle expiration (e.g. return 403 / 410)
        break;
    case INVALID_SIGNATURE:
        // Handle tampering / bad signature (e.g. return 403)
        break;
}
```

Configuration
-------------

```hocon
# Secret key for HMAC-SHA256 signature. REQUIRED, no usable default
file.signed-url.secret-key = null

# Default duration for which the signed URL is valid
file.signed-url.default-validity-duration = "1 hour"

# Base path for the signed download URLs
file.signed-url.base-path = "/api/files"
```

Defaults are shown above. Override them in your application configuration file.
The application will fail fast on startup if `file.signed-url.secret-key` is missing or blank.

Notes
-----

- This module is stateless; it does not persist any URL consumption or download count.
- The constant-time algorithm `MessageDigest.isEqual` is used to prevent timing attacks during signature verification.
- This module is part of the [Plume File](../) ecosystem.

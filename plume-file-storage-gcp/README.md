# Plume File Storage GCP

A Google Cloud Storage backend for the [Plume File](https://github.com/Coreoz/Plume-file) module.

This library provides a `FileStorageService` implementation that stores and retrieves files from
**Google Cloud Storage (GCS)**.  
It supports storing files either at the **root of a bucket** or inside a configurable **subfolder (prefix)**.

---

## Features

* ✅ Configurable **base path**: store files at the bucket root or in a “folder”.
* ✅ Works with **Application Default Credentials**, service account JSON key or Workload Identity.
* ✅ Supports injection of a custom `Credentials` object via `GcpCredentialsProvider`.

---

## Installation

To use this module, add the following dependency to your project.
This will 

Maven:

```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-storage-gcp</artifactId>
</dependency>
```

In the ApplicationModule class, install the following Guice module:

```java 
install(new GuiceFileStorageGcpModule());
```

---

## Google Cloud prerequisites

1. **Create or use a GCS bucket**.

2. **Create a service account** and grant it a role that includes the following permissions:
    * `storage.objects.create`
    * `storage.objects.get`
    * `storage.objects.delete`
(For example: `roles/storage.objectAdmin`)

3. **Provide credentials** to your application:
    * Recommended: set the configuration parameter `file.storage.gcp.credentials-path` to the path of the
      service account JSON key file.
      or
    * Provide your own implementation of `GcpCredentialsProvider` (see below).

---

## Usage

### 1. Upload / Fetch / Delete a file

See Usage of the [Plume File core module](../plume-file-core/README.md/#usage) to upload files.

### 2. Override credentials with `GcpCredentialsProvider`

You can implement and bind your own `GcpCredentialsProvider` to provide `com.google.auth.Credentials`:

```java
public class GcpCredentialsProvider implements Provider<Credentials> {
    @Override
    public Credentials get() {
        // return your custom Credentials, e.g. from a JSON file or ADC
    }
}
```

Then bind it in your Guice module:

```java
import com.google.auth.Credentials;

// your Guice module ...

bind(Credentials.class).toProvider(MyCustomGcpCredentialsProvider.class);
```

---

## Configurations

| Parameter                           | Description                                                                                                                                                                                         |
|-------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `file.storage.gcp.bucket-name`      | Name of the GCS bucket.                                                                                                                                                                             |
| `file.storage.gcp.project-id`       | GCP project ID.                                                                                                                                                                                     |
| `file.storage.gcp.bucket-base-path` | Optional folder/prefix inside the bucket. Use `""` for root or e.g. `"uploads/"`.                                                                                                                   |
| `file.storage.gcp.credentials-path` | File system path to the service account JSON key file (only required for [GcpCredentialsProvider.java](src%2Fmain%2Fjava%2Fcom%2Fcoreoz%2Fplume%2Ffile%2Fcredential%2FGcpCredentialsProvider.java)) |


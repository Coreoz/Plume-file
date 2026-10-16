Plume File Storage System
=========================

A [Plume File](../) module to store file data on the system disk through java.io.File class.

Installation
------------
1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-storage-system</artifactId>
</dependency>
```
2. In the `ApplicationModule` class, install the following Guice module:
```java
install(new GuiceFileStorageSystemModule());
```

Encryption
----------
Files stored on disk are **encrypted by default** using AES-GCM (authenticated encryption).

A Base64 encoded AES-256 key (exactly 32 bytes once decoded) must be provided
through the configuration, otherwise the application fails to start:
```hocon
file.storage.encryption.key = "base64-encoded-aes-key"
```

The key **must be randomly generated and kept secret**, for example with OpenSSL:
```shell
openssl rand -base64 32
```
Any key that is not exactly 32 bytes long (in particular any shorter key) is rejected at startup:
it is pointless to accept a weak key that would not provide the expected security level.

To store files **unencrypted**, encryption must be disabled explicitly:
```hocon
file.storage.encryption.disabled = true
```

Encryption is provided by the reusable [`plume-file-storage-encryption`](../plume-file-storage-encryption)
module (AES-GCM decorator around the storage service), so it stays out of the core and out of the
other storage connectors (database, GCP), which rely on their own at-rest encryption. This disk
module simply wires that decorator by default.

Each encrypted file starts with a small header (magic bytes + IV). Files that do not have this
header are treated as legacy unencrypted files and are returned as-is, allowing encrypted and
plain files to coexist when encryption is enabled on an existing storage.

### Migrating an existing disk storage

Upgrading an existing application to this version enables encryption **by default**, which has two
consequences:

1. **A key becomes mandatory.** If `file.storage.encryption.key` is not set, the application fails
   to start (secure by default). Either provide a Base64 AES-256 key (32 bytes) or opt out explicitly
   with `file.storage.encryption.disabled = true`.
2. **Existing files do not need to be re-encrypted by hand.** Legacy plaintext files stay readable
   thanks to the header detection, while every newly stored file is encrypted automatically.

If you additionally want to encrypt the already stored files retroactively, rewrite each of them
through the regular API (`fetch` then `add` under the same unique name). See the
[`plume-file-storage-encryption`](../plume-file-storage-encryption) README for the detailed passive
and active migration strategies.

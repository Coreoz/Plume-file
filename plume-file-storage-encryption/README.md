Plume File Storage Encryption
=============================

A [Plume File](..) module providing a reusable, **storage-agnostic** encryption layer for any
`FileStorageService` implementation.

It is used **by default** by [`plume-file-storage-system`](../plume-file-storage-system) (disk
storage). It can also be wrapped around any other `FileStorageService` (including custom connectors) to add
application level encryption where it makes sense.

Components
----------
- `FileEncryptionService`: the **extension point** (interface). It defines the encryption strategy
  as two streaming operations, `encryptStream` and `decryptStream`.
- `AesGcmFileEncryptionService`: the **default implementation**, using AES-GCM (authenticated
  encryption). Each encrypted file starts with a small header (3 magic bytes `PFE` + a 1 byte
  format version + a random 12 bytes IV), followed by the GCM ciphertext and its authentication
  tag. The file unique name and the format version are bound to the ciphertext as GCM
  *Additional Authenticated Data* (AAD), so a file cannot be swapped for another one encrypted
  with the same key. Streams that do not start with this header are treated as legacy unencrypted
  files and returned as-is, so encrypted and plain files can coexist (useful when enabling
  encryption on an existing storage).
- `EncryptingFileStorageService`: a `FileStorageService` **decorator** that encrypts data on `add`
  and decrypts it on `fetch`, delegating the actual storage to the wrapped `FileStorageService`.

Installation
------------
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-storage-encryption</artifactId>
</dependency>
```

Usage
-----
Wrap any `FileStorageService` with the decorator and give it a `FileEncryptionService`:
```java
FileEncryptionService encryptionService = AesGcmFileEncryptionService.fromBase64Key(aesKeyBase64);
FileStorageService encryptedStorage = new EncryptingFileStorageService(delegateStorage, encryptionService);
```

Key management rules
--------------------
- The AES key must be **16, 24 or 32 bytes** long once decoded (AES-128/192/256). Any other length
  is rejected with an `IllegalArgumentException` at construction time.
- The key must be **externalized** (environment variable, secrets manager, KMS/HSM) and
  **never hard-coded** nor committed to the repository.
- The same key must be used for reading and writing a given file: AES-GCM is authenticated, so a
  wrong key (or any tampering of the ciphertext) makes decryption fail instead of returning
  corrupted data.
- Losing the key means the encrypted files can no longer be read. Plan key backup and, if needed,
  key rotation (see below) accordingly.

Behaviour and guarantees
-------------------------
- **Streaming**: encryption and decryption are applied on the fly; files are never fully buffered
  in memory.
- **Metadata transparency**: when used through the Plume File core, the checksum and the file size
  are computed on the **plaintext** before storage, so they stay stable regardless of the key. The
  bytes physically written on disk are larger than the plaintext (header + IV + GCM tag).
- **Integrity binding**: the file unique name is authenticated as AAD, so moving or substituting an
  encrypted file under a different name is detected and fails decryption.
- **Format versioning**: a version byte in the header allows the on-disk format to evolve later
  (algorithm change, key rotation, ...) while keeping the ability to read older files.
- **Backward compatibility**: `AesGcmFileEncryptionService` can still read files that were stored
  before encryption was enabled, because it detects the `PFE` header.

Providing a custom encryption strategy
--------------------------------------
Because `FileEncryptionService` is an interface, you are not tied to AES-GCM. You may provide your
own implementation to use a different algorithm (e.g. ChaCha20-Poly1305), a KMS/HSM backed key,
key rotation (several active keys identified by a key id stored in the header), envelope
encryption, etc.

```java
public class MyFileEncryptionService implements FileEncryptionService {
    @Override
    public InputStream encryptStream(InputStream plainData, String fileUniqueName) throws IOException { /* ... */ }

    @Override
    public InputStream decryptStream(InputStream storedData, String fileUniqueName) { /* ... */ }
}
```

Guidelines for custom implementations:
- Stay **streaming oriented**: do not load the whole file in memory.
- Be **thread-safe**: a single instance is typically shared as a singleton.
- If you want encrypted and plain files to coexist, keep a **format marker** at the beginning of
  the stream (as the default implementation does) and return unknown streams unchanged in
  `decryptStream`.
- Prefer **authenticated encryption** so that tampering or a wrong key fails loudly.
- Consider binding the `fileUniqueName` to the ciphertext (for instance as AAD, as the default
  implementation does) to prevent file substitution.

To use a custom strategy with the disk storage module, override the `FileEncryptionService` binding
in your dependency injection configuration; the `EncryptingFileStorageService` decorator will then
use it transparently.

Migrating an existing storage to encryption
-------------------------------------------
**You never have to encrypt existing files by hand.** Thanks to the `PFE` format marker, encrypted
and plain files coexist: on `fetch`, a file that starts with the marker is decrypted, while a file
that does not is considered a legacy plaintext file and returned unchanged.

Two migration strategies are available:

- **Passive migration (default, recommended, zero effort).** Provide the key and deploy. Existing
  files stay in plaintext and remain readable, while every newly written file is encrypted
  automatically. No downtime, no script. The only drawback: old files remain unencrypted at rest
  until they happen to be rewritten.
- **Active migration (encrypt everything retroactively).** If you need the existing files to be
  encrypted too, rewrite each one through the regular API: `fetch` it (the decorator returns the
  plaintext, legacy files included) then `add` it again under the same unique name (the decorator
  stores it encrypted). This is easily scripted by looping over your `fileUniqueName` values; no
  manual cryptography is involved.

The main thing to anticipate is **the key**, not the files: since encryption is enabled by default
on the disk module, an application that starts without a key will fail fast (secure by default). To
migrate, either provide a key (new files encrypted, old ones still readable) or opt out explicitly
with `file.storage.encryption.disabled = true`.

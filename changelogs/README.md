Complementary migration guides
==============================
This guide contains some additional information to the migrations guides when files are involved.

For the complete migration guides, please refer to [Plume File Github releases](https://github.com/Coreoz/Plume-file/releases).

Plume File SQL migration guide to V4
------------------------------------
This upgrade contains the Jakarta update along with Plume 5 and Java 17.
Follow the [Plume v5 migration guide](https://github.com/Coreoz/Plume/blob/master/docs/releases/RELEASE_v5.md) to upgrade.

Plume File SQL migration guide to V3
------------------------------------
This migration guide does not delete old Plume File tables. They will be found under *table_name*_history.

#### Migrating v1.0.0-rc1 ~ v1.0.0-rc9

[Migration script](sql/migration_1.0.0-rc1_1.0.0-rc9.sql)

#### Migrating v2.0.0-alpha1 ~ v2.0.0-beta4

- For database storage: [migration script](sql/migration_database_2.0.0-alpha1_2.0.0-beta4.sql)
- For disk storage: [migration script](sql/migration_disk_2.0.0-alpha1_2.0.0-beta4.sql)
  - **Warning** The uid column must reference the file name on the disk

#### Migrating v2.0.0-beta5

- For database storage: [migration script](sql/migration_database_2.0.0-beta5.sql)
- For disk storage: [migration script](sql/migration_disk_2.0.0-beta5.sql)
  - **Warning** The uid column must reference the file name on the disk

Plume File v5.x
---------------

#### File signature

Added support for temporary, cryptographically signed file download URLs with two new modules:
- `plume-file-signed-url`: pure Java service module to generate and validate HMAC-signed file URLs.
- `plume-file-web-signed-download-jersey`: Jersey based single web-service to serve signed temporary download URLs.
These new modules are stateless and do not require any SQL database migrations.

Additionally, a new `plume-file-web-download-jersey-common` module has been introduced.
It shares the download logic (services, configuration and helpers) that was previously located in
`plume-file-web-download-jersey`, so that both `plume-file-web-download-jersey` and
`plume-file-web-signed-download-jersey` can rely on the same code base.
This change does not introduce any breaking change: the elements that were moved are only deprecated
in `plume-file-web-download-jersey`, not removed. No SQL database migration is required.

#### Disk storage encryption (breaking configuration change)

A new reusable module `plume-file-storage-encryption` adds an AES-GCM encryption layer, and the
disk storage module `plume-file-storage-system` now **encrypts files by default**.

**Breaking change for the disk storage:** a Base64 encoded AES key (16, 24 or 32 bytes once decoded)
must now be provided, otherwise the application fails to start:
```hocon
file.storage.encryption.key = "base64-encoded-aes-key"
```
To keep storing files unencrypted, encryption must be disabled explicitly:
```hocon
file.storage.encryption.disabled = true
```

Migration notes:
- **Existing files do not need to be re-encrypted manually.** Legacy plaintext files remain readable
  (format marker detection), while newly stored files are encrypted automatically (passive migration).
- To encrypt already stored files retroactively, rewrite each one through the regular API
  (`fetch` then `add`), see the module READMEs for details (active migration).
- The database and GCP storage connectors are **not** affected: they keep relying on their own
  at-rest encryption. No SQL database migration is required.

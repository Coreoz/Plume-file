Plume File SQL migration guide
==============================

This migration guide does not delete old Plume File tables. They will be found under *table_name*_history.

#### Migrating v1.0.0-rc1 ~ v1.0.0-rc9

[Migration script](migration_1.0.0-rc1_1.0.0-rc9.sql)

#### Migrating v2.0.0-alpha1 ~ v2.0.0-beta4

- For database storage: [migration script](migration_database_2.0.0-alpha1_2.0.0-beta4.sql)
- For disk storage: [migration script](migration_disk_2.0.0-alpha1_2.0.0-beta4.sql)
  - **Warning** The uid column must reference the file name on the disk

#### Migrating v2.0.0-beta5

- For database storage: [migration script](migration_database_2.0.0-beta5.sql)
- For disk storage: [migration script](migration_disk_2.0.0-beta5.sql)
  - **Warning** The uid column must reference the file name on the disk

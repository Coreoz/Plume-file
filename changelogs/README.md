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

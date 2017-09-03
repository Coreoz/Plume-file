Plume File Core
===============

Store uploaded files into database and serve it with a light but solid web-service.

Getting started
---------------
1. Install Maven dependency:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>plume-file-core</artifactId>
</dependency>
```
2. Install Guice module: `install(new GuiceFileModuleQuerydsl());`
3. Provide an implementation of `FileTypesProvider` in Guice
4. Install Jersey web-service: `register(FileWs.class);`

An example is available in the [Plume Demo project](https://github.com/Coreoz/Plume-demo/tree/master/plume-demo-full-guice-jersey).

Configuration
-------------
```
application.api-base-path = "/api" # should match swagger base path
file.ws-path = "/files"
file.max-age-cache = 365 days # should be put to 0 if a file with the same id is updated
file.cleaning-hour = 03:00 # the time of day at which the cleaning task is scheduled
```

Hibernate
---------
Replace the installation of `GuiceFileModuleQuerydsl` by `GuiceFileModuleHibernate`.


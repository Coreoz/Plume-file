Plume File
===========

Store uploaded files into database and serve it with a light but solid web-service.

Getting started
---------------
An implementation of FileTypesProvider should be provided.

Configuration
-------------
```
application.api-base-path = "/api" # should match swagger base path
file.ws-path = "/files"
file.max-age-cache = 365 days # should be put to 0 if a file with the same id is updated
file.cleaning-hour = 03:00 # the time of day at which the cleaning task is scheduled
```
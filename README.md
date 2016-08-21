Plume File
===========

Store updated files into database and serve it with light but solid web-service.

Configuration
-------------
```
application.api-base-path = "/api" # should match swagger base path
file.ws-path = "/files"
file.max-age-cache = 365 days # should be put to 0 if a file with the same id is updated
```
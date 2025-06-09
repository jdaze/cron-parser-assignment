# Assumptions for design and implementation
- Unix Cron support only, no L, W or /# or @yearly etc.
- really basic exception handling, for input like "/15 0 1,15 * 1-5 /usr/bin/find" it will just fail on "/15"
- 0 or 7 is supported for Sunday -> Output will contain only 0 if there's Sunday

# Requirements
- Compiled JAR can be found inside jar-exec directory
- Application should be started using Java 23
- example usage: **java -jar cronparser-1.0.0-Java23.jar "*/15 0 1,15 * 1-5 /usr/bin/find"**




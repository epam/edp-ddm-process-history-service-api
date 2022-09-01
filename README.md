# process-history-service-api

This service provides web api for getting business process and task history from database.

### Related components:
* `process-history-service-persistence` - service, which interacts with database
* PostgreSQL database for data persistence

### Local development:
###### Prerequisites:
* Database `process_history` is configured and running

###### Process history database setup:
1. Create database `process_history`
1. Run `initial-db-setup` script from the `citus` repository

###### Configuration:
1. Check `src/main/resources/application-local.yaml` and replace if needed:
    * data-platform.datasource... properties with actual values from local DB
    * data-platform.kafka.boostrap and audit.kafka.bootstrap with url of local Kafka

###### Steps:
1. (Optional) Package application into jar file with `mvn clean package`
1. Add `--spring.profiles.active=local` to application run arguments
1. Run application with your favourite IDE or via `java -jar ...` with jar file, created above

Application starts by default on port 7071, to get familiar with available endpoints - visit swagger (`localhost:7071/openapi`).

### License
process-history-service-api is Open Source software released under the Apache 2.0 license.

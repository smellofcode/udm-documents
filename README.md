# Ultimate Document Management - documents microservice

## Setting local environment

### Create local database

1. Use `psql` and connect as an admin.
2. `CREATE USER udmdocuments WITH PASSWORD 'udmdocuments';`
3. `CREATE SCHEMA udmdocuments;`
4. `GRANT ALL PRIVILEGES ON SCHEMA udmdocuments TO udmdocuments;`

### Set up environment variables

1. Copy `.env.example` and store it in root as `.env` (note, that this file is git ignored).
2. Edit `.env` and properly set up database credentials, schema name that you have just defined above.

## Building the project

1. Run `gradlew build` to build the whole project.
2. Run `gradlew spotlessApply` to apply spotless formatter on the code.
3. Run `gradlew openApiGenerate` to regenerate RESTful API from OpenAPI spec.

## Launching the application

1. Run `gradlew :configuration:web-api:bootRun` to start the microservice.

## Setup
The application requires Java 17 (Eclipse Temurin)

Gradle 8.8.1 can be installed via any Gradle Wrapper command, such as `./gradlew --version`

## Run
`./gradlew run` will start the application.

`./gradlew randomNumbersClient` will start clients that send random valid 9 digit numbers to the application on localhost.

`./gradlew terminatingClient` will start a client that only sends `terminate` to the server.


The application outputs to `./app/numbers.log`.

The application logs to `./app/app.log`.

## Assumptions
1. Only one instance of this application will be running at the same time.
1. The application accepts input from up to 5 concurrent clients at one time, but one client could disconnect and another client could take its place.
1. If more than 5 clients connect to the server, clients after the first 5 are queued but their input is not accepted/processed.
1. If a client connection fails, the server is not terminated.
1. The application will never resume with the same data after shutdown.  If the application shuts down then is restarted, numbers.log would be cleared on start and all unique/duplicate stats would be reset.
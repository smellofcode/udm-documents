plugins {
    id("com.diffplug.spotless")
    id("org.sonarqube") version "3.0"
    id("jacoco")
}
group = "com.udm"
version = "0.0.1-SNAPSHOT"

sonarqube {
    properties {
        property("sonar.coverage.exclusions", "configuration/web-api/**/*")
        property("sonar.cpd.exclusions", "**/adapters/**/*Payload.java")
    }
}

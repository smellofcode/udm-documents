
val springKafkaVersion: String by project
val springBootVersion: String by project
val springVersion: String by project
val jdbiVersion: String by project
val liquibaseVersion: String by project
val logbackEncoderVersion: String by project
val postgresqlVersion: String by project
val springDotEnvVersion: String by project

plugins {
    id("udm.java")
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
}

dependencies {

    //projects
    implementation(project(":filestore"))
    implementation(project(":filestore:adapter:in:web-mvc"))
    implementation(project(":filestore:adapter:out:db-jdbi"))
    implementation(project(":filestore:adapter:out:file"))

    implementation(project(":folders"))
    implementation(project(":folders:adapter:in:web-mvc"))
    implementation(project(":folders:adapter:out:db-jdbi"))

    //Spring boot bundles
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    implementation("org.springframework.security:spring-security-config")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")

    //Spring resource server
    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    //Additional dependencies for runtime
    implementation("me.paulschwarz:spring-dotenv:$springDotEnvVersion")

    //JDBC, Hibernate, Jakarta, JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:$springBootVersion")
    implementation("org.jdbi:jdbi3-core:$jdbiVersion")
    implementation("org.jdbi:jdbi3-sqlobject:$jdbiVersion")
    implementation("org.jdbi:jdbi3-postgres:$jdbiVersion")
    implementation("org.jdbi:jdbi3-stringtemplate4:$jdbiVersion")
    //MEP
    implementation("org.springframework.kafka:spring-kafka:$springKafkaVersion")
    //Driver
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    //Migrations
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    //Entity Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    //Logging
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")
}

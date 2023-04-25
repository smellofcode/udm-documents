val springBootVersion: String by project
val liquibaseVersion: String by project
val h2Version: String by project

plugins {
    id("udm.java")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("com.h2database:h2:$h2Version")
    testImplementation("org.liquibase:liquibase-core:$liquibaseVersion")
}

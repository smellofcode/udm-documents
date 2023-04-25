val springBootVersion: String by project
val jdbiVersion: String by project
val liquibaseVersion: String by project
val h2Version: String by project

plugins {
    id("udm.java")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:$springBootVersion")
    implementation("org.jdbi:jdbi3-core:$jdbiVersion")
    implementation("org.jdbi:jdbi3-sqlobject:$jdbiVersion")
    implementation("org.jdbi:jdbi3-postgres:$jdbiVersion")
    implementation("org.jdbi:jdbi3-stringtemplate4:$jdbiVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("com.h2database:h2:$h2Version")
    testImplementation("org.liquibase:liquibase-core:$liquibaseVersion")
}

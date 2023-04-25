val servletAPIVersion: String by project
val springBootVersion: String by project

plugins {
    id("udm.java")
}

dependencies {
    implementation("jakarta.servlet:jakarta.servlet-api:$servletAPIVersion")
    implementation("org.springframework.boot:spring-boot-starter-json:$springBootVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-tomcat:$springBootVersion")
}

val springBootVersion: String by project
val springVersion: String by project
val mapstructVersion: String by project
val springSecurityVersion: String by project
val springSecurityTestVersion: String by project
val springSecurityOAuthTestVersion: String by project

plugins {
    id("udm.java")
}

dependencies {
    implementation("org.springframework:spring-webmvc:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    implementation("org.springframework.security:spring-security-oauth2-resource-server:$springSecurityVersion")
    testImplementation("org.springframework.security:spring-security-config:$springSecurityTestVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("org.springframework.security:spring-security-test:$springSecurityTestVersion")
    testImplementation("org.springframework.security:spring-security-oauth2-jose:$springSecurityTestVersion")
    //mapstruct
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
}

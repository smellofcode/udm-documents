val jpaAPIVersion: String by project
val springVersion: String by project

plugins {
    id("udm.java")
}

dependencies {
    // Spring essentials
    compileOnly("org.springframework:spring-context:$springVersion")
    testCompileOnly("org.springframework:spring-context:$springVersion")
    compileOnly("org.springframework:spring-tx:$springVersion")
    testCompileOnly("org.springframework:spring-tx:$springVersion")

    // JPA API
    compileOnly("jakarta.persistence:jakarta.persistence-api:$jpaAPIVersion")
    testCompileOnly("jakarta.persistence:jakarta.persistence-api:$jpaAPIVersion")
}

plugins {
    id("udm.java")
}

val springBootVersion: String by project

tasks.jar {
    archiveFileName.set("filestore-adapter-out-file.jar")
}

dependencies {
    implementation(project(":filestore"))

    implementation("org.springframework.boot:spring-boot-starter:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")
}
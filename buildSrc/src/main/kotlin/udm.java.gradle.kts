import org.gradle.api.tasks.testing.logging.TestLogEvent

val springBootVersion: String by project
val lombokVersion: String by project
val junitVersion: String by project
val assertJVersion: String by project
val archunitVersion: String by project
val mockitoVersion: String by project
val slf4jVersion: String by project

plugins {
    id("java-library")
    id("com.diffplug.spotless")
    id("jacoco")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
    }
}

spotless {
    isEnforceCheck = true
    java {
        importOrder()
        palantirJavaFormat()
        removeUnusedImports()
        formatAnnotations()
        targetExclude("**/generated/**")
        licenseHeaderFile(rootProject.file("LICENSE"))
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Slf4J
    compileOnly("org.slf4j:slf4j-api:$slf4jVersion")
    // lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    // arch unit
    testImplementation("com.tngtech.archunit:archunit-junit5:$archunitVersion")
    // junit
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    // mockito
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

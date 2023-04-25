val springBootVersion: String by project

plugins {
    id("org.openapi.generator") version "6.2.0" //before upgrade wait for >6.2.2
    id("udm.java")
}

tasks.jar {
    archiveFileName.set("api.jar")
}

dependencies {
    //swagger ui
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
    //required by openapi-generator
    implementation("org.springframework.boot:spring-boot-starter-tomcat:$springBootVersion")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.8")
    implementation("org.openapitools:jackson-databind-nullable:0.2.4")
}


tasks["compileJava"].dependsOn("openApiGenerate")

sourceSets {
    main {
        java {
            srcDir("${project.buildDir}/generated/src/main/java")
        }
    }
}

openApiValidate {
    inputSpec.set("${project.projectDir}/src/main/resources/open-api.yaml")
}

openApiGenerate {

    generatorName.set("spring")
    library.set("spring-boot")
    inputSpec.set("${project.projectDir}/src/main/resources/open-api.yaml")
    outputDir.set("${project.buildDir}/generated")

    globalProperties.set(
        mapOf(
            "modelDocs" to "false",
            "models" to "",
            "apis" to "",
            "supportingFiles" to "" //set to "false" if you get a compilation error that ApiUtil is not available
        )
    )

    typeMappings.set(
        mapOf(
            "Date" to "java.time.LocalDate"
        )
    )

    configOptions.set(
        mapOf(
            "useSpringBoot3" to "true",
            "useOptional" to "true",
            "useSwaggerUI" to "false",
            "swaggerDocketConfig" to "false",
            "performBeanValidation" to "false",
            "useBeanValidation" to "false",
            "useTags" to "true",
            "singleContentTypes" to "true",
            "basePackage" to "com.udm.documents.filestore.adapters.in.web.mvc.generated.api",
            "configPackage" to "com.udm.documents.filestore.adapters.in.web.mvc.generated.api",
            "title" to rootProject.name,
            "java8" to "false",
            "dateLibrary" to "java17",
            "serializableModel" to "true",
            "artifactId" to rootProject.name,
            "apiPackage" to "com.udm.documents.filestore.adapters.in.web.mvc.generated.api",
            "modelPackage" to "com.udm.documents.filestore.adapters.in.web.mvc.generated.api.model",
            "invokerPackage" to "com.udm.documents.filestore.adapters.in.web.mvc.generated.api",
            "interfaceOnly" to "true"
        )
    )
}

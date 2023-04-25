plugins {
    id("udm.java")
}

// Note that these dependencies are needed only for global Arch Unit test.
dependencies {
    testImplementation(project(":filestore"))
    testImplementation(project(":filestore:adapter:in:web-mvc"))
    testImplementation(project(":filestore:adapter:out:db-jdbi"))
    testImplementation(project(":filestore:adapter:out:file"))
}

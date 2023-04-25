plugins {
    id("udm.adapter-in-web-mvc")
}

dependencies {
    implementation(project(":filestore"))
    implementation(project(":api"))
}
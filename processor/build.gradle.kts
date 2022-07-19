plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.0-1.0.6")
    implementation(project(":annotation"))

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
}
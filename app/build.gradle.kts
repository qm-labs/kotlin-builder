plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(project(":annotation"))
    ksp(project(":processor"))

    api(libs.bundles.tests)
    api(libs.bundles.kotests) {
        exclude("org.junit.jupiter")
    }
}
tasks.test {
    useJUnitPlatform()
}

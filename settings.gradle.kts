rootProject.name = "kotlin-builder"

include(
        ":processor",
        ":annotation",
        ":app"
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://plugins.gradle.org/m2/")
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        mavenCentral()
        google()
    }
}
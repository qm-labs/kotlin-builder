import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotest)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.ksp)
}


allprojects {
    version = rootProject.version

    afterEvaluate {
        tasks.withType<KotlinCompile>().all {
            kotlinOptions {
                jvmTarget = "1.8"
                allWarningsAsErrors = true
                suppressWarnings = false
                kotlin.sourceSets.main {
                    kotlin.srcDir("build/generated/ksp/main/kotlin")
                }
            }
        }
        tasks.withType<LintTask>().all {
            disabledRules.add("no-wildcard-imports")
        }
    }
}

tasks {

}

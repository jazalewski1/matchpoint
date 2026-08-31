import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)

    kotlin("plugin.serialization") version "2.0.21"

    id("com.autonomousapps.dependency-analysis")
}

android {
    namespace = "dev.jazalewski1.matchpoint.feature.match"
    compileSdk { version = release(37) }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests { isIncludeAndroidResources = true }
        unitTests.all { test ->
            test.testLogging {
                showStandardStreams = true
                events(
                    TestLogEvent.PASSED,
                    TestLogEvent.SKIPPED,
                    TestLogEvent.FAILED,
                    TestLogEvent.STANDARD_OUT,
                    TestLogEvent.STANDARD_ERROR,
                )
                exceptionFormat = TestExceptionFormat.FULL
                showCauses = true
                showStackTraces = true
            }
            test.jvmArgs("--enable-native-access=ALL-UNNAMED")
        }
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":domain:tennis"))

    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.runtime)
    api(libs.dagger)
    api(libs.javax.inject)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)

    ksp(libs.hilt.compiler)

    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)

    testRuntimeOnly(libs.androidx.core)
    testRuntimeOnly(libs.robolectric)
}

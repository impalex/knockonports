/*
 * Copyright (c) 2024-2025 Alexander Yaburov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt.plugin)
}

val appScheme = "knockonports"
val appHost = "app"
val knockHost = "knock"

val keystorePropertiesFile = rootProject.file("keystore.properties")

val keystoreProperties = Properties()

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}


android {
    namespace = "me.impa.knockonports"
    compileSdk = 35

    if (keystoreProperties.isNotEmpty()) {
        signingConfigs {
            create("release") {
                keyAlias = keystoreProperties["keyAlias"] as? String
                    ?: error("keyAlias not found in keystore.properties")
                keyPassword = keystoreProperties["keyPassword"] as? String
                    ?: error("keyPassword not found in keystore.properties")
                storeFile = file(
                    keystoreProperties["storeFile"] as? String
                        ?: error("storeFile not found in keystore.properties")
                )
                storePassword = keystoreProperties["storePassword"] as? String
                    ?: error("storePassword not found in keystore.properties")
            }
        }
    }

    defaultConfig {
        applicationId = "me.impa.knockonports"
        minSdk = 24
        targetSdk = 35
        versionCode = 216
        versionName = "2.0.8"

        testInstrumentationRunner = "me.impa.knockonports.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        @Suppress("UnstableApiUsage")
        externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
            }
        }
        buildConfigField("String", "APP_SCHEME", "\"$appScheme\"")
        buildConfigField("String", "APP_HOST", "\"$appHost\"")
        buildConfigField("String", "KNOCK_HOST", "\"$knockHost\"")
        manifestPlaceholders.putAll(mapOf(
            "appScheme" to appScheme,
            "appHost" to appHost,
            "knockHost" to knockHost
        ))
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.findByName("debug")
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("Boolean", "DEBUG", "true")
        }
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("Boolean", "DEBUG", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
        stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    ndkVersion = "28.1.13356709"
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.immutable.collections)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.adaptive.navigation.suite)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.ui.tooling)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Datastore
    implementation(libs.datastore.preferences)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Glance
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.glance.appwidget.preview)

    // Utils
    implementation(libs.calvin.reorderable)
    implementation(libs.timber)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Compose tests
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.screengrab)

    // Debug
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.rebugger)
    detektPlugins(libs.detekt.formatting)
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(rootProject.files("detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
    parallel = true
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

tasks.register("generateReleaseChangeLog") {
    doLast {
        val changeLogFile = File("$rootDir/fastlane/metadata/android/en-US/changelogs/" +
                "${android.defaultConfig.versionCode}.txt")
        println("Generating changelog for version ${android.defaultConfig.versionCode} from $changeLogFile")
        val outputDir = File("${layout.buildDirectory.get()}/outputs/changelog")
        val outputFileName = "changelog.txt"
        outputDir.mkdirs()
        if (changeLogFile.exists()) {
            changeLogFile.copyTo(File(outputDir, outputFileName), overwrite = true)
            println("Copied changelog from $changeLogFile to $outputDir/$outputFileName")
        } else {
            val outputFile = File(outputDir, outputFileName)
            outputFile.writeText("No changelog found for this version.")
            println("No changelog found for this version. Generated empty changelog in $outputDir/$outputFileName")
        }
    }
}

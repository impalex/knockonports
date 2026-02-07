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

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt.plugin)
    id("kotlin-parcelize")
}

val appScheme: String by rootProject.extra
val appHost: String by rootProject.extra
val knockHost: String by rootProject.extra

val keystorePropertiesFile: File? = rootProject.file("keystore.properties")

val keystoreProperties = Properties()

if (keystorePropertiesFile?.exists() == true) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}


android {
    namespace = "me.impa.knockonports"
    compileSdk = 36

    flavorDimensions += listOf("store")

    productFlavors {
        create("google") {
            isDefault = true
            dimension = "store"
        }
        create("foss") {
            dimension = "store"
        }
    }


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
        targetSdk = 36

        val productVersion: Int by rootProject.extra
        val releaseVersion: Int by rootProject.extra

        versionCode = requireNotNull(targetSdk) * 1_000_00_00 + productVersion * 1_00_00 + releaseVersion * 1_00
        versionName = "2.1.0"

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
        val knocklordCapName: String by rootProject.extra
        val knockletCapName: String by rootProject.extra
        val wearSeqDataPath: String by rootProject.extra
        val wearKnockDataPath: String by rootProject.extra
        resValue("string", "cap_app_installed", knocklordCapName)
        buildConfigField("String", "CAP_KNOCKLORD_INSTALLED", "\"$knocklordCapName\"")
        buildConfigField("String", "CAP_KNOCKLET_INSTALLED", "\"$knockletCapName\"")
        buildConfigField("String", "WEAR_PATH_SEQUENCE_LIST", "\"$wearSeqDataPath\"")
        buildConfigField("String", "WEAR_PATH_KNOCK_STATUS", "\"$wearKnockDataPath\"")
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
        resValues = true
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.addAll("-Xannotation-default-target=param-property", "-Xexplicit-backing-fields")
    }
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
        stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
    }
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

    // Biometric
    implementation(libs.androidx.biometric)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.ui.tooling)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Datastore
    implementation(libs.datastore.preferences)

    // Google Play Services
    "googleImplementation"(libs.play.services.wearable)
    "googleImplementation"(libs.androidx.wear.remote.interactions)
    "googleImplementation"(libs.kotlinx.coroutines.play.services)

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
    implementation(libs.material.kolor)
    implementation(libs.color.picker)
    "googleImplementation"(project(":shared"))

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

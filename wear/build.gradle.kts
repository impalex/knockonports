import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.detekt.plugin)
}

val keystorePropertiesFile: File? = rootProject.file("keystore.properties")

val keystoreProperties = Properties()

if (keystorePropertiesFile?.exists() == true) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "me.impa.knockonports"
    compileSdk {
        version = release(36)
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
        minSdk = 30
        targetSdk = 36

        val productVersion: Int by rootProject.extra
        val releaseVersion: Int by rootProject.extra

        versionCode = requireNotNull(targetSdk) * 1_000_00_00 + productVersion * 1_00_00 + releaseVersion * 1_00 + 3
        versionName = "1.0.0"
        val knocklordCapName: String by rootProject.extra
        val knockletCapName: String by rootProject.extra
        val wearSeqDataPath: String by rootProject.extra
        val appScheme: String by rootProject.extra
        val appHost: String by rootProject.extra
        val knockHost: String by rootProject.extra
        val wearKnockDataPath: String by rootProject.extra
        resValue("string", "cap_app_installed", knockletCapName)
        buildConfigField("String", "CAP_KNOCKLORD_INSTALLED", "\"$knocklordCapName\"")
        buildConfigField("String", "CAP_KNOCKLET_INSTALLED", "\"$knockletCapName\"")
        buildConfigField("String", "WEAR_PATH_SEQUENCE_LIST", "\"$wearSeqDataPath\"")
        buildConfigField("String", "WEAR_PATH_KNOCK_STATUS", "\"$wearKnockDataPath\"")
        buildConfigField("String", "APP_SCHEME", "\"$appScheme\"")
        buildConfigField("String", "APP_HOST", "\"$appHost\"")
        buildConfigField("String", "KNOCK_HOST", "\"$knockHost\"")
        manifestPlaceholders.putAll(mapOf(
            "wearSeqDataPath" to wearSeqDataPath,
            "wearKnockDataPath" to wearKnockDataPath,
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(rootProject.files("detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
    parallel = true
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
        stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
    }
}

dependencies {
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)

    // Wearable
    implementation(libs.play.services.wearable)
    implementation(libs.androidx.wear.remote.interactions)
    implementation(libs.androidx.wear.tooling.preview)

    // Horologist
    implementation(libs.horologist.compose.layout)

    // Coroutines
    implementation(libs.kotlinx.coroutines.play.services)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Other
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.datastore.preferences)
    implementation(libs.timber)
    implementation(libs.protobuf.javalite)
    implementation(project(":shared"))

    // Test
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
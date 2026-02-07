import org.gradle.internal.extensions.core.extra

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

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt.plugin) apply false
    alias(libs.plugins.squareup.wire) apply false
    alias(libs.plugins.android.library) apply false
}

val productVersion: Int by extra(1)
val releaseVersion: Int by extra(3)

val knockletCapName: String by extra("knocklet_installed")
val knocklordCapName: String by extra("knocklord_installed")
val wearSeqDataPath: String by extra("/sequence_list")
val wearKnockDataPath: String by extra("/knock_status")
val appScheme: String by extra("knockonports")
val appHost: String by extra("app")
val knockHost: String by extra("knock")

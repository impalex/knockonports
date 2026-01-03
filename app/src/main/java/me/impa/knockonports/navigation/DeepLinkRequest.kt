/*
 * Copyright (c) 2025 Alexander Yaburov
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

package me.impa.knockonports.navigation

import android.net.Uri

class DeepLinkRequest(val uri: Uri) {
    val pathSegments: List<String> = uri.pathSegments
    val queries = buildMap {
        uri.queryParameterNames.forEach { argName ->
            put(argName, uri.getQueryParameter(argName)!!)
        }
    }
}
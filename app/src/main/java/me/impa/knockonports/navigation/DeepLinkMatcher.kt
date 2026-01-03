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

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.KSerializer
import timber.log.Timber

class DeepLinkMatcher<T : NavKey>(
    val request: DeepLinkRequest,
    val deepLinkPattern: DeepLinkPattern<T>
    ) {

    @Suppress("ReturnCount")
    fun match(): DeepLinkMatchResult<T>? {
        if (request.pathSegments.size != deepLinkPattern.pathSegments.size) return null

        if (request.uri == deepLinkPattern.uriPattern)
            return DeepLinkMatchResult(deepLinkPattern.serializer, emptyMap())

        val args = mutableMapOf<String, Any>()

        request.pathSegments
            .asSequence()
            .zip(deepLinkPattern.pathSegments.asSequence())
            .forEach {
                val requestSegment = it.first
                val candidateSegment = it.second
                if (candidateSegment.isParamArg) {
                    val parsedValue = try {
                        candidateSegment.typeParser.invoke(requestSegment)
                    } catch (e: IllegalArgumentException) {
                        Timber.e(e, "Failed to parse argument $requestSegment")
                        return null
                    }
                    args[candidateSegment.stringValue] = parsedValue
                } else if (requestSegment != candidateSegment.stringValue) {
                    return null
                }
            }

        request.queries.forEach { query ->
            val name = query.key
            val queryStringParser = deepLinkPattern.queryValueParsers[name]
            val queryParsedValue = try {
                queryStringParser!!.invoke(query.value)
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Failed to parse query argument $query]")
                return null
            }
            args[name] = queryParsedValue
        }
        return DeepLinkMatchResult(deepLinkPattern.serializer, args)
    }
}

data class DeepLinkMatchResult<T: NavKey>(
    val serializer: KSerializer<T>,
    val args: Map<String, Any>
)
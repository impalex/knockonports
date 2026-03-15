/*
 * Copyright (c) 2026 Alexander Yaburov
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

package me.impa.knockonports.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import me.impa.knockonports.R
import me.impa.knockonports.helper.safeBottomContentPadding
import me.impa.knockonports.navigation.TranslateRoute
import me.impa.knockonports.screen.component.common.RegisterAppBar
import me.impa.knockonports.ui.theme.KnockOnPortsTheme
import java.util.Locale

@Composable
fun TranslateScreen(modifier: Modifier = Modifier) {
    val title = stringResource(R.string.title_screen_translation)
    RegisterAppBar<TranslateRoute>(title = title, showBackButton = true)
    TranslateScreenContent(modifier)
}

private val langs = mapOf(
    Locale.ENGLISH to R.string.title_lang_en,
    Locale.forLanguageTag("ru-RU") to R.string.title_lang_ru,
    Locale.SIMPLIFIED_CHINESE to R.string.title_lang_zh
)

@Composable
private fun TranslateScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.then(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        )
    ) {
        SupportedLangSection()
        HorizontalDivider(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp))
        TranslatorsSection()
        HorizontalDivider(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp))
        ContributeCard()
        Spacer(modifier = Modifier.padding(safeBottomContentPadding()))
    }
}

@Composable
private fun SupportedLangSection() {
    Text(text = stringResource(R.string.title_supported_languages), style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    langs.forEach { (locale, resId) ->

        // NOTE This is a very expensive operation. It would make sense to implement caching in the future.
        val context = LocalContext.current
        val resources = LocalResources.current
        val configuration = Configuration(resources.configuration).apply {
            setLocale(locale)
        }
        val localizedContext = context.createConfigurationContext(configuration)
        val origLangName = localizedContext.getString(resId)

        ListItem(
            modifier = Modifier.fillMaxWidth(),
            headlineContent = { Text(stringResource(resId)) },
            supportingContent = { Text(origLangName) },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = locale.language.uppercase(), modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium)
                }
            }
        )
    }
}

@Composable
private fun TranslatorsSection() {
    Text(text = stringResource(R.string.title_our_translators), style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(text = stringResource(R.string.text_special_thanks))
    val usernames = stringArrayResource(R.array.translators)
    val profiles = stringArrayResource(R.array.translators_profiles)
    val translators = usernames.zip(profiles).toMap()
    val uriHandler = LocalUriHandler.current
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        translators.forEach { (username, profile) ->
            SuggestionChip(
                onClick = { uriHandler.openUri(profile) },
                label = { Text(text = username) }
            )
        }
    }
}

@Composable
private fun ContributeCard() {
    val uriHandler = LocalUriHandler.current
    ElevatedCard() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(R.string.title_help_translate),
                style = MaterialTheme.typography.headlineSmall)
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
            Text(text = stringResource(R.string.text_help_translate),
                modifier = Modifier.padding(bottom = 8.dp))
            Button(onClick = { uriHandler.openUri("https://hosted.weblate.org/engage/knock-on-ports/")} ) {
                Text(text = stringResource(R.string.action_translate))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewTranslateScreenContent() {
    KnockOnPortsTheme {
        Surface {
            TranslateScreenContent()
        }
    }
}

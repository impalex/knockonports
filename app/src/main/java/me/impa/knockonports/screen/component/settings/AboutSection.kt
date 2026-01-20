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

package me.impa.knockonports.screen.component.settings

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.R
import me.impa.knockonports.screen.component.common.HeaderSection
import me.impa.knockonports.helper.openPlayStoreAppPage
import me.impa.knockonports.screen.component.common.PrefDescriptionClickable
import timber.log.Timber
import kotlin.random.Random
import androidx.compose.ui.platform.LocalResources
import me.impa.knockonports.helper.openPlayStoreDevPage

fun LazyListScope.aboutSection(isInstalledFromPlayStore: Boolean = false) {

    item { HeaderSection(stringResource(R.string.title_settings_about)) }

    item(key = "app_version") {
        val resources = LocalResources.current
        val annoyingVersion = rememberSaveable { getAnnoyingVersion(resources) }
        PrefDescriptionClickable(
            title = stringResource(R.string.title_settings_version),
            subtitle = annoyingVersion
        )
    }

    if (isInstalledFromPlayStore) {
        item(key = "rate_app") {
            val context = LocalContext.current
            PrefDescriptionClickable(
                title = stringResource(R.string.title_settings_rate_app),
                subtitle = stringResource(R.string.text_settings_rate_app),
                onClick = { openPlayStoreAppPage(context) }
            )
        }
        item(key = "dev_page") {
            val context = LocalContext.current
            PrefDescriptionClickable(
                title = stringResource(R.string.title_settings_developer_page),
                subtitle = stringResource(R.string.text_settings_developer_page),
                onClick = { openPlayStoreDevPage(context) }
            )
        }
    }

    item(key = "privacy_policy") {
        val uriHandler = LocalUriHandler.current
        PrefDescriptionClickable(
            title = stringResource(R.string.title_settings_privacy_policy),
            subtitle = stringResource(R.string.text_settings_privacy_policy),
            onClick = { uriHandler.openUri("https://impalex.github.io/knockonports/policy.html") }
        )
    }

    item(key = "source_code") {
        val uriHandler = LocalUriHandler.current
        PrefDescriptionClickable(
            title = stringResource(R.string.title_settings_source_code),
            subtitle = stringResource(R.string.text_settings_source_code),
            onClick = { uriHandler.openUri("https://github.com/impalex/knockonports") }
        )
    }

    item(key = "discord_invite") {
        val invite = stringResource(R.string.discord_invite)
        val uriHandler = LocalUriHandler.current
        PrefDescriptionClickable(
            title = stringResource(R.string.title_settings_discord),
            subtitle = stringResource(R.string.text_settings_discord),
            onClick = { uriHandler.openUri(invite) }
        )
    }

    item(key = "report_issue") {
        val uriHandler = LocalUriHandler.current
        PrefDescriptionClickable(
            title = stringResource(R.string.title_settings_report_issue),
            subtitle = stringResource(R.string.text_settings_report_issue),
            onClick = { uriHandler.openUri("https://github.com/impalex/knockonports/issues") }
        )
    }

    item(key = "contact_author") {
        val context = LocalContext.current
        val title = stringResource(R.string.title_settings_contact_author)
        val email = stringResource(R.string.contact_author_mail)
        val subject = stringResource(R.string.contact_subject)
        PrefDescriptionClickable(
            title = title,
            subtitle = stringResource(R.string.text_settings_contact_author),
            onClick = { sendMail(context, title, email, subject) }
        )
    }
}

fun sendMail(context: Context, chooserTitle: String, email: String, subject: String) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }
    try {
        context.startActivity(
            try {
                Intent.createChooser(emailIntent, chooserTitle)
            } catch (e: Exception) {
                Timber.e(e, "Unable to start chooser")
                throw e
            }
        )
    } catch (_: Exception) {
        Timber.e("Unable to send email")
    }

}

@Suppress("MagicNumber")
fun getAnnoyingVersion(resources: Resources) =
    when (Random(System.currentTimeMillis()).nextInt(1, 20)) {
        1 -> resources.getString(
            R.string.text_annoying_version_1, BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE % 40 + 5
        )

        2 -> resources.getString(
            R.string.text_annoying_version_2, BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )

        3 -> resources.getString(R.string.text_annoying_version_3, BuildConfig.VERSION_NAME)
        4 -> resources.getString(R.string.text_annoying_version_4, BuildConfig.VERSION_NAME)
        5 -> resources.getString(R.string.text_annoying_version_5, BuildConfig.VERSION_NAME)
        6 -> resources.getString(R.string.text_annoying_version_6, BuildConfig.VERSION_NAME)
        7 -> resources.getString(
            R.string.text_annoying_version_7, BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )

        8 -> resources.getString(R.string.text_annoying_version_8, BuildConfig.VERSION_NAME)
        9 -> resources.getString(R.string.text_annoying_version_9, BuildConfig.VERSION_NAME)

        else -> BuildConfig.VERSION_NAME

    }

@Preview
@Composable
fun PreviewAboutSection() {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        aboutSection()

    }
}
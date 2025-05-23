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

package me.impa.knockonports.ui.theme.variant

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.persistentMapOf
import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.config.ThemeContrast

object SkySteelScheme : AppColorScheme {

    // Sky Steel
    // Seed #5A93FF
    // Primary #5A93FF
    // Secondary #8690AC
    // Tetriary #AC83AE

    val primaryLight = Color(0xFF455E91)
    val onPrimaryLight = Color(0xFFFFFFFF)
    val primaryContainerLight = Color(0xFFD8E2FF)
    val onPrimaryContainerLight = Color(0xFF2C4678)
    val secondaryLight = Color(0xFF575E71)
    val onSecondaryLight = Color(0xFFFFFFFF)
    val secondaryContainerLight = Color(0xFFDBE2F9)
    val onSecondaryContainerLight = Color(0xFF3F4759)
    val tertiaryLight = Color(0xFF715573)
    val onTertiaryLight = Color(0xFFFFFFFF)
    val tertiaryContainerLight = Color(0xFFFCD7FB)
    val onTertiaryContainerLight = Color(0xFF583E5A)
    val errorLight = Color(0xFFBA1A1A)
    val onErrorLight = Color(0xFFFFFFFF)
    val errorContainerLight = Color(0xFFFFDAD6)
    val onErrorContainerLight = Color(0xFF93000A)
    val backgroundLight = Color(0xFFFAF9FF)
    val onBackgroundLight = Color(0xFF1A1B20)
    val surfaceLight = Color(0xFFFAF9FF)
    val onSurfaceLight = Color(0xFF1A1B20)
    val surfaceVariantLight = Color(0xFFE1E2EC)
    val onSurfaceVariantLight = Color(0xFF44474F)
    val outlineLight = Color(0xFF757780)
    val outlineVariantLight = Color(0xFFC5C6D0)
    val scrimLight = Color(0xFF000000)
    val inverseSurfaceLight = Color(0xFF2F3036)
    val inverseOnSurfaceLight = Color(0xFFF0F0F7)
    val inversePrimaryLight = Color(0xFFAEC6FF)
    val surfaceDimLight = Color(0xFFDAD9E0)
    val surfaceBrightLight = Color(0xFFFAF9FF)
    val surfaceContainerLowestLight = Color(0xFFFFFFFF)
    val surfaceContainerLowLight = Color(0xFFF3F3FA)
    val surfaceContainerLight = Color(0xFFEEEDF4)
    val surfaceContainerHighLight = Color(0xFFE8E7EF)
    val surfaceContainerHighestLight = Color(0xFFE2E2E9)

    val primaryLightMediumContrast = Color(0xFF193566)
    val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
    val primaryContainerLightMediumContrast = Color(0xFF546CA1)
    val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val secondaryLightMediumContrast = Color(0xFF2F3648)
    val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
    val secondaryContainerLightMediumContrast = Color(0xFF656D80)
    val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val tertiaryLightMediumContrast = Color(0xFF462D49)
    val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
    val tertiaryContainerLightMediumContrast = Color(0xFF816382)
    val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val errorLightMediumContrast = Color(0xFF740006)
    val onErrorLightMediumContrast = Color(0xFFFFFFFF)
    val errorContainerLightMediumContrast = Color(0xFFCF2C27)
    val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
    val backgroundLightMediumContrast = Color(0xFFFAF9FF)
    val onBackgroundLightMediumContrast = Color(0xFF1A1B20)
    val surfaceLightMediumContrast = Color(0xFFFAF9FF)
    val onSurfaceLightMediumContrast = Color(0xFF0F1116)
    val surfaceVariantLightMediumContrast = Color(0xFFE1E2EC)
    val onSurfaceVariantLightMediumContrast = Color(0xFF33363E)
    val outlineLightMediumContrast = Color(0xFF50525A)
    val outlineVariantLightMediumContrast = Color(0xFF6B6D75)
    val scrimLightMediumContrast = Color(0xFF000000)
    val inverseSurfaceLightMediumContrast = Color(0xFF2F3036)
    val inverseOnSurfaceLightMediumContrast = Color(0xFFF0F0F7)
    val inversePrimaryLightMediumContrast = Color(0xFFAEC6FF)
    val surfaceDimLightMediumContrast = Color(0xFFC6C6CD)
    val surfaceBrightLightMediumContrast = Color(0xFFFAF9FF)
    val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
    val surfaceContainerLowLightMediumContrast = Color(0xFFF3F3FA)
    val surfaceContainerLightMediumContrast = Color(0xFFE8E7EF)
    val surfaceContainerHighLightMediumContrast = Color(0xFFDCDCE3)
    val surfaceContainerHighestLightMediumContrast = Color(0xFFD1D1D8)

    val primaryLightHighContrast = Color(0xFF0C2A5B)
    val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
    val primaryContainerLightHighContrast = Color(0xFF2F487A)
    val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val secondaryLightHighContrast = Color(0xFF252C3D)
    val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
    val secondaryContainerLightHighContrast = Color(0xFF42495B)
    val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val tertiaryLightHighContrast = Color(0xFF3C233E)
    val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
    val tertiaryContainerLightHighContrast = Color(0xFF5B405D)
    val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val errorLightHighContrast = Color(0xFF600004)
    val onErrorLightHighContrast = Color(0xFFFFFFFF)
    val errorContainerLightHighContrast = Color(0xFF98000A)
    val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
    val backgroundLightHighContrast = Color(0xFFFAF9FF)
    val onBackgroundLightHighContrast = Color(0xFF1A1B20)
    val surfaceLightHighContrast = Color(0xFFFAF9FF)
    val onSurfaceLightHighContrast = Color(0xFF000000)
    val surfaceVariantLightHighContrast = Color(0xFFE1E2EC)
    val onSurfaceVariantLightHighContrast = Color(0xFF000000)
    val outlineLightHighContrast = Color(0xFF292C33)
    val outlineVariantLightHighContrast = Color(0xFF474951)
    val scrimLightHighContrast = Color(0xFF000000)
    val inverseSurfaceLightHighContrast = Color(0xFF2F3036)
    val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
    val inversePrimaryLightHighContrast = Color(0xFFAEC6FF)
    val surfaceDimLightHighContrast = Color(0xFFB8B8BF)
    val surfaceBrightLightHighContrast = Color(0xFFFAF9FF)
    val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
    val surfaceContainerLowLightHighContrast = Color(0xFFF0F0F7)
    val surfaceContainerLightHighContrast = Color(0xFFE2E2E9)
    val surfaceContainerHighLightHighContrast = Color(0xFFD4D4DB)
    val surfaceContainerHighestLightHighContrast = Color(0xFFC6C6CD)

    val primaryDark = Color(0xFFAEC6FF)
    val onPrimaryDark = Color(0xFF122F60)
    val primaryContainerDark = Color(0xFF2C4678)
    val onPrimaryContainerDark = Color(0xFFD8E2FF)
    val secondaryDark = Color(0xFFBFC6DC)
    val onSecondaryDark = Color(0xFF293041)
    val secondaryContainerDark = Color(0xFF3F4759)
    val onSecondaryContainerDark = Color(0xFFDBE2F9)
    val tertiaryDark = Color(0xFFDFBBDE)
    val onTertiaryDark = Color(0xFF402843)
    val tertiaryContainerDark = Color(0xFF583E5A)
    val onTertiaryContainerDark = Color(0xFFFCD7FB)
    val errorDark = Color(0xFFFFB4AB)
    val onErrorDark = Color(0xFF690005)
    val errorContainerDark = Color(0xFF93000A)
    val onErrorContainerDark = Color(0xFFFFDAD6)
    val backgroundDark = Color(0xFF121318)
    val onBackgroundDark = Color(0xFFE2E2E9)
    val surfaceDark = Color(0xFF121318)
    val onSurfaceDark = Color(0xFFE2E2E9)
    val surfaceVariantDark = Color(0xFF44474F)
    val onSurfaceVariantDark = Color(0xFFC5C6D0)
    val outlineDark = Color(0xFF8E9099)
    val outlineVariantDark = Color(0xFF44474F)
    val scrimDark = Color(0xFF000000)
    val inverseSurfaceDark = Color(0xFFE2E2E9)
    val inverseOnSurfaceDark = Color(0xFF2F3036)
    val inversePrimaryDark = Color(0xFF455E91)
    val surfaceDimDark = Color(0xFF121318)
    val surfaceBrightDark = Color(0xFF37393E)
    val surfaceContainerLowestDark = Color(0xFF0C0E13)
    val surfaceContainerLowDark = Color(0xFF1A1B20)
    val surfaceContainerDark = Color(0xFF1E1F25)
    val surfaceContainerHighDark = Color(0xFF282A2F)
    val surfaceContainerHighestDark = Color(0xFF33353A)

    val primaryDarkMediumContrast = Color(0xFFCFDCFF)
    val onPrimaryDarkMediumContrast = Color(0xFF022455)
    val primaryContainerDarkMediumContrast = Color(0xFF7890C7)
    val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
    val secondaryDarkMediumContrast = Color(0xFFD5DCF3)
    val onSecondaryDarkMediumContrast = Color(0xFF1E2536)
    val secondaryContainerDarkMediumContrast = Color(0xFF8990A5)
    val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
    val tertiaryDarkMediumContrast = Color(0xFFF6D1F5)
    val onTertiaryDarkMediumContrast = Color(0xFF351D38)
    val tertiaryContainerDarkMediumContrast = Color(0xFFA686A7)
    val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
    val errorDarkMediumContrast = Color(0xFFFFD2CC)
    val onErrorDarkMediumContrast = Color(0xFF540003)
    val errorContainerDarkMediumContrast = Color(0xFFFF5449)
    val onErrorContainerDarkMediumContrast = Color(0xFF000000)
    val backgroundDarkMediumContrast = Color(0xFF121318)
    val onBackgroundDarkMediumContrast = Color(0xFFE2E2E9)
    val surfaceDarkMediumContrast = Color(0xFF121318)
    val onSurfaceDarkMediumContrast = Color(0xFFFFFFFF)
    val surfaceVariantDarkMediumContrast = Color(0xFF44474F)
    val onSurfaceVariantDarkMediumContrast = Color(0xFFDBDCE6)
    val outlineDarkMediumContrast = Color(0xFFB0B1BB)
    val outlineVariantDarkMediumContrast = Color(0xFF8E9099)
    val scrimDarkMediumContrast = Color(0xFF000000)
    val inverseSurfaceDarkMediumContrast = Color(0xFFE2E2E9)
    val inverseOnSurfaceDarkMediumContrast = Color(0xFF282A2F)
    val inversePrimaryDarkMediumContrast = Color(0xFF2D4779)
    val surfaceDimDarkMediumContrast = Color(0xFF121318)
    val surfaceBrightDarkMediumContrast = Color(0xFF43444A)
    val surfaceContainerLowestDarkMediumContrast = Color(0xFF06070C)
    val surfaceContainerLowDarkMediumContrast = Color(0xFF1C1D23)
    val surfaceContainerDarkMediumContrast = Color(0xFF26282D)
    val surfaceContainerHighDarkMediumContrast = Color(0xFF313238)
    val surfaceContainerHighestDarkMediumContrast = Color(0xFF3C3D43)

    val primaryDarkHighContrast = Color(0xFFECEFFF)
    val onPrimaryDarkHighContrast = Color(0xFF000000)
    val primaryContainerDarkHighContrast = Color(0xFFA9C2FC)
    val onPrimaryContainerDarkHighContrast = Color(0xFF000A23)
    val secondaryDarkHighContrast = Color(0xFFECEFFF)
    val onSecondaryDarkHighContrast = Color(0xFF000000)
    val secondaryContainerDarkHighContrast = Color(0xFFBBC2D8)
    val onSecondaryContainerDarkHighContrast = Color(0xFF040B1B)
    val tertiaryDarkHighContrast = Color(0xFFFFEAFC)
    val onTertiaryDarkHighContrast = Color(0xFF000000)
    val tertiaryContainerDarkHighContrast = Color(0xFFDBB8DA)
    val onTertiaryContainerDarkHighContrast = Color(0xFF17031B)
    val errorDarkHighContrast = Color(0xFFFFECE9)
    val onErrorDarkHighContrast = Color(0xFF000000)
    val errorContainerDarkHighContrast = Color(0xFFFFAEA4)
    val onErrorContainerDarkHighContrast = Color(0xFF220001)
    val backgroundDarkHighContrast = Color(0xFF121318)
    val onBackgroundDarkHighContrast = Color(0xFFE2E2E9)
    val surfaceDarkHighContrast = Color(0xFF121318)
    val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
    val surfaceVariantDarkHighContrast = Color(0xFF44474F)
    val onSurfaceVariantDarkHighContrast = Color(0xFFFFFFFF)
    val outlineDarkHighContrast = Color(0xFFEEEFF9)
    val outlineVariantDarkHighContrast = Color(0xFFC1C2CC)
    val scrimDarkHighContrast = Color(0xFF000000)
    val inverseSurfaceDarkHighContrast = Color(0xFFE2E2E9)
    val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
    val inversePrimaryDarkHighContrast = Color(0xFF2D4779)
    val surfaceDimDarkHighContrast = Color(0xFF121318)
    val surfaceBrightDarkHighContrast = Color(0xFF4E5056)
    val surfaceContainerLowestDarkHighContrast = Color(0xFF000000)
    val surfaceContainerLowDarkHighContrast = Color(0xFF1E1F25)
    val surfaceContainerDarkHighContrast = Color(0xFF2F3036)
    val surfaceContainerHighDarkHighContrast = Color(0xFF3A3B41)
    val surfaceContainerHighestDarkHighContrast = Color(0xFF45464C)

    private val lightScheme = lightColorScheme(
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        primaryContainer = primaryContainerLight,
        onPrimaryContainer = onPrimaryContainerLight,
        secondary = secondaryLight,
        onSecondary = onSecondaryLight,
        secondaryContainer = secondaryContainerLight,
        onSecondaryContainer = onSecondaryContainerLight,
        tertiary = tertiaryLight,
        onTertiary = onTertiaryLight,
        tertiaryContainer = tertiaryContainerLight,
        onTertiaryContainer = onTertiaryContainerLight,
        error = errorLight,
        onError = onErrorLight,
        errorContainer = errorContainerLight,
        onErrorContainer = onErrorContainerLight,
        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,
        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight,
        outline = outlineLight,
        outlineVariant = outlineVariantLight,
        scrim = scrimLight,
        inverseSurface = inverseSurfaceLight,
        inverseOnSurface = inverseOnSurfaceLight,
        inversePrimary = inversePrimaryLight,
        surfaceDim = surfaceDimLight,
        surfaceBright = surfaceBrightLight,
        surfaceContainerLowest = surfaceContainerLowestLight,
        surfaceContainerLow = surfaceContainerLowLight,
        surfaceContainer = surfaceContainerLight,
        surfaceContainerHigh = surfaceContainerHighLight,
        surfaceContainerHighest = surfaceContainerHighestLight,
    )

    private val darkScheme = darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
        surfaceDim = surfaceDimDark,
        surfaceBright = surfaceBrightDark,
        surfaceContainerLowest = surfaceContainerLowestDark,
        surfaceContainerLow = surfaceContainerLowDark,
        surfaceContainer = surfaceContainerDark,
        surfaceContainerHigh = surfaceContainerHighDark,
        surfaceContainerHighest = surfaceContainerHighestDark,
    )

    private val mediumContrastLightColorScheme = lightColorScheme(
        primary = primaryLightMediumContrast,
        onPrimary = onPrimaryLightMediumContrast,
        primaryContainer = primaryContainerLightMediumContrast,
        onPrimaryContainer = onPrimaryContainerLightMediumContrast,
        secondary = secondaryLightMediumContrast,
        onSecondary = onSecondaryLightMediumContrast,
        secondaryContainer = secondaryContainerLightMediumContrast,
        onSecondaryContainer = onSecondaryContainerLightMediumContrast,
        tertiary = tertiaryLightMediumContrast,
        onTertiary = onTertiaryLightMediumContrast,
        tertiaryContainer = tertiaryContainerLightMediumContrast,
        onTertiaryContainer = onTertiaryContainerLightMediumContrast,
        error = errorLightMediumContrast,
        onError = onErrorLightMediumContrast,
        errorContainer = errorContainerLightMediumContrast,
        onErrorContainer = onErrorContainerLightMediumContrast,
        background = backgroundLightMediumContrast,
        onBackground = onBackgroundLightMediumContrast,
        surface = surfaceLightMediumContrast,
        onSurface = onSurfaceLightMediumContrast,
        surfaceVariant = surfaceVariantLightMediumContrast,
        onSurfaceVariant = onSurfaceVariantLightMediumContrast,
        outline = outlineLightMediumContrast,
        outlineVariant = outlineVariantLightMediumContrast,
        scrim = scrimLightMediumContrast,
        inverseSurface = inverseSurfaceLightMediumContrast,
        inverseOnSurface = inverseOnSurfaceLightMediumContrast,
        inversePrimary = inversePrimaryLightMediumContrast,
        surfaceDim = surfaceDimLightMediumContrast,
        surfaceBright = surfaceBrightLightMediumContrast,
        surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
        surfaceContainerLow = surfaceContainerLowLightMediumContrast,
        surfaceContainer = surfaceContainerLightMediumContrast,
        surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
        surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
    )

    private val highContrastLightColorScheme = lightColorScheme(
        primary = primaryLightHighContrast,
        onPrimary = onPrimaryLightHighContrast,
        primaryContainer = primaryContainerLightHighContrast,
        onPrimaryContainer = onPrimaryContainerLightHighContrast,
        secondary = secondaryLightHighContrast,
        onSecondary = onSecondaryLightHighContrast,
        secondaryContainer = secondaryContainerLightHighContrast,
        onSecondaryContainer = onSecondaryContainerLightHighContrast,
        tertiary = tertiaryLightHighContrast,
        onTertiary = onTertiaryLightHighContrast,
        tertiaryContainer = tertiaryContainerLightHighContrast,
        onTertiaryContainer = onTertiaryContainerLightHighContrast,
        error = errorLightHighContrast,
        onError = onErrorLightHighContrast,
        errorContainer = errorContainerLightHighContrast,
        onErrorContainer = onErrorContainerLightHighContrast,
        background = backgroundLightHighContrast,
        onBackground = onBackgroundLightHighContrast,
        surface = surfaceLightHighContrast,
        onSurface = onSurfaceLightHighContrast,
        surfaceVariant = surfaceVariantLightHighContrast,
        onSurfaceVariant = onSurfaceVariantLightHighContrast,
        outline = outlineLightHighContrast,
        outlineVariant = outlineVariantLightHighContrast,
        scrim = scrimLightHighContrast,
        inverseSurface = inverseSurfaceLightHighContrast,
        inverseOnSurface = inverseOnSurfaceLightHighContrast,
        inversePrimary = inversePrimaryLightHighContrast,
        surfaceDim = surfaceDimLightHighContrast,
        surfaceBright = surfaceBrightLightHighContrast,
        surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
        surfaceContainerLow = surfaceContainerLowLightHighContrast,
        surfaceContainer = surfaceContainerLightHighContrast,
        surfaceContainerHigh = surfaceContainerHighLightHighContrast,
        surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
    )

    private val mediumContrastDarkColorScheme = darkColorScheme(
        primary = primaryDarkMediumContrast,
        onPrimary = onPrimaryDarkMediumContrast,
        primaryContainer = primaryContainerDarkMediumContrast,
        onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
        secondary = secondaryDarkMediumContrast,
        onSecondary = onSecondaryDarkMediumContrast,
        secondaryContainer = secondaryContainerDarkMediumContrast,
        onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
        tertiary = tertiaryDarkMediumContrast,
        onTertiary = onTertiaryDarkMediumContrast,
        tertiaryContainer = tertiaryContainerDarkMediumContrast,
        onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
        error = errorDarkMediumContrast,
        onError = onErrorDarkMediumContrast,
        errorContainer = errorContainerDarkMediumContrast,
        onErrorContainer = onErrorContainerDarkMediumContrast,
        background = backgroundDarkMediumContrast,
        onBackground = onBackgroundDarkMediumContrast,
        surface = surfaceDarkMediumContrast,
        onSurface = onSurfaceDarkMediumContrast,
        surfaceVariant = surfaceVariantDarkMediumContrast,
        onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
        outline = outlineDarkMediumContrast,
        outlineVariant = outlineVariantDarkMediumContrast,
        scrim = scrimDarkMediumContrast,
        inverseSurface = inverseSurfaceDarkMediumContrast,
        inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
        inversePrimary = inversePrimaryDarkMediumContrast,
        surfaceDim = surfaceDimDarkMediumContrast,
        surfaceBright = surfaceBrightDarkMediumContrast,
        surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
        surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
        surfaceContainer = surfaceContainerDarkMediumContrast,
        surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
        surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
    )

    private val highContrastDarkColorScheme = darkColorScheme(
        primary = primaryDarkHighContrast,
        onPrimary = onPrimaryDarkHighContrast,
        primaryContainer = primaryContainerDarkHighContrast,
        onPrimaryContainer = onPrimaryContainerDarkHighContrast,
        secondary = secondaryDarkHighContrast,
        onSecondary = onSecondaryDarkHighContrast,
        secondaryContainer = secondaryContainerDarkHighContrast,
        onSecondaryContainer = onSecondaryContainerDarkHighContrast,
        tertiary = tertiaryDarkHighContrast,
        onTertiary = onTertiaryDarkHighContrast,
        tertiaryContainer = tertiaryContainerDarkHighContrast,
        onTertiaryContainer = onTertiaryContainerDarkHighContrast,
        error = errorDarkHighContrast,
        onError = onErrorDarkHighContrast,
        errorContainer = errorContainerDarkHighContrast,
        onErrorContainer = onErrorContainerDarkHighContrast,
        background = backgroundDarkHighContrast,
        onBackground = onBackgroundDarkHighContrast,
        surface = surfaceDarkHighContrast,
        onSurface = onSurfaceDarkHighContrast,
        surfaceVariant = surfaceVariantDarkHighContrast,
        onSurfaceVariant = onSurfaceVariantDarkHighContrast,
        outline = outlineDarkHighContrast,
        outlineVariant = outlineVariantDarkHighContrast,
        scrim = scrimDarkHighContrast,
        inverseSurface = inverseSurfaceDarkHighContrast,
        inverseOnSurface = inverseOnSurfaceDarkHighContrast,
        inversePrimary = inversePrimaryDarkHighContrast,
        surfaceDim = surfaceDimDarkHighContrast,
        surfaceBright = surfaceBrightDarkHighContrast,
        surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
        surfaceContainerLow = surfaceContainerLowDarkHighContrast,
        surfaceContainer = surfaceContainerDarkHighContrast,
        surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
        surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
    )

    override val colorPrimary = Color(0xFF5A93FF)
    override val colorSecondary = Color(0xFF8690AC)
    override val colorTetriary = Color(0xFFAC83AE)

    override val name = "Sky Steel"

    override val theme = persistentMapOf(
        DarkMode.LIGHT to persistentMapOf(
            ThemeContrast.STANDARD to lightScheme,
            ThemeContrast.MEDIUM to mediumContrastLightColorScheme,
            ThemeContrast.HIGH to highContrastLightColorScheme
        ),
        DarkMode.DARK to persistentMapOf(
            ThemeContrast.STANDARD to darkScheme,
            ThemeContrast.MEDIUM to mediumContrastDarkColorScheme,
            ThemeContrast.HIGH to highContrastDarkColorScheme
        ),
    )

}
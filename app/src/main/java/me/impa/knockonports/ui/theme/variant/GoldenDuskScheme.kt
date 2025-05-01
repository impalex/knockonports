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

object GoldenDuskScheme : AppColorScheme {

    // Golden Dusk
    // Seed #E3CD00
    // Primary #E3CD00
    // Secondary #999164
    // Tetriary #649C7D

    val primaryLight = Color(0xFF695F12)
    val onPrimaryLight = Color(0xFFFFFFFF)
    val primaryContainerLight = Color(0xFFF2E48A)
    val onPrimaryContainerLight = Color(0xFF504700)
    val secondaryLight = Color(0xFF645F41)
    val onSecondaryLight = Color(0xFFFFFFFF)
    val secondaryContainerLight = Color(0xFFEBE3BD)
    val onSecondaryContainerLight = Color(0xFF4C472B)
    val tertiaryLight = Color(0xFF416651)
    val onTertiaryLight = Color(0xFFFFFFFF)
    val tertiaryContainerLight = Color(0xFFC3ECD2)
    val onTertiaryContainerLight = Color(0xFF294E3B)
    val errorLight = Color(0xFFBA1A1A)
    val onErrorLight = Color(0xFFFFFFFF)
    val errorContainerLight = Color(0xFFFFDAD6)
    val onErrorContainerLight = Color(0xFF93000A)
    val backgroundLight = Color(0xFFFFF9EB)
    val onBackgroundLight = Color(0xFF1D1C13)
    val surfaceLight = Color(0xFFFFF9EB)
    val onSurfaceLight = Color(0xFF1D1C13)
    val surfaceVariantLight = Color(0xFFE8E2D0)
    val onSurfaceVariantLight = Color(0xFF4A4739)
    val outlineLight = Color(0xFF7B7768)
    val outlineVariantLight = Color(0xFFCCC6B5)
    val scrimLight = Color(0xFF000000)
    val inverseSurfaceLight = Color(0xFF323027)
    val inverseOnSurfaceLight = Color(0xFFF6F0E3)
    val inversePrimaryLight = Color(0xFFD5C871)
    val surfaceDimLight = Color(0xFFDFDACC)
    val surfaceBrightLight = Color(0xFFFFF9EB)
    val surfaceContainerLowestLight = Color(0xFFFFFFFF)
    val surfaceContainerLowLight = Color(0xFFF9F3E5)
    val surfaceContainerLight = Color(0xFFF3EDE0)
    val surfaceContainerHighLight = Color(0xFFEDE8DA)
    val surfaceContainerHighestLight = Color(0xFFE7E2D5)

    val primaryLightMediumContrast = Color(0xFF3D3700)
    val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
    val primaryContainerLightMediumContrast = Color(0xFF786E20)
    val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val secondaryLightMediumContrast = Color(0xFF3B371C)
    val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
    val secondaryContainerLightMediumContrast = Color(0xFF736E4E)
    val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val tertiaryLightMediumContrast = Color(0xFF183D2B)
    val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
    val tertiaryContainerLightMediumContrast = Color(0xFF4F7560)
    val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val errorLightMediumContrast = Color(0xFF740006)
    val onErrorLightMediumContrast = Color(0xFFFFFFFF)
    val errorContainerLightMediumContrast = Color(0xFFCF2C27)
    val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
    val backgroundLightMediumContrast = Color(0xFFFFF9EB)
    val onBackgroundLightMediumContrast = Color(0xFF1D1C13)
    val surfaceLightMediumContrast = Color(0xFFFFF9EB)
    val onSurfaceLightMediumContrast = Color(0xFF13110A)
    val surfaceVariantLightMediumContrast = Color(0xFFE8E2D0)
    val onSurfaceVariantLightMediumContrast = Color(0xFF39362A)
    val outlineLightMediumContrast = Color(0xFF565345)
    val outlineVariantLightMediumContrast = Color(0xFF716D5E)
    val scrimLightMediumContrast = Color(0xFF000000)
    val inverseSurfaceLightMediumContrast = Color(0xFF323027)
    val inverseOnSurfaceLightMediumContrast = Color(0xFFF6F0E3)
    val inversePrimaryLightMediumContrast = Color(0xFFD5C871)
    val surfaceDimLightMediumContrast = Color(0xFFCBC6B9)
    val surfaceBrightLightMediumContrast = Color(0xFFFFF9EB)
    val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
    val surfaceContainerLowLightMediumContrast = Color(0xFFF9F3E5)
    val surfaceContainerLightMediumContrast = Color(0xFFEDE8DA)
    val surfaceContainerHighLightMediumContrast = Color(0xFFE2DCCF)
    val surfaceContainerHighestLightMediumContrast = Color(0xFFD6D1C4)

    val primaryLightHighContrast = Color(0xFF322C00)
    val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
    val primaryContainerLightHighContrast = Color(0xFF524A00)
    val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val secondaryLightHighContrast = Color(0xFF302C13)
    val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
    val secondaryContainerLightHighContrast = Color(0xFF4E4A2D)
    val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val tertiaryLightHighContrast = Color(0xFF0C3321)
    val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
    val tertiaryContainerLightHighContrast = Color(0xFF2C503D)
    val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val errorLightHighContrast = Color(0xFF600004)
    val onErrorLightHighContrast = Color(0xFFFFFFFF)
    val errorContainerLightHighContrast = Color(0xFF98000A)
    val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
    val backgroundLightHighContrast = Color(0xFFFFF9EB)
    val onBackgroundLightHighContrast = Color(0xFF1D1C13)
    val surfaceLightHighContrast = Color(0xFFFFF9EB)
    val onSurfaceLightHighContrast = Color(0xFF000000)
    val surfaceVariantLightHighContrast = Color(0xFFE8E2D0)
    val onSurfaceVariantLightHighContrast = Color(0xFF000000)
    val outlineLightHighContrast = Color(0xFF2F2C20)
    val outlineVariantLightHighContrast = Color(0xFF4C493C)
    val scrimLightHighContrast = Color(0xFF000000)
    val inverseSurfaceLightHighContrast = Color(0xFF323027)
    val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
    val inversePrimaryLightHighContrast = Color(0xFFD5C871)
    val surfaceDimLightHighContrast = Color(0xFFBDB8AC)
    val surfaceBrightLightHighContrast = Color(0xFFFFF9EB)
    val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
    val surfaceContainerLowLightHighContrast = Color(0xFFF6F0E3)
    val surfaceContainerLightHighContrast = Color(0xFFE7E2D5)
    val surfaceContainerHighLightHighContrast = Color(0xFFD9D4C7)
    val surfaceContainerHighestLightHighContrast = Color(0xFFCBC6B9)

    val primaryDark = Color(0xFFD5C871)
    val onPrimaryDark = Color(0xFF373100)
    val primaryContainerDark = Color(0xFF504700)
    val onPrimaryContainerDark = Color(0xFFF2E48A)
    val secondaryDark = Color(0xFFCFC7A2)
    val onSecondaryDark = Color(0xFF353117)
    val secondaryContainerDark = Color(0xFF4C472B)
    val onSecondaryContainerDark = Color(0xFFEBE3BD)
    val tertiaryDark = Color(0xFFA7D0B7)
    val onTertiaryDark = Color(0xFF113725)
    val tertiaryContainerDark = Color(0xFF294E3B)
    val onTertiaryContainerDark = Color(0xFFC3ECD2)
    val errorDark = Color(0xFFFFB4AB)
    val onErrorDark = Color(0xFF690005)
    val errorContainerDark = Color(0xFF93000A)
    val onErrorContainerDark = Color(0xFFFFDAD6)
    val backgroundDark = Color(0xFF15130C)
    val onBackgroundDark = Color(0xFFE7E2D5)
    val surfaceDark = Color(0xFF15130C)
    val onSurfaceDark = Color(0xFFE7E2D5)
    val surfaceVariantDark = Color(0xFF4A4739)
    val onSurfaceVariantDark = Color(0xFFCCC6B5)
    val outlineDark = Color(0xFF959181)
    val outlineVariantDark = Color(0xFF4A4739)
    val scrimDark = Color(0xFF000000)
    val inverseSurfaceDark = Color(0xFFE7E2D5)
    val inverseOnSurfaceDark = Color(0xFF323027)
    val inversePrimaryDark = Color(0xFF695F12)
    val surfaceDimDark = Color(0xFF15130C)
    val surfaceBrightDark = Color(0xFF3B3930)
    val surfaceContainerLowestDark = Color(0xFF100E07)
    val surfaceContainerLowDark = Color(0xFF1D1C13)
    val surfaceContainerDark = Color(0xFF212017)
    val surfaceContainerHighDark = Color(0xFF2C2A21)
    val surfaceContainerHighestDark = Color(0xFF37352C)

    val primaryDarkMediumContrast = Color(0xFFECDE84)
    val onPrimaryDarkMediumContrast = Color(0xFF2B2600)
    val primaryContainerDarkMediumContrast = Color(0xFF9E9241)
    val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
    val secondaryDarkMediumContrast = Color(0xFFE5DDB7)
    val onSecondaryDarkMediumContrast = Color(0xFF2A260D)
    val secondaryContainerDarkMediumContrast = Color(0xFF989170)
    val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
    val tertiaryDarkMediumContrast = Color(0xFFBDE6CC)
    val onTertiaryDarkMediumContrast = Color(0xFF042C1B)
    val tertiaryContainerDarkMediumContrast = Color(0xFF729982)
    val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
    val errorDarkMediumContrast = Color(0xFFFFD2CC)
    val onErrorDarkMediumContrast = Color(0xFF540003)
    val errorContainerDarkMediumContrast = Color(0xFFFF5449)
    val onErrorContainerDarkMediumContrast = Color(0xFF000000)
    val backgroundDarkMediumContrast = Color(0xFF15130C)
    val onBackgroundDarkMediumContrast = Color(0xFFE7E2D5)
    val surfaceDarkMediumContrast = Color(0xFF15130C)
    val onSurfaceDarkMediumContrast = Color(0xFFFFFFFF)
    val surfaceVariantDarkMediumContrast = Color(0xFF4A4739)
    val onSurfaceVariantDarkMediumContrast = Color(0xFFE2DCCA)
    val outlineDarkMediumContrast = Color(0xFFB7B2A1)
    val outlineVariantDarkMediumContrast = Color(0xFF959080)
    val scrimDarkMediumContrast = Color(0xFF000000)
    val inverseSurfaceDarkMediumContrast = Color(0xFFE7E2D5)
    val inverseOnSurfaceDarkMediumContrast = Color(0xFF2C2A21)
    val inversePrimaryDarkMediumContrast = Color(0xFF514800)
    val surfaceDimDarkMediumContrast = Color(0xFF15130C)
    val surfaceBrightDarkMediumContrast = Color(0xFF47443B)
    val surfaceContainerLowestDarkMediumContrast = Color(0xFF080703)
    val surfaceContainerLowDarkMediumContrast = Color(0xFF1F1E15)
    val surfaceContainerDarkMediumContrast = Color(0xFF2A281F)
    val surfaceContainerHighDarkMediumContrast = Color(0xFF353329)
    val surfaceContainerHighestDarkMediumContrast = Color(0xFF403E34)

    val primaryDarkHighContrast = Color(0xFFFFF1A0)
    val onPrimaryDarkHighContrast = Color(0xFF000000)
    val primaryContainerDarkHighContrast = Color(0xFFD1C46D)
    val onPrimaryContainerDarkHighContrast = Color(0xFF0E0B00)
    val secondaryDarkHighContrast = Color(0xFFF9F0CA)
    val onSecondaryDarkHighContrast = Color(0xFF000000)
    val secondaryContainerDarkHighContrast = Color(0xFFCBC39F)
    val onSecondaryContainerDarkHighContrast = Color(0xFF0E0B00)
    val tertiaryDarkHighContrast = Color(0xFFD0FADF)
    val onTertiaryDarkHighContrast = Color(0xFF000000)
    val tertiaryContainerDarkHighContrast = Color(0xFFA3CCB3)
    val onTertiaryContainerDarkHighContrast = Color(0xFF000E06)
    val errorDarkHighContrast = Color(0xFFFFECE9)
    val onErrorDarkHighContrast = Color(0xFF000000)
    val errorContainerDarkHighContrast = Color(0xFFFFAEA4)
    val onErrorContainerDarkHighContrast = Color(0xFF220001)
    val backgroundDarkHighContrast = Color(0xFF15130C)
    val onBackgroundDarkHighContrast = Color(0xFFE7E2D5)
    val surfaceDarkHighContrast = Color(0xFF15130C)
    val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
    val surfaceVariantDarkHighContrast = Color(0xFF4A4739)
    val onSurfaceVariantDarkHighContrast = Color(0xFFFFFFFF)
    val outlineDarkHighContrast = Color(0xFFF6F0DD)
    val outlineVariantDarkHighContrast = Color(0xFFC8C2B1)
    val scrimDarkHighContrast = Color(0xFF000000)
    val inverseSurfaceDarkHighContrast = Color(0xFFE7E2D5)
    val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
    val inversePrimaryDarkHighContrast = Color(0xFF514800)
    val surfaceDimDarkHighContrast = Color(0xFF15130C)
    val surfaceBrightDarkHighContrast = Color(0xFF535046)
    val surfaceContainerLowestDarkHighContrast = Color(0xFF000000)
    val surfaceContainerLowDarkHighContrast = Color(0xFF212017)
    val surfaceContainerDarkHighContrast = Color(0xFF323027)
    val surfaceContainerHighDarkHighContrast = Color(0xFF3E3B32)
    val surfaceContainerHighestDarkHighContrast = Color(0xFF49473D)

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

    override val colorPrimary = Color(0xFFE3CD00)
    override val colorSecondary = Color(0xFF999164)
    override val colorTetriary = Color(0xFF649C7D)

    override val name = "Golden Dusk"

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
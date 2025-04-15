/*
 * Copyright (c) 2025 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package me.impa.knockonports.ui.theme.variant

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.persistentMapOf
import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.config.ThemeContrast

object NeonMossScheme : AppColorScheme {

    // Sky Steel
    // Seed #00E24E
    // Primary #00E24E
    // Secondary #749A71
    // Tetriary #13A0AD

    val primaryLight = Color(0xFF3A693B)
    val onPrimaryLight = Color(0xFFFFFFFF)
    val primaryContainerLight = Color(0xFFBBF0B6)
    val onPrimaryContainerLight = Color(0xFF225025)
    val secondaryLight = Color(0xFF52634F)
    val onSecondaryLight = Color(0xFFFFFFFF)
    val secondaryContainerLight = Color(0xFFD5E8CF)
    val onSecondaryContainerLight = Color(0xFF3B4B39)
    val tertiaryLight = Color(0xFF39656B)
    val onTertiaryLight = Color(0xFFFFFFFF)
    val tertiaryContainerLight = Color(0xFFBCEBF1)
    val onTertiaryContainerLight = Color(0xFF1F4D53)
    val errorLight = Color(0xFFBA1A1A)
    val onErrorLight = Color(0xFFFFFFFF)
    val errorContainerLight = Color(0xFFFFDAD6)
    val onErrorContainerLight = Color(0xFF93000A)
    val backgroundLight = Color(0xFFF7FBF1)
    val onBackgroundLight = Color(0xFF181D17)
    val surfaceLight = Color(0xFFF7FBF1)
    val onSurfaceLight = Color(0xFF181D17)
    val surfaceVariantLight = Color(0xFFDEE5D9)
    val onSurfaceVariantLight = Color(0xFF424940)
    val outlineLight = Color(0xFF72796F)
    val outlineVariantLight = Color(0xFFC2C9BD)
    val scrimLight = Color(0xFF000000)
    val inverseSurfaceLight = Color(0xFF2D322C)
    val inverseOnSurfaceLight = Color(0xFFEEF2E9)
    val inversePrimaryLight = Color(0xFFA0D49B)
    val surfaceDimLight = Color(0xFFD7DBD2)
    val surfaceBrightLight = Color(0xFFF7FBF1)
    val surfaceContainerLowestLight = Color(0xFFFFFFFF)
    val surfaceContainerLowLight = Color(0xFFF1F5EC)
    val surfaceContainerLight = Color(0xFFEBEFE6)
    val surfaceContainerHighLight = Color(0xFFE6E9E0)
    val surfaceContainerHighestLight = Color(0xFFE0E4DB)

    val primaryLightMediumContrast = Color(0xFF0F3F16)
    val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
    val primaryContainerLightMediumContrast = Color(0xFF487848)
    val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val secondaryLightMediumContrast = Color(0xFF2A3A29)
    val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
    val secondaryContainerLightMediumContrast = Color(0xFF61725D)
    val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val tertiaryLightMediumContrast = Color(0xFF083C42)
    val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
    val tertiaryContainerLightMediumContrast = Color(0xFF48747A)
    val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val errorLightMediumContrast = Color(0xFF740006)
    val onErrorLightMediumContrast = Color(0xFFFFFFFF)
    val errorContainerLightMediumContrast = Color(0xFFCF2C27)
    val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
    val backgroundLightMediumContrast = Color(0xFFF7FBF1)
    val onBackgroundLightMediumContrast = Color(0xFF181D17)
    val surfaceLightMediumContrast = Color(0xFFF7FBF1)
    val onSurfaceLightMediumContrast = Color(0xFF0E120D)
    val surfaceVariantLightMediumContrast = Color(0xFFDEE5D9)
    val onSurfaceVariantLightMediumContrast = Color(0xFF313830)
    val outlineLightMediumContrast = Color(0xFF4E544B)
    val outlineVariantLightMediumContrast = Color(0xFF686F65)
    val scrimLightMediumContrast = Color(0xFF000000)
    val inverseSurfaceLightMediumContrast = Color(0xFF2D322C)
    val inverseOnSurfaceLightMediumContrast = Color(0xFFEEF2E9)
    val inversePrimaryLightMediumContrast = Color(0xFFA0D49B)
    val surfaceDimLightMediumContrast = Color(0xFFC4C8BF)
    val surfaceBrightLightMediumContrast = Color(0xFFF7FBF1)
    val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
    val surfaceContainerLowLightMediumContrast = Color(0xFFF1F5EC)
    val surfaceContainerLightMediumContrast = Color(0xFFE6E9E0)
    val surfaceContainerHighLightMediumContrast = Color(0xFFDADED5)
    val surfaceContainerHighestLightMediumContrast = Color(0xFFCFD3CA)

    val primaryLightHighContrast = Color(0xFF02340C)
    val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
    val primaryContainerLightHighContrast = Color(0xFF245327)
    val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val secondaryLightHighContrast = Color(0xFF21301F)
    val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
    val secondaryContainerLightHighContrast = Color(0xFF3D4D3B)
    val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val tertiaryLightHighContrast = Color(0xFF003237)
    val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
    val tertiaryContainerLightHighContrast = Color(0xFF215055)
    val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val errorLightHighContrast = Color(0xFF600004)
    val onErrorLightHighContrast = Color(0xFFFFFFFF)
    val errorContainerLightHighContrast = Color(0xFF98000A)
    val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
    val backgroundLightHighContrast = Color(0xFFF7FBF1)
    val onBackgroundLightHighContrast = Color(0xFF181D17)
    val surfaceLightHighContrast = Color(0xFFF7FBF1)
    val onSurfaceLightHighContrast = Color(0xFF000000)
    val surfaceVariantLightHighContrast = Color(0xFFDEE5D9)
    val onSurfaceVariantLightHighContrast = Color(0xFF000000)
    val outlineLightHighContrast = Color(0xFF272E26)
    val outlineVariantLightHighContrast = Color(0xFF444B42)
    val scrimLightHighContrast = Color(0xFF000000)
    val inverseSurfaceLightHighContrast = Color(0xFF2D322C)
    val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
    val inversePrimaryLightHighContrast = Color(0xFFA0D49B)
    val surfaceDimLightHighContrast = Color(0xFFB6BAB2)
    val surfaceBrightLightHighContrast = Color(0xFFF7FBF1)
    val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
    val surfaceContainerLowLightHighContrast = Color(0xFFEEF2E9)
    val surfaceContainerLightHighContrast = Color(0xFFE0E4DB)
    val surfaceContainerHighLightHighContrast = Color(0xFFD2D6CD)
    val surfaceContainerHighestLightHighContrast = Color(0xFFC4C8BF)

    val primaryDark = Color(0xFFA0D49B)
    val onPrimaryDark = Color(0xFF073910)
    val primaryContainerDark = Color(0xFF225025)
    val onPrimaryContainerDark = Color(0xFFBBF0B6)
    val secondaryDark = Color(0xFFB9CCB4)
    val onSecondaryDark = Color(0xFF253423)
    val secondaryContainerDark = Color(0xFF3B4B39)
    val onSecondaryContainerDark = Color(0xFFD5E8CF)
    val tertiaryDark = Color(0xFFA1CED5)
    val onTertiaryDark = Color(0xFF00363C)
    val tertiaryContainerDark = Color(0xFF1F4D53)
    val onTertiaryContainerDark = Color(0xFFBCEBF1)
    val errorDark = Color(0xFFFFB4AB)
    val onErrorDark = Color(0xFF690005)
    val errorContainerDark = Color(0xFF93000A)
    val onErrorContainerDark = Color(0xFFFFDAD6)
    val backgroundDark = Color(0xFF10140F)
    val onBackgroundDark = Color(0xFFE0E4DB)
    val surfaceDark = Color(0xFF10140F)
    val onSurfaceDark = Color(0xFFE0E4DB)
    val surfaceVariantDark = Color(0xFF424940)
    val onSurfaceVariantDark = Color(0xFFC2C9BD)
    val outlineDark = Color(0xFF8C9388)
    val outlineVariantDark = Color(0xFF424940)
    val scrimDark = Color(0xFF000000)
    val inverseSurfaceDark = Color(0xFFE0E4DB)
    val inverseOnSurfaceDark = Color(0xFF2D322C)
    val inversePrimaryDark = Color(0xFF3A693B)
    val surfaceDimDark = Color(0xFF10140F)
    val surfaceBrightDark = Color(0xFF363A34)
    val surfaceContainerLowestDark = Color(0xFF0B0F0A)
    val surfaceContainerLowDark = Color(0xFF181D17)
    val surfaceContainerDark = Color(0xFF1C211B)
    val surfaceContainerHighDark = Color(0xFF272B25)
    val surfaceContainerHighestDark = Color(0xFF323630)

    val primaryDarkMediumContrast = Color(0xFFB5EAB0)
    val onPrimaryDarkMediumContrast = Color(0xFF002D08)
    val primaryContainerDarkMediumContrast = Color(0xFF6B9D69)
    val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
    val secondaryDarkMediumContrast = Color(0xFFCFE2C9)
    val onSecondaryDarkMediumContrast = Color(0xFF1A2919)
    val secondaryContainerDarkMediumContrast = Color(0xFF849680)
    val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
    val tertiaryDarkMediumContrast = Color(0xFFB6E4EB)
    val onTertiaryDarkMediumContrast = Color(0xFF002B2F)
    val tertiaryContainerDarkMediumContrast = Color(0xFF6B989E)
    val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
    val errorDarkMediumContrast = Color(0xFFFFD2CC)
    val onErrorDarkMediumContrast = Color(0xFF540003)
    val errorContainerDarkMediumContrast = Color(0xFFFF5449)
    val onErrorContainerDarkMediumContrast = Color(0xFF000000)
    val backgroundDarkMediumContrast = Color(0xFF10140F)
    val onBackgroundDarkMediumContrast = Color(0xFFE0E4DB)
    val surfaceDarkMediumContrast = Color(0xFF10140F)
    val onSurfaceDarkMediumContrast = Color(0xFFFFFFFF)
    val surfaceVariantDarkMediumContrast = Color(0xFF424940)
    val onSurfaceVariantDarkMediumContrast = Color(0xFFD8DED3)
    val outlineDarkMediumContrast = Color(0xFFADB4A9)
    val outlineVariantDarkMediumContrast = Color(0xFF8B9288)
    val scrimDarkMediumContrast = Color(0xFF000000)
    val inverseSurfaceDarkMediumContrast = Color(0xFFE0E4DB)
    val inverseOnSurfaceDarkMediumContrast = Color(0xFF272B25)
    val inversePrimaryDarkMediumContrast = Color(0xFF235126)
    val surfaceDimDarkMediumContrast = Color(0xFF10140F)
    val surfaceBrightDarkMediumContrast = Color(0xFF41463F)
    val surfaceContainerLowestDarkMediumContrast = Color(0xFF050805)
    val surfaceContainerLowDarkMediumContrast = Color(0xFF1A1F19)
    val surfaceContainerDarkMediumContrast = Color(0xFF252923)
    val surfaceContainerHighDarkMediumContrast = Color(0xFF2F342E)
    val surfaceContainerHighestDarkMediumContrast = Color(0xFF3A3F39)

    val primaryDarkHighContrast = Color(0xFFC8FEC2)
    val onPrimaryDarkHighContrast = Color(0xFF000000)
    val primaryContainerDarkHighContrast = Color(0xFF9CD098)
    val onPrimaryContainerDarkHighContrast = Color(0xFF000F02)
    val secondaryDarkHighContrast = Color(0xFFE2F5DC)
    val onSecondaryDarkHighContrast = Color(0xFF000000)
    val secondaryContainerDarkHighContrast = Color(0xFFB5C8B0)
    val onSecondaryContainerDarkHighContrast = Color(0xFF020E03)
    val tertiaryDarkHighContrast = Color(0xFFC9F8FF)
    val onTertiaryDarkHighContrast = Color(0xFF000000)
    val tertiaryContainerDarkHighContrast = Color(0xFF9DCBD1)
    val onTertiaryContainerDarkHighContrast = Color(0xFF000E10)
    val errorDarkHighContrast = Color(0xFFFFECE9)
    val onErrorDarkHighContrast = Color(0xFF000000)
    val errorContainerDarkHighContrast = Color(0xFFFFAEA4)
    val onErrorContainerDarkHighContrast = Color(0xFF220001)
    val backgroundDarkHighContrast = Color(0xFF10140F)
    val onBackgroundDarkHighContrast = Color(0xFFE0E4DB)
    val surfaceDarkHighContrast = Color(0xFF10140F)
    val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
    val surfaceVariantDarkHighContrast = Color(0xFF424940)
    val onSurfaceVariantDarkHighContrast = Color(0xFFFFFFFF)
    val outlineDarkHighContrast = Color(0xFFECF2E6)
    val outlineVariantDarkHighContrast = Color(0xFFBEC5B9)
    val scrimDarkHighContrast = Color(0xFF000000)
    val inverseSurfaceDarkHighContrast = Color(0xFFE0E4DB)
    val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
    val inversePrimaryDarkHighContrast = Color(0xFF235126)
    val surfaceDimDarkHighContrast = Color(0xFF10140F)
    val surfaceBrightDarkHighContrast = Color(0xFF4D514B)
    val surfaceContainerLowestDarkHighContrast = Color(0xFF000000)
    val surfaceContainerLowDarkHighContrast = Color(0xFF1C211B)
    val surfaceContainerDarkHighContrast = Color(0xFF2D322C)
    val surfaceContainerHighDarkHighContrast = Color(0xFF383D36)
    val surfaceContainerHighestDarkHighContrast = Color(0xFF444842)

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

    override val colorPrimary = Color(0xFF00E24E)
    override val colorSecondary = Color(0xFF749A71)
    override val colorTetriary = Color(0xFF13A0AD)

    override val name = "Neon Moss"

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
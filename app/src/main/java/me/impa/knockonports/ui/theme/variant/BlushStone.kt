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

object BlushStone : AppColorScheme {

    // Blush Stone
    // Seed #FFBEC3
    // Primary #FFBEC3
    // Secondary #A08C8D
    // Tetriary #A28D76

    val primaryLight = Color(0xFF8F4952)
    val onPrimaryLight = Color(0xFFFFFFFF)
    val primaryContainerLight = Color(0xFFFFDADC)
    val onPrimaryContainerLight = Color(0xFF72333B)
    val secondaryLight = Color(0xFF765659)
    val onSecondaryLight = Color(0xFFFFFFFF)
    val secondaryContainerLight = Color(0xFFFFDADC)
    val onSecondaryContainerLight = Color(0xFF5C3F41)
    val tertiaryLight = Color(0xFF775930)
    val onTertiaryLight = Color(0xFFFFFFFF)
    val tertiaryContainerLight = Color(0xFFFFDDB5)
    val onTertiaryContainerLight = Color(0xFF5D411B)
    val errorLight = Color(0xFFBA1A1A)
    val onErrorLight = Color(0xFFFFFFFF)
    val errorContainerLight = Color(0xFFFFDAD6)
    val onErrorContainerLight = Color(0xFF93000A)
    val backgroundLight = Color(0xFFFFF8F7)
    val onBackgroundLight = Color(0xFF22191A)
    val surfaceLight = Color(0xFFFFF8F7)
    val onSurfaceLight = Color(0xFF22191A)
    val surfaceVariantLight = Color(0xFFF4DDDE)
    val onSurfaceVariantLight = Color(0xFF524344)
    val outlineLight = Color(0xFF857374)
    val outlineVariantLight = Color(0xFFD7C1C2)
    val scrimLight = Color(0xFF000000)
    val inverseSurfaceLight = Color(0xFF382E2E)
    val inverseOnSurfaceLight = Color(0xFFFFEDED)
    val inversePrimaryLight = Color(0xFFFFB2B9)
    val surfaceDimLight = Color(0xFFE7D6D6)
    val surfaceBrightLight = Color(0xFFFFF8F7)
    val surfaceContainerLowestLight = Color(0xFFFFFFFF)
    val surfaceContainerLowLight = Color(0xFFFFF0F0)
    val surfaceContainerLight = Color(0xFFFCEAEA)
    val surfaceContainerHighLight = Color(0xFFF6E4E4)
    val surfaceContainerHighestLight = Color(0xFFF0DEDF)

    val primaryLightMediumContrast = Color(0xFF5D222B)
    val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
    val primaryContainerLightMediumContrast = Color(0xFFA05860)
    val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val secondaryLightMediumContrast = Color(0xFF4A2F31)
    val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
    val secondaryContainerLightMediumContrast = Color(0xFF866567)
    val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val tertiaryLightMediumContrast = Color(0xFF4A310C)
    val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
    val tertiaryContainerLightMediumContrast = Color(0xFF87673D)
    val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
    val errorLightMediumContrast = Color(0xFF740006)
    val onErrorLightMediumContrast = Color(0xFFFFFFFF)
    val errorContainerLightMediumContrast = Color(0xFFCF2C27)
    val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
    val backgroundLightMediumContrast = Color(0xFFFFF8F7)
    val onBackgroundLightMediumContrast = Color(0xFF22191A)
    val surfaceLightMediumContrast = Color(0xFFFFF8F7)
    val onSurfaceLightMediumContrast = Color(0xFF170F10)
    val surfaceVariantLightMediumContrast = Color(0xFFF4DDDE)
    val onSurfaceVariantLightMediumContrast = Color(0xFF413334)
    val outlineLightMediumContrast = Color(0xFF5E4F4F)
    val outlineVariantLightMediumContrast = Color(0xFF7A696A)
    val scrimLightMediumContrast = Color(0xFF000000)
    val inverseSurfaceLightMediumContrast = Color(0xFF382E2E)
    val inverseOnSurfaceLightMediumContrast = Color(0xFFFFEDED)
    val inversePrimaryLightMediumContrast = Color(0xFFFFB2B9)
    val surfaceDimLightMediumContrast = Color(0xFFD3C3C3)
    val surfaceBrightLightMediumContrast = Color(0xFFFFF8F7)
    val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
    val surfaceContainerLowLightMediumContrast = Color(0xFFFFF0F0)
    val surfaceContainerLightMediumContrast = Color(0xFFF6E4E4)
    val surfaceContainerHighLightMediumContrast = Color(0xFFEAD9D9)
    val surfaceContainerHighestLightMediumContrast = Color(0xFFDFCECE)

    val primaryLightHighContrast = Color(0xFF511921)
    val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
    val primaryContainerLightHighContrast = Color(0xFF75353D)
    val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val secondaryLightHighContrast = Color(0xFF3F2527)
    val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
    val secondaryContainerLightHighContrast = Color(0xFF5F4144)
    val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val tertiaryLightHighContrast = Color(0xFF3F2703)
    val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
    val tertiaryContainerLightHighContrast = Color(0xFF60441D)
    val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
    val errorLightHighContrast = Color(0xFF600004)
    val onErrorLightHighContrast = Color(0xFFFFFFFF)
    val errorContainerLightHighContrast = Color(0xFF98000A)
    val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
    val backgroundLightHighContrast = Color(0xFFFFF8F7)
    val onBackgroundLightHighContrast = Color(0xFF22191A)
    val surfaceLightHighContrast = Color(0xFFFFF8F7)
    val onSurfaceLightHighContrast = Color(0xFF000000)
    val surfaceVariantLightHighContrast = Color(0xFFF4DDDE)
    val onSurfaceVariantLightHighContrast = Color(0xFF000000)
    val outlineLightHighContrast = Color(0xFF36292A)
    val outlineVariantLightHighContrast = Color(0xFF554546)
    val scrimLightHighContrast = Color(0xFF000000)
    val inverseSurfaceLightHighContrast = Color(0xFF382E2E)
    val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
    val inversePrimaryLightHighContrast = Color(0xFFFFB2B9)
    val surfaceDimLightHighContrast = Color(0xFFC5B5B5)
    val surfaceBrightLightHighContrast = Color(0xFFFFF8F7)
    val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
    val surfaceContainerLowLightHighContrast = Color(0xFFFFEDED)
    val surfaceContainerLightHighContrast = Color(0xFFF0DEDF)
    val surfaceContainerHighLightHighContrast = Color(0xFFE2D0D1)
    val surfaceContainerHighestLightHighContrast = Color(0xFFD3C3C3)

    val primaryDark = Color(0xFFFFB2B9)
    val onPrimaryDark = Color(0xFF561D26)
    val primaryContainerDark = Color(0xFF72333B)
    val onPrimaryContainerDark = Color(0xFFFFDADC)
    val secondaryDark = Color(0xFFE5BDBF)
    val onSecondaryDark = Color(0xFF44292C)
    val secondaryContainerDark = Color(0xFF5C3F41)
    val onSecondaryContainerDark = Color(0xFFFFDADC)
    val tertiaryDark = Color(0xFFE8C08E)
    val onTertiaryDark = Color(0xFF442B06)
    val tertiaryContainerDark = Color(0xFF5D411B)
    val onTertiaryContainerDark = Color(0xFFFFDDB5)
    val errorDark = Color(0xFFFFB4AB)
    val onErrorDark = Color(0xFF690005)
    val errorContainerDark = Color(0xFF93000A)
    val onErrorContainerDark = Color(0xFFFFDAD6)
    val backgroundDark = Color(0xFF1A1112)
    val onBackgroundDark = Color(0xFFF0DEDF)
    val surfaceDark = Color(0xFF1A1112)
    val onSurfaceDark = Color(0xFFF0DEDF)
    val surfaceVariantDark = Color(0xFF524344)
    val onSurfaceVariantDark = Color(0xFFD7C1C2)
    val outlineDark = Color(0xFF9F8C8D)
    val outlineVariantDark = Color(0xFF524344)
    val scrimDark = Color(0xFF000000)
    val inverseSurfaceDark = Color(0xFFF0DEDF)
    val inverseOnSurfaceDark = Color(0xFF382E2E)
    val inversePrimaryDark = Color(0xFF8F4952)
    val surfaceDimDark = Color(0xFF1A1112)
    val surfaceBrightDark = Color(0xFF413737)
    val surfaceContainerLowestDark = Color(0xFF140C0D)
    val surfaceContainerLowDark = Color(0xFF22191A)
    val surfaceContainerDark = Color(0xFF271D1E)
    val surfaceContainerHighDark = Color(0xFF312828)
    val surfaceContainerHighestDark = Color(0xFF3D3233)

    val primaryDarkMediumContrast = Color(0xFFFFD1D4)
    val onPrimaryDarkMediumContrast = Color(0xFF48121B)
    val primaryContainerDarkMediumContrast = Color(0xFFCA7A82)
    val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
    val secondaryDarkMediumContrast = Color(0xFFFCD2D5)
    val onSecondaryDarkMediumContrast = Color(0xFF371F21)
    val secondaryContainerDarkMediumContrast = Color(0xFFAC888A)
    val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
    val tertiaryDarkMediumContrast = Color(0xFFFFD5A3)
    val onTertiaryDarkMediumContrast = Color(0xFF372100)
    val tertiaryContainerDarkMediumContrast = Color(0xFFAE8A5D)
    val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
    val errorDarkMediumContrast = Color(0xFFFFD2CC)
    val onErrorDarkMediumContrast = Color(0xFF540003)
    val errorContainerDarkMediumContrast = Color(0xFFFF5449)
    val onErrorContainerDarkMediumContrast = Color(0xFF000000)
    val backgroundDarkMediumContrast = Color(0xFF1A1112)
    val onBackgroundDarkMediumContrast = Color(0xFFF0DEDF)
    val surfaceDarkMediumContrast = Color(0xFF1A1112)
    val onSurfaceDarkMediumContrast = Color(0xFFFFFFFF)
    val surfaceVariantDarkMediumContrast = Color(0xFF524344)
    val onSurfaceVariantDarkMediumContrast = Color(0xFFEDD7D8)
    val outlineDarkMediumContrast = Color(0xFFC2ADAE)
    val outlineVariantDarkMediumContrast = Color(0xFF9F8C8D)
    val scrimDarkMediumContrast = Color(0xFF000000)
    val inverseSurfaceDarkMediumContrast = Color(0xFFF0DEDF)
    val inverseOnSurfaceDarkMediumContrast = Color(0xFF312828)
    val inversePrimaryDarkMediumContrast = Color(0xFF74343C)
    val surfaceDimDarkMediumContrast = Color(0xFF1A1112)
    val surfaceBrightDarkMediumContrast = Color(0xFF4D4242)
    val surfaceContainerLowestDarkMediumContrast = Color(0xFF0D0606)
    val surfaceContainerLowDarkMediumContrast = Color(0xFF241B1C)
    val surfaceContainerDarkMediumContrast = Color(0xFF2F2526)
    val surfaceContainerHighDarkMediumContrast = Color(0xFF3A3031)
    val surfaceContainerHighestDarkMediumContrast = Color(0xFF463B3C)

    val primaryDarkHighContrast = Color(0xFFFFEBEC)
    val onPrimaryDarkHighContrast = Color(0xFF000000)
    val primaryContainerDarkHighContrast = Color(0xFFFFACB4)
    val onPrimaryContainerDarkHighContrast = Color(0xFF210005)
    val secondaryDarkHighContrast = Color(0xFFFFEBEC)
    val onSecondaryDarkHighContrast = Color(0xFF000000)
    val secondaryContainerDarkHighContrast = Color(0xFFE1B9BB)
    val onSecondaryContainerDarkHighContrast = Color(0xFF190608)
    val tertiaryDarkHighContrast = Color(0xFFFFEDDB)
    val onTertiaryDarkHighContrast = Color(0xFF000000)
    val tertiaryContainerDarkHighContrast = Color(0xFFE4BC8B)
    val onTertiaryContainerDarkHighContrast = Color(0xFF140900)
    val errorDarkHighContrast = Color(0xFFFFECE9)
    val onErrorDarkHighContrast = Color(0xFF000000)
    val errorContainerDarkHighContrast = Color(0xFFFFAEA4)
    val onErrorContainerDarkHighContrast = Color(0xFF220001)
    val backgroundDarkHighContrast = Color(0xFF1A1112)
    val onBackgroundDarkHighContrast = Color(0xFFF0DEDF)
    val surfaceDarkHighContrast = Color(0xFF1A1112)
    val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
    val surfaceVariantDarkHighContrast = Color(0xFF524344)
    val onSurfaceVariantDarkHighContrast = Color(0xFFFFFFFF)
    val outlineDarkHighContrast = Color(0xFFFFEBEC)
    val outlineVariantDarkHighContrast = Color(0xFFD3BEBE)
    val scrimDarkHighContrast = Color(0xFF000000)
    val inverseSurfaceDarkHighContrast = Color(0xFFF0DEDF)
    val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
    val inversePrimaryDarkHighContrast = Color(0xFF74343C)
    val surfaceDimDarkHighContrast = Color(0xFF1A1112)
    val surfaceBrightDarkHighContrast = Color(0xFF594D4E)
    val surfaceContainerLowestDarkHighContrast = Color(0xFF000000)
    val surfaceContainerLowDarkHighContrast = Color(0xFF271D1E)
    val surfaceContainerDarkHighContrast = Color(0xFF382E2E)
    val surfaceContainerHighDarkHighContrast = Color(0xFF443939)
    val surfaceContainerHighestDarkHighContrast = Color(0xFF4F4445)

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

    override val colorPrimary = Color(0xFFFFBEC3)
    override val colorSecondary = Color(0xFFA08C8D)
    override val colorTetriary = Color(0xFFA28D76)

    override val name = "Blush Stone"

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
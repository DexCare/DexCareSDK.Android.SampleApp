package com.dexcare.sample.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    surface = Color(0xFFDAD5D9),
    background = Color(0xFFDAD5D9),
)

data class AppColor(
    val primary: Color = Color(0xff0069aa),
    val primaryDark: Color = Color(0xff075c87),
    val accent: Color = Color(0xff56a1d5),
    val error: Color = Color(0xffDA3510),
    val success: Color = Color(0xff00aa55),
    val link: Color = Color(0xff0070a9),
    val light: Color = Color(0xffffffff)
)

val AppLightColor = AppColor()
val AppDarkColor = AppColor()

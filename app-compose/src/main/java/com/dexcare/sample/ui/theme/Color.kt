package com.dexcare.sample.ui.theme

import androidx.compose.ui.graphics.Color


data class AppColor(
    val primary: Color = Color(0xff0069aa),
    val primaryDark: Color = Color(0xff075c87),
    val accent: Color = Color(0xff56a1d5),
    val error: Color = Color(0xffDA3510),
    val success: Color = Color(0xFF43A047),
    val link: Color = Color(0xff0070a9),
    val light: Color = Color(0xffffffff),
    val background: Color = Color(0xFFDAD5D9),
    val surface: Color = Color(0xFFDAD5D9),
    val textPrimary :Color = Color(0xff333333)
)

val AppLightColor = AppColor()
val AppDarkColor = AppColor()

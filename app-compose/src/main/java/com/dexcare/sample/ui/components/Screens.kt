package com.dexcare.sample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import com.dexcare.sample.ui.theme.LocalColorScheme


@Composable
fun ActionBarScreen(
    title: String,
    onBackPressed: () -> Unit,
    content: @Composable () -> Unit
) {
    Column {
        Actionbar(
            title = title,
            icon = Icons.Default.ArrowBack,
            actionIconClick = onBackPressed
        )
        FullScreen {
            content()
        }
    }
}

@Composable
fun ActionBarScreen(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Actionbar(
            title = title,
            icon = null,
        )
        FullScreen {
            content()
        }
    }
}

@Composable
fun FullScreen(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(LocalColorScheme.current.background)
    ) {
        content()
    }
}

/**
 * Actionbar with icon and title.
 *
 * @param modifier [Modifier] parameter for the actionbar container.
 * @param title The title text for actionbar.
 * @param textStyle [TextStyle] for the action bar title.
 *
 * @param icon Icon for actionbar.
 * @param iconContentDescription content description for action bar icon.
 * Recommended to be provided for each icon.
 * @param actionIconClick Callback for click action on actionbar icon.
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Actionbar(
    modifier: Modifier = Modifier,
    title: String,
    textStyle: TextStyle? = null,
    icon: ImageVector? = null,
    iconContentDescription: String? = null,
    actionIconClick: () -> Unit = {},
) {
    val appColors = LocalColorScheme.current
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier.semantics { heading() },
                text = title,
                style = textStyle ?: MaterialTheme.typography.bodyLarge,
                color = appColors.onPrimary
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = appColors.primary
        ),
        navigationIcon = {
            if (icon != null) {
                IconButton(
                    onClick = actionIconClick,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = iconContentDescription,
                        tint = appColors.onPrimary
                    )
                }
            }
        },
    )
}

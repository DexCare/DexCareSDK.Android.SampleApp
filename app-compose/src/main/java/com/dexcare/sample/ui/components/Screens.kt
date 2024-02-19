package com.dexcare.sample.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalAppColor
import com.dexcare.sample.ui.theme.PreviewUi

@Composable
fun ActionBarScreen(
    title: String, onBackPressed: () -> Unit, content: @Composable () -> Unit
) {
    Column {
        Actionbar(
            title = title, icon = Icons.Default.ArrowBack, actionIconClick = onBackPressed
        )
        FullScreen {
            content()
        }
    }
}

@Composable
fun ActionBarScreen(
    title: String, content: @Composable () -> Unit
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
            .background(LocalAppColor.current.background)
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
    val appColors = LocalAppColor.current
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier.semantics { heading() },
                text = title,
                style = textStyle ?: MaterialTheme.typography.bodyLarge,
                color = appColors.light
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
                        tint = appColors.light
                    )
                }
            }
        },
    )
}


@Composable
fun InformationScreen(
    title: String, message: String, showTopBar: Boolean = true, onDismiss: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        BackHandler {
            onDismiss()
        }
        if (showTopBar) {
            Actionbar(
                title = "", icon = Icons.Default.Close, actionIconClick = onDismiss
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(Dimens.Spacing.medium),
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(Dimens.Spacing.medium),
                textAlign = TextAlign.Center,
            )

            SolidButton(
                text = "Dismiss",
                onClick = onDismiss,
                modifier = Modifier
                    .padding(Dimens.Spacing.x2Large)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun AcmeCircularProgress(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier, color = LocalAppColor.current.primaryDark, strokeWidth = 5.dp
    )
}

@Composable
fun FullScreenProgress() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AcmeCircularProgress()
    }
}

@Preview
@Composable
private fun PreviewSuccessScreen() {
    PreviewUi {
        InformationScreen(title = "Appointment Complete",
            message = "Your appointment with Dr. ABC has been scheduled for Oct 4th at 2:00 PM.",
            onDismiss = {})
    }
}

@Composable
@Preview
private fun PreviewFullScreenProgress() {
    PreviewUi {
        FullScreenProgress()
    }
}

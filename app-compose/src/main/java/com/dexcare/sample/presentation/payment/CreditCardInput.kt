package com.dexcare.sample.presentation.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dexcare.sample.ui.components.SolidButton
import com.dexcare.sample.ui.theme.Dimens
import com.dexcare.sample.ui.theme.LocalColorScheme
import com.stripe.android.Stripe
import com.stripe.android.view.CardInputWidget
import com.dexcare.acme.android.R
import org.dexcare.services.models.CreditCard
import timber.log.Timber

@Composable
fun CreditCardInput(modifier: Modifier = Modifier, onCardSubmit: (CreditCard) -> Unit) {
    var inputWidget: CardInputWidget? = null
    val context = LocalContext.current
    val isInputError = remember {
        mutableStateOf(false)
    }
    Column {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                CardInputWidget(context)
            },
            update = {
                inputWidget = it
            }
        )
        if (isInputError.value) {
            Text(
                text = "Invalid input",
                modifier = Modifier.padding(vertical = Dimens.Spacing.xSmall),
                style = MaterialTheme.typography.labelSmall,
                color = LocalColorScheme.current.error
            )
        }

        SolidButton(
            text = "Submit",
            modifier = Modifier
                .padding(
                    vertical = Dimens.Spacing.large,
                    horizontal = Dimens.Spacing.x2Large
                )
                .fillMaxWidth()
        ) {
            Timber.d("CardParams: ${inputWidget?.cardParams}")
            val cardParams = inputWidget?.cardParams
            if (cardParams != null) {
                isInputError.value = false
                val token = Stripe(context, context.getString(R.string.stripe_publishable_key))
                    .createCardTokenSynchronous(cardParams)
                onCardSubmit(CreditCard(token.id))
            } else {
                isInputError.value = true
            }
        }
    }

}

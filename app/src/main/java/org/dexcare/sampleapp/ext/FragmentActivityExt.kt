package org.dexcare.sampleapp.ext

import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import org.dexcare.sampleapp.R

fun FragmentActivity.showMaterialDialog(
    title: String? = null,
    message: String? = null,
    positiveButtonText: String? = null,
    positiveButtonCallback: () -> Unit = {},
    negativeButtonText: String? = null,
    negativeButtonCallback: () -> Unit = {},
    isCancelable: Boolean = true
) {
    MaterialDialog(this).show {
        cornerRadius(res = R.dimen.material_dialog_corner_radius)

        cancelable(isCancelable)

        title?.let { title(text = it) }
        message?.let { message(text = it) }

        if (positiveButtonText != null) positiveButton(text = positiveButtonText) {
            positiveButtonCallback()
        }

        if (negativeButtonText != null) negativeButton(text = negativeButtonText) {
            negativeButtonCallback()
        }
    }
}

fun FragmentActivity.showItemListDialog(
    items: List<String>,
    title: String = "",
    onItemSelected: (index: Int) -> Unit
) {
    MaterialDialog(this).show {
        cornerRadius(res = R.dimen.material_dialog_corner_radius)
        title(text = title)

        listItems(
            items = items,
            waitForPositiveButton = false,
            selection = { _: MaterialDialog, index: Int, _: String ->
                onItemSelected(index)
                dismiss()
            }
        )

        negativeButton(R.string.cancel)
    }
}
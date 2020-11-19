package org.dexcare.sampleapp.ui.common.viewmodel.input.address

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Keep
import com.seatgeek.placesautocomplete.PlacesApi
import com.seatgeek.placesautocomplete.adapter.AbstractPlacesAutocompleteAdapter
import com.seatgeek.placesautocomplete.history.AutocompleteHistoryManager
import com.seatgeek.placesautocomplete.model.AutocompleteResultType
import com.seatgeek.placesautocomplete.model.Place
import org.dexcare.sampleapp.R

@Suppress("unused")
@Keep
class AddressAutocompleteAdapter(
    context: Context,
    api: PlacesApi,
    autocompleteResultType: AutocompleteResultType,
    history: AutocompleteHistoryManager
) : AbstractPlacesAutocompleteAdapter(context, api, autocompleteResultType, history) {

    override fun newView(parent: ViewGroup): View =
        LayoutInflater.from(parent.context).inflate(R.layout.layout_address_autocomplete_item, parent, false)


    override fun bindView(view: View, item: Place) {
        val textView = view.findViewById<TextView>(R.id.text)
        textView.text = item.description
    }
}
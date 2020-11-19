package org.dexcare.sampleapp.ui.common.viewmodel.input.address

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.seatgeek.placesautocomplete.model.AddressComponentType
import com.seatgeek.placesautocomplete.model.PlaceDetails
import org.dexcare.sampleapp.ui.common.viewmodel.BaseViewModel
import org.dexcare.util.ZipCodeValidator

class AddressViewModel : BaseViewModel() {

    @Bindable
    var streetAddress: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.streetAddress)
        }

    @Bindable
    var addressLine2: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.addressLine2)
        }

    @Bindable
    var city: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)
        }

    @Bindable
    var state: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.state)
        }

    @Bindable
    var zipCode: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.zipCode)
        }

    fun populateAddressFields(details: PlaceDetails?) {

        if (details == null) {
            streetAddress = ""
            return
        }

        var street = ""
        var route = ""
        var cityStr = ""
        var adminArea = ""

        for (component in details.address_components) {
            for (type in component.types) {
                when (type) {
                    AddressComponentType.STREET_NUMBER -> street = component.short_name
                    AddressComponentType.ROUTE -> route = component.short_name
                    AddressComponentType.LOCALITY -> cityStr = component.long_name
                    AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1 -> {
                        adminArea = component.long_name
                        state = component.short_name
                    }
                    AddressComponentType.POSTAL_CODE -> zipCode = component.long_name
                    else -> {
                    }
                }
            }
        }

        streetAddress = (if (street.isEmpty()) "" else "$street ") + route

        if (cityStr.isEmpty()) {
            cityStr = adminArea
        }

        city = cityStr
    }

    fun clear() {
        streetAddress = ""
        addressLine2 = ""
        city = ""
        state = ""
        zipCode = ""
    }

    fun isValid(): Boolean {
        return streetAddress.isNotEmpty() &&
                city.isNotEmpty() &&
                state.isNotEmpty() &&
                ZipCodeValidator.isValid(zipCode)
    }

}
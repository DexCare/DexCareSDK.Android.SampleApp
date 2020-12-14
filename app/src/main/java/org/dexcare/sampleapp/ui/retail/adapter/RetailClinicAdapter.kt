package org.dexcare.sampleapp.ui.retail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import org.dexcare.sampleapp.databinding.RecycleritemClinicBinding
import org.dexcare.sampleapp.ui.common.recyclerview.ViewModelAdapter

class RetailClinicAdapter(viewModels: MutableList<RetailClinicViewModel>) :
    ViewModelAdapter<RetailClinicViewModel>(viewModels) {
    override fun getViewDataBinding(
        objectInstance: ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean
    ): ViewDataBinding? {
        return RecycleritemClinicBinding.inflate(inflater)
    }
}

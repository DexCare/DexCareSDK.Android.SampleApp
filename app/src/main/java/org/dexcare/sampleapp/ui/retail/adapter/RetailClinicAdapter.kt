package org.dexcare.sampleapp.ui.retail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import org.dexcare.sampleapp.databinding.RecycleritemClinicBinding
import org.dexcare.sampleapp.ui.common.recyclerview.ViewModelAdapter
import org.koin.core.KoinComponent

class RetailClinicAdapter(viewModels: MutableList<RetailClinicViewModel>) : ViewModelAdapter<RetailClinicViewModel>(viewModels), KoinComponent {
    override fun getViewDataBinding(
        objectInstance: ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean
    ): ViewDataBinding? {
        return RecycleritemClinicBinding.inflate(inflater).also { binding ->

        }
    }
}

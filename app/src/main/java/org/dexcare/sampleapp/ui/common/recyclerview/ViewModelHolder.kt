package org.dexcare.sampleapp.ui.common.recyclerview

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView

class ViewModelHolder<T : ViewDataBinding>(private val binding: T) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(viewModel: ViewModel) {
        // every list item view is expected to have a 'viewModel' variable in the data section of the xml
        binding.setVariable(BR.viewModel, viewModel)
    }
}

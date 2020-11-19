package org.dexcare.sampleapp.ui.common.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import org.koin.ext.getFullName
import kotlin.reflect.KClass

abstract class ViewModelAdapter<T : ViewModel>(items: MutableList<T>) :
    RecyclerView.Adapter<ViewModelHolder<*>>() {

    var items: MutableList<T> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val itemTypes: MutableList<KClass<out ViewModel>> = mutableListOf()
    private val positionList: MutableList<Int> = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        val itemType = items[position]::class

        val itemTypePosition = itemTypes.indexOfFirst { it.getFullName() == itemType.getFullName() }

        return if (itemTypePosition == -1) {
            itemTypes.add(itemType)
            itemTypes.size - 1
        } else itemTypePosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModelHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val itemType = itemTypes[viewType]

        val viewDataBinding = getViewDataBinding(
            itemType.objectInstance
            // The ViewModel classes need to have an empty constructor to use newInstance().
            // You can use .apply{} instead of passing values in the constructor
                ?: itemType.java.newInstance(), inflater, parent
        )
            ?: throw Exception("Unknown view model: ${itemType::class.getFullName()}")

        return ViewModelHolder(viewDataBinding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewModelHolder<*>, position: Int) {
        holder.onBind(items[position])
    }

    abstract fun getViewDataBinding(
        objectInstance: ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean = false
    ): ViewDataBinding?
}

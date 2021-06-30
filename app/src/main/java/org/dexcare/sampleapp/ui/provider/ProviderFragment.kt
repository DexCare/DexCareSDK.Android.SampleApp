package org.dexcare.sampleapp.ui.provider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.databinding.ProviderFragmentBinding
import org.dexcare.sampleapp.ext.showMaterialDialog
import org.dexcare.sampleapp.ui.provider.adapter.ProviderTimeSlotAdapter
import org.dexcare.sampleapp.ui.provider.adapter.ProviderTimeSlotViewModel
import org.dexcare.services.provider.models.Provider
import org.koin.android.ext.android.get
import java.time.format.DateTimeFormatter
import java.util.*

class ProviderFragment : Fragment() {

    private val viewModel: ProviderFragmentViewModel by viewModels()
    private lateinit var binding: ProviderFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProviderFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.recyclerTimeSlots.apply {
            adapter = ProviderTimeSlotAdapter(
                get(),
                mutableListOf()
            )
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                showMaterialDialog(message = it.javaClass.simpleName)
            }
        })

        viewModel.getProvider(getString(R.string.hardcoded_national_provider_id))
            .observe(viewLifecycleOwner, { provider ->
                viewModel.providerName = provider.name

                fetchTimeSlots(provider)
            })
    }

    private fun fetchTimeSlots(provider: Provider) {
        viewModel.getProviderTimeSlots()
            .observe(viewLifecycleOwner, { providerTimeSlot ->
                val nextDayWithTimeSlots = providerTimeSlot.scheduleDays.firstOrNull {
                    it.timeSlots.isNotEmpty()
                }
                if (nextDayWithTimeSlots == null) {
                    viewModel.timeSlotDate = getString(R.string.no_timeslots_found)
                    return@observe
                }


                val dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
                viewModel.timeSlotDate = nextDayWithTimeSlots.localDate.format(dateFormat)
                val timeSlots = nextDayWithTimeSlots.timeSlots

                (binding.recyclerTimeSlots.adapter as? ProviderTimeSlotAdapter)?.items =
                    timeSlots.map { timeSlot ->
                        ProviderTimeSlotViewModel(
                            timeSlot, provider
                        )
                    }.toMutableList()
            })
    }
}

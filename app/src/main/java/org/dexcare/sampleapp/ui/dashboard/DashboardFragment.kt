package org.dexcare.sampleapp.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.dexcare.sampleapp.databinding.DashboardFragmentBinding
import timber.log.Timber


class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var binding: DashboardFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webview.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        binding.webview.loadUrl("https://scheduling.frosh.dex.care/virtual/precheck/availability?brand=PROVIDENCE&locationState=CO")
                //"https://th.frosh.dex.care/visit/room/323fb4ea-e1cb-490d-83e1-b9db970526ca?brand=PROVIDENCE")

        binding.webview.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return false
            }
        }

        val ua = binding.webview.settings.userAgentString

        Timber.d("abinet $ua")

        binding.btnProvider.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.toProviderFragment())
        }

        binding.btnRetail.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.toRetailClinicsFragment())
        }

        binding.btnVirtual.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.toVirtualRegionFragment())
        }
    }

}

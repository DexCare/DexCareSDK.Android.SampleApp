package org.dexcare.sampleapp.ui.webview

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.dexcare.sampleapp.databinding.WebviewFragmentBinding
import timber.log.Timber

class WebViewFragment : Fragment() {

    lateinit var binding: WebviewFragmentBinding
    private val args: WebViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WebviewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        WebView.setWebContentsDebuggingEnabled(true)

        binding.webView.apply {

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.mediaPlaybackRequiresUserGesture = false

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    consoleMessage?.let {
                        Timber.d("Received console message: ${it.message()}.  Source: ${it.sourceId()}.  LogLevel: ${it.messageLevel().name}")
                    }
                    return true
                }

                override fun onPermissionRequest(request: PermissionRequest?) {
                    // we should be prompting the user for permission, but for testing
                    // we are just auto-granting
                    Timber.d("Granting WebView permissions: ${request?.resources}")
                    request?.grant(request.resources)
                }
            }
            webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Timber.d("Started loading url: $url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Timber.d("Finished loading url: $url")
                }
                
            }

            loadUrl("https://th.frosh.dex.care/visit/${args.visitId}?brand=providence")
        }
    }
}

package com.saikat.downloaderx.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.saikat.downloaderx.R
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

class BrowserFragment : Fragment() {

    private lateinit var viewModel: BrowserViewModel
    private lateinit var geckoView: GeckoView
    private lateinit var urlInput: EditText
    private lateinit var goButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var sniffButton: Button
    private lateinit var extensionsButton: Button
    private lateinit var bookmarksButton: Button
    
    private lateinit var geckoRuntime: GeckoRuntime
    private lateinit var geckoSession: GeckoSession

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(BrowserViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_browser, container, false)

        geckoView = root.findViewById(R.id.gecko_view)
        urlInput = root.findViewById(R.id.url_input)
        goButton = root.findViewById(R.id.go_button)
        progressBar = root.findViewById(R.id.progress_bar)
        sniffButton = root.findViewById(R.id.sniff_button)
        extensionsButton = root.findViewById(R.id.extensions_button)
        bookmarksButton = root.findViewById(R.id.bookmarks_button)

        setupGeckoView()
        setupButtons()
        
        return root
    }
    
    private fun setupGeckoView() {
        // Initialize GeckoRuntime if not already initialized
        if (!::geckoRuntime.isInitialized) {
            geckoRuntime = GeckoRuntime.create(requireContext())
        }
        
        // Create and configure GeckoSession
        geckoSession = GeckoSession()
        geckoSession.open(geckoRuntime)
        
        // Set progress delegate to update progress bar
        geckoSession.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onPageStart(session: GeckoSession, url: String) {
                progressBar.visibility = View.VISIBLE
                urlInput.setText(url)
            }

            override fun onPageStop(session: GeckoSession, success: Boolean) {
                progressBar.visibility = View.GONE
            }

            override fun onProgressChange(session: GeckoSession, progress: Int) {
                progressBar.progress = progress
            }
        }
        
        // Set content delegate to detect media streams
        geckoSession.contentDelegate = object : GeckoSession.ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                // Update title if needed
            }

            override fun onCloseRequest(session: GeckoSession) {
                // Handle close request
            }

            override fun onFullScreen(session: GeckoSession, fullScreen: Boolean) {
                // Handle fullscreen changes
            }
        }
        
        // Attach the session to the view
        geckoView.setSession(geckoSession)
        
        // Load default homepage
        geckoSession.loadUri("https://www.google.com")
    }
    
    private fun setupButtons() {
        goButton.setOnClickListener {
            val url = urlInput.text.toString()
            if (url.isNotEmpty()) {
                loadUrl(url)
            }
        }
        
        sniffButton.setOnClickListener {
            // Start media sniffing
            viewModel.startMediaSniffing(geckoSession)
        }
        
        extensionsButton.setOnClickListener {
            // Show extensions dialog
            ExtensionsDialogFragment().show(childFragmentManager, "extensions")
        }
        
        bookmarksButton.setOnClickListener {
            // Show bookmarks dialog
            BookmarksDialogFragment().show(childFragmentManager, "bookmarks")
        }
    }
    
    private fun loadUrl(url: String) {
        var processedUrl = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            processedUrl = "https://$url"
        }
        geckoSession.loadUri(processedUrl)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        geckoSession.close()
    }
}
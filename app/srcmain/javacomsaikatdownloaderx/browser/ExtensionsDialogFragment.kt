package com.saikat.downloaderx.browser

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.saikat.downloaderx.R
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.WebExtension
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExtensionsDialogFragment : DialogFragment() {

    private lateinit var viewModel: BrowserViewModel
    private lateinit var extensionsList: ListView
    private lateinit var installButton: Button
    
    private val extensionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                installExtension(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_extensions, container, false)
        
        viewModel = ViewModelProvider(requireParentFragment()).get(BrowserViewModel::class.java)
        
        extensionsList = root.findViewById(R.id.extensions_list)
        installButton = root.findViewById(R.id.install_extension_button)
        
        installButton.setOnClickListener {
            openExtensionPicker()
        }
        
        updateExtensionsList()
        
        return root
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Firefox Extensions")
        return dialog
    }
    
    private fun updateExtensionsList() {
        val extensions = viewModel.installedExtensions.value ?: emptyList()
        
        val data = extensions.map { extension ->
            mapOf(
                "name" to (extension.metaData.name ?: "Unknown Extension"),
                "version" to (extension.metaData.version ?: "Unknown Version"),
                "description" to (extension.metaData.description ?: "No description")
            )
        }
        
        val adapter = SimpleAdapter(
            context,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf("name", "version"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        
        extensionsList.adapter = adapter
    }
    
    private fun openExtensionPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        extensionLauncher.launch(intent)
    }
    
    private fun installExtension(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File(requireContext().cacheDir, "temp_extension.xpi")
            
            FileOutputStream(tempFile).use { output ->
                inputStream?.copyTo(output)
            }
            
            val runtime = GeckoRuntime.getDefault(requireContext())
            runtime.webExtensionController.install(tempFile.absolutePath)
                .accept { extension ->
                    viewModel.addExtension(extension)
                    updateExtensionsList()
                    Toast.makeText(context, "Extension installed", Toast.LENGTH_SHORT).show()
                }
                .exceptionally { throwable ->
                    Toast.makeText(context, "Failed to install extension: ${throwable.message}", Toast.LENGTH_SHORT).show()
                    null
                }
            
        } catch (e: IOException) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
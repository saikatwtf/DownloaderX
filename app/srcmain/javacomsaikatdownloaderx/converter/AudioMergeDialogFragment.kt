package com.saikat.downloaderx.converter

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.saikat.downloaderx.R

class AudioMergeDialogFragment : DialogFragment() {

    private lateinit var viewModel: ConverterViewModel
    private lateinit var audiosList: ListView
    private lateinit var addAudioButton: Button
    private lateinit var mergeButton: Button
    
    private val selectedAudios = mutableListOf<Uri>()
    private lateinit var adapter: ArrayAdapter<String>
    
    private val audioLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedAudios.add(uri)
                adapter.add(uri.lastPathSegment)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_audio_merge, container, false)
        
        viewModel = ViewModelProvider(requireParentFragment()).get(ConverterViewModel::class.java)
        
        audiosList = root.findViewById(R.id.audios_list)
        addAudioButton = root.findViewById(R.id.add_audio_button)
        mergeButton = root.findViewById(R.id.merge_button)
        
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        audiosList.adapter = adapter
        
        addAudioButton.setOnClickListener {
            openAudioPicker()
        }
        
        mergeButton.setOnClickListener {
            mergeAudios()
        }
        
        return root
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Merge Audio Files")
        return dialog
    }
    
    private fun openAudioPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "audio/*"
        audioLauncher.launch(intent)
    }
    
    private fun mergeAudios() {
        if (selectedAudios.isEmpty()) {
            Toast.makeText(context, "Please add at least one audio file", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewModel.mergeAudios(requireContext(), selectedAudios)
        Toast.makeText(context, "Audio merging started", Toast.LENGTH_SHORT).show()
        dismiss()
    }
}
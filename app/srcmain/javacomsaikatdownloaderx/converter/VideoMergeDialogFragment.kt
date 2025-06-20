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

class VideoMergeDialogFragment : DialogFragment() {

    private lateinit var viewModel: ConverterViewModel
    private lateinit var videosList: ListView
    private lateinit var addVideoButton: Button
    private lateinit var mergeButton: Button
    
    private val selectedVideos = mutableListOf<Uri>()
    private lateinit var adapter: ArrayAdapter<String>
    
    private val videoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedVideos.add(uri)
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
        val root = inflater.inflate(R.layout.dialog_video_merge, container, false)
        
        viewModel = ViewModelProvider(requireParentFragment()).get(ConverterViewModel::class.java)
        
        videosList = root.findViewById(R.id.videos_list)
        addVideoButton = root.findViewById(R.id.add_video_button)
        mergeButton = root.findViewById(R.id.merge_button)
        
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        videosList.adapter = adapter
        
        addVideoButton.setOnClickListener {
            openVideoPicker()
        }
        
        mergeButton.setOnClickListener {
            mergeVideos()
        }
        
        return root
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Merge Videos")
        return dialog
    }
    
    private fun openVideoPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "video/*"
        videoLauncher.launch(intent)
    }
    
    private fun mergeVideos() {
        if (selectedVideos.isEmpty()) {
            Toast.makeText(context, "Please add at least one video", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewModel.mergeVideos(requireContext(), selectedVideos)
        Toast.makeText(context, "Video merging started", Toast.LENGTH_SHORT).show()
        dismiss()
    }
}
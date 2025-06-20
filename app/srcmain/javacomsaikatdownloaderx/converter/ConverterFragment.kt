package com.saikat.downloaderx.converter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.saikat.downloaderx.R

class ConverterFragment : Fragment() {

    private lateinit var viewModel: ConverterViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var videoLayout: View
    private lateinit var audioLayout: View
    private lateinit var imageLayout: View
    
    // Video Editor
    private lateinit var videoInputText: TextView
    private lateinit var videoSelectButton: Button
    private lateinit var videoTrimButton: Button
    private lateinit var videoMergeButton: Button
    private lateinit var videoConvertButton: Button
    private lateinit var extractAudioButton: Button
    
    // Audio Editor
    private lateinit var audioInputText: TextView
    private lateinit var audioSelectButton: Button
    private lateinit var audioTrimButton: Button
    private lateinit var audioMergeButton: Button
    
    // Image Converter
    private lateinit var imageInputText: TextView
    private lateinit var imageSelectButton: Button
    private lateinit var heicToJpgButton: Button
    
    private val videoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.setSelectedVideoUri(uri)
                videoInputText.text = uri.lastPathSegment
            }
        }
    }
    
    private val audioLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.setSelectedAudioUri(uri)
                audioInputText.text = uri.lastPathSegment
            }
        }
    }
    
    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.setSelectedImageUri(uri)
                imageInputText.text = uri.lastPathSegment
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ConverterViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_converter, container, false)

        initViews(root)
        setupTabLayout()
        setupListeners()
        
        return root
    }
    
    private fun initViews(root: View) {
        tabLayout = root.findViewById(R.id.tab_layout)
        videoLayout = root.findViewById(R.id.video_editor_layout)
        audioLayout = root.findViewById(R.id.audio_editor_layout)
        imageLayout = root.findViewById(R.id.image_converter_layout)
        
        // Video Editor
        videoInputText = root.findViewById(R.id.video_input_text)
        videoSelectButton = root.findViewById(R.id.video_select_button)
        videoTrimButton = root.findViewById(R.id.video_trim_button)
        videoMergeButton = root.findViewById(R.id.video_merge_button)
        videoConvertButton = root.findViewById(R.id.video_convert_button)
        extractAudioButton = root.findViewById(R.id.extract_audio_button)
        
        // Audio Editor
        audioInputText = root.findViewById(R.id.audio_input_text)
        audioSelectButton = root.findViewById(R.id.audio_select_button)
        audioTrimButton = root.findViewById(R.id.audio_trim_button)
        audioMergeButton = root.findViewById(R.id.audio_merge_button)
        
        // Image Converter
        imageInputText = root.findViewById(R.id.image_input_text)
        imageSelectButton = root.findViewById(R.id.image_select_button)
        heicToJpgButton = root.findViewById(R.id.heic_to_jpg_button)
    }
    
    private fun setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.video_editor))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.audio_editor))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.image_converter))
        
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        videoLayout.visibility = View.VISIBLE
                        audioLayout.visibility = View.GONE
                        imageLayout.visibility = View.GONE
                    }
                    1 -> {
                        videoLayout.visibility = View.GONE
                        audioLayout.visibility = View.VISIBLE
                        imageLayout.visibility = View.GONE
                    }
                    2 -> {
                        videoLayout.visibility = View.GONE
                        audioLayout.visibility = View.GONE
                        imageLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        // Show video editor by default
        videoLayout.visibility = View.VISIBLE
        audioLayout.visibility = View.GONE
        imageLayout.visibility = View.GONE
    }
    
    private fun setupListeners() {
        // Video Editor
        videoSelectButton.setOnClickListener {
            openFilePicker("video/*", videoLauncher)
        }
        
        videoTrimButton.setOnClickListener {
            if (viewModel.selectedVideoUri.value != null) {
                showVideoTrimDialog()
            } else {
                Toast.makeText(context, "Please select a video first", Toast.LENGTH_SHORT).show()
            }
        }
        
        videoMergeButton.setOnClickListener {
            showVideoMergeDialog()
        }
        
        videoConvertButton.setOnClickListener {
            if (viewModel.selectedVideoUri.value != null) {
                showVideoConvertDialog()
            } else {
                Toast.makeText(context, "Please select a video first", Toast.LENGTH_SHORT).show()
            }
        }
        
        extractAudioButton.setOnClickListener {
            if (viewModel.selectedVideoUri.value != null) {
                viewModel.extractAudioFromVideo(requireContext())
                Toast.makeText(context, "Audio extraction started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please select a video first", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Audio Editor
        audioSelectButton.setOnClickListener {
            openFilePicker("audio/*", audioLauncher)
        }
        
        audioTrimButton.setOnClickListener {
            if (viewModel.selectedAudioUri.value != null) {
                showAudioTrimDialog()
            } else {
                Toast.makeText(context, "Please select an audio file first", Toast.LENGTH_SHORT).show()
            }
        }
        
        audioMergeButton.setOnClickListener {
            showAudioMergeDialog()
        }
        
        // Image Converter
        imageSelectButton.setOnClickListener {
            openFilePicker("image/*", imageLauncher)
        }
        
        heicToJpgButton.setOnClickListener {
            if (viewModel.selectedImageUri.value != null) {
                viewModel.convertHeicToJpg(requireContext())
                Toast.makeText(context, "HEIC to JPG conversion started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun openFilePicker(mimeType: String, launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = mimeType
        launcher.launch(intent)
    }
    
    private fun showVideoTrimDialog() {
        VideoTrimDialogFragment().show(childFragmentManager, "videoTrim")
    }
    
    private fun showVideoMergeDialog() {
        VideoMergeDialogFragment().show(childFragmentManager, "videoMerge")
    }
    
    private fun showVideoConvertDialog() {
        VideoConvertDialogFragment().show(childFragmentManager, "videoConvert")
    }
    
    private fun showAudioTrimDialog() {
        AudioTrimDialogFragment().show(childFragmentManager, "audioTrim")
    }
    
    private fun showAudioMergeDialog() {
        AudioMergeDialogFragment().show(childFragmentManager, "audioMerge")
    }
}
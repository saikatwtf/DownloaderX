package com.saikat.downloaderx.converter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.saikat.downloaderx.R

class VideoConvertDialogFragment : DialogFragment() {

    private lateinit var viewModel: ConverterViewModel
    private lateinit var formatGroup: RadioGroup
    private lateinit var mp4Radio: RadioButton
    private lateinit var mkvRadio: RadioButton
    private lateinit var convertButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_video_convert, container, false)
        
        viewModel = ViewModelProvider(requireParentFragment()).get(ConverterViewModel::class.java)
        
        formatGroup = root.findViewById(R.id.format_group)
        mp4Radio = root.findViewById(R.id.mp4_radio)
        mkvRadio = root.findViewById(R.id.mkv_radio)
        convertButton = root.findViewById(R.id.convert_button)
        
        convertButton.setOnClickListener {
            convertVideo()
        }
        
        return root
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Convert Video Format")
        return dialog
    }
    
    private fun convertVideo() {
        val format = if (mp4Radio.isChecked) "mp4" else "mkv"
        
        viewModel.convertVideoFormat(requireContext(), format)
        Toast.makeText(context, "Video conversion started", Toast.LENGTH_SHORT).show()
        dismiss()
    }
}
package com.saikat.downloaderx.converter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.saikat.downloaderx.R

class AudioTrimDialogFragment : DialogFragment() {

    private lateinit var viewModel: ConverterViewModel
    private lateinit var startTimeInput: EditText
    private lateinit var endTimeInput: EditText
    private lateinit var trimButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_audio_trim, container, false)
        
        viewModel = ViewModelProvider(requireParentFragment()).get(ConverterViewModel::class.java)
        
        startTimeInput = root.findViewById(R.id.start_time_input)
        endTimeInput = root.findViewById(R.id.end_time_input)
        trimButton = root.findViewById(R.id.trim_button)
        
        trimButton.setOnClickListener {
            trimAudio()
        }
        
        return root
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Trim Audio")
        return dialog
    }
    
    private fun trimAudio() {
        val startTime = startTimeInput.text.toString().trim()
        val endTime = endTimeInput.text.toString().trim()
        
        if (startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(context, "Please enter both start and end times", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewModel.trimAudio(requireContext(), startTime, endTime)
        Toast.makeText(context, "Audio trimming started", Toast.LENGTH_SHORT).show()
        dismiss()
    }
}
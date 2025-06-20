package com.saikat.downloaderx.systemtools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.saikat.downloaderx.R

class SystemToolsFragment : Fragment() {

    private lateinit var viewModel: SystemToolsViewModel
    
    // Junk Cleaner
    private lateinit var junkCleanerButton: Button
    private lateinit var junkCleanerProgress: ProgressBar
    private lateinit var junkCleanerStatus: TextView
    
    // Phone Booster
    private lateinit var phoneBoosterButton: Button
    private lateinit var phoneBoosterProgress: ProgressBar
    private lateinit var phoneBoosterStatus: TextView
    
    // Photo Cleaner
    private lateinit var photoCleanerButton: Button
    private lateinit var photoCleanerProgress: ProgressBar
    private lateinit var photoCleanerStatus: TextView
    
    // Battery Saver
    private lateinit var batterySaverButton: Button
    private lateinit var batterySaverStatus: TextView
    
    // File Recovery
    private lateinit var fileRecoveryButton: Button
    private lateinit var fileRecoveryProgress: ProgressBar
    private lateinit var fileRecoveryStatus: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SystemToolsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_system_tools, container, false)

        initViews(root)
        setupObservers()
        setupListeners()
        
        return root
    }
    
    private fun initViews(root: View) {
        // Junk Cleaner
        junkCleanerButton = root.findViewById(R.id.junk_cleaner_button)
        junkCleanerProgress = root.findViewById(R.id.junk_cleaner_progress)
        junkCleanerStatus = root.findViewById(R.id.junk_cleaner_status)
        
        // Phone Booster
        phoneBoosterButton = root.findViewById(R.id.phone_booster_button)
        phoneBoosterProgress = root.findViewById(R.id.phone_booster_progress)
        phoneBoosterStatus = root.findViewById(R.id.phone_booster_status)
        
        // Photo Cleaner
        photoCleanerButton = root.findViewById(R.id.photo_cleaner_button)
        photoCleanerProgress = root.findViewById(R.id.photo_cleaner_progress)
        photoCleanerStatus = root.findViewById(R.id.photo_cleaner_status)
        
        // Battery Saver
        batterySaverButton = root.findViewById(R.id.battery_saver_button)
        batterySaverStatus = root.findViewById(R.id.battery_saver_status)
        
        // File Recovery
        fileRecoveryButton = root.findViewById(R.id.file_recovery_button)
        fileRecoveryProgress = root.findViewById(R.id.file_recovery_progress)
        fileRecoveryStatus = root.findViewById(R.id.file_recovery_status)
    }
    
    private fun setupObservers() {
        // Junk Cleaner
        viewModel.junkCleanerStatus.observe(viewLifecycleOwner) { status ->
            junkCleanerStatus.text = status
        }
        
        viewModel.junkCleanerProgress.observe(viewLifecycleOwner) { progress ->
            junkCleanerProgress.progress = progress
            junkCleanerProgress.visibility = if (progress < 100) View.VISIBLE else View.GONE
        }
        
        // Phone Booster
        viewModel.phoneBoosterStatus.observe(viewLifecycleOwner) { status ->
            phoneBoosterStatus.text = status
        }
        
        viewModel.phoneBoosterProgress.observe(viewLifecycleOwner) { progress ->
            phoneBoosterProgress.progress = progress
            phoneBoosterProgress.visibility = if (progress < 100) View.VISIBLE else View.GONE
        }
        
        // Photo Cleaner
        viewModel.photoCleanerStatus.observe(viewLifecycleOwner) { status ->
            photoCleanerStatus.text = status
        }
        
        viewModel.photoCleanerProgress.observe(viewLifecycleOwner) { progress ->
            photoCleanerProgress.progress = progress
            photoCleanerProgress.visibility = if (progress < 100) View.VISIBLE else View.GONE
        }
        
        // Battery Saver
        viewModel.batterySaverStatus.observe(viewLifecycleOwner) { status ->
            batterySaverStatus.text = status
            batterySaverButton.text = if (viewModel.isBatterySaverEnabled.value == true) 
                "Disable Battery Saver" else "Enable Battery Saver"
        }
        
        // File Recovery
        viewModel.fileRecoveryStatus.observe(viewLifecycleOwner) { status ->
            fileRecoveryStatus.text = status
        }
        
        viewModel.fileRecoveryProgress.observe(viewLifecycleOwner) { progress ->
            fileRecoveryProgress.progress = progress
            fileRecoveryProgress.visibility = if (progress < 100) View.VISIBLE else View.GONE
        }
    }
    
    private fun setupListeners() {
        junkCleanerButton.setOnClickListener {
            if (viewModel.isJunkCleanerRunning.value != true) {
                viewModel.startJunkCleaner()
                Toast.makeText(context, "Junk cleaning started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Junk cleaner is already running", Toast.LENGTH_SHORT).show()
            }
        }
        
        phoneBoosterButton.setOnClickListener {
            if (viewModel.isPhoneBoosterRunning.value != true) {
                viewModel.startPhoneBooster()
                Toast.makeText(context, "Phone boosting started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Phone booster is already running", Toast.LENGTH_SHORT).show()
            }
        }
        
        photoCleanerButton.setOnClickListener {
            if (viewModel.isPhotoCleanerRunning.value != true) {
                viewModel.startPhotoCleaner()
                Toast.makeText(context, "Photo cleaning started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Photo cleaner is already running", Toast.LENGTH_SHORT).show()
            }
        }
        
        batterySaverButton.setOnClickListener {
            val isEnabled = viewModel.isBatterySaverEnabled.value ?: false
            if (isEnabled) {
                viewModel.disableBatterySaver()
                Toast.makeText(context, "Battery saver disabled", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.enableBatterySaver()
                Toast.makeText(context, "Battery saver enabled", Toast.LENGTH_SHORT).show()
            }
        }
        
        fileRecoveryButton.setOnClickListener {
            if (viewModel.isFileRecoveryRunning.value != true) {
                viewModel.startFileRecovery()
                Toast.makeText(context, "File recovery started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "File recovery is already running", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
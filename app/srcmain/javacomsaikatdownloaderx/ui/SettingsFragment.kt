package com.saikat.downloaderx.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.saikat.downloaderx.R
import com.saikat.downloaderx.utils.ThemeManager
import java.io.File

class SettingsFragment : Fragment() {

    private lateinit var themeRadioGroup: RadioGroup
    private lateinit var lightThemeRadio: RadioButton
    private lateinit var darkThemeRadio: RadioButton
    private lateinit var amoledThemeRadio: RadioButton
    private lateinit var retroGreenThemeRadio: RadioButton
    private lateinit var hackerNeonThemeRadio: RadioButton
    
    private lateinit var ffmpegPathText: TextView
    private lateinit var mp4decryptPathText: TextView
    private lateinit var ytdlpPathText: TextView
    private lateinit var m3u8dlPathText: TextView
    private lateinit var downloadFolderText: TextView
    
    private lateinit var ffmpegSelectButton: Button
    private lateinit var mp4decryptSelectButton: Button
    private lateinit var ytdlpSelectButton: Button
    private lateinit var m3u8dlSelectButton: Button
    private lateinit var downloadFolderSelectButton: Button
    
    private lateinit var screamingSwitch: Switch
    
    private val ffmpegLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val path = uri.path
                if (path != null) {
                    val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("ffmpeg_path", path).apply()
                    ffmpegPathText.text = path
                }
            }
        }
    }
    
    private val mp4decryptLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val path = uri.path
                if (path != null) {
                    val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("mp4decrypt_path", path).apply()
                    mp4decryptPathText.text = path
                }
            }
        }
    }
    
    private val ytdlpLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val path = uri.path
                if (path != null) {
                    val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("yt_dlp_path", path).apply()
                    ytdlpPathText.text = path
                }
            }
        }
    }
    
    private val m3u8dlLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val path = uri.path
                if (path != null) {
                    val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("n_m3u8dl_re_path", path).apply()
                    m3u8dlPathText.text = path
                }
            }
        }
    }
    
    private val downloadFolderLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val path = uri.path
                if (path != null) {
                    val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("download_folder", path).apply()
                    downloadFolderText.text = path
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        initViews(root)
        loadSettings()
        setupListeners()
        
        return root
    }
    
    private fun initViews(root: View) {
        themeRadioGroup = root.findViewById(R.id.theme_radio_group)
        lightThemeRadio = root.findViewById(R.id.light_theme_radio)
        darkThemeRadio = root.findViewById(R.id.dark_theme_radio)
        amoledThemeRadio = root.findViewById(R.id.amoled_theme_radio)
        retroGreenThemeRadio = root.findViewById(R.id.retro_green_theme_radio)
        hackerNeonThemeRadio = root.findViewById(R.id.hacker_neon_theme_radio)
        
        ffmpegPathText = root.findViewById(R.id.ffmpeg_path_text)
        mp4decryptPathText = root.findViewById(R.id.mp4decrypt_path_text)
        ytdlpPathText = root.findViewById(R.id.ytdlp_path_text)
        m3u8dlPathText = root.findViewById(R.id.m3u8dl_path_text)
        downloadFolderText = root.findViewById(R.id.download_folder_text)
        
        ffmpegSelectButton = root.findViewById(R.id.ffmpeg_select_button)
        mp4decryptSelectButton = root.findViewById(R.id.mp4decrypt_select_button)
        ytdlpSelectButton = root.findViewById(R.id.ytdlp_select_button)
        m3u8dlSelectButton = root.findViewById(R.id.m3u8dl_select_button)
        downloadFolderSelectButton = root.findViewById(R.id.download_folder_select_button)
        
        screamingSwitch = root.findViewById(R.id.screaming_switch)
    }
    
    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        // Load theme setting
        val theme = prefs.getString("selected_theme", ThemeManager.THEME_DARK)
        when (theme) {
            ThemeManager.THEME_LIGHT -> lightThemeRadio.isChecked = true
            ThemeManager.THEME_DARK -> darkThemeRadio.isChecked = true
            ThemeManager.THEME_AMOLED -> amoledThemeRadio.isChecked = true
            ThemeManager.THEME_RETRO_GREEN -> retroGreenThemeRadio.isChecked = true
            ThemeManager.THEME_HACKER_NEON -> hackerNeonThemeRadio.isChecked = true
        }
        
        // Load binary paths
        val ffmpegPath = prefs.getString("ffmpeg_path", "${requireContext().filesDir}/binaries/ffmpeg")
        val mp4decryptPath = prefs.getString("mp4decrypt_path", "${requireContext().filesDir}/binaries/mp4decrypt")
        val ytdlpPath = prefs.getString("yt_dlp_path", "${requireContext().filesDir}/binaries/yt-dlp")
        val m3u8dlPath = prefs.getString("n_m3u8dl_re_path", "${requireContext().filesDir}/binaries/N_m3u8DL-RE")
        val downloadFolder = prefs.getString("download_folder", null)
        
        ffmpegPathText.text = ffmpegPath
        mp4decryptPathText.text = mp4decryptPath
        ytdlpPathText.text = ytdlpPath
        m3u8dlPathText.text = m3u8dlPath
        downloadFolderText.text = downloadFolder ?: "Default"
        
        // Load screaming notification setting
        screamingSwitch.isChecked = prefs.getBoolean("screaming_notification", false)
    }
    
    private fun setupListeners() {
        themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                R.id.light_theme_radio -> ThemeManager.THEME_LIGHT
                R.id.dark_theme_radio -> ThemeManager.THEME_DARK
                R.id.amoled_theme_radio -> ThemeManager.THEME_AMOLED
                R.id.retro_green_theme_radio -> ThemeManager.THEME_RETRO_GREEN
                R.id.hacker_neon_theme_radio -> ThemeManager.THEME_HACKER_NEON
                else -> ThemeManager.THEME_DARK
            }
            
            val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("selected_theme", theme).apply()
            
            ThemeManager.setTheme(requireContext(), theme)
            requireActivity().recreate()
        }
        
        ffmpegSelectButton.setOnClickListener {
            openFilePicker(ffmpegLauncher)
        }
        
        mp4decryptSelectButton.setOnClickListener {
            openFilePicker(mp4decryptLauncher)
        }
        
        ytdlpSelectButton.setOnClickListener {
            openFilePicker(ytdlpLauncher)
        }
        
        m3u8dlSelectButton.setOnClickListener {
            openFilePicker(m3u8dlLauncher)
        }
        
        downloadFolderSelectButton.setOnClickListener {
            openFolderPicker(downloadFolderLauncher)
        }
        
        screamingSwitch.setOnCheckedChangeListener { _, isChecked ->
            val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("screaming_notification", isChecked).apply()
            
            if (isChecked) {
                Toast.makeText(context, "Screaming notification enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun openFilePicker(launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        launcher.launch(intent)
    }
    
    private fun openFolderPicker(launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        launcher.launch(intent)
    }
}
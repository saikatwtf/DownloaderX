package com.saikat.downloaderx

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.saikat.downloaderx.utils.ThemeManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DownloaderXApplication : Application() {
    
    companion object {
        lateinit var instance: DownloaderXApplication
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize theme
        ThemeManager.applyTheme(this)
        
        // Initialize binary paths
        initializeBinaries()
    }
    
    private fun initializeBinaries() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val assetsDir = File(filesDir, "binaries")
        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }
        
        // Extract binaries from assets if needed
        extractBinaryIfNeeded("ffmpeg", assetsDir)
        extractBinaryIfNeeded("mp4decrypt", assetsDir)
        extractBinaryIfNeeded("yt-dlp", assetsDir)
        extractBinaryIfNeeded("N_m3u8DL-RE", assetsDir)
        
        // Set default binary paths if not set
        if (!prefs.contains("ffmpeg_path")) {
            prefs.edit().putString("ffmpeg_path", "${assetsDir.absolutePath}/ffmpeg").apply()
        }
        if (!prefs.contains("mp4decrypt_path")) {
            prefs.edit().putString("mp4decrypt_path", "${assetsDir.absolutePath}/mp4decrypt").apply()
        }
        if (!prefs.contains("yt_dlp_path")) {
            prefs.edit().putString("yt_dlp_path", "${assetsDir.absolutePath}/yt-dlp").apply()
        }
        if (!prefs.contains("n_m3u8dl_re_path")) {
            prefs.edit().putString("n_m3u8dl_re_path", "${assetsDir.absolutePath}/N_m3u8DL-RE").apply()
        }
    }
    
    private fun extractBinaryIfNeeded(binaryName: String, targetDir: File) {
        try {
            val targetFile = File(targetDir, binaryName)
            if (!targetFile.exists()) {
                assets.open("binaries/$binaryName").use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                // Make binary executable
                targetFile.setExecutable(true)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
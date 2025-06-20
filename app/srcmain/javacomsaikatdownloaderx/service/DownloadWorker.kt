package com.saikat.downloaderx.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.saikat.downloaderx.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class DownloadWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationId = 1
    private val channelId = "download_channel"
    private val channelName = "Download Notifications"

    override suspend fun doWork(): Result {
        // Create notification channel for Android O and above
        createNotificationChannel()
        
        // Start foreground service with notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("DownloaderX")
            .setContentText("Download in progress...")
            .setSmallIcon(R.drawable.ic_download)
            .setProgress(0, 0, true)
            .build()
            
        setForeground(ForegroundInfo(notificationId, notification))
        
        // Get input data
        val url = inputData.getString("url") ?: return Result.failure()
        val keys = inputData.getString("keys") ?: ""
        val format = inputData.getString("format") ?: "mkv"
        val subtitles = inputData.getBoolean("subtitles", false)
        val logging = inputData.getBoolean("logging", false)
        val retry = inputData.getBoolean("retry", true)
        
        // Get download directory
        val downloadDir = getDownloadDirectory()
        
        try {
            // Determine which tool to use based on URL
            val result = when {
                url.contains("youtube.com") || url.contains("youtu.be") -> {
                    downloadWithYtDlp(url, downloadDir, format, subtitles, logging)
                }
                url.endsWith(".m3u8") || url.contains("m3u8") -> {
                    downloadWithM3u8dlRe(url, keys, downloadDir, format, subtitles, logging, retry)
                }
                url.endsWith(".mpd") || url.contains("mpd") -> {
                    downloadWithM3u8dlRe(url, keys, downloadDir, format, subtitles, logging, retry)
                }
                else -> {
                    downloadWithYtDlp(url, downloadDir, format, subtitles, logging)
                }
            }
            
            // Update notification
            val completionNotification = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Download Complete")
                .setContentText("File saved to Downloads folder")
                .setSmallIcon(R.drawable.ic_download)
                .build()
                
            notificationManager.notify(notificationId, completionNotification)
            
            // Play completion sound if enabled
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val playSound = prefs.getBoolean("screaming_notification", false)
            if (playSound) {
                playScreamSound()
            }
            
            return if (result) Result.success() else Result.failure()
        } catch (e: Exception) {
            e.printStackTrace()
            
            // Update notification for failure
            val failureNotification = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Download Failed")
                .setContentText("Error: ${e.message}")
                .setSmallIcon(R.drawable.ic_download)
                .build()
                
            notificationManager.notify(notificationId, failureNotification)
            
            return Result.failure()
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun getDownloadDirectory(): File {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val customPath = prefs.getString("download_folder", null)
        
        return if (customPath != null) {
            File(customPath)
        } else {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "DownloaderX")
        }.apply { 
            if (!exists()) mkdirs() 
        }
    }
    
    private suspend fun downloadWithYtDlp(
        url: String,
        outputDir: File,
        format: String,
        subtitles: Boolean,
        logging: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val ytDlpPath = prefs.getString("yt_dlp_path", "${context.filesDir}/binaries/yt-dlp") ?: "${context.filesDir}/binaries/yt-dlp"
            
            val ytDlpFile = File(ytDlpPath)
            if (!ytDlpFile.exists()) {
                return@withContext false
            }
            
            val command = StringBuilder()
            command.append("$ytDlpPath ")
            command.append("\"$url\" ")
            command.append("-o \"${outputDir.absolutePath}/%(title)s.%(ext)s\" ")
            
            if (format == "mp4") {
                command.append("-f \"bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best\" ")
            }
            
            if (subtitles) {
                command.append("--write-subs --write-auto-subs ")
            }
            
            val logFile = if (logging) {
                File(outputDir, "yt-dlp-log.txt").also {
                    command.append("--verbose ")
                }
            } else null
            
            val process = ProcessBuilder()
                .command("sh", "-c", command.toString())
                .redirectErrorStream(true)
                .start()
                
            val exitCode = process.waitFor()
            
            return@withContext exitCode == 0
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    private suspend fun downloadWithM3u8dlRe(
        url: String,
        keys: String,
        outputDir: File,
        format: String,
        subtitles: Boolean,
        logging: Boolean,
        retry: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val m3u8dlPath = prefs.getString("n_m3u8dl_re_path", "${context.filesDir}/binaries/N_m3u8DL-RE") 
                ?: "${context.filesDir}/binaries/N_m3u8DL-RE"
            
            val m3u8dlFile = File(m3u8dlPath)
            if (!m3u8dlFile.exists()) {
                return@withContext false
            }
            
            val command = StringBuilder()
            command.append("$m3u8dlPath ")
            command.append("\"$url\" ")
            command.append("--save-dir \"${outputDir.absolutePath}\" ")
            command.append("--tmp-dir \"${context.cacheDir.absolutePath}\" ")
            
            if (keys.isNotEmpty()) {
                val keyParts = keys.split(":")
                if (keyParts.size == 2) {
                    command.append("--key ${keyParts[0]} --iv ${keyParts[1]} ")
                }
            }
            
            if (format == "mp4") {
                command.append("--mux-after-done --use-mp4box ")
            } else {
                command.append("--mux-after-done ")
            }
            
            if (subtitles) {
                command.append("--save-subs ")
            }
            
            if (retry) {
                command.append("--auto-retry ")
            }
            
            if (logging) {
                command.append("--log-level debug ")
            }
            
            val process = ProcessBuilder()
                .command("sh", "-c", command.toString())
                .redirectErrorStream(true)
                .start()
                
            val exitCode = process.waitFor()
            
            // If failed and retry is enabled, try with fallback method
            if (exitCode != 0 && retry) {
                return@withContext downloadWithFallback(url, keys, outputDir, format, subtitles, logging)
            }
            
            return@withContext exitCode == 0
        } catch (e: Exception) {
            e.printStackTrace()
            if (retry) {
                return@withContext downloadWithFallback(url, keys, outputDir, format, subtitles, logging)
            }
            return@withContext false
        }
    }
    
    private suspend fun downloadWithFallback(
        url: String,
        keys: String,
        outputDir: File,
        format: String,
        subtitles: Boolean,
        logging: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val ffmpegPath = prefs.getString("ffmpeg_path", "${context.filesDir}/binaries/ffmpeg") 
                ?: "${context.filesDir}/binaries/ffmpeg"
            
            val ffmpegFile = File(ffmpegPath)
            if (!ffmpegFile.exists()) {
                return@withContext false
            }
            
            val outputFile = File(outputDir, "fallback_download.${format}")
            
            val command = StringBuilder()
            command.append("$ffmpegPath ")
            command.append("-i \"$url\" ")
            
            if (keys.isNotEmpty()) {
                command.append("-decryption_key ${keys.split(":")[0]} ")
            }
            
            if (subtitles) {
                command.append("-scodec copy ")
            }
            
            command.append("-c copy \"${outputFile.absolutePath}\" ")
            
            if (logging) {
                command.append("-v debug ")
            } else {
                command.append("-v quiet ")
            }
            
            val process = ProcessBuilder()
                .command("sh", "-c", command.toString())
                .redirectErrorStream(true)
                .start()
                
            val exitCode = process.waitFor()
            
            return@withContext exitCode == 0
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    private fun playScreamSound() {
        try {
            val mediaPlayer = MediaPlayer.create(context, R.raw.scream)
            mediaPlayer?.setOnCompletionListener { it.release() }
            mediaPlayer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
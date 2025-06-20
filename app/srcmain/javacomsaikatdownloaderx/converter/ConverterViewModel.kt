package com.saikat.downloaderx.converter

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ConverterViewModel : ViewModel() {

    // Video Editor
    private val _selectedVideoUri = MutableLiveData<Uri>()
    val selectedVideoUri: LiveData<Uri> = _selectedVideoUri
    
    // Audio Editor
    private val _selectedAudioUri = MutableLiveData<Uri>()
    val selectedAudioUri: LiveData<Uri> = _selectedAudioUri
    
    // Image Converter
    private val _selectedImageUri = MutableLiveData<Uri>()
    val selectedImageUri: LiveData<Uri> = _selectedImageUri
    
    fun setSelectedVideoUri(uri: Uri) {
        _selectedVideoUri.value = uri
    }
    
    fun setSelectedAudioUri(uri: Uri) {
        _selectedAudioUri.value = uri
    }
    
    fun setSelectedImageUri(uri: Uri) {
        _selectedImageUri.value = uri
    }
    
    fun trimVideo(context: Context, startTime: String, endTime: String) {
        val videoUri = _selectedVideoUri.value ?: return
        
        viewModelScope.launch {
            try {
                val inputPath = getFilePathFromUri(context, videoUri)
                val outputFile = File(context.getExternalFilesDir(null), "trimmed_video.mp4")
                
                val ffmpegPath = getFfmpegPath(context)
                
                val command = "$ffmpegPath -i \"$inputPath\" -ss $startTime -to $endTime -c copy \"${outputFile.absolutePath}\""
                
                executeCommand(command)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun mergeVideos(context: Context, videoUris: List<Uri>) {
        if (videoUris.isEmpty()) return
        
        viewModelScope.launch {
            try {
                val inputListFile = File(context.cacheDir, "input_list.txt")
                FileOutputStream(inputListFile).use { output ->
                    for (uri in videoUris) {
                        val path = getFilePathFromUri(context, uri)
                        output.write("file '$path'\n".toByteArray())
                    }
                }
                
                val outputFile = File(context.getExternalFilesDir(null), "merged_video.mp4")
                val ffmpegPath = getFfmpegPath(context)
                
                val command = "$ffmpegPath -f concat -safe 0 -i \"${inputListFile.absolutePath}\" -c copy \"${outputFile.absolutePath}\""
                
                executeCommand(command)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun convertVideoFormat(context: Context, outputFormat: String) {
        val videoUri = _selectedVideoUri.value ?: return
        
        viewModelScope.launch {
            try {
                val inputPath = getFilePathFromUri(context, videoUri)
                val outputFile = File(context.getExternalFilesDir(null), "converted_video.$outputFormat")
                
                val ffmpegPath = getFfmpegPath(context)
                
                val command = "$ffmpegPath -i \"$inputPath\" -c copy \"${outputFile.absolutePath}\""
                
                executeCommand(command)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun extractAudioFromVideo(context: Context) {
        val videoUri = _selectedVideoUri.value ?: return
        
        viewModelScope.launch {
            try {
                val inputPath = getFilePathFromUri(context, videoUri)
                val outputFile = File(context.getExternalFilesDir(null), "extracted_audio.mp3")
                
                val ffmpegPath = getFfmpegPath(context)
                
                val command = "$ffmpegPath -i \"$inputPath\" -q:a 0 -map a \"${outputFile.absolutePath}\""
                
                executeCommand(command)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun trimAudio(context: Context, startTime: String, endTime: String) {
        val audioUri = _selectedAudioUri.value ?: return
        
        viewModelScope.launch {
            try {
                val inputPath = getFilePathFromUri(context, audioUri)
                val outputFile = File(context.getExternalFilesDir(null), "trimmed_audio.mp3")
                
                val ffmpegPath = getFfmpegPath(context)
                
                val command = "$ffmpegPath -i \"$inputPath\" -ss $startTime -to $endTime -c copy \"${outputFile.absolutePath}\""
                
                executeCommand(command)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun mergeAudios(context: Context, audioUris: List<Uri>) {
        if (audioUris.isEmpty()) return
        
        viewModelScope.launch {
            try {
                val inputListFile = File(context.cacheDir, "input_list.txt")
                FileOutputStream(inputListFile).use { output ->
                    for (uri in audioUris) {
                        val path = getFilePathFromUri(context, uri)
                        output.write("file '$path'\n".toByteArray())
                    }
                }
                
                val outputFile = File(context.getExternalFilesDir(null), "merged_audio.mp3")
                val ffmpegPath = getFfmpegPath(context)
                
                val command = "$ffmpegPath -f concat -safe 0 -i \"${inputListFile.absolutePath}\" -c copy \"${outputFile.absolutePath}\""
                
                executeCommand(command)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun convertHeicToJpg(context: Context) {
        val imageUri = _selectedImageUri.value ?: return
        
        viewModelScope.launch {
            try {
                val inputPath = getFilePathFromUri(context, imageUri)
                val outputFile = File(context.getExternalFilesDir(null), "converted_image.jpg")
                
                val ffmpegPath = getFfmpegPath(context)
                
                val command = "$ffmpegPath -i \"$inputPath\" \"${outputFile.absolutePath}\""
                
                executeCommand(command)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private suspend fun getFilePathFromUri(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val tempFile = File(context.cacheDir, "temp_file")
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return@withContext tempFile.absolutePath
    }
    
    private fun getFfmpegPath(context: Context): String {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("ffmpeg_path", "${context.filesDir}/binaries/ffmpeg") ?: "${context.filesDir}/binaries/ffmpeg"
    }
    
    private suspend fun executeCommand(command: String) = withContext(Dispatchers.IO) {
        try {
            val process = ProcessBuilder()
                .command("sh", "-c", command)
                .redirectErrorStream(true)
                .start()
                
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
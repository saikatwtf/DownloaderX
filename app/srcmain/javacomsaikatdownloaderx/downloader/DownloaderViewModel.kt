package com.saikat.downloaderx.downloader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.UUID

class DownloaderViewModel : ViewModel() {

    private val _downloadJobs = MutableLiveData<Map<UUID, String>>()
    val downloadJobs: LiveData<Map<UUID, String>> = _downloadJobs
    
    private val jobsMap = mutableMapOf<UUID, String>()

    fun addDownloadJob(id: UUID, url: String) {
        jobsMap[id] = url
        _downloadJobs.value = jobsMap.toMap()
    }
    
    fun removeDownloadJob(id: UUID) {
        jobsMap.remove(id)
        _downloadJobs.value = jobsMap.toMap()
    }
}
package com.saikat.downloaderx.downloader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.saikat.downloaderx.R
import com.saikat.downloaderx.service.DownloadWorker
import java.util.UUID

class DownloaderFragment : Fragment() {

    private lateinit var viewModel: DownloaderViewModel
    private lateinit var urlEditText: EditText
    private lateinit var keysEditText: EditText
    private lateinit var formatGroup: RadioGroup
    private lateinit var mkv: RadioButton
    private lateinit var mp4: RadioButton
    private lateinit var includeSubtitles: CheckBox
    private lateinit var enableLogging: CheckBox
    private lateinit var retryFailed: CheckBox
    private lateinit var downloadButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(DownloaderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_downloader, container, false)

        urlEditText = root.findViewById(R.id.url_input)
        keysEditText = root.findViewById(R.id.keys_input)
        formatGroup = root.findViewById(R.id.format_group)
        mkv = root.findViewById(R.id.format_mkv)
        mp4 = root.findViewById(R.id.format_mp4)
        includeSubtitles = root.findViewById(R.id.include_subtitles)
        enableLogging = root.findViewById(R.id.enable_logging)
        retryFailed = root.findViewById(R.id.retry_failed)
        downloadButton = root.findViewById(R.id.download_button)

        downloadButton.setOnClickListener {
            startDownload()
        }

        return root
    }

    private fun startDownload() {
        val url = urlEditText.text.toString().trim()
        if (url.isEmpty()) {
            Toast.makeText(context, "Please enter a URL", Toast.LENGTH_SHORT).show()
            return
        }

        val keys = keysEditText.text.toString().trim()
        val format = if (mkv.isChecked) "mkv" else "mp4"
        val subtitles = includeSubtitles.isChecked
        val logging = enableLogging.isChecked
        val retry = retryFailed.isChecked

        // Create input data for the worker
        val inputData = Data.Builder()
            .putString("url", url)
            .putString("keys", keys)
            .putString("format", format)
            .putBoolean("subtitles", subtitles)
            .putBoolean("logging", logging)
            .putBoolean("retry", retry)
            .build()

        // Create and enqueue the download work request
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(requireContext()).enqueue(downloadWorkRequest)

        // Save the work ID for tracking
        val workId = downloadWorkRequest.id
        viewModel.addDownloadJob(workId, url)

        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
    }
}
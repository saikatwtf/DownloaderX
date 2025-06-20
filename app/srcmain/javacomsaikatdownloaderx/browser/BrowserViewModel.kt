package com.saikat.downloaderx.browser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.WebExtension

class BrowserViewModel : ViewModel() {

    private val _detectedMedia = MutableLiveData<List<MediaItem>>()
    val detectedMedia: LiveData<List<MediaItem>> = _detectedMedia
    
    private val _installedExtensions = MutableLiveData<List<WebExtension>>()
    val installedExtensions: LiveData<List<WebExtension>> = _installedExtensions
    
    private val _bookmarks = MutableLiveData<List<Bookmark>>()
    val bookmarks: LiveData<List<Bookmark>> = _bookmarks
    
    private val mediaItems = mutableListOf<MediaItem>()
    private val extensionsList = mutableListOf<WebExtension>()
    
    init {
        // Initialize default bookmarks
        val defaultBookmarks = listOf(
            Bookmark("Amazon", "https://www.amazon.com"),
            Bookmark("Flipkart", "https://www.flipkart.com"),
            Bookmark("Instagram", "https://www.instagram.com"),
            Bookmark("Facebook", "https://www.facebook.com")
        )
        _bookmarks.value = defaultBookmarks
    }
    
    fun startMediaSniffing(session: GeckoSession) {
        // Clear previous detected media
        mediaItems.clear()
        _detectedMedia.value = mediaItems
        
        // Inject media detection script
        val script = """
            (function() {
                const mediaElements = document.querySelectorAll('video, audio');
                const sources = [];
                
                mediaElements.forEach(element => {
                    if (element.src) {
                        sources.push({
                            url: element.src,
                            type: element.tagName.toLowerCase()
                        });
                    }
                });
                
                const linkElements = document.querySelectorAll('a[href]');
                linkElements.forEach(link => {
                    const href = link.href;
                    if (href.endsWith('.m3u8') || href.endsWith('.mpd') || 
                        href.includes('m3u8') || href.includes('mpd')) {
                        sources.push({
                            url: href,
                            type: href.includes('m3u8') ? 'm3u8' : 'mpd'
                        });
                    }
                });
                
                return sources;
            })();
        """.trimIndent()
        
        // Execute the script and process results
        session.evaluateJS(script) { result ->
            if (result != null && result.isArray) {
                for (i in 0 until result.length()) {
                    val item = result.getObject(i)
                    val url = item.getString("url")
                    val type = item.getString("type")
                    mediaItems.add(MediaItem(url, type))
                }
                _detectedMedia.postValue(mediaItems)
            }
        }
    }
    
    fun addExtension(extension: WebExtension) {
        extensionsList.add(extension)
        _installedExtensions.value = extensionsList.toList()
    }
    
    fun removeExtension(extension: WebExtension) {
        extensionsList.remove(extension)
        _installedExtensions.value = extensionsList.toList()
    }
    
    fun addBookmark(bookmark: Bookmark) {
        val currentBookmarks = _bookmarks.value?.toMutableList() ?: mutableListOf()
        currentBookmarks.add(bookmark)
        _bookmarks.value = currentBookmarks
    }
    
    fun removeBookmark(bookmark: Bookmark) {
        val currentBookmarks = _bookmarks.value?.toMutableList() ?: mutableListOf()
        currentBookmarks.remove(bookmark)
        _bookmarks.value = currentBookmarks
    }
}

data class MediaItem(val url: String, val type: String)
data class Bookmark(val name: String, val url: String)
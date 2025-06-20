package com.saikat.downloaderx.browser

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.saikat.downloaderx.R

class BookmarksDialogFragment : DialogFragment() {

    private lateinit var viewModel: BrowserViewModel
    private lateinit var bookmarksList: ListView
    private lateinit var nameInput: EditText
    private lateinit var urlInput: EditText
    private lateinit var addButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_bookmarks, container, false)
        
        viewModel = ViewModelProvider(requireParentFragment()).get(BrowserViewModel::class.java)
        
        bookmarksList = root.findViewById(R.id.bookmarks_list)
        nameInput = root.findViewById(R.id.bookmark_name_input)
        urlInput = root.findViewById(R.id.bookmark_url_input)
        addButton = root.findViewById(R.id.add_bookmark_button)
        
        addButton.setOnClickListener {
            addBookmark()
        }
        
        updateBookmarksList()
        
        return root
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Bookmarks")
        return dialog
    }
    
    private fun updateBookmarksList() {
        val bookmarks = viewModel.bookmarks.value ?: emptyList()
        
        val data = bookmarks.map { bookmark ->
            mapOf(
                "name" to bookmark.name,
                "url" to bookmark.url
            )
        }
        
        val adapter = SimpleAdapter(
            context,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf("name", "url"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        
        bookmarksList.adapter = adapter
        
        bookmarksList.setOnItemClickListener { _, _, position, _ ->
            val bookmark = bookmarks[position]
            val parentFragment = parentFragment as? BrowserFragment
            parentFragment?.loadUrl(bookmark.url)
            dismiss()
        }
        
        bookmarksList.setOnItemLongClickListener { _, _, position, _ ->
            val bookmark = bookmarks[position]
            viewModel.removeBookmark(bookmark)
            updateBookmarksList()
            true
        }
    }
    
    private fun addBookmark() {
        val name = nameInput.text.toString().trim()
        val url = urlInput.text.toString().trim()
        
        if (name.isEmpty() || url.isEmpty()) {
            Toast.makeText(context, "Please enter both name and URL", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewModel.addBookmark(Bookmark(name, url))
        nameInput.text.clear()
        urlInput.text.clear()
        updateBookmarksList()
        
        Toast.makeText(context, "Bookmark added", Toast.LENGTH_SHORT).show()
    }
}
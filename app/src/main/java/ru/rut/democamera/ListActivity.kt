package ru.rut.democamera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import ru.rut.democamera.databinding.ActivityListBinding
import java.io.File

class ListActivity : Activity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var adapter: ImageAdapter
    private val files = mutableListOf<File>()
    private val selectedFiles = mutableSetOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (selectedFiles.isNotEmpty()) {
            clearSelectedFiles()
        }

        binding.deleteButton.setOnClickListener {
            deleteSelectedFiles()
        }

        binding.clearButton.setOnClickListener {
            clearSelectedFiles()
        }

        loadImagesFromGallery()
    }

    private fun loadImagesFromGallery() {
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.DATA
        )

        val pictureFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/DemoCameraPictures"
        val videoFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath + "/DemoCameraVideos"

        val selection =
            ("(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?)" +
                    " AND (${MediaStore.Files.FileColumns.DATA} LIKE ? OR ${MediaStore.Files.FileColumns.DATA} LIKE ?)")
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
            "$pictureFolder%",
            "$videoFolder%"
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        files.clear()
        val query = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            while (cursor.moveToNext()) {
                val data = cursor.getString(dataIndex)
                files.add(File(data))
            }
        }

        adapter = ImageAdapter(files.toTypedArray(), ::onFileClick, ::onFileLongClick)

        binding.recyclerView.layoutManager = GridLayoutManager(this, 4)
        binding.recyclerView.adapter = adapter
    }

    private fun onFileClick(file: File) {
        if (selectedFiles.isNotEmpty()) {
            onFileLongClick(file)
        } else {
            val intent = Intent(this, GalleryActivity::class.java).apply {
                putExtra("filePath", file.absolutePath)
            }
            startActivity(intent)
        }
    }

    private fun onFileLongClick(file: File): Boolean {
        binding.clearButton.visibility = View.VISIBLE
        binding.deleteButton.visibility = View.VISIBLE
        if (selectedFiles.contains(file)) {
            selectedFiles.remove(file)
        } else {
            selectedFiles.add(file)
        }

        if (selectedFiles.isEmpty()) {
            binding.clearButton.visibility = View.GONE
            binding.deleteButton.visibility = View.GONE
        }

        adapter.setSelectedFiles(selectedFiles)
        return true
    }

    private fun clearSelectedFiles() {
        selectedFiles.clear()
        adapter.setSelectedFiles(selectedFiles)
        binding.clearButton.visibility = View.GONE
        binding.deleteButton.visibility = View.GONE
    }

    private fun deleteSelectedFiles() {
        if (selectedFiles.isNotEmpty()) {
            selectedFiles.forEach { file ->
                if (file.exists()) {
                    file.delete()
                    contentResolver.delete(
                        MediaStore.Files.getContentUri("external"),
                        "${MediaStore.Files.FileColumns.DATA} = ?",
                        arrayOf(file.absolutePath)
                    )
                }
            }
            binding.clearButton.visibility = View.GONE
            binding.deleteButton.visibility = View.GONE
            selectedFiles.clear()
            loadImagesFromGallery()
        }
    }
}
package ru.rut.democamera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import ru.rut.democamera.databinding.ActivityGalleryBinding
import java.io.File

class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedFilePath = intent.getStringExtra("filePath")
        val files = fetchMediaStoreFiles()
        val initialPosition = files.indexOfFirst { it.absolutePath == selectedFilePath }

        val adapter = GalleryAdapter(files.toTypedArray()) {
        }

        binding.viewPager.adapter = adapter

        val targetPosition = if (initialPosition != -1) initialPosition else 0
        binding.viewPager.setCurrentItem(targetPosition, false)
    }

    private fun fetchMediaStoreFiles(): List<File> {
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

        val files = mutableListOf<File>()
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

        return files
    }
}

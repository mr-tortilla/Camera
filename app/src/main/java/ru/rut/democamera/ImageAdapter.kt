package ru.rut.democamera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.rut.democamera.databinding.ListItemImageBinding
import java.io.File

class ImageAdapter(
    private val fileArray: Array<File>,
    private val onItemClick: (File) -> Unit,
    private val onItemLongClick: (File) -> Boolean
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private val selectedFiles = mutableSetOf<File>()

    fun setSelectedFiles(files: Set<File>) {
        selectedFiles.clear()
        selectedFiles.addAll(files)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ListItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(file: File, onItemClick: (File) -> Unit, onItemLongClick: (File) -> Boolean, isSelected: Boolean) {
            Glide.with(binding.root).load(file).into(binding.localImg)
            binding.videoIcon.visibility = if (file.extension == "mp4") View.VISIBLE else View.GONE

            // Установка прозрачности для выбранных элементов
            binding.root.alpha = if (isSelected) 0.5f else 1.0f

            binding.root.setOnClickListener { onItemClick(file) }

            binding.root.setOnLongClickListener { onItemLongClick(file) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ListItemImageBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = fileArray[position]
        holder.bind(file, onItemClick, onItemLongClick, selectedFiles.contains(file))
    }

    override fun getItemCount(): Int = fileArray.size
}

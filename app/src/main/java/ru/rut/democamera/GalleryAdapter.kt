package ru.rut.democamera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import ru.rut.democamera.databinding.GalleryItemBinding

class GalleryAdapter(
    private val fileArray: Array<File>,
    private val clickListener: (File) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: GalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(file: File, clickListener: (File) -> Unit) {
            if (isVideoFile(file)) {
                binding.videoView.visibility = View.VISIBLE
                binding.imageView.visibility = View.GONE

                binding.videoView.setVideoPath(file.absolutePath)

                binding.videoView.setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = false

                    val videoWidth = mediaPlayer.videoWidth
                    val videoHeight = mediaPlayer.videoHeight

                    val containerWidth = binding.videoView.width
                    val containerHeight = binding.videoView.height

                    val videoProportion = videoWidth.toFloat() / videoHeight
                    val containerProportion = containerWidth.toFloat() / containerHeight

                    if (videoProportion > containerProportion) {
                        binding.videoView.layoutParams.height = containerHeight
                        binding.videoView.layoutParams.width = (containerHeight * videoProportion).toInt()
                    } else {
                        binding.videoView.layoutParams.width = containerWidth
                        binding.videoView.layoutParams.height = (containerWidth / videoProportion).toInt()
                    }
                    val mediaController = MediaController(binding.videoView.context)
                    val controllerContainer = binding.mediaControllerContainer
                    mediaController.setAnchorView(controllerContainer)
                    binding.videoView.setMediaController(mediaController)

                    mediaPlayer.start()
                }
                binding.videoView.setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = false

                    val videoWidth = mediaPlayer.videoWidth
                    val videoHeight = mediaPlayer.videoHeight

                    val containerWidth = binding.videoView.width
                    val containerHeight = binding.videoView.height

                    val videoProportion = videoWidth.toFloat() / videoHeight
                    val containerProportion = containerWidth.toFloat() / containerHeight

                    if (videoProportion > containerProportion) {
                        binding.videoView.layoutParams.height = containerHeight
                        binding.videoView.layoutParams.width = (containerHeight * videoProportion).toInt()
                    } else {
                        binding.videoView.layoutParams.width = containerWidth
                        binding.videoView.layoutParams.height = (containerWidth / videoProportion).toInt()
                    }

                    binding.videoView.requestLayout()

                    val mediaController = MediaController(binding.videoView.context)
                    val controllerContainer = binding.mediaControllerContainer
                    mediaController.setAnchorView(controllerContainer)
                    binding.videoView.setMediaController(mediaController)

                    mediaPlayer.start()
                }

                binding.root.setOnClickListener {
                    clickListener(file)
                }
            } else {
                binding.imageView.visibility = View.VISIBLE
                binding.videoView.visibility = View.GONE

                Glide.with(binding.root).load(file).into(binding.imageView)

                binding.imageView.setOnClickListener {
                    clickListener(file)
                }
            }
        }

        private fun isVideoFile(file: File): Boolean {
            val videoExtensions = listOf("mp4", "mkv", "avi")
            return file.extension.lowercase() in videoExtensions
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(GalleryItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fileArray[position], clickListener)
    }

    override fun getItemCount(): Int {
        return fileArray.size
    }
}
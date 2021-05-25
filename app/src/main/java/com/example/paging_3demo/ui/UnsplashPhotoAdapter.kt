package com.example.paging_3demo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.paging_3demo.R
import com.example.paging_3demo.databinding.ItemUnsplashPhotoBinding
import com.example.paging_3demo.db.unplashdb.UnsplashPhotoEntity
import com.example.paging_3demo.network.api.data.UnsplashPhoto

class UnsplashPhotoAdapter :
    PagingDataAdapter<UnsplashPhotoEntity, UnsplashPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            ItemUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)

        currentItem?.let { item ->
            holder.bind(item)
        }
    }

    class PhotoViewHolder(private val binding: ItemUnsplashPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: UnsplashPhotoEntity) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.regularUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageView)

                textViewUserName.text = "Dummy username"
            }
        }
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhotoEntity>() {
            override fun areItemsTheSame(oldItem: UnsplashPhotoEntity, newItem: UnsplashPhotoEntity) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UnsplashPhotoEntity, newItem: UnsplashPhotoEntity) =
                oldItem == newItem
        }
    }
}
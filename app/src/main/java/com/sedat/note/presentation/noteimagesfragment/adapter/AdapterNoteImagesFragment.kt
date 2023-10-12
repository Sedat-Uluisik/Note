package com.sedat.note.presentation.noteimagesfragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.sedat.note.databinding.ItemLayoutImagesBinding
import com.sedat.note.domain.model.NoteImage
import javax.inject.Inject

class AdapterNoteImagesFragment @Inject constructor(
    private val glide: RequestManager
): ListAdapter<NoteImage, AdapterNoteImagesFragment.ViewHolder>(DiffUtilImages) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            glide,
            ItemLayoutImagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            if(position >= 0 && position < currentList.size) {
                if (getItem(position).imageFileUrl.isNotEmpty())
                    _itemClick.invoke(getItem(position).imageFileUrl)
            }
        }
    }

    class ViewHolder(private val glide: RequestManager, private val binding: ItemLayoutImagesBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(noteImage: NoteImage) = with(binding){
            txtDescription.text = noteImage.description
            glide
                .load(noteImage.imageFileUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(RoundedCorners(20))
                .into(imgNoteImage)
        }
    }

    private var _itemClick: (imagePath: String) -> Unit = {}
    fun itemClick(click: (imagePath: String) -> Unit){
        _itemClick = click
    }

    private object DiffUtilImages: DiffUtil.ItemCallback<NoteImage>(){
        override fun areItemsTheSame(oldItem: NoteImage, newItem: NoteImage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteImage, newItem: NoteImage): Boolean {
            return oldItem == newItem
        }

    }

}
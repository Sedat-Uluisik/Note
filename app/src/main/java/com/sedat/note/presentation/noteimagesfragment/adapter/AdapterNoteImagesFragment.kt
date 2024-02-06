package com.sedat.note.presentation.noteimagesfragment.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.sedat.note.R
import com.sedat.note.databinding.ItemLayoutImagesBinding
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.util.afterTextChange
import com.sedat.note.util.hide
import com.sedat.note.util.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        holder.bind(getItem(position), _deleteBtnClick, _updateItemDesc)

        holder.binding.imgNoteImage.setOnClickListener {
            if(position >= 0 && position < currentList.size) {
                if (getItem(position).imageFileUrl.isNotEmpty())
                    _itemClick.invoke(getItem(position).imageFileUrl)
            }
        }
    }

    class ViewHolder(private val glide: RequestManager, val binding: ItemLayoutImagesBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(noteImage: NoteImage, deleteBtnClick: (Int, Int, String) -> Unit, updateItemDesc: (id: Int, txt: String) -> Unit) = with(binding){
            if(noteImage.description.isNotEmpty()){
                layoutDesc.show()
                txtDescription.setText(noteImage.description)
            }else
                layoutDesc.hide()

            glide
                .load(noteImage.imageFileUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(RoundedCorners(20))
                .into(imgNoteImage)

            btnDeleteImage.setOnClickListener {
                deleteBtnClick.invoke(noteImage.rootID, noteImage.id, noteImage.imageFileUrl)
            }

            txtDescription.addTextChangedListener(object : TextWatcher{

                var job: Job ?= null

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    if(p0.toString().isNotEmpty() || p0.toString().isNotBlank()){
                        job?.cancel()

                        job = CoroutineScope(Dispatchers.Main).launch {
                            delay(750)

                            imgDesc.setImageResource(R.drawable.ic_save_20)
                            imgDesc.tag = R.drawable.ic_save_20
                        }
                    }else
                        job?.cancel()
                }

            })

            imgDesc.setOnClickListener {
                if(imgDesc.tag == R.drawable.ic_save_20){

                    imgDesc.setImageResource(R.drawable.ic_edit_20)

                    updateItemDesc.invoke(noteImage.id, txtDescription.text.toString())
                }else
                    txtDescription.requestFocus()
            }
        }
    }

    private var _itemClick: (imagePath: String) -> Unit = {}
    fun itemClick(click: (imagePath: String) -> Unit){
        _itemClick = click
    }

    private var _updateItemDesc: (id: Int, txt: String) -> Unit = {_,_ ->}
    fun updateItemDesc(update: (id: Int, txt: String) -> Unit){
        _updateItemDesc = update
    }

    private var _deleteBtnClick: (noteId: Int, imageId: Int, imagePath: String) -> Unit = {_,_, _ ->}
    fun deleteBtnClick(click: (noteId: Int, imageId: Int, imagePath: String) -> Unit){
        _deleteBtnClick = click
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
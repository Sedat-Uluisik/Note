package com.sedat.note.presentation.homefragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sedat.note.databinding.LayoutNoteItemBinding
import com.sedat.note.domain.model.Note
import com.sedat.note.util.hide
import com.sedat.note.util.show
import javax.inject.Inject

class AdapterHomeFragment @Inject constructor(): ListAdapter<Note, AdapterHomeFragment.ViewHolder>(DiffUtilHome) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutNoteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), _moreBtnClick)

        holder.itemView.setOnClickListener {
            if(position >= 0 && position < currentList.size) {
                if (getItem(position).subNoteCount > 0)
                    _itemClick.invoke(getItem(position))
            }
        }
    }

    class ViewHolder(private val binding: LayoutNoteItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(note: Note, moreBtnClick: (note: Note) -> Unit) = with(binding){
            noteText.text = note.text
            noteTimeText.text = note.convertDate()

            println(note.subNoteCount)

            if(note.subNoteCount > 0) {
                hasSubNoteBtn.show()
            }
            else {
                hasSubNoteBtn.hide()
            }
            if(note.imageCount > 0)
                hasImageFromNote.show()
            else
                hasImageFromNote.hide()

            moreBtn.setOnClickListener {
                moreBtnClick.invoke(note)
            }
        }
    }

    private var _moreBtnClick: (note: Note) -> Unit = {}
    fun moreBtnClick(click: (note: Note) -> Unit){
        _moreBtnClick = click
    }

    private var _itemClick: (note: Note) -> Unit = {}
    fun itemClick(click: (note: Note) -> Unit){
        _itemClick = click
    }

    private object DiffUtilHome: DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.subNoteCount == newItem.subNoteCount
        }
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return  oldItem == newItem
        }
    }

}
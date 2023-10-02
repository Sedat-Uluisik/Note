package com.sedat.note.presentation.homefragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sedat.note.databinding.LayoutNoteItemBinding
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteWithSubNoteInfo
import java.util.Date
import javax.inject.Inject

class AdapterHomeFragment @Inject constructor(): ListAdapter<NoteWithSubNoteInfo, AdapterHomeFragment.ViewHolder>(DiffUtilHome) {
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
                if (getItem(position).subNoteList.isNotEmpty())
                    _itemClick.invoke(getItem(position))
            }
        }
    }

    class ViewHolder(private val binding: LayoutNoteItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(note: NoteWithSubNoteInfo, moreBtnClick: (note: NoteWithSubNoteInfo) -> Unit) = with(binding){
            noteText.text = note.note.text
            noteTimeText.text = note.note.convertDate()

            if(note.subNoteList.isNotEmpty())
                hasSubNoteBtn.visibility = View.VISIBLE
            else
                hasSubNoteBtn.visibility = View.GONE

            moreBtn.setOnClickListener {
                moreBtnClick.invoke(note)
            }
        }
    }

    private var _moreBtnClick: (note: NoteWithSubNoteInfo) -> Unit = {}
    fun moreBtnClick(click: (note: NoteWithSubNoteInfo) -> Unit){
        _moreBtnClick = click
    }

    private var _itemClick: (note: NoteWithSubNoteInfo) -> Unit = {}
    fun itemClick(click: (note: NoteWithSubNoteInfo) -> Unit){
        _itemClick = click
    }

    private object DiffUtilHome: DiffUtil.ItemCallback<NoteWithSubNoteInfo>(){
        override fun areItemsTheSame(oldItem: NoteWithSubNoteInfo, newItem: NoteWithSubNoteInfo): Boolean {
            return oldItem.note.id == newItem.note.id || oldItem.subNoteList.size == newItem.subNoteList.size
        }

        override fun areContentsTheSame(oldItem: NoteWithSubNoteInfo, newItem: NoteWithSubNoteInfo): Boolean {
            return oldItem.note == newItem.note
        }

    }

}
package com.sedat.note.presentation.createnotefragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sedat.note.databinding.LayoutNoteItemBinding
import com.sedat.note.domain.model.Note
import javax.inject.Inject

class AdapterCreateNote @Inject constructor(): ListAdapter<Note, AdapterCreateNote.CreateNoteViewHolder>(DiffUtilCreateNote) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateNoteViewHolder {
        return CreateNoteViewHolder(
            LayoutNoteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CreateNoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CreateNoteViewHolder(binding: LayoutNoteItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(note: Note){

        }

    }

    private object DiffUtilCreateNote: DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

    }

}
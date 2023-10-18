package com.sedat.note.presentation.homefragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sedat.note.databinding.LayoutNoteItemBinding
import com.sedat.note.domain.model.Note
import com.sedat.note.util.ButtonsClick
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
        holder.bind(getItem(position), _btnClick)

        holder.itemView.setOnClickListener {
            if(position >= 0 && position < currentList.size) {
                if (getItem(position).subNoteCount > 0)
                    _btnClick.invoke(getItem(position), ButtonsClick.RECYCLERVIEW_ITEM_CLICK)
            }
        }
    }

    class ViewHolder(private val binding: LayoutNoteItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(note: Note, btnClick: (note: Note, type: ButtonsClick) -> Unit) = with(binding){
            noteText.text = note.text
            noteTimeText.text = note.convertDate()

            if(note.subNoteCount > 0) {
                btnSubNote.show()
            }
            else {
                btnSubNote.hide()
            }
            if(note.imageCount > 0)
                btnImage.show()
            else
                btnImage.hide()

            moreBtn.setOnClickListener {
                btnClick.invoke(note, ButtonsClick.MORE)
            }
            btnSubNote.setOnClickListener {
                btnClick.invoke(note, ButtonsClick.SHOW_SUB_NOTES)
            }
            btnImage.setOnClickListener {
                btnClick.invoke(note, ButtonsClick.SHOW_IMAGE)
            }

        }
    }

    private var _btnClick: (note: Note, type: ButtonsClick) -> Unit = {_,_ ->}
    fun moreBtnClick(click: (note: Note, type: ButtonsClick) -> Unit){
        _btnClick = click
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
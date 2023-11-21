package com.sedat.note.presentation.homefragment.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sedat.note.R
import com.sedat.note.databinding.LayoutNoteItemBinding
import com.sedat.note.domain.model.Note
import com.sedat.note.util.ButtonsClick
import com.sedat.note.util.TypeConverter
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
                _btnClick.invoke(it, getItem(position), ButtonsClick.RECYCLERVIEW_ITEM_CLICK)
            }
        }
    }

    class ViewHolder(private val binding: LayoutNoteItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(note: Note, btnClick: (view: View, note: Note, type: ButtonsClick) -> Unit) = with(binding){

            val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, TypeConverter().fromString(note.color))
            gradientDrawable.cornerRadius = 30f
            gradientDrawable.setStroke(
                0,
                ContextCompat.getColor(binding.root.context, R.color.grey)
            )
            homeFragmentAdapterItemRootLayout.background = gradientDrawable

            if(note.text.length > 100){
                val trimmedText = note.text.substring(0, 100) + "..."
                noteText.text = trimmedText
            }else
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
                btnClick.invoke(it, note, ButtonsClick.MORE)
            }
            btnSubNote.setOnClickListener {
                btnClick.invoke(it, note, ButtonsClick.SHOW_SUB_NOTES)
            }
            btnImage.setOnClickListener {
                btnClick.invoke(it, note, ButtonsClick.SHOW_IMAGE)
            }

        }
    }

    private var _btnClick: (view: View, note: Note, type: ButtonsClick) -> Unit = {_, _, _ ->}
    fun moreBtnClick(click: (view: View, note: Note, type: ButtonsClick) -> Unit){
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
package com.sedat.note.presentation.createnotefragment.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sedat.note.R
import com.sedat.note.databinding.LayoutColorsBinding
import javax.inject.Inject

class AdapterColors @Inject constructor(): ListAdapter<IntArray, AdapterColors.ColorHolder>(DiffUtilColors) {

    private var _itemClick: (colorCode: IntArray) -> Unit ={}
    fun itemClick(click: (IntArray) -> Unit){
        _itemClick = click
    }

    class ColorHolder(private val binding: LayoutColorsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(intArray: IntArray, click: (IntArray) -> Unit){
            val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArray)

            gradientDrawable.cornerRadius = 0f
            gradientDrawable.setStroke(
                0,
                ContextCompat.getColor(this.binding.root.context, R.color.grey)
            )

            binding.colorLayout.background = gradientDrawable

            binding.colorLayout.setOnClickListener {
                click.invoke(intArray)
            }
        }
    }

    private object DiffUtilColors: DiffUtil.ItemCallback<IntArray>(){
        override fun areItemsTheSame(oldItem: IntArray, newItem: IntArray): Boolean {
            return oldItem.contentEquals(newItem)

        }
        override fun areContentsTheSame(oldItem: IntArray, newItem: IntArray): Boolean {
            return oldItem.contentEquals(newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
        return ColorHolder(
            LayoutColorsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ColorHolder, position: Int) {
        holder.bind(getItem(position), _itemClick)
    }
}
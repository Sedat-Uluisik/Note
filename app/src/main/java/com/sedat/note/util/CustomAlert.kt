package com.sedat.note.util

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.sedat.note.R
import com.sedat.note.databinding.ItemLayoutCustomAlertBinding

class CustomAlert(private val _context: Context): AlertDialog.Builder(_context) {

    private lateinit var onButtonClick: (click: ButtonsClick) -> Unit

    fun showAlertForImageSelect(click: (click: ButtonsClick) -> Unit){
        val view = ItemLayoutCustomAlertBinding.inflate(LayoutInflater.from(_context))
        val builder = AlertDialog.Builder(_context)
        builder.setView(view.root)

        onButtonClick = click
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        if (alertDialog.window != null)
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        alertDialog.show()

        with(view){
            btnCamera.setOnClickListener {
                onButtonClick(ButtonsClick.IMAGE_FOR_CAMERA)
                alertDialog.dismiss()
            }

            btnGallery.setOnClickListener {
                onButtonClick(ButtonsClick.IMAGE_FOR_GALLERY)
                alertDialog.dismiss()
            }
        }
    }

    fun showDefaultAlert(title: String, message: String, okButton: (Boolean) -> Unit){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                okButton.invoke(true)
            }
            .setNegativeButton(context.getString(R.string.cancel)) { _, _ ->

            }
            .setCancelable(false)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
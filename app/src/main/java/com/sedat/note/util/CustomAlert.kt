package com.sedat.note.util

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.sedat.note.databinding.ItemLayoutCustomAlertBinding

class CustomAlert(private val _context: Context): AlertDialog.Builder(_context) {

    private lateinit var onButtonClick: (click: ButtonsClick) -> Unit

    fun showCustomAlert(click: (click: ButtonsClick) -> Unit){
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
            btnAddSubCategory.setOnClickListener {
                onButtonClick(ButtonsClick.ADD_SUB_NOTE)
                alertDialog.dismiss()
            }

            btnAddImage.setOnClickListener {
                onButtonClick(ButtonsClick.ADD_IMAGE)
                alertDialog.dismiss()
            }

            btnDeleteNote.setOnClickListener {
                onButtonClick(ButtonsClick.DELETE_NOTE)
                alertDialog.dismiss()
            }
        }
    }

    fun showDefaultAlert(title: String, message: String, okButton: (Boolean) -> Unit){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton("ok") { _, _ ->
                okButton.invoke(true)
            }
            .setNegativeButton("cancel") { _, _ ->

            }
            .setCancelable(false)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
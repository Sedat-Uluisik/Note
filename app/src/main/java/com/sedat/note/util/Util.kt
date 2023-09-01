package com.sedat.note.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.hide(){
    if(this.visibility == View.VISIBLE)
        this.visibility = View.GONE
}

fun View.show(){
    if(this.visibility == View.GONE)
        this.visibility = View.VISIBLE
}

fun Activity.closeKeyboard() {
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
}
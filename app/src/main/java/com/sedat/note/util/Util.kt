package com.sedat.note.util

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

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

fun EditText.afterTextChange(afterTextChange: (String) -> Unit){
    this.addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            s?.let {
                    afterTextChange.invoke(it.toString())
            }
        }

    })
}
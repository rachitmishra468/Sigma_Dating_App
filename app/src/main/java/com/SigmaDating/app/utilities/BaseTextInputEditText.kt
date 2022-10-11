package com.SigmaDating.app.utilities

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText

class BaseTextInputEditText(context: Context?, attrs: AttributeSet?) : TextInputEditText(context!!, attrs){

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) this.hideKeyboard()

    }


    fun View.hideKeyboard(){
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
       // inputMethodManager.hideSoftInputFromWindow(this.windowToken, 1)
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
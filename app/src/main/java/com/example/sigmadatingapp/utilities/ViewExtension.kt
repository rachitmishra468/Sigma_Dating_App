package com.example.sigmadatingapp.utilities

import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(
    message: String,
    length: Int = Snackbar.LENGTH_SHORT,
    anchorView: BottomNavigationView? = null,
    action: Boolean = true,
    actionText: String = "Ok",
    callback: () -> String = {"test" }
): Snackbar {
    val snackBarView = Snackbar.make(this, message, length)
    val params = snackBarView.view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(
        params.leftMargin,
        params.topMargin,
        params.rightMargin,
        params.bottomMargin + 100
    )
    snackBarView.animationMode = Snackbar.ANIMATION_MODE_SLIDE

    snackBarView.view.layoutParams = params
    if (anchorView != null) {
        snackBarView.anchorView = anchorView
    }
    if (action)
        snackBarView.setAction(actionText) {
            callback()
        }
    snackBarView.show()
    return snackBarView
}
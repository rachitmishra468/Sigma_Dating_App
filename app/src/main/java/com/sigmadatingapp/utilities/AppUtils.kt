package com.sigmadatingapp.utilities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.sigmadatingapp.R
import com.google.android.material.snackbar.Snackbar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.regex.Pattern

object AppUtils {

        private var dialog: Dialog? = null

        fun hideLoader() {
            if (dialog!!.isShowing) dialog!!.dismiss()
        }

        fun showLoader(context: Context?) {
            val builder = AlertDialog.Builder(context,R.style.NewDialog)
            val inflater = LayoutInflater.from(context)
            val view: View = inflater.inflate(R.layout.custom_loader, null, false)
            builder.setView(view)
            dialog?.getWindow()?.setDimAmount(0f);
            dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);
            dialog = builder.create()
            (dialog as AlertDialog?)?.setCancelable(false)
            val groupcreate: LottieAnimationView =
                view.findViewById<View>(R.id.email) as LottieAnimationView
            groupcreate.setAnimation(R.raw.loader)
            groupcreate.playAnimation()
            (dialog as AlertDialog?)?.getWindow()!!
                .setBackgroundDrawableResource(R.color.hint_text_color)
            (dialog as AlertDialog?)?.show()
        }

        fun isValidEmail(email: String?): Boolean {
            val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$"
            val pat = Pattern.compile(emailRegex)
            return !(email == null || email.isEmpty() || !pat.matcher(email).matches())
        }


    fun isValid_password(password: String?): Boolean {

        if (password != null) {
            return (password.length>5)
        }

        return false
    }


    fun isValid_phone_number(number: String?): Boolean{
        if(number?.matches(".*[0-9].*".toRegex()) == false){
            return false
        }
        if(number?.length != 10){
            return false
        }
        return true
    }

    fun checkIfEmailIsValid(emailInputText:String): String? {
            if(!Patterns.EMAIL_ADDRESS.matcher(emailInputText).matches()){
              return "Invalid Email Address"
            }
            else{
              return  null
            }
    }





        fun registerBroadCastReceiver(context: Context, receiver: BroadcastReceiver?) {
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            context.registerReceiver(receiver, intentFilter)
        }

        fun unregisterBroadCastReceiver(context: Context, receiver: BroadcastReceiver?) {
            if (receiver != null) context.unregisterReceiver(receiver)
        }

        fun internetConnectivityAction(context: Context, isconnected: Boolean, container: View?) {
            if (isconnected) {
//            showNormalSnackBar(context, container, context.getString(R.string.internetconnected));
            } else {
                showErrorSnackBar(
                    context,
                    container,
                    context.getString(R.string.internetdisconnect)
                )
            }
        }

        fun isNetworkInterfaceAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

        @SuppressLint("ResourceAsColor")
        fun showSnackBar(
            context: Context?,
            view: View?,
            message: String?,
            colorid: Int,
            textcolorid: Int
        ) {
            val snackbar = Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(ContextCompat.getColor(context!!, colorid))
            val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
            textView.setTextColor(R.color.cardview_shadow_end_color)
            snackbar.show()
        }

        fun showErrorSnackBar(context: Context?, view: View?, message: String?) {
            val snackbar = Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.hint_text_color
                )
            )
            val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
            textView.setTextColor(ContextCompat.getColor(context, R.color.white))
            snackbar.show()
        }

        fun showNormalSnackBar(context: Context?, view: View?, message: String?) {
            val snackbar = Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.hint_text_color))
            val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
            textView.maxLines = 5
            textView.setTextColor(ContextCompat.getColor(context, R.color.white))
            snackbar.show()
        }

        fun hideSoftKeyboard(activity: Context, view: View) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
        }

        fun showSoftKeyBoard(context: Context, view: View) {
            if (view.requestFocus()) { val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        fun setCustomDate(inputdate: String): String? {
            var newformateddate = ""
            val toformatstr = "MMM dd, yyyy | hh:mm aa"
            val todateFormat = SimpleDateFormat(toformatstr)
            val fromformatstr = "yyyy-MM-dd hh:mm:ss"
            val fromdateFormat = SimpleDateFormat(fromformatstr)
            try {
                val date = fromdateFormat.parse(inputdate.trim { it <= ' ' })
                newformateddate = todateFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return newformateddate
        }


    fun   checkValidationOnFisrtStep(context: Context,view: View?, fname:String, lastname:String): Boolean {
        val pattern = Regex("^[A-Za-z]+$")
        if (fname.isEmpty()|| fname == "null"){
            showErrorSnackBar(context,view,"Enter valid First Name")
    return false
}
        else if(lastname.isEmpty()||lastname.isBlank()||fname == "null"){
            showErrorSnackBar(context,view,"Enter valid  Last Name")
            return false
        }

return true
    }
}
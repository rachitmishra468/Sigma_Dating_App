package com.SigmaDating.app.utilities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.SigmaDating.R
import com.SigmaDating.app.AppReseources
import com.airbnb.lottie.LottieAnimationView
import com.example.demoapp.other.Constants
import com.google.android.material.snackbar.Snackbar
import java.lang.Math.abs
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object AppUtils {

    private var dialog: Dialog? = null
    private var mp: MediaPlayer? = null
    private var flag: Boolean? = false

    fun hideLoader() {
        try {
            if (dialog!!.isShowing) dialog!!.dismiss()
        } catch (e: Exception) {
        }

    }

    fun showLoader(context: Context?) {
        context?.javaClass?.name?.let { Log.d("TAG@123", it) }
        val builder = AlertDialog.Builder(context, R.style.NewDialog)
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
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!*]).{8,15})"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }


    fun isValid_password_match(new_password: String?, confirm_password: String): Boolean {

        if (new_password.equals(confirm_password)) {
            return true
        }

        return false
    }


    fun isValid_phone_number(number: String?): Boolean {
        if (number?.matches(".*[0-9].*".toRegex()) == false) {
            return false
        }
        if (number?.length != 12) {
            return false
        }
        return true
    }

    fun checkIfEmailIsValid(emailInputText: String): String? {
        if (!Patterns.EMAIL_ADDRESS.matcher(emailInputText).matches()) {
            return "Invalid Email Address"
        } else {
            return null
        }
    }

    fun isValidDate(dateOfBirth: String): Boolean {
        var valid = true
        val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        formatter.setLenient(false)
        try {
            val date: Date = formatter.parse(dateOfBirth)
            return true
        } catch (e: ParseException) {
            return false
            //If input date is in different format or invalid.
        }
        return valid
    }


    private fun validateMonthWithMaxDate(day: String, month: String): Boolean =
        day == "31" && (month == "4" || month == "6" || month == "9" || month == "11" || month == "04" || month == "06" || month == "09")

    private fun isFebruaryMonth(month: String): Boolean = month == "2" || month == "02"
    private fun isLeapYear(year: String): Boolean = year.toInt() % 4 == 0
    private fun leapYearWith29Date(day: String): Boolean = !(day == "30" || day == "31")
    private fun notLeapYearFebruary(day: String): Boolean =
        !(day == "29" || day == "30" || day == "31")


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
                R.color.light_blue_900
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
                R.color.hint_text_color
            )
        )
        val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.maxLines = 5
        textView.setTextColor(ContextCompat.getColor(context, R.color.white))
        snackbar.show()
    }

    fun hideSoftKeyboard(activity: Context, view: View) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }


    fun animateImageview(view: ImageView) {
        view.animate().scaleX(0.7f).setDuration(100).withEndAction {
            view.animate().scaleX(1f).scaleY(1f)
        }
    }


    fun checkValidationOnFisrtStep(
        context: Context,
        view: View?,
        fname: String,
        lastname: String
    ): Boolean {
        val pattern = Regex("^[A-Za-z]+$")

        Log.d("TAG@123", "fname :" + fname + " :: lastname :" + lastname)
        if (fname.isEmpty() || fname == "null") {
            showErrorSnackBar(context, view, "Enter valid First Name")
            return false
        } else if (lastname.isEmpty() || lastname.isBlank() || fname == "null") {
            showErrorSnackBar(context, view, "Enter valid  Last Name")
            return false
        }

        return true
    }


    fun getAgeDiffernce(dobString: String): Int {
        var date: Date? = null
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        try {
            date = sdf.parse(dobString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date == null) return 0
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob.time = date
        val year = dob[Calendar.YEAR]
        val month = dob[Calendar.MONTH]
        val day = dob[Calendar.DAY_OF_MONTH]
        dob[year, month + 1] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }


    fun customTextView(view: TextView, view_fab: Context) {
        val spanTxt = SpannableStringBuilder(
            "I agree with the "
        )
        spanTxt.append("Terms and Conditions")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                try {
                    open_web(view_fab)
                } catch (e: Exception) {
                    Log.d("TAG@123", "Terms and Conditions" + e.message)

                }
            }
        }, spanTxt.length - "Terms and Conditions".length, spanTxt.length, 0)
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    fun open_web(context: Context) {
        val alert = AlertDialog.Builder(context)
        alert.setTitle(Constants.terms_con_hadder)
        val wv = WebView(context)
        wv.loadUrl(Constants.terms_con)
        wv.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                view.loadUrl(url!!)
                return true
            }
        })
        alert.setView(wv)
        alert.setNegativeButton(
            "Close"
        ) { dialog, id -> dialog.dismiss() }
        alert.show()
    }


    fun Age_finder(dob: String): String {
        try {
            val maxDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val maxMonth = Calendar.getInstance().get(Calendar.MONTH)
            val maxYear = Calendar.getInstance().get(Calendar.YEAR)
            val currentDate = dob
            val finalDate = "$maxMonth/$maxDay/$maxYear"
            val date1: Date
            val date2: Date
            val dates = SimpleDateFormat("MM/dd/yyyy")
            date1 = dates.parse(currentDate)
            date2 = dates.parse(finalDate)
            val difference: Long = abs(date1.time - date2.time)
            val differenceDates = difference / (24 * 60 * 60 * 1000)
            Log.d("TAH@123", "Age : " + abs(differenceDates / 365).toString())
            return abs(differenceDates / 365).toString()
        } catch (e: Exception) {
            Log.d("TAH@123", "Age Exception: " + e.message)
            return ""
        }
    }


    fun isNetworkInterfaceAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }


    fun isNetworkAvailable(): Boolean {
        if (isNetworkAvailable_()) {
            return true
        }
        Toast.makeText(
            AppReseources.getAppContext()!!,
            R.string.internet_error_message,
            Toast.LENGTH_LONG
        ).show()
        return false
    }

    fun isNetworkAvailable_(): Boolean {
        val context = AppReseources.getAppContext()!!
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }

        return false
    }

    fun playPhoneCallRing(context: Context?) {
        if (!flag!!) {
            mp = MediaPlayer.create(context, com.SigmaDating.R.raw.phone_ringing_sound)
            mp?.start()
            flag = true

        }

    }

    fun stopPhoneCallRing() {
        mp?.stop()
        flag = false
    }


    fun open_ad_link(url: String, context: Context) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        context.startActivity(i)
    }

}
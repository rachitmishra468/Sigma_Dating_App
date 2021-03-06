package com.SigmaDating.apk.utilities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.SigmaDating.R
import com.SigmaDating.apk.model.communityModel.UniversityList
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object AppUtils {

    private var dialog: Dialog? = null

    fun hideLoader() {
        try {
            if (dialog!!.isShowing) dialog!!.dismiss()
        }catch (e:Exception){}

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

        if (password != null) {
            return (password.length > 5)
        }

        return false
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
        if (number?.length != 10) {
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

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
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

    fun validateDate(date: String): Boolean {
        val regex = "^(0[0-9]||1[0-2])/([0-2][0-9]||3[0-1])/([0-9][0-9])?[0-9][0-9]$"
        val matcher = Pattern.compile(regex).matcher(date)
        return if (matcher.matches()) {
            matcher.reset()
            if (matcher.find()) {
                val dateDetails = date.split("/")
                val day: String = dateDetails[1]
                val month: String = dateDetails[0]
                val year: String = dateDetails[2]
                if (validateMonthWithMaxDate(day, month)) {
                    false
                } else if (isFebruaryMonth(month)) {
                    if (isLeapYear(year)) {
                        leapYearWith29Date(day)
                    } else {
                        notLeapYearFebruary(day)
                    }
                } else {
                    true
                }
            } else {
                false
            }
        } else {
            false
        }
    }

    private fun validateMonthWithMaxDate(day: String, month: String): Boolean =
        day == "31" && (month == "4" || month == "6" || month == "9" || month == "11" || month == "04" || month == "06" || month == "09")

    private fun isFebruaryMonth(month: String): Boolean = month == "2" || month == "02"
    private fun isLeapYear(year: String): Boolean = year.toInt() % 4 == 0
    private fun leapYearWith29Date(day: String): Boolean = !(day == "30" || day == "31")
    private fun notLeapYearFebruary(day: String): Boolean =
        !(day == "29" || day == "30" || day == "31")

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

    fun showSoftKeyBoard(context: Context, view: View) {
        if (view.requestFocus()) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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


    fun checkValidationOnFisrtStep(
        context: Context,
        view: View?,
        fname: String,
        lastname: String
    ): Boolean {
        val pattern = Regex("^[A-Za-z]+$")

        Log.d("TAG@123","fname :"+fname+" :: lastname :"+lastname)
        if (fname.isEmpty() || fname == "null") {
            showErrorSnackBar(context, view, "Enter valid First Name")
            return false
        } else if(lastname.isEmpty() || lastname.isBlank() || fname == "null") {
            showErrorSnackBar(context, view, "Enter valid  Last Name")
            return false
        }

        return true
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }
public fun getcheckImagerotation(photoPath:String, bitmap: Bitmap):Bitmap{
    val ei: ExifInterface = ExifInterface(photoPath)
    val orientation = ei.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )

    var rotatedBitmap: Bitmap? = null
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmap, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(bitmap, 270F)
        ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
        else -> rotatedBitmap = bitmap
    }
    return rotatedBitmap!!
}
    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }





    fun index(spinner: Spinner, value: String?): Int {
        val adapter: Adapter = spinner.getAdapter()
        val n: Int = adapter.getCount()
        for (i in 0 until n) {
            var name= (spinner.getItemAtPosition(i) as UniversityList).name
            if(name.equals(value)){
                return i
                break
            }
        }
        return 0
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
}
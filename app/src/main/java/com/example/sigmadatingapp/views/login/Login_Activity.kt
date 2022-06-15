package com.example.sigmadatingapp.views.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.example.sigmadatingapp.R
import com.example.sigmadatingapp.module.Loginmodel
import com.example.sigmadatingapp.repository.MainRepository
import com.example.sigmadatingapp.storage.AppConstants
import com.example.sigmadatingapp.storage.AppConstants.PHONE_LOGIN
import com.example.sigmadatingapp.storage.SharedPreferencesStorage
import com.example.sigmadatingapp.utilities.AppUtils
import com.example.sigmadatingapp.views.OnBoardingActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.hbb20.CountryCodePicker.OnCountryChangeListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@AndroidEntryPoint
class Login_Activity : AppCompatActivity() {


    private val mainViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage

    private lateinit var phone_number_layout: LinearLayout
    private lateinit var emailLayoutLayout: LinearLayout
    lateinit var email_button: Button
    lateinit var phone_number_button: Button
    lateinit var button_login_email_phone_both: Button
    lateinit var country_spinner: CountryCodePicker
    lateinit var activity_main: ConstraintLayout
    lateinit var editText_email: EditText
    lateinit var editText_password: EditText
    lateinit var edittext_phone_no: EditText


    private var disposableObserver: SingleObserver<Loginmodel>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_scroll)
        initialize_view()
    }

    fun initialize_view() {
        button_login_email_phone_both = findViewById(R.id.button_login_email_phone_both)
        email_button = findViewById(R.id.email_button)
        phone_number_button = findViewById(R.id.phone_number_button)
        phone_number_layout = findViewById(R.id.phone_number_layout)
        emailLayoutLayout = findViewById(R.id.emailLayoutLayout)
        country_spinner = findViewById(R.id.ccp)
        activity_main = findViewById(R.id.activity_main)
        editText_email = findViewById(R.id.editText_email)
        editText_password = findViewById(R.id.editText_password)
        edittext_phone_no = findViewById(R.id.edittext_phone_no)

    }


    fun onCountryPickerClick(view: View?) {
        country_spinner.setOnCountryChangeListener(OnCountryChangeListener {
            sharedPreferencesStorage.setValue(
                AppConstants.USER_COUNTRY_CODE,
                country_spinner.getSelectedCountryCodeWithPlus()
            )
        })
    }

    fun email_button_click(view: View) {
        email_button.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
        phone_number_button.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
        email_button.setTextColor(this.getResources().getColor(R.color.black))
        phone_number_button.setTextColor(this.getResources().getColor(R.color.white))
        phone_number_layout.visibility = View.GONE
        emailLayoutLayout.visibility = View.VISIBLE
        button_login_email_phone_both.setText(R.string.sign_in)
        PHONE_LOGIN=false

    }

    fun phonenumber_button_click(view: View) {
        email_button.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
        phone_number_button.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
        phone_number_button.setTextColor(this.getResources().getColor(R.color.black))
        email_button.setTextColor(this.getResources().getColor(R.color.white))
        phone_number_layout.visibility = View.VISIBLE
        emailLayoutLayout.visibility = View.GONE
        button_login_email_phone_both.setText(R.string.send_otp_text)
        PHONE_LOGIN=true
    }

    fun sign_up(view: View) {
        AppUtils.showLoader(this)
        Handler().postDelayed(
            {
                AppUtils.hideLoader()

                startActivity(Intent(this, OnBoardingActivity::class.java))
            },
            1500
        )
    }


    fun login_call(view: View) {

        if (AppUtils.isNetworkInterfaceAvailable(this)) {

            if (PHONE_LOGIN){
                if (!AppUtils.isValid_phone_number(edittext_phone_no.text.toString())) {
                    edittext_phone_no.error = "Invalid Phone Number"
                    return
                }
            }
            else{
            if (AppUtils.checkIfEmailIsValid(editText_email.text.toString())!=null) {
                editText_email.error = "Invalid Email Address"
                return
            }
            if (!AppUtils.isValid_password(editText_password.text.toString())) {
                editText_password.error = "Password Length Must be of 6-8"
                return
            }}

            login()

          /*  AppUtils.showLoader(this)
            Handler().postDelayed(
                {
                    AppUtils.hideLoader()

                    startActivity(Intent(this, OnBoardingActivity::class.java))
                },
                1500
            )*/


        }
    }




    fun login() {
        mainViewModel.res?.observe(this, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let {res->
                        if (res?.status == true){
                            Toast.makeText(this@Login_Activity, res.message, Toast.LENGTH_LONG).show()
                        }else{

                            Toast.makeText(this@Login_Activity, res!!.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                Status.LOADING -> {
                    AppUtils.showLoader(this)
                }
                Status.ERROR -> {

                }
            }
        })



    }


}
package com.sigmadatingapp.views.login

import android.app.Activity
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
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.demoapp.other.Status
import com.sigmadatingapp.module.Loginmodel
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.storage.AppConstants.PHONE_LOGIN
import com.sigmadatingapp.storage.SharedPreferencesStorage
import com.sigmadatingapp.utilities.AppUtils
import com.hbb20.CountryCodePicker
import com.hbb20.CountryCodePicker.OnCountryChangeListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import javax.inject.Inject
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import java.util.*
import com.facebook.FacebookException
import com.sigmadatingapp.R
import com.facebook.login.LoginResult


import com.facebook.FacebookCallback
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sigmadatingapp.views.Home
import com.sigmadatingapp.views.intro_registration.OnBoardingActivity


@AndroidEntryPoint
class Login_Activity : AppCompatActivity() {

    private val EMAIL = "email"
    private val USER_POSTS = "user_posts"
    private val AUTH_TYPE = "rerequest"

    private var mCallbackManager: CallbackManager? = null
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
    lateinit var mLoginButton: LoginButton
    lateinit var signInButton: SignInButton
    lateinit var gso: GoogleSignInOptions


    private var disposableObserver: SingleObserver<Loginmodel>? = null

    //variable for FirebaseAuth class
    //private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_scroll)
        //below line is for getting instance of our FirebaseAuth.
        //  mAuth = FirebaseAuth.getInstance()
        mCallbackManager = CallbackManager.Factory.create();
        initialize_view()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        signInButton = findViewById(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener {
            signIn()
        }


        // Set the initial permissions to request from the user while logging in
        mLoginButton.setPermissions(Arrays.asList(EMAIL, USER_POSTS));

        mLoginButton.setAuthType(AUTH_TYPE);
        mLoginButton.registerCallback(
            mCallbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onCancel() {
                    setResult(RESULT_CANCELED)
                    finish()
                }

                override fun onError(@NonNull e: FacebookException) {
                    // Handle exception
                }

                override fun onSuccess(result: LoginResult?) {
                    setResult(RESULT_OK);
                    finish();
                }
            })
    }


    private fun signIn() {
        val signInIntent: Intent = GoogleSignIn.getClient(this, gso).getSignInIntent()
        startActivityForResult(signInIntent, 111)
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
        mLoginButton = findViewById(R.id.login_button);

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
        PHONE_LOGIN = false

    }

    fun phonenumber_button_click(view: View) {
        email_button.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
        phone_number_button.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
        phone_number_button.setTextColor(this.getResources().getColor(R.color.black))
        email_button.setTextColor(this.getResources().getColor(R.color.white))
        phone_number_layout.visibility = View.VISIBLE
        emailLayoutLayout.visibility = View.GONE
        button_login_email_phone_both.setText(R.string.send_otp_text)
        PHONE_LOGIN = true
    }

    fun sign_up(view: View) {
        AppUtils.showLoader(this)
        Handler().postDelayed(
            {
                AppUtils.hideLoader()

                startActivity(Intent(this, OnBoardingActivity::class.java))
                finish()
            },
            500
        )
    }


    fun login_call(view: View) {

        if (AppUtils.isNetworkInterfaceAvailable(this)) {

            if (PHONE_LOGIN) {
                if (!AppUtils.isValid_phone_number(edittext_phone_no.text.toString())) {
                    edittext_phone_no.error = "Invalid Phone Number"
                    return
                } else {
                    sign_up(view)
                }
            } else {
                if (AppUtils.checkIfEmailIsValid(editText_email.text.toString()) != null) {
                    editText_email.error = "Invalid Email Address"
                    return
                }
                if (!AppUtils.isValid_password(editText_password.text.toString())) {
                    editText_password.error = "Password Length Must be of 6-8"
                    return
                }
            }


            sharedPreferencesStorage.setValue(AppConstants.email, editText_email.text.toString())
            sharedPreferencesStorage.setValue(
                AppConstants.password,
                editText_password.text.toString()
            )
            login()

        }
    }


    fun login() {
        mainViewModel.res?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            Toast.makeText(this@Login_Activity, res.message, Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, Home::class.java))
                            finish()
                        } else {

                            Toast.makeText(this@Login_Activity, res!!.message, Toast.LENGTH_LONG)
                                .show()
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 111) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else {
            mCallbackManager?.onActivityResult(requestCode, resultCode, data);
        }


    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            // updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            // Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            // updateUI(null)
        }
    }


    private fun sendVerificationCode(number: String) {
        //this method is used for getting OTP on user phone number.
        /* PhoneAuthProvider.getInstance().verifyPhoneNumber(
             number, // Phone number to verify
             60,             // Timeout duration
             TimeUnit.SECONDS,   // Unit of timeout
             this,           // Activity (for callback binding)
             mCallbacks)*/
    }

}
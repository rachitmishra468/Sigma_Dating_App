package com.SigmaDating.apk.views.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.demoapp.other.Status
import com.SigmaDating.apk.model.Loginmodel
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.storage.AppConstants.PHONE_LOGIN
import com.SigmaDating.apk.storage.SharedPreferencesStorage
import com.SigmaDating.apk.utilities.AppUtils
import com.hbb20.CountryCodePicker
import com.hbb20.CountryCodePicker.OnCountryChangeListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import javax.inject.Inject
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import java.util.*
import com.facebook.FacebookException
import com.SigmaDating.R
import com.SigmaDating.apk.AppReseources
import com.facebook.login.LoginResult


import com.facebook.FacebookCallback
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.SigmaDating.apk.views.Home
import com.SigmaDating.apk.views.intro_registration.OnBoardingActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.facebook.GraphResponse

import org.json.JSONObject

import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import org.jetbrains.anko.email


@AndroidEntryPoint
class Login_Activity : AppCompatActivity() {

    private val EMAIL = "email"
    private val USER_POSTS = "user_posts"
    private val AUTH_TYPE = "rerequest"

    private var mCallbackManager: CallbackManager? = null
    val mainViewModel: LoginViewModel by viewModels()



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
    lateinit var signInButton: ImageView
    lateinit var fb_sign_in_button:ImageView
    lateinit var gso: GoogleSignInOptions
    lateinit var textforgot: TextView

    var editText_otp: EditText? = null
    var verfie_otp: Button? = null


    private var disposableObserver: SingleObserver<Loginmodel>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_scroll)
        mCallbackManager = CallbackManager.Factory.create();
        initialize_view()
        AppReseources.setGoogleSignInOptions()
        gso= AppReseources.getGoogleSignInOptions()!!
        signInButton = findViewById(R.id.sign_in_button)
        fb_sign_in_button=findViewById(R.id.fb_sign_in_button)
        fb_sign_in_button.setOnClickListener {
            mLoginButton.performClick();
        }
        verfie_otp = findViewById(R.id.verfie_otp)
        signInButton.setOnClickListener {
            signIn()
        }

        textforgot.setOnClickListener {
            openforgotPasswordDialog()
        }
        // Set the initial permissions to request from the user while logging in


        mLoginButton.setPermissions(Arrays.asList(EMAIL, USER_POSTS));
        mLoginButton.setAuthType(AUTH_TYPE);
        mLoginButton.registerCallback(
            mCallbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onCancel() {
                    setResult(RESULT_CANCELED)
                   // finish()
                }

                override fun onError(@NonNull e: FacebookException) {
                    // Handle exception
                }

                override fun onSuccess(result: LoginResult?) {
                    setResult(RESULT_OK);
                    Log.d("TAG@123",""+result)

                    val request = GraphRequest.newMeRequest(
                        result?.accessToken
                    ) { jsonObject, response ->

                        Log.d("TAG@123", ""+jsonObject?.get("email"))

                        sharedPreferencesStorage.setValue(
                            AppConstants.upload_image,
                            jsonObject?.get("picture").toString()
                        )

                        sharedPreferencesStorage.setValue(
                            AppConstants.email,
                            jsonObject?.get("email")
                        )
                        sharedPreferencesStorage.setValue(
                            AppConstants.isSocialLogin,
                            true
                        )

                        sharedPreferencesStorage.setValue(
                            AppConstants.appleId,
                            jsonObject?.get("email")
                        )

                        sharedPreferencesStorage.setValue(
                            AppConstants.fisrtname,
                            jsonObject?.get("name")
                        )
                        LoginManager.getInstance().logOut();
                        mainViewModel.Register()

                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,link,email,picture")
                    request.parameters = parameters
                    request.executeAsync()





                }
            })

    }

    private fun openforgotPasswordDialog() {
        val dialog = BottomSheetDialog(this, R.style.DialogStyle)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_forgot, null)
        val btnSubmit = view.findViewById<Button>(R.id.button_forgot_submit)
        val editTextEmail = view.findViewById<EditText>(R.id.editText_forgot_email)
        btnSubmit.setOnClickListener {
            if (AppUtils.isValidEmail(editTextEmail.text.toString())) {
                sharedPreferencesStorage.setValue(AppConstants.email, editTextEmail.text.toString())
                forgotPasswordCall(dialog)
                mainViewModel.User_forgot()
            } else {
                editTextEmail.error = "Invalid Email Address"
            }
            //dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setOnDismissListener {
            dialog.dismiss()
            Log.d("TAG@123", "DISMISSED")
        }

        dialog.setCancelable(true)
        dialog.setContentView(view)



        dialog.show()

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
        textforgot = findViewById(R.id.textView2)
        editText_otp = findViewById(R.id.editText_otp)

        login()
        sent_otp()
        verifly_otp()
        sosicl_login()

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
        editText_otp?.visibility = View.GONE
        verfie_otp?.visibility = View.GONE
        button_login_email_phone_both.visibility = View.VISIBLE
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

        startActivity(Intent(this, OnBoardingActivity::class.java))
        finish()

    }


    fun verifyotp(view: View) {
        mainViewModel.phone_verifly_OTP(editText_otp?.text.toString())
    }


    fun login_call(view: View) {

        if (AppUtils.isNetworkInterfaceAvailable(this)) {

            if (PHONE_LOGIN) {
                if (!AppUtils.isValid_phone_number(edittext_phone_no.text.toString())) {
                    edittext_phone_no.error = "Invalid Phone Number"
                    return
                } else {
                    sharedPreferencesStorage.setValue(
                        AppConstants.phone,
                        edittext_phone_no.text.toString()
                    )

                    sharedPreferencesStorage.setValue(
                        AppConstants.USER_COUNTRY_CODE,
                        country_spinner.selectedCountryCodeWithPlus
                    )

                    Log.d(
                        "TAG@123", sharedPreferencesStorage.setValue(
                            AppConstants.USER_COUNTRY_CODE,
                            country_spinner.selectedCountryCodeWithPlus
                        ).toString()
                    )
                    sharedPreferencesStorage.setValue(
                        AppConstants.isSocialLogin,
                        false
                    )
                    mainViewModel.login_phone()
                }
            } else {
                if (AppUtils.checkIfEmailIsValid(editText_email.text.toString()) != null) {
                    editText_email.error = "Invalid Email Address"
                    return
                }
                if (!AppUtils.isValid_password(editText_password.text.toString())) {
                    editText_password.error = "Password Length Must be of 5-8"
                    return
                }
                sharedPreferencesStorage.setValue(
                    AppConstants.isSocialLogin,
                    false
                )

                sharedPreferencesStorage.setValue(
                    AppConstants.email,
                    editText_email.text.toString()
                )
                sharedPreferencesStorage.setValue(
                    AppConstants.password,
                    editText_password.text.toString()
                )
                mainViewModel.User_1_login()
            }


        }
    }


    fun login() {
        mainViewModel._res?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            Log.d("TAG@123",it.data?.user.toString())
                            sharedPreferencesStorage.setValue(AppConstants.IS_AUTHENTICATED, true)
                            sharedPreferencesStorage.setValue(AppConstants.USER_ID, res.user.id)
                            Log.d("TAG@123", res.user.id)
                            Toast.makeText(this@Login_Activity, res.message, Toast.LENGTH_LONG)
                                .show()
                            startActivity(Intent(this, Home::class.java))
                            finish()
                        } else {
                            sharedPreferencesStorage.setValue(AppConstants.IS_AUTHENTICATED, false)
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


    fun forgotPasswordCall(dialog: BottomSheetDialog) {

        mainViewModel.responsForgot?.observe(this, Observer {

            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {

                            dialog.dismiss()
                            Toast.makeText(this@Login_Activity, res.message, Toast.LENGTH_LONG)
                                .show()

                        } else {
                            dialog.dismiss()
                            Log.d("TAG123", "tags")
                             Toast.makeText(this@Login_Activity, res?.message, Toast.LENGTH_LONG).show()
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
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else {
            mCallbackManager?.onActivityResult(requestCode, resultCode, data);
        }


    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            Log.d("TAG@123",""+account.email)
            Log.d("TAG@123",""+account.id)
            Log.d("TAG@123",""+account.idToken)
            Log.d("TAG@123",""+account.photoUrl)
            Log.d("TAG@123",""+account.displayName)

            sharedPreferencesStorage.setValue(
                AppConstants.upload_image,
                ""+account.photoUrl.toString()
            )

            sharedPreferencesStorage.setValue(
                AppConstants.email,
                account.email
            )
            sharedPreferencesStorage.setValue(
                AppConstants.isSocialLogin,
                true
            )

            sharedPreferencesStorage.setValue(
                AppConstants.appleId,
                account.email
            )

            sharedPreferencesStorage.setValue(
                AppConstants.fisrtname,
                account.displayName
            )


            mainViewModel.Register()

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


    fun sent_otp() {
        mainViewModel.sent_otp?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {

                            sharedPreferencesStorage.setValue(AppConstants.IS_AUTHENTICATED, true)
                            Toast.makeText(this@Login_Activity, res.message, Toast.LENGTH_LONG)
                                .show()
                            phone_number_layout.visibility = View.GONE
                            button_login_email_phone_both.visibility = View.GONE
                            verfie_otp?.visibility = View.VISIBLE
                            editText_otp?.visibility = View.VISIBLE

                        } else {
                            sharedPreferencesStorage.setValue(AppConstants.IS_AUTHENTICATED, false)
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


    fun sosicl_login(){
        mainViewModel.registration?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Log.d("TAG@123",it.data?.user.toString())
                                if (it.data?.user==null){
                                    startActivity(Intent(this, OnBoardingActivity::class.java))
                                    finish()
                                }else{
                                    sharedPreferencesStorage.setValue(AppConstants.IS_AUTHENTICATED, true)
                                    sharedPreferencesStorage.setValue(AppConstants.USER_ID,  res.user.id)
                                    Toast.makeText(this@Login_Activity, res.message, Toast.LENGTH_LONG)
                                        .show()
                                    startActivity(Intent(this, Home::class.java))
                                    finish()}
                            }catch (e:Exception){
                                Log.d("TAG@123",e.message.toString())
                            }

                        } else {
                            sharedPreferencesStorage.setValue(AppConstants.IS_AUTHENTICATED, false)
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


    fun verifly_otp() {
        mainViewModel.verifly_otp?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Log.d("TAG@123",it.data?.user.toString())
                                if (it.data?.user==null){
                                    startActivity(Intent(this, OnBoardingActivity::class.java))
                                    finish()
                                }else{
                                sharedPreferencesStorage.setValue(AppConstants.IS_AUTHENTICATED, true)
                                sharedPreferencesStorage.setValue(AppConstants.USER_ID,  res.user.id)
                                Toast.makeText(this@Login_Activity, res.message, Toast.LENGTH_LONG)
                                    .show()
                                startActivity(Intent(this, Home::class.java))
                                finish()}
                            }catch (e:Exception){
                                Log.d("TAG@123",e.message.toString())
                            }

                        } else {
                            sharedPreferencesStorage.setValue(AppConstants.IS_AUTHENTICATED, false)
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


}
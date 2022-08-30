package com.SigmaDating.apk.views

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.SigmaDating.apk.AppReseources
import com.SigmaDating.apk.model.Pages
import com.SigmaDating.apk.storage.SharedPreferencesStorage
import com.SigmaDating.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class Home : AppCompatActivity() {
    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    val homeviewmodel: HomeViewModel by viewModels()
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setSupportActionBar(binding.toolbar)

        val navController = findNavController(com.SigmaDating.R.id.nav_host_fragment_content_home)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(com.SigmaDating.R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    public fun clearBackStack() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }


    fun initializeGoogleSignIn() {
        try {

            mGoogleSignInClient =
                GoogleSignIn.getClient(this, AppReseources.getGoogleSignInOptions()!!)
            mGoogleSignInClient.signOut().addOnCompleteListener {

                Log.d("TAG@123", "GoogleSignInOptions Logout ")
            }

        } catch (e: Exception) {
        }
    }


    fun OpenSocial(Url: String?) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(Url)
            startActivity(i)

        } catch (e: Exception) {
        }

    }


    companion object {
        lateinit var pages: ArrayList<Pages>
        var notifications_count: String="0"
        var current_user_profile: String=""
        var mCurrent_user_token:String=""
        var  match_id:String=""
        fun get_settingpage_data(alias: String): Pages? {
            for (i in 0..pages.size) {
                var page = pages.get(i)
                if (alias.equals(page.alias)) {
                    return page
                }
            }
            return null
        }

    }
}
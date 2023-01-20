package com.SigmaDating.app.views

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.SigmaDating.R
import com.SigmaDating.app.AppReseources
import com.SigmaDating.app.model.Bids
import com.SigmaDating.app.model.Pages
import com.SigmaDating.app.model.advertising_model
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
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
    private lateinit var firebaseAnalytics: FirebaseAnalytics

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

    override fun onBackPressed() {
        if (!sharedPreferencesStorage.getBoolean(AppConstants.Disclaimer)) {
            finish()
        }
        super.onBackPressed()
    }

    fun initializeGoogleSignIn() {
        try {
            mGoogleSignInClient =
                GoogleSignIn.getClient(this, AppReseources.getGoogleSignInOptions()!!)
            mGoogleSignInClient.signOut().addOnCompleteListener {

                Log.d("TAG@123", "GoogleSignInOptions Logout ")
            }

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(
                    "USER_LOGOUT",
                    sharedPreferencesStorage.getString(AppConstants.USER_ID)
                )

            }


        } catch (e: Exception) {
        }
    }


    /* override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
         if (keyCode == KeyEvent.KEYCODE_BACK) {
             (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
                 InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY
             )
         }
         return super.onKeyUp(keyCode, event)
     }

     override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
         if (keyCode == KeyEvent.KEYCODE_BACK) {
             (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
                 InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY
             )
         }
         return super.onKeyDown(keyCode, event)
     }*/

    companion object {
        lateinit var pages: ArrayList<Pages>
        lateinit var ads_list: ArrayList<advertising_model>
        var notifications_count: String = "0"
        var current_user_profile: String = ""
        var mCurrent_user_token: String = ""
        var mVideoGrant_user_token: String = ""
        var share_app_text: String = ""
        var safety_message_text: String = ""

        var match_id: String = ""
        var sender_id: String = ""
        var chatFlag: Boolean = false
        var toastflag: Boolean = false
        var show_block: Boolean = false
        var ads_list_index: Int = 0

        fun get_settingpage_data(alias: String): Pages? {
            for (i in 0..pages.size) {
                val page = pages.get(i)
                if (alias.equals(page.alias)) {
                    return page
                }
            }
            return null
        }
    }

    override fun onResume() {
        super.onResume()
        do_sent_firebaselog("USER_ID", sharedPreferencesStorage.getString(AppConstants.USER_ID))
    }


    private fun do_sent_firebaselog(event_name: String, event_log: String) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(
                event_name,
                event_log
            )
        }
    }


}
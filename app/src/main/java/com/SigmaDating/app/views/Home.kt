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
import com.SigmaDating.app.model.Pages
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
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
        var notifications_count: String="0"
        var current_user_profile: String=""
        var mCurrent_user_token:String=""
        var mVideoGrant_user_token:String=""
        var  match_id:String=""
        var sender_id:String=""
        var chatFlag:Boolean=false

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
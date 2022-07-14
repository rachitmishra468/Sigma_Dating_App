package com.SigmaDating.apk.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.SigmaDating.databinding.ActivityHomeBinding
import com.SigmaDating.apk.storage.SharedPreferencesStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.android.gms.common.api.GoogleApiClient
import android.R
import android.util.Log
import android.view.View
import com.SigmaDating.apk.AppReseources

import com.google.android.gms.common.SignInButton

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status


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

             mGoogleSignInClient= GoogleSignIn.getClient(this,AppReseources.getGoogleSignInOptions()!!)
             mGoogleSignInClient.signOut().addOnCompleteListener {

                 Log.d("TAG@123","GoogleSignInOptions Logout ")
             }

         }catch (e:Exception){}
    }
}
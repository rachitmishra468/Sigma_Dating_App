package com.sigmadatingapp.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sigmadatingapp.R
import com.sigmadatingapp.databinding.ActivityHomeBinding
import com.sigmadatingapp.storage.SharedPreferencesStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Home : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    val homeviewmodel:HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

       setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_home)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    public fun clearBackStack() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }
}
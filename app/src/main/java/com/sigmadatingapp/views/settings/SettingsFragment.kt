package com.sigmadatingapp.views.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sigmadatingapp.databinding.FragmentSettingsBinding
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.views.Home
import com.sigmadatingapp.views.Splash
import com.sigmadatingapp.views.intro_registration.OnBoardingActivity

class SettingsFragment : Fragment() {

    lateinit var _binding: FragmentSettingsBinding



    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentSettingsBinding.inflate(inflater, container, false)
        _binding.imageView2.setOnClickListener {
         //  findNavController().navigate(R.id.action_settings_frag_to_SecondFragment)
            (activity as Home).onBackPressed()
        }

        _binding.continueLogout.setOnClickListener{
            (activity as Home?)?.sharedPreferencesStorage?.setValue(AppConstants.IS_AUTHENTICATED,false)
            startActivity(Intent(requireContext(), Splash::class.java))
            (activity as Home?)?.finish()
        }
        return binding.root
    }

}
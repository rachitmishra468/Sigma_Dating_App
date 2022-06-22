package com.sigmadatingapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sigmadatingapp.R
import com.sigmadatingapp.databinding.FragmentSecondBinding
import com.sigmadatingapp.databinding.FragmentSettingsBinding

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
        return binding.root
    }

}
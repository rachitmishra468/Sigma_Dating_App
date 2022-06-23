package com.sigmadatingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sigmadatingapp.databinding.FragmentSecondBinding
import com.sigmadatingapp.databinding.NotificationSettingFragmentBinding

class NotificationSettingsFragment  :Fragment(){
    lateinit var _binding: NotificationSettingFragmentBinding
    private val binding get() = _binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       // val view= inflater.inflate(R.layout.notification_setting_fragment, container, false)
        _binding = NotificationSettingFragmentBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
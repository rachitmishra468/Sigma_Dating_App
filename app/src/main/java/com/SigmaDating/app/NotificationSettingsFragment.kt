package com.SigmaDating.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.SigmaDating.databinding.NotificationSettingFragmentBinding
import com.bumptech.glide.Glide


class NotificationSettingsFragment  :Fragment(){
    lateinit var _binding: NotificationSettingFragmentBinding
    private val binding get() = _binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = NotificationSettingFragmentBinding.inflate(inflater, container, false)

      val   username =  getArguments()?.getString("user_id")
        if (username!=null) {
            _binding.texttitle.text=username
        }
       val  imagedata = getArguments()?.getString("user_image")
        imagedata?.let {
            Glide.with(requireActivity()).load(it).into(_binding.imgheader as ImageView)
        }

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}
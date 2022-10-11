package com.SigmaDating.app.views.intro_registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.SigmaDating.R
import com.SigmaDating.databinding.FragmentOtpVerificationBinding


class Otp_verification : Fragment() {

    private lateinit var _binding:FragmentOtpVerificationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        _binding.emailButtonVerification.setOnClickListener {
            _binding.emailButtonVerification.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            _binding.phoneNumberVerification.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            _binding.emailButtonVerification.setTextColor(this.getResources().getColor(R.color.black))
            _binding.phoneNumberVerification.setTextColor(this.getResources().getColor(R.color.white))
        }

        _binding.phoneNumberVerification.setOnClickListener {
            _binding.phoneNumberVerification.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            _binding.emailButtonVerification.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            _binding.phoneNumberVerification.setTextColor(this.getResources().getColor(R.color.black))
            _binding.emailButtonVerification.setTextColor(this.getResources().getColor(R.color.white))
        }
        _binding.verificationDone.setOnClickListener {
            (activity as OnBoardingActivity?)?.setCurrentItem(5, true)
        }

        return _binding.root
    }






    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Otp_verification().apply {

            }
    }
}
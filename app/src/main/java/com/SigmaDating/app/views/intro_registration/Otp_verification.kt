package com.SigmaDating.app.views.intro_registration

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.SigmaDating.R
import com.SigmaDating.app.model.Loginmodel
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.databinding.FragmentOtpVerificationBinding
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status


class Otp_verification : Fragment() {

    private lateinit var _binding: FragmentOtpVerificationBinding
    private var mUser_Verification: Boolean = false
    private var email = ""
    private var phone_otp_send: Boolean = false
    private var email_otp_send: Boolean = false
    private var email_old = ""
    private var phone_old = ""
    lateinit var constraint_f1: ConstraintLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        constraint_f1 = _binding!!.root.findViewById(R.id.constraint_f1)
        _binding.emailButtonVerification.setOnClickListener {
            _binding.editTextOtpVerification.setText("")
            _binding.emailButtonVerification.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            _binding.phoneNumberVerification.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            _binding.emailButtonVerification.setTextColor(
                this.getResources().getColor(R.color.black)
            )
            _binding.phoneNumberVerification.setTextColor(
                this.getResources().getColor(R.color.white)
            )
            email = "email"
            if (!email_otp_send) {
                (activity as OnBoardingActivity?)?.userRegister?.sent_otp =
                    MutableLiveData<Resource<Loginmodel>>()
                (activity as OnBoardingActivity?)?.userRegister?.verification_phone_email(false)
                sent_otp(false)
            } else {
                _binding.editTextOtpVerification.visibility = View.VISIBLE
                _binding.verfieOtp.visibility = View.VISIBLE
                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "Already OTP Sent to Your Email ."
                )
            }
        }

        _binding.phoneNumberVerification.setOnClickListener {
            _binding.editTextOtpVerification.setText("")
            _binding.phoneNumberVerification.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            _binding.emailButtonVerification.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            _binding.phoneNumberVerification.setTextColor(
                this.getResources().getColor(R.color.black)
            )
            _binding.emailButtonVerification.setTextColor(
                this.getResources().getColor(R.color.white)
            )
            email = "phone"
            if (!phone_otp_send) {
                (activity as OnBoardingActivity?)?.userRegister?.sent_otp =
                    MutableLiveData<Resource<Loginmodel>>()
                (activity as OnBoardingActivity?)?.userRegister?.verification_phone_email(true)
                sent_otp(true)
            } else {
                _binding.editTextOtpVerification.visibility = View.VISIBLE
                _binding.verfieOtp.visibility = View.VISIBLE
                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "Already OTP Sent to Your Phone Number ."
                )
            }
        }

        _binding.verificationDone.setOnClickListener {
            if (mUser_Verification) {
                (activity as OnBoardingActivity?)?.setCurrentItem(5, true)
            } else {
                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "OTP Verification not Completed!",
                )
            }
        }

        _binding.verfieOtp.setOnClickListener {
            if (_binding.editTextOtpVerification.text.toString().equals("")) {

                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "Enter OTP ..",
                )
            } else {
                (activity as OnBoardingActivity?)?.userRegister?.verifly_otp =
                    MutableLiveData<Resource<Loginmodel>>()
                if (email.equals("email")) {
                    (activity as OnBoardingActivity?)?.userRegister?.verifly_OTP(
                        _binding.editTextOtpVerification.text.toString(),
                        false
                    )
                } else {
                    (activity as OnBoardingActivity?)?.userRegister?.verifly_OTP(
                        _binding.editTextOtpVerification.text.toString(),
                        true
                    )
                }
                verifly_otp()
            }
        }
        return _binding.root
    }

    fun sent_otp(flag: Boolean) {
        (activity as OnBoardingActivity?)?.userRegister?.sent_otp?.observe(
            requireActivity(),
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {

                                AppUtils.showErrorSnackBar(
                                    requireContext(),
                                    constraint_f1,
                                    res.message,
                                )
                                Log.d("TAG@123", res.message)
                                _binding.editTextOtpVerification.visibility = View.VISIBLE
                                _binding.verfieOtp.visibility = View.VISIBLE
                                if (flag) {
                                    phone_otp_send = true
                                } else {
                                    email_otp_send = true
                                }

                            } else {
                                Log.d("TAG@123", res!!.message)
                                AppUtils.showErrorSnackBar(
                                    requireContext(),
                                    constraint_f1,
                                    res!!.message
                                )

                                _binding.editTextOtpVerification.visibility = View.GONE
                                _binding.verfieOtp.visibility = View.GONE
                            }
                        }
                    }
                    Status.LOADING -> {
                        AppUtils.showLoader(requireContext())
                    }
                    Status.ERROR -> {

                    }
                }
            })

    }


    fun verifly_otp() {
        (activity as OnBoardingActivity?)?.userRegister?.verifly_otp?.observe(
            requireActivity(),
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                                try {
                                    mUser_Verification = true
                                    phone_otp_send = false
                                    email_otp_send = false
                                    (activity as OnBoardingActivity?)?.setCurrentItem(6, true)
                                    //  _binding.verificationDone.setBackgroundResource(R.drawable.signup_circle_bg)
                                    //  _binding.verificationDone.setTextColor(resources.getColor(R.color.white))
                                    // Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG).show()
                                    Log.d("TAG@123", res.message)
                                } catch (e: Exception) {
                                    Log.d("TAG@123", e.message.toString())
                                }
                            } else {
                                _binding.editTextOtpVerification.setText("")
                                if (res != null) {

                                    AppUtils.showErrorSnackBar(
                                        requireContext(),
                                        constraint_f1,
                                        res.message,
                                    )
                                }
                                res?.let { it1 -> Log.d("TAG@123", it1.message) }
                            }
                        }
                    }
                    Status.LOADING -> {
                        AppUtils.showLoader(requireContext())
                    }
                    Status.ERROR -> {

                    }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        /*  if (email_old.equals(
                  (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.getString(
                      AppConstants.email
                  )
              )
          ) {
              email_old = (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.getString(
                  AppConstants.email
              ).toString()

          } else {

              mUser_Verification = false
              email = ""
              phone_otp_send = false
              email_otp_send = false

          }
          if (phone_old.equals(
                  (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.getString(
                      AppConstants.phone
                  )
              )
          ) {
              phone_old =
                  (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.getString(AppConstants.phone)
                      .toString()
          } else {

              mUser_Verification = false
              email = ""
              phone_otp_send = false
              email_otp_send = false
          }*/

        mUser_Verification = false
        _binding.editTextOtpVerification.setText("")
        _binding.editTextOtpVerification.visibility = View.INVISIBLE
        _binding.verfieOtp.visibility = View.INVISIBLE
        _binding.emailButtonVerification.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
        _binding.phoneNumberVerification.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
        _binding.emailButtonVerification.setTextColor(this.getResources().getColor(R.color.white))
        _binding.phoneNumberVerification.setTextColor(this.getResources().getColor(R.color.white))

        if (email_otp_send || phone_otp_send) {
            _binding.editTextOtpVerification.visibility = View.VISIBLE
            _binding.verfieOtp.visibility = View.VISIBLE

            if (email.equals("email")) {
                _binding.emailButtonVerification.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
                _binding.phoneNumberVerification.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
                _binding.emailButtonVerification.setTextColor(
                    this.getResources().getColor(R.color.black)
                )
                _binding.phoneNumberVerification.setTextColor(
                    this.getResources().getColor(R.color.white)
                )
            } else {
                _binding.phoneNumberVerification.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
                _binding.emailButtonVerification.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
                _binding.phoneNumberVerification.setTextColor(
                    this.getResources().getColor(R.color.black)
                )
                _binding.emailButtonVerification.setTextColor(
                    this.getResources().getColor(R.color.white)
                )
            }

        }

        Log.d("TAG@123", "onResume")
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Otp_verification().apply {

            }
    }
}
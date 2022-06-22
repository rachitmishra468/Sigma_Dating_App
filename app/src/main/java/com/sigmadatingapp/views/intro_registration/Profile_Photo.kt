package com.sigmadatingapp.views.intro_registration

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.demoapp.other.Status
import com.sigmadatingapp.R
import com.sigmadatingapp.storage.SharedPreferencesStorage
import com.sigmadatingapp.utilities.AppUtils
import com.sigmadatingapp.views.Home
import com.sigmadatingapp.views.login.LoginViewModel
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Profile_Photo : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var profile_continue: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_profile__photo, container, false)

        profile_continue = view.findViewById(R.id.profile_continue)
        profile_continue?.setOnClickListener {

            // startActivity(Intent(context, Home::class.java))
            //(activity as OnBoardingActivity?)?.finish
            if (AppUtils.isNetworkInterfaceAvailable(requireContext())) {
                Register()
            }

        }


        return view;
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile_Photo().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    fun Register() {
        (activity as OnBoardingActivity?)?.userRegister?.res?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            startActivity(Intent(context, Home::class.java))
                            (activity as OnBoardingActivity?)?.finish()
                            Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG).show()
                        } else {

                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                .show()
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

}
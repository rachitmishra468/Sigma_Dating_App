package com.sigmadatingapp.views.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.demoapp.other.Status
import com.sigmadatingapp.R
import com.sigmadatingapp.databinding.FragmentEditProfileBinding
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.utilities.AppUtils
import com.sigmadatingapp.views.Home

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditProfile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding:FragmentEditProfileBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding= FragmentEditProfileBinding.inflate(inflater, container, false)
        _binding?.imageView2?.setOnClickListener {
            (activity as Home).onBackPressed()
        }

        subscribe_Login_User_details()
        (activity as Home).homeviewmodel.get_Login_User_details((activity as Home).sharedPreferencesStorage.getString(
            AppConstants.USER_ID))
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }


    fun subscribe_Login_User_details(){
        (activity as Home?)?.homeviewmodel?.get_user_data?.observe(this, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            Log.d("TAG@123",res.toString())


                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG).show()
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
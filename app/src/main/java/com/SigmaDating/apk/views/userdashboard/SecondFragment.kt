package com.SigmaDating.apk.views.userdashboard

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.SigmaDating.R
import com.SigmaDating.apk.AppReseources
import com.SigmaDating.apk.adapters.Profile_Adapter


import com.SigmaDating.apk.model.EditProfiledata
import com.SigmaDating.apk.model.home_model
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.views.Home
import com.SigmaDating.databinding.FragmentSecondBinding
import com.bumptech.glide.Glide
import com.example.demoapp.other.Status
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class SecondFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentSecondBinding? = null
    private lateinit var photoAdapter: Profile_Adapter
    private var dataList = mutableListOf<EditProfiledata>()
    private var dataListuser = mutableListOf<String>()

    private val binding get() = _binding!!
    lateinit var chatIcon: ImageView
    private var name_text: TextView? = null
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    private var userID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG@123", " SecondFragment onCreateView")
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        footer_transition()
        _binding?.editProfile?.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_editprofile)
        }
        _binding?.settingIcon?.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_settings)
        }
        _binding?.movetonotification?.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_notification)
        }
        return binding.root

    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG@123", " onStop")

    }

    override fun onPause() {
        super.onPause()
        Log.d("TAG@123", " onPause")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG@123", " onDestroy")
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG@123", " onResume")
        userID = getArguments()?.getString("user_id")
        if (userID == null) {
            userID = (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        }
        if (userID != null) {
            (activity as Home).homeviewmodel.get_secound_feb_User_details(
                userID!!
            )
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            subscribe_Login_User_details()
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("TAG@123", " onDetach")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG@123", " SecondFragment onViewCreated")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        Log.d("TAG@123", " SecondFragment onCreate")

    }

    fun setAdapterListData(dataListuser: ArrayList<String>) {
        _binding?.recyclerView?.layoutManager = GridLayoutManager(AppReseources.getAppContext(), 3)
        photoAdapter = Profile_Adapter(requireContext())
        _binding?.recyclerView?.adapter = photoAdapter
        photoAdapter.setDataList(dataListuser)
        Log.d("TAG@123", " setAdapterListData  ${dataListuser.size}")
    }

    fun subscribe_Login_User_details() {
        (activity as Home?)?.homeviewmodel?.get_secound_feb_data?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let { res ->
                        if (res?.status == true) {
                            Log.d("TAG@123", "111 " + it.data?.user.toString())
                            _binding?.let {
                                it.nameText.setText(res?.user.first_name + res?.user.last_name)
                                it.addresText.setText(res?.user.location)
                            }

                            it.data?.user?.upload_image?.let {
                                Glide.with(AppReseources.getAppContext()!!).load(it)
                                    .error(R.drawable.profile_img)
                                    .into(_binding!!.logoDetail);
                            }
                            dataListuser.clear()
                            if (!res.user.photos.isNullOrEmpty()) {
                                dataListuser = res.user.photos
                            }

                            setAdapterListData(dataListuser as ArrayList<String>)
                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
                Status.LOADING -> {


                    Log.d("TAG@123", "LOADING is null")
                }
                Status.ERROR -> {

                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()


    }


    fun footer_transition() {
        chatIcon = binding.root.findViewById(R.id.chat_Icon)
        match_list = binding.root.findViewById(R.id.match_list)
        sigma_list = binding.root.findViewById(R.id.sigma_list)

        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_solid))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_disable))
        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_disable))

        chatIcon.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_chat)
        }

        match_list.setOnClickListener {
            // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        sigma_list.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }

}
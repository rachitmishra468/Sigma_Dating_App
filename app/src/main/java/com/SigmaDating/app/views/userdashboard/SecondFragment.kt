package com.SigmaDating.app.views.userdashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.SigmaDating.R
import com.SigmaDating.app.AppReseources
import com.SigmaDating.app.adapters.Profile_Adapter


import com.SigmaDating.app.model.EditProfiledata
import com.SigmaDating.app.model.Postdata
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
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
    private var dataListuser = listOf<Postdata>()

    private val binding get() = _binding!!
    lateinit var chatIcon: ImageView
    private var name_text: TextView? = null
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    lateinit var greekLatter:TextView
    private var userID: String? = null
    private var name: String? = null
    private var photo: String? = null
    lateinit var empty_text_view: TextView
    lateinit var empty_item_layout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG@123", " SecondFragment onCreateView")
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        empty_text_view = binding.root.findViewById(R.id.empty_text_view)
        empty_item_layout = binding.root.findViewById(R.id.empty_item_layout)
        empty_item_layout.visibility = View.GONE
        footer_transition()
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
        _binding?.editProfile?.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_editprofile)
        }
        if(!userID.equals((activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID))){
            _binding?.fab?.visibility=View.GONE
            _binding?.profileImg?.visibility=View.VISIBLE
        }
        else{
            _binding?.profileImg?.visibility=View.INVISIBLE
        }
        _binding?.fab?.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_createpost)
        }
        _binding?.settingIcon?.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_settings)
        }
        _binding?.movetonotification?.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_notification)
        }
        _binding?.profileImg?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user_name", name)
            bundle.putString("user_image",photo)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_chatListFragment_to_userChatFragment, bundle,
                    null,
                    null);

           // findNavController().navigate(R.id.action_chatListFragment_to_userChatFragment)
        }

        _binding?.comments?.setOnClickListener {
            val bundle = Bundle()
             bundle.putString("user_id",userID)
             findNavController().navigate(R.id.action_SecondFragment_to_Report_feb, bundle)

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

        CoroutineScope(Dispatchers.Main).launch {
            subscribe_Login_User_details()
        }


        Home.notifications_count.let {
            _binding?.tvCounter?.setText(Home.notifications_count)
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

    fun setAdapterListData(dataListuser: ArrayList<Postdata>) {


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
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            Log.d("TAG@123", "111 " + it.data?.user.toString())
                            _binding?.let {
                                it.nameText.setText(res?.user.first_name +" "+ res.user.last_name)
                                it.addresText.setText(res?.user.university)
                                it.ageText.setText(""+res?.user.age)


                            }

                            if(it.data!!.user.greekletter.length>0) {
                                greekLatter.text = it.data.user.greekletter
                                greekLatter.visibility=View.VISIBLE
                            }
                            else{
                                greekLatter.visibility=View.GONE
                            }



                            it.data.user.upload_image.let {
                                Glide.with(AppReseources.getAppContext()!!).load(it)
                                    .error(R.drawable.profile_img)
                                    .into(_binding!!.logoDetail);
                            }
                            name=res.user.first_name +" "+ res.user.last_name
                            photo=it.data.user.upload_image
                            if (!res.posts.isNullOrEmpty()) {
                                dataListuser = res.posts
                                setAdapterListData(dataListuser as ArrayList<Postdata>)
                            }else{
                                    empty_text_view.text = res.post_message
                                    empty_item_layout.visibility = View.VISIBLE
                                    Log.d("TAG@123", " empty_text  Show")

                            }

                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
                Status.LOADING -> {

                    AppUtils.showLoader(requireContext())
                    Log.d("TAG@123", "LOADING is null")
                }
                Status.ERROR -> {
                    AppUtils.hideLoader()
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
        greekLatter= binding.root.findViewById(R.id.greek_latter)

        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_solid))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_disable))
        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_disable))

        chatIcon.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_chat)
        }

        match_list.setOnClickListener {
            AppUtils.animateImageview(match_list)
            // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        sigma_list.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }

}
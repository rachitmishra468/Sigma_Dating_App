package com.SigmaDating.apk.views.post

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.SigmaDating.R
import com.SigmaDating.apk.AppReseources
import com.SigmaDating.apk.adapters.PostAdapter
import com.SigmaDating.apk.adapters.Profile_Adapter
import com.SigmaDating.apk.model.Loginmodel
import com.SigmaDating.apk.model.Postdata
import com.SigmaDating.apk.model.post
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.views.Home
import com.SigmaDating.databinding.FragmentCreatePostBinding
import com.SigmaDating.databinding.FragmentPostListBinding
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.gson.JsonObject

class PostList : Fragment() {

    private var _binding: FragmentPostListBinding?=null
    private val binding get() = _binding!!
    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    lateinit var greekLatter: TextView
    private lateinit var photoAdapter: PostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentPostListBinding.inflate(inflater, container, false)
        footer_transition()
        (activity as Home).homeviewmodel.All_post= MutableLiveData<Resource<post>>()
        subscribe_create_post()
        val jsonObject = JsonObject()
        Log.d(
            "TAG@123",
            (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
        )
        jsonObject.addProperty(
            "user_id",
            (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
        )

        (activity as Home).homeviewmodel.getAllPost(jsonObject)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    fun setAdapterListData(dataListuser: ArrayList<Postdata>) {
        _binding?.postRecyclerview?.layoutManager =  LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        photoAdapter = PostAdapter(requireContext())
        _binding?.postRecyclerview?.adapter = photoAdapter
        photoAdapter.setDataList(dataListuser)
        Log.d("TAG@123", " setAdapterListData  ${dataListuser.size}")
    }


    fun subscribe_create_post() {
        (activity as Home?)?.homeviewmodel?.All_post?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                               setAdapterListData(res.data as ArrayList<Postdata>)
                            } else {

                            }
                        }
                    }
                    Status.LOADING -> {
                        AppUtils.showLoader(requireContext())
                    }
                    Status.ERROR -> {
                        AppUtils.hideLoader()
                    }
                }
            })
    }

    fun footer_transition() {
        chatIcon = binding.root.findViewById(R.id.chat_Icon)
        match_list = binding.root.findViewById(R.id.match_list)
        sigma_list = binding.root.findViewById(R.id.sigma_list)
       // greekLatter= binding.root.findViewById(R.id.greek_latter)

        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_disable))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_disable))
        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_enable))

        chatIcon.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_chat)
        }

        match_list.setOnClickListener {
            //AppUtils.animateImageview(match_list)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        sigma_list.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }


}
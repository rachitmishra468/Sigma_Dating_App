package com.SigmaDating.app.views.post

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.SigmaDating.R
import com.SigmaDating.app.adapters.PostAdapter
import com.SigmaDating.app.model.Loginmodel
import com.SigmaDating.app.model.Postdata
import com.SigmaDating.app.model.delelepost
import com.SigmaDating.app.model.post
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.SigmaDating.databinding.FragmentPostListBinding
import com.bumptech.glide.Glide
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import de.hdodenhof.circleimageview.CircleImageView

class PostList : Fragment(), PostAdapter.OnItemClickListener {

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!
    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    lateinit var user_profile_photo: CircleImageView
    private var userID: String? = null
    private lateinit var photoAdapter: PostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        footer_transition()
        get_postdata()
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

    fun setAdapterListData(booleantype: Boolean, dataListuser: ArrayList<Postdata>) {
        _binding?.postRecyclerview?.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        photoAdapter = PostAdapter(booleantype, this, requireContext())
        _binding?.postRecyclerview?.adapter = photoAdapter
        photoAdapter.setDataList(dataListuser)
        photoAdapter.notifyDataSetChanged()
        Log.d("TAG@123", " setAdapterListData  ${dataListuser.size}")
    }

    fun getUserisSame(): Boolean {
        return !userID.equals((activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID))

    }
    fun subscribe_save_post_like() {
        (activity as Home?)?.homeviewmodel?.like_post?.observe(
            viewLifecycleOwner,
            Observer { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                                Log.d("TAG@123",res.message)
                                Toast.makeText(requireContext(),res.message,Toast.LENGTH_SHORT).show()
                            }else{
                                if (res != null) {
                                    Log.d("TAG@123",res.message)
                                    Toast.makeText(requireContext(),res.message,Toast.LENGTH_SHORT).show()
                                }

                            }
                        }
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {

                    }
                }
            })
    }


    fun subscribe_create_post() {
        (activity as Home?)?.homeviewmodel?.All_post?.observe(
            viewLifecycleOwner,
            Observer { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {

                                if (!userID.equals(
                                        (activity as Home).sharedPreferencesStorage.getString(
                                            AppConstants.USER_ID
                                        )
                                    )
                                ) {
                                    setAdapterListData(false, res.data as ArrayList<Postdata>)
                                } else {
                                    setAdapterListData(true, res.data as ArrayList<Postdata>)

                                }

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

    fun deletePostObserverResponse() {
        (activity as Home?)?.homeviewmodel?.delete_post?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        get_postdata()
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

    override fun onDelete(position: Postdata,flag:Int) {
        when(flag){
            1->{
                val bundle = Bundle()
                bundle.putString("post_id",position.id)
                bundle.putString("user_name",position.first_name+" "+position.last_name)
                bundle.putString("user_img",position.upload_image)
                bundle.putString("comment_title",position.title)
                bundle.putString("media",position.media)
                findNavController().navigate(R.id.action_FirstFragment_to_comment,bundle)
            }
            2->{
                save_post_like(position.id)
            }
            3->{
                alertDeletepopup(position)

            }
        }



    }

    fun footer_transition() {
        chatIcon = binding.root.findViewById(R.id.chat_Icon)
        match_list = binding.root.findViewById(R.id.match_list)
        sigma_list = binding.root.findViewById(R.id.sigma_list)
        user_profile_photo = binding.root.findViewById(R.id.user_profile_photo)
        Home.notifications_count.let {
            _binding?.tvCounter?.text = it
        }
        Home.current_user_profile.let {
            Glide.with(requireContext()).load(it)
                .error(R.drawable.profile_img)
                .into(user_profile_photo);
        }

        _binding?.movetonotification?.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_notification)
        }


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


    fun get_postdata() {
        (activity as Home).homeviewmodel.All_post = MutableLiveData<Resource<post>>()
        subscribe_create_post()
        val jsonObject = JsonObject()
        userID = getArguments()?.getString("user_id")
        if (userID == null) {
            userID = (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        }
        Log.d("TAG@123", userID + "")
        jsonObject.addProperty("user_id", userID)
        (activity as Home).homeviewmodel.getAllPost(jsonObject)
    }

    fun save_post_like(post_id:String) {
        (activity as Home).homeviewmodel.like_post = MutableLiveData<Resource<Loginmodel>>()
        subscribe_save_post_like()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userID)
        jsonObject.addProperty("post_id", post_id)
        (activity as Home).homeviewmodel.save_like_post_data(jsonObject)
    }

    fun alertDeletepopup(position: Postdata) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Are you want to Delete this Post.")
        builder.background = ColorDrawable(
            Color.parseColor("#FFFFFF")
        )
        builder.setPositiveButton("Yes") { dialog, which ->

            val jsonObject = JsonObject()
            Log.d("TAG@123", position.id + "")
            (activity as Home).homeviewmodel.delete_post = MutableLiveData<Resource<delelepost>>()
            deletePostObserverResponse()
            jsonObject.addProperty("id", position.id)
            (activity as Home).homeviewmodel.deletepost(jsonObject)
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }


}
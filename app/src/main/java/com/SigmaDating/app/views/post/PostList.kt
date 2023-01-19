package com.SigmaDating.app.views.post

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.SigmaDating.R
import com.SigmaDating.app.adapters.PostAdapter
import com.SigmaDating.app.model.*
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.SigmaDating.databinding.FragmentPostListBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.gson.JsonObject
import de.hdodenhof.circleimageview.CircleImageView

class PostList : Fragment(), PostAdapter.OnItemClickListener {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!
    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    lateinit var user_profile_photo: CircleImageView
    private var userID: String? = null
    private lateinit var photoAdapter: PostAdapter
    lateinit var empty_text_view: TextView
    lateinit var empty_item_layout: LinearLayout

    //Ad

    lateinit var ad_video: VideoView
    lateinit var close_ad_img: ImageView
    lateinit var ad_main: ConstraintLayout
    lateinit var ads_image_view: ImageView
    lateinit var progress_bar_ads: ProgressBar
    lateinit var skip_text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)

        empty_text_view = binding.root.findViewById(R.id.empty_text_view)
        empty_item_layout = binding.root.findViewById(R.id.empty_item_layout)
        empty_item_layout.visibility = View.GONE

        footer_transition()
        get_postdata()

        (activity as Home).homeviewmodel.app_ads =
            MutableLiveData<Resource<advertisingData>>()
        (activity as Home).homeviewmodel.get_ads_list("feedscreen")
        ad_main = binding.root.findViewById(R.id.ad_main)
        ad_main.visibility = View.GONE
        subscribe_app_ads()

        //Ad view
        progress_bar_ads = binding.root.findViewById(R.id.progress_bar_ads)
        ads_image_view = binding.root.findViewById(R.id.ads_image_view)
        skip_text = binding.root.findViewById(R.id.skip_text)
        ad_main.visibility = View.VISIBLE
        close_ad_img = binding.root.findViewById(R.id.close_ad_img)
        ad_video = binding.root.findViewById(R.id.videoview)

        _binding!!.userProfilePhoto.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        close_ad_img.setOnClickListener {
            if (ad_video.isPlaying) {
                ad_video.stopPlayback()
            }
            ad_main.visibility = View.GONE
        }

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

    private fun setAdapterListData(
        booleantype: Boolean,
        dataListuser: ArrayList<Postdata>,
        mess: String
    ) {

        var list :ArrayList<Postdata> = arrayListOf()

        if (dataListuser.size == 0) {
            empty_text_view.text = mess
            empty_item_layout.visibility = View.VISIBLE
            Log.d("TAG@123", " empty_text  Show")
        }

        if (!booleantype) {
            dataListuser.forEach {
                if(it.isPrivate!="0"){
                    list.add(it)
                }
            }
        }else{
            list.addAll(dataListuser)
        }
        _binding?.postRecyclerview?.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        photoAdapter = PostAdapter(booleantype, this, requireContext())
        _binding?.postRecyclerview?.adapter = photoAdapter
        photoAdapter.setDataList(list)
        photoAdapter.notifyDataSetChanged()
        Log.d("TAG@123", " setAdapterListData  ${dataListuser.size}")
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
                                Log.d("TAG@123", res.message)
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                if (res != null) {
                                    Log.d("TAG@123", res.message)
                                    Toast.makeText(
                                        requireContext(),
                                        res.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                        if (it.data!!.status) {
                            if (!userID.equals(
                                    (activity as Home).sharedPreferencesStorage.getString(
                                        AppConstants.USER_ID
                                    )
                                )
                            ) {


                                setAdapterListData(
                                    false,
                                    it.data.data as ArrayList<Postdata>,
                                    it.data.message
                                )
                            } else {
                                setAdapterListData(
                                    true,
                                    it.data.data as ArrayList<Postdata>,
                                    it.data.message
                                )

                            }

                        } else {


                            empty_text_view.text = it.data.message
                            empty_item_layout.visibility = View.VISIBLE
                            Log.d("TAG@123", " empty_text  Show")

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


    fun PostStatusObserverResponse() {
        (activity as Home?)?.homeviewmodel?.change_status_post?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        Log.d("TAG@123", "Poast Hide Status :-" + it.message)
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


    fun deletePostObserverResponse() {
        (activity as Home?)?.homeviewmodel?.delete_post?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        Log.d("TAG@123", "Delete Post Status :-" + it.message)
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

    override fun onDelete(position: Postdata, flag: Int) {
        when (flag) {
            1 -> {
                val bundle = Bundle()
                bundle.putString("post_id", position.id)
                bundle.putString("user_name", position.first_name + " " + position.last_name)
                bundle.putString("user_img", position.upload_image)
                bundle.putString("comment_title", position.title)
                bundle.putString("media", position.media)
                findNavController().navigate(R.id.action_FirstFragment_to_comment, bundle)
                do_sent_firebaselog("comment_post", position.first_name + " " + position.last_name)

            }
            2 -> {
                save_post_like(position.id)
                do_sent_firebaselog("like_post", position.first_name + " " + position.id)

            }
            3 -> {
                alertDeletepopup(position)

            }
            4 -> {
                val jsonObject = JsonObject()
                Log.d("TAG@123", position.id + "")
                (activity as Home).homeviewmodel.change_status_post =
                    MutableLiveData<Resource<delelepost>>()
                PostStatusObserverResponse()
                jsonObject.addProperty(
                    "user_id",
                    (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                )
                jsonObject.addProperty("post_id", position.id)
                jsonObject.addProperty("isPrivate", "1")
                Log.d("TAG@123", "Poast Status :-" + jsonObject.toString())
                (activity as Home).homeviewmodel.PostStatusChange(jsonObject)

            }
            5 -> {
                val jsonObject = JsonObject()
                Log.d("TAG@123", position.id + "")
                (activity as Home).homeviewmodel.change_status_post =
                    MutableLiveData<Resource<delelepost>>()
                PostStatusObserverResponse()
                jsonObject.addProperty(
                    "user_id",
                    (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                )
                jsonObject.addProperty("post_id", position.id)
                jsonObject.addProperty("isPrivate", "0")
                Log.d("TAG@123", "Poast Status :-" + jsonObject.toString())
                (activity as Home).homeviewmodel.PostStatusChange(jsonObject)

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

    fun save_post_like(post_id: String) {
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


    fun subscribe_app_ads() {
        (activity as Home?)?.homeviewmodel?.app_ads?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    ad_main.visibility = View.VISIBLE
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Log.d("TAG@123", "ads data count  :" + it.data.toString())
                                Home.ads_list = it.data?.ads as ArrayList<advertising_model>
                                if (Home.ads_list.isNotEmpty()) {
                                    Home.ads_list_index = 0
                                    start_ads_listing(Home.ads_list)
                                }
                            } catch (e: Exception) {
                                Log.d("TAG@123", "Exception  :" + e.message.toString())
                            }
                        }

                    }
                }
                Status.LOADING -> {
                    ad_main.visibility = View.GONE
                }
                Status.ERROR -> {
                    ad_main.visibility = View.GONE

                }
            }
        })

    }


    fun start_ads_listing(list: ArrayList<advertising_model>) {
        Log.d("TAG@123", "start_ads_listing")

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (Home.ads_list_index == list.size) {
                    Home.ads_list_index = 0
                }
                if (list[Home.ads_list_index].type.equals("image")) {
                    ads_image_view.visibility = View.VISIBLE
                    ad_video.visibility = View.GONE
                    skip_text.visibility = View.GONE

                    Log.d("TAG@123", "start_ads_listing" + list[Home.ads_list_index].filename)
                    // Glide.with(requireContext()).load(list[ads_list_index].filename).into(ads_image_view)
                    progress_bar_ads.visibility = View.VISIBLE
                    Glide.with(requireContext()).load(list[Home.ads_list_index].filename)
                        .listener(object : RequestListener<Drawable> {
                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progress_bar_ads.visibility = View.GONE
                                return false;
                            }

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progress_bar_ads.visibility = View.GONE
                                return false;
                            }

                        }).into(ads_image_view);

                    ads_image_view.setOnClickListener {
                        requireContext().let {
                            AppUtils.open_ad_link(list[Home.ads_list_index].ad_link, it)
                        }
                    }

                    Home.ads_list_index++
                    handler.postDelayed(this, 10000)//1 sec delay
                } else {
                    skip_text.visibility = View.VISIBLE
                    ad_video.visibility = View.VISIBLE
                    progress_bar_ads.visibility = View.VISIBLE
                    ads_image_view.visibility = View.GONE
                    ad_video.setVideoPath(list[Home.ads_list_index].filename)
                    ad_video.setOnPreparedListener {
                        progress_bar_ads.visibility = View.GONE
                        ad_video.start()
                    }

                    skip_text.setOnClickListener {
                        Home.ads_list_index++
                        start_ads_listing(Home.ads_list)
                    }

                    ad_video.setOnCompletionListener {
                        ad_video.start()
                    }

                    ad_video.setOnClickListener {
                        requireContext().let {
                            AppUtils.open_ad_link(list[Home.ads_list_index].ad_link, it)
                        }
                    }
                }

            }
        }, 0)
    }

    private fun do_sent_firebaselog(event_name: String, event_log: String) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(
                event_name,
                event_log
            )
        }
    }


}
package com.SigmaDating.app.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.SigmaDating.R
import com.SigmaDating.app.adapters.ProfileMatch
import com.SigmaDating.app.model.*
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.storage.AppConstants.*
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.utilities.AppUtils.open_ad_link
import com.SigmaDating.app.views.CardManager.CardViewChanger
import com.SigmaDating.app.views.Home.Companion.notifications_count
import com.SigmaDating.app.views.Home.Companion.pages
import com.SigmaDating.app.views.Home.Companion.prohibited_words
import com.SigmaDating.app.views.Home.Companion.safety_message_text
import com.SigmaDating.app.views.Home.Companion.share_app_text
import com.SigmaDating.databinding.FragmentFirstBinding
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.demoapp.other.Constants
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.gson.JsonObject
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*


class FirstFragment : Fragment(), ProfileMatch.OnCategoryClickListener {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private var courseModalArrayList: ArrayList<Bids>? = null
    var broken_heart: LottieAnimationView? = null
    var heart_loading: LottieAnimationView? = null
    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    lateinit var editProfile: CircleImageView
    lateinit var notificationIcon: ConstraintLayout
    lateinit var notification: ConstraintLayout
    var cardViewChanger: CardViewChanger? = null
    private var idUserConnected = ""
    var userId: String? = null
    lateinit var adapter: ProfileMatch
    lateinit var credentials_card: ConstraintLayout
    lateinit var tvCounter: TextView
    lateinit var empty_text_view: TextView
    lateinit var empty_item_layout: LinearLayout
    lateinit var toast_layout: LinearLayout
    lateinit var images_toast: ImageView
    lateinit var images_toast_nah: ImageView
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG@123", "FirstFragment onCreate")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG@123", "FirstFragment onCreateView")
        Home.mCurrent_user_token = ""
        Home.mVideoGrant_user_token = ""
        Home.show_block = true
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        editProfile = binding.root.findViewById(R.id.edit_profile)
        notificationIcon = binding.root.findViewById(R.id.notification)
        cardViewChanger = binding.root.findViewById(R.id.card_stack_view)
        credentials_card = binding.root.findViewById(R.id.credentials_card)
        tvCounter = binding.root.findViewById(R.id.tvCounter)
        empty_text_view = binding.root.findViewById(R.id.empty_text_view)
        empty_item_layout = binding.root.findViewById(R.id.empty_item_layout)
        toast_layout = binding.root.findViewById(R.id.toast_view)
        images_toast = binding.root.findViewById(R.id.images_toast)
        images_toast_nah = binding.root.findViewById(R.id.images_toast_nah)
        empty_item_layout.visibility = View.GONE
        val show_disclamer = binding.root.findViewById<LinearLayout>(R.id.show_disclamer)
        val webView = binding.root.findViewById<WebView>(R.id.webView_diclamer)
        val logout = binding.root.findViewById<Button>(R.id.logout)
        val cancle = binding.root.findViewById<Button>(R.id.cancel)
        if (!(activity as Home).sharedPreferencesStorage.getBoolean(AppConstants.Disclaimer)) {
            AppUtils.showLoader(requireContext())
            webView.webViewClient = WebViewClient()
            show_disclamer.visibility = View.VISIBLE
            webView.loadUrl(Constants.disclaimer)
        } else {
            show_disclamer.visibility = View.GONE
            callApis()
        }

        logout.setOnClickListener {
            (activity as Home).sharedPreferencesStorage.setValue(
                AppConstants.Disclaimer,
                true
            )


            show_disclamer.visibility = View.GONE
            callApis()
        }


        cancle.setOnClickListener {
            (activity as Home).sharedPreferencesStorage.setValue(
                AppConstants.Disclaimer,
                false
            )
           // (activity as Home).onBackPressed()
            Toast.makeText(requireContext(), "You cannot continue until you agree to terms", Toast.LENGTH_LONG)
                .show()
        }

        editProfile.setOnClickListener {
            val bundle = Bundle()
            userId = (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
            bundle.putString("user_id", userId)
            bundle.putString("navigate", "Home")
            findNavController().navigate(
                R.id.action_FirstFragment_to_SecondFragment,
                bundle,
                null,
                null
            )
        }
        notificationIcon.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_all_activity)
        }

        footer_transition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG@123", "FirstFragment onViewCreated")
        cardViewChanger!!.setFlingListener(object : CardViewChanger.OnCardFlingListener {
            override fun onCardExitLeft(dataObject: Any) {
                Log.d("TAG@123", "onCardExitLeft")
                if (dataObject is Bids) {
                    if ((dataObject as Bids).record_type.equals("bid")) {
                        // showToast("dislike")
                        idUserConnected = (dataObject as Bids).id
                        Log.d("TAG@123", "idUserConnected " + idUserConnected)
                        swipe_update(idUserConnected, "dislike")
                        // Toast.makeText(requireContext(), "Nah", Toast.LENGTH_SHORT).show()
                        do_sent_firebaselog("do_swipe", "Left")
                    }
                }
            }

            override fun onCardExitRight(dataObject: Any) {
                Log.d("TAG@123", "onCardExitRight")
                if (dataObject is Bids) {
                    if ((dataObject as Bids).record_type.equals("bid")) {
                        // showToast("like")
                        idUserConnected = (dataObject as Bids).id
                        Log.d("TAG@123", "idUserConnected " + idUserConnected)
                        swipe_update(idUserConnected, "like")
                        do_sent_firebaselog("do_swipe", "Right")
                    } else {
                        Log.d("TAG@123", "open this link " + (dataObject as Bids).ad_link)
                        do_sent_firebaselog(
                            "ad view",
                            "Video ad link :" + (dataObject as Bids).ad_link
                        )
                        requireContext().let {

                            if(!(dataObject as Bids).ad_link.isNullOrEmpty()) {
                                open_ad_link((dataObject as Bids).ad_link, it)
                            }

                        }
                    }
                }
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                Log.d("TAG@123", "onAdapterAboutToEmpty :" + itemsInAdapter)
                if (itemsInAdapter == 0) {
                    Log.d("TAG@123", "Bid list isNullOrEmpty")
                    empty_text_view.text = "No matching bids found."
                    empty_item_layout.visibility = View.VISIBLE
                }
            }

            override fun onScroll(v: Float) {
                Log.d("TAG@123", "onScroll" + v)
                if (v < 0) {
                    showToast("dislike")
                } else if (v >= 0 && v <= 1) {
                    showToast("like")
                } else if (v >= 1) {
                    showToast("superlike")
                }
            }

            override fun onCardExitTop(dataObject: Any) {
                Log.d("TAG@123", "onCardExitTop")

                if (dataObject is Bids) {
                    if ((dataObject as Bids).record_type.equals("bid")) {
                        ///showToast("superlike")
                        idUserConnected = (dataObject as Bids).id
                        Log.d("TAG@123", "idUserConnected " + idUserConnected)
                        swipe_update(idUserConnected, "superlike")
                        // Toast.makeText(requireContext(), "Super Like", Toast.LENGTH_SHORT).show()

                        do_sent_firebaselog("do_swipe", "Top")
                    }
                }
            }

            override fun onCardExitBottom(dataObject: Any?) {
                Log.d("TAG@123", "onCardExitBottom")

            }

            override fun onCardanimatiotop(dataObject: Any?) {
                Log.d("TAG@123", "onCardanimatiotop")

            }

            override fun onCardanimatioright(dataObject: Any?) {
                Log.d("TAG@123", "onCardanimatioright")

            }

            override fun onCardanimatioleft(dataObject: Any?) {
                Log.d("TAG@123", "onCardanimatioleft")

            }
        })

    }


    override fun onResume() {
        super.onResume()
        (activity as Home).homeviewmodel.info_update =
            MutableLiveData<Resource<contactinfoModel>>()
        (activity as Home).homeviewmodel.get_contact_info()
        update_data()
    }


    fun update_data() {
        (activity as Home?)?.homeviewmodel?.info_update?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                share_app_text =
                                    it.data?.data?.share_app_text.toString()
                                safety_message_text = it.data?.data?.safety_message_text.toString()
                                prohibited_words = it.data?.data?.prohibited_words.toString()
                                Home.filter_offensive_text = it.data?.data?.filter_offensive_text.toString()
                                Log.d(
                                    "TAG@123",
                                    "share_app_text $share_app_text  : $safety_message_text"
                                )
                            } catch (e: Exception) {
                                Log.d("TAG@123", "Exception ::" + e.message.toString())
                            }
                        } else {

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        Log.d("TAG@123", "on destroy called")
    }


    fun footer_transition() {
        chatIcon = binding.root.findViewById(R.id.chat_Icon)
        match_list = binding.root.findViewById(R.id.match_list)
        sigma_list = binding.root.findViewById(R.id.sigma_list)
        //For Home sigma_list is Enable

        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_enable))
        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_disable))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_disable))


        chatIcon.setOnClickListener {
            if (AppUtils.isNetworkAvailable()) {
                findNavController().navigate(R.id.action_FirstFragment_to_chat)
            }
        }

        match_list.setOnClickListener {
            if (AppUtils.isNetworkAvailable()) {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }
        }
        sigma_list.setOnClickListener {
            AppUtils.animateImageview(sigma_list)
        }
        (activity as Home).clearBackStack()
    }


    override fun onCategoryClick(
        position: Bids?,
        count: Int,
        extras: FragmentNavigator.Extras?,
        imageView: ImageView
    ) {

        when (count) {
            1 -> {
                extras?.let {
                    val bundle = Bundle()
                    bundle.putString("user_id", position?.id)
                    bundle.putString("navigate", "Home")
                    findNavController().navigate(
                        R.id.action_FirstFragment_to_reportUserFragment,
                        bundle,
                        null,
                        extras
                    )
                } ?: run {
                    val bundle = Bundle()
                    bundle.putString("user_id", position?.id)
                    bundle.putString("navigate", "Home")
                    findNavController().navigate(
                        R.id.action_FirstFragment_to_reportUserFragment,
                        bundle,
                        null,
                        null
                    )
                }
            }
            2 -> {
               /* if ((position as Bids).record_type.equals("bid")) {
                    showToast("like")
                }
                cardViewChanger?.throwRight()*/

                val bundle = Bundle()
                bundle.putString("user_id",(position as Bids).id)
                bundle.putString("is_From","LIKE")
                findNavController().navigate(R.id.action_SecondFragment_to_postlist,bundle)


            }
            3 -> {
                if ((position as Bids).record_type.equals("bid")) {
                    showToast("superlike")
                }

                cardViewChanger?.throwTop()
            }
            4 -> {
                extras?.let {
                    val bundle = Bundle()
                    bundle.putString("user_id", position?.id)

                    findNavController().navigate(
                        R.id.action_FirstFragment_to_SecondFragment,
                        bundle,
                        null,
                        extras
                    )
                } ?: run {
                    val bundle = Bundle()
                    bundle.putString("user_id", position?.id)
                    bundle.putString("navigate", "Home")

                    findNavController().navigate(
                        R.id.action_FirstFragment_to_SecondFragment,
                        bundle,
                        null,
                        null
                    )

                }
            }

            5 -> {
                if ((position as Bids).record_type.equals("bid")) {
                    showToast("dislike")
                }

                cardViewChanger?.throwLeft()
            }
            7 -> {
                requireContext().let {
                    position?.ad_link?.let { it1 -> AppUtils.open_ad_link(it1, it) }
                }

                if (position?.type.equals("image")) {
                    do_sent_firebaselog("ad view", "image ad link :" + position?.ad_link)
                } else {
                    do_sent_firebaselog("ad view", "Video ad link :" + position?.ad_link)
                }
            }
            6 -> findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }


    }


    fun subscribe_swipe() {
        (activity as Home?)?.homeviewmodel?.profile_swipe?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Log.d("TAG@123", "" + it.data?.message)
                            } catch (e: Exception) {
                                Log.d("TAG@123", "Exception ::" + e.message.toString())
                            }
                        } else {

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

    fun subscribe_bids() {
        (activity as Home?)?.homeviewmodel?.user_bids?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Log.d(
                                    "TAG@123",
                                    "notifications_count  :" + it.data?.notifications_count.toString()
                                )

                                Log.d("TAG@123", "notifications_count  :" + it.data.toString())
                                courseModalArrayList = it.data?.bids as ArrayList<Bids>

                                Log.d(
                                    "TAG@123",
                                    "courseModalArrayList Size   :" + courseModalArrayList!!.size
                                )
                                pages = it.data.pages as ArrayList<Pages>
                                notifications_count = it.data.notifications_count
                                notifications_count.let {
                                    tvCounter.setText(notifications_count)
                                }

                                if (Home.toastflag) {
                                    Toast.makeText(
                                        requireContext(),
                                        res.message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    Home.toastflag = false
                                }
                                if (courseModalArrayList!!.size == 0) {
                                    empty_text_view.text = it.data.message
                                    empty_item_layout.visibility = View.VISIBLE
                                    Log.d("TAG@123", " empty_text  Show")
                                } else {
                                    empty_item_layout.visibility = View.GONE
                                }
                                adapter =
                                    ProfileMatch(courseModalArrayList!!, requireActivity(), this)
                                cardViewChanger?.setAdapter(adapter)
                                adapter.notifyDataSetChanged()

                            } catch (e: Exception) {
                                Log.d("TAG@123", "Exception  :" + e.message.toString())
                            }
                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_SHORT)
                                .show()
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


    fun subscribe_Login_User_details() {
        (activity as Home?)?.homeviewmodel?.get_user_data?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Log.d("TAG@123", it.data?.user.toString())
                                (activity as Home).sharedPreferencesStorage.setValue(
                                    AppConstants.upload_image,
                                    it.data?.user?.upload_image
                                )
                                Glide.with(requireContext()).load(it.data?.user?.upload_image)
                                    .error(R.drawable.profile_img)
                                    .into(editProfile);
                                (activity as Home).sharedPreferencesStorage.setValue(
                                    AppConstants.USER_NAME,
                                    res.user.first_name + " " + res.user.last_name
                                )
                                Home.current_user_profile = it.data?.user?.upload_image.toString()

                                (activity as Home).sharedPreferencesStorage.setValue(
                                    emergency_contact_one,
                                    it.data?.user?.emergency_contact1.toString()
                                )

                                (activity as Home).sharedPreferencesStorage.setValue(
                                    emergency_contact_two,
                                    it.data?.user?.emergency_contact2.toString()
                                )

                                (activity as Home).sharedPreferencesStorage.setValue(
                                    emergency_contact_three,
                                    it.data?.user?.emergency_contact3.toString()
                                )

                                if (it.data?.user?.upload_image?.length == 0 || it.data?.user?.upload_image == null) {
                                    Glide.with(requireContext()).load(
                                        (activity as Home?)?.sharedPreferencesStorage!!.getString(
                                            AppConstants.upload_image
                                        )
                                    )
                                        .error(R.drawable.profile_img)
                                        .into(editProfile);

                                } else {

                                }
                            } catch (e: Exception) {
                                Log.d("TAG@123", "Exception  :-" + e.message.toString())
                            }
                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_SHORT)
                                .show()
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

    fun swipe_update(id: String, key: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "user_id",
            (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
        )
        jsonObject.addProperty("profile_id", id)
        jsonObject.addProperty(key, "yes")
        Log.d("TAG@123", jsonObject.toString())
        (activity as Home).homeviewmodel.profile_swipe = MutableLiveData<Resource<Loginmodel>>()
        (activity as Home).homeviewmodel.profile_swipe_details(jsonObject)
        subscribe_swipe()
    }


    private fun do_sent_firebaselog(event_name: String, event_log: String) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(event_name, event_log)
        }
    }


    inner class WebViewClient : android.webkit.WebViewClient() {

        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.contains("sigmadating")) {
                requireContext().let {
                    if(!url.isNullOrEmpty()) {
                        open_ad_link(url, it)
                    }

                }
                view.reload()
                return true
            }
            view.loadUrl(url)
            return false
        }

        // ProgressBar will disappear once page is loaded
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            AppUtils.hideLoader()
        }
    }


    private fun callApis() {
        (activity as Home).homeviewmodel.get_Login_User_bids(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )

        (activity as Home).homeviewmodel.get_Login_User_details(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )

        subscribe_bids()
        subscribe_Login_User_details()
    }


    fun showToast(like: String) {
        toast_layout.visibility = View.VISIBLE
        if (like.equals("dislike")) {
            images_toast.visibility = View.GONE
            images_toast_nah.visibility = View.VISIBLE
            images_toast_nah.setImageDrawable(getResources().getDrawable(R.drawable.nah));

        } else if (like.equals("like")) {
            images_toast.visibility = View.GONE
            images_toast_nah.visibility = View.VISIBLE
            images_toast_nah.setImageDrawable(getResources().getDrawable(R.drawable.yep));

        } else {
            images_toast.visibility = View.VISIBLE
            images_toast_nah.visibility = View.GONE
            images_toast.setImageDrawable(getResources().getDrawable(R.drawable.vibing));
        }

        Handler().postDelayed(
            java.lang.Runnable {
                toast_layout.visibility = View.GONE
            },
            100
        )

    }


}



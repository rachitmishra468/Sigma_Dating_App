package com.SigmaDating.app.views.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.BuildConfig
import com.SigmaDating.R
import com.SigmaDating.app.AppReseources
import com.SigmaDating.app.adapters.ChatList_Adapter
import com.SigmaDating.app.model.*
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.utilities.PhoneTextWatcher
import com.SigmaDating.app.views.Home
import com.SigmaDating.app.views.Home.Companion.chatFlag
import com.SigmaDating.databinding.FragmentChatListBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val CAMERA_MIC_PERMISSION_REQUEST_CODE = 1

class ChatListFragment : Fragment(), ChatList_Adapter.OnCategoryClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var safety_icon: ImageView
    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    lateinit var tvCounter: TextView
    lateinit var movetonotification: ConstraintLayout
    private lateinit var chatlistAdapter: ChatList_Adapter
    private var dataList = mutableListOf<User_bids_list>()
    private var chat_list_recycler: RecyclerView? = null
    var latitude = ""
    var longitude = ""
    lateinit var empty_text_view: TextView
    lateinit var empty_item_layout: LinearLayout
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    //Ad

    lateinit var ad_video: VideoView
    lateinit var close_ad_img: ImageView
    lateinit var ad_main: ConstraintLayout
    lateinit var ads_image_view: ImageView
    lateinit var progress_bar_ads: ProgressBar
    lateinit var skip_text: TextView
    var ads_close: Boolean = false


    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage
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
        chatFlag = false
        Home.show_block = false
        // Inflate the layout for this fragment
        _binding = FragmentChatListBinding.inflate(inflater, container, false)

        empty_text_view = binding.root.findViewById(R.id.empty_text_view)
        empty_item_layout = binding.root.findViewById(R.id.empty_item_layout)
        empty_item_layout.visibility = View.GONE
        tvCounter = binding.root.findViewById(R.id.tvCounter)
        movetonotification = binding.root.findViewById(R.id.movetonotification)
        Home.notifications_count.let {
            _binding?.tvCounter?.setText(Home.notifications_count)
        }
        safety_icon = binding.root.findViewById(R.id.safety_icon)

        chatlistAdapter = ChatList_Adapter(requireContext(), this)
        footer_transition()
        _binding!!.movetoedit.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_editprofile)
        }
        _binding!!.movetosetting.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_setting)
        }

        _binding!!.movetonotification.setOnClickListener {

            findNavController().navigate(R.id.action_chatListFragment_to_notification)
        }

        _binding?.shareIcon?.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + R.string.app_name)
                var shareMessage = Home.share_app_text + "\n"
                shareMessage =
                    """ ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}                   
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: java.lang.Exception) {
                //e.toString();
            }
        }

        safety_icon.setOnClickListener {

            if (
                (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_one)
                    .equals("null")
                && (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_two)
                    .equals("null")
                && (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_three)
                    .equals("null")
            ) {
                Update_sefty_contact_number()
            } else if ((activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_one)
                    .equals("")
                && (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_two)
                    .equals("")
                && (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_three)
                    .equals("")
            ) {
                Update_sefty_contact_number()
            } else {
                if (checkPermissions()) {
                    if (!(activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_one)
                            .isNullOrEmpty()
                    ) {
                        sendSMS(
                            (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_one),
                            Home.safety_message_text
                        )
                    }
                    if (!(activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_two)
                            .isNullOrEmpty()
                    ) {
                        sendSMS(
                            (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_two),
                            Home.safety_message_text
                        )
                    }
                    if (!(activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_three)
                            .isNullOrEmpty()
                    ) {
                        sendSMS(
                            (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_three),
                            Home.safety_message_text
                        )
                    }

                } else {
                    requestPermissions(arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS
                    ))
                }
            }
        }




        chat_list_recycler = binding.root.findViewById(R.id.chatlist_recyclerView)
        chat_list_recycler?.layoutManager = GridLayoutManager(requireContext(), 1)
        _binding!!.editTextTextSearch.addTextChangedListener {
            filter(it.toString())
        }
        (activity as Home).homeviewmodel.all_match_bids = MutableLiveData<Resource<Match_bids>>()
        (activity as Home).homeviewmodel.get_user_match_bids(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )
        subscribe_create_post()

        (activity as Home).homeviewmodel.app_ads =
            MutableLiveData<Resource<advertisingData>>()
        (activity as Home).homeviewmodel.get_ads_list("chatscreen", (activity as Home).sharedPreferencesStorage.getString(
            AppConstants.USER_ID
        ))
        ad_main = binding.root.findViewById(R.id.ad_main)
        ad_main.visibility = View.GONE
        subscribe_app_ads()

        //Ad view
        progress_bar_ads = binding.root.findViewById(R.id.progress_bar_ads)
        ads_image_view = binding.root.findViewById(R.id.ads_image_view)
        skip_text = binding.root.findViewById(R.id.skip_text)
        close_ad_img = binding.root.findViewById(R.id.close_ad_img)
        ad_main.visibility = View.VISIBLE
        ad_video = binding.root.findViewById(R.id.videoview)


        skip_text.setOnClickListener {
            if (ads_close) {
                if (ad_video.isPlaying) {
                    ad_video.stopPlayback()
                }
                ad_main.visibility = View.GONE
            }
        }



        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(AppReseources.getAppContext()!!)
        getLocation()

        return binding.root;
    }


    fun filter(text: String?) {
        val temp: MutableList<User_bids_list> = ArrayList()
        for (d in dataList) {
            if ((d.first_name + " " + d.last_name).lowercase()
                    .startsWith(text?.lowercase().toString())
            ) {
                temp.add(d)
            }
        }
        chatlistAdapter.updateList(temp)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun footer_transition() {
        chatIcon = binding.root.findViewById(R.id.chat_Icon)
        match_list = binding.root.findViewById(R.id.match_list)
        sigma_list = binding.root.findViewById(R.id.sigma_list)

        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_disable))
        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_disable))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_enable))


        chatIcon.setOnClickListener {
            AppUtils.animateImageview(chatIcon)
            // findNavController().navigate(R.id.action_FirstFragment_to_chat)
        }

        match_list.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        sigma_list.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_FirstFragment)
        }

    }


    override fun onCategoryClick(position: User_bids_list, flag: Boolean) {


        (activity as Home).homeviewmodel.ctrateToken_data =
            MutableLiveData<Resource<Token_data>>()

        (activity as Home).sharedPreferencesStorage.getString(
            AppConstants.USER_ID
        )
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "identity",
            position.match_id
        )
        /* jsonObject.addProperty(
             "identity",
             (activity as Home).sharedPreferencesStorage.getString(
                 AppConstants.USER_ID
             )        )*/
        Log.d("TAG@123", "identity : " + jsonObject.toString())
        (activity as Home).homeviewmodel.get_User_token(
            jsonObject
        )
        subscribe_Login_User_details(position, flag)

    }

    fun subscribe_create_post() {
        (activity as Home?)?.homeviewmodel?.all_match_bids?.observe(
            viewLifecycleOwner,
            Observer { res ->
                when (res.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        Log.d("TAG@123", "Status " + res.status)
                        Home.notifications_count = "0"

                        if (res.data!!.data.isNullOrEmpty()) {
                            empty_text_view.text = res.data.message
                            empty_item_layout.visibility = View.VISIBLE
                            Log.d("TAG@123", " empty_text  Show")
                        } else {
                            dataList = res.data.data as ArrayList<User_bids_list>
                            setAdapterListData(res.data.data as ArrayList<User_bids_list>)
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


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraMicrophoneAndBluetooth()
        }
    }


    private fun requestPermissionForCameraMicrophoneAndBluetooth() {
        val permissionsList: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        }
        requestPermissions(permissionsList)
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {

                        try {
                            val geocoder =
                                Geocoder(AppReseources.getAppContext(), Locale.getDefault())
                            val list: List<Address> =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)


                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                latitude = "${list[0].latitude}"
                                longitude = "${list[0].longitude}"
                                Log.d("TAG@123", "location name" + latitude)

                            }

                        } catch (e: Exception) {
                            Log.e("TAG@123", "Location Exception : ${e.message}")
                        }
                    }

                }

            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
            }
        } else {

            requestPermissions(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
            ))
        }
    }




    private fun requestPermissions(permissions: Array<String>) {
        var displayRational = false
        for (permission in permissions) {
            displayRational =
                displayRational or ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    permission
                )
        }
        if (displayRational) {
            Toast.makeText(requireContext(), "permissions needed", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), permissions, CAMERA_MIC_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun checkPermissionForCameraAndMicrophone(): Boolean {
        return checkPermissions(
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS)
        )
    }

    private fun checkPermissions(permissions: Array<String>): Boolean {
        var shouldCheck = true
        for (permission in permissions) {
            shouldCheck = shouldCheck and (PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(requireContext(), permission))
        }
        return shouldCheck
    }


    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }


    fun setAdapterListData(dataListuser: ArrayList<User_bids_list>) {
        chat_list_recycler?.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        chat_list_recycler?.adapter = chatlistAdapter
        chatlistAdapter.setDataList(dataListuser)
        Log.d("TAG@123", " setAdapterListData  ${dataListuser.size}")
    }


    fun subscribe_Login_User_details(position: User_bids_list, flag: Boolean) {
        (activity as Home?)?.homeviewmodel?.ctrateToken_data?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()

                    if (flag) {
                        val bundle = Bundle()
                        bundle.putString("user_id", position.id)
                        findNavController().navigate(
                            R.id.action_FirstFragment_to_SecondFragment,
                            bundle,
                            null,
                            null
                        )
                    } else {
                        val bundle = Bundle()
                        bundle.putString(
                            "user_name",
                            position?.first_name + " " + position.last_name
                        )
                        bundle.putString("user_image", position.upload_image)
                        bundle.putString("user_ID", position.id)
                        bundle.putString("match_ID", position.match_id)
                        Navigation.findNavController(binding.root)
                            .navigate(
                                R.id.action_chatListFragment_to_userChatFragment, bundle,
                                null,
                                null
                            );
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


    fun subscribe_app_ads() {
        (activity as Home?)?.homeviewmodel?.app_ads?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Log.d("TAG@123", "ads data count  :" + it.data.toString())
                                Home.ads_list = it.data?.ads as ArrayList<advertising_model>
                                if (Home.ads_list.isNotEmpty()) {
                                    ad_main.visibility = View.VISIBLE
                                    Home.ads_list_index = 0
                                    start_ads_listing(Home.ads_list)
                                    var i = 6
                                    val handler = Handler()
                                    handler.postDelayed(object : Runnable {
                                        override fun run() {
                                            if (1 <= i) {
                                                skip_text.setText("" + i + " Skip Ads ")
                                                i -= 1
                                            } else {
                                                ads_close = true
                                                skip_text.setText(" Skip Ads ")
                                                handler.removeCallbacksAndMessages(null);
                                            }
                                            handler.postDelayed(this, 1000)//1 sec delay
                                        }
                                    }, 0)
                                }else{
                                    ad_main.visibility=View.GONE
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



                    ad_video.setOnCompletionListener {
                        Home.ads_list_index++
                        start_ads_listing(Home.ads_list)
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


    fun sendSMS(phoneNo: String?, msg: String?) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            val uri = msg + "\n " + "http://maps.google.com/?q=$latitude,$longitude"
            smsManager.sendTextMessage(phoneNo, null, uri, null, null)
            Toast.makeText(
                requireContext(), "Message has been sent.",
                Toast.LENGTH_LONG
            ).show()
        } catch (ex: java.lang.Exception) {
            Toast.makeText(
                requireContext(), ex.message.toString(),
                Toast.LENGTH_LONG
            ).show()
            ex.printStackTrace()
        }
    }

    fun Update_sefty_contact_number() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.update_sefty_sheet_dialog, null)
        val editText_one = view.findViewById<EditText>(R.id.editText_one)
        val editText_two = view.findViewById<EditText>(R.id.editText_two)
        val editText_three = view.findViewById<EditText>(R.id.editText_three)
        val save_contact = view.findViewById<Button>(R.id.save_contact)
        editText_one.setText((activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_one))
        editText_two.setText((activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_two))
        editText_three.setText((activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_three))
        editText_one.addTextChangedListener(PhoneTextWatcher(editText_one))
        editText_two.addTextChangedListener(PhoneTextWatcher(editText_two))
        editText_three.addTextChangedListener(PhoneTextWatcher(editText_three))
        save_contact.setOnClickListener {
            if (editText_one.text.isEmpty()
                && editText_two.text.isEmpty()
                && editText_three.text.isEmpty()
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter Contact Number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (editText_one.text.isNotEmpty()
                && editText_one.text.length != 12
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter Correct Number",
                    Toast.LENGTH_LONG
                ).show()

            } else if (editText_two.text.isNotEmpty()
                && editText_two.text.length != 12
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter Correct Number",
                    Toast.LENGTH_LONG
                ).show()

            } else if (editText_three.text.isNotEmpty()
                && editText_three.text.length != 12
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter Correct Number",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                (activity as Home).homeviewmodel.contact_responce =
                    MutableLiveData<Resource<Loginmodel>>()
                val jsonObject = JsonObject()
                jsonObject.addProperty(
                    "user_id",
                    (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                )
                jsonObject.addProperty(
                    "emergency_contact1",
                    editText_one.text.toString()
                )

                jsonObject.addProperty(
                    "emergency_contact2",
                    editText_two.text.toString()
                )

                jsonObject.addProperty(
                    "emergency_contact3",
                    editText_three.text.toString()
                )

                (activity as Home).homeviewmodel.post_users_updatecontacts(jsonObject)
                subscribe_create_post(
                    editText_one.text.toString(),
                    editText_two.text.toString(), editText_three.text.toString()
                )
                dialog.dismiss()
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    fun subscribe_create_post(editText_one: String, editText_two: String, editText_three: String) {
        (activity as Home?)?.homeviewmodel?.contact_responce?.observe(
            viewLifecycleOwner,
            Observer { res ->
                when (res.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()

                        (activity as Home).sharedPreferencesStorage.setValue(
                            AppConstants.emergency_contact_one,
                            editText_one
                        )

                        (activity as Home).sharedPreferencesStorage.setValue(
                            AppConstants.emergency_contact_two,
                            editText_two
                        )

                        (activity as Home).sharedPreferencesStorage.setValue(
                            AppConstants.emergency_contact_three,
                            editText_three
                        )

                        Toast.makeText(
                            requireContext(),
                            res.data?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
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


}
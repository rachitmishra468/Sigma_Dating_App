package com.SigmaDating.app.views.userdashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.SigmaDating.R
import com.SigmaDating.app.AppReseources
import com.SigmaDating.app.adapters.Profile_Adapter
import com.SigmaDating.app.model.*


import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.SigmaDating.databinding.FragmentSecondBinding
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
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

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
    private val permissionId = 2
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val binding get() = _binding!!
    lateinit var chatIcon: ImageView
    private var name_text: TextView? = null
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    lateinit var safety_icon: ImageView
    lateinit var greekLatter: TextView
    private var userID: String? = null
    private var name: String? = null
    private var photo: String? = null
    lateinit var empty_text_view: TextView
    lateinit var empty_item_layout: LinearLayout
    lateinit var main_ad_layout: LinearLayout
    var latitude = ""
    var longitude = ""
    var phone_number = ""

    lateinit var ad_video: VideoView
    lateinit var close_ad_img: ImageView
    lateinit var ad_main: ConstraintLayout
    lateinit var ads_image_view: ImageView
    lateinit var progress_bar_ads: ProgressBar
    lateinit var skip_text: TextView

    var ads_close: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG@123", " SecondFragment onCreateView")
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        empty_text_view = binding.root.findViewById(R.id.empty_text_view)
        empty_item_layout = binding.root.findViewById(R.id.empty_item_layout)
        safety_icon = binding.root.findViewById(R.id.safety_icon)
        main_ad_layout = binding.root.findViewById(R.id.main_ad_layout)
        main_ad_layout.visibility = View.GONE
        safety_icon.setOnClickListener {

            if (
                (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_one).equals("null")
                && (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_two).equals("null")
                && (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_three).equals("null")
            ) {
                Update_sefty_contact_number()
            } else if ((activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_one).equals("")
                && (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_two).equals("")
                && (activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_three).equals("")
            ) {
                Update_sefty_contact_number()
            } else {
                if (checkPermissions()) {
                    if (!(activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_one).isNullOrEmpty()) {
                        sendSMS((activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_one), Home.safety_message_text)
                    }
                    if (!(activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_two).isNullOrEmpty()) {
                        sendSMS((activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_two), Home.safety_message_text)
                    }
                    if (!(activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_three).isNullOrEmpty()) {
                        sendSMS((activity as Home).sharedPreferencesStorage.getString(AppConstants.emergency_contact_three), Home.safety_message_text)
                    }

                } else {
                    requestPermissions()
                }
            }
        }
        empty_item_layout.visibility = View.GONE
        footer_transition()
        userID = getArguments()?.getString("user_id")
        if (userID == null) {
            userID = (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        }

        _binding?.editProfile?.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_editprofile)
        }
        if (!userID.equals((activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID))) {
            _binding?.fab?.visibility = View.GONE
            _binding?.profileImg?.visibility = View.VISIBLE
        } else {
            _binding?.profileImg?.visibility = View.INVISIBLE
            Home.show_block = false
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
            if (Home.mCurrent_user_token.equals("")) {

            } else {
                val bundle = Bundle()
                bundle.putString("user_name", name)
                bundle.putString("user_image", photo)
                Navigation.findNavController(binding.root)
                    .navigate(
                        R.id.action_chatListFragment_to_userChatFragment, bundle,
                        null,
                        null
                    );
            }
        }

        _binding?.comments?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user_id", userID)
            bundle.putString("navigate", "Home")
            findNavController().navigate(R.id.action_SecondFragment_to_Report_feb, bundle)

        }
        ad_main = binding.root.findViewById(R.id.ad_main)
        ad_main.visibility = View.GONE
        progress_bar_ads = binding.root.findViewById(R.id.progress_bar_ads)
        ads_image_view = binding.root.findViewById(R.id.ads_image_view)
        close_ad_img = binding.root.findViewById(R.id.close_ad_img)
        skip_text = binding.root.findViewById(R.id.skip_text)
        ad_main.visibility = View.VISIBLE
        ad_video = binding.root.findViewById(R.id.videoview)
        skip_text.setOnClickListener {
            if (ads_close) {
                ad_video.stopPlayback()
                ad_main.visibility = View.GONE
            }
        }

        if (userID != null) {
            (activity as Home).homeviewmodel.get_secound_feb_User_details(
                userID!!
            )
        }
        (activity as Home).homeviewmodel.get_secound_feb_data =
            MutableLiveData<Resource<Loginmodel>>()
        subscribe_Login_User_details()

        (activity as Home).homeviewmodel.app_ads =
            MutableLiveData<Resource<advertisingData>>()
        (activity as Home).homeviewmodel.get_ads_list("mainscreen")
        subscribe_app_ads()

        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(AppReseources.getAppContext()!!)
        getLocation()

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
        val list: ArrayList<Postdata> = arrayListOf()


        if (!userID.equals((activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID))) {
            dataListuser.forEach {
                if (it.isPrivate != "0") {
                    list.add(it)
                }
            }

        } else {
            list.addAll(dataListuser)
        }

        photoAdapter.setDataList(list)
        Log.d("TAG@123", " setAdapterListData  ${dataListuser.size}")
    }

    private fun subscribe_Login_User_details() {
        AppUtils.showLoader(requireContext())
        (activity as Home?)?.homeviewmodel?.get_secound_feb_data?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                                Log.d("TAG@123", "111 " + it.data?.user.toString())
                                _binding?.let {
                                    it.nameText.setText(res.user.first_name + " " + res.user.last_name)
                                    it.addresText.setText(res.user.university)
                                    it.ageText.setText("" + res.user.age)
                                }

                                if (it.data!!.user.greekletter.length > 0) {
                                    greekLatter.text = it.data.user.greekletter
                                    greekLatter.visibility = View.VISIBLE
                                } else {
                                    greekLatter.visibility = View.GONE
                                }
                                it.data.user.upload_image.let {
                                    Glide.with(AppReseources.getAppContext()!!).load(it)
                                        .error(R.drawable.profile_img)
                                        .into(_binding!!.logoDetail);
                                }
                                name = res.user.first_name + " " + res.user.last_name
                                photo = it.data.user.upload_image
                                if (!res.posts.isNullOrEmpty()) {
                                    dataListuser = res.posts
                                    setAdapterListData(dataListuser as ArrayList<Postdata>)
                                } else {
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
        greekLatter = binding.root.findViewById(R.id.greek_latter)

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
        main_ad_layout.visibility = View.VISIBLE
        val handler = Handler()
        var i = 6
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



        Log.d("TAG@123", "start_ads_listing")

        val handlert = Handler()
        handlert.postDelayed(object : Runnable {
            override fun run() {
                if (Home.ads_list_index == list.size) {
                    Home.ads_list_index = 0
                }
                if (list[Home.ads_list_index].type.equals("image")) {
                    ads_image_view.visibility = View.VISIBLE
                    ad_video.visibility = View.GONE
                    //  skip_text.visibility = View.GONE

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
                    handlert.postDelayed(this, 10000)//10 sec delay
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

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
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
        }}
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
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
            requestPermissions()
        }
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
package com.SigmaDating.app.views.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.telephony.SmsManager
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.SigmaDating.BuildConfig
import com.SigmaDating.R
import com.SigmaDating.app.AppReseources
import com.SigmaDating.app.advertising.view.AdvertisingActivity
import com.SigmaDating.app.model.Loginmodel
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.utilities.PhoneTextWatcher
import com.SigmaDating.app.views.Home
import com.SigmaDating.app.views.Splash
import com.SigmaDating.databinding.FragmentSettingsBinding
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


class SettingsFragment : Fragment() {

    lateinit var _binding: FragmentSettingsBinding
    private val binding get() = _binding
    var notification_flag = 1;
    var advertisement_flag = 1;
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    var location_text = ""
    var latitude = ""
    var longitude = ""
    var show_dilog = false


    fun Call_links() {
        val bundle = Bundle()
        _binding.licencesText.setOnClickListener {
            var link = Home.get_settingpage_data("licenses")
            link.let {
                bundle.putString("Url_Link", link?.url)
                bundle.putString("Hadder_text", link?.title)
                findNavController().navigate(R.id.action_settings_frag_to_ContactUs, bundle)
            }
        }
        _binding.privacyText.setOnClickListener {
            var link = Home.get_settingpage_data("privacy-preferences")

            link.let {
                bundle.putString("Url_Link", link?.url)
                bundle.putString("Hadder_text", link?.title)
                findNavController().navigate(R.id.action_settings_frag_to_ContactUs, bundle)
            }
        }

        _binding.privacyTextTwo.setOnClickListener {

            var link = Home.get_settingpage_data("privacy-policy")
            link.let {
                bundle.putString("Url_Link", link?.url)
                bundle.putString("Hadder_text", link?.title)
                findNavController().navigate(R.id.action_settings_frag_to_ContactUs, bundle)
            }
        }

        _binding.termsServices.setOnClickListener {
            var link = Home.get_settingpage_data("terms-of-service")
            link.let {
                bundle.putString("Url_Link", link?.url)
                bundle.putString("Hadder_text", link?.title)
                findNavController().navigate(R.id.action_settings_frag_to_ContactUs, bundle)
            }
        }
        _binding.contactUs.setOnClickListener {

            var link = Home.get_settingpage_data("support")
            link.let {
                bundle.putString("Url_Link", link?.url)
                bundle.putString("Hadder_text", link?.title)
                findNavController().navigate(R.id.action_setting_to_contact_from, bundle)
            }
        }

        _binding.community.setOnClickListener {

            var link = Home.get_settingpage_data("community-guidelines")
            link.let {
                bundle.putString("Url_Link", link?.url)
                bundle.putString("Hadder_text", link?.title)
                findNavController().navigate(R.id.action_settings_frag_to_ContactUs, bundle)
            }
        }

        _binding.sefery.setOnClickListener {

            var link = Home.get_settingpage_data("safety-guidelines")
            link.let {
                bundle.putString("Url_Link", link?.url)
                bundle.putString("Hadder_text", link?.title)
                findNavController().navigate(R.id.action_settings_frag_to_ContactUs, bundle)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        Call_links()
      //  _binding.textViewShareApp.text = "Share Sigma Social"
        _binding.textViewShareApp.setOnClickListener {
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
        _binding.passwordUpdate.setOnClickListener {
            Update_password(false);
        }
        _binding.seftyButton.setOnClickListener {
        }
        _binding.seftyUpdate.setOnClickListener {
            if (!show_dilog) {
                Update_sefty_contact_number()
            }

        }
        _binding.phoneNumberText.setOnClickListener {
            Update_phone_location(true);
        }
        _binding.locationText.setOnClickListener {
            Update_phone_location(false);
        }
        _binding.imageView2.setOnClickListener {
            (activity as Home).onBackPressed()
        }
        _binding.advertisin.setOnClickListener {
            startActivity(Intent(requireContext(), AdvertisingActivity::class.java))
        }
        _binding.textViewMembership.setOnClickListener {
            findNavController().navigate(R.id.action_setting_to_planslist)
        }

        _binding.contactFrom.setOnClickListener {
            findNavController().navigate(R.id.action_setting_to_contact_from)
        }

        _binding.switch1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            notification_flag = if (isChecked) 1 else 0

            Log.d("TAG@123", "notification_flag :" + notification_flag)

        })


        _binding.addSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            advertisement_flag = if (isChecked) 1 else 0

            Log.d("TAG@123", "advertisement_flag :" + advertisement_flag)

        })

        _binding.updateSetting.setOnClickListener {

            (activity as Home).homeviewmodel.setting_update_details =
                MutableLiveData<Resource<Loginmodel>>()
            subscribe_setting_update_details()
            val jsonObject = JsonObject()
            Log.d(
                "TAG@123",
                (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
            )
            jsonObject.addProperty(
                "user_id",
                (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
            )
            //jsonObject.addProperty("age_range", age_range)
            //  jsonObject.addProperty("distance", distance)
            jsonObject.addProperty("location", _binding.locationText.text.toString())
            jsonObject.addProperty("latitude", latitude)
            jsonObject.addProperty("longitude", longitude)
            // jsonObject.addProperty("show_me", show_me)
            //   jsonObject.addProperty("interested_in", interested_in)
            jsonObject.addProperty("notifications", notification_flag)
            jsonObject.addProperty("ads", advertisement_flag)


            Log.d("TAG@123", "interested_in :" + jsonObject.toString())
            (activity as Home).homeviewmodel.get_setting_update_details(jsonObject)
        }

        _binding.continueLogout.setOnClickListener {
            (activity as Home?)?.sharedPreferencesStorage?.setValue(
                AppConstants.IS_AUTHENTICATED,
                false
            )
            (activity as Home?)?.sharedPreferencesStorage?.setValue(
                AppConstants.Disclaimer,
                false
            )
            (activity as Home?)?.initializeGoogleSignIn()
            startActivity(Intent(requireContext(), Splash::class.java))
            (activity as Home?)?.finish()
        }


        _binding.continueDeleteAccount.setOnClickListener {
            Update_password(true);
        }
        subscribe_Login_User_details()
        (activity as Home).homeviewmodel.get_Login_User_details(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )

        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(AppReseources.getAppContext()!!)
        getLocation()
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

    fun subscribe_setting_update_details() {
        (activity as Home?)?.homeviewmodel?.setting_update_details?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                                Log.d("TAG@123", "111 " + res.message)
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                Log.d("TAG@123", "111 " + res?.message)
                                Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
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
                            Log.d("TAG@123", "111 " + it.data.toString())
                            _binding.textEmailId.setText(it.data?.user?.email)
                            _binding.phoneNumberText.setText(it.data?.user?.phone)
                            if (it.data?.user?.phone?.isEmpty() == true) {
                                _binding.phoneNumberText.setText("Update")
                            }
                            _binding.locationText.setText(it.data?.user?.location)

                            latitude = it.data?.user?.latitude.toString()
                            longitude = it.data?.user?.longitude.toString()
                            if (it.data?.user?.location?.isEmpty() == true) {
                                _binding.locationText.setText(location_text)
                                location_text = it.data.user.location.toString()
                            }
                            _binding.switch1.isChecked = it.data?.user?.notifications == 1
                            _binding.addSwitch.isChecked = it.data?.user?.advertisement == 1
                            (activity as Home).sharedPreferencesStorage.setValue(
                                AppConstants.emergency_contact_one,
                                it.data?.user?.emergency_contact1.toString()
                            )

                            (activity as Home).sharedPreferencesStorage.setValue(
                                AppConstants.emergency_contact_two,
                                it.data?.user?.emergency_contact2.toString()
                            )

                            (activity as Home).sharedPreferencesStorage.setValue(
                                AppConstants.emergency_contact_three,
                                it.data?.user?.emergency_contact3.toString()
                            )


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
                    AppUtils.hideLoader()
                }
            }
        })
    }

    fun subscribe_delete_account(dialog: BottomSheetDialog) {
        (activity as Home?)?.homeviewmodel?.change_password?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            dialog.dismiss()
                            AppUtils.hideLoader()
                            Log.d("TAG@123", "112" + res.toString())
                            res.message.let {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            (activity as Home?)?.sharedPreferencesStorage?.setValue(
                                AppConstants.IS_AUTHENTICATED,
                                false
                            )
                            (activity as Home?)?.sharedPreferencesStorage?.setValue(
                                AppConstants.Disclaimer,
                                false
                            )
                            (activity as Home?)?.initializeGoogleSignIn()
                            startActivity(Intent(requireContext(), Splash::class.java))
                            (activity as Home?)?.finish()

                        } else {
                            res!!.message.let {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

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

    fun subscribe_change_password(dialog: BottomSheetDialog) {
        (activity as Home?)?.homeviewmodel?.change_password?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            dialog.dismiss()
                            AppUtils.hideLoader()
                            Log.d("TAG@123", "112" + res.toString())
                            res.message.let {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            res!!.message.let {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

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


    fun Update_password(delete: Boolean) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.update_password_sheet_dialog, null)
        val current_password = view.findViewById<EditText>(R.id.current_password)
        val textView = view.findViewById<TextView>(R.id.textView5)
        val editText_password = view.findViewById<EditText>(R.id.editText_password)
        val editText_password_confirm = view.findViewById<EditText>(R.id.editText_password_confirm)
        val btnClose = view.findViewById<Button>(R.id.create_password)
        val hint = view.findViewById<TextView>(R.id.hint)
        if (delete) {
            hint.setText("")
            textView.setText("Delete Account")
            btnClose.setText("Confirm")
            editText_password.visibility = View.GONE
            editText_password_confirm.hint = "Enter Password"
        }
        btnClose.setOnClickListener {
            if (delete) {
                if (AppUtils.isValid_password(editText_password_confirm.text.toString())
                ) {

                    if (AppUtils.isValid_password(editText_password_confirm.text.toString())) {
                        subscribe_delete_account(dialog)
                        (activity as Home).homeviewmodel.User_delete_account(
                            (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID),
                            editText_password_confirm.text.toString(),
                        )
                    } else {
                        editText_password_confirm.setError("Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character")
                    }

                } else {
                    Toast.makeText(
                        context,
                        "Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }


            } else {
                if (AppUtils.isValid_password(editText_password.text.toString())) {
                    if (AppUtils.isValid_password_match(
                            editText_password.text.toString(),
                            editText_password_confirm.text.toString()
                        )
                    ) {
                        subscribe_change_password(dialog)
                        (activity as Home).homeviewmodel.User_change_password(
                            (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID),
                            editText_password.text.toString(),
                            editText_password_confirm.text.toString()
                        )
                    } else {
                        editText_password_confirm.error = "Password Does Not Match"
                    }

                } else {
                    editText_password.error =
                        "Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character"

                }
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    fun setEditTextMaxLength(editText: EditText, length: Int) {
        val FilterArray = arrayOfNulls<InputFilter>(1)
        FilterArray[0] = LengthFilter(length)
        editText.filters = FilterArray
    }


    fun Update_phone_location(phone: Boolean) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.update_phone_location, null)

        val title_text = view.findViewById<TextView>(R.id.textView5)
        val current_value = view.findViewById<EditText>(R.id.editText_password)
        val update_value = view.findViewById<Button>(R.id.update_value)
        val update_current = view.findViewById<Button>(R.id.update_current)

        if (phone) {
            setEditTextMaxLength(current_value, 12)
            current_value.addTextChangedListener(PhoneTextWatcher(current_value))
        }

        title_text.setText(
            if (phone) {
                "Update Phone Number."
            } else {
                "Update Location With Post Code."
            }
        )
        current_value.setHint(
            if (phone) {
                update_current.visibility = View.GONE
                current_value.inputType = InputType.TYPE_CLASS_NUMBER
                "Enter Phone Number."
            } else {
                update_current.visibility = View.VISIBLE
                current_value.inputType = InputType.TYPE_CLASS_TEXT
                "Enter Location With Post Code."
            }
        )
        update_current.setOnClickListener {
            _binding.locationText.text = location_text
            dialog.dismiss()
        }

        update_value.setOnClickListener {
            if (!current_value.text.toString().isEmpty()) {
                subscribe_change_password(dialog)
                var key = if (phone) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty(
                        "user_id",
                        (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                    )
                    jsonObject.addProperty("phone", "+1" + current_value.text.toString())
                    (activity as Home).homeviewmodel.setting_update_details =
                        MutableLiveData<Resource<Loginmodel>>()
                    (activity as Home).homeviewmodel.get_setting_update_details(jsonObject)
                    _binding.phoneNumberText.text = current_value.text.toString()
                    dialog.dismiss()
                } else {
                    val jsonObject = JsonObject()
                    Log.d(
                        "TAG@123",
                        (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                    )
                    jsonObject.addProperty(
                        "user_id",
                        (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                    )
                    // jsonObject.addProperty("age_range", age_range)
                    //  jsonObject.addProperty("distance", distance)
                    jsonObject.addProperty("location", current_value.text.toString())
                    jsonObject.addProperty("latitude", latitude)
                    jsonObject.addProperty("longitude", longitude)
                    Log.d("TAG@123", "updateSetting :" + jsonObject.toString())
                    (activity as Home).homeviewmodel.get_setting_update_details(jsonObject)
                    _binding.locationText.text = current_value.text.toString()
                    dialog.dismiss()
                }

            } else {
                current_value.setError("Please Enter .. ")
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
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
                                AppReseources.getAppContext()
                                    ?.let { Geocoder(it, Locale.getDefault()) }
                            val list: MutableList<Address>? =
                                geocoder?.getFromLocation(location.latitude, location.longitude, 1)


                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                latitude = "${list?.get(0)?.latitude}"
                                longitude = "${list?.get(0)?.longitude}"

                                location_text = "${list?.get(0)?.locality}"
                                Log.d("TAG@123", "location name" + location_text)

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

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
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
        show_dilog = true
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
                show_dilog = false
                dialog.dismiss()
            }
        }
        dialog.setOnDismissListener {
            show_dilog = false
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    fun sendSMS(phoneNo: String?, msg: String?) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            val uri = msg + "\n " + "http://maps.google.com/?q=$latitude,$longitude"
            smsManager.sendTextMessage(phoneNo, null, uri, null, null)
            Toast.makeText(
                requireContext(), "Message Send",
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
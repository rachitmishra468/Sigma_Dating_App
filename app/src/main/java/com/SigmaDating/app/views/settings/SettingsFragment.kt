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
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.demoapp.other.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.SigmaDating.R
import com.SigmaDating.app.AppReseources
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.SigmaDating.app.views.Splash
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import android.widget.RadioButton

import com.SigmaDating.app.utilities.PhoneTextWatcher
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import androidx.lifecycle.MutableLiveData
import com.SigmaDating.app.model.Loginmodel
import com.SigmaDating.databinding.FragmentSettingsBinding
import com.example.demoapp.other.Resource

class SettingsFragment : Fragment() {

    lateinit var _binding: FragmentSettingsBinding
    private val binding get() = _binding

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    var age_range = ""
    var distance = ""
    var location_text = ""
    var latitude = ""
    var longitude = ""
    var interested_in = ""
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
                findNavController().navigate(R.id.action_settings_frag_to_ContactUs, bundle)
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
        _binding.passwordUpdate.setOnClickListener {
            Update_password(false);
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

        _binding.rg.setOnCheckedChangeListener { group, checkedId ->
            val rb = _binding.root.findViewById(checkedId) as RadioButton
            interested_in = rb.text.toString()
            Log.d("TAG@123", "interested_in  $interested_in")
        }

        _binding.seekBar.addOnChangeListener { rangeSlider, value, fromUser ->
            Log.d("TAG@123", value.toString())
            _binding.textView11.text = "$value miles"
            distance = value.toString()
        }

        _binding.seekBarAge.addOnChangeListener { rangeSlider, value, fromUser ->
            val values = rangeSlider.values
            Log.d("TAG@123", "Start value: ${values[0]}, End value: ${values[1]}")
            _binding.textView8.text = "${values[0].toInt()}-${values[1].toInt()} "
            age_range = "${values[0].toInt()}-${values[1].toInt()}"

        }

        _binding.updateSetting.setOnClickListener {

            (activity as Home).homeviewmodel.setting_update_details= MutableLiveData<Resource<Loginmodel>>()
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
            jsonObject.addProperty("age_range", age_range)
            jsonObject.addProperty("distance", distance)
            jsonObject.addProperty("location", _binding.locationText.text.toString())
            jsonObject.addProperty("latitude", latitude)
            jsonObject.addProperty("longitude", longitude)
            jsonObject.addProperty("interested_in", interested_in)
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


                            if (it.data?.user?.age_range?.isEmpty() == false) {
                                _binding.textView8.setText(it.data.user.age_range)

                                try {
                                    _binding.seekBarAge.setValues(
                                        res.user.age_range.split("-").get(0).toFloat(),
                                        res.user.age_range.split("-").get(1).toFloat()
                                    )
                                } catch (e: Exception) {
                                    Log.d("TAG@123", "seekBarAge ${e.message}")
                                }
                            }

                            if (it.data?.user?.interested_in?.isEmpty() == false) {
                                interested_in = it.data.user.interested_in
                                when (interested_in) {
                                    "Women" -> _binding.rbWomen.setChecked(true);
                                    "Men" -> _binding.rbMen.setChecked(true);
                                    "WOMEN" -> _binding.rbWomen.setChecked(true);
                                    "MEN" -> _binding.rbMen.setChecked(true);
                                    "BOTH" -> _binding.rbMore.setChecked(true);
                                    "Both" -> _binding.rbMore.setChecked(true);

                                }
                            }


                            if (it.data?.user?.distance?.isEmpty() == false) {
                                if (it.data.user.distance.toFloat() >= 25) {
                                    _binding.seekBar.setValue(it.data.user.distance.toFloat())
                                }
                                _binding.textView11.setText(it.data.user.distance + " miles")
                            }


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
                    Toast.makeText(context, "Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character", Toast.LENGTH_SHORT)
                        .show()
                }


            } else {
                if (AppUtils.isValid_password(editText_password.text.toString())) {
                    if ( AppUtils.isValid_password_match(
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
                        editText_password_confirm.error="Password Does Not Match"
                    }

                } else {
                    editText_password.error="Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character"

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
                    jsonObject.addProperty("phone", "+1"+current_value.text.toString())
                    (activity as Home).homeviewmodel.get_setting_update_details(jsonObject)
                    _binding.phoneNumberText.text = "+1"+current_value.text.toString()
                    dialog.dismiss()
                }
                else {
                    val jsonObject = JsonObject()
                    Log.d(
                        "TAG@123",
                        (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                    )
                    jsonObject.addProperty(
                        "user_id",
                        (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                    )
                    jsonObject.addProperty("age_range", age_range)
                    jsonObject.addProperty("distance", distance)
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

                        try{
                        val geocoder = Geocoder(AppReseources.getAppContext(), Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)


                        CoroutineScope(Dispatchers.Main).launch {
                            delay(500)
                            latitude = "${list[0].latitude}"
                            longitude = "${list[0].longitude}"

                            location_text = "${list[0].locality}"
                            Log.d("TAG@123","location name"+location_text)

                        }

                        }catch (e:Exception){
                            Log.e("TAG@123","Location Exception : ${e.message}")
                        }
                    }

                }

            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
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
                Manifest.permission.ACCESS_FINE_LOCATION
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

}
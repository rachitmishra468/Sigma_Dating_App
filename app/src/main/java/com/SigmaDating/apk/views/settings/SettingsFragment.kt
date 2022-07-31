package com.SigmaDating.apk.views.settings

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
import android.text.method.DigitsKeyListener
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
import com.SigmaDating.apk.AppReseources
import com.SigmaDating.databinding.FragmentSettingsBinding
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.views.Home
import com.SigmaDating.apk.views.Splash
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

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
            Update_password();
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

        _binding.seekBar.addOnChangeListener { rangeSlider, value, fromUser ->
            val values = rangeSlider.values
            Log.d("TAG@123", "Start value: ${values[0]}, End value: ${values[1]}")
            Log.d("TAG@123", value.toString())
            //  _binding.textView11.text = "${values[0].toInt()}-${values[1].toInt()} miles"
            _binding.textView11.text = "${value.toString()} miles"

            distance = value.toString()
        }

        _binding.seekBarAge.addOnChangeListener { rangeSlider, value, fromUser ->
            val values = rangeSlider.values
            Log.d("TAG@123", "Start value: ${values[0]}, End value: ${values[1]}")
            _binding.textView8.text = "${values[0].toInt()}-${values[1].toInt()} "
            age_range = "${values[0].toInt()}-${values[1].toInt()}"

        }

        _binding.updateSetting.setOnClickListener {
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
            Log.d("TAG@123", "updateSetting :" + jsonObject.toString())
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
        subscribe_Login_User_details()

        subscribe_setting_update_details()

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

                    }
                    Status.ERROR -> {

                    }
                }
            })
    }


    fun subscribe_Login_User_details() {
        (activity as Home?)?.homeviewmodel?.get_user_data?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
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
                                //  _binding.seekBarAge.setValues(1.0f,5.0f);

                            }

                            if (it.data?.user?.distance?.isEmpty() == false) {
                                _binding.textView11.setText(it.data.user.distance + " miles")
                            }


                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
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

    fun Update_password() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.update_password_sheet_dialog, null)

        val current_password = view.findViewById<EditText>(R.id.current_password)
        val editText_password = view.findViewById<EditText>(R.id.editText_password)
        val editText_password_confirm = view.findViewById<EditText>(R.id.editText_password_confirm)

        val btnClose = view.findViewById<Button>(R.id.create_password)

        btnClose.setOnClickListener {

            if (AppUtils.isValid_password_match(
                    editText_password.text.toString(),
                    editText_password_confirm.text.toString()
                )
            ) {

                if (AppUtils.isValid_password(editText_password.text.toString())) {
                    subscribe_change_password(dialog)
                    (activity as Home).homeviewmodel.User_change_password(
                        (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID),
                        editText_password.text.toString(),
                        editText_password_confirm.text.toString()
                    )
                } else {
                    editText_password.setError("Please Enter Valid Password")
                }

            } else {

                Toast.makeText(context, "Password Does Not Match", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    fun Update_phone_location(phone: Boolean) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.update_phone_location, null)

        val title_text = view.findViewById<TextView>(R.id.textView5)
        val current_value = view.findViewById<EditText>(R.id.editText_password)
        val update_value = view.findViewById<Button>(R.id.update_value)
        val update_current = view.findViewById<Button>(R.id.update_current)

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
                    (activity as Home).homeviewmodel.update_phone_location(
                        (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID),
                        "phone", current_value.text.toString()
                    )
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
                        val geocoder = Geocoder(AppReseources.getAppContext(), Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)


                        CoroutineScope(Dispatchers.Main).launch {
                            delay(500)
                            latitude = "${list[0].latitude}"
                            longitude = "${list[0].longitude}"
                            // tvCountryName.text = "Country Name\n${list[0].countryName}"
                            // tvLocality.text = "Locality\n${list[0].locality}"
                            location_text = "${list[0].locality}"
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
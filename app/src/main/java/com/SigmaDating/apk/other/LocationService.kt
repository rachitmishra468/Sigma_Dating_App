package com.SigmaDating.apk.other

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.SigmaDating.apk.AppReseources
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.views.intro_registration.OnBoardingActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

open  class LocationService {


companion object {

    private val permissionId = 2
    var latitude = ""
    var longitude = ""
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    fun get_location(requireActivity: FragmentActivity) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AppReseources.getAppContext()!!)
        getLocation(requireActivity)
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    fun getLocation(requireActivity : FragmentActivity) {
        if (checkPermissions(requireActivity)) {
            if (isLocationEnabled(requireActivity)) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(AppReseources.getAppContext(), Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)


                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1000)
                            (requireActivity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                                AppConstants.latitude,
                                "${list[0].latitude}"
                            )
                            (requireActivity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                                AppConstants.longitude,
                                "${list[0].longitude}"
                            )

                            (requireActivity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                                AppConstants.location,
                                "${list[0].locality}"
                            )
                            Log.d("TAG@123","Location :- ${list[0].latitude} , ${list[0].longitude}")


                        }


                    }
                }

            } else {
                Toast.makeText(requireActivity, "Please turn on location", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            requestPermissions(requireActivity)
        }
    }
    private fun requestPermissions(requireActivity :FragmentActivity) {
        ActivityCompat.requestPermissions(
            requireActivity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    private fun checkPermissions(requireContext:FragmentActivity): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun isLocationEnabled(requireContext:FragmentActivity): Boolean {
        val locationManager: LocationManager =
            AppReseources.getAppContext()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


}


}
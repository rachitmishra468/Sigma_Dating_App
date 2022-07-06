package com.sigmadatingapp.views.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.demoapp.other.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sigmadatingapp.R
import com.sigmadatingapp.databinding.FragmentSettingsBinding
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.utilities.AppUtils
import com.sigmadatingapp.views.Home
import com.sigmadatingapp.views.Splash
import com.sigmadatingapp.views.intro_registration.OnBoardingActivity

class SettingsFragment : Fragment() {

    lateinit var _binding: FragmentSettingsBinding
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        _binding.passwordUpdate.setOnClickListener {
            Update_password();
        }
        _binding.imageView2.setOnClickListener {
            (activity as Home).onBackPressed()
        }

        _binding.continueLogout.setOnClickListener {
            (activity as Home?)?.sharedPreferencesStorage?.setValue(
                AppConstants.IS_AUTHENTICATED,
                false
            )
            startActivity(Intent(requireContext(), Splash::class.java))
            (activity as Home?)?.finish()
        }
        subscribe_Login_User_details()

        (activity as Home).homeviewmodel.get_Login_User_details(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )

        return binding.root
    }

    fun subscribe_Login_User_details() {
        (activity as Home?)?.homeviewmodel?.get_user_data?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            Log.d("TAG@123", "111"+res.toString())
                            _binding.textEmailId.setText(it.data?.user?.email)
                            _binding.phoneNumberText.setText(it.data?.user?.phone)
                            _binding.locationText.setText(it.data?.user?.location)

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

                }
            }
        })
    }

    fun subscribe_change_password(dialog: BottomSheetDialog) {
        (activity as Home?)?.homeviewmodel?.change_password?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            dialog.dismiss()
                            AppUtils.hideLoader()
                            Log.d("TAG@123", "112"+res.toString())
                            res.message.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
                        } else {
                            res!!.message.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }

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

                if (AppUtils.isValid_password(current_password.text.toString())) {
                    subscribe_change_password(dialog)
                    (activity as Home).homeviewmodel.User_change_password(
                        (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID),
                        editText_password.text.toString(),
                        editText_password_confirm.text.toString()
                    )
                } else {
                    current_password.setError("Please Enter Valid Password")
                }

            } else {

                Toast.makeText(context, "Password Does Not Match", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

}
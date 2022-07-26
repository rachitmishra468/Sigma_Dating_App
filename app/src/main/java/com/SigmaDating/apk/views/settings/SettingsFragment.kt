package com.SigmaDating.apk.views.settings

import android.content.Intent
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
import androidx.lifecycle.Observer
import com.example.demoapp.other.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.SigmaDating.R
import com.SigmaDating.databinding.FragmentSettingsBinding
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.views.Home
import com.SigmaDating.apk.views.Splash

class SettingsFragment : Fragment() {

    lateinit var _binding: FragmentSettingsBinding
    private val binding get() = _binding

    fun Call_links() {

        var link: String = "http://103.10.234.134/sigmadating/terms.php"
        _binding.licencesText.setOnClickListener {
            link = Home.get_settingpage_data("licenses")
            (activity as Home).OpenSocial(link)
        }
        _binding.privacyText.setOnClickListener {
            link = Home.get_settingpage_data("privacy-preferences")
            (activity as Home).OpenSocial(link)
        }

        _binding.privacyTextTwo.setOnClickListener {
            link = Home.get_settingpage_data("terms-of-service")
            (activity as Home).OpenSocial(link)
        }

        _binding.termsServices.setOnClickListener {
            link = Home.get_settingpage_data("terms-of-service")
            (activity as Home).OpenSocial(link)
        }
        _binding.contactUs.setOnClickListener {
            link = Home.get_settingpage_data("support")
            (activity as Home).OpenSocial(link)
        }

        _binding.community.setOnClickListener {
            link = Home.get_settingpage_data("community-guidelines")
            (activity as Home).OpenSocial(link)
        }

        _binding.sefery.setOnClickListener {
            link = Home.get_settingpage_data("safety-guidelines")
            (activity as Home).OpenSocial(link)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        (activity as Home).homeviewmodel.get_Login_User_details(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )

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
    fun subscribe_Login_User_details() {
        (activity as Home?)?.homeviewmodel?.get_user_data?.observe(this, Observer {
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
                            if (it.data?.user?.location?.isEmpty() == true) {
                                _binding.locationText.setText("Update")
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
        (activity as Home?)?.homeviewmodel?.change_password?.observe(this, Observer {
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

        title_text.setText(
            if (phone) {
                "Update Phone Number."
            } else {
                "Update Location With Post Code."
            }
        )
        current_value.setHint(
            if (phone) {
                current_value.inputType= InputType.TYPE_CLASS_NUMBER
                "Enter Phone Number."
            } else {
                current_value.inputType= InputType.TYPE_CLASS_TEXT
                "Enter Location With Post Code."
            }
        )


        update_value.setOnClickListener {
            if (!current_value.text.toString().isEmpty()) {
                subscribe_change_password(dialog)
                var key =if (phone) {
                    "phone"
                } else {
                    "location"
                }
                (activity as Home).homeviewmodel.update_phone_location(
                    (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID),
                    key,current_value.text.toString()
                )
            } else {
                current_value.setError("Please Enter .. ")
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


}
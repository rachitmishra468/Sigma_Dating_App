package com.sigmadatingapp.views.intro_registration

import android.R.string
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hbb20.CountryCodePicker
import com.sigmadatingapp.R
import com.sigmadatingapp.databinding.AboutBirthdayBinding
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.utilities.AppUtils


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment3.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment3 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var button_birthday: Button
    lateinit var editbirthday: EditText
    private var ss: String? = null
    lateinit var email_id: EditText
    lateinit var country_spinner: CountryCodePicker

lateinit var edit_text_phone:EditText
    private var binding: AboutBirthdayBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AboutBirthdayBinding.inflate(inflater, container, false)
        button_birthday = binding!!.root.findViewById(R.id.button_birthday)
        country_spinner = binding!!.root.findViewById(R.id.ccp)
        email_id = binding!!.root.findViewById(R.id.edit_emal)
        editbirthday = binding!!.root.findViewById<EditText>(R.id.edit_text_birthday)
        edit_text_phone=binding!!.root.findViewById(R.id.edit_text_phone)
        editbirthday.addTextChangedListener(textWatcher)
        button_birthday.setOnClickListener {

            if (AppUtils.checkIfEmailIsValid(email_id.text.toString()) != null) {
                email_id.error = "Invalid Email Address"

            } else if (editbirthday.text.toString().isEmpty() || !AppUtils.isValidDate(editbirthday.text.toString().trim())
            ) {

                Toast.makeText(requireActivity(), "Enter Valid Date of Birth", Toast.LENGTH_LONG)
                    .show()

            } else {

                if (editbirthday.text.toString().isEmpty()||!AppUtils.isValidDate(editbirthday.text.toString().trim())) {

                    Toast.makeText(requireActivity(),"Enter Valid Date of Birth",Toast.LENGTH_LONG).show()
                }
                else{

                    (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                        AppConstants.email,
                        email_id.text.toString()
                    )
                    (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                        AppConstants.Dob,
                        editbirthday.text.toString()
                    )


                    (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                        AppConstants.USER_COUNTRY_CODE,
                        country_spinner.selectedCountryCodeWithPlus
                    )


                    (activity as OnBoardingActivity?)?.setCurrentItem(3, true)
                   // (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(AppConstants.Dob,editbirthday.text.toString())
                  //  (activity as OnBoardingActivity?)?.setCurrentItem(3, true)
                }



            }
        }



        return binding!!.root
    }


    fun onCountryPickerClick_fag(view: View?) {
        country_spinner.setOnCountryChangeListener(CountryCodePicker.OnCountryChangeListener {
            (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                AppConstants.USER_COUNTRY_CODE,
                country_spinner.getSelectedCountryCodeWithPlus()
            )
        })
    }


    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {


        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
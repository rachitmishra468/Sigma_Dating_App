package com.SigmaDating.app.views.intro_registration

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.SigmaDating.R
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import java.text.SimpleDateFormat
import java.util.*

import android.view.*
import androidx.lifecycle.MutableLiveData
import com.SigmaDating.app.model.Loginmodel
import com.SigmaDating.app.utilities.PhoneTextWatcher
import com.SigmaDating.databinding.AboutBirthdayBinding
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status


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
    lateinit var editbirthday: TextView
    private var ss: String? = null
    lateinit var email_id: EditText
    lateinit var country_spinner: Spinner
    lateinit var constraint_f1: ConstraintLayout
    lateinit var edit_text_phone: EditText
    private var binding: AboutBirthdayBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AboutBirthdayBinding.inflate(inflater, container, false)
        button_birthday = binding!!.root.findViewById(R.id.button_birthday)
        country_spinner = binding!!.root.findViewById(R.id.ccp)
        email_id = binding!!.root.findViewById(R.id.edit_emal)
        constraint_f1 = binding!!.root.findViewById(R.id.constraint_f1)
        editbirthday = binding!!.root.findViewById<TextView>(R.id.edit_text_birthday)
        edit_text_phone = binding!!.root.findViewById(R.id.edit_text_phone)


        edit_text_phone.addTextChangedListener(PhoneTextWatcher(edit_text_phone))
        val Country_code = resources.getStringArray(R.array.Country_code)

        // access the spinner
        if (country_spinner != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, Country_code
            )
            country_spinner.adapter = adapter
        }



        editbirthday.setOnClickListener {
            date_picker()
        }

        button_birthday.setOnClickListener {

            if (AppUtils.checkIfEmailIsValid(email_id.text.toString()) != null) {
                AppUtils.showErrorSnackBar(requireContext(), constraint_f1, "Invalid Email Address")
            } else if (edit_text_phone.text.toString().isEmpty() || !AppUtils.isValid_phone_number(
                    edit_text_phone.text.toString().trim()
                )
            ) {
                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "Enter Valid Phone Number"
                )

            } else {

                if (editbirthday.text.toString()
                        .isEmpty() || !AppUtils.isValidDate(editbirthday.text.toString().trim())
                ) {

                    AppUtils.showErrorSnackBar(
                        requireContext(),
                        constraint_f1,
                        "Enter Valid Date of Birth(MM/DD/YYYY)"
                    )

                } else {

                    (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                        AppConstants.email,
                        email_id.text.toString()
                    )
                    (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                        AppConstants.Dob,
                        editbirthday.text.toString()
                    )
                    /*Todo Remove +91 to +1 when we done testing */
                    (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                        AppConstants.phone,   country_spinner.selectedItem.toString() + edit_text_phone.text.toString()
                    )

                    val ee = AppUtils.getAgeDiffernce(editbirthday.text.toString())
                    Log.d("TAG@123", ee.toString())


                    (activity as OnBoardingActivity?)?.userRegister?.email_validate = MutableLiveData<Resource<Loginmodel>>()

                    (activity as OnBoardingActivity?)?.userRegister?.email_validation_check(
                        email_id.text.toString(),
                        country_spinner.selectedItem.toString()+edit_text_phone.text.toString(),
                        editbirthday.text.toString()
                    )

                    checkEmailValidation()

                }


            }
        }



        return binding!!.root
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


    fun date_picker() {
        // Get instance of calendar
        // mCalendar will be set to current/today's date
        val mCalendar = Calendar.getInstance()

        // Creating a simple calendar dialog.
        // It was 9 Aug 2021 when this program was developed.
        val mDialog = DatePickerDialog(requireContext(), { _, mYear, mMonth, mDay ->
            mCalendar[Calendar.YEAR] = mYear
            mCalendar[Calendar.MONTH] = mMonth
            mCalendar[Calendar.DAY_OF_MONTH] = mDay
            val myFormat = "MM/dd/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            editbirthday.text = sdf.format(mCalendar.time)

        }, mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH], mCalendar[Calendar.DAY_OF_MONTH])

        val minDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)  //31
        val minMonth = Calendar.getInstance().get(Calendar.MONTH)
        val minYear = Calendar.getInstance().get(Calendar.YEAR) - 30;
        mCalendar.set(minYear, minMonth, minDay)
        /*if we need min and max date please uncomment these lines*/
       // mDialog.datePicker.minDate = mCalendar.timeInMillis


        val maxDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)  //31
        val maxMonth = Calendar.getInstance().get(Calendar.MONTH)  //12
        val maxYear = Calendar.getInstance().get(Calendar.YEAR) ;
        mCalendar.set(maxYear, maxMonth , maxDay)
        mDialog.datePicker.maxDate = mCalendar.timeInMillis

        // Display the calendar dialog
        mDialog.show()

    }


    fun checkEmailValidation() {
        (activity as OnBoardingActivity?)?.userRegister?.email_validate?.observe(
            requireActivity(),
            androidx.lifecycle.Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data?.let { res ->
                            if (res.status) {
                                Log.d("TAG@123","status ${res.message}")
                                (activity as OnBoardingActivity?)?.setCurrentItem(5, true)
                            } else {
                                Log.d("TAG@123","status ${res.message}")
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG).show()
                                if(res.message.contains("age")){
                                    (activity as OnBoardingActivity?)?.finish()
                                } else {
                                    
                                }

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

}
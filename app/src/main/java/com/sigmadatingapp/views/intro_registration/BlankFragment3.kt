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
    lateinit var editbirthday:EditText
    private var ss:String?=null

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
    ): View? {
        // Inflate the layout for this fragment
        // var root= inflater.inflate(R.layout.about_birthday, container, false)
        binding = AboutBirthdayBinding.inflate(inflater, container, false)
        button_birthday = binding!!.root.findViewById(R.id.button_birthday)
        editbirthday=binding!!.root.findViewById<EditText>(R.id.edit_text_birthday)
        editbirthday.addTextChangedListener(textWatcher)
        button_birthday.setOnClickListener {
            
            if (!editbirthday.text.toString().isEmpty()||AppUtils.isValidDate(editbirthday.text.toString().trim())) {
                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(AppConstants.Dob,editbirthday.text.toString())
                (activity as OnBoardingActivity?)?.setCurrentItem(3, true)

            }
            else{
                Toast.makeText(requireActivity(),"Enter Valid Date of Birth",Toast.LENGTH_LONG).show()
            }
        }

       // binding!!.editTextBirthday.addTextChangedListener()


        return binding!!.root
    }
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if(editbirthday.text.length==2 ||editbirthday.text.length==5){
                editbirthday.append("/")
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
/*var data=editbirthday.text.toString()
            val sb: StringBuilder = StringBuilder(string)
            sb.deleteCharAt(3)*/
           // Toast.makeText(requireContext(), "Maximum Limit Reached", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment3.
         */
        // TODO: Rename and change types and number of parameters
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
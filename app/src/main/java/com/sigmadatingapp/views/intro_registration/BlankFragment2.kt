package com.sigmadatingapp.views.intro_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sigmadatingapp.R
import com.sigmadatingapp.storage.AppConstants

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment2.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment2 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var radioGroup: RadioGroup? = null

    lateinit var radioButtonSelect: RadioButton
    lateinit var continue_second: Button
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
        var root = inflater.inflate(R.layout.about_yourself_layout2, container, false)
        radioGroup = root.findViewById(R.id.rg)
        continue_second = root.findViewById(R.id.continue_second)
        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                continue_second.setBackgroundResource(R.drawable.signup_circle_bg)
                continue_second.setTextColor(resources.getColor(R.color.white))
            }
        }
        continue_second.setOnClickListener {
            val selectedId = radioGroup?.getCheckedRadioButtonId()
            if (selectedId == -1) {
                Toast.makeText(activity, "Select one field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            radioButtonSelect = root.findViewById(selectedId!!)
            Toast.makeText(activity, radioButtonSelect.text, Toast.LENGTH_SHORT).show()
            (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                AppConstants.Gender,
                radioButtonSelect.text
            )
            (activity as OnBoardingActivity?)?.setCurrentItem(2, true)
        }

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.SigmaDating.app.views.intro_registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.SigmaDating.R
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils


class Showme : Fragment() {

    var radioGroup: RadioGroup? = null

    lateinit var radioButtonSelect: RadioButton
    lateinit var continue_second: Button
    lateinit var constraint_f1: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root =  inflater.inflate(R.layout.fragment_showme, container, false)
        radioGroup = root.findViewById(R.id.rg_showme)
        continue_second = root.findViewById(R.id.continue_showme)
        constraint_f1 = root.findViewById(R.id.constraint_f1)
        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                continue_second.setBackgroundResource(R.drawable.signup_circle_bg)
                continue_second.setTextColor(resources.getColor(R.color.white))
            }
        }
        continue_second.setOnClickListener {
            val selectedId = radioGroup?.getCheckedRadioButtonId()
            if (selectedId == -1) {
                AppUtils.showErrorSnackBar(requireContext(), constraint_f1, "Select one field")
                return@setOnClickListener
            }

            radioButtonSelect = root.findViewById(selectedId!!)
            (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                AppConstants.showme,
                radioButtonSelect.text
            )
            (activity as OnBoardingActivity?)?.setCurrentItem(4, true)
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Showme().apply {
                arguments = Bundle().apply {

                }
            }
    }


}
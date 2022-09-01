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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GenderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GenderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var radioGroup: RadioGroup? = null

    lateinit var radioButtonSelect: RadioButton
    lateinit var continue_second: Button
    lateinit var constraint_f1: ConstraintLayout

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

        var root = inflater.inflate(R.layout.fragment_gender, container, false)
        radioGroup = root.findViewById(R.id.rg)
        continue_second = root.findViewById(R.id.continue_second)
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
                AppConstants.gender,
                radioButtonSelect.text
            )
            (activity as OnBoardingActivity?)?.setCurrentItem(2, true)
        }

        return root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GenderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
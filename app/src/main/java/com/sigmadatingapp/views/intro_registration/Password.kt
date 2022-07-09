package com.sigmadatingapp.views.intro_registration

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.sigmadatingapp.R
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.utilities.AppUtils
import com.sigmadatingapp.views.Home

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Password : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var editText_password: EditText
    lateinit var editText_password_confirm: EditText
    lateinit var constraint_f1:ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var create_password: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_password, container, false)
        editText_password = view.findViewById(R.id.editText_password)
        editText_password_confirm = view.findViewById(R.id.editText_password_confirm)
        constraint_f1= view.findViewById(R.id.constraint_f1)
        create_password = view.findViewById(R.id.create_password)
        create_password?.setOnClickListener {

            if (!AppUtils.isValid_password(editText_password.text.toString())) {
                editText_password.error = "Password Length Must be of 6-8"
                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "Password Length Must be of 6-8"
                )
            } else if (!AppUtils.isValid_password(editText_password_confirm.text.toString())) {
                editText_password_confirm.error = "Password Length Must be of 6-8"
                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "Password Length Must be of 6-8"
                )
            }else if (!editText_password_confirm.text.toString().equals(editText_password.text.toString())) {
                editText_password_confirm.error = "Password does not Match"
                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "Password does not Match"
                )
            } else {
                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                    AppConstants.password,
                    editText_password_confirm.text.toString()
                )

                (activity as OnBoardingActivity?)?.setCurrentItem(5, true)
            }

        }



        return view;
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Password().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
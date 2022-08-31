package com.SigmaDating.app.views.intro_registration

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.SigmaDating.R
import com.SigmaDating.app.AppReseources
import com.SigmaDating.app.other.LocationService
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.utilities.PasswordStrength

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Password : Fragment(), TextWatcher {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var editText_password: EditText
    lateinit var strengthView: TextView

    lateinit var progressBar: ProgressBar
    lateinit var editText_password_confirm: EditText
    lateinit var constraint_f1: ConstraintLayout


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
        editText_password.addTextChangedListener(this)
        editText_password_confirm = view.findViewById(R.id.editText_password_confirm)
        constraint_f1 = view.findViewById(R.id.constraint_f1)
        create_password = view.findViewById(R.id.create_password)
        progressBar = view.findViewById(R.id.progressBar) as ProgressBar
        strengthView = view.findViewById(R.id.password_strength) as TextView
        LocationService.get_location(requireActivity())
        create_password?.setOnClickListener {

            if (!AppUtils.isValid_password(editText_password.text.toString())) {
                editText_password.error =
                    "Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character"
                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character"
                )
            } else if (!AppUtils.isValid_password(editText_password_confirm.text.toString())) {
                editText_password_confirm.error =
                    "Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character"
                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character"
                )
            } else if (!editText_password_confirm.text.toString()
                    .equals(editText_password.text.toString())
            ) {
                editText_password_confirm.error = "Password Does Not Match"
                AppUtils.showErrorSnackBar(
                    requireContext(),
                    constraint_f1,
                    "Password Does Not Match"
                )
            } else {
                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                    AppConstants.password,
                    editText_password_confirm.text.toString()
                )

                (activity as OnBoardingActivity?)?.setCurrentItem(6, true)
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

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        updatePasswordStrengthView(p0.toString())
    }

    override fun afterTextChanged(p0: Editable?) {
    }


    private fun updatePasswordStrengthView(password: String) {


        if (TextView.VISIBLE != strengthView.visibility)
            return

        if (TextUtils.isEmpty(password)) {
            strengthView.text = ""
            progressBar.progress = 0
            return
        }

        val str = PasswordStrength.calculateStrength(password)
        strengthView.text = str.getText(AppReseources.getAppContext()!!)
        strengthView.setTextColor(str.color)

        progressBar.progressDrawable.setColorFilter(
            str.color,
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        if (str.getText(AppReseources.getAppContext()!!) == "Weak") {
            progressBar.progress = 25
        } else if (str.getText(AppReseources.getAppContext()!!) == "Medium") {
            progressBar.progress = 50
        } else if (str.getText(AppReseources.getAppContext()!!) == "Strong") {
            progressBar.progress = 75
        } else {
            progressBar.progress = 100
        }
    }
}
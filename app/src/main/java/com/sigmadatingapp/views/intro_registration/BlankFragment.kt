package com.sigmadatingapp.views.intro_registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.sigmadatingapp.R
import com.sigmadatingapp.databinding.AboutYourselfLayoutBinding
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.utilities.AppUtils


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var continue_first: Button
    private var editName: EditText? = null
    var editlastName: EditText? = null
    lateinit var constraint_f1: ConstraintLayout
    private var binding: AboutYourselfLayoutBinding? = null

   /* @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AboutYourselfLayoutBinding.inflate(inflater, container, false)
        continue_first = binding!!.root.findViewById(R.id.continue_first)
        constraint_f1 = binding!!.root.findViewById(R.id.constraint_f1)
        editlastName=binding!!.root.findViewById(R.id.et_lastname)
        editName=binding!!.root.findViewById(R.id.editText_name)
        editName?.addTextChangedListener(textWatcher)
        editlastName?.addTextChangedListener(textWatcher)
        continue_first.setOnClickListener {
            val fnmame = editName?.text.toString()
            val lastname = editlastName?.text.toString()

            if (AppUtils.isNetworkInterfaceAvailable(requireActivity())) {
                if (AppUtils.checkValidationOnFisrtStep(requireActivity(), constraint_f1, fnmame, lastname)) {
                    AppUtils.hideSoftKeyboard(requireActivity(), constraint_f1)
                    (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(AppConstants.fisrtname, fnmame)
                    (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(AppConstants.Lastname, lastname)
                    (activity as OnBoardingActivity?)?.setCurrentItem(1, true)
                }

            } else {
                Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
            }
        }
        return binding!!.root
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

            val editName: String = editName?.getText().toString()
            val editlastName: String = editlastName?.getText().toString()
            // check whether both the fields are empty or not
            if (!editName.isEmpty() && !editlastName.isEmpty()){
                continue_first.setEnabled(!editName.isEmpty() && !editlastName.isEmpty())
                Log.d("SIGMA_APP","valid condition")
            }
            else{ continue_first.setEnabled(false)}

        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
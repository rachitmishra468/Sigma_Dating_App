package com.sigmadatingapp.views.intro_registration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.sigmadatingapp.R
import com.sigmadatingapp.databinding.FragmentSchoolInputBinding
import com.sigmadatingapp.views.Home

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class BlankFragment4 : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var about_school_binding:FragmentSchoolInputBinding?=null
    private var continueSchool:Button?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        about_school_binding= FragmentSchoolInputBinding.inflate(inflater, container, false)
        continueSchool=    about_school_binding?.root?.findViewById(R.id.continue_school)
        continueSchool?.setOnClickListener {

            (activity as OnBoardingActivity?)?.setCurrentItem(4, true)

        }
        return  about_school_binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    companion object{

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment4().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }

}
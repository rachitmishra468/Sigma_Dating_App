package com.sigmadatingapp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sigmadatingapp.databinding.FragmentSchoolInputBinding
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class BlankFragment4 : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var about_school:FragmentSchoolInputBinding?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       about_school= FragmentSchoolInputBinding.inflate(inflater, container, false)
        return  about_school?.root
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
            BlankFragment3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }

}
package com.sigmadatingapp.views.intro_registration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    private var Socority_button:Button?=null
    private var fraternity_button:Button?=null
    private var fraternity_autocomplte:AutoCompleteTextView?=null
    private var edit_school:EditText?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        about_school_binding= FragmentSchoolInputBinding.inflate(inflater, container, false)
        continueSchool=    about_school_binding?.root?.findViewById(R.id.continue_school)
        edit_school=    about_school_binding?.root?.findViewById(R.id.editText_school_name)
        fraternity_autocomplte=    about_school_binding?.root?.findViewById(R.id.et_type)
        Socority_button=about_school_binding?.root?.findViewById(R.id.Socority_button)
        fraternity_button=about_school_binding?.root?.findViewById(R.id.fraternity_button)
        continueSchool?.setOnClickListener {
            (activity as OnBoardingActivity?)?.setCurrentItem(4, true)

        }
        fraternity_button?.setOnClickListener {
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            Socority_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.black))
            Socority_button?.setTextColor(this.getResources().getColor(R.color.white))

        }

        Socority_button?.setOnClickListener {
            Socority_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            Socority_button?.setTextColor(this.getResources().getColor(R.color.black))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.white))

        }
        val languages = resources.getStringArray(R.array.Fraternity)
        val adapter = ArrayAdapter(requireActivity(),
            android.R.layout.simple_list_item_1, languages)
        fraternity_autocomplte?.threshold=1
        fraternity_autocomplte?.setAdapter(adapter)
        fraternity_autocomplte?.setOnItemClickListener { adapterView, view, i, l ->
Toast.makeText(requireActivity(),adapter.getItem(i).toString(),Toast.LENGTH_LONG).show()

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
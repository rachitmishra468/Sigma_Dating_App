package com.sigmadatingapp.views.intro_registration

import FraternitiesList
import School_CommunityResponse
import SororitiesList
import UniversityList
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.demoapp.other.Status
import com.sigmadatingapp.R
import com.sigmadatingapp.adapters.CommunityAdapter
import com.sigmadatingapp.databinding.FragmentSchoolInputBinding
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.utilities.AppUtils
import com.sigmadatingapp.views.Home
import com.sigmadatingapp.views.login.LoginViewModel

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
    val mainViewModel: RegistrationViewModel by viewModels()
    private var edit_school:AutoCompleteTextView?=null
    lateinit var fraternitiesList:List<FraternitiesList>
    lateinit var sororitiesList:List<SororitiesList>
    lateinit var schoolList:List<UniversityList>


    var sa = arrayOf("lorem ipsum", "lorem ipsum", " ipsum lorem ipsum", "lorem ipsum lorem ipsum", "lorem ipsuma")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        about_school_binding= FragmentSchoolInputBinding.inflate(inflater, container, false)
        continueSchool=    about_school_binding?.root?.findViewById(R.id.continue_school)
        edit_school=    about_school_binding?.root?.findViewById(R.id.act_scholl)
        fraternity_autocomplte=    about_school_binding?.root?.findViewById(R.id.et_type)
        Socority_button=about_school_binding?.root?.findViewById(R.id.Socority_button)
        fraternity_button=about_school_binding?.root?.findViewById(R.id.fraternity_button)
        schoolListResponse()


        val adapter = ArrayAdapter(requireActivity(),
            android.R.layout.simple_list_item_1, sa)
        continueSchool?.setOnClickListener {
            (activity as OnBoardingActivity?)?.setCurrentItem(4, true)

        }

        requireActivity().let { ctx ->
            val cityAdapter = CommunityAdapter(ctx, R.layout.customautotextview_layout, schoolList)
            edit_school?.setAdapter(cityAdapter)
            edit_school?.setOnItemClickListener { parent, _, position, _ ->
                val city = cityAdapter.getItem(position) as UniversityList?
                edit_school?.setText(city?.name)
            }

        }
        fraternity_button?.setOnClickListener {
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            Socority_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.black))
            Socority_button?.setTextColor(this.getResources().getColor(R.color.white))
            sa = arrayOf("lorem ipsum", "lorem ipsum", " ipsum lorem ipsum", "lorem ipsum lorem ipsum", "lorem ipsuma")
            val adapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_list_item_1, fraternitiesList)
            adapter.setNotifyOnChange(true)
            fraternity_autocomplte?.threshold=1
        }

        Socority_button?.setOnClickListener {
            Socority_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            Socority_button?.setTextColor(this.getResources().getColor(R.color.black))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.white))

             sa = arrayOf("test", "lorem ipsum", " ipsum lorem ipsum", "lorem ipsum lorem ipsum", "lorem ipsuma")
            val adapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_list_item_1, sa)
            adapter.setNotifyOnChange(true)
            fraternity_autocomplte?.threshold=1

        }

        adapter.setNotifyOnChange(true)
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

    fun schoolListResponse(){

        mainViewModel.responseserver?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                             schoolList=res.data.universityList
                            sororitiesList=res.data.sororitiesList
                            fraternitiesList=res.data.fraternitiesList


                            //sharedPreferencesStorage.setValue(AppConstants.IS_AUTHENTICATED, true)
                        } else {

                        }
                    }
                }
                Status.LOADING -> {
                    AppUtils.showLoader(requireActivity())
                }
                Status.ERROR -> {

                }
            }
        })

    }

}
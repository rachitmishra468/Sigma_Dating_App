package com.sigmadatingapp.views.intro_registration


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.demoapp.other.Status
import com.sigmadatingapp.R
import com.sigmadatingapp.adapters.CommunityAdapter
import com.sigmadatingapp.adapters.FraternitiesAdapter
import com.sigmadatingapp.adapters.SororitAdapter
import com.sigmadatingapp.databinding.FragmentSchoolInputBinding
import com.sigmadatingapp.model.communityModel.FraternitiesList
import com.sigmadatingapp.model.communityModel.SororitiesList
import com.sigmadatingapp.model.communityModel.UniversityList
import com.sigmadatingapp.utilities.AppUtils
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class BlankFragment4 : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var about_school_binding: FragmentSchoolInputBinding? = null
    private var continueSchool: Button? = null
    private var Socority_button: Button? = null
    private var fraternity_button: Button? = null
    private var fraternity_autocomplte: AutoCompleteTextView? = null
    private var schoolAct_Search: AutoCompleteTextView? = null
    var fraternitiesList: List<FraternitiesList>? = null
    var sororitiesList: List<SororitiesList>? = null
    var schoolList: List<UniversityList>? = null


    var sa = arrayOf(
        "lorem ipsum",
        "lorem ipsum",
        " ipsum lorem ipsum",
        "lorem ipsum lorem ipsum",
        "lorem ipsuma"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        about_school_binding = FragmentSchoolInputBinding.inflate(inflater, container, false)
        continueSchool = about_school_binding?.root?.findViewById(R.id.continue_school)
        schoolAct_Search = about_school_binding?.root?.findViewById(R.id.act_scholl)
        fraternity_autocomplte = about_school_binding?.root?.findViewById(R.id.et_type)
        Socority_button = about_school_binding?.root?.findViewById(R.id.Socority_button)
        fraternity_button = about_school_binding?.root?.findViewById(R.id.fraternity_button)



        continueSchool?.setOnClickListener {

            if (schoolAct_Search?.text.toString().equals("")) {
                Toast.makeText(
                    requireActivity(),
                    "Enter School Name",
                    Toast.LENGTH_LONG
                )
            } else {

                (activity as OnBoardingActivity?)?.setCurrentItem(4, true)
            }


        }


        fraternity_button?.setOnClickListener {
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            Socority_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.black))
            Socority_button?.setTextColor(this.getResources().getColor(R.color.white))

            fraternity_autocomplte?.setText("")
            fraternity_autocomplte?.setHint("Select Fraternity")
            val schoolAdapter = FraternitiesAdapter(
                requireActivity(),
                R.layout.customautotextview_layout,
                fraternitiesList!!
            )
            fraternity_autocomplte?.threshold = 1
            fraternity_autocomplte?.setAdapter(schoolAdapter)
            fraternity_autocomplte?.setOnItemClickListener { parent, _, position, _ ->
                val city = schoolAdapter.getItem(position) as FraternitiesList?
                fraternity_autocomplte?.setText(city?.name)
            }
        }

        Socority_button?.setOnClickListener {
            Socority_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            Socority_button?.setTextColor(this.resources.getColor(R.color.black))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.white))
            fraternity_autocomplte?.setText("")
            fraternity_autocomplte?.setHint("Select Sorority")
            val schoolAdapter = SororitAdapter(
                requireActivity(),
                R.layout.customautotextview_layout,
                sororitiesList!!
            )
            fraternity_autocomplte?.threshold = 1
            fraternity_autocomplte?.setAdapter(schoolAdapter)
            fraternity_autocomplte?.setOnItemClickListener { parent, _, position, _ ->
                val city = schoolAdapter.getItem(position) as SororitiesList?
                fraternity_autocomplte?.setText(city?.name)
            }


        }


        return about_school_binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        schoolListResponse()
        (activity as OnBoardingActivity?)?.userRegister?.getSchoolingData()
    }


    fun schoolListResponse() {

        (activity as OnBoardingActivity?)?.userRegister?.school_dataResponse?.observe(
            requireActivity(),
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { it1 ->
                            if (it1?.status == true) {
                                Log.d("TAG@123", it.toString())
                                schoolList = ArrayList<UniversityList>()
                                schoolList = it1.data.universityList
                                sororitiesList = ArrayList<SororitiesList>()
                                sororitiesList = it1.data.sororitiesList
                                fraternitiesList = ArrayList<FraternitiesList>()
                                fraternitiesList = it1.data.fraternitiesList
                                setAdapterData()

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

    private fun setAdapterData() {
        val schoolAdapter =
            CommunityAdapter(requireActivity(), R.layout.customautotextview_layout, schoolList!!)
        schoolAct_Search?.threshold = 1
        schoolAct_Search?.setAdapter(schoolAdapter)
        schoolAct_Search?.setOnItemClickListener { parent, _, position, _ ->
            val city = schoolAdapter.getItem(position) as UniversityList?
            schoolAct_Search?.setText(city?.name)
        }


        val adapter = FraternitiesAdapter(
            requireActivity(),
            R.layout.customautotextview_layout,
            fraternitiesList!!
        )
        fraternity_autocomplte?.threshold = 1
        fraternity_autocomplte?.setAdapter(adapter)
        fraternity_autocomplte?.setOnItemClickListener { parent, _, position, _ ->
            val city = adapter.getItem(position) as FraternitiesList?
            fraternity_autocomplte?.setText(city?.name)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

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
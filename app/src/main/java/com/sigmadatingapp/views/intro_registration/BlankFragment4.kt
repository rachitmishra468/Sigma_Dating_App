package com.sigmadatingapp.views.intro_registration


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.demoapp.other.Status
import com.sigmadatingapp.R
import com.sigmadatingapp.adapters.CommunityAdapter
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
    private val mainViewModel: RegistrationViewModel by viewModels()
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

        schoolListResponse()

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
          /*  val adapter =  ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, sa!!)
            fraternity_autocomplte?.threshold=1
            fraternity_autocomplte?.setAdapter(adapter)
            fraternity_autocomplte?.setOnItemClickListener { adapterView, view, i, l ->
                Toast.makeText(requireActivity(),adapter.getItem(i).toString(),Toast.LENGTH_LONG).show()

            }*/

        }


        return about_school_binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    fun schoolListResponse() {

        mainViewModel.responseserver?.observe(requireActivity(), Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { it1 ->
                        if (it1?.status == true) {
                            schoolList = ArrayList<UniversityList>()
                            schoolList = it1.data.universityList
                            sororitiesList = ArrayList<SororitiesList>()
                            sororitiesList = it1.data.sororitiesList
                            fraternitiesList = ArrayList<FraternitiesList>()
                            fraternitiesList = it1.data.fraternitiesList


                            setAdapterData()
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

    private fun setAdapterData() {
        val schoolAdapter = CommunityAdapter(requireActivity(), R.layout.customautotextview_layout, schoolList!!)
        schoolAct_Search?.threshold = 1
        schoolAct_Search?.setAdapter(schoolAdapter)
        schoolAct_Search?.setOnItemClickListener { parent, _, position, _ ->
            val city = schoolAdapter.getItem(position) as UniversityList?
            schoolAct_Search?.setText(city?.name)
        }


      /*  val adapter =  CommunityAdapter(requireActivity(), R.layout.customautotextview_layout, schoolList!!)
        fraternity_autocomplte?.threshold=1
        fraternity_autocomplte?.setAdapter(adapter)
        fraternity_autocomplte?.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(requireActivity(),adapter.getItem(i).toString(),Toast.LENGTH_LONG).show()

        }*/
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
package com.SigmaDating.apk.views.intro_registration


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.demoapp.other.Status
import com.SigmaDating.R
import com.SigmaDating.apk.adapters.CommunityAdapter
import com.SigmaDating.databinding.FragmentSchoolInputBinding
import com.SigmaDating.apk.model.communityModel.UniversityList
import com.SigmaDating.apk.storage.AppConstants
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
    lateinit var fraternity_Spinner: Spinner
    private var schoolAct_spinner: Spinner? = null
    var fraternitiesList: List<UniversityList>? = null
    var sororitiesList: List<UniversityList>? = null
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
        schoolAct_spinner = about_school_binding?.root?.findViewById(R.id.schoolAct_spinner)
        fraternity_Spinner = about_school_binding?.root?.findViewById(R.id.et_type)!!
        Socority_button = about_school_binding?.root?.findViewById(R.id.Socority_button)
        fraternity_button = about_school_binding?.root?.findViewById(R.id.fraternity_button)


        continueSchool?.setOnClickListener {

            if (schoolAct_spinner?.selectedItem.toString().equals("")) {
                Toast.makeText(
                    requireActivity(),
                    "Enter School Name",
                    Toast.LENGTH_LONG
                )
            } else {

                (activity as OnBoardingActivity?)?.setCurrentItem(4, true)
            }


        }




        schoolAct_spinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                //Toast.makeText(requireContext(), "" + (parent?.getItemAtPosition(pos) as UniversityList).name, Toast.LENGTH_SHORT).show()

                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                    AppConstants.university,
                    (parent?.getItemAtPosition(pos) as UniversityList).name
                )


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })



        fraternity_Spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                    AppConstants.community,
                    (parent?.getItemAtPosition(pos) as UniversityList).name
                )


            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        fraternity_button?.setOnClickListener {
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            Socority_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.black))
            Socority_button?.setTextColor(this.getResources().getColor(R.color.white))

            val schoolAdapter = CommunityAdapter(
                requireActivity(),
                fraternitiesList!! as ArrayList<UniversityList>
            )
            fraternity_Spinner.adapter = schoolAdapter
        }

        Socority_button?.setOnClickListener {
            Socority_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            Socority_button?.setTextColor(this.resources.getColor(R.color.black))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.white))

            val schoolAdapter = CommunityAdapter(
                requireActivity(),
                sororitiesList!! as ArrayList<UniversityList>
            )
            fraternity_Spinner.adapter = schoolAdapter

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
                       // AppUtils.hideLoader()
                        it.data.let { it1 ->
                            if (it1?.status == true) {
                                Log.d("TAG@123", it.toString())
                                schoolList = ArrayList<UniversityList>()
                                schoolList = it1.data.universityList
                                sororitiesList = ArrayList<UniversityList>()
                                sororitiesList = it1.data.sororitiesList
                                fraternitiesList = ArrayList<UniversityList>()
                                fraternitiesList = it1.data.fraternitiesList
                                setAdapterData()

                            } else {

                            }
                        }
                    }
                    Status.LOADING -> {
                        //AppUtils.showLoader(requireActivity())
                    }
                    Status.ERROR -> {

                    }
                }
            })


    }

    private fun setAdapterData() {
        val schoolAdapter = CommunityAdapter(
            requireActivity(),
            schoolList!! as ArrayList<UniversityList>
        )
        schoolAct_spinner?.adapter = schoolAdapter
        schoolAdapter.notifyDataSetChanged()

        val adapter = CommunityAdapter(
            requireActivity(),
            fraternitiesList!! as ArrayList<UniversityList>
        )
        fraternity_Spinner.adapter = adapter
        adapter.notifyDataSetChanged()
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
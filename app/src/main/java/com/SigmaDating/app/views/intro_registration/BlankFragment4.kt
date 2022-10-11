package com.SigmaDating.app.views.intro_registration


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.app.adapters.SchoolAdapter
import com.SigmaDating.app.model.communityModel.UniversityList
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.utilities.EmptyDataObserver
import com.SigmaDating.databinding.FragmentSchoolInputBinding
import com.example.demoapp.other.Status
import dagger.hilt.android.AndroidEntryPoint


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class BlankFragment4 : Fragment(), SearchView.OnQueryTextListener,
    SchoolAdapter.OnItemClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private var about_school_binding: FragmentSchoolInputBinding? = null
    private var continueSchool: Button? = null
    private var Socority_button: Button? = null
    private var fraternity_button: Button? = null
    private var independent:Button?=null
    lateinit var fraternity_Spinner: EditText
    private var schoolAct_spinner: EditText? = null
    var fraternitiesList: List<UniversityList>? = null
    var sororitiesList: List<UniversityList>? = null
    var schoolList: List<UniversityList>? = null
    var schoolListCopy: List<UniversityList>? = null
    lateinit var commonDataList: ArrayList<String>
    private lateinit var schoolAdapter: SchoolAdapter
    var flag:Boolean=true;
    lateinit var dialog: Dialog
    private val mLastClickTime: Long = 0
    var searchRecyclerView: RecyclerView? = null
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
        independent= about_school_binding?.root?.findViewById(R.id.independent)
        fraternity_button = about_school_binding?.root?.findViewById(R.id.fraternity_button)
        fraternity_button!!.isSelected=true
        fraternity_button!!.hint="Select Fraternity"

        schoolAct_spinner!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {

                AppUtils.showLoader(requireActivity())
                schoolList = ArrayList<UniversityList>()
                schoolList = schoolListCopy
                openSchoolSearchDialog(AppConstants.School,"School / University")
                true
            } else false
        }

        fraternity_Spinner!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                flag=true
                schoolList = ArrayList<UniversityList>()
                schoolList = fraternitiesList

                if (fraternity_button!!.isSelected){
                    openSchoolSearchDialog(AppConstants.Fraternity, "Fraternity")
                }
              else if (Socority_button!!.isSelected){
                    openSchoolSearchDialog(AppConstants.Sorority, "Sorority ")
                }

                true
            } else false
        }


        continueSchool?.setOnClickListener {

                if (schoolAct_spinner!!.text.isEmpty()) {
                    Toast.makeText(
                        requireActivity(),
                        "Select School/University",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else if(fraternity_Spinner!!.text.isEmpty()&& flag){
                    Toast.makeText(
                        requireActivity(),
                        "Select Fraternity/Sorority ",
                        Toast.LENGTH_LONG
                    ).show()

                }else {
                    (activity as OnBoardingActivity?)?.setCurrentItem(6, true)
                }

        }


        fraternity_button?.setOnClickListener {
            flag=true;
            if (!fraternity_button!!.isSelected){
                fraternity_Spinner?.setText("")
                fraternity_Spinner?.hint = "Select Fraternity"
                fraternity_button!!.isSelected=true
                Socority_button!!.isSelected=false
            }
            else{

            }
            fraternity_Spinner?.hint = "Select Fraternity"
               // schoolAct_spinner!!.isSelected=false
                //fraternity_button!!.isSelected=true

            fraternity_Spinner.visibility=View.VISIBLE
            independent?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            independent?.setTextColor(this.resources.getColor(R.color.white))

            fraternity_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            Socority_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.black))
            Socority_button?.setTextColor(this.getResources().getColor(R.color.white))

            schoolList = ArrayList<UniversityList>()
            schoolList = fraternitiesList
            schoolAdapter = SchoolAdapter(this, AppConstants.Fraternity)


        }

        Socority_button?.setOnClickListener {
            flag=true;
            if (!Socority_button!!.isSelected){
                fraternity_button!!.isSelected=false
                Socority_button!!.isSelected=true
                fraternity_Spinner?.setText("")
                fraternity_Spinner?.hint = "Select Sorority"
            }

            fraternity_Spinner.visibility=View.VISIBLE
            independent?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            independent?.setTextColor(this.resources.getColor(R.color.white))

            fraternity_Spinner?.hint = "Select Sorority"
            Socority_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            Socority_button?.setTextColor(this.resources.getColor(R.color.black))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.white))
            schoolList = ArrayList<UniversityList>()
            schoolList = sororitiesList
            schoolAdapter = SchoolAdapter(this, AppConstants.Sorority)

        }

        independent?.setOnClickListener {

            flag=false;
            fraternity_Spinner.setText("")
            (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                AppConstants.community,
                "Independent"
            )
            Socority_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            Socority_button?.setTextColor(this.getResources().getColor(R.color.white))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.white))
            fraternity_Spinner.visibility=View.INVISIBLE
            independent?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            independent?.setTextColor(this.resources.getColor(R.color.black))
        }


        return about_school_binding?.root
    }


    fun openSchoolSearchDialog(stringtype: String, title :String) {

        dialog = Dialog(requireContext(), R.style.AppBaseTheme2)
        dialog.setContentView(R.layout.search_dialog_school)
        dialog.findViewById<SearchView>(R.id.search_view).setOnQueryTextListener(this)
        searchRecyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view_school)
        var title_layout=dialog.findViewById<TextView>(R.id.title_layout)
        title_layout.text=title
        val empty_dataparent = dialog.findViewById<View>(R.id.empty_data_parent)
        val searchVieww=dialog.findViewById<SearchView>(R.id.search_view)


        if (stringtype.equals(AppConstants.School)){

            searchVieww.queryHint="Search School/University"
        }
        else if (stringtype.equals(AppConstants.Sorority)){
            schoolList= ArrayList<UniversityList>()
            schoolList=sororitiesList
            searchVieww.queryHint="Search Sorority "
        }
        else if (stringtype.equals(AppConstants.Fraternity)){
            schoolList= ArrayList<UniversityList>()
            schoolList=fraternitiesList
            searchVieww.queryHint="Search Fraternity"
        }
        searchRecyclerView!!.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        schoolAdapter = SchoolAdapter(this, stringtype)
        searchRecyclerView!!.adapter = schoolAdapter
        schoolAdapter.addData(schoolList!!)
        schoolAdapter.notifyDataSetChanged()
        val emptyDataObserver = EmptyDataObserver(searchRecyclerView, empty_dataparent)
        schoolAdapter.registerAdapterDataObserver(emptyDataObserver)
        dialog.show()
        AppUtils.hideLoader()
        schoolAct_spinner!!.isEnabled=false
        fraternity_Spinner.isEnabled=false
        dialog.setOnDismissListener {
            schoolAct_spinner!!.isEnabled=true
            fraternity_Spinner.isEnabled=true

        }


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
                                Log.d("TAG@123", it.data.toString())
                                schoolList = ArrayList<UniversityList>()
                                schoolList = it1.data.universityList
                                schoolListCopy=schoolList
                                sororitiesList = ArrayList<UniversityList>()
                                sororitiesList = it1.data.sororitiesList
                                fraternitiesList = ArrayList<UniversityList>()
                                fraternitiesList = it1.data.fraternitiesList
                                //setAdapterData()

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

    override fun onQueryTextSubmit(p0: String?): Boolean {
        schoolAdapter.filter.filter(p0)
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        schoolAdapter.filter.filter(p0)

        return false




    }

    override fun onItClick(position: UniversityList, stringtype: String) {
        var clickItemData = position.name
        if (stringtype.equals(AppConstants.Fraternity) || stringtype.equals(AppConstants.Sorority)) {
            fraternity_Spinner!!.setText(clickItemData)
            (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                AppConstants.community,
                position.name

            )
        } else {
            schoolAct_spinner!!.setText(clickItemData)
            (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(AppConstants.university, position.name)
        }

        if (dialog != null || dialog.isShowing) {
            dialog.dismiss()
            schoolAct_spinner!!.isEnabled=true
            fraternity_Spinner.isEnabled=true
        }

    }

}
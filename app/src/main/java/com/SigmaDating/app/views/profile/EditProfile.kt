package com.SigmaDating.app.views.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.app.InstagramAppModule.InstagramApp
import com.SigmaDating.app.InstagramAppModule.InstagramApp.OAuthAuthenticationListener
import com.SigmaDating.app.InstagramAppModule.InstagramSession
import com.SigmaDating.app.adapters.Edit_Profile_Adapter
import com.SigmaDating.app.adapters.InterestAdapter
import com.SigmaDating.app.adapters.SchoolAdapter
import com.SigmaDating.app.model.Loginmodel
import com.SigmaDating.app.model.communityModel.Interest
import com.SigmaDating.app.model.communityModel.UniversityList
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.utilities.EmptyDataObserver
import com.SigmaDating.app.utilities.URIPathHelper
import com.SigmaDating.app.views.Home
import com.SigmaDating.databinding.FragmentEditProfileBinding
import com.SigmaDating.model.SchoolCommunityResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import kotlinx.coroutines.*
import org.jetbrains.anko.doAsync
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditProfile : Fragment(), Edit_Profile_Adapter.OnCategoryClickListener,
    SearchView.OnQueryTextListener, SchoolAdapter.OnItemClickListener,
    InterestAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters

    private var instaObj: InstagramApp? = null
    val CLIENT_ID = "8605825122768702"
    val CLIENT_SECRET = "417afa3975760ff12c3d9634ec2ff29a"
    val CALLBACK_URL = "https://www.sigmasocialapp.com/app/oauth/authorize"

    var ig_flag: Boolean = false
    var fb_flag: Boolean = false
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private var param1: String? = null
    private var param2: String? = null
    private val OPERATION_CHOOSE_PHOTO = 2
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoAdapter: Edit_Profile_Adapter
    lateinit var dataList: ArrayList<String>
    lateinit var interestsList: ArrayList<String>
    var fraternitiesList: List<UniversityList>? = null
    var schoolList: List<UniversityList>? = null
    var interest: List<Interest>? = null
    lateinit var fraternity_Spinner: EditText
    lateinit var interests_text: TextView
    private var schoolAct_spinner: EditText? = null
    lateinit var rootContainer: ChipGroup
    lateinit var rootContainer_intrest: ChipGroup
    var sororitiesList: List<UniversityList>? = null
    var university: String? = null
    var community: String? = null
    var about: String? = null
    var is_private: String = "0"
    var currentPhotoPath: String? = null
    private var mUri: Uri? = null
    private val OPERATION_CAPTURE_PHOTO = 1
    private lateinit var schoolAdapter: SchoolAdapter
    private lateinit var intrestAdapter: InterestAdapter
    lateinit var dialog: Dialog
    var searchRecyclerView: RecyclerView? = null
    private var Socority_button: Button? = null
    private var fraternity_button: Button? = null
    private var independent: Button? = null

    var interested_in = ""
    var show_me = ""
    var age_range = ""
    var distance = ""
    var gender = ""
    private var toast_flag: Boolean = true


    private lateinit var firebaseAnalytics: FirebaseAnalytics

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
    ): View {

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        Socority_button = _binding?.root?.findViewById(R.id.Socority_button)
        independent = _binding?.root?.findViewById(R.id.independent)
        fraternity_button = _binding?.root?.findViewById(R.id.fraternity_button)
        fraternity_button!!.isSelected = true
        schoolAct_spinner = _binding?.root?.findViewById(R.id.school_data)
        fraternity_Spinner = _binding?.root?.findViewById(R.id.et_type)!!
        rootContainer = _binding?.root?.findViewById(R.id.rootContainer)!!
        interests_text = _binding?.root?.findViewById(R.id.interests_text)!!
        _binding?.updateImageView?.layoutManager = GridLayoutManager(requireContext(), 3)
        _binding?.instragramText?.setOnClickListener {
            if (ig_flag) {
                show_dilog(true)
            } else {
                instaObj = InstagramApp(
                    requireContext(), CLIENT_ID,
                    CLIENT_SECRET, CALLBACK_URL
                )
                instaObj!!.setListener(listener)
                instaObj!!.authorize()
            }

        }
        _binding?.fbText?.setOnClickListener {
            show_dilog(false)
        }

        _binding?.imageView2?.setOnClickListener {
            (activity as Home).onBackPressed()
        }
        _binding?.done?.setOnClickListener {
            if (community.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please Select Fraternity/Sorority data",
                    Toast.LENGTH_LONG
                )
                    .show()

            } else {
                (activity as Home).homeviewmodel.update_profile =
                    MutableLiveData<Resource<Loginmodel>>()
                subscribe_edit_profile()
                var t = interestsList.joinToString(",")
                university?.let { it1 ->
                    community?.let { it2 ->
                        (activity as Home).homeviewmodel.Update_edit_page_data(
                            (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID),
                            it1,
                            it2,
                            t,
                            _binding!!.userAbout.text.toString(),
                            is_private,
                            interested_in,
                            show_me,
                            age_range,
                            distance,
                            gender
                        )
                    }

                }
            }
        }


        fraternity_button?.setOnClickListener {
            if (!fraternity_button!!.isSelected) {
                fraternity_Spinner.setText("Select Fraternity")
                community = null
                Log.d("TAG@123", "Community :" + community)
                fraternity_button!!.isSelected = true
                Socority_button!!.isSelected = false
            } else {

            }
            fraternity_Spinner?.setText("Select Fraternity")

            fraternity_Spinner.visibility = View.VISIBLE

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
            if (!Socority_button!!.isSelected) {
                fraternity_button!!.isSelected = false
                Socority_button!!.isSelected = true
                community = null
                fraternity_Spinner.setText("Select Sorority")
            }

            fraternity_Spinner.visibility = View.VISIBLE
            independent?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            independent?.setTextColor(this.resources.getColor(R.color.white))

            fraternity_Spinner.setText("Select Sorority")
            Socority_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            Socority_button?.setTextColor(this.resources.getColor(R.color.black))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.white))

            schoolList = ArrayList<UniversityList>()
            schoolList = sororitiesList
            schoolAdapter = SchoolAdapter(this, AppConstants.Sorority)

        }
        independent?.setOnClickListener {
            (activity as Home?)?.sharedPreferencesStorage?.setValue(
                AppConstants.community,
                "Independent"
            )
            community = "Independent"
            Socority_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            fraternity_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
            Socority_button?.setTextColor(this.getResources().getColor(R.color.white))
            fraternity_button?.setTextColor(this.getResources().getColor(R.color.white))
            fraternity_Spinner.visibility = View.INVISIBLE
            independent?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
            independent?.setTextColor(this.resources.getColor(R.color.black))
        }
        schoolAct_spinner!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                openSchoolSearchDialog(AppConstants.School, schoolList as List<UniversityList>)
                true
            } else false
        }

        interests_text.setOnClickListener {
            openinterestsSearchDialog(AppConstants.School, interest as List<Interest>)
        }

        _binding?.profileCreationGroup?.setOnCheckedChangeListener { group, checkedId ->
            val rb = _binding?.root!!.findViewById(checkedId) as RadioButton
            when (rb.text.toString()) {
                "Public" -> {
                    is_private = "0"
                }
                "Private" -> {
                    is_private = "1"
                }
            }
            Log.d("TAG@123", "profile : " + rb.text.toString())

        }

        _binding?.genderWomen?.setChecked(true);
        _binding!!.genderRb.setOnCheckedChangeListener { group, checkedId ->
            val rb = _binding!!.root.findViewById(checkedId) as RadioButton
            gender = rb.text.toString()
            Log.d("TAG@123", "show me  $gender")
        }


        _binding!!.rgShowme.setOnCheckedChangeListener { group, checkedId ->
            val rb = _binding!!.root.findViewById(checkedId) as RadioButton
            show_me = rb.text.toString()
            Log.d("TAG@123", "show me  $show_me")
        }


        _binding!!.rg.setOnCheckedChangeListener { group, checkedId ->
            val rb = _binding!!.root.findViewById(checkedId) as RadioButton
            interested_in = rb.text.toString()
            Log.d("TAG@123", "interested_in  $interested_in")
        }

        _binding!!.seekBarAge.addOnChangeListener { rangeSlider, value, fromUser ->
            val values = rangeSlider.values
            Log.d("TAG@123", "Start value: ${values[0]}, End value: ${values[1]}")
            _binding!!.textView8.text = "${values[0].toInt()}-${values[1].toInt()} "
            age_range = "${values[0].toInt()}-${values[1].toInt()}"

        }

        _binding!!.seekBar.addOnChangeListener { rangeSlider, value, fromUser ->
            Log.d("TAG@123", value.toString())
            _binding!!.textView11.text = "$value miles"
            distance = value.toString()
        }


        fraternity_Spinner!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                schoolList = ArrayList<UniversityList>()
                schoolList = fraternitiesList
                if (fraternity_button!!.isSelected) {
                    openSchoolSearchDialog(
                        AppConstants.Fraternity, fraternitiesList as List<UniversityList>
                    )
                } else if (Socority_button!!.isSelected) {
                    openSchoolSearchDialog(
                        AppConstants.Fraternity, sororitiesList as List<UniversityList>
                    )
                }
                true
            } else false
        }
        (activity as Home).homeviewmodel.school_dataResponse =
            MutableLiveData<Resource<SchoolCommunityResponse>>()
        (activity as Home).homeviewmodel.get_user_edit_user =
            MutableLiveData<Resource<Loginmodel>>()
        (activity as Home).homeviewmodel.get_edit_page_data(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun openSchoolSearchDialog(stringtype: String, passDataList: List<UniversityList>) {
        dialog = Dialog(requireContext(), R.style.AppBaseTheme2)
        dialog.setContentView(R.layout.search_dialog_school)
        dialog.findViewById<SearchView>(R.id.search_view).setOnQueryTextListener(this)

        searchRecyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view_school)
        val titleText = dialog.findViewById<TextView>(R.id.title_layout)
        val searchVieww = dialog.findViewById<SearchView>(R.id.search_view)
        searchVieww.setOnClickListener {
            searchVieww.setIconified(false);
        }
        val empty_dataparent = dialog.findViewById<View>(R.id.empty_data_parent)
        searchRecyclerView!!.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        schoolAdapter = SchoolAdapter(this, stringtype)
        searchRecyclerView!!.adapter = schoolAdapter
        schoolAdapter.addData(passDataList)
        schoolAdapter.notifyDataSetChanged()
        val emptyDataObserver = EmptyDataObserver(searchRecyclerView, empty_dataparent)
        schoolAdapter.registerAdapterDataObserver(emptyDataObserver)

        if (stringtype.equals(AppConstants.School)) {
            titleText.text = "School / University"
            searchVieww.queryHint = "Search School/University"
        } else {
            titleText.text = "Sorority/Fraternity"
            searchVieww.queryHint = "Search Sorority/Fraternity "
        }
        dialog.show()
        schoolAct_spinner!!.isEnabled = false
        fraternity_Spinner.isEnabled = false
        dialog.setOnDismissListener {
            schoolAct_spinner!!.isEnabled = true
            fraternity_Spinner.isEnabled = true
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun openinterestsSearchDialog(stringtype: String, passDataList: List<Interest>) {
        dialog = Dialog(requireContext(), R.style.AppBaseTheme2)
        dialog.setContentView(R.layout.open_interests)
        dialog.findViewById<SearchView>(R.id.search_view).setOnQueryTextListener(this)
        searchRecyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view_school)
        val titleText = dialog.findViewById<TextView>(R.id.title_layout)
        val searchVieww = dialog.findViewById<SearchView>(R.id.search_view)
        val empty_dataparent = dialog.findViewById<View>(R.id.empty_data_parent)
        val done_interset = dialog.findViewById<Button>(R.id.done_interset)
        rootContainer_intrest = dialog.findViewById<ChipGroup>(R.id.rootContainer_intrest)
        setupChipGroupDynamically(interestsList.filterNotNull(), rootContainer_intrest, true)
        searchRecyclerView!!.layoutManager = GridLayoutManager(requireContext(), 1)
        intrestAdapter = InterestAdapter(this, stringtype)
        searchRecyclerView!!.adapter = intrestAdapter
        intrestAdapter.addData(passDataList)
        intrestAdapter.notifyDataSetChanged()
        val emptyDataObserver = EmptyDataObserver(searchRecyclerView, empty_dataparent)
        intrestAdapter.registerAdapterDataObserver(emptyDataObserver)
        titleText.text = "Search Interests"
        searchVieww.queryHint = "Search Interests"
        dialog.show()
        schoolAct_spinner!!.isEnabled = false
        fraternity_Spinner.isEnabled = false
        done_interset.setOnClickListener {

            if (dialog != null || dialog.isShowing) {
                dialog.dismiss()
                schoolAct_spinner!!.isEnabled = true
                fraternity_Spinner.isEnabled = true
            }
            setupChipGroupDynamically(interestsList.filterNotNull(), rootContainer, false)
        }

        dialog.setOnDismissListener {
            schoolAct_spinner!!.isEnabled = true
            fraternity_Spinner.isEnabled = true
            setupChipGroupDynamically(interestsList.filterNotNull(), rootContainer, false)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun subscribe_edit_profile() {

        (activity as Home?)?.homeviewmodel?.update_profile?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {

                                do_sent_firebaselog(
                                    "profile_update",
                                    (activity as Home).sharedPreferencesStorage.getString(
                                        AppConstants.USER_ID
                                    )
                                )
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG)
                                    .show()

                                Update_edit_Profile_info()

                            } catch (e: Exception) {
                            }
                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                .show()
                        }

                    }
                }
                Status.LOADING -> {
                    AppUtils.showLoader(requireContext())
                }
                Status.ERROR -> {

                }
            }
        })


    }


    fun subscribe_delete_images() {
        (activity as Home?)?.homeviewmodel?.delete_images?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {

                            try {
                                Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                    .show()
                                Update_edit_Profile_info()
                            } catch (e: Exception) {
                            }

                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
                Status.LOADING -> {
                    AppUtils.showLoader(requireContext())
                }
                Status.ERROR -> {
                    AppUtils.hideLoader()
                }
            }
        })


    }


    fun subscribe_upload_images() {
        (activity as Home?)?.homeviewmodel?.upload_images?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                AppUtils.hideLoader()
                                Log.d("TAG@123", "upload Images : " + it.data.toString())
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG)
                                    .show()
                                Update_edit_Profile_info()

                            } catch (e: Exception) {
                            }

                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
                Status.LOADING -> {
                    AppUtils.showLoader(requireContext())
                }
                Status.ERROR -> {
                    AppUtils.hideLoader()
                }
            }
        })


    }


    fun schoolListResponse() {
        (activity as Home?)?.homeviewmodel?.school_dataResponse?.observe(
            requireActivity(),
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { it1 ->
                            if (it1?.status == true) {
                                try {
                                    Log.d("TAG@123", it.toString())
                                    schoolList = ArrayList<UniversityList>()
                                    schoolList = it1.data.universityList
                                    fraternitiesList = ArrayList<UniversityList>()
                                    fraternitiesList = it1.data.fraternitiesList

                                    sororitiesList = ArrayList<UniversityList>()
                                    sororitiesList = it1.data.sororitiesList
                                    interest = ArrayList<Interest>()
                                    interest = it1.data.interestList
                                    // setAdapterData()

                                    Update_edit_Profile_info()

                                } catch (e: Exception) {
                                    Log.d("TAG@123", "Exception schoolListResponse ${e.message}")
                                }
                            } else {
                                Log.d("TAG@123", "Exception status == falsh")
                            }
                        }
                    }
                    Status.LOADING -> {
                        AppUtils.showLoader(requireContext())
                    }
                    Status.ERROR -> {
                        AppUtils.hideLoader()
                    }
                }
            })


    }


    fun subscribe_Login_User_details() {
        (activity as Home?)?.homeviewmodel?.get_user_edit_user?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data.let { res ->
                            if (res?.status == true) {
                                try {
                                    dataList = ArrayList()
                                    dataList.clear()
                                    interestsList = ArrayList()
                                    interestsList.clear()

                                    Log.d(
                                        "TAG@123",
                                        "Login_User_details : " + it.data?.user.toString()
                                    )
                                    if (!res.user.photos.isNullOrEmpty()) {
                                        dataList = res.user.photos
                                    }
                                    set_Adapterdata()
                                    res.user.university.let {
                                        university = it
                                        schoolAct_spinner!!.setText(university)
                                    } ?: run {
                                        university = ""
                                    }

                                    res.user.community.let {
                                        community = it
                                        fraternity_Spinner.setText(community)
                                    } ?: run {
                                        university = ""
                                    }
                                    res.user.ig_auth_token?.let {
                                        ig_flag = true
                                        _binding?.instragramText?.text = "Disconnect"
                                    }

                                    res.user.fb_auth_token?.let {
                                        fb_flag = true
                                        _binding?.fbText?.text = "Disconnect"
                                    }

                                    res.user.orgType.let {
                                        set_default_button(it)
                                    }
                                    res.user.is_private.let {
                                        when (it) {
                                            "0" -> {
                                                _binding?.profilePublic?.setChecked(true);
                                                _binding?.profilePrivate?.setChecked(false);
                                            }
                                            "1" -> {
                                                _binding?.profilePublic?.setChecked(false);
                                                _binding?.profilePrivate?.setChecked(true);
                                            }

                                            else -> {

                                            }
                                        }

                                    }
                                    if (it.data?.user?.interested_in?.isEmpty() == false) {
                                        interested_in = it.data.user.interested_in
                                        when (interested_in) {
                                            "Women" -> _binding?.rbWomen?.setChecked(true);
                                            "Men" -> _binding?.rbMen?.setChecked(true);
                                            "WOMEN" -> _binding?.rbWomen?.setChecked(true);
                                            "MEN" -> _binding?.rbMen?.setChecked(true);
                                            "BOTH" -> _binding?.rbMore?.setChecked(true);
                                            "Both" -> _binding?.rbMore?.setChecked(true);

                                        }
                                    }

                                    if (it.data?.user?.gender?.isEmpty() == false) {
                                        gender = it.data.user.gender
                                        when (gender) {
                                            "Woman" -> _binding?.genderWomen?.setChecked(true);
                                            "Man" -> _binding?.genderMen?.setChecked(true);
                                            "WOMAN" -> _binding?.genderWomen?.setChecked(true);
                                            "MAN" -> _binding?.genderMen?.setChecked(true);
                                        }
                                    }

                                    if (it.data?.user?.show_me?.isEmpty() == false) {
                                        show_me = it.data.user.show_me
                                        when (show_me) {
                                            "Greek Life" -> _binding!!.rbGreek.setChecked(true);
                                            "Independents" -> _binding!!.rbIndepndt.setChecked(true);
                                            "Both" -> _binding!!.rbboth.setChecked(true);
                                        }
                                    }




                                    if (it.data?.user?.age_range?.isEmpty() == false) {
                                        _binding!!.textView8.setText(it.data.user.age_range)

                                        try {
                                            _binding!!.seekBarAge.setValues(
                                                res.user.age_range.split("-").get(0).toFloat(),
                                                res.user.age_range.split("-").get(1).toFloat()
                                            )
                                        } catch (e: Exception) {
                                            Log.d("TAG@123", "seekBarAge ${e.message}")
                                        }
                                    }

                                    if (it.data?.user?.distance?.isEmpty() == false) {
                                        if (it.data.user.distance.toFloat() >= 25) {
                                            _binding!!.seekBar.setValue(it.data.user.distance.toFloat())
                                        }
                                        _binding!!.textView11.setText(it.data.user.distance + " miles")
                                    }


                                    res.user.about.let {
                                        about = it
                                        _binding?.userAbout?.setText(about)
                                    } ?: run {
                                        about = ""
                                        _binding?.userAbout?.setText(about)
                                    }
                                    if (res.user.interests.contains(",")) {
                                        interestsList =
                                            res.user.interests.split(",") as ArrayList<String>
                                    } else {
                                        interestsList.add(res.user.interests)
                                    }

                                    if (interestsList[0].equals("")) {
                                        interestsList.removeAt(0)
                                    }
                                    Log.d("TAG@123", "interestsList " + interestsList.size)
                                    setupChipGroupDynamically(
                                        interestsList!!.filterNotNull(),
                                        rootContainer,
                                        false
                                    )

                                } catch (e: Exception) {
                                    Log.d(
                                        "TAG@123",
                                        "Exception subscribe_Login_User_details ${e.message}"
                                    )

                                }

                            } else {
                                Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {

                    }
                }
            })


    }


    fun set_Adapterdata() {
        dataList.add("ADD_IMAGES")
        photoAdapter = Edit_Profile_Adapter(requireContext(), this)
        _binding?.updateImageView?.adapter = photoAdapter
        dataList.let { photoAdapter.setDataList(it) }
        photoAdapter.notifyDataSetChanged()
    }

    override fun onCategoryClick(position: Int, boolean: Boolean) {
        if (boolean) {
            checkGallerypermission()
        } else {
            (activity as Home).homeviewmodel.delete_images = MutableLiveData<Resource<Loginmodel>>()
            subscribe_delete_images()
            (activity as Home).homeviewmodel.User_delete_images(
                (activity as Home).sharedPreferencesStorage.getString(
                    AppConstants.USER_ID
                ), dataList.get(position)

            )
        }

    }


    private fun checkGallerypermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions,
                AppConstants.STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            popup()
        }
    }


    private fun popup() {
        val view = layoutInflater.inflate(R.layout.selectcamera_gallery_layout, null)
        val dialog = activity?.let { BottomSheetDialog(requireActivity(), R.style.DialogStyle) }!!
        dialog.setContentView(view)
        dialog.show()
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp
        dialog.window!!.attributes.windowAnimations = R.style.DialogStyle
        dialog.show()
        dialog.setCanceledOnTouchOutside(true)
        val button_camera = dialog.findViewById<ImageView>(R.id.button_camera)
        val button_gallery = dialog.findViewById<ImageView>(R.id.button_gallery)


        button_camera?.setOnClickListener {
            dialog.dismiss()
            capturePhoto()
        }
        button_gallery?.setOnClickListener {
            dialog.dismiss()
            openGallery()
        }


    }

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    requireContext(), "com.SigmaDating.app.provider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, OPERATION_CAPTURE_PHOTO)
            }
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            activity?.packageManager?.let {
                intent.resolveActivity(it)?.also {
                    startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    val mUri = Uri.fromFile(File(currentPhotoPath))
                    Glide.with(this)
                        .asBitmap()
                        .load(mUri)
                        .into(object : CustomTarget<Bitmap>(300, 300) {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {

                                Bitmap.createScaledBitmap(resource, 250, 250, true);
                                convertBitmapToBase64(resource, true)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {

                    if (data != null) {
                        try {

                            Glide.with(this)
                                .asBitmap()
                                .load(
                                    File(
                                        URIPathHelper().getPath(
                                            requireContext(),
                                            data!!.data!!
                                        )
                                    )
                                )
                                .into(object : CustomTarget<Bitmap>(300, 300) {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        // imageProfile?.setImageBitmap(resource)
                                        Bitmap.createScaledBitmap(resource, 250, 250, true);
                                        convertBitmapToBase64(resource, false)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {

                                    }
                                })


                            /* val bitmap = MediaStore.Images.Media.getBitmap(
                                 requireActivity().contentResolver,
                                 data.data
                             )
                             Bitmap.createScaledBitmap(bitmap, 250, 250, true);
                             convertBitmapToBase64(bitmap, false)*/
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                }
        }
    }


    fun convertBitmapToBase64(bm: Bitmap, flag: Boolean) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("")
        progressDialog.setMessage("Please wait ...")
        progressDialog.show()
        (activity as Home).homeviewmodel.upload_images = MutableLiveData<Resource<Loginmodel>>()
        subscribe_upload_images()
        doAsync {
            var encoded = ""
            val baos = ByteArrayOutputStream()
            if (flag) {
                bm.compress(Bitmap.CompressFormat.PNG, 10, baos)
            } else {
                bm.compress(Bitmap.CompressFormat.PNG, 50, baos)
            }

            val b = baos.toByteArray()
            encoded = Base64.encodeToString(b, Base64.DEFAULT)
            (activity as Home).homeviewmodel.User_upload_images(
                (activity as Home).sharedPreferencesStorage.getString(
                    AppConstants.USER_ID
                ), "data:image/png;base64,$encoded"
            )
            Log.d("TAG@123", "  images  --------- $encoded")
            progressDialog.dismiss()
        }

    }

    private fun setupChipGroupDynamically(
        list: List<String>,
        rootContainer: ChipGroup,
        flag: Boolean
    ) {
        if (list.isNotEmpty()) {
            try {
                rootContainer.removeAllViews()
                for (i in list.indices) {
                    Log.d("TAG@123", "ChipGroup : " + list.get(i))
                    if (list[i].isNotEmpty()) {
                        rootContainer.addView(createChip(list.get(i), i, flag))
                    }

                }
            } catch (e: Exception) {
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun createChip(label: String, index: Int, flag: Boolean): Chip {
        val chip = Chip(requireContext(), null, R.style.chip_style)
        chip.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        chip.text = label

        if (flag) {
            chip.isCloseIconVisible = true
            chip.isChipIconVisible = true
            chip.isCheckable = true
            chip.isClickable = true
            chip.chipStrokeWidth = 3f
            chip.chipCornerRadius = 10f
            chip.setTextColor(getColorStateList(requireContext(), R.color.blue))
            chip.setChipBackgroundColorResource(android.R.color.transparent)
            chip.setChipStrokeColorResource(R.color.blue)
            chip.setOnCloseIconClickListener {
                Log.d("TAG@123", interestsList.toString())
                if (interestsList.contains(label)) {
                    interestsList.remove(label)
                    setupChipGroupDynamically(interestsList!!, rootContainer_intrest, true)
                }
                Log.d("TAG@123", interestsList.toString())

            }
        } else {
            chip.isCloseIconVisible = false
            chip.isChipIconVisible = false
            chip.isCheckable = false
            chip.isChecked = true
            chip.chipStrokeWidth = 3f
            chip.chipCornerRadius = 10f
            chip.setTextColor(getColorStateList(requireContext(), R.color.blue))
            //  chip.setTextSize(14f)
            chip.setChipStrokeColorResource(R.color.blue)
            chip.setChipBackgroundColorResource(android.R.color.transparent)
        }
        return chip
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        if (this::schoolAdapter.isInitialized) {
            schoolAdapter.filter.filter(p0)
        }
        if (this::intrestAdapter.isInitialized) {
            intrestAdapter.filter.filter(p0)
        }

        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (this::schoolAdapter.isInitialized) {
            schoolAdapter.filter.filter(p0)
        }
        if (this::intrestAdapter.isInitialized) {
            intrestAdapter.filter.filter(p0)
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            schoolListResponse()
        }

    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onItClick(position: UniversityList, stringtype: String) {
        var clickItemData = position.name
        if (stringtype.equals(AppConstants.Fraternity) || stringtype.equals(AppConstants.Sorority)) {
            fraternity_Spinner!!.setText(clickItemData)
            community = clickItemData
        } else {
            schoolAct_spinner!!.setText(clickItemData)
            university = clickItemData
        }

        if (dialog != null || dialog.isShowing) {
            dialog.dismiss()
            schoolAct_spinner!!.isEnabled = true
            fraternity_Spinner.isEnabled = true
        }
    }


    fun Update_edit_Profile_info() {
        (activity as Home).homeviewmodel.get_user_edit_user =
            MutableLiveData<Resource<Loginmodel>>()
        (activity as Home).homeviewmodel.get_Edit_User_details(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )
        subscribe_Login_User_details()
    }

    override fun onIntrestClick(position: Interest, stringtype: String) {
        interestsList.filterNotNull()

        Log.d("TAG@123", "interestsList " + interestsList.size)

        var clickItemData = position.interest
        if (interestsList.size > 5) {
            if (toast_flag) {
                Toast.makeText(
                    requireContext(),
                    "You can only choose maximum 6.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                toast_flag = false
                toast()
            }
        } else if (interestsList.size <= 6) {
            if (interestsList.contains(clickItemData) == false) {
                interestsList.add(clickItemData)
                setupChipGroupDynamically(interestsList!!, rootContainer_intrest, true)
            } else {
                if (toast_flag) {
                    Toast.makeText(requireContext(), "Already added", Toast.LENGTH_SHORT)
                        .show()
                    toast_flag = false
                    toast()
                }
            }
        } else {
            if (toast_flag) {
                Toast.makeText(
                    requireContext(),
                    "You can only choose maximum 6.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                toast_flag = false
                toast()
            }
        }
    }

    fun toast() {
        Handler().postDelayed(
            java.lang.Runnable {
                toast_flag = true
            },
            2000
        )
    }

    fun set_default_button(name: String) {
        when (name) {
            "Sorority" -> {
                Socority_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
                Socority_button?.setTextColor(this.resources.getColor(R.color.black))

                independent?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
                independent?.setTextColor(this.resources.getColor(R.color.white))

                fraternity_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
                fraternity_button?.setTextColor(this.getResources().getColor(R.color.white))

            }
            "Fraternity" -> {

                fraternity_button?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
                fraternity_button?.setTextColor(this.resources.getColor(R.color.black))

                independent?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
                independent?.setTextColor(this.resources.getColor(R.color.white))

                Socority_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
                Socority_button?.setTextColor(this.getResources().getColor(R.color.white))

            }
            "Independent" -> {
                independent?.setBackground(resources.getDrawable(R.drawable.white_radius_bg))
                independent?.setTextColor(this.resources.getColor(R.color.black))

                Socority_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
                Socority_button?.setTextColor(this.resources.getColor(R.color.white))

                fraternity_button?.setBackground(resources.getDrawable(R.drawable.gray_circle_radius_bg))
                fraternity_button?.setTextColor(this.getResources().getColor(R.color.white))

                fraternity_Spinner.visibility = View.INVISIBLE
            }

        }

    }


    private fun do_sent_firebaselog(event_name: String, event_log: String) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(
                event_name,
                event_log
            )
        }
    }


    var listener: OAuthAuthenticationListener = object : OAuthAuthenticationListener {
        override fun onSuccess() {

            runBlocking(Dispatchers.Main) {
                var mSession: InstagramSession = InstagramSession(context)
                var mAccessToken: String = mSession.getAccessToken()
                _binding?.instragramText?.text = "Disconnect"
                ig_flag = true
                (activity as Home).homeviewmodel.update_token(
                    (activity as Home).sharedPreferencesStorage.getString(
                        AppConstants.USER_ID
                    ), mSession.getAccessToken(), mSession.id

                )


            }
        }

        override fun onFail(error: String) {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT)
                .show()
        }
    }


    fun show_dilog(flag: Boolean) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(if (flag) "Disconnect Instagram" else "Disconnect Facebook")
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage(if (flag) "Your Instagram photos will be removed from Your profile." else "Your Facebook photos will be removed from Your profile.")
        builder.background = ColorDrawable(
            Color.parseColor("#FFFFFF")
        )
        builder.setPositiveButton("OK") { dialog, which ->
            (activity as Home).homeviewmodel.update_token(
                (activity as Home).sharedPreferencesStorage.getString(
                    AppConstants.USER_ID
                ), "", ""
            )
            _binding?.instragramText?.text = "Connect Instagram"
            ig_flag = false
            dialog.dismiss()
        }
        builder.setNegativeButton("CANCEL") { dialog, which ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }


}






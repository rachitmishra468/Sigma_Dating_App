package com.SigmaDating.apk.views.profile

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.other.Status
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.SigmaDating.R
import com.SigmaDating.apk.AppReseources
import com.SigmaDating.apk.adapters.Edit_Profile_Adapter
import com.SigmaDating.apk.adapters.SchoolAdapter
import com.SigmaDating.databinding.FragmentEditProfileBinding
import com.SigmaDating.apk.model.communityModel.Interest
import com.SigmaDating.apk.model.communityModel.UniversityList
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.utilities.EmptyDataObserver
import com.SigmaDating.apk.views.Home
import org.jetbrains.anko.doAsync
import java.io.ByteArrayOutputStream
import java.io.IOException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditProfile : Fragment(), Edit_Profile_Adapter.OnCategoryClickListener,
    SearchView.OnQueryTextListener, SchoolAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
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
    private var schoolAct_spinner: EditText? = null
    lateinit var rootContainer: ChipGroup
    var university: String? = null
    var community: String? = null
    var about: String? = null
    // var interests: String? = null


    private lateinit var schoolAdapter: SchoolAdapter
    lateinit var dialog: Dialog
    var searchRecyclerView: RecyclerView? = null
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

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        schoolAct_spinner = _binding?.root?.findViewById(R.id.school_data)
        fraternity_Spinner = _binding?.root?.findViewById(R.id.et_type)!!
        rootContainer = _binding?.root?.findViewById(R.id.rootContainer)!!
        _binding?.updateImageView?.layoutManager = GridLayoutManager(requireContext(), 3)
        _binding?.imageView2?.setOnClickListener {
            (activity as Home).onBackPressed()
        }
        _binding?.done?.setOnClickListener {
            var t = interestsList.joinToString(",")
            university?.let { it1 ->
                community?.let { it2 ->
                    (activity as Home).homeviewmodel.Update_edit_page_data(
                        (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID),
                        it1,
                        it2,
                        t,
                        _binding!!.userAbout.text.toString()
                    )
                }

            }
        }


        schoolAct_spinner!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                openSchoolSearchDialog(AppConstants.School,schoolList as List<UniversityList>)
                true
            } else false
        }

        fraternity_Spinner!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                schoolList = ArrayList<UniversityList>()
                schoolList = fraternitiesList
                openSchoolSearchDialog(AppConstants.Fraternity,fraternitiesList as List<UniversityList>)
                true
            } else false
        }

        schoolListResponse()
        subscribe_Login_User_details()
        subscribe_edit_profile()
        subscribe_upload_images()
        subscribe_delete_images()


        (activity as Home).homeviewmodel.get_edit_page_data(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )
        return binding.root
    }

    private fun openSchoolSearchDialog(stringtype: String, passDataList: List<UniversityList>) {


        dialog = Dialog(requireContext(), R.style.AppBaseTheme2)
        dialog.setContentView(R.layout.search_dialog_school)
        dialog.findViewById<SearchView>(R.id.search_view).setOnQueryTextListener(this)
        searchRecyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view_school)
        val titleText=dialog.findViewById<TextView>(R.id.title_layout)
        val searchVieww=dialog.findViewById<SearchView>(R.id.search_view)
        val empty_dataparent = dialog.findViewById<View>(R.id.empty_data_parent)
        searchRecyclerView!!.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        schoolAdapter = SchoolAdapter(this, stringtype)
        searchRecyclerView!!.adapter = schoolAdapter
        schoolAdapter.addData(passDataList!!)
        schoolAdapter.notifyDataSetChanged()
        val emptyDataObserver = EmptyDataObserver(searchRecyclerView, empty_dataparent)
        schoolAdapter.registerAdapterDataObserver(emptyDataObserver)

        if (stringtype.equals(AppConstants.School)){
            titleText.text="School / University"
            searchVieww.queryHint="Search School/University"
        }
        else{
titleText.text="Sorority/Fraternity"
            searchVieww.queryHint="Search Sorority/Fraternity "
        }
        dialog.show()
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
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG).show()
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
                                (activity as Home).homeviewmodel.get_Login_User_details(
                                    (activity as Home).sharedPreferencesStorage.getString(
                                        AppConstants.USER_ID
                                    )
                                )
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


    fun subscribe_upload_images() {
        (activity as Home?)?.homeviewmodel?.upload_images?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Log.d("TAG@123", "1311" + it.data.toString())
                                Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                    .show()
                                (activity as Home).homeviewmodel.get_Login_User_details(
                                    (activity as Home).sharedPreferencesStorage.getString(
                                        AppConstants.USER_ID
                                    )
                                )
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


    fun schoolListResponse() {

        (activity as Home?)?.homeviewmodel?.school_dataResponse?.observe(
            requireActivity(),
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        // AppUtils.hideLoader()
                        it.data.let { it1 ->
                            if (it1?.status == true) {

                                try {
                                    Log.d("TAG@123", it.toString())
                                    schoolList = ArrayList<UniversityList>()
                                    schoolList = it1.data.universityList
                                    fraternitiesList = ArrayList<UniversityList>()
                                    fraternitiesList = it1.data.fraternitiesList + it1.data.sororitiesList
                                    interest = ArrayList<Interest>()
                                    interest = it1.data.interestList


                                    // setAdapterData()
                                    setupChipGroupDynamically(interest!!)


                                } catch (e: Exception) {
                                    Log.d("TAG@123", "Exception schoolListResponse ${e.message}")
                                }
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



    fun subscribe_Login_User_details() {

        (activity as Home?)?.homeviewmodel?.get_user_data?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                dataList = ArrayList()
                                dataList.clear()
                                interestsList = ArrayList()
                                interestsList.clear()
                                Log.d("TAG@123", "1311" + it.toString())
                                if (!res.user.photos.isNullOrEmpty()) {
                                    dataList = res.user.photos
                                }
                                set_Adapterdata()

                                res.user.university.let {
                                    university = it
                                    schoolAct_spinner!!.setText(university)
                                }?: run {
                                    university = ""
                                }
                                res.user.community.let {
                                    community = it
                                    fraternity_Spinner.setText(community)
                                }?: run {
                                    university = ""
                                }
                                res.user.about.let {
                                    about = it
                                    _binding?.userAbout?.setText(about)
                                }?: run {
                                    about = ""
                                    _binding?.userAbout?.setText(about)
                                }
                                interestsList = res.user.interests.split(",") as ArrayList<String>


                            } catch (e: Exception) {
                                Log.d("TAG@123", "Exception subscribe_Login_User_details ${e.message}")

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

            openGallery()


        } else {

            (activity as Home).homeviewmodel.User_delete_images(

                (activity as Home).sharedPreferencesStorage.getString(
                    AppConstants.USER_ID
                ), dataList.get(position)

            )
        }

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

            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {

                    if (data != null) {
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                data.data
                            )
                            Bitmap.createScaledBitmap(bitmap, 350, 350, true);
                            convertBitmapToBase64(bitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }


                }
        }
    }


    fun convertBitmapToBase64(bm: Bitmap) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("")
        progressDialog.setMessage("please wait ...")
        progressDialog.show()
        doAsync {
            var encoded = ""
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.PNG, 0, baos)
            val b = baos.toByteArray()
            encoded = Base64.encodeToString(b, Base64.DEFAULT)
            (activity as Home).homeviewmodel.User_upload_images(
                (activity as Home).sharedPreferencesStorage.getString(
                    AppConstants.USER_ID
                ), "data:image/png;base64," + encoded
            )

            Log.d("TAG@123", "  images  --------- " + encoded)
            progressDialog.dismiss()


        }
    }
    /* private fun setAdapterData() {

         try {
             val schoolAdapter = AppReseources.getAppContext()?.let {
                 CommunityAdapter(
                     it,
                     schoolList!! as ArrayList<UniversityList>
                 )
             }
             schoolAct_spinner?.adapter = schoolAdapter
             schoolAdapter?.notifyDataSetChanged()
             schoolAct_spinner?.setSelection(index(schoolAct_spinner!!, university))


             val adapter = AppReseources.getAppContext()?.let {
                 CommunityAdapter(
                     it,
                     fraternitiesList!! as ArrayList<UniversityList>
                 )
             }
             fraternity_Spinner.adapter = adapter
             adapter?.notifyDataSetChanged()
             fraternity_Spinner.setSelection(index(fraternity_Spinner, community))
         } catch (e: Exception) {

             Log.d("TAG@123", "Ex -:" + e.message.toString())
         }

     }*/


    private fun setupChipGroupDynamically(list: List<Interest>) {
        try {
            rootContainer.removeAllViews()
            for (i in list.indices) {
                rootContainer.addView(createChip(list.get(i).interest, i))
            }
        } catch (e: Exception) {
        }
    }

    private fun createChip(label: String, index: Int): Chip {

        val chip = Chip(requireContext(), null)
        chip.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        chip.text = label

        chip.isCloseIconVisible = false
        chip.isChipIconVisible = true
        chip.isCheckable = true
        chip.isClickable = true

        chip.isChecked = interestsList.contains(label)
        chip.setBackgroundColor(
            if (interestsList.contains(label)) {
                ContextCompat.getColor(AppReseources.getAppContext()!!, R.color.light_blue_900)
            } else {
                ContextCompat.getColor(AppReseources.getAppContext()!!, R.color.teal_200)
            }
        )


        chip.chipCornerRadius = 1.0F
        chip.setOnClickListener {
            Log.d("TAG@123", interestsList.toString())
            if (chip.isChecked) {
                if (!interestsList.contains(label)) {
                    interestsList.add(label)
                    chip.setBackgroundColor(
                        ContextCompat.getColor(
                            AppReseources.getAppContext()!!,
                            R.color.light_blue_900
                        )
                    )
                }

            } else {
                if (interestsList.contains(label)) {
                    interestsList.remove(label)
                    chip.setBackgroundColor(
                        ContextCompat.getColor(
                            AppReseources.getAppContext()!!,
                            R.color.teal_200
                        )
                    )

                }
            }
            Log.d("TAG@123", interestsList.toString())

        }
        chip.setOnCloseIconClickListener {
        }
        return chip

    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        schoolAdapter.filter.filter(p0)
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        schoolAdapter.filter.filter(p0)
        return false
    }
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
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
            /*  (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                  AppConstants.community,
                  position.name
              )*/
        } else {
            schoolAct_spinner!!.setText(clickItemData)
            university = clickItemData
            /*(activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                AppConstants.university,
                position.name
            )*/
        }

        if (dialog != null || dialog.isShowing) {
            dialog.dismiss()
        }
    }
}






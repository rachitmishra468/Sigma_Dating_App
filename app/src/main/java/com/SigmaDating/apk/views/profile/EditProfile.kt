package com.SigmaDating.apk.views.profile

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.demoapp.other.Status
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.SigmaDating.R
import com.SigmaDating.apk.adapters.CommunityAdapter
import com.SigmaDating.apk.adapters.Edit_Profile_Adapter
import com.SigmaDating.databinding.FragmentEditProfileBinding
import com.SigmaDating.apk.model.communityModel.Interest
import com.SigmaDating.apk.model.communityModel.UniversityList
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.utilities.AppUtils.index
import com.SigmaDating.apk.views.Home
import org.jetbrains.anko.doAsync
import java.io.ByteArrayOutputStream
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditProfile : Fragment(), Edit_Profile_Adapter.OnCategoryClickListener {
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
    lateinit var fraternity_Spinner: Spinner
    private var schoolAct_spinner: Spinner? = null
    lateinit var rootContainer: ChipGroup
    var university: String? = null
    var community: String? = null
    var about: String? = null
   // var interests: String? = null



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





        schoolAct_spinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                university = (parent?.getItemAtPosition(pos) as UniversityList).name
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        fraternity_Spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                community = (parent?.getItemAtPosition(pos) as UniversityList).name
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

       // (activity as Home?)?.homeviewmodel?.getSchoolingData()

        subscribe_Login_User_details()
        subscribe_edit_profile()
        subscribe_upload_images()
        subscribe_delete_images()
        schoolListResponse()

        (activity as Home).homeviewmodel.get_edit_page_data(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )
        return binding.root
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
        (activity as Home?)?.homeviewmodel?.update_profile?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG)
                                    .show()
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
        (activity as Home?)?.homeviewmodel?.delete_images?.observe(this, Observer {
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
        (activity as Home?)?.homeviewmodel?.upload_images?.observe(this, Observer {
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
                                    fraternitiesList =
                                        it1.data.fraternitiesList + it1.data.sororitiesList
                                    interest = ArrayList<Interest>()
                                    interest = it1.data.interestList
                                    setAdapterData()
                                    setupChipGroupDynamically(interest!!)


                                } catch (e: Exception) {
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

        (activity as Home?)?.homeviewmodel?.get_user_data?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {


                                dataList = ArrayList()
                                interestsList=ArrayList()
                                Log.d("TAG@123", "1311" + it.data.toString())
                                if (!res.user.photos.isNullOrEmpty()) {
                                    dataList = res.user.photos
                                }
                                set_Adapterdata()

                                    res.user.university.let {
                                        university = it
                                    }
                                    res.user.community.let {
                                        community = it
                                    }
                                    res.user.about.let {
                                        about = it
                                        _binding?.userAbout?.setText(about)
                                    }

                                interestsList = res.user.interests.split(",") as ArrayList<String>



                            } catch (e: Exception) {
                                Log.d("TAG@123","Exception ${e.message}")

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
        dataList?.add("ADD_IMAGES")
        photoAdapter = Edit_Profile_Adapter(requireContext(), this)
        _binding?.updateImageView?.adapter = photoAdapter
        dataList?.let { photoAdapter.setDataList(it) }
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
                                activity!!.contentResolver,
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
    private fun setAdapterData() {

        try {
            val schoolAdapter = CommunityAdapter(
                (activity as Home),
                schoolList!! as ArrayList<UniversityList>
            )
            schoolAct_spinner?.adapter = schoolAdapter
            schoolAdapter.notifyDataSetChanged()
            schoolAct_spinner?.setSelection(index(schoolAct_spinner!!, university))


            val adapter = CommunityAdapter(
                (activity as Home),
                fraternitiesList!! as ArrayList<UniversityList>
            )
            fraternity_Spinner.adapter = adapter
            adapter.notifyDataSetChanged()
            fraternity_Spinner.setSelection(index(fraternity_Spinner, community))
        } catch (e: Exception) {

            Log.d("TAG@123", "Ex -:" + e.message.toString())
        }

    }


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

        chip.chipCornerRadius = 1.0F
        chip.setOnClickListener {
            Log.d("TAG@123", interestsList.toString())
            if (chip.isChecked) {
                if (!interestsList.contains(label)) {
                    interestsList.add(label)
                }

            } else {
                if (interestsList.contains(label)) {
                    interestsList.remove(label)

                }
            }
            Log.d("TAG@123", interestsList.toString())

        }
        chip.setOnCloseIconClickListener {
        }
        return chip

    }
}






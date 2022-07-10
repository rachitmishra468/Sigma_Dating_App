package com.sigmadatingapp.views.profile

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.demoapp.other.Status
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sigmadatingapp.R
import com.sigmadatingapp.adapters.CommunityAdapter
import com.sigmadatingapp.adapters.Edit_Profile_Adapter
import com.sigmadatingapp.adapters.Profile_Adapter
import com.sigmadatingapp.databinding.FragmentEditProfileBinding
import com.sigmadatingapp.model.EditProfiledata
import com.sigmadatingapp.model.communityModel.Interest
import com.sigmadatingapp.model.communityModel.UniversityList
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.utilities.AppUtils
import com.sigmadatingapp.views.Home
import com.sigmadatingapp.views.intro_registration.OnBoardingActivity
import okhttp3.internal.notifyAll
import org.jetbrains.anko.doAsync
import java.io.ByteArrayOutputStream

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
    var fraternitiesList: List<UniversityList>? = null
    var schoolList: List<UniversityList>? = null
    var interest: List<Interest>? = null
    lateinit var fraternity_Spinner: Spinner
    private var schoolAct_spinner: Spinner? = null
    lateinit var rootContainer: ChipGroup
    var university: String? = null
    var community: String? = null
    var about: String? = null
    var interests: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        (activity as Home).homeviewmodel.get_Login_User_details(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )

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

            university?.let { it1 ->
                community?.let { it2 ->
                    interests?.let { it3 ->
                        (activity as Home).homeviewmodel.update_profile(
                            (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID),
                            it1, it2, it3, _binding!!.etLastname.text.toString()
                        )
                    }
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


        subscribe_Login_User_details()
        subscribe_edit_profile()
        subscribe_upload_images()
        subscribe_delete_images()
        schoolListResponse()


        (activity as Home?)?.homeviewmodel?.getSchoolingData()



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
                                Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                .show()
                            }catch (e:Exception){}
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
                            }catch (e:Exception){}

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


                            Log.d("TAG@123", "1311" + res.toString())
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                .show()
                            (activity as Home).homeviewmodel.get_Login_User_details(
                                (activity as Home).sharedPreferencesStorage.getString(
                                    AppConstants.USER_ID
                                )
                            )
                            }catch (e:Exception){}

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
                                }catch (e:Exception){}
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
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {


                            dataList = ArrayList()
                            Log.d("TAG@123", "1311" + res.toString())
                            if (!res.user.photos.isNullOrEmpty()) {

                                dataList = res.user.photos

                                res.user.university.let {
                                    university = it
                                }
                                res.user.community.let {
                                    community = it
                                }
                                res.user.about.let {
                                    about = it
                                    _binding?.etLastname?.setText(about)
                                }
                                res.user.interests.let {
                                    interests = it
                                }


                            }
                            set_Adapterdata()
                            }catch (e:Exception){}

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


    fun set_Adapterdata() {
        dataList?.add("ADD_IMAGES")
        photoAdapter = Edit_Profile_Adapter(requireContext(), this)
        _binding?.updateImageView?.adapter = photoAdapter
        dataList?.let { photoAdapter.setDataList(it) }
    }

    override fun onCategoryClick(position: Int, boolean: Boolean) {


        if (boolean) {
            val checkSelfPermission = ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                )
            } else {
                openGallery()
            }

        } else {

            (activity as Home).homeviewmodel.User_delete_images(

                (activity as Home).sharedPreferencesStorage.getString(
                    AppConstants.USER_ID
                ), dataList.get(position)

            )
        }

    }

    private fun openGallery() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (resultCode == Activity.RESULT_OK) {
                        if (Build.VERSION.SDK_INT >= 19) {
                            handleImageOnKitkat(data)
                        }
                    }
                }
        }
    }

    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        if (DocumentsContract.isDocumentUri(requireActivity(), uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri?.authority) {
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selsetion
                )
            } else if ("com.android.providers.downloads.documents" == uri?.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse(
                        "content://downloads/public_downloads"
                    ), java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri?.scheme, ignoreCase = true)) {
            imagePath = getImagePath(uri!!, null)
        } else if ("file".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = uri.path
        }
        renderImage(imagePath)
    }


    private fun renderImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            getResizedBitmap(bitmap, 500)?.let { convertBitmapToBase64(it) }

        } else {
            show("ImagePath is null")
        }
    }

    private fun show(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
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

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }


    @SuppressLint("Range")
    private fun getImagePath(uri: Uri, selection: String?): String {
        var path: String? = null

        val cursor = activity?.contentResolver?.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }


    private fun setAdapterData() {

        try {
            val schoolAdapter = CommunityAdapter(
                (activity as Home),
                schoolList!! as ArrayList<UniversityList>
            )
            schoolAct_spinner?.adapter = schoolAdapter
            schoolAdapter.notifyDataSetChanged()

            val adapter = CommunityAdapter(
                (activity as Home),
                fraternitiesList!! as ArrayList<UniversityList>
            )
            fraternity_Spinner.adapter = adapter
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
        }

    }


    private fun setupChipGroupDynamically(list: List<Interest>) {
        try {
            rootContainer.removeAllViews()
            for (i in 0..list.size - 1) {
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
        if (label.equals(interests)) {
            chip.isChecked = true
        } else {

            chip.isChecked = false
        }
        chip.chipCornerRadius = 1.0F
        chip.setOnClickListener {
            interests=chip.text.toString()
        }
        chip.setOnCloseIconClickListener {
        }
        return chip

    }
}






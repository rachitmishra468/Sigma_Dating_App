package com.SigmaDating.apk.views.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
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
import com.SigmaDating.apk.adapters.InterestAdapter
import com.SigmaDating.apk.adapters.SchoolAdapter
import com.SigmaDating.apk.model.Loginmodel
import com.SigmaDating.databinding.FragmentEditProfileBinding
import com.SigmaDating.apk.model.communityModel.Interest
import com.SigmaDating.apk.model.communityModel.UniversityList
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.utilities.EmptyDataObserver
import com.SigmaDating.apk.views.Home
import com.SigmaDating.model.SchoolCommunityResponse
import com.example.demoapp.other.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditProfile : Fragment(), Edit_Profile_Adapter.OnCategoryClickListener,
    SearchView.OnQueryTextListener, SchoolAdapter.OnItemClickListener,InterestAdapter.OnItemClickListener {
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
    lateinit var interests_text:TextView
    private var schoolAct_spinner: EditText? = null
    lateinit var rootContainer: ChipGroup
    lateinit var rootContainer_intrest:ChipGroup

    var university: String? = null
    var community: String? = null
    var about: String? = null

    // var interests: String? = null
    private var mUri: Uri? = null
    private val OPERATION_CAPTURE_PHOTO = 1
    private lateinit var schoolAdapter: SchoolAdapter
    private lateinit var intrestAdapter: InterestAdapter
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
        interests_text= _binding?.root?.findViewById(R.id.interests_text)!!
        _binding?.updateImageView?.layoutManager = GridLayoutManager(requireContext(), 3)
        _binding?.imageView2?.setOnClickListener {
            (activity as Home).onBackPressed()
        }
        _binding?.done?.setOnClickListener {
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
                        _binding!!.userAbout.text.toString()
                    )
                }

            }
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


        fraternity_Spinner!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                schoolList = ArrayList<UniversityList>()
                schoolList = fraternitiesList
                openSchoolSearchDialog(
                    AppConstants.Fraternity,
                    fraternitiesList as List<UniversityList>
                )
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
        val done_interset= dialog.findViewById<Button>(R.id.done_interset)
        rootContainer_intrest=dialog.findViewById<ChipGroup>(R.id.rootContainer_intrest)
        setupChipGroupDynamically(interestsList!!,rootContainer_intrest,true)
        searchRecyclerView!!.layoutManager = GridLayoutManager(requireContext(), 2)
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
            setupChipGroupDynamically(interestsList!!,rootContainer,false)
        }

        dialog.setOnDismissListener {
            schoolAct_spinner!!.isEnabled = true
            fraternity_Spinner.isEnabled = true
            setupChipGroupDynamically(interestsList!!,rootContainer,false)
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
                                    fraternitiesList =
                                        it1.data.fraternitiesList + it1.data.sororitiesList
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
                                    setupChipGroupDynamically(interestsList!!,rootContainer,false)

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
            popup()
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
        val capturedImage = File(requireActivity().externalCacheDir, "My_Captured_Photo.jpg")
        if (capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        mUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                requireActivity(),
                "com.SigmaDating.apk.fileprovider",
                capturedImage
            )
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)
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
                    val bitmap = BitmapFactory.decodeStream(
                        requireActivity().getContentResolver().openInputStream(mUri!!)
                    )
                    val rotationMatrix = Matrix()
                    if (bitmap.getWidth() >= bitmap.getHeight()) {
                        rotationMatrix.setRotate((-90).toFloat())
                    } else {
                        rotationMatrix.setRotate((0).toFloat())
                    }

                    val rotatedBitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.getWidth(),
                        bitmap.getHeight(),
                        rotationMatrix,
                        true
                    )

                    Bitmap.createScaledBitmap(rotatedBitmap, 80, 90, true);
                    convertBitmapToBase64(rotatedBitmap, true)
                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {

                    if (data != null) {
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                data.data
                            )
                            Bitmap.createScaledBitmap(bitmap, 250, 250, true);
                            convertBitmapToBase64(bitmap, false)
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
                ), "data:image/png;base64," + encoded
            )
            Log.d("TAG@123", "  images  --------- " + encoded)
            progressDialog.dismiss()
        }

    }

    private fun setupChipGroupDynamically(list: List<String>, rootContainer:ChipGroup ,flag:Boolean) {
        if(list.size>0){
        try {
            rootContainer.removeAllViews()
            for (i in list.indices) {
                Log.d("TAG@123", "ChipGroup : "+list.get(i))
                if(list.get(i).length>0){
                    rootContainer.addView(createChip(list.get(i), i,flag))
                }

            }
        } catch (e: Exception) {
        }}
    }

    @SuppressLint("ResourceType")
    private fun createChip(label: String, index: Int, flag: Boolean): Chip {

        val chip = Chip(requireContext(), null)
        chip.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        chip.text = label

        chip.setBackgroundColor(
            if (interestsList.contains(label)) {
                ContextCompat.getColor(AppReseources.getAppContext()!!, R.color.light_blue_900)
            } else {
                ContextCompat.getColor(AppReseources.getAppContext()!!, R.color.teal_200)
            }
        )


        chip.chipCornerRadius = 1.0F

        if(flag){
            chip.isCloseIconVisible = true
            chip.isChipIconVisible = true
            chip.isCheckable = true
            chip.isClickable = true


            chip.setOnCloseIconClickListener {
                Log.d("TAG@123", interestsList.toString())
                    if (interestsList.contains(label)) {
                        interestsList.remove(label)
                        setupChipGroupDynamically(interestsList!!,rootContainer_intrest, true)
                }
                Log.d("TAG@123", interestsList.toString())

            }
        }
        else{
            chip.isCloseIconVisible = false
            chip.isChipIconVisible = false
            chip.isCheckable = false
            chip.isClickable = false
        }

        return chip

    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        if(this::schoolAdapter.isInitialized){
            schoolAdapter.filter.filter(p0)
        }
        if(this::intrestAdapter.isInitialized){
            intrestAdapter.filter.filter(p0)
        }

        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if(this::schoolAdapter.isInitialized){
            schoolAdapter.filter.filter(p0)
        }
        if(this::intrestAdapter.isInitialized){
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
        subscribe_Login_User_details()
        (activity as Home).homeviewmodel.get_Edit_User_details(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )
    }

    override fun onIntrestClick(position: Interest, stringtype: String) {
        //interestsList


        var clickItemData = position.interest
          if(interestsList.size<=6){
              if(interestsList.contains(clickItemData)==false){
                  interestsList.add(clickItemData)
                  setupChipGroupDynamically(interestsList!!,rootContainer_intrest,true)
              }else{
                  Toast.makeText(requireContext(), "Already added", Toast.LENGTH_LONG)
                      .show()
              }
          }
        else{
              Toast.makeText(requireContext(), "You can only choose maximum 6.", Toast.LENGTH_LONG)
                  .show()

        }


    }
}






package com.SigmaDating.app.views.post

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.SigmaDating.R
import com.SigmaDating.app.model.Loginmodel
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.views.Home
import com.SigmaDating.databinding.FragmentCreatePostBinding
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.SigmaDating.app.AppReseources

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import android.annotation.SuppressLint
import android.app.Dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.text.InputType
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.app.adapters.User_Tag_Adapter
import com.SigmaDating.app.model.Match_bids
import com.SigmaDating.app.model.User_bids_list
import com.SigmaDating.app.utilities.*
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import kotlinx.coroutines.delay as delay


class CreatePost : Fragment(), User_Tag_Adapter.OnCategoryClickListener {
    val CAMERA_REQUEST_CODE = 102
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val permissions33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_AUDIO
    )

    lateinit var schoolAdapter : User_Tag_Adapter
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    var location_text = ""
    var latitude = ""
    var longitude = ""
    lateinit var user_tag_id: ArrayList<String>
    lateinit var dialog: Dialog
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private var mUri: Uri? = null
    private val OPERATION_CHOOSE_PHOTO = 2
    var file: File? = null
    lateinit var selectedImage: Uri
    var currentPhotoPath: String? = null
    private var dataList = mutableListOf<User_bids_list>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        user_tag_id = ArrayList()
        (activity as Home).homeviewmodel.all_match_bids = MutableLiveData<Resource<Match_bids>>()
        subscribe_bids_list()
        (activity as Home).homeviewmodel.get_user_match_bids(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )


        _binding!!.postCreationGroup.setOnCheckedChangeListener { group, checkedId ->
            val rb = _binding!!.root.findViewById(checkedId) as RadioButton

            when(rb.text.toString()){
                "Public"->{

                }
                "Private"->{

                }
            }
            Log.d("TAG@123", "is_private   "+rb.text.toString())
        }

        _binding?.let {
            it.backPost.setOnClickListener {
                (activity as Home).onBackPressed()
            }

            it.imageProfile.setOnClickListener {
                checkGallerypermission()
            }

            it.tagUser.setOnClickListener {
                if (!dataList.isNullOrEmpty()) {
                    openUserTagDialog(dataList)
                }
            }
            it.tagLocation.setOnClickListener {
                Update_phone_location()
            }
        }


        _binding?.done?.setOnClickListener {
           /* if (_binding?.postTitle?.text.toString().equals("")) {
                _binding?.postTitle?.error = "Enter Post Title .."
            }
            else*/
           /* if (_binding?.postDiscription?.text.toString().equals("")) {
                _binding?.postDiscription?.error = "Enter Post Caption .."
            } else*/
            if (file == null) {
                Toast.makeText(requireContext(), "Please select a media to upload.", Toast.LENGTH_LONG).show()
            }
            else if (!_binding!!.postPublic.isChecked && !_binding!!.postPrivate.isChecked) {
                Toast.makeText(requireContext(), "Please select Post Visibility", Toast.LENGTH_LONG).show()
            }
            else {
                (activity as Home).homeviewmodel.create_post =
                    MutableLiveData<Resource<Loginmodel>>()
                subscribe_create_post()
                Log.d("TAG@123", (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID))
                val map: HashMap<String, String> = HashMap()
               // map.put("title", _binding?.postTitle?.text.toString())
                map.put("title", "")
                map.put("user_id", (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID))
                map.put("description", _binding?.postDiscription?.text.toString())
                map.put("location", location_text)
                if(_binding!!.postPrivate.isChecked){
                    map.put("isPrivate", "0")
                }else{
                    map.put("isPrivate", "1")
                }
                map.put("tag_users", user_tag_id.joinToString(","))

                Log.d("TAG@123", map.toString())

                (activity as Home).homeviewmodel.create_post(file!!, map)
            }
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AppReseources.getAppContext()!!)
        getLocation()
        return binding.root
    }


    fun Update_phone_location() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.update_phone_location, null)
        val title_text = view.findViewById<TextView>(R.id.textView5)
        val current_value = view.findViewById<EditText>(R.id.editText_password)
        val update_value = view.findViewById<Button>(R.id.update_value)
        val update_current = view.findViewById<Button>(R.id.update_current)
        title_text.setText("Update Location With Post Code.")
        current_value.setHint("Enter Location With Post Code.")
        current_value.inputType = InputType.TYPE_CLASS_TEXT
        update_current.setOnClickListener {
            _binding?.let {
                it.textUpdateLocation.visibility = View.VISIBLE
                getLocation()

            }
            dialog.dismiss()
        }
        update_value.setOnClickListener {
            if (!current_value.text.toString().isEmpty()) {
                location_text = current_value.text.toString()
                _binding?.let {
                    it.textUpdateLocation.visibility = View.VISIBLE
                    it.textUpdateLocation.text = current_value.text
                }
                dialog.dismiss()
            } else {
                current_value.setError("Please Enter valid Location.. ")
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    private fun checkGallerypermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(requireActivity(),
                    permissions,
                    AppConstants.STORAGE_PERMISSION_REQUEST_CODE)
            } else {
                popup()
            }
        }
        else {

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
    }
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {

                        val geocoder = AppReseources.getAppContext()
                            ?.let { Geocoder(it, Locale.getDefault()) }

                        val list: MutableList<Address>? =
                            geocoder?.getFromLocation(location.latitude, location.longitude, 1)


                        CoroutineScope(Dispatchers.Main).launch {
                            delay(500)
                            latitude = "${list?.get(0)?.latitude}"
                            longitude = "${list?.get(0)?.longitude}"
                            location_text = "${list?.get(0)?.locality}"
                            Log.d("TAG@123", "location name" + location_text)
                            _binding?.textUpdateLocation?.text= location_text
                            _binding?.textUpdateLocation?.visibility=View.VISIBLE

                        }


                    }
                }

            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            requestPermissions()
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
        // Ensure that there's a camera activity to handle the intent
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    requireContext(), "com.SigmaDating.app.provider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
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
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/* video/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
            activity?.packageManager?.let {
                intent.resolveActivity(it)?.also {
                    startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAG@123", "resultCode :" + resultCode)
        when (requestCode) {

            CAMERA_REQUEST_CODE ->
                if (resultCode == Activity.RESULT_OK) {
                    file = File(currentPhotoPath)
                    _binding?.imageProfile?.setImageURI(Uri.fromFile(file))
                    selectedImage = Uri.fromFile(file)
                    Log.d("TAG@123", "URI :" + selectedImage)
                    Log.d("TAG@123", "ABsolute Url of Image is " + Uri.fromFile(file))

                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        selectedImage = data!!.data!!
                        val filePath = URIPathHelper().getPath(requireContext(), selectedImage)
                        file = File(filePath)
                        val mimeType: String? = selectedImage.let { returnUri ->
                            requireContext().contentResolver.getType(returnUri)
                        }
                        if (mimeType.toString().equals("video/mp4")) {
                            _binding?.imageProfile?.let {
                                Glide.with(requireContext()).asBitmap().load(selectedImage).into(it)
                            };
                        } else {
                            _binding?.imageProfile?.let {
                                Glide.with(requireContext()).load(file).into(
                                    it
                                )
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("TAG@123", "File URI Exception : ${e.message}")

                    }
                }
        }
    }


    fun subscribe_create_post() {
        (activity as Home?)?.homeviewmodel?.create_post?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                                Log.d("TAG@123", "111 " + res.message)
                                show_dilog(res.message)
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                Log.d("TAG@123", "111 " + res?.message)
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


    fun show_dilog(mes: String) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Are you want to Create More Post.")
        builder.background = ColorDrawable(
            Color.parseColor("#FFFFFF")
        )
        builder.setPositiveButton("Yes") { dialog, which ->
            _binding?.postTitle?.setText("")
            _binding?.postDiscription?.setText("")
            _binding?.imageProfile?.setImageResource(R.drawable.icon_camera);
        }
        builder.setNegativeButton("No") { dialog, which ->
            (activity as Home).onBackPressed()
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }


    fun subscribe_bids_list() {
        (activity as Home?)?.homeviewmodel?.all_match_bids?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                                dataList = res.data as ArrayList<User_bids_list>
                                // setAdapterListData(res.data as ArrayList<User_bids_list>)
                            } else {

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


    @SuppressLint("NotifyDataSetChanged")
    private fun openUserTagDialog(passDataList: List<User_bids_list>) {
        dialog = Dialog(requireContext(), R.style.AppBaseTheme2)
        dialog.setContentView(R.layout.user_tag_sheet)
        var searchRecyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_user_tag)
        var doneBtn = dialog.findViewById<Button>(R.id.text_done)
        val titleText = dialog.findViewById<TextView>(R.id.title_layout)
        titleText.setText("Tag People")
        searchRecyclerView!!.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )

        schoolAdapter = User_Tag_Adapter(requireContext(), this)
        searchRecyclerView!!.adapter = schoolAdapter
        schoolAdapter.setDataList(passDataList)
        schoolAdapter.notifyDataSetChanged()
        dialog.show()
        if (dialog.isShowing) {
            doneBtn.setOnClickListener { dialog.dismiss() }
        }

    }

    override fun onCategoryClick(position: User_bids_list) {
        if (!user_tag_id.contains(position.id)) {
            position.tag_add = true
            user_tag_id.add(position.id)
          // Toast.makeText(requireContext(), "Added", Toast.LENGTH_SHORT).show()
        } else {
            position.tag_add = false
            user_tag_id.remove(position.id)
            schoolAdapter.notifyDataSetChanged()
           // Toast.makeText(requireContext(), "Already Tag", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }



}
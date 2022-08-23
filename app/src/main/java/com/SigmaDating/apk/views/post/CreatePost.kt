package com.SigmaDating.apk.views.post

import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.SigmaDating.R
import com.SigmaDating.apk.model.Loginmodel
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.views.Home
import com.SigmaDating.databinding.FragmentCreatePostBinding
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import android.R.attr.description
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Environment
import android.provider.DocumentsContract
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import okhttp3.RequestBody
import okhttp3.MultipartBody
import java.io.FileOutputStream
import java.lang.Exception
import androidx.core.app.ActivityCompat.startActivityForResult
import com.SigmaDating.apk.AppReseources

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import android.R.attr.data
import android.annotation.SuppressLint
import android.app.Dialog
import android.webkit.MimeTypeMap

import android.content.ContentResolver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.SearchView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.apk.adapters.SchoolAdapter
import com.SigmaDating.apk.adapters.User_Tag_Adapter
import com.SigmaDating.apk.model.Match_bids
import com.SigmaDating.apk.model.User_bids_list
import com.SigmaDating.apk.model.communityModel.UniversityList
import com.SigmaDating.apk.utilities.EmptyDataObserver
import com.SigmaDating.apk.utilities.FileUtils
import com.SigmaDating.apk.utilities.URIPathHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class CreatePost : Fragment(), User_Tag_Adapter.OnCategoryClickListener {
    val CAMERA_REQUEST_CODE = 102
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    lateinit var user_tag_id: ArrayList<String>
    lateinit var dialog: Dialog
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private var mUri: Uri? = null
    private val OPERATION_CHOOSE_PHOTO = 2
    lateinit var file: File
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

        _binding?.let {
            it.backPost.setOnClickListener {
                (activity as Home).onBackPressed()
            }

            it.imageProfile.setOnClickListener {
                checkGallerypermission()
            }

            it.tagUser.setOnClickListener {
                if (!dataList.isNullOrEmpty()) {
                    openUserTagDialog( dataList)
                }
            }

        }


        _binding?.done?.setOnClickListener {
            if (_binding?.postTitle?.text.toString().equals("")) {
                _binding?.postTitle?.error = "Enter Post Title.."
            } else if (_binding?.postDiscription?.text.toString().equals("")) {
                _binding?.postDiscription?.error = "Enter Post Caption.."
            } else if (!file.isFile) {
                Toast.makeText(requireContext(), "Add Image", Toast.LENGTH_LONG)
                    .show()
            } else {
                (activity as Home).homeviewmodel.create_post =
                    MutableLiveData<Resource<Loginmodel>>()
                subscribe_create_post()

                Log.d(
                    "TAG@123",
                    (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                )

                val map: HashMap<String, String> = HashMap()
                map.put("title", _binding?.postTitle?.text.toString())
                map.put(
                    "user_id",
                    (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                )
                map.put("description", _binding?.postDiscription?.text.toString())
                map.put("location", "")
                map.put("tag_users",user_tag_id.joinToString(","))


                (activity as Home).homeviewmodel.create_post(file, map)

            }

        }

        return binding.root
    }


    private fun checkGallerypermission() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                permissions,
                AppConstants.STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            popup()
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
                    requireContext(), "com.SigmaDating.apk.provider",
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
            intent.type = "image/*"
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
                    selectedImage = data!!.data!!
                    val filePath = URIPathHelper().getPath(requireContext(), selectedImage)
                    file = File(filePath)
                    Log.d("TAG@123", "FILEPATH :" + filePath)
                    Log.d("TAG@123", "FILE  PATH :" + file.absolutePath)
                    _binding?.imageProfile?.setImageURI(selectedImage)
                    Log.d("TAG@123", "URI :" + selectedImage)
                    Log.d("TAG@123", "PATH :" + selectedImage.path)
                    Log.d("TAG@123", "ABsolute Url of Image is " + Uri.fromFile(file))
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
    private fun openUserTagDialog( passDataList: List<User_bids_list>) {
        dialog = Dialog(requireContext(), R.style.AppBaseTheme2)
        dialog.setContentView(R.layout.user_tag_sheet)
        var searchRecyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_user_tag)
        val titleText = dialog.findViewById<TextView>(R.id.title_layout)
        titleText.setText("Tag People")
        searchRecyclerView!!.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )

        var schoolAdapter = User_Tag_Adapter(requireContext(), this)
        searchRecyclerView!!.adapter = schoolAdapter
        schoolAdapter.setDataList(passDataList)
        schoolAdapter.notifyDataSetChanged()
        dialog.show()
        /* schoolAct_spinner!!.isEnabled = false
         fraternity_Spinner.isEnabled = false
         dialog.setOnDismissListener {
             schoolAct_spinner!!.isEnabled = true
             fraternity_Spinner.isEnabled = true
         }*/
    }

    override fun onCategoryClick(position: User_bids_list) {
        if (!user_tag_id.contains(position.id)) {
            position.tag_add=true
            user_tag_id.add(position.id)
            Toast.makeText(requireContext(), "Added", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Already Tag", Toast.LENGTH_LONG).show()
        }
    }


}
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
import android.webkit.MimeTypeMap

import android.content.ContentResolver
import androidx.core.app.ActivityCompat.startActivityForResult
import com.SigmaDating.apk.utilities.FileUtils
import com.SigmaDating.apk.utilities.URIPathHelper


class CreatePost : Fragment() {

    val CAMERA_PERM_CODE = 101
    val CAMERA_REQUEST_CODE = 102
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private var mUri: Uri? = null
    private val OPERATION_CHOOSE_PHOTO = 2
    private val OPERATION_CAPTURE_PHOTO = 102
    var encoded = ""
    lateinit var file: File
    lateinit var selectedImage: Uri
    var currentPhotoPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
_binding?.let {
   it.backPost.setOnClickListener {
       (activity as Home).onBackPressed()
   }

    it.imageProfile?.setOnClickListener {
        checkGallerypermission()

    }

    it.interestsText.setOnClickListener {

    }

}


        _binding?.done?.setOnClickListener {
            if (_binding?.postTitle?.text.toString().equals("")) {
                _binding?.postTitle?.error = "Enter Post Title.."
            } else if (_binding?.postDiscription?.text.toString().equals("")) {
                _binding?.postDiscription?.error = "Enter Post Caption.."
            } /*else if (encoded.equals("")) {
                Toast.makeText(requireContext(), "Add Image", Toast.LENGTH_LONG)
                    .show()
            }*/ else {
                (activity as Home).homeviewmodel.create_post = MutableLiveData<Resource<Loginmodel>>()
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
                    requireContext()       ,             "com.SigmaDating.apk.provider",
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
                    selectedImage=Uri.fromFile(file)
                    Log.d("TAG@123", "URI :" + selectedImage)
                    Log.d("TAG@123", "ABsolute Url of Image is " + Uri.fromFile(file))

                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = data!!.data!!
                    val filePath = URIPathHelper().getPath(requireContext(), selectedImage)
                    file= File(filePath)
                    Log.d("TAG@123", "FILEPATH :" + filePath)
                    Log.d("TAG@123", "FILE  PATH :" + file.absolutePath)
                    _binding?.imageProfile?.setImageURI(selectedImage)
                    Log.d("TAG@123", "URI :" + selectedImage)
                    Log.d("TAG@123", "PATH :" + selectedImage.path)
                    Log.d("TAG@123", "ABsolute Url of Image is " + Uri.fromFile(file))
                }
        }
    }


    fun getPathFromURI(contentUri: Uri?): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            requireActivity().getContentResolver().query(contentUri!!, proj, null, null, null)!!
        if (cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res
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


    fun bitmapToFile(filepath: String?): File? { // File name like "image.png"

        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(filepath)
            val scaledBitmap = BitmapFactory.decodeFile(file.path)
            val bytes = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, bytes)
            val bitmapdata = bytes.toByteArray()
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }


    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (requireContext().contentResolver != null) {
            val cursor = requireContext().contentResolver.query(
                uri!!, null, null, null, null
            )
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }


}
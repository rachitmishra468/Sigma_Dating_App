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
import com.SigmaDating.apk.utilities.FileUtils


class CreatePost : Fragment() {
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private var mUri: Uri? = null
    private val OPERATION_CHOOSE_PHOTO = 2
    private val OPERATION_CAPTURE_PHOTO = 1
    var encoded = ""
    lateinit var file : File
    lateinit var selectedImage:Uri

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
                (activity as Home).homeviewmodel.create_post= MutableLiveData<Resource<Loginmodel>>()
                subscribe_create_post()

                Log.d(
                    "TAG@123",
                    (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                )

                val map: HashMap<String, String> = HashMap()
                map.put("title",_binding?.postTitle?.text.toString())
                map.put("user_id",(activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID))
                map.put("description",_binding?.postDiscription?.text.toString())
                map.put("location", "")
                getContext()?.getContentResolver()?.getType(selectedImage)?.let { it1 ->
                    (activity as Home).homeviewmodel.create_post(file,map,
                        it1
                    )
                }
            }

        }

        return binding.root
    }


    private fun checkGallerypermission() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
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
        val capturedImage = File(requireActivity().externalCacheDir, "MyPhoto.jpg")
        if (capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        selectedImage = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                AppReseources.getAppContext()!!,
                AppReseources.getAppContext()!!.getPackageName().toString() + ".provider",
                capturedImage
            )
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)

    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
          /*  val mimeTypes = arrayOf("image/jpg", "image/png","image/gif")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)*/
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
        Log.d("TAG@123","resultCode :"+resultCode)
        when (requestCode) {

            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    val file_t = File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg")
                    selectedImage  = FileProvider.getUriForFile(
                        AppReseources.getAppContext()!!,
                        AppReseources.getAppContext()!!.getApplicationContext().getPackageName().toString() + ".provider",
                        file_t
                    )
                    Log.d("TAG@123","URI :"+selectedImage)
                    file = bitmapToFile(FileUtils(requireContext()).getPath(selectedImage))
                    val bitmap = BitmapFactory.decodeStream(
                        requireActivity().getContentResolver().openInputStream(selectedImage!!)
                    )
                    _binding?.imageProfile?.setImageBitmap(bitmap)


                    /*  val bitmap = BitmapFactory.decodeStream(
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
                      _binding?.imageProfile?.setImageBitmap(rotatedBitmap)
                      Bitmap.createScaledBitmap(rotatedBitmap, 80, 90, true);
                      convertBitmapToBase64(rotatedBitmap, true)*/
                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = data!!.data!!
                    file = bitmapToFile(getRealPathFromURI(requireContext(),selectedImage))

                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        data.data
                    )
                    _binding?.imageProfile?.setImageBitmap(bitmap)

/*
                    if (data != null) {
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                data.data
                            )
                            _binding?.imageProfile?.setImageBitmap(bitmap)
                            Bitmap.createScaledBitmap(bitmap, 250, 250, true);
                            convertBitmapToBase64(bitmap, false)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
*/


                }
        }
    }







    fun convertBitmapToBase64(bm: Bitmap, flag: Boolean) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("")
        progressDialog.setMessage("Please wait ...")
        progressDialog.show()
        (activity as Home).homeviewmodel.upload_images = MutableLiveData<Resource<Loginmodel>>()
        // subscribe_upload_images()
        doAsync {

            val baos = ByteArrayOutputStream()
            if (flag) {
                bm.compress(Bitmap.CompressFormat.PNG, 10, baos)
            } else {
                bm.compress(Bitmap.CompressFormat.PNG, 50, baos)
            }

            val b = baos.toByteArray()
            encoded = Base64.encodeToString(b, Base64.DEFAULT)
            encoded = "data:image/png;base64,"+ encoded
            Log.d("TAG@123", "  images  --------- " + encoded)
            progressDialog.dismiss()
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




    fun bitmapToFile(filepath: String?): File { // File name like "image.png"

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
        } catch (e: Exception){

            return file!!
        }
    }


    fun getRealPathFromURI(uri: Uri?): String {
        var path = ""
        if (AppReseources.getAppContext()!!.getContentResolver() != null) {
            val cursor: Cursor? = AppReseources.getAppContext()!!.getContentResolver().query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        val isKitKatorAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKatorAbove && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.ImageColumns._ID
        val projection = arrayOf(column)
        try {
            cursor = context.getContentResolver().query(uri!!, projection, selection, selectionArgs,null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

}
package com.sigmadatingapp.views.intro_registration

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.demoapp.other.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sigmadatingapp.R
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.storage.SharedPreferencesStorage
import com.sigmadatingapp.utilities.AppUtils
import com.sigmadatingapp.views.Home
import com.sigmadatingapp.views.login.LoginViewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Profile_Photo : Fragment() {

    //Our constants
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    private var param1: String? = null
    private var param2: String? = null
    private var profile_continue: Button? = null
    private var imageProfile: CircleImageView? = null
    private var mUri: Uri? = null
    var img_choose_dummy: CircleImageView? = null
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

        var view = inflater.inflate(R.layout.fragment_profile__photo, container, false)

        profile_continue = view.findViewById(R.id.profile_continue)
        imageProfile = view.findViewById(R.id.img_profile)
        profile_continue?.setOnClickListener {

            // startActivity(Intent(context, Home::class.java))
            //(activity as OnBoardingActivity?)?.finish
            if (AppUtils.isNetworkInterfaceAvailable(requireContext())) {

                (activity as OnBoardingActivity?)?.userRegister?.Register()
            }

        }

        imageProfile?.setOnClickListener {

            RatePopup()
        }

        Register()
        return view;
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun RatePopup() {
        val view = layoutInflater.inflate(R.layout.selectcamera_gallery_layout, null)
        val dialog = activity?.let { BottomSheetDialog(requireActivity(),R.style.DialogStyle) }!!
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
        img_choose_dummy = dialog.findViewById<CircleImageView>(R.id.img_choose_dummy)
        val button_camera = dialog.findViewById<CircleImageView>(R.id.button_camera)
        val button_gallery = dialog.findViewById<CircleImageView>(R.id.button_gallery)


        button_camera?.setOnClickListener {
            dialog.dismiss()
            capturePhoto()
        }
        button_gallery?.setOnClickListener {
            val checkSelfPermission = ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                //Requests permissions to be granted to this application at runtime
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                )
            } else {
                dialog.dismiss()
                openGallery()
            }
        }

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile_Photo().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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
                "com.sigmadatingapp.fileprovider",
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
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }

    private fun renderImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            imageProfile?.setImageBitmap(bitmap)
            if (bitmap != null) {
                img_choose_dummy?.visibility = View.GONE
            }

        } else {
            show("ImagePath is null")
        }
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

    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        //DocumentsContract defines the contract between a documents provider and the platform.
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantedResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when (requestCode) {
            1 ->
                if (grantedResults.isNotEmpty() && grantedResults.get(0) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    openGallery()
                } else {
                    show("Unfortunately You are Denied Permission to Perform this Operation.")
                }
        }
    }

    private fun show(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(
                        requireActivity().getContentResolver().openInputStream(mUri!!)
                    )
                    img_choose_dummy?.let {
                        if (bitmap!=null){
                            it.visibility=View.GONE
                        }
                        else{
                            it.visibility=View.VISIBLE
                        }
                    }

                    imageProfile!!.setImageBitmap(bitmap)

                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitkat(data)
                    }
                }
        }
    }

    fun Register() {
        (activity as OnBoardingActivity?)?.userRegister?.registration?.observe(
            requireActivity(),
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {

                                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(AppConstants.IS_AUTHENTICATED, true)
                                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(AppConstants.USER_ID, res.user.id)
                                startActivity(Intent(context, Home::class.java))
                                (activity as OnBoardingActivity?)?.finish()
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG)
                                    .show()
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
}
package com.SigmaDating.apk.views.intro_registration

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Intent
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
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.example.demoapp.other.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.SigmaDating.R
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.views.Home
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.doAsync
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Profile_Photo : Fragment() {

    //Our constants
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    private val REQUEST_PERMISSION = 100
    private var param1: String? = null
    private var param2: String? = null
    lateinit var profile_continue: Button
    private var imageProfile: CircleImageView? = null
    private var mUri: Uri? = null
    lateinit var img_choose_dummy: ImageView
    lateinit var tc_check: CheckBox
    lateinit var constraint_f1: ConstraintLayout
    lateinit var bitmap_string: String
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
        img_choose_dummy = view.findViewById(R.id.img_choose_dummy)
        tc_check = view.findViewById(R.id.tc_check)
        constraint_f1 = view.findViewById(R.id.constraint_f1)
        imageProfile = view.findViewById(R.id.img_profile)
        bitmap_string = ""
        profile_continue.setOnClickListener {
            if (AppUtils.isNetworkInterfaceAvailable(requireContext())) {

                if (tc_check.isChecked) {
                    if (bitmap_string.equals("")) {
                        AppUtils.showErrorSnackBar(
                            requireContext(),
                            constraint_f1,
                            "Please Select Profile Photo "
                        )
                    } else {
                        (activity as OnBoardingActivity?)?.userRegister?.Register(bitmap_string)
                    }

                } else {
                    AppUtils.showErrorSnackBar(
                        requireContext(),
                        constraint_f1,
                        "Please check the box to continue. "
                    )
                }

            }

        }

        img_choose_dummy.setOnClickListener {

            Popup()
        }

        Register()
        return view;
    }


    private fun Popup() {
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

    override fun onResume() {
        super.onResume()


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
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            activity?.packageManager?.let {
                intent.resolveActivity(it)?.also {
                    startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
                }
            }
        }
    }

    private fun renderImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            imageProfile?.setImageBitmap(bitmap)
            val drawable: BitmapDrawable = imageProfile?.getDrawable() as BitmapDrawable
            var bitmapl: Bitmap = drawable.getBitmap()
            bitmapl = Bitmap.createScaledBitmap(bitmapl, 350, 350, true);
            convertBitmapToBase64(bitmapl)

        } else {
            show("ImagePath is null")
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
            encoded = "data:image/png;base64,"+Base64.encodeToString(b, Base64.DEFAULT)
            bitmap_string=encoded
            Log.d("TAG@123", "  images  --------- " + encoded)
            progressDialog.dismiss()
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

                    imageProfile!!.setImageBitmap(bitmap)
                    val drawable: BitmapDrawable = imageProfile?.getDrawable() as BitmapDrawable
                    var bitmapl: Bitmap = drawable.getBitmap()
                    bitmapl = Bitmap.createScaledBitmap(bitmapl, 460, 460, true);
                    convertBitmapToBase64(bitmapl)


                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {

                    if (data!=null) {
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                activity!!.contentResolver,
                                data.data
                            )
                            imageProfile?.setImageBitmap(bitmap)
                           Bitmap.createScaledBitmap(bitmap, 350, 350, true);
                            convertBitmapToBase64(bitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                       /* if (Build.VERSION.SDK_INT >= 19) {
                            handleImageOnKitkat(data)
                        }*/

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

                                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                                    AppConstants.IS_AUTHENTICATED,
                                    true
                                )
                                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                                    AppConstants.USER_ID,
                                    res.user.id
                                )
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
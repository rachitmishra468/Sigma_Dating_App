package com.SigmaDating.app.views.intro_registration

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.SigmaDating.R
import com.SigmaDating.app.other.LocationService
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.utilities.URIPathHelper
import com.SigmaDating.app.views.Home
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.demoapp.other.Constants
import com.example.demoapp.other.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.doAsync
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Profile_Photo : Fragment() {
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

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
    private var text_termncon:TextView?=null
    lateinit var constraint_f1: ConstraintLayout
    lateinit var bitmap_string: String
    lateinit var show_disclamer:LinearLayout
    lateinit var webView :WebView
    lateinit var logout:Button
    lateinit var cancle:Button
    lateinit var user:String


    var currentPhotoPath: String? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

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
        text_termncon=view.findViewById(R.id.text_termncon)
        constraint_f1 = view.findViewById(R.id.constraint_f1)
        imageProfile = view.findViewById(R.id.img_profile)
        bitmap_string = ""
        profile_continue.setOnClickListener {
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

        img_choose_dummy.setOnClickListener {
            checkGallerypermission()
        }

        imageProfile?.setOnClickListener {
            checkGallerypermission()
        }

         show_disclamer = view.findViewById<LinearLayout>(R.id.show_disclamer)
         webView = view.findViewById<WebView>(R.id.webView_diclamer)
         logout = view.findViewById<Button>(R.id.logout)
         cancle = view.findViewById<Button>(R.id.cancel)
        show_disclamer.visibility = View.GONE
        logout.setOnClickListener {
            (activity as Home).sharedPreferencesStorage.setValue(
                AppConstants.Disclaimer,
                true
            )

            (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                AppConstants.IS_AUTHENTICATED, true)
            (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                AppConstants.USER_ID, user)
            (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                AppConstants.FLOW_TYPE, "Sign_UP")
            startActivity(Intent(context, Home::class.java))
            do_sent_firebaselog("Sign Up",user)
            (activity as OnBoardingActivity?)?.finish()


        }


        cancle.setOnClickListener {
             Toast.makeText(requireContext(), "You cannot continue until you agree to terms", Toast.LENGTH_LONG)
                                    .show()

        }
        Register()
        LocationService.get_location(requireActivity())
        return view;
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
                Popup()
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
                Popup()
            }
        }
    }



     fun Popup() {
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
        text_termncon?.let { AppUtils.customTextView(it,requireContext()) }

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
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    requireContext(), "com.SigmaDating.app.provider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, OPERATION_CAPTURE_PHOTO)
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
        progressDialog.setMessage("Please wait ...")
        progressDialog.show()
        doAsync {
            var encoded = ""
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.PNG, 50, baos)
            val b = baos.toByteArray()
            encoded = "data:image/png;base64,"+Base64.encodeToString(b, Base64.DEFAULT)
            bitmap_string=encoded
            Log.d("TAG@123", "  images  --------- " + encoded)
            progressDialog.dismiss()
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
                    mUri= Uri.fromFile(File(currentPhotoPath))

                    Glide.with(this)
                        .asBitmap()
                        .load(mUri)
                        .into(object : CustomTarget<Bitmap>(300,300){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                imageProfile?.setImageBitmap(resource)
                                Bitmap.createScaledBitmap(resource, 250, 250, true);
                                convertBitmapToBase64(resource)
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })

                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (data!=null) {
                        try {

                            Glide.with(this)
                                .asBitmap()
                                .load(File(URIPathHelper().getPath(requireContext(), data!!.data!!)))
                                .into(object : CustomTarget<Bitmap>(300,300){
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        imageProfile?.setImageBitmap(resource)
                                        Bitmap.createScaledBitmap(resource, 100, 100, true);
                                        convertBitmapToBase64(resource)
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {

                                    }
                                })
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
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

                               /* AppUtils.showLoader(requireContext())
                                webView.webViewClient = WebViewClient()
                                show_disclamer.visibility = View.VISIBLE
                                webView.loadUrl(Constants.disclaimer)*/
                                user=res.user.id

                                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                                    AppConstants.IS_AUTHENTICATED, true)
                                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                                    AppConstants.USER_ID, user)
                                (activity as OnBoardingActivity?)?.sharedPreferencesStorage?.setValue(
                                    AppConstants.FLOW_TYPE, "Sign_UP")
                                startActivity(Intent(context, Home::class.java))
                                do_sent_firebaselog("Sign Up",user)
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



    private fun do_sent_firebaselog(event_name: String, event_log: String) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(
                event_name,
                event_log
            )
        }
    }


}
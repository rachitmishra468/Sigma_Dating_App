package com.SigmaDating.apk.views.profile

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.SigmaDating.R
import com.SigmaDating.apk.AppReseources
import com.SigmaDating.apk.adapters.UserReportInterestAdapter
import com.SigmaDating.apk.model.Loginmodel
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.views.Home
import com.SigmaDating.databinding.FragmentReportUserBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.JsonObject
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var edit_profile: CircleImageView
    private var userID: String? = null
    lateinit var interestsList: ArrayList<String>
    private var _binding: FragmentReportUserBinding? = null
    private lateinit var photoAdapter: UserReportInterestAdapter
    private val binding get() = _binding!!
    lateinit var rootContainer: ChipGroup
    lateinit var chatIcon: ImageView
    private var name_text: TextView? = null
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    lateinit var tv_report_to_user: Button
    lateinit var tv_block: Button

    lateinit var tvCounter: TextView
    private val args: ReportUserFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(
                android.R.transition.move
            )
        }

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

        _binding = FragmentReportUserBinding.inflate(inflater, container, false)
        edit_profile = _binding!!.root.findViewById(R.id.edit_profile)
        rootContainer = _binding?.root?.findViewById(R.id.rootContainer)!!
        tvCounter = _binding!!.root.findViewById(R.id.tvCounter)
        tv_report_to_user = _binding!!.root.findViewById(R.id.tv_report_to_user)
        tv_block = _binding!!.root.findViewById(R.id.tv_block)
        //_binding.
        footer_transition()

        edit_profile.setOnClickListener {
            (activity as Home).onBackPressed()
        }
        _binding!!.cancelReportfb.setOnClickListener {
            (activity as Home).onBackPressed()
        }
        _binding!!.superLikeRf.setOnClickListener {
            (activity as Home).onBackPressed()
        }
        _binding!!.starRf.setOnClickListener {
            (activity as Home).onBackPressed()
        }

        Home.notifications_count.let {
            tvCounter.setText(Home.notifications_count)
        }

        userID = getArguments()?.getString("user_id")
        _binding!!.grideReportFg.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user_id", userID)
            findNavController().navigate(
                R.id.action_FirstFragment_to_SecondFragment,
                bundle,
                null,
                null
            )
        }

        userID = getArguments()?.getString("user_id")
        if (userID == null) {
            userID = (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        }
        if (userID != null) {
            (activity as Home).homeviewmodel.get_secound_feb_User_details(
                userID!!
            )
        }

        tv_report_to_user.setOnClickListener {

            Update_password(userID!!)

        }
        tv_block.setOnClickListener {
            (activity as Home).homeviewmodel.report_block_user= MutableLiveData<Resource<Loginmodel>>()
            subscribe_report_block_user()
            val jsonObject = JsonObject()
            Log.d(
                "TAG@123",
                (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
            )
            jsonObject.addProperty(
                "user_id",
                (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
            )
            jsonObject.addProperty("profile_id", userID)
            Log.d(
                "TAG@123",
                jsonObject.toString()
            )
            (activity as Home).homeviewmodel.block_user(jsonObject)
        }


        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            subscribe_User_details()


        }
        return _binding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hotelIv = args.userImage

        _binding!!.idImgView.apply {
            transitionName = hotelIv
            Glide.with(this)
                .load(hotelIv)
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(this)
        }

    }


    fun subscribe_User_details() {
        (activity as Home?)?.homeviewmodel?.get_secound_feb_data?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data.let { res ->
                            if (res?.status == true) {
                                Log.d("TAG@123", "USER_REPORT " + it.data?.user.toString())
                                _binding?.let {
                                    it.TextName.setText(res.user.first_name + " " + res.user.last_name)
                                    it.textAge.setText(res.user.location)
                                    it.tvUniversity.setText(res.user.university)
                                    it.tvLocation.setText(res.user.location)
                                    it.tvDescription.setText(res.user.about)
                                    it.universityText.setText(res.user.university)
                                    if(res.user.greekletter.length>0) {
                                        it.reportGreek.text = res.user.greekletter
                                        it.reportGreek.visibility=View.VISIBLE
                                    }
                                    else{
                                        it.reportGreek.visibility=View.GONE
                                    }


                                    it.textAge.setText("" + res?.user.age)

                                }
                                res.user.apply { }
                                it.data?.user?.upload_image?.let {
                                    Glide.with(AppReseources.getAppContext()!!).load(it)
                                        .error(R.drawable.profile_img)
                                        .into(_binding!!.idImgView);
                                }

                                interestsList = ArrayList<String>()
                                //  interestsList = res.user.interests.split(",") as ArrayList<String>
                                val interest = res.user.interests.split(",").toTypedArray()
                                val dd = interest.toList()

                                setupChipGroupDynamically(dd)
                            } else {
                                Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                    Status.LOADING -> {


                        Log.d("TAG@123", "LOADING is null")
                    }
                    Status.ERROR -> {

                    }
                }
            })
    }


    fun subscribe_report_block_user() {
        (activity as Home?)?.homeviewmodel?.report_block_user?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                                Toast.makeText(
                                    requireContext(),
                                    "" + res.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                (activity as Home).onBackPressed()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "" + res!!.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    Status.LOADING -> {
                        AppUtils.showLoader(requireContext())
                        Log.d("TAG@123", "LOADING is null")
                    }
                    Status.ERROR -> {
                        AppUtils.hideLoader()
                    }
                }
            })
    }

    private fun setupChipGroupDynamically(list: List<String>) {
        try {
            rootContainer.removeAllViews()
            for (i in list.indices) {
                rootContainer.addView(createChip(list.get(i), i))
            }
        } catch (e: Exception) {
            System.out.println(e.message)
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
        chip.isChipIconVisible = false
        chip.isCheckable = false
        chip.isClickable = true
        return chip

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun footer_transition() {
        chatIcon = binding.root.findViewById(R.id.chat_Icon)
        match_list = binding.root.findViewById(R.id.match_list)
        sigma_list = binding.root.findViewById(R.id.sigma_list)

        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_disable))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_disable))
        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_disable))

        chatIcon.setOnClickListener {
            AppUtils.animateImageview(chatIcon)
            findNavController().navigate(R.id.action_reportUserFragment_to_userChatFragment)

        }
        match_list.setOnClickListener {
            AppUtils.animateImageview(match_list)
            findNavController().navigate(R.id.action_reportUserFragment_to_SecondFragment)
        }
        sigma_list.setOnClickListener {
            AppUtils.animateImageview(sigma_list)
            findNavController().navigate(R.id.action_reportUserFragment_to_FirstFragment)
        }

    }


    fun Update_password(profile: String) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.report_user_popup, null)
        var editText_password = view.findViewById<EditText>(R.id.editText_password)
        var btnClose = view.findViewById<Button>(R.id.create_password)

        btnClose.setOnClickListener {
            if (editText_password.text.isEmpty()) {
                editText_password.error = "Enter Your Reason.."
            } else {
                (activity as Home).homeviewmodel.report_block_user= MutableLiveData<Resource<Loginmodel>>()
                subscribe_report_block_user()
                val jsonObject = JsonObject()
                Log.d(
                    "TAG@123",
                    (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                )
                jsonObject.addProperty(
                    "user_id",
                    (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                )
                jsonObject.addProperty("profile_id", profile)
                jsonObject.addProperty("reason", editText_password.text.toString())
                Log.d(
                    "TAG@123",
                    jsonObject.toString()
                )
                (activity as Home).homeviewmodel.report_user(jsonObject)
                dialog.dismiss()
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


}
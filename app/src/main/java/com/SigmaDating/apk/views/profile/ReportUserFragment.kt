package com.SigmaDating.apk.views.profile

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.SigmaDating.R
import com.SigmaDating.apk.AppReseources
import com.SigmaDating.apk.adapters.UserReportInterestAdapter
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.views.Home
import com.SigmaDating.databinding.FragmentReportUserBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.demoapp.other.Status
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
    lateinit var tvCounter:TextView
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
        tvCounter= _binding!!.root.findViewById(R.id.tvCounter)
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
            bundle.putString("user_id",userID)
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
                                    it.TextName.setText(res?.user.first_name + res?.user.last_name)
                                    it.textAge.setText(res?.user.location)
                                    it.tvUniversity.setText(res?.user.university)
                                    it.tvLocation.setText(res?.user.location)
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

       /* chip.isChecked = interestsList.contains(label)
        chip.setBackgroundColor(
            if (interestsList.contains(label)) {
                ContextCompat.getColor(AppReseources.getAppContext()!!, R.color.light_blue_900)
            } else {
                ContextCompat.getColor(AppReseources.getAppContext()!!, R.color.teal_200)
            }
        )
        chip.chipCornerRadius = 1.0F*/

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

        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_solid))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_disable))
        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_disable))

        chatIcon.setOnClickListener {
            findNavController().navigate(R.id.action_reportUserFragment_to_userChatFragment)
        }

        match_list.setOnClickListener {
             findNavController().navigate(R.id.action_reportUserFragment_to_SecondFragment)
        }
        sigma_list.setOnClickListener {
            findNavController().navigate(R.id.action_reportUserFragment_to_FirstFragment)
        }

    }

}
package com.SigmaDating.app.views

import android.app.Dialog
import android.provider.Settings
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.SigmaDating.app.adapters.ProfileMatch
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.SigmaDating.R
import com.SigmaDating.app.model.Bids
import com.SigmaDating.app.model.Loginmodel
import com.SigmaDating.app.model.Pages
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.CardManager.CardViewChanger
import com.SigmaDating.app.views.Home.Companion.notifications_count
import com.SigmaDating.app.views.Home.Companion.pages
import com.SigmaDating.databinding.FragmentFirstBinding
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList


class FirstFragment : Fragment(), ProfileMatch.OnCategoryClickListener {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private var courseModalArrayList: ArrayList<Bids>? = null
    var broken_heart: LottieAnimationView? = null
    var heart_loading: LottieAnimationView? = null
    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    lateinit var editProfile: CircleImageView
    lateinit var notificationIcon: ConstraintLayout
    lateinit var notification: ConstraintLayout
    var cardViewChanger: CardViewChanger? = null
    private var idUserConnected = ""
    var userId: String? = null
    lateinit var adapter: ProfileMatch
    lateinit var credentials_card: ConstraintLayout
    lateinit var tvCounter: TextView
    lateinit var empty_text_view: TextView
    lateinit var empty_item_layout: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG@123", "FirstFragment onCreate")
        if (!(activity as Home).sharedPreferencesStorage.getBoolean(AppConstants.Disclaimer)) (
                Disclaimer()
                )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG@123", "FirstFragment onCreateView")
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        editProfile = binding.root.findViewById(R.id.edit_profile)
        notificationIcon = binding.root.findViewById(R.id.notification)
        cardViewChanger = binding.root.findViewById(R.id.card_stack_view)
        credentials_card = binding.root.findViewById(R.id.credentials_card)
        tvCounter = binding.root.findViewById(R.id.tvCounter)
        empty_text_view = binding.root.findViewById(R.id.empty_text_view)
        empty_item_layout = binding.root.findViewById(R.id.empty_item_layout)
        empty_item_layout.visibility = View.GONE

        editProfile.setOnClickListener {
            val bundle = Bundle()
            userId = (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
            bundle.putString("user_id", userId)
            findNavController().navigate(
                R.id.action_FirstFragment_to_SecondFragment,
                bundle,
                null,
                null
            )
        }
        notificationIcon.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_all_activity)
        }

        (activity as Home).homeviewmodel.get_home_feb_data(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )
        footer_transition()
        subscribe_Login_User_details()
        subscribe_bids()
        return binding.root

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG@123", "FirstFragment onViewCreated")
        cardViewChanger!!.setFlingListener(object : CardViewChanger.OnCardFlingListener {
            override fun onCardExitLeft(dataObject: Any) {
                Log.d("TAG@123", "onCardExitLeft")
                if (dataObject is Bids) {
                    idUserConnected = (dataObject as Bids).id
                    Log.d("TAG@123", "idUserConnected " + idUserConnected)
                    swipe_update(idUserConnected, "dislike")
                    Toast.makeText(requireContext(), "Nah", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCardExitRight(dataObject: Any) {
                Log.d("TAG@123", "onCardExitRight")
                if (dataObject is Bids) {
                    idUserConnected = (dataObject as Bids).id
                    Log.d("TAG@123", "idUserConnected " + idUserConnected)
                    swipe_update(idUserConnected, "like")
                    Toast.makeText(requireContext(), "Like", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                Log.d("TAG@123", "onAdapterAboutToEmpty")
                empty_text_view.text = "No matching bids found."
                empty_item_layout.visibility = View.VISIBLE
            }

            override fun onScroll(v: Float) {
                Log.d("TAG@123", "onScroll")
            }

            override fun onCardExitTop(dataObject: Any) {
                Log.d("TAG@123", "onCardExitTop")

                if (dataObject is Bids) {
                    idUserConnected = (dataObject as Bids).id
                    Log.d("TAG@123", "idUserConnected " + idUserConnected)
                    swipe_update(idUserConnected, "superlike")
                    Toast.makeText(requireContext(), "Super Like", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCardExitBottom(dataObject: Any?) {
                Log.d("TAG@123", "onCardExitBottom")

            }
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        Log.d("TAG@123", "on destroy called")
    }


    fun footer_transition() {
        chatIcon = binding.root.findViewById(R.id.chat_Icon)
        match_list = binding.root.findViewById(R.id.match_list)
        sigma_list = binding.root.findViewById(R.id.sigma_list)
        //For Home sigma_list is Enable

        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_enable))
        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_disable))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_disable))


        chatIcon.setOnClickListener {

            findNavController().navigate(R.id.action_FirstFragment_to_chat)
        }

        match_list.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        sigma_list.setOnClickListener {
            AppUtils.animateImageview(sigma_list)
        }
        (activity as Home).clearBackStack()
    }


    override fun onCategoryClick(
        position: Bids?,
        count: Int,
        extras: FragmentNavigator.Extras?,
        imageView: ImageView
    ) {

        when (count) {
            1 -> {
                extras?.let {
                    val bundle = Bundle()
                    bundle.putString("user_id", position?.id)
                    findNavController().navigate(
                        R.id.action_FirstFragment_to_reportUserFragment,
                        bundle,
                        null,
                        extras
                    )
                } ?: run {
                    val bundle = Bundle()
                    bundle.putString("user_id", position?.id)
                    findNavController().navigate(
                        R.id.action_FirstFragment_to_reportUserFragment,
                        bundle,
                        null,
                        null
                    )

                }
            }
            2 -> cardViewChanger?.throwRight()
            3 -> cardViewChanger?.throwTop()
            4 -> {
                extras?.let {
                    val bundle = Bundle()
                    bundle.putString("user_id", position?.id)
                    findNavController().navigate(
                        R.id.action_FirstFragment_to_SecondFragment,
                        bundle,
                        null,
                        extras
                    )
                } ?: run {
                    val bundle = Bundle()
                    bundle.putString("user_id", position?.id)
                    findNavController().navigate(
                        R.id.action_FirstFragment_to_SecondFragment,
                        bundle,
                        null,
                        null
                    )

                }
            }
            5 -> cardViewChanger?.throwLeft()
            6 -> findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }


    }

    private fun Disclaimer() {
        val dialog = Dialog(requireContext(), R.style.AppBaseTheme)
        dialog.setContentView(R.layout.full_screen_dialog)
        val logout = dialog.findViewById<Button>(R.id.logout)
        val cancle = dialog.findViewById<Button>(R.id.cancel)
        logout.setOnClickListener {
            (activity as Home).sharedPreferencesStorage.setValue(
                AppConstants.Disclaimer,
                true
            )
            dialog.dismiss()
        }
        cancle.setOnClickListener {
            (activity as Home).sharedPreferencesStorage.setValue(
                AppConstants.Disclaimer,
                false
            )
            (activity as Home).onBackPressed()
        }
        dialog.show()
    }

    fun subscribe_swipe() {
        (activity as Home?)?.homeviewmodel?.profile_swipe?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Log.d("TAG@123",  ""+ it.data?.message)
                            } catch (e: Exception) {
                                Log.d("TAG@123", "Exception ::" + e.message.toString())
                            }
                        } else {

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


    fun subscribe_bids() {
        (activity as Home?)?.homeviewmodel?.user_bids?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let { res ->
                        if (res?.status == true) {
                            try {
                                Log.d(
                                    "TAG@123",
                                    "notifications_count  :" + it.data?.notifications_count.toString()
                                )

                                Log.d("TAG@123", "notifications_count  :" + it.data.toString())
                                courseModalArrayList = it.data?.bids as ArrayList<Bids>
                                pages = it.data.pages as ArrayList<Pages>
                                notifications_count = it.data.notifications_count
                                notifications_count.let {
                                    tvCounter.setText(notifications_count)
                                }

                                if (courseModalArrayList!!.size==0) {
                                    empty_text_view.text = it.data.message
                                    empty_item_layout.visibility = View.VISIBLE
                                    Log.d("TAG@123", " empty_text  Show")
                                }
                                adapter = ProfileMatch(courseModalArrayList!!, requireActivity(), this)
                                cardViewChanger?.setAdapter(adapter)
                                adapter.notifyDataSetChanged()

                            } catch (e: Exception) {
                                Log.d("TAG@123", "Exception  :" + e.message.toString())
                            }
                        } else {


                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_SHORT)
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


    fun subscribe_Login_User_details() {
        (activity as Home?)?.homeviewmodel?.get_user_data?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {

                            try {
                                Log.d("TAG@123", it.data?.user.toString())
                                (activity as Home).sharedPreferencesStorage.setValue(
                                    AppConstants.upload_image,
                                    it.data?.user?.upload_image
                                )
                                Glide.with(requireContext()).load(it.data?.user?.upload_image)
                                    .error(R.drawable.profile_img)
                                    .into(editProfile);

                                Home.current_user_profile = it.data?.user?.upload_image.toString()

                                if (it.data?.user?.upload_image?.length == 0 || it.data?.user?.upload_image == null) {
                                    Glide.with(requireContext()).load(
                                        (activity as Home?)?.sharedPreferencesStorage!!.getString(
                                            AppConstants.upload_image
                                        )
                                    )
                                        .error(R.drawable.profile_img)
                                        .into(editProfile);

                                } else {

                                }


                            } catch (e: Exception) {
                                Log.d("TAG@123", "Exception  :-" + e.message.toString())
                            }

                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_SHORT)
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


    fun swipe_update(id: String, key: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "user_id",
            (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
        )
        jsonObject.addProperty("profile_id", id)
        jsonObject.addProperty(key, "yes")
        Log.d("TAG@123", jsonObject.toString())
        (activity as Home).homeviewmodel.profile_swipe = MutableLiveData<Resource<Loginmodel>>()
        subscribe_swipe()
        (activity as Home).homeviewmodel.profile_swipe_details(jsonObject)
    }


}



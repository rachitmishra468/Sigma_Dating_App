package com.SigmaDating.apk.views

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.SigmaDating.apk.model.Profile
import android.widget.Toast

import com.SigmaDating.apk.adapters.ProfileMatch
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.SigmaDating.R
import com.SigmaDating.apk.model.Bids
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.views.CardManager.CardViewChanger
import com.SigmaDating.databinding.FragmentFirstBinding
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.demoapp.other.Status
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.ArrayList
import java.util.*


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
var userId:String?=null
    lateinit var adapter:ProfileMatch


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (!(activity as Home).sharedPreferencesStorage.getBoolean(AppConstants.Disclaimer)) (
                Disclaimer()
                )

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        editProfile = binding.root.findViewById(R.id.edit_profile)
        notificationIcon = binding.root.findViewById(R.id.notification)
        cardViewChanger = binding.root.findViewById(R.id.card_stack_view)
        editProfile.setOnClickListener {
            val extras = FragmentNavigatorExtras(editProfile to "large_image")
            val bundle = Bundle()
            userId= (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID)
            bundle.putString("user_id", userId)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment,bundle,null,extras)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardViewChanger!!.setFlingListener(object : CardViewChanger.OnCardFlingListener {
            override fun onCardExitLeft(dataObject: Any) {
                Log.d("TAG@123", "onCardExitLeft")

            }

            override fun onCardExitRight(dataObject: Any) {
                Log.d("TAG@123", "onCardExitRight")

            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {

            }

            override fun onScroll(v: Float) {
                Log.d("TAG@123", "onScroll")
            }

            override fun onCardExitTop(dataObject: Any) {
                Log.d("TAG@123", "onCardExitTop")

            }

            override fun onCardExitBottom(dataObject: Any?) {
                Log.d("TAG@123", "onCardExitBottom")

            }
        })


       // cardViewChanger.se


    }


    fun play_animation() {
        Handler().postDelayed(
            {

                binding.heartLoading.visibility = View.GONE
                binding.brokenHeart.visibility = View.GONE

            },
            1500
        )
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        (activity as Home).clearBackStack()
    }


    override fun onCategoryClick(position: Bids?, count: Int) {

        when (count) {
            1 -> findNavController().navigate(R.id.action_FirstFragment_to_reportUserFragment)
            2 -> cardViewChanger?.throwRight()
            3 -> cardViewChanger?.throwTop()
            4 -> findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
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

    fun subscribe_bids(){
        (activity as Home?)?.homeviewmodel?.user_bids?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let { res ->

                        if (res?.status == true) {
                            try {
                                Log.d("TAG@123","notifications_count  :"+ it.data?.notifications_count.toString())
                                courseModalArrayList=it.data?.bids as ArrayList<Bids>
                                adapter = ProfileMatch(courseModalArrayList!!, requireActivity(), this)
                                cardViewChanger?.setAdapter(adapter)
                                adapter.notifyDataSetChanged()

                            } catch (e: Exception) {
                                Log.d("TAG@123", "Exception" + e.message.toString())
                            }
                        }else{

                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
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
                    it.data.let { res ->
                        if (res?.status == true) {

                            try {
                                Log.d("TAG@123", it.data?.user.toString())

                                Glide.with(requireContext()).load(it.data?.user?.upload_image)
                                    .error(R.drawable.profile_img)
                                    .into(editProfile);



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
                                Log.d("TAG@123", "Exception" + e.message.toString())
                            }

                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
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


}



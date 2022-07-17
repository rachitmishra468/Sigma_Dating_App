package com.SigmaDating.apk.views

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.SigmaDating.apk.model.Profile
import android.widget.Toast

import android.util.Log
import com.daprlabs.cardstack.SwipeDeck.SwipeEventCallback
import com.SigmaDating.apk.adapters.ProfileMatch
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.demoapp.other.Status
import com.SigmaDating.R
import com.SigmaDating.databinding.FragmentFirstBinding
import com.SigmaDating.apk.storage.AppConstants
import de.hdodenhof.circleimageview.CircleImageView


class FirstFragment : Fragment(), ProfileMatch.OnCategoryClickListener {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private var courseModalArrayList: ArrayList<Profile>? = null
    var broken_heart: LottieAnimationView? = null
    var heart_loading: LottieAnimationView? = null
    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    lateinit var editProfile: CircleImageView
    lateinit var notificationIcon: ConstraintLayout
    lateinit var notification:ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as Home).homeviewmodel.get_Login_User_details(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )
        if (!(activity as Home).sharedPreferencesStorage.getBoolean(AppConstants.Disclaimer))(
                Disclaimer()
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        editProfile = binding.root.findViewById(R.id.edit_profile)
        notificationIcon = binding.root.findViewById(R.id.notification)
        editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        notificationIcon.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_all_activity)
        }
        footer_transition()
        subscribe_Login_User_details()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseModalArrayList = ArrayList()
        for (i in 1..10) {
            courseModalArrayList!!.add(Profile("C++", R.drawable.dummy_imf))
        }


        val adapter = ProfileMatch(courseModalArrayList!!, requireActivity(), this)
        binding.swipeDeck.setAdapter(adapter)
        binding.swipeDeck.setEventCallback(object : SwipeEventCallback {
            override fun cardSwipedLeft(position: Int) {
                //  Toast.makeText(context, "Card Swiped Left", Toast.LENGTH_SHORT).show()
            }

            override fun cardSwipedRight(position: Int) {
                // Toast.makeText(context, "Card Swiped Right", Toast.LENGTH_SHORT).show()
            }

            override fun cardsDepleted() {
                // this method is called when no card is present
                binding.brokenHeart.playAnimation()
                play_animation()
                //  Toast.makeText(context, "No more courses present", Toast.LENGTH_SHORT).show()
            }

            override fun cardActionDown() {
                // this method is called when card is swiped down.
                Log.i("TAG", "CARDS MOVED DOWN")
            }

            override fun cardActionUp() {
                // this method is called when card is moved up.
                Log.i("TAG", "CARDS MOVED UP")
            }
        })


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


    override fun onCategoryClick(position: Profile?,count:Int) {

        when(count){
            1->findNavController().navigate(R.id.action_FirstFragment_to_reportUserFragment)
            2->binding.swipeDeck.swipeTopCardRight(150)
            3->binding.swipeDeck.swipeTopCardRight(150)
            4->findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            5->binding.swipeDeck.swipeTopCardLeft(150)
            6->findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)


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


    fun subscribe_Login_User_details() {
        (activity as Home?)?.homeviewmodel?.get_user_data?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let { res ->
                        if (res?.status == true) {

                            try {
                                Log.d("TAG@123", it.data?.user.toString())

                                    Glide.with(requireContext()).load(it.data?.user?.upload_image)
                                        .error(R.drawable.profile_img)
                                        .into(editProfile);



                                if(it.data?.user?.upload_image?.length==0||it.data?.user?.upload_image==null){
                                    Glide.with(requireContext()).load( (activity as Home?)?.sharedPreferencesStorage!!.getString(AppConstants.upload_image))
                                        .error(R.drawable.profile_img)
                                        .into(editProfile);

                                }else{

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
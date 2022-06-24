package com.sigmadatingapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sigmadatingapp.module.Profile
import android.widget.Toast

import android.util.Log
import com.daprlabs.cardstack.SwipeDeck.SwipeEventCallback
import com.sigmadatingapp.adapters.ProfileMatch
import android.os.Handler
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.sigmadatingapp.R
import com.sigmadatingapp.databinding.FragmentFirstBinding
import de.hdodenhof.circleimageview.CircleImageView


class FirstFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        editProfile = binding.root.findViewById(R.id.edit_profile)
        notificationIcon = binding.root.findViewById(R.id.notification)
        editProfile.setOnClickListener {
            // findNavController().navigate(R.id.action_FirstFragment_to_edit_profile)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        notificationIcon.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_notification)
        }
        footer_transition()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseModalArrayList = ArrayList()
        for (i in 1..10) {
            courseModalArrayList!!.add(Profile("C++", com.sigmadatingapp.R.drawable.dummy_imf))
        }


        val adapter = ProfileMatch(courseModalArrayList, context)
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


}
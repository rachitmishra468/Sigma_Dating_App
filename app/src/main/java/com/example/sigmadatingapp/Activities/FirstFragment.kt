package com.example.sigmadatingapp.Activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.daprlabs.cardstack.SwipeDeck
import com.example.sigmadatingapp.module.Profile
import android.widget.Toast

import android.R
import android.util.Log
import com.daprlabs.cardstack.SwipeDeck.SwipeEventCallback
import com.example.sigmadatingapp.Adapters.ProfileMatch
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Intent
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.example.sigmadatingapp.databinding.FragmentFirstBinding
import com.example.sigmadatingapp.storage.AppConstants


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private var courseModalArrayList: ArrayList<Profile>? = null


    var broken_heart: LottieAnimationView? = null
    var heart_loading: LottieAnimationView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        courseModalArrayList = ArrayList()



        for (i in 1..10) {
            courseModalArrayList!!.add(
                Profile(
                    "C++",
                    com.example.sigmadatingapp.R.drawable.dummy_imf
                )
            )
        }


        // on below line we are creating a variable for our adapter class and passing array list to it.

        // on below line we are creating a variable for our adapter class and passing array list to it.
        val adapter = ProfileMatch(courseModalArrayList, context)

        // on below line we are setting adapter to our card stack.

        // on below line we are setting adapter to our card stack.
        binding.swipeDeck!!.setAdapter(adapter)

        // on below line we are setting event callback to our card stack.

        // on below line we are setting event callback to our card stack.
        binding.swipeDeck!!.setEventCallback(object : SwipeEventCallback {
            override fun cardSwipedLeft(position: Int) {
                binding.brokenHeart.visibility=View.VISIBLE
                binding.brokenHeart.playAnimation()
                check_login_flag()
                Toast.makeText(context, "Card Swiped Left", Toast.LENGTH_SHORT).show()
            }

            override fun cardSwipedRight(position: Int) {
                // on card swiped to right we are displaying a toast message.

// Custom animation speed or duration.
                // Custom animation speed or duration.
                binding.heartLoading.visibility=View.VISIBLE
                binding.heartLoading.playAnimation()
                check_login_flag()
                Toast.makeText(context, "Card Swiped Right", Toast.LENGTH_SHORT).show()
            }

            override fun cardsDepleted() {
                // this method is called when no card is present
                binding.brokenHeart.playAnimation()
                check_login_flag()
                Toast.makeText(context, "No more courses present", Toast.LENGTH_SHORT)
                    .show()
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


        /*  binding.buttonFirst.setOnClickListener {
              findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
          }*/
    }



    fun check_login_flag() {
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
}
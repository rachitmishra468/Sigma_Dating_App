package com.example.sigmadatingapp.Activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.sigmadatingapp.databinding.FragmentFirstBinding
import com.daprlabs.cardstack.SwipeDeck
import com.example.sigmadatingapp.module.Profile
import android.widget.Toast

import android.R
import android.util.Log
import com.daprlabs.cardstack.SwipeDeck.SwipeEventCallback
import com.example.sigmadatingapp.Adapters.ProfileMatch


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private var courseModalArrayList: ArrayList<Profile>? = null

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



       for(i in 1..30){
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
                // on card swipe left we are displaying a toast message.
                Toast.makeText(context, "Card Swiped Left", Toast.LENGTH_SHORT).show()
            }

            override fun cardSwipedRight(position: Int) {
                // on card swiped to right we are displaying a toast message.
                Toast.makeText(context, "Card Swiped Right", Toast.LENGTH_SHORT).show()
            }

            override fun cardsDepleted() {
                // this method is called when no card is present
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
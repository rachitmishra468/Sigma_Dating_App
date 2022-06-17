package com.sigmadatingapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.sigmadatingapp.R
import com.sigmadatingapp.databinding.FragmentSecond2Binding
import com.sigmadatingapp.views.intro_registration.BlankFragment


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class Second2Fragment : Fragment() {

    private var _binding: FragmentSecond2Binding? = null
    lateinit var button_second: Button
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       // _binding = FragmentSecond2Binding.inflate(inflater, container, false)
        val root = inflater.inflate(R.layout.fragment_second2, container, false)
        button_second = root.findViewById(R.id.button_signup)
        button_second.setOnClickListener {
           // (activity as OnBoardingActivity?)?.setCurrentItem(1, true)
        }
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      /*  binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_Second2Fragment_to_First2Fragment)
        }*/
    }

   /* override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
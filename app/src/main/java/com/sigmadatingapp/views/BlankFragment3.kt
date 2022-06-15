package com.sigmadatingapp.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.sigmadatingapp.R
import com.sigmadatingapp.databinding.AboutBirthdayBinding
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sigmadatingapp.R
import com.example.sigmadatingapp.databinding.AboutBirthdayBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment3.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment3 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var button_birthday: Button
    private var ss:String?=null
    private var binding: AboutBirthdayBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // var root= inflater.inflate(R.layout.about_birthday, container, false)
        binding = AboutBirthdayBinding.inflate(inflater, container, false)
        button_birthday = binding!!.root.findViewById(R.id.button_birthday)
        button_birthday.setOnClickListener {

           // (activity as OnBoardingActivity?)?.setCurrentItem(1, true)
             startActivity(Intent(context, Home::class.java))
            (activity as OnBoardingActivity?)?.finish()
            // (activity as OnBoardingActivity?)?.setCurrentItem(1, true)
            startActivity(Intent(context, Home::class.java))
        }

       // binding!!.editTextBirthday.addTextChangedListener()


        return binding!!.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment3.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
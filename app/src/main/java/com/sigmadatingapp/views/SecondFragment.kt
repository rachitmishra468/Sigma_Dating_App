package com.sigmadatingapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sigmadatingapp.R
import com.sigmadatingapp.adapters.Profile_Adapter
import com.sigmadatingapp.databinding.FragmentSecondBinding

import com.sigmadatingapp.module.EditProfiledata

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

private var setting_icon:ImageView?=null
    private lateinit var  photoAdapter: Profile_Adapter
    private var dataList = mutableListOf<EditProfiledata>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.recyclerView?.layoutManager = GridLayoutManager(requireContext(),3)
        photoAdapter = Profile_Adapter(requireContext())
        _binding?.recyclerView?.adapter = photoAdapter

        //add data
        for (i in 1..10){
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/african-american-woman-talking-mobile-phone-black-people-50437769.jpg"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/beautiful-young-woman-maine-usa-close-up-portrait-native-108644385.jpg"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/beauty-black-skin-woman-african-ethnic-female-face-young-african-american-model-long-afro-hair-smiling-model-isolated-163819588.jpg"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/african-american-woman-praying-african-american-woman-praying-god-seeking-prayer-213590092.jpg"))



        }


        photoAdapter.setDataList(dataList)

        setting_icon?.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_settings)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
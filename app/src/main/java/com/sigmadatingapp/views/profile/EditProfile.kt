package com.sigmadatingapp.views.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.demoapp.other.Status
import com.sigmadatingapp.R
import com.sigmadatingapp.adapters.Edit_Profile_Adapter
import com.sigmadatingapp.adapters.Profile_Adapter
import com.sigmadatingapp.databinding.FragmentEditProfileBinding
import com.sigmadatingapp.model.EditProfiledata
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.utilities.AppUtils
import com.sigmadatingapp.views.Home

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditProfile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoAdapter: Edit_Profile_Adapter
    private var dataList = mutableListOf<EditProfiledata>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)


        //add data
        for (i in 1..2) {
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/african-american-woman-talking-mobile-phone-black-people-50437769.jpg"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/beautiful-young-woman-maine-usa-close-up-portrait-native-108644385.jpg"))
           // dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/beauty-black-skin-woman-african-ethnic-female-face-young-african-american-model-long-afro-hair-smiling-model-isolated-163819588.jpg"))
           // dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/african-american-woman-praying-african-american-woman-praying-god-seeking-prayer-213590092.jpg"))
        }
        _binding?.updateImageView?.layoutManager = GridLayoutManager(requireContext(), 3)
        photoAdapter = Edit_Profile_Adapter(requireContext())
        _binding?.updateImageView?.adapter = photoAdapter
        photoAdapter.setDataList(dataList)

        _binding?.imageView2?.setOnClickListener {
            (activity as Home).onBackPressed()
        }

        subscribe_Login_User_details()
        (activity as Home).homeviewmodel.get_Login_User_details(
            (activity as Home).sharedPreferencesStorage.getString(
                AppConstants.USER_ID
            )
        )
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    fun subscribe_Login_User_details() {
        (activity as Home?)?.homeviewmodel?.get_user_data?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    it.data.let { res ->
                        if (res?.status == true) {
                            Log.d("TAG@123", "1311" + res.toString())


                        } else {
                            Toast.makeText(requireContext(), res!!.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
                Status.LOADING -> {
                    AppUtils.showLoader(requireContext())
                }
                Status.ERROR -> {

                }
            }
        })


    }

}
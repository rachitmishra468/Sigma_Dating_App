package com.SigmaDating.app.views.membership

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.SigmaDating.R
import com.SigmaDating.app.model.Plans
import com.SigmaDating.app.model.SubscriptionPlanData
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.SigmaDating.databinding.FragmentSubscriptionPlanDiscriptionBinding
import com.bumptech.glide.Glide
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.gson.JsonObject
import javax.inject.Inject


class Subscription_Plan_Discription : Fragment() {

    private var _binding: FragmentSubscriptionPlanDiscriptionBinding? = null
    private val binding get() = _binding!!
    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    lateinit var plan_ID: String


    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSubscriptionPlanDiscriptionBinding.inflate(inflater, container, false)
        footer_transition()

        (context as Home).sharedPreferencesStorage.getString(
            AppConstants.upload_image
        ).let {
            Glide.with(requireActivity()).load(it).into(_binding!!.headerImg)

        }

        Home.notifications_count.let {
            _binding?.tvCounter?.setText(Home.notifications_count)
        }

        _binding!!.movetonotification.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_all_activity)
        }

        _binding!!.headerImg.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        arguments?.let {
            plan_ID = it.getString("id").toString()
            _binding!!.planNameDis.text = it.getString("plan_name")
            _binding!!.planePriceDis.text = "$ " + it.getString("price")
            _binding!!.planDiscription.text =
                HtmlCompat.fromHtml(it.getString("plan_description")!!, 0)
            background(Integer.parseInt(it.getString("colour")))

        }

        _binding!!.buyNow.setOnClickListener {
            (activity as Home).homeviewmodel.subscription_post =
                MutableLiveData<Resource<SubscriptionPlanData>>()
            val jsonObject = JsonObject()
            jsonObject.addProperty(
                "user_id",
                (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
            )
            jsonObject.addProperty(
                "plan_id",
                plan_ID
            )

            (activity as Home).homeviewmodel.postSubscriptionData(jsonObject)
            subscribe_create_post()

        }


        return binding.root
    }

    fun background(color: Int) {
        when (color) {
            0 -> {
                _binding!!.mainLayoutDis.setCardBackgroundColor(Color.parseColor("#34488D"))
               // _binding!!.buyNow.setBackgroundColor(Color.parseColor("#EA2391"))
                // color = 1

            }
            1 -> {
                _binding!!.mainLayoutDis.setCardBackgroundColor(Color.parseColor("#EA2391"))
              //  _binding!!.buyNow.setBackgroundColor(Color.parseColor("#34488D"))

            }
            2 -> {
                _binding!!.mainLayoutDis.setCardBackgroundColor(Color.parseColor("#6C4294"))
             //   _binding!!.buyNow.setBackgroundColor(Color.parseColor("#EA2391"))

            }
            else -> {
                _binding!!.mainLayoutDis.setCardBackgroundColor(Color.parseColor("#EA2391"))
            //    _binding!!.buyNow.setBackgroundColor(Color.parseColor("#34488D"))


            }
        }

    }


    fun footer_transition() {
        chatIcon = binding.root.findViewById(R.id.chat_Icon)
        match_list = binding.root.findViewById(R.id.match_list)
        sigma_list = binding.root.findViewById(R.id.sigma_list)


        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_disable))
        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_disable))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_disable))

        chatIcon.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_chat)
        }

        match_list.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        sigma_list.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }

    fun subscribe_create_post() {
        AppUtils.showLoader(requireContext())
        (activity as Home?)?.homeviewmodel?.subscription_post?.observe(
            viewLifecycleOwner,
            Observer { res ->
                when (res.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        Toast.makeText(requireContext(), res.data?.message.toString(), Toast.LENGTH_LONG).show()
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


}
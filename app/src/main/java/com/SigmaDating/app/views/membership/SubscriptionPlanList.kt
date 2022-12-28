package com.SigmaDating.app.views.membership

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.SigmaDating.R
import com.SigmaDating.app.adapters.ChatList_Adapter
import com.SigmaDating.app.adapters.SubscriptionAdapter
import com.SigmaDating.app.model.Match_bids
import com.SigmaDating.app.model.Plans
import com.SigmaDating.app.model.SubscriptionPlanData
import com.SigmaDating.app.model.User_bids_list
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.SigmaDating.databinding.FragmentSecondBinding
import com.SigmaDating.databinding.FragmentSubscriptionPlanListBinding
import com.bumptech.glide.Glide
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import javax.inject.Inject


class SubscriptionPlanList : Fragment(), SubscriptionAdapter.OnCategoryClickListener {
    private var _binding: FragmentSubscriptionPlanListBinding? = null
    private val binding get() = _binding!!
    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    private var dataList = mutableListOf<Plans>()
    private lateinit var mSubscriptionAdapter: SubscriptionAdapter

    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscriptionPlanListBinding.inflate(inflater, container, false)
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

        mSubscriptionAdapter = SubscriptionAdapter(requireContext(), this)
        (activity as Home).homeviewmodel.subscriptionPlans =
            MutableLiveData<Resource<SubscriptionPlanData>>()
        (activity as Home).homeviewmodel.getSubscriptionPlans()
        subscribe_create_post()

        return binding.root
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
        (activity as Home?)?.homeviewmodel?.subscriptionPlans?.observe(
            viewLifecycleOwner,
            Observer { res ->
                when (res.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        Log.d("TAG@123", "Status " + res.status)
                        Home.notifications_count = "0"

                        if (res.data!!.plans.isNullOrEmpty()) {

                        } else {
                            dataList = res.data.plans as ArrayList<Plans>
                            setAdapterListData(res.data.plans as ArrayList<Plans>)
                        }

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


    fun setAdapterListData(dataListuser: ArrayList<Plans>) {
        _binding?.subscriptionList?.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        _binding?.subscriptionList?.adapter = mSubscriptionAdapter
        mSubscriptionAdapter.setDataList(dataListuser)
        Log.d("TAG@123", " setAdapterListData  ${dataListuser.size}")
    }

    override fun onCategoryClick(position: Plans, flag: Int) {
        val bundle = Bundle()
        bundle.putString("id", position.id)
        bundle.putString("plan_name", position.plan_name)
        bundle.putString("plan_description", position.plan_description)
        bundle.putString("price",position.price)
        bundle.putString("colour", position.color)

        findNavController().navigate(R.id.action_planslIist_to_discription,bundle)
    }

}
package com.SigmaDating.app.views.all_activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.app.adapters.All_Activity_Adapter
import com.SigmaDating.app.model.*
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.SigmaDating.databinding.FragmentAllActivitysBinding
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.gson.JsonObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class All_Activitys : Fragment(), All_Activity_Adapter.OnCategoryClickListener {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adapter: All_Activity_Adapter
    private var All_list_recycler: RecyclerView? = null
    private lateinit var _binding: FragmentAllActivitysBinding
    private val binding get() = _binding!!
    private var userID: String? = null
    lateinit var empty_text_view: TextView
    lateinit var empty_item_layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllActivitysBinding.inflate(inflater, container, false)
        empty_text_view = binding.root.findViewById(R.id.empty_text_view)
        empty_item_layout = binding.root.findViewById(R.id.empty_item_layout)
        empty_item_layout.visibility = View.GONE

        _binding.finishNotification.setOnClickListener {
            (activity as Home).onBackPressed()
        }

        All_list_recycler = binding.root.findViewById(R.id.alllist_recyclerView)
        get_Notification_data()
        return binding.root;
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            All_Activitys().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    fun get_Notification_data() {
        (activity as Home).homeviewmodel.notification_list =
            MutableLiveData<Resource<Notification_model>>()
        subscribe_notification_list()
        userID = (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
        Log.d("TAG@123", userID + "")
        (activity as Home).homeviewmodel.get_notification_list(userID!!)
    }


    override fun onCategoryClick(position: Notification_list) {
        Log.d("TAG@123", userID + "")
        delete_Notification_data(position.id)
    }


    fun setAdapterListData(
        booleantype: Boolean,
        dataListuser: ArrayList<Notification_list>,
        mes: String
    ) {

        if (dataListuser.isEmpty()){
            empty_text_view.text = mes
            empty_item_layout.visibility = View.VISIBLE
            Log.d("TAG@123", " empty_text  Show")
        }
        All_list_recycler?.layoutManager = GridLayoutManager(requireContext(), 1)
        adapter = All_Activity_Adapter(requireContext(), this)
        All_list_recycler?.adapter = adapter
        adapter.setDataList(dataListuser)
        adapter.notifyDataSetChanged()
        Log.d("TAG@123", " setAdapterListData  ${dataListuser.size}")
    }

    fun subscribe_notification_list() {
        (activity as Home?)?.homeviewmodel?.notification_list?.observe(
            viewLifecycleOwner,
            Observer { res ->
                when (res.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        Log.d("TAG@123", "Status " + res.status)
                        Home.notifications_count = "0"

                        if(res.data!!.data.isNullOrEmpty()){
                            empty_text_view.text = res.data!!.message
                            empty_item_layout.visibility = View.VISIBLE
                            Log.d("TAG@123", " empty_text  Show")
                        }
                        else {
                            setAdapterListData(
                                false,
                                res.data?.data as ArrayList<Notification_list>, ""
                            )
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

    fun subscribe_notification_delete() {
        (activity as Home?)?.homeviewmodel?.deletenotification?.observe(
            viewLifecycleOwner,
            Observer { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                                get_Notification_data()
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                if (res != null) {
                                    Toast.makeText(
                                        requireContext(),
                                        res.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
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

    fun delete_Notification_data(id: String) {
        (activity as Home).homeviewmodel.deletenotification =
            MutableLiveData<Resource<Loginmodel>>()
        subscribe_notification_delete()
        var userID = (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
        Log.d("TAG@123", userID + "")
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userID)
        jsonObject.addProperty("id", id)
        (activity as Home).homeviewmodel.user_deletenotification(jsonObject)
    }

}
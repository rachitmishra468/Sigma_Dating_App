package com.SigmaDating.apk.views.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.adapters.ChatList_Adapter
import com.SigmaDating.apk.model.*
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.views.Home
import com.SigmaDating.databinding.FragmentChatListBinding
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.gms.common.data.DataHolder


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatListFragment : Fragment(), ChatList_Adapter.OnCategoryClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatlistAdapter: ChatList_Adapter
    private var dataList = mutableListOf<User_bids_list>()
    private var chat_list_recycler: RecyclerView? = null

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
        // Inflate the layout for this fragment
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        chatlistAdapter = ChatList_Adapter(requireContext(), this)
        footer_transition()
        _binding!!.movetoedit.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_editprofile)
        }
        _binding!!.movetosetting.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_setting)
        }
        _binding!!.movetonotification.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_notification)
        }
        chat_list_recycler = binding.root.findViewById(R.id.chatlist_recyclerView)
        chat_list_recycler?.layoutManager = GridLayoutManager(requireContext(), 1)
       // chatlistAdapter = ChatList_Adapter(requireContext(), this)
        _binding!!.editTextTextSearch.addTextChangedListener {

            filter(it.toString())
        }


        (activity as Home).homeviewmodel.all_match_bids= MutableLiveData<Resource<Match_bids>>()
        subscribe_create_post()
        (activity as Home).homeviewmodel.get_user_match_bids( (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID))



        return binding.root;
    }

    fun filter(text: String?) {
        val temp: MutableList<User_bids_list> = ArrayList()
        for (d in dataList) {
            if ((d.first_name+" "+d.last_name).lowercase().startsWith(text?.lowercase().toString())) {
                temp.add(d)
            }
        }
        chatlistAdapter.updateList(temp)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun footer_transition() {
        chatIcon = binding.root.findViewById(R.id.chat_Icon)
        match_list = binding.root.findViewById(R.id.match_list)
        sigma_list = binding.root.findViewById(R.id.sigma_list)

        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_disable))
        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_disable))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_enable))


        chatIcon.setOnClickListener {
            AppUtils.animateImageview(chatIcon)
            // findNavController().navigate(R.id.action_FirstFragment_to_chat)
        }

        match_list.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        sigma_list.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_FirstFragment)
        }

    }


    override fun onCategoryClick(position: User_bids_list) {
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_chatListFragment_to_userChatFragment);
    }

    fun subscribe_create_post() {
        (activity as Home?)?.homeviewmodel?.all_match_bids?.observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                                dataList=res.data as ArrayList<User_bids_list>
                                setAdapterListData(res.data as ArrayList<User_bids_list>)
                            } else {

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


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }



    fun setAdapterListData(dataListuser: ArrayList<User_bids_list>) {
        chat_list_recycler?.layoutManager =  LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )

        chat_list_recycler?.adapter = chatlistAdapter
        chatlistAdapter.setDataList(dataListuser)
        Log.d("TAG@123", " setAdapterListData  ${dataListuser.size}")
    }

}
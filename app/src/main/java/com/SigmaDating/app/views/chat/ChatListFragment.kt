package com.SigmaDating.app.views.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.SigmaDating.app.adapters.ChatList_Adapter
import com.SigmaDating.app.model.*
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.SigmaDating.databinding.FragmentChatListBinding
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.gson.JsonObject
import javax.inject.Inject

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
    lateinit var tvCounter: TextView
    lateinit var movetonotification : ConstraintLayout
    private lateinit var chatlistAdapter: ChatList_Adapter
    private var dataList = mutableListOf<User_bids_list>()
    private var chat_list_recycler: RecyclerView? = null

    lateinit var empty_text_view: TextView
    lateinit var empty_item_layout: LinearLayout
    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage
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

        empty_text_view = binding.root.findViewById(R.id.empty_text_view)
        empty_item_layout = binding.root.findViewById(R.id.empty_item_layout)
        empty_item_layout.visibility = View.GONE
        tvCounter = binding.root.findViewById(R.id.tvCounter)
        movetonotification= binding.root.findViewById(R.id.movetonotification)
        Home.notifications_count.let {
            _binding?.tvCounter?.setText(Home.notifications_count)
        }

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


    override fun onCategoryClick(position: User_bids_list,flag:Boolean) {


            (activity as Home).homeviewmodel.ctrateToken_data =
                MutableLiveData<Resource<Token_data>>()
            val jsonObject = JsonObject()
            jsonObject.addProperty(
                "identity",
                position.match_id
            )
            (activity as Home).homeviewmodel.get_User_token(
                jsonObject
            )
            subscribe_Login_User_details(position,flag)

    }

    fun subscribe_create_post() {
        (activity as Home?)?.homeviewmodel?.all_match_bids?.observe(
            viewLifecycleOwner,
            Observer {res->
                when (res.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        Log.d("TAG@123", "Notification list Status " + res.status)
                        Home.notifications_count = "0"

                        if(res.data!!.data.isNullOrEmpty()){
                            empty_text_view.text = res.data.message
                            empty_item_layout.visibility = View.VISIBLE
                            Log.d("TAG@123", " empty_text  Show")
                        }
                        else {
                            dataList=res.data.data as ArrayList<User_bids_list>
                            setAdapterListData(res.data.data as ArrayList<User_bids_list>)
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


    fun subscribe_Login_User_details(position: User_bids_list,flag: Boolean) {
        (activity as Home?)?.homeviewmodel?.ctrateToken_data?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()

                    if(flag){
                        val bundle = Bundle()
                        bundle.putString("user_id", position.id)
                        findNavController().navigate(
                            R.id.action_FirstFragment_to_SecondFragment,
                            bundle,
                            null,
                            null
                        )
                    }else{
                    val bundle = Bundle()
                    bundle.putString("user_name", position?.first_name+" "+position.last_name)
                    bundle.putString("user_image",position.upload_image)
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_chatListFragment_to_userChatFragment, bundle,
                            null,
                            null);
                }}
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
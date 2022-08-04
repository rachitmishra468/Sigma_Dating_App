package com.SigmaDating.apk.views.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.adapters.ChatList_Adapter
import com.SigmaDating.apk.model.EditProfiledata
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.databinding.FragmentChatListBinding
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
    private var dataList = mutableListOf<EditProfiledata>()
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
        chatlistAdapter = ChatList_Adapter(requireContext(), this)
        _binding!!.editTextTextSearch.addTextChangedListener {

            filter(it.toString())
        }

        //add data
        for (i in 1..10) {
            dataList.add(
                EditProfiledata(
                    "https://thumbs.dreamstime.com/b/african-american-woman-talking-mobile-phone-black-people-50437769.jpg",
                    "test demo"
                )
            )
            dataList.add(
                EditProfiledata(
                    "https://thumbs.dreamstime.com/b/beautiful-young-woman-maine-usa-close-up-portrait-native-108644385.jpg",
                    "lorem ipsum"
                )
            )
            dataList.add(
                EditProfiledata(
                    "https://thumbs.dreamstime.com/b/beauty-black-skin-woman-african-ethnic-female-face-young-african-american-model-long-afro-hair-smiling-model-isolated-163819588.jpg",
                    "demo lorem test"
                )
            )
            dataList.add(
                EditProfiledata(
                    "https://thumbs.dreamstime.com/b/african-american-woman-praying-african-american-woman-praying-god-seeking-prayer-213590092.jpg",
                    "dummy data"
                )
            )


        }

        chat_list_recycler?.adapter = chatlistAdapter
        chatlistAdapter.setDataList(dataList)
        chatlistAdapter.notifyDataSetChanged()

        return binding.root;
    }

    fun filter(text: String?) {
        val temp: MutableList<EditProfiledata> = ArrayList()
        for (d in dataList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.text.contains(text.toString())) {
                temp.add(d)
            }
        }
        //update recyclerview
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


    override fun onCategoryClick(position: EditProfiledata) {
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_chatListFragment_to_userChatFragment);
    }

}
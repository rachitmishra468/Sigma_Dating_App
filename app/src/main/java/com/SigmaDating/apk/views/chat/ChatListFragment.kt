package com.SigmaDating.apk.views.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.adapters.ChatList_Adapter
import com.SigmaDating.databinding.FragmentChatListBinding
import com.SigmaDating.apk.model.EditProfiledata

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatListFragment : Fragment(), ChatList_Adapter.OnCategoryClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var chatIcon: ImageView
    lateinit var match_list: ImageView
    lateinit var sigma_list: ImageView
    private var _binding:FragmentChatListBinding?=null
    private val binding get() = _binding!!
    private lateinit var  chatlistAdapter: ChatList_Adapter
    private var dataList = mutableListOf<EditProfiledata>()

    private var chat_list_recycler: RecyclerView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatListBinding.inflate(inflater, container, false)

        footer_transition()
        chat_list_recycler=binding.root.findViewById(R.id.chatlist_recyclerView)
        chat_list_recycler?.layoutManager = GridLayoutManager(requireContext(),1)
        chatlistAdapter = ChatList_Adapter(requireContext(),this)


        //add data
        for (i in 1..10){
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/african-american-woman-talking-mobile-phone-black-people-50437769.jpg"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/beautiful-young-woman-maine-usa-close-up-portrait-native-108644385.jpg"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/beauty-black-skin-woman-african-ethnic-female-face-young-african-american-model-long-afro-hair-smiling-model-isolated-163819588.jpg"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/african-american-woman-praying-african-american-woman-praying-god-seeking-prayer-213590092.jpg"))



        }

        chat_list_recycler?.adapter = chatlistAdapter
        chatlistAdapter.setDataList(dataList)
        chatlistAdapter.notifyDataSetChanged()

        return binding.root;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun footer_transition(){
        chatIcon = binding.root.findViewById(R.id.chat_Icon)
        match_list = binding.root.findViewById(R.id.match_list)
        sigma_list= binding.root.findViewById(R.id.sigma_list)
        //For chat chatIcon is Enable

        sigma_list.setImageDrawable(resources.getDrawable(R.drawable.sigma_disable))
        match_list.setImageDrawable(resources.getDrawable(R.drawable.heart_disable))
        chatIcon.setImageDrawable(resources.getDrawable(R.drawable.comments_enable))


        chatIcon.setOnClickListener {
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
        Navigation.findNavController(binding.root).navigate(R.id.action_chatListFragment_to_userChatFragment);
    }

}
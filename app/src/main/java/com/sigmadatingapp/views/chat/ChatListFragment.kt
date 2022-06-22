package com.sigmadatingapp.views.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sigmadatingapp.R
import com.sigmadatingapp.adapters.ChatList_Adapter
import com.sigmadatingapp.adapters.Profile_Adapter
import com.sigmadatingapp.module.EditProfiledata

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var  chatlistAdapter: ChatList_Adapter
    private var dataList = mutableListOf<EditProfiledata>()

    private var chatlist: RecyclerView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_chat_list, container, false)
        chatlist=    view.findViewById(R.id.chatlist_recyclerView)
        chatlist?.layoutManager = GridLayoutManager(requireContext(),1)
        chatlistAdapter = ChatList_Adapter(requireContext())


        //add data
        for (i in 1..10){
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/african-american-woman-talking-mobile-phone-black-people-50437769.jpg"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/beautiful-young-woman-maine-usa-close-up-portrait-native-108644385.jpg"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/beauty-black-skin-woman-african-ethnic-female-face-young-african-american-model-long-afro-hair-smiling-model-isolated-163819588.jpg"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/african-american-woman-praying-african-american-woman-praying-god-seeking-prayer-213590092.jpg"))



        }

        chatlist?.adapter = chatlistAdapter
        chatlistAdapter.setDataList(dataList)
        chatlistAdapter.notifyDataSetChanged()

        return view;
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
}
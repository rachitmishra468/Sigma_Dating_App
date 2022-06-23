package com.sigmadatingapp.views.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sigmadatingapp.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class UserChatFragment :Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var recyclerView: RecyclerView?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.user_chat_fragment, container, false)
        recyclerView=    view.findViewById(R.id.user_chatscreen_recycler)
        return view;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object{

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
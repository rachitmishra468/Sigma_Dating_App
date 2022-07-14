package com.SigmaDating.apk.views.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class UserChatFragment :Fragment() {
var chat_settings_img:ImageView?=null
    private var param1: String? = null
    private var param2: String? = null
    private var recyclerView: RecyclerView?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.user_chat_fragment, container, false)
        recyclerView=    view.findViewById(R.id.user_chatscreen_recycler)
        chat_settings_img=    view.findViewById(R.id.chat_settings)
        chat_settings_img?.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_userChatFragment_to_notificationSettingsFragment);
        }
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
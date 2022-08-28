package com.SigmaDating.apk.views.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.views.Home
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.twilio.conversations.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserChatFragment : Fragment(), QuickstartConversationsManagerListener {
    var chat_settings_img: ImageView? = null

    private var recyclerView: RecyclerView? = null

    private var messagesAdapter: MessagesAdapter? = null

    private var writeMessageEditText: TextInputEditText? = null

    private var mTextInputLayout: TextInputLayout? = null


   private val quickstartConversationsManager = QuickstartConversationsManager()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_chat_fragment, container, false)
        quickstartConversationsManager.setListener(this)
        recyclerView = view.findViewById(R.id.messageList)
        writeMessageEditText = view.findViewById(R.id.messageInput)

        mTextInputLayout?.setEndIconOnClickListener {
            val messageBody = writeMessageEditText?.text.toString()
            if (messageBody.length > 0) {
                quickstartConversationsManager.sendMessage(messageBody)
            }
        }
        chat_settings_img = view.findViewById(R.id.chat_settings)
        chat_settings_img?.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_userChatFragment_to_notificationSettingsFragment);
        }

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = layoutManager
        messagesAdapter = MessagesAdapter(quickstartConversationsManager)
        recyclerView!!.adapter = messagesAdapter

        quickstartConversationsManager.initializeWithAccessToken(
            requireContext(),
            Home.mCurrent_user_token
        )


        return view;
    }


 /*   override fun receivedNewMessage() {
        lifecycle.coroutineScope.launch(Dispatchers.Main) {
            messagesAdapter!!.notifyDataSetChanged()
        }
    }

    override fun messageSentCallback() {
        lifecycle.coroutineScope.launch(Dispatchers.Main) {
            writeMessageEditText!!.setText("")
        }
    }

    override fun reloadMessages() {
        lifecycle.coroutineScope.launch(Dispatchers.Main) {
            messagesAdapter!!.notifyDataSetChanged()
        }
    }*/


    internal class MessagesAdapter(var quickstartConversationsManager: QuickstartConversationsManager) :
        RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
        class ViewHolder(val messageTextView: TextView) : RecyclerView.ViewHolder(
            messageTextView
        )

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val messageTextView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_message_item_outgoing, parent, false) as TextView
            return ViewHolder(messageTextView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var message: Message = quickstartConversationsManager.getMessages().get(position)
            var messageText = String.format("%s: %s", message.author, message.messageBody)
            //  holder.messageTextView.text = messageText
        }

        override fun getItemCount(): Int {
            return quickstartConversationsManager.getMessages().size
        }
    }

    override fun receivedNewMessage() {
        TODO("Not yet implemented")
    }

    override fun messageSentCallback() {
        TODO("Not yet implemented")
    }

    override fun reloadMessages() {
        TODO("Not yet implemented")
    }


}
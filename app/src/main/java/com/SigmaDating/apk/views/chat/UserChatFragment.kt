package com.SigmaDating.apk.views.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.views.Home
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import com.twilio.conversations.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserChatFragment : Fragment() {
    var chat_settings_img: ImageView? = null

    private var recyclerView: RecyclerView? = null

    private var messagesAdapter: MessagesAdapter? = null

    private var writeMessageEditText: TextInputEditText? = null

    private var mTextInputLayout: TextInputLayout? = null
    lateinit var  layoutManager:LinearLayoutManager


    private val quickstartConversationsManager = QuickstartConversationsManager()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_chat_fragment, container, false)
        recyclerView = view.findViewById(R.id.messageList)
        writeMessageEditText = view.findViewById(R.id.messageInput)
        mTextInputLayout = view.findViewById(R.id.messageInputHolder)

        mTextInputLayout?.setEndIconOnClickListener {
            Log.d("TAG@123", "EndIconOnClickListener : ")
            val jsonObject = JsonObject()
            jsonObject.addProperty(
                "identity",
                (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID))

            val messageBody = writeMessageEditText?.text.toString()
            if (messageBody.length > 0) {
                Log.d("TAG@123", "EndIconOnClickListener : " + messageBody)
                quickstartConversationsManager.sendMessage(messageBody,jsonObject)
            }
        }
        chat_settings_img = view.findViewById(R.id.chat_settings)
        chat_settings_img?.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_userChatFragment_to_notificationSettingsFragment);
        }

        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = layoutManager
        messagesAdapter = MessagesAdapter(quickstartConversationsManager)
        recyclerView!!.adapter = messagesAdapter

        quickstartConversationsManager.initializeWithAccessToken(
            requireContext(),
            Home.mCurrent_user_token
        )

//
        subscribe_lisner_images()
        return view;
    }

    fun subscribe_lisner_images() {
        quickstartConversationsManager.mutableLiveData?.observe(viewLifecycleOwner, Observer {
            when (it) {
                1 -> {
                    AppUtils.hideLoader()
                    Log.d("TAG@123", "notifyDataSetChanged : " + 1)
                    GlobalScope.launch(Dispatchers.Main) {
                        messagesAdapter!!.notifyDataSetChanged()
                    }
                }
                2 -> {
                    AppUtils.hideLoader()
                    Log.d("TAG@123", "notifyDataSetChanged : " + 2)
                    GlobalScope.launch(Dispatchers.Main) {
                        writeMessageEditText!!.setText("")
                    }
                }
                3 -> {
                    AppUtils.hideLoader()
                    Log.d("TAG@123", "notifyDataSetChanged : " + 3)
                    GlobalScope.launch(Dispatchers.Main) {
                        messagesAdapter!!.notifyDataSetChanged()
                    }

                }
                4->{
                    Log.d("TAG@123", "data  : ")
                    GlobalScope.launch(Dispatchers.Main) {
                        layoutManager.stackFromEnd = true
                        messagesAdapter!!.notifyDataSetChanged()
                    }
                }
                5->{
                    Log.d("TAG@123", "showLoader : ")
                     AppUtils.showLoader(requireContext())
                }
            }
        })


    }


    internal class MessagesAdapter(var quickstartConversationsManager: QuickstartConversationsManager) :
        RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textname: TextView
            // var univercity_name: TextView
            // var date_text:TextView

            init {
                // image = itemView.findViewById(R.id.image)
                textname = itemView.findViewById(R.id.message_body)
                // univercity_name = itemView.findViewById(R.id.univercity_name)
                // date_text= itemView.findViewById(R.id.date_text)

            }

        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_message_item_outgoing, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var message: Message = quickstartConversationsManager.getMessages().get(position)
            message.attributes.jsonObject?.let { Log.d("TAG@123", it.getString("identity")) }
            Log.d("TAG@123", "attachedMedia : "+message.attachedMedia.size)
            Log.d("TAG@123", "attributes : "+message.attributes)
            var messageText = String.format("%s: %s", message.author, message.body)
            holder.textname.text = messageText
        }

        override fun getItemCount(): Int {
            return quickstartConversationsManager.getMessages().size
        }
    }


}
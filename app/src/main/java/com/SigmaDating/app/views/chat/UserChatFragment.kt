package com.SigmaDating.app.views.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.app.model.Token_data
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.video.VideoActivity
import com.SigmaDating.app.views.Home
import com.bumptech.glide.Glide
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import com.twilio.conversations.Message
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class UserChatFragment : Fragment() , QuickstartConversationsManager.SendNotification {
    var chat_settings_img: ImageView? = null
    var make_videocall: ImageView? = null

    private var recyclerView: RecyclerView? = null

    private var messagesAdapter: MessagesAdapter? = null

    private var writeMessageEditText: TextInputEditText? = null

    private var mTextInputLayout: TextInputLayout? = null
    lateinit var layoutManager: LinearLayoutManager
    var headerImg: CircleImageView? = null
    private var userChatname: TextView? = null

    private val quickstartConversationsManager = QuickstartConversationsManager()
    private var username: String? = null
    private var imagedata: String? = null
    private var id: String? = null
    private var match_id:String?=null

    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_chat_fragment, container, false)
        recyclerView = view.findViewById(R.id.messageList)
        writeMessageEditText = view.findViewById(R.id.messageInput)
        //activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)


        mTextInputLayout = view.findViewById(R.id.messageInputHolder)
        headerImg = view.findViewById(R.id.header_img)
        userChatname = view.findViewById<TextView>(R.id.chat_user_name)
        // val imgdata= (activity as Home).sharedPreferencesStorage.getString(AppConstants.upload_image)

        username = getArguments()?.getString("user_name")
        match_id= getArguments()?.getString("match_ID")
        id = getArguments()?.getString("user_ID")
        if (username != null) {
            userChatname?.text = username
        }
        imagedata = getArguments()?.getString("user_image")
        imagedata?.let {
            Glide.with(requireActivity()).load(it).into(headerImg as ImageView)
        }
        mTextInputLayout?.setEndIconOnClickListener {
            Log.d("TAG@123", "EndIconOnClickListener : ")

            val jsonObject = JsonObject()
            jsonObject.addProperty(
                "identity",
                (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
            )

            val messageBody = writeMessageEditText?.text.toString()
            if (messageBody.length > 0) {
                Log.d("TAG@123", "EndIconOnClickListener : " + messageBody)
                quickstartConversationsManager.sendMessage(messageBody, jsonObject)
            }
        }
        chat_settings_img = view.findViewById(R.id.chat_settings)
        chat_settings_img?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user_id", username)
            bundle.putString("user_image", imagedata)
            Navigation.findNavController(view)
                .navigate(
                    R.id.action_userChatFragment_to_notificationSettingsFragment,
                    bundle,
                    null,
                    null
                );
        }

        make_videocall = view.findViewById(R.id.make_videocall)
        make_videocall?.setOnClickListener {
            (activity as Home).homeviewmodel.ctrateToken_data =
                MutableLiveData<Resource<Token_data>>()
            val jsonObject = JsonObject()
            jsonObject.addProperty(
                "identity",
                id
            )
            Log.d("TAG@123", "identity : " + jsonObject.toString())
            (activity as Home).homeviewmodel.get_User_video_token(
                jsonObject
            )
            subscribe_Login_User_details()


        }





        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true

        recyclerView!!.layoutManager = layoutManager
        messagesAdapter =
            MessagesAdapter(imagedata, requireContext(), quickstartConversationsManager)
        recyclerView!!.adapter = messagesAdapter
        recyclerView!!.scrollToPosition(quickstartConversationsManager.messages.size - 1)
        setListeners()
        quickstartConversationsManager.initializeWithAccessToken(
            requireContext(),
            Home.mCurrent_user_token,
            this
        )

//
        subscribe_lisner_images()
        Log.d("TAG@123", (context as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID))
        return view;
    }

    private fun setListeners() {

        recyclerView!!.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                if (quickstartConversationsManager.messages.isEmpty()) {
                    recyclerView!!.postDelayed(Runnable {
                        recyclerView!!.smoothScrollToPosition(quickstartConversationsManager.messages.size)
                    }, 100)
                } else {
                    recyclerView!!.postDelayed(Runnable {
                        recyclerView!!.smoothScrollToPosition(quickstartConversationsManager.messages.size - 1)
                    }, 100)
                }
            }
        }

    }

    fun subscribe_lisner_images() {


        quickstartConversationsManager.mutableLiveData?.observe(viewLifecycleOwner, Observer {
            when (it) {


                1 -> {
                    AppUtils.hideLoader()
                    Log.d("TAG@123", "notifyDataSetChanged 1: " + 1)
                    GlobalScope.launch(Dispatchers.Main) {
                        messagesAdapter!!.notifyDataSetChanged()
                        //   setListeners()

                    }
                }
                2 -> {
                    AppUtils.hideLoader()
                    Log.d("TAG@123", "notifyDataSetChanged 2: " + 2)
                    GlobalScope.launch(Dispatchers.Main) {
                        writeMessageEditText!!.setText("")
                        recyclerView?.scrollToPosition(quickstartConversationsManager.messages.size - 1)

                    }
                }
                3 -> {
                    AppUtils.hideLoader()
                    Log.d("TAG@123", "notifyDataSetChanged 3: " + 3)
                    GlobalScope.launch(Dispatchers.Main) {
                        messagesAdapter!!.notifyDataSetChanged()
                        //  setListeners()
                    }

                }
                4 -> {
                    Log.d("TAG@123", "data  4: ")
                    GlobalScope.launch(Dispatchers.Main) {
                        //  layoutManager.stackFromEnd = true

                        messagesAdapter!!.notifyDataSetChanged()
                        //setListeners()
                        recyclerView?.scrollToPosition(quickstartConversationsManager.messages.size - 1)
                        writeMessageEditText!!.setText("")
                    }
                }
                5 -> {
                    Log.d("TAG@123", "showLoader 5: ")
                    AppUtils.showLoader(requireContext())
                }
            }
        })


    }


    internal class MessagesAdapter(
        var imageString: String?,
        var context: Context,
        var quickstartConversationsManager: QuickstartConversationsManager
    ) :
        RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
        private val VIEW_TYPE_MY_MESSAGE by lazy { 1 }
        private val VIEW_TYPE_OTHER_MESSAGE by lazy { 2 }
        var booleanuser: Boolean = false

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textname: TextView
            var textTime: TextView
            var imageAvatar: CircleImageView

            init {
                textname = itemView.findViewById(R.id.txtOtherMessage)
                textTime = itemView.findViewById(R.id.txtOtherMessageTime)
                imageAvatar = itemView.findViewById(R.id.imageAvatar)


            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            if (viewType == VIEW_TYPE_MY_MESSAGE) {
                return ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_message_item_outgoing, parent, false)
                )
            } else {
                return ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.incoming_message_list_item, parent, false)
                )
            }

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val message: Message = quickstartConversationsManager.getMessages().get(position)
            val sdf = SimpleDateFormat("hh:mm aa")
            val date: Date = message.dateCreatedAsDate
            val date_mess = sdf.format(date)
            Log.d("TAG@123", "Date :" + date_mess)
            Calendar.getInstance().timeInMillis
            val messageText = String.format(message.body)
            holder.textname.text = messageText
            holder.textTime.text = date_mess
            if (booleanuser) {
                val imgURl = (context as Home).sharedPreferencesStorage.getString(
                    AppConstants.upload_image
                )
                if (imgURl == "") {
                    Glide.with(context).load(R.drawable.blue_profile).into(holder.imageAvatar);
                } else {
                    Glide.with(context).load(imgURl).into(holder.imageAvatar);
                }
            } else {
                Glide.with(context).load(imageString).into(holder.imageAvatar);
            }

        }

        override fun getItemCount(): Int {
            return quickstartConversationsManager.getMessages().size


        }

        override fun getItemViewType(position: Int): Int {
            try {
                val message = quickstartConversationsManager.messages.get(position)
                val dd = message.attributes.string
                val jsonObject = JSONObject(dd)
                if ((context as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID) == jsonObject.get(
                        "identity"
                    )
                ) {
                    booleanuser = true
                    return VIEW_TYPE_MY_MESSAGE
                } else {
                    booleanuser = false
                    return VIEW_TYPE_OTHER_MESSAGE
                }
            } catch (e: Exception) {
                return VIEW_TYPE_MY_MESSAGE
            }

        }
    }

    fun subscribe_Login_User_details() {
        (activity as Home?)?.homeviewmodel?.ctrateToken_data?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()

                    activity?.let {
                        val intent = Intent(it, VideoActivity::class.java)
                        it.startActivity(intent)
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

    override fun send() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "user_id",
            id
        )
        jsonObject.addProperty(
            "match_id",
            match_id
        )
        jsonObject.addProperty(
            "type",
            "chat"
        )
        Log.d("TAG@123", "send Notification data  "+jsonObject.toString())
        (activity as Home).homeviewmodel.sendChatNotification(jsonObject)
    }


}
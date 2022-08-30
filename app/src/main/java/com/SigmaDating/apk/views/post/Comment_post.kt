package com.SigmaDating.apk.views.post

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.SigmaDating.R
import com.SigmaDating.apk.adapters.Comment_Adapter
import com.SigmaDating.apk.adapters.PostAdapter
import com.SigmaDating.apk.model.*
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.utilities.AppUtils
import com.SigmaDating.apk.views.Home
import com.SigmaDating.databinding.FragmentCommentPostBinding
import com.SigmaDating.databinding.FragmentPostListBinding
import com.bumptech.glide.Glide
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject


class Comment_post : Fragment() {

    private var _binding: FragmentCommentPostBinding? = null
    private val binding get() = _binding!!
    private var postID: String? = null
    private lateinit var commentAdapter: Comment_Adapter

    private var writeMessageEditText: TextInputEditText? = null

    private var mTextInputLayout: TextInputLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentPostBinding.inflate(inflater, container, false)
        writeMessageEditText = binding.root.findViewById(R.id.commentInput)
        mTextInputLayout = binding.root.findViewById(R.id.commentsInputHolder)
        postID = getArguments()?.getString("post_id")
        _binding!!.userName.setText(getArguments()?.getString("user_name"))
        Glide.with(requireContext()).load(getArguments()?.getString("user_img"))
            .into(_binding!!.userImg);
        _binding!!.commentTitle.setText(getArguments()?.getString("comment_title"))
        Glide.with(requireContext()).load(getArguments()?.getString("media"))
            .into(_binding!!.imageProfile);
        get_commentdata()
        return binding.root
    }


    fun get_commentdata() {

        get_all_comment_list()
        mTextInputLayout?.setEndIconOnClickListener {
            val messageBody = writeMessageEditText?.text.toString()
            if (messageBody.length > 0) {
                Log.d("TAG@123", "messa : " + messageBody)
                val jsonObject = JsonObject()
                jsonObject.addProperty("comment_text", messageBody)
                jsonObject.addProperty("post_id", postID)
                jsonObject.addProperty(
                    "user_id",
                    (activity as Home).sharedPreferencesStorage.getString(AppConstants.USER_ID)
                )
                (activity as Home).homeviewmodel.sent_comment = MutableLiveData<Resource<Loginmodel>>()
                subscribe_sent_comment()
                Log.d("TAG@123", "sent_comment : " + jsonObject.toString())
                (activity as Home).homeviewmodel.sent_comment(jsonObject)
            }
        }
    }


    fun subscribe_get_comment_list() {
        (activity as Home?)?.homeviewmodel?.All_comment?.observe(
            viewLifecycleOwner,
            Observer { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        it.data.let { res ->

                            Log.d("TAG@123", "112" + res.toString())
                                setAdapterListData(false, res?.data as ArrayList<comment_list>)

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


    fun subscribe_sent_comment() {
        (activity as Home?)?.homeviewmodel?.sent_comment?.observe(
            viewLifecycleOwner,
            Observer { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data.let { res ->
                            if (res?.status == true) {
                                writeMessageEditText?.setText("")
                                get_all_comment_list()

                            } else {

                            }
                        }
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {

                    }
                }
            })
    }


    fun setAdapterListData(booleantype: Boolean, dataListuser: ArrayList<comment_list>) {
        _binding?.postComeent?.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        commentAdapter = Comment_Adapter(booleantype, requireContext())
        _binding?.postComeent?.adapter = commentAdapter
        (_binding?.postComeent?.layoutManager as LinearLayoutManager).stackFromEnd = true
        commentAdapter.setDataList(dataListuser)
        commentAdapter.notifyDataSetChanged()
        Log.d("TAG@123", " setAdapterListData  ${dataListuser.size}")
    }


    fun get_all_comment_list() {
        (activity as Home).homeviewmodel.All_comment = MutableLiveData<Resource<Comment_model>>()
        subscribe_get_comment_list()
        val jsonObject = JsonObject()
        Log.d("TAG@123", postID + "")
        jsonObject.addProperty("post_id", postID)
        (activity as Home).homeviewmodel.getAllComment(jsonObject)

    }


}
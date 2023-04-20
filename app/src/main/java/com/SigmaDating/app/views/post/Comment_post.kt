package com.SigmaDating.app.views.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.SigmaDating.R
import com.SigmaDating.app.adapters.Comment_Adapter
import com.SigmaDating.app.adapters.TagsAdapter
import com.SigmaDating.app.model.Comment_model
import com.SigmaDating.app.model.Loginmodel
import com.SigmaDating.app.model.TaggedUsers
import com.SigmaDating.app.model.comment_list
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.SigmaDating.databinding.FragmentCommentPostBinding
import com.bumptech.glide.Glide
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject


class Comment_post : Fragment() ,TagsAdapter.OnCategoryClickListener {

    private var _binding: FragmentCommentPostBinding? = null
    private val binding get() = _binding!!
    private var postID: String? = null
    private lateinit var commentAdapter: Comment_Adapter
    lateinit var adapter: TagsAdapter

    private var writeMessageEditText: TextInputEditText? = null

    private var mTextInputLayout: TextInputLayout? = null
    lateinit var layoutTest : LinearLayout

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
        layoutTest =  binding.root.findViewById(R.id.layoutTest)
        val dim = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dim.setMargins(2,2,2,2)
        Home.tags_user.let {
            for (p in 0 until 8 ){
            for (i in 0 until Home.tags_user.size){
                val textView = TextView(requireContext())
                textView.apply {
                    text = Home.tags_user.get(i).name
                    this.setLayoutParams(dim)
                    this.textSize=9f
                    this.setBackground(resources.getDrawable(R.drawable.border_line_radius))
                    this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tag_icon_small, 0, 0, 0);
                }
                layoutTest.addView(textView)
            }
        }
        }
        postID = getArguments()?.getString("post_id")
        _binding!!.userName.setText(getArguments()?.getString("user_name"))
        if (Home.tags_user.isNullOrEmpty()) {
            _binding!!.tagsComeent.visibility = View.GONE
            _binding!!.tagsText.visibility = View.GONE
        } else {
           _binding?.tagsComeent?.layoutManager = GridLayoutManager(requireContext(),4)
          /*  _binding?.tagsComeent?.layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL, false
            )*/
            adapter = TagsAdapter(requireContext(), this)
            _binding?.tagsComeent?.adapter = adapter
            adapter.setDataList(Home.tags_user)
            adapter.notifyDataSetChanged()
        }
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
                (activity as Home).homeviewmodel.sent_comment =
                    MutableLiveData<Resource<Loginmodel>>()
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
                           try {
                               setAdapterListData(
                                   false,
                                   res?.data?.reversed() as ArrayList<comment_list>
                               )
                           } catch (e:Exception){

                           }
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
                        AppUtils.hideLoader()
                        it.data.let { res ->
                            if (res?.status == true) {
                                writeMessageEditText?.setText("")
                                get_all_comment_list()

                            } else {

                            }
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

    override fun onCategoryClick(position: TaggedUsers) {
        val bundle = Bundle()
        bundle.putString("user_id",position.id)
        findNavController().navigate(R.id.comment_post_action_SecondFragment,bundle)
    }

}
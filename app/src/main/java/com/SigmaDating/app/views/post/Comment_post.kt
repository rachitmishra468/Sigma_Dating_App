package com.SigmaDating.app.views.post

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.marginBottom
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
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject


class Comment_post : Fragment(), TagsAdapter.OnCategoryClickListener {

    private var _binding: FragmentCommentPostBinding? = null
    private val binding get() = _binding!!
    private var postID: String? = null
    private var videofile: String? = null
    private lateinit var commentAdapter: Comment_Adapter
    lateinit var adapter: TagsAdapter
    lateinit var rootContainer: ChipGroup
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
        rootContainer = _binding?.root?.findViewById(R.id.rootContainer)!!
        Home.tags_user.let {
            setupChipGroupDynamically(it, rootContainer)
        }
        postID = getArguments()?.getString("post_id")
        videofile = getArguments()?.getString("videofile")
        var mPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        if (!videofile.isNullOrEmpty()) {
            _binding!!.videoView.player = mPlayer
            mPlayer.playWhenReady = false
            _binding!!.videoView.visibility = View.VISIBLE
            _binding!!.imageProfile.visibility = View.INVISIBLE
            mPlayer.setMediaSource(buildMediaSource(videofile!!))
            mPlayer.prepare()
            mPlayer.pause()
            if (mPlayer.isPlaying) {
                mPlayer.pause()
            }
        } else {
            _binding!!.videoView.visibility = View.INVISIBLE
            _binding!!.imageProfile.visibility = View.VISIBLE
        }

        _binding!!.userName.setText(getArguments()?.getString("user_name"))
        if (Home.tags_user.isNullOrEmpty()) {
            _binding!!.tagsComeent.visibility = View.GONE
            _binding!!.tagsText.visibility = View.GONE
        } else {
            _binding?.tagsComeent?.layoutManager = GridLayoutManager(requireContext(), 4)
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

        _binding!!.imageView2.setOnClickListener {
            mPlayer.pause()
            (activity as Home).onBackPressed()
        }
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
                            } catch (e: Exception) {

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
        bundle.putString("user_id", position.id)
        findNavController().navigate(R.id.comment_post_action_SecondFragment, bundle)
    }


    private fun setupChipGroupDynamically(
        list: List<TaggedUsers>,
        rootContainer: ChipGroup,
    ) {
        if (list.isNotEmpty()) {
            try {
                rootContainer.removeAllViews()
                for (i in list.indices) {
                    Log.d("TAG@123", "ChipGroup : " + list.get(i))
                    if (list[i].name.isNotEmpty()) {
                        rootContainer.addView(createChip(list.get(i).name, i, list.get(i).id))
                    }

                }
            } catch (e: Exception) {
            }
        }
    }


    @SuppressLint("ResourceType")
    private fun createChip(label: String, index: Int, ID: String): Chip {
        val chip = Chip(requireContext(), null)
        chip.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        chip.text = label
        chip.isCloseIconVisible = false
        chip.isChipIconVisible = true
        chip.isCheckable = false
        chip.isChecked = true
        chip.chipStrokeWidth = 1f
        chip.chipCornerRadius = 5f
        chip.isChecked = true
        chip.chipIconSize = 22f
        chip.chipMinHeight = 60f
        chip.setChipStartPadding(10f)
        chip.setChipEndPadding(0f)
        //chip.height= 19
        chip.width = 35
        chip.chipIcon = resources.getDrawable(R.drawable.tag_icon)
        chip.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.blue))
        chip.setTextSize(11f)
        chip.setChipStrokeColorResource(R.color.blue)
        chip.setChipBackgroundColorResource(android.R.color.transparent)
        chip.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user_id", ID)
            findNavController().navigate(R.id.comment_post_action_SecondFragment, bundle)

        }

        return chip
    }

    private fun buildMediaSource(videoURL: String): MediaSource {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoURL))
        return mediaSource
    }


}
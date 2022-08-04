package com.SigmaDating.apk.views.all_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.adapters.All_Activity_Adapter
import com.SigmaDating.apk.adapters.ChatList_Adapter
import com.SigmaDating.apk.model.EditProfiledata
import com.SigmaDating.apk.views.Home
import com.SigmaDating.databinding.FragmentAllActivitysBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class All_Activitys : Fragment(), All_Activity_Adapter.OnCategoryClickListener {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adapter: All_Activity_Adapter
    private var dataList = mutableListOf<EditProfiledata>()
    private var All_list_recycler: RecyclerView? = null
    private lateinit var _binding: FragmentAllActivitysBinding
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllActivitysBinding.inflate(inflater, container, false)
        _binding.finishNotification.setOnClickListener {
            (activity as Home).onBackPressed()
        }

        All_list_recycler = binding.root.findViewById(R.id.alllist_recyclerView)
        All_list_recycler?.layoutManager = GridLayoutManager(requireContext(), 1)
        adapter = All_Activity_Adapter(requireContext(), this)


        //add data
        for (i in 1..10) {
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/african-american-woman-talking-mobile-phone-black-people-50437769.jpg","demo test"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/beautiful-young-woman-maine-usa-close-up-portrait-native-108644385.jpg","demo test"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/beauty-black-skin-woman-african-ethnic-female-face-young-african-american-model-long-afro-hair-smiling-model-isolated-163819588.jpg","demo test"))
            dataList.add(EditProfiledata("https://thumbs.dreamstime.com/b/african-american-woman-praying-african-american-woman-praying-god-seeking-prayer-213590092.jpg","demo test"))


        }

        All_list_recycler?.adapter = adapter
        adapter.setDataList(dataList)
        adapter.notifyDataSetChanged()

        return binding.root;
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            All_Activitys().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCategoryClick(position: EditProfiledata) {

    }
}
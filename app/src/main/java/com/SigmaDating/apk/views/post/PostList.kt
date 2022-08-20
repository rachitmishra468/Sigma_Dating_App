package com.SigmaDating.apk.views.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.SigmaDating.R
import com.SigmaDating.databinding.FragmentCreatePostBinding
import com.SigmaDating.databinding.FragmentPostListBinding

class PostList : Fragment() {

    private var _binding: FragmentPostListBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentPostListBinding.inflate(inflater, container, false)

        return binding.root
    }


}
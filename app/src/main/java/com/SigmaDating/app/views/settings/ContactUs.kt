package com.SigmaDating.app.views.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import com.SigmaDating.R
import com.SigmaDating.app.utilities.AppUtils.hideLoader
import com.SigmaDating.app.utilities.AppUtils.showLoader
import com.SigmaDating.app.views.Home

private const val ARG_PARAM1 = "Url_Link"
private const val ARG_PARAM2 = "Hadder_text"


class ContactUs : Fragment() {

    private var mURL: String? = null
    private var mHadder: String? = null
    private lateinit var webView: WebView
    lateinit var imageView2:ImageView
    lateinit var textView6:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        var view= inflater.inflate(R.layout.fragment_contact_us, container, false)
        arguments?.let {
            mURL = it.getString(ARG_PARAM1)
            mHadder = it.getString(ARG_PARAM2)
            Log.d("TAG@123","URL $mURL Hadder $mHadder")
        }
        imageView2=view.findViewById(R.id.imageView2)
        imageView2.setOnClickListener {
            (activity as Home).onBackPressed()
        }
        textView6=view.findViewById(R.id.textView6)
        webView = view.findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()

        showLoader(requireContext())
        mURL?.let { webView.loadUrl(it) }
        mHadder?.let { textView6.text=mHadder }
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactUs().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    inner class WebViewClient : android.webkit.WebViewClient() {

        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        // ProgressBar will disappear once page is loaded
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            hideLoader()
        }
    }
}
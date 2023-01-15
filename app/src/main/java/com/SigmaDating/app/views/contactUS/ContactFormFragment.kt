package com.SigmaDating.app.views.contactUS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.SigmaDating.R
import com.SigmaDating.app.model.Loginmodel
import com.SigmaDating.app.model.SubscriptionPlanData
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.gson.JsonObject


class ContactFormFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var imageView2: ImageView

    lateinit var editText_name: EditText
    lateinit var editText_email: EditText
    lateinit var editText_subject: EditText
    lateinit var editText_message: EditText
    lateinit var buttonSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_contact_form, container, false)

        imageView2 = view.findViewById(R.id.imageView2)
        editText_name = view.findViewById(R.id.editText_name)
        editText_email = view.findViewById(R.id.editText_email)
        editText_subject = view.findViewById(R.id.editText_subject)
        editText_message = view.findViewById(R.id.editText_message)
        buttonSubmit = view.findViewById(R.id.buttonSubmit)

        imageView2.setOnClickListener {
            (activity as Home).onBackPressed()
        }

        buttonSubmit.setOnClickListener {

            if (editText_name.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter Your Name",
                    Toast.LENGTH_LONG
                ).show()

            } else if (editText_email.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter Your Email",
                    Toast.LENGTH_LONG
                ).show()
            } else if (editText_subject.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter Subject",
                    Toast.LENGTH_LONG
                ).show()
            } else if (editText_message.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter Message",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                (activity as Home).homeviewmodel.contact_responce =
                    MutableLiveData<Resource<Loginmodel>>()
                val jsonObject = JsonObject()
                jsonObject.addProperty(
                    "name",
                    editText_name.text.toString()
                )
                jsonObject.addProperty(
                    "email",
                    editText_email.text.toString()
                )

                jsonObject.addProperty(
                    "subject",
                    editText_subject.text.toString()
                )

                jsonObject.addProperty(
                    "message",
                    editText_message.text.toString()
                )

                (activity as Home).homeviewmodel.post_Contact_form(jsonObject)
                subscribe_create_post()






            }


        }


        return view
    }


    fun subscribe_create_post() {
        AppUtils.showLoader(requireContext())
        (activity as Home?)?.homeviewmodel?.contact_responce?.observe(
            viewLifecycleOwner,
            Observer { res ->
                when (res.status) {
                    Status.SUCCESS -> {
                        AppUtils.hideLoader()
                        Toast.makeText(requireContext(), res.data?.message.toString(), Toast.LENGTH_LONG).show()
                        (activity as Home).onBackPressed()
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





    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactFormFragment().apply {

            }
    }
}
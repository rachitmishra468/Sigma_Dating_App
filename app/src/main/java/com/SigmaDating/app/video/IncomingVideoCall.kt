package com.SigmaDating.app.video

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.SigmaDating.R
import com.SigmaDating.app.model.Token_data
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.bumptech.glide.Glide
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView


@AndroidEntryPoint
class IncomingVideoCall : AppCompatActivity() {

    lateinit var call_pick: FloatingActionButton
    lateinit var call_cut: FloatingActionButton
    lateinit var nameTextView:TextView
    lateinit var userImageView: CircleImageView
    val homeviewmodel: IncomingVideoCallViewModel by viewModels()
    var user_ID = ""
    var type = ""
    var match_ID = ""
    var user_name=""
    var user_images=""
    var CALL_ACTION = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_video_call)
        call_pick = findViewById(R.id.pick_call)
        call_cut = findViewById(R.id.end_call)
        nameTextView=findViewById(R.id.incomig_call_title)
        userImageView=findViewById(R.id.image_profile)
        user_ID = intent.getStringExtra("USERID").toString()
        match_ID = intent.getStringExtra("MATCHID").toString()
        user_name = intent.getStringExtra("NAME").toString()
        user_images = intent.getStringExtra("IMAGE").toString()
        CALL_ACTION = intent.getStringExtra("ACTION").toString()
        Log.d("TAG@123", "CALL_ACTION CALL_ACTION : " + CALL_ACTION)
        Log.d("TAG@123", "identity IncomingVideoCall : " + match_ID)
        Home.match_id = match_ID
        homeviewmodel.ctrateToken_data =
            MutableLiveData<Resource<Token_data>>()
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "identity",
            match_ID
        )
        Log.d("TAG@123", "identity : " + jsonObject.toString())
        homeviewmodel.get_User_video_token(
            jsonObject
        )
        subscribe_Login_User_details()

        Glide.with(this).load(user_images)
            .error(R.drawable.profile_img)
            .into(userImageView);
        nameTextView.setText(user_name)
        call_pick.isEnabled = false
        call_pick.setOnClickListener {
            AppUtils.stopPhoneCallRing()
            val intent = Intent(this, VideoActivity::class.java)
            intent.putExtra("TYPE", 1)
            startActivity(intent)
            finish()
        }
        call_cut.setOnClickListener {
            AppUtils.stopPhoneCallRing()
            val intent = Intent(this, VideoActivity::class.java)
            intent.putExtra("TYPE", 2)
            startActivity(intent)
            finish()
        }

        val manager = NotificationManagerCompat.from(this)
        manager.apply {
            cancelAll()
        }
    }

    fun subscribe_Login_User_details() {
        homeviewmodel.ctrateToken_data.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    call_pick.isEnabled = true
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                }
            }
        })
    }

}
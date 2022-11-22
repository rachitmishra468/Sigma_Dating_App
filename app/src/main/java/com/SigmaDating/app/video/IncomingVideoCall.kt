package com.SigmaDating.app.video

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.SigmaDating.R
import com.SigmaDating.app.utilities.AppUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IncomingVideoCall : AppCompatActivity() {

    lateinit var call_pick: FloatingActionButton
    lateinit var call_cut: FloatingActionButton
    val homeviewmodel: IncomingVideoCallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_video_call)
        call_pick = findViewById(R.id.pick_call)
        call_cut = findViewById(R.id.end_call)

       // homeviewmodel.

        call_pick.setOnClickListener {
            AppUtils.stopPhoneCallRing()
        }
        call_cut.setOnClickListener {
            onBackPressed()
            AppUtils.stopPhoneCallRing()
        }

    }

}
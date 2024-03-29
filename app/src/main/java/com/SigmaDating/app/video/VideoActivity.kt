package com.SigmaDating.app.video


import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.SigmaDating.R
import com.SigmaDating.app.model.Token_data
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.SigmaDating.app.views.Home.Companion.match_id
import com.bumptech.glide.Glide
import com.example.demoapp.other.Resource
import com.example.demoapp.other.Status
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import com.twilio.video.*
import com.twilio.video.ktx.Video
import com.twilio.video.ktx.createLocalAudioTrack
import com.twilio.video.ktx.createLocalVideoTrack
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import tvi.webrtc.VideoSink
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class VideoActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage
    val viewModel: IncomingVideoCallViewModel by viewModels()
    private lateinit var connectActionFab: FloatingActionButton
    private lateinit var localVideoActionFab: FloatingActionButton
    private lateinit var muteActionFab: FloatingActionButton
    private lateinit var switchCameraActionFab: FloatingActionButton
    private lateinit var primaryVideoView: VideoView
    private lateinit var reconnectingProgressBar: ProgressBar
    private lateinit var thumbnailVideoView: VideoView
    private lateinit var videoStatusTextView: TextView
    private lateinit var main_window: ConstraintLayout

    private val CAMERA_MIC_PERMISSION_REQUEST_CODE = 1
    private val TAG = "TAG@123"
    private val CAMERA_PERMISSION_INDEX = 0
    private val MIC_PERMISSION_INDEX = 1
    private var DEFAULT_CONVERSATION_NAME = "Sigma" + match_id
    var type = 0
    var user_ID = ""
    var match_ID = ""
    var user_name = ""
    var user_images = ""
    var sender_id = ""
    var CALL_ACTION: String? = null
    lateinit var call_pick: FloatingActionButton
    lateinit var call_cut: FloatingActionButton
    lateinit var nameTextView: TextView
    lateinit var userImageView: CircleImageView

    private lateinit var accessToken: String

    private var room: Room? = null
    private var localParticipant: LocalParticipant? = null
    private var view_flag = false

    /*
     * AudioCodec and VideoCodec represent the preferred codec for encoding and decoding audio and
     * video.
     */
    private val audioCodec: AudioCodec
        get() {
            val audioCodecName = sharedPreferences.getString(
                SettingsActivity.PREF_AUDIO_CODEC,
                SettingsActivity.PREF_AUDIO_CODEC_DEFAULT
            )

            return when (audioCodecName) {
                IsacCodec.NAME -> IsacCodec()
                OpusCodec.NAME -> OpusCodec()
                PcmaCodec.NAME -> PcmaCodec()
                PcmuCodec.NAME -> PcmuCodec()
                G722Codec.NAME -> G722Codec()
                else -> OpusCodec()
            }
        }
    private val videoCodec: VideoCodec
        get() {
            val videoCodecName = sharedPreferences.getString(
                SettingsActivity.PREF_VIDEO_CODEC,
                SettingsActivity.PREF_VIDEO_CODEC_DEFAULT
            )

            return when (videoCodecName) {
                Vp8Codec.NAME -> {
                    val simulcast = sharedPreferences.getBoolean(
                        SettingsActivity.PREF_VP8_SIMULCAST,
                        SettingsActivity.PREF_VP8_SIMULCAST_DEFAULT
                    )
                    Vp8Codec(simulcast)
                }
                H264Codec.NAME -> H264Codec()
                Vp9Codec.NAME -> Vp9Codec()
                else -> Vp8Codec()
            }
        }

    private val enableAutomaticSubscription: Boolean
        get() {
            return sharedPreferences.getBoolean(
                SettingsActivity.PREF_ENABLE_AUTOMATIC_SUBSCRIPTION,
                SettingsActivity.PREF_ENABLE_AUTOMATIC_SUBCRIPTION_DEFAULT
            )
        }

    /*
     * Encoding parameters represent the sender side bandwidth constraints.
     */
    private val encodingParameters: EncodingParameters
        get() {
            val defaultMaxAudioBitrate = SettingsActivity.PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT
            val defaultMaxVideoBitrate = SettingsActivity.PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT
            val maxAudioBitrate = Integer.parseInt(
                sharedPreferences.getString(
                    SettingsActivity.PREF_SENDER_MAX_AUDIO_BITRATE,
                    defaultMaxAudioBitrate
                ) ?: defaultMaxAudioBitrate
            )
            val maxVideoBitrate = Integer.parseInt(
                sharedPreferences.getString(
                    SettingsActivity.PREF_SENDER_MAX_VIDEO_BITRATE,
                    defaultMaxVideoBitrate
                ) ?: defaultMaxVideoBitrate
            )

            return EncodingParameters(maxAudioBitrate, maxVideoBitrate)
        }

    /*
     * Room events listener
     */
    private val roomListener = object : Room.Listener {
        override fun onConnected(room: Room) {
            Log.d("TAG@123", "onConnected :$room")
            Log.d("TAG@123", "room name :" + room.name)
            Log.d("TAG@123", "room sid :" + room.sid.toString())

            localParticipant = room.localParticipant
            Log.d(TAG, "localParticipant + " + room.localParticipant.toString())
            videoStatusTextView.text = "Connected to ${room.name}"
            title = room.name

            Log.d(TAG, "remoteParticipants + " + room.remoteParticipants.toString())
            // Only one participant is supported
            room.remoteParticipants.firstOrNull()?.let { addRemoteParticipant(it) }
            // addRemoteParticipant(room.remoteParticipants.firstOrNull()!!)
        }

        override fun onReconnected(room: Room) {
            Log.d("TAG@123", "onReconnected :" + room)
            videoStatusTextView.text = "Connected to ${room.name}"
            reconnectingProgressBar.visibility = View.GONE
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
            Log.d(
                "TAG@123",
                "onReconnecting :" + room + "  :: TwilioException :" + twilioException.toString()
            )
            videoStatusTextView.text = "Reconnecting to ${room.name}"
            reconnectingProgressBar.visibility = View.VISIBLE
        }

        override fun onConnectFailure(room: Room, e: TwilioException) {
            Log.d("TAG@123", "onConnectFailure :" + room + "  :: TwilioException :" + e.toString())
            videoStatusTextView.text = "Failed to connect"
            audioSwitch.deactivate()
            initializeUI()
        }

        override fun onDisconnected(room: Room, e: TwilioException?) {
            Log.d("TAG@123", "Error :" + e.toString())
            close()
            localParticipant = null
            videoStatusTextView.text = "Disconnected from ${room.name}"
            reconnectingProgressBar.visibility = View.GONE
            this@VideoActivity.room = null
            if (!disconnectedFromOnDestroy) {
                audioSwitch.deactivate()
                initializeUI()
                moveLocalVideoToPrimaryView()
                onBackPressed()
            }

        }

        override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
            Log.d("TAG@123", "onConnectFailure :" + room + "  :: participant :" + participant.sid)
            addRemoteParticipant(participant)
        }

        override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {
            Log.d(
                "TAG@123",
                "onParticipantDisconnected :" + room + "  :: participant :" + participant.sid
            )
            removeRemoteParticipant(participant)
        }

        override fun onRecordingStarted(room: Room) {
            Log.d("TAG@123", "onRecordingStarted :" + room)

            /*
             * Indicates when media shared to a Room is being recorded. Note that
             * recording is only available in our Group Rooms developer preview.
             */
            Log.d(TAG, "onRecordingStarted")
        }

        override fun onRecordingStopped(room: Room) {
            Log.d("TAG@123", "onRecordingStopped :" + room)

            /*
             * Indicates when media shared to a Room is no longer being recorded. Note that
             * recording is only available in our Group Rooms developer preview.
             */
            Log.d(TAG, "onRecordingStopped")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showWhenLockedAndTurnScreenOn()
        setContentView(R.layout.activity_video)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                broadCastReceiver,
                IntentFilter("BROADCAST_FOR_CLOSE_VIDEO")
            )
        Home.incoming_call = true
        main_window = findViewById(R.id.main_window)
        connectActionFab = findViewById(R.id.connectActionFab)
        localVideoActionFab = findViewById(R.id.localVideoActionFab)
        muteActionFab = findViewById(R.id.muteActionFab)
        switchCameraActionFab = findViewById(R.id.switchCameraActionFab)
        primaryVideoView = findViewById(R.id.primaryVideoView)
        reconnectingProgressBar = findViewById(R.id.reconnectingProgressBar)
        thumbnailVideoView = findViewById(R.id.thumbnailVideoView)
        videoStatusTextView = findViewById(R.id.videoStatusTextView)
        val manager = NotificationManagerCompat.from(this)
        manager.apply {
            cancelAll()
        }
        CALL_ACTION = intent.getStringExtra("ACTION").toString()
        view_flag = false
        Log.d("TAG@123", "Video identity : ----- " + intent.getIntExtra("TYPE", 100))
        when (intent.getIntExtra("TYPE", 100)) {
            1 -> {
                Log.d("TAG@123", "CALL_ACTION CALL_ACTION : " + CALL_ACTION)
                user_ID = intent.getStringExtra("USERID").toString()
                match_ID = intent.getStringExtra("MATCHID").toString()
                user_name = intent.getStringExtra("NAME").toString()
                user_images = intent.getStringExtra("IMAGE").toString()
                sender_id = intent.getStringExtra("SENDERID").toString()

                viewModel.ctrateToken_data =
                    MutableLiveData<Resource<Token_data>>()
                val jsonObject = JsonObject()
                jsonObject.addProperty(
                    "identity",
                    match_ID
                )
                Log.d("TAG@123", "identity : " + jsonObject.toString())
                viewModel.get_User_video_token(
                    jsonObject
                )
                subscribe_Login_User_details(true)
                from_notification()
            }
            0 -> {
                from_notification()
                CALL_ACTION = "Chat"
                main_window.visibility = View.GONE
                user_ID = intent.getStringExtra("USERID").toString()
                match_ID = intent.getStringExtra("MATCHID").toString()
                user_name = intent.getStringExtra("NAME").toString()
                user_images = intent.getStringExtra("IMAGE").toString()
                sender_id = intent.getStringExtra("SENDERID").toString()
                viewModel.ctrateToken_data =
                    MutableLiveData<Resource<Token_data>>()
                val jsonObject = JsonObject()
                jsonObject.addProperty(
                    "identity",
                    match_ID + "0"
                )
                Log.d("TAG@123", "identity : " + jsonObject.toString())
                viewModel.get_User_video_token(
                    jsonObject
                )
                subscribe_Login_User_details(false)

            }
            else -> {
                user_ID = intent.getStringExtra("USERID").toString()
                match_ID = intent.getStringExtra("MATCHID").toString()
                user_name = intent.getStringExtra("NAME").toString()
                user_images = intent.getStringExtra("IMAGE").toString()
                sender_id = intent.getStringExtra("SENDERID").toString()
                viewModel.ctrateToken_data =
                    MutableLiveData<Resource<Token_data>>()
                val jsonObject = JsonObject()
                jsonObject.addProperty(
                    "identity",
                    match_ID
                )
                Log.d("TAG@123", "identity : " + jsonObject.toString())
                viewModel.get_User_video_token(
                    jsonObject
                )
                subscribe_Login_User_details(true)
                from_notification()

            }
        }


    }

    fun subscribe_Login_User_details(flag: Boolean) {
        viewModel.ctrateToken_data.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtils.hideLoader()
                    call_pick.isEnabled = true

                    connectActionFab.setOnClickListener {
                        onBackPressed()
                    }


                    /*
                     * Set local video view to primary view
                     */
                    localVideoView = primaryVideoView

                    /*
                     * Enable changing the volume using the up/down keys during a conversation
                     */
                    savedVolumeControlStream = volumeControlStream
                    volumeControlStream = AudioManager.STREAM_VOICE_CALL

                    /*
                     * Set access token
                     */
                    setAccessToken()
                    //  connectToRoom(DEFAULT_CONVERSATION_NAME,false)

                    if (CALL_ACTION.equals("accept")) {
                        call_pick()
                    } else if (CALL_ACTION.equals("OPEN")) {

                    } else if (CALL_ACTION.equals("Chat")) {
                        from_chat()
                    } else {
                        cut_call()
                    }
                }
                Status.LOADING -> {
                    AppUtils.showLoader(this)
                    if (flag) {
                        AppUtils.playPhoneCallRing(this)
                    }
                }
                Status.ERROR -> {
                    AppUtils.hideLoader()
                }
            }
        })
    }


    fun from_notification() {
        AppUtils.stopPhoneCallRing()
        call_pick = findViewById(R.id.pick_call)
        call_cut = findViewById(R.id.end_call)
        nameTextView = findViewById(R.id.incomig_call_title)
        userImageView = findViewById(R.id.image_profile)
        Glide.with(this).load(user_images)
            .error(R.drawable.profile_img)
            .into(userImageView);
        nameTextView.setText("Video call " + user_name)
        call_pick.isEnabled = false
        call_pick.setOnClickListener {
            call_pick()
        }
        call_cut.setOnClickListener {
            cut_call()
        }
    }


    fun call_pick() {
        AppUtils.stopPhoneCallRing()
        main_window.visibility = View.GONE
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraMicrophoneAndBluetooth()
        } else {
            audioSwitch.start { audioDevices, audioDevice ->
                updateAudioDeviceIcon(
                    audioDevice
                )
            }
        }
        initializeUI()
        connectToRoom(DEFAULT_CONVERSATION_NAME, true)
        connectvideoTrack(false)
    }

    fun cut_call() {
        AppUtils.stopPhoneCallRing()
        onBackPressed()
        //sharedPreferencesStorage.getString(AppConstants.USER_ID)
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "user_id",
            Home.sender_id
        )
        jsonObject.addProperty(
            "match_id",
            match_ID
        )
        jsonObject.addProperty(
            "type",
            "close_video"
        )
        jsonObject.addProperty(
            "name",
            sharedPreferencesStorage.getString(
                AppConstants.USER_NAME
            )
        )
        jsonObject.addProperty(
            "image",
            user_images
        )
        Log.d(TAG, "video Cut data  Send : -" + jsonObject.toString())
        viewModel.sendChatNotification(jsonObject)
        onBackPressed()
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

    fun from_chat() {
        connectActionFab.setOnClickListener { onBackPressed() }


        /*
         * Set local video view to primary view
         */
        localVideoView = primaryVideoView

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        savedVolumeControlStream = volumeControlStream
        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        /*
         * Set access token
         */
        setAccessToken()

        /*
         * Check camera and microphone permissions. Also, request for bluetooth
         * permissions for enablement of bluetooth audio routing.
         */
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraMicrophoneAndBluetooth()
        } else {
            audioSwitch.start { audioDevices, audioDevice -> updateAudioDeviceIcon(audioDevice) }
        }
        /*
         * Set the initial state of the UI
         */
        initializeUI()
        connectToRoom(DEFAULT_CONVERSATION_NAME, true)
        connectvideoTrack(false)
    }


    /*
     * RemoteParticipant events listener
     */
    private val participantListener = object : RemoteParticipant.Listener {
        override fun onAudioTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            Log.d(
                TAG, "onAudioTrackPublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                        "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteAudioTrackPublication.trackName}]"
            )
            videoStatusTextView.text = "onAudioTrackAdded"
        }

        override fun onAudioTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            Log.d(
                TAG, "onAudioTrackUnpublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                        "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteAudioTrackPublication.trackName}]"
            )
            videoStatusTextView.text = "onAudioTrackRemoved"
        }

        override fun onDataTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication
        ) {
            Log.d(
                TAG, "onDataTrackPublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                        "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteDataTrackPublication.trackName}]"
            )
            videoStatusTextView.text = "onDataTrackPublished"
        }

        override fun onDataTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication
        ) {
            Log.d(
                TAG, "onDataTrackUnpublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                        "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteDataTrackPublication.trackName}]"
            )
            videoStatusTextView.text = "onDataTrackUnpublished"
        }

        override fun onVideoTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            Log.d(
                TAG, "onVideoTrackPublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                        "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteVideoTrackPublication.trackName}]"
            )
            videoStatusTextView.text = "onVideoTrackPublished"
        }

        override fun onVideoTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            Log.d(
                TAG, "onVideoTrackUnpublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                        "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteVideoTrackPublication.trackName}]"
            )
            videoStatusTextView.text = "onVideoTrackUnpublished"
        }

        override fun onAudioTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            remoteAudioTrack: RemoteAudioTrack
        ) {
            Log.d(
                TAG, "onAudioTrackSubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                        "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                        "name=${remoteAudioTrack.name}]"
            )
            videoStatusTextView.text = "onAudioTrackSubscribed"
        }

        override fun onAudioTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            remoteAudioTrack: RemoteAudioTrack
        ) {
            Log.d(
                TAG, "onAudioTrackUnsubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                        "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                        "name=${remoteAudioTrack.name}]"
            )
            videoStatusTextView.text = "onAudioTrackUnsubscribed"
        }

        override fun onAudioTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            twilioException: TwilioException
        ) {
            Log.d(
                TAG, "onAudioTrackSubscriptionFailed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                        "name=${remoteAudioTrackPublication.trackName}]" +
                        "[TwilioException: code=${twilioException.code}, " +
                        "message=${twilioException.message}]"
            )
            videoStatusTextView.text = "onAudioTrackSubscriptionFailed"
        }

        override fun onDataTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            remoteDataTrack: RemoteDataTrack
        ) {
            Log.d(
                TAG, "onDataTrackSubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                        "name=${remoteDataTrack.name}]"
            )
            videoStatusTextView.text = "onDataTrackSubscribed"
        }

        override fun onDataTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            remoteDataTrack: RemoteDataTrack
        ) {
            Log.d(
                TAG, "onDataTrackUnsubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                        "name=${remoteDataTrack.name}]"
            )
            videoStatusTextView.text = "onDataTrackUnsubscribed"
        }

        override fun onDataTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            twilioException: TwilioException
        ) {
            Log.d(
                TAG, "onDataTrackSubscriptionFailed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                        "name=${remoteDataTrackPublication.trackName}]" +
                        "[TwilioException: code=${twilioException.code}, " +
                        "message=${twilioException.message}]"
            )
            videoStatusTextView.text = "onDataTrackSubscriptionFailed"
        }

        override fun onVideoTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            remoteVideoTrack: RemoteVideoTrack
        ) {


            Log.d(
                TAG, "onVideoTrackSubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                        "name=${remoteVideoTrack.name}]"
            )
            videoStatusTextView.text = "onVideoTrackSubscribed"
            addRemoteParticipantVideo(remoteVideoTrack)
        }

        override fun onVideoTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            remoteVideoTrack: RemoteVideoTrack
        ) {
            Log.d(
                TAG, "onVideoTrackUnsubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                        "name=${remoteVideoTrack.name}]"
            )
            videoStatusTextView.text = "onVideoTrackUnsubscribed"
            removeParticipantVideo(remoteVideoTrack)
        }

        override fun onVideoTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            twilioException: TwilioException
        ) {
            Log.d(
                TAG, "onVideoTrackSubscriptionFailed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                        "name=${remoteVideoTrackPublication.trackName}]" +
                        "[TwilioException: code=${twilioException.code}, " +
                        "message=${twilioException.message}]"
            )
            videoStatusTextView.text = "onVideoTrackSubscriptionFailed"

        }

        override fun onAudioTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
        }

        override fun onVideoTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
        }

        override fun onVideoTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
        }

        override fun onAudioTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
        }
    }

    private var localAudioTrack: LocalAudioTrack? = null
    private var localVideoTrack: LocalVideoTrack? = null

    private val cameraCapturerCompat by lazy {
        CameraCapturerCompat(this, CameraCapturerCompat.Source.FRONT_CAMERA)
    }
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this@VideoActivity)
    }

    /*
     * Audio management
     */
    private val audioSwitch by lazy {
        AudioSwitch(
            applicationContext, preferredDeviceList = listOf(
                AudioDevice.BluetoothHeadset::class.java,
                AudioDevice.WiredHeadset::class.java,
                AudioDevice.Speakerphone::class.java,
                AudioDevice.Earpiece::class.java
            )
        )
    }
    private var savedVolumeControlStream by Delegates.notNull<Int>()
    private var audioDeviceMenuItem: MenuItem? = null

    private var participantIdentity: String? = null
    private lateinit var localVideoView: VideoSink
    private var disconnectedFromOnDestroy = false
    private var isSpeakerPhoneEnabled = true


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            val cameraAndMicPermissionGranted =
                ((PackageManager.PERMISSION_GRANTED == grantResults[CAMERA_PERMISSION_INDEX])
                        and (PackageManager.PERMISSION_GRANTED == grantResults[MIC_PERMISSION_INDEX]))

            audioSwitch.start { audioDevices, audioDevice -> updateAudioDeviceIcon(audioDevice) }

            if (cameraAndMicPermissionGranted) {
                createAudioAndVideoTracks()
            } else {
                Toast.makeText(
                    this,
                    "permissions_needed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    fun connectvideoTrack(flag: Boolean) {
        try {
            localVideoTrack =
                if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
                    createLocalVideoTrack(
                        this,
                        true,
                        cameraCapturerCompat
                    )
                } else {
                    localVideoTrack
                }
            localVideoTrack?.addSink(localVideoView)
            localVideoTrack?.let { localParticipant?.publishTrack(it) }
            localParticipant?.setEncodingParameters(encodingParameters)
            room?.let {
                reconnectingProgressBar.visibility = if (it.state != Room.State.RECONNECTING)
                    View.GONE else
                    View.VISIBLE
                videoStatusTextView.text = "Connected to ${it.name}"
            }

            if (flag) {
                room?.disconnect()
            }
        } catch (e: Exception) {

        }


    }


    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            Log.d("TAG@123", "----.////// broadCastReceiver")

            when (intent?.action) {

                "BROADCAST_FOR_CLOSE_VIDEO" -> onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (view_flag) {
            connectvideoTrack(false)
        }

    }

    override fun onPause() {
        Log.d("TAG@123", "onPause onPause onPause : " + CALL_ACTION)

        CALL_ACTION = "OPEN"
        /*
         * If this local video track is being shared in a Room, remove from local
         * participant before releasing the video track. Participants will be notified that
         * the track has been removed.
         */
        localVideoTrack?.let { localParticipant?.unpublishTrack(it) }

        /*
         * Release the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         */
        localVideoTrack?.release()
        localVideoTrack = null
        view_flag = true
        super.onPause()
    }

    override fun onDestroy() {
        /*
         * Tear down audio management and restore previous volume stream
         */
        audioSwitch.stop()
        volumeControlStream = savedVolumeControlStream

        /*
         * Always disconnect from the room before leaving the Activity to
         * ensure any memory allocated to the Room resource is freed.
         */
        room?.disconnect()
        disconnectedFromOnDestroy = true

        /*
         * Release the local audio and video tracks ensuring any memory allocated to audio
         * or video is freed.
         */
        Home.incoming_call = false
        localAudioTrack?.release()
        localVideoTrack?.release()

        Log.d("TAG@123", "unregister Receiver ")
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(broadCastReceiver)

        super.onDestroy()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        audioDeviceMenuItem = menu.findItem(R.id.menu_audio_device)

        // AudioSwitch has already started and thus notified of the initial selected device
        // so we need to updates the UI
        updateAudioDeviceIcon(audioSwitch.selectedAudioDevice)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            // R.id.menu_audio_device -> showAudioDevices()
        }
        return true
    }


    private fun checkPermissions(permissions: Array<String>): Boolean {
        var shouldCheck = true
        for (permission in permissions) {
            shouldCheck = shouldCheck and (PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(this, permission))
        }
        return shouldCheck
    }

    private fun requestPermissions(permissions: Array<String>) {
        var displayRational = false
        for (permission in permissions) {
            displayRational =
                displayRational or ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permission
                )
        }
        if (displayRational) {
            Toast.makeText(this, "permissions needed", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(
                this, permissions, CAMERA_MIC_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermissionForCameraAndMicrophone(): Boolean {
        return checkPermissions(
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        )
    }

    private fun requestPermissionForCameraMicrophoneAndBluetooth() {
        val permissionsList: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        }
        requestPermissions(permissionsList)
    }

    private fun createAudioAndVideoTracks() {
        localAudioTrack = createLocalAudioTrack(this, true)
        localVideoTrack = createLocalVideoTrack(
            this,
            true,
            cameraCapturerCompat
        )
    }

    private fun setAccessToken() {

        this.accessToken = Home.mVideoGrant_user_token
    }

    private fun connectToRoom(roomName: String, flag: Boolean) {
        Log.d(TAG, "DEFAULT_CONVERSATION_NAME ---$roomName")
        createAudioAndVideoTracks()
        if (flag == true) {
            audioSwitch.activate()
        }
        room = Video.connect(this, accessToken, roomListener) {
            roomName(roomName)
            audioTracks(listOf(localAudioTrack))
            videoTracks(listOf(localVideoTrack))
            preferAudioCodecs(listOf(audioCodec))
            preferVideoCodecs(listOf(videoCodec))
            encodingParameters(encodingParameters)
            enableAutomaticSubscription(enableAutomaticSubscription)
        }
        setDisconnectAction(flag)
    }

    private fun initializeUI() {
        connectActionFab.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_video_call_white_24dp
            )
        )
        connectActionFab.show()
        connectActionFab.setOnClickListener(connectActionClickListener())
        switchCameraActionFab.show()
        switchCameraActionFab.setOnClickListener(switchCameraClickListener())
        localVideoActionFab.show()
        localVideoActionFab.setOnClickListener(localVideoClickListener())
        muteActionFab.show()
        muteActionFab.setOnClickListener(muteClickListener())
    }

    private fun updateAudioDeviceIcon(selectedAudioDevice: AudioDevice?) {
        val audioDeviceMenuIcon = when (selectedAudioDevice) {
            is AudioDevice.BluetoothHeadset -> R.drawable.ic_bluetooth_white_24dp
            is AudioDevice.WiredHeadset -> R.drawable.ic_headset_mic_white_24dp
            is AudioDevice.Speakerphone -> R.drawable.ic_volume_up_white_24dp
            else -> R.drawable.ic_phonelink_ring_white_24dp
        }

        audioDeviceMenuItem?.setIcon(audioDeviceMenuIcon)
    }

    private fun setDisconnectAction(flag: Boolean) {
        connectActionFab.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_call_end_white_24px
            )
        )
        if (!flag) {
            // room?.disconnect()
        }
        connectActionFab.show()
        connectActionFab.setOnClickListener(disconnectClickListener())
    }

    private fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {
        Log.d("TAG@123", "addRemoteParticipant :" + room)
        if (thumbnailVideoView.visibility == View.VISIBLE) {
            return
        }
        participantIdentity = remoteParticipant.identity
        videoStatusTextView.text = "Participant $participantIdentity joined"
        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let { addRemoteParticipantVideo(it) }
            }
        }
        remoteParticipant.setListener(participantListener)
    }


    private fun addRemoteParticipantVideo(videoTrack: VideoTrack) {
        Log.d("TAG@123", "addRemoteParticipantVideo :")
        moveLocalVideoToThumbnailView()
        primaryVideoView.mirror = false
        videoTrack.addSink(primaryVideoView)
    }

    private fun moveLocalVideoToThumbnailView() {
        Log.d("TAG@123", "moveLocalVideoToThumbnailView :")
        if (thumbnailVideoView.visibility == View.GONE) {
            thumbnailVideoView.visibility = View.VISIBLE
            with(localVideoTrack) {
                this?.removeSink(primaryVideoView)
                this?.addSink(thumbnailVideoView)
            }
            localVideoView = thumbnailVideoView
            thumbnailVideoView.mirror = cameraCapturerCompat.cameraSource ==
                    CameraCapturerCompat.Source.FRONT_CAMERA
        }
    }

    /*
     * Called when participant leaves the room
     */
    private fun removeRemoteParticipant(remoteParticipant: RemoteParticipant) {
        videoStatusTextView.text = "Participant $remoteParticipant.identity left."
        if (remoteParticipant.identity != participantIdentity) {
            return
        }
        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let { removeParticipantVideo(it) }
            }
        }
        moveLocalVideoToPrimaryView()
        onBackPressed()
    }

    private fun removeParticipantVideo(videoTrack: VideoTrack) {
        videoTrack.removeSink(primaryVideoView)
    }

    private fun moveLocalVideoToPrimaryView() {
        if (thumbnailVideoView.visibility == View.VISIBLE) {
            thumbnailVideoView.visibility = View.GONE
            with(localVideoTrack) {
                this?.removeSink(thumbnailVideoView)
                this?.addSink(primaryVideoView)
            }
            localVideoView = primaryVideoView
            primaryVideoView.mirror = cameraCapturerCompat.cameraSource ==
                    CameraCapturerCompat.Source.FRONT_CAMERA
        }
    }


    private fun disconnectClickListener(): View.OnClickListener {
        return View.OnClickListener {
            /*
             * Disconnect from room
             */
            room?.disconnect()
            initializeUI()
        }
    }

    private fun connectActionClickListener(): View.OnClickListener {
        return View.OnClickListener {
            onBackPressed()
        }
    }

    private fun cancelConnectDialogClickListener(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ ->
            initializeUI()
            // alertDialog?.dismiss()
        }
    }

    private fun switchCameraClickListener(): View.OnClickListener {
        return View.OnClickListener {
            val cameraSource = cameraCapturerCompat.cameraSource
            cameraCapturerCompat.switchCamera()
            if (thumbnailVideoView.visibility == View.VISIBLE) {
                thumbnailVideoView.mirror = cameraSource == CameraCapturerCompat.Source.BACK_CAMERA
            } else {
                primaryVideoView.mirror = cameraSource == CameraCapturerCompat.Source.BACK_CAMERA
            }
        }
    }

    private fun localVideoClickListener(): View.OnClickListener {
        return View.OnClickListener {
            /*
             * Enable/disable the local video track
             */
            localVideoTrack?.let {
                val enable = !it.isEnabled
                it.enable(enable)
                val icon: Int
                if (enable) {
                    icon = R.drawable.ic_videocam_white_24dp
                    switchCameraActionFab.show()
                } else {
                    icon = R.drawable.ic_videocam_off_black_24dp
                    switchCameraActionFab.hide()
                }
                localVideoActionFab.setImageDrawable(
                    ContextCompat.getDrawable(this@VideoActivity, icon)
                )
            }
        }
    }

    private fun muteClickListener(): View.OnClickListener {
        return View.OnClickListener {
            /*
             * Enable/disable the local audio track. The results of this operation are
             * signaled to other Participants in the same Room. When an audio track is
             * disabled, the audio is muted.
             */
            localAudioTrack?.let {
                val enable = !it.isEnabled
                it.enable(enable)
                val icon = if (enable)
                    R.drawable.ic_mic_white_24dp
                else
                    R.drawable.ic_mic_off_black_24dp
                muteActionFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@VideoActivity, icon
                    )
                )
            }
        }
    }


    /*  private fun createConnectDialog(
          participantEditText: EditText,
          callParticipantsClickListener: DialogInterface.OnClickListener,
          cancelClickListener: DialogInterface.OnClickListener,
          context: Context
      ): AlertDialog {
          val alertDialogBuilder = AlertDialog.Builder(context).apply {
              setIcon(R.drawable.ic_video_call_white_24dp)
              setTitle("Connect to a room")
              setPositiveButton("Connect", callParticipantsClickListener)
              setNegativeButton("Cancel", cancelClickListener)
              setCancelable(false)
          }

        //  setRoomNameFieldInDialog(participantEditText, alertDialogBuilder, context)

          return alertDialogBuilder.create()
      }*/

    /*  @SuppressLint("RestrictedApi")
      private fun setRoomNameFieldInDialog(
          roomNameEditText: EditText,
          alertDialogBuilder: AlertDialog.Builder,
          context: Context
      ) {
          roomNameEditText.hint = "room name"
          val horizontalPadding =
              context.resources.getDimensionPixelOffset(R.dimen.activity_horizontal_margin)
          val verticalPadding =
              context.resourcesDisc.getDimensionPixelOffset(R.dimen.activity_vertical_margin)
          alertDialogBuilder.setView(
              roomNameEditText,
              horizontalPadding,
              verticalPadding,
              horizontalPadding,
              0
          )
      }*/


    fun close() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "user_id",
            user_ID
        )
        jsonObject.addProperty(
            "match_id",
            match_id
        )
        jsonObject.addProperty(
            "type",
            "close_video"
        )
        jsonObject.addProperty(
            "name",
            user_name
        )
        jsonObject.addProperty(
            "image",
            user_images
        )

        Log.d("TAG@123", "video Cut data  Send : -" + jsonObject.toString())
        viewModel.sendChatNotification(jsonObject)
    }


    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}

package com.socio.sociosphere.Messagenew;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.socio.sociosphere.MainActivity;
import com.socio.sociosphere.R;
import com.socio.sociosphere.agora.media.RtcTokenBuilder2;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;

public class CallingScr extends AppCompatActivity {

    private String appId = "b9c50fe3cfa74f00a795cd59ca1ec60b";
    private String appCertificate = "bed1dd0d9d944ac6981dba45f37de13c";
    private int expirationTimeInSeconds = 3600;
    private String channelName; // To store channel name received from the previous activity
    private String userName; // To store user name received from the previous activity
    private String token = null;
    private int uid = 0;
    private boolean isJoined = false;

    private RtcEngine agoraEngine;

    private SurfaceView localSurfaceView;
    private SurfaceView remoteSurfaceView;

    private final int PERMISSION_REQ_ID = 22;
    private final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private boolean checkSelfPermission() {
        return !(ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED);
    }

    private void showMessage(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void setupVideoSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            agoraEngine.enableVideo();
        } catch (Exception e) {
            showMessage(e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling_scr);  // Set the layout

        // Receive intent data (channel name and username)
        channelName = getIntent().getStringExtra("CHANNEL_NAME");
        userName = getIntent().getStringExtra("USER_NAME");

        // Check if the channel name and user name are received
        if (channelName == null || userName == null) {
            showMessage("Error: Channel or User name is missing");
            finish();  // Close the activity if no data was received
            return;
        }

        // Initialize the SurfaceView containers
        FrameLayout localVideoViewContainer = findViewById(R.id.local_video_view_container);
        FrameLayout remoteVideoViewContainer = findViewById(R.id.remote_video_view_container);

        // Create token for the channel
        RtcTokenBuilder2 tokenBuilder = new RtcTokenBuilder2();
        int timestamp = (int) (System.currentTimeMillis() / 1000 + expirationTimeInSeconds);

        token = tokenBuilder.buildTokenWithUid(appId, appCertificate, channelName, uid,
                RtcTokenBuilder2.Role.ROLE_PUBLISHER, timestamp, timestamp);

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        setupVideoSDKEngine();

        Button leavebtn = findViewById(R.id.LeaveButton);


joinChannel();
        leavebtn.setOnClickListener(view -> leaveChannel(view));
        listenForIncomingCallschange();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (agoraEngine != null) {
            agoraEngine.stopPreview();
            agoraEngine.leaveChannel();
            new Thread(() -> {
                RtcEngine.destroy();
                agoraEngine = null;
            }).start();
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(int uid, int elapsed) {
//            showMessage("Remote user joined " + uid);
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
//            showMessage("Joined Channel " + channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
//            showMessage("Remote user offline " + uid + " " + reason);
            runOnUiThread(() -> {
                if (remoteSurfaceView != null) {
                    remoteSurfaceView.setVisibility(View.GONE);
                }
            });
        }
    };

    private void setupRemoteVideo(int uid) {
        remoteSurfaceView = new SurfaceView(getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        // Ensure that the remote SurfaceView is added to the container
        FrameLayout remoteVideoViewContainer = findViewById(R.id.remote_video_view_container);
        remoteVideoViewContainer.addView(remoteSurfaceView);
        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        remoteSurfaceView.setVisibility(View.VISIBLE);
    }

    private void setupLocalVideo() {
        localSurfaceView = new SurfaceView(getBaseContext());
        // Ensure that the local SurfaceView is added to the container
        FrameLayout localVideoViewContainer = findViewById(R.id.local_video_view_container);
        localVideoViewContainer.addView(localSurfaceView);
        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    public void joinChannel() {
        if (checkSelfPermission()) {
            ChannelMediaOptions options = new ChannelMediaOptions();
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            setupLocalVideo();
            localSurfaceView.setVisibility(View.VISIBLE);
            agoraEngine.startPreview();
            agoraEngine.joinChannel(token, channelName, uid, options);
        } else {
            Toast.makeText(getApplicationContext(), "Permissions were not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void leaveChannel(View view) {
        if (!isJoined) {
//            showMessage("Join a channel first");
        } else {
            agoraEngine.leaveChannel();
//            showMessage("You left the channel");
            deleteAllCalls();
            if (remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
            if (localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
            isJoined = false;
        }
    }

    public void deleteAllCalls() {
        // Get a reference to the "calls" directory in the Firebase Realtime Database
        DatabaseReference callsRef = FirebaseDatabase.getInstance().getReference("calls");

        // Remove all calls from the "calls" directory
        callsRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Show success message
//                    showMessage("All calls deleted successfully");
                })
                .addOnFailureListener(e -> {
                    // Show error message if there is an issue
//                    showMessage("Failed to delete calls: " + e.getMessage());
                });
    }


    private void listenForIncomingCallschange() {

        DatabaseReference callsRef = FirebaseDatabase.getInstance().getReference("calls");

        callsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Toast.makeText(CallingScr.this, "Call Ended", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}

package com.socio.sociosphere.Messagenew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socio.sociosphere.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    String receiverId, ReceiverName, senderroom, receiverroom;
    String senderId, SenderName;
    DatabaseReference dbreferenceSender, dbrefererencereciver, userreference;
    TextView sendbtn,chat_rcv_name;
    EditText messagetext;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    Button callbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        userreference = FirebaseDatabase.getInstance().getReference("Users");
        receiverId = getIntent().getStringExtra("uid");
        ReceiverName = getIntent().getStringExtra("name");
        chat_rcv_name=findViewById(R.id.MessageActivity_fullname);
        chat_rcv_name.setText(ReceiverName);

        callbtn=findViewById(R.id.MessageActivity_Callbtn);



//        Toast.makeText(this,ReceiverName,Toast.LENGTH_LONG).show();

        if (receiverId != null) {
            senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            senderroom = senderId + receiverId;
            receiverroom = receiverId + senderId;
        }

        sendbtn = findViewById(R.id.MessageActivity_btn_send);
        messageAdapter = new MessageAdapter(this);  // Corrected variable name here
        recyclerView = findViewById(R.id.MessageActivity_recyclerView);
        messagetext = findViewById(R.id.MessageActivity_text_send);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbreferenceSender = FirebaseDatabase.getInstance().getReference("Chats").child(senderroom);
        dbrefererencereciver = FirebaseDatabase.getInstance().getReference("Chats").child(receiverroom);

        dbreferenceSender.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MessageModel> message = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    message.add(messageModel);
                }
                messageAdapter.clear();
                for (MessageModel messageModel : message) {
                    messageAdapter.add(messageModel);
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Failed to load messages: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messagetext.getText().toString();
                if (message.trim().length() > 0) {
                    sendMessage(message);
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCall(  receiverId,  ReceiverName);
            }
        });
    }

    // Moved sendMessage method outside of onCreate
    private void sendMessage(String message) {
        String messageId = UUID.randomUUID().toString();
        long currentTime = System.currentTimeMillis();
        MessageModel messageModel = new MessageModel(message,FirebaseAuth.getInstance().getCurrentUser().getUid(), messageId,currentTime);
        messageAdapter.add(messageModel);

        dbreferenceSender.child(messageId).setValue(messageModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Message sent successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        dbrefererencereciver.child(messageId).setValue(messageModel);
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        messagetext.setText("");
    }



//    private void startCall(String userName) {
//        // Push the call data to Firebase Realtime Database
//        DatabaseReference callsRef = FirebaseDatabase.getInstance().getReference("calls");
//
//        callsRef.removeValue();
//        // Create a call object (with call initiation details like channelName and callerUid)
//        String callId = callsRef.push().getKey();
//        if (callId != null) {
//            String channelName = "TestChannel"; // Replace with your desired channel name
//            Call call = new Call(channelName, userName, FirebaseAuth.getInstance().getCurrentUser().getUid(),receiverId,ReceiverName, System.currentTimeMillis(), "incoming"); // Set status as incoming
//            callsRef.child(callId).setValue(call);
//
//            // Notify user
//            Toast.makeText(this, "Starting call with " + userName, Toast.LENGTH_SHORT).show();
//
//            // Proceed to the calling screen (Caller)
//            Intent intent = new Intent(ChatActivity.this, CallingScr.class); // TestCallActivity or CallingScr
//            intent.putExtra("CHANNEL_NAME", channelName); // Passing channel name
//            intent.putExtra("USER_NAME", userName); // Passing user name
//            startActivity(intent);
//        }
//    }

    private void startCall( String receiverId, String receiverName) {
        // Reference to Firebase "calls" node
        DatabaseReference callsRef = FirebaseDatabase.getInstance().getReference("calls");

        // Reference to Firebase "users" node
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Get the current user's UID
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // Fetch the current user's name
        usersRef.child(currentUserUid).child("name").get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful() && userTask.getResult().exists()) {
                String currentUserName = userTask.getResult().getValue(String.class);

                // Step 1: Delete all existing calls
                callsRef.removeValue().addOnCompleteListener(deleteTask -> {
                    if (deleteTask.isSuccessful()) {
                        // Step 2: Create and push new call data
                        String callId = callsRef.push().getKey();
                        if (callId == null) {
                            Toast.makeText(this, "Failed to initiate call. Try again.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Define the channel name
                        String channelName = "TestChannel"; // Replace with dynamic logic if needed

                        // Create the Call object
                        Call call = new Call(
                                channelName,
                                currentUserName, // Use fetched current user name
                                currentUserUid,
                                receiverId,
                                receiverName,
                                System.currentTimeMillis(),
                                "incoming"
                        );

                        // Push the call to Firebase
                        callsRef.child(callId).setValue(call).addOnCompleteListener(addTask -> {
                            if (addTask.isSuccessful()) {
                                Toast.makeText(this, "Starting call with " + receiverName, Toast.LENGTH_SHORT).show();

                                // Proceed to the calling screen
                                Intent intent = new Intent(ChatActivity.this, CallingScr.class);
                                intent.putExtra("CHANNEL_NAME", channelName);
                                intent.putExtra("USER_NAME", currentUserName); // Pass current user name
                                intent.putExtra("RECEIVER_ID", receiverId);
                                intent.putExtra("RECEIVER_NAME", receiverName);
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "Failed to start the call. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Failed to clear previous calls. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Failed to fetch user details. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class Call {
        private String channelName;
        private String userName;
        private String callerUid;
        private String receiverId;
        private String receiverName;
        private long timestamp;
        private String status;

        // No-argument constructor
        public Call() {
        }

        // Constructor with parameters
        public Call(String channelName, String userName, String callerUid, String receiverId, String receiverName, long timestamp, String status) {
            this.channelName = channelName;
            this.userName = userName;
            this.callerUid = callerUid;
            this.receiverId = receiverId;
            this.receiverName = receiverName;
            this.timestamp = timestamp;
            this.status = status;
        }

        // Getters and setters
        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCallerUid() {
            return callerUid;
        }

        public void setCallerUid(String callerUid) {
            this.callerUid = callerUid;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }




}

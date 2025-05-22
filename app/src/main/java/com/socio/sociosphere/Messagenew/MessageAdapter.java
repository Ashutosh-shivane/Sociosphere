package com.socio.sociosphere.Messagenew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.socio.sociosphere.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context context;
    private List<MessageModel> messageList;

    public MessageAdapter(Context context) {
        this.context = context;
        this.messageList = new ArrayList<>();
    }

    public void add(MessageModel message) {
        messageList.add(message);
        notifyDataSetChanged();
    }

    public void clear() {
        messageList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate different layouts based on the view type (sent or received)
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = inflater.inflate(R.layout.msg_item_right, parent, false); // Layout for sent messages
            return new MyViewHolder(view, VIEW_TYPE_MESSAGE_SENT);
        } else {
            View view = inflater.inflate(R.layout.msg_item_left, parent, false); // Layout for received messages
            return new MyViewHolder(view, VIEW_TYPE_MESSAGE_RECEIVED);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
        MessageModel message = messageList.get(position);

        // Set the message text based on the sender
        if (getItemViewType(position) == VIEW_TYPE_MESSAGE_SENT) {
            holder.textviewsentmsg.setText(message.getMessage());
        } else {
            holder.textviewreceivedmsg.setText(message.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageList.get(position);
        // Determine if the message is sent or received based on the sender's ID
        if (message.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public List<MessageModel> getmessageList() {
        return messageList;
    }

    // ViewHolder class to handle the views for each message
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textviewsentmsg, textviewreceivedmsg;

        public MyViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            // Initialize the TextViews based on the view type
            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                textviewsentmsg = itemView.findViewById(R.id.showMessagesend);
            } else {
                textviewreceivedmsg = itemView.findViewById(R.id.showMessagercv);
            }
        }
    }
}

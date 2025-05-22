package com.socio.sociosphere.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socio.sociosphere.Adapter.NotificationAdapter;
import com.socio.sociosphere.Model.NotifiationModel;
import com.socio.sociosphere.R;

import java.util.ArrayList;


public class Notifications2Fragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<NotifiationModel> list;
    //NotificationAdapter adapter;


    public Notifications2Fragment() {
        // Required empty public constructor
    }


    public static Notifications2Fragment newInstance() {

        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_notifications2, container, false);

        recyclerView = view.findViewById(R.id.notifications2RV);
        list = new ArrayList<>();
        list.add(new NotifiationModel(R.drawable.profile3, "<b>Amin mentioned</b> you in a comment", "Just now"));
        list.add(new NotifiationModel(R.drawable.profile_cesar, "<b>Caesar</b> liked your post", "2 hours ago"));
        list.add(new NotifiationModel(R.drawable.profile2, "<b>John snow</b> sent you a message", "1 day ago"));
//        list.add(new NotifiationModel(R.drawable.profile3, "<b>Rick Grimes</b> mentioned you in a comment", "Just now"));
//        list.add(new NotifiationModel(R.drawable.profile_cesar, "<b>Caesar</b> mentioned you in story", "1 week ago"));
//        list.add(new NotifiationModel(R.drawable.profile3, "<b>Amin mentioned</b> you in a comment", "Just now"));
//        list.add(new NotifiationModel(R.drawable.profile_cesar, "<b>Caesar</b> liked your post", "2 hours ago"));
//        list.add(new NotifiationModel(R.drawable.profile2, "<b>John snow</b> sent you a message", "1 day ago"));
//        list.add(new NotifiationModel(R.drawable.profile3, "<b>Rick Grimes</b> mentioned you in a comment", "Just now"));
//        list.add(new NotifiationModel(R.drawable.profile_cesar, "<b>Caesar</b> mentioned you in story", "1 week ago"));
//        list.add(new NotifiationModel(R.drawable.profile3, "<b>Amin mentioned</b> you in a comment", "Just now"));
//        list.add(new NotifiationModel(R.drawable.profile_cesar, "<b>Caesar</b> liked your post", "2 hours ago"));
//        list.add(new NotifiationModel(R.drawable.profile2, "<b>John snow</b> sent you a message", "1 day ago"));
//        list.add(new NotifiationModel(R.drawable.profile3, "<b>Rick Grimes</b> mentioned you in a comment", "Just now"));
//        list.add(new NotifiationModel(R.drawable.profile_cesar, "<b>Caesar</b> mentioned you in story", "1 week ago"));


        NotificationAdapter adapter = new NotificationAdapter(list, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        return view;
    }
}
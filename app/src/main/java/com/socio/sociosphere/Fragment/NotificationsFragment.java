package com.socio.sociosphere.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.socio.sociosphere.Adapter.ViewPagerAdapter;
import com.socio.sociosphere.R;

public class NotificationsFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        // Add any arguments to the bundle if needed
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle any arguments passed to the fragment here
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        // Now this line will work correctly since the constructor accepts 2 parameters
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), 2)); // 2 is the number of tabs

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}

package com.socio.sociosphere.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.socio.sociosphere.Fragment.Notifications2Fragment;
import com.socio.sociosphere.Fragment.RequestFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Return the Fragment corresponding to the position

        switch(position){
            case 0: return new Notifications2Fragment();
            case 1 : return new RequestFragment();
            default: return new Notifications2Fragment();
        }

    }

    @Override
    public int getCount() {

        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = null;
        if(position == 0){
            title = "Notifications";
        }
        else if(position == 1){
            title = "Requests";
        }

        return title;
    }


}
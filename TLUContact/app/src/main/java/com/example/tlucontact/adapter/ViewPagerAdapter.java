package com.example.tlucontact.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tlucontact.fragment.CBNVFragment;
import com.example.tlucontact.fragment.DonViFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private Fragment[] fragments = new Fragment[2];

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                fragments[0] = new DonViFragment();
                return fragments[0];
            case 1:
                fragments[1] = new CBNVFragment();
                return fragments[1];
            default:
                return new DonViFragment();
        }
    }

    public Fragment getFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

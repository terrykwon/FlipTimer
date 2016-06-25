package com.terrykwon.fliptimer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.terrykwon.fliptimer.fragments.ChartFragment;
import com.terrykwon.fliptimer.fragments.RecordFragment;
import com.terrykwon.fliptimer.fragments.TimerFragment;

/**
 * A FragmentPagerAdapter for the ViewPager.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Don't need to break because return.
                return new ChartFragment();
            case 1:
                return new TimerFragment();
            case 2:
                return new RecordFragment();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}

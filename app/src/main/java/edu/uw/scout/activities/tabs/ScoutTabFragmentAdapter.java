package edu.uw.scout.activities.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by ezturner on 8/26/16.
 */
// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class ScoutTabFragmentAdapter extends FragmentStatePagerAdapter {

    private String[] scoutTabs;

    public ScoutTabFragmentAdapter(FragmentManager fragmentManager, String[] scoutTabs) {
        super(fragmentManager);
        this.scoutTabs = scoutTabs;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new ScoutTabFragment();
        Bundle args = new Bundle();
        args.putInt(ScoutTabFragment.TAB_ID, i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return scoutTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position >= 0 && position < scoutTabs.length) {
            return scoutTabs[position];
        } else {
            return "Scout Tab";
        }
    }
}
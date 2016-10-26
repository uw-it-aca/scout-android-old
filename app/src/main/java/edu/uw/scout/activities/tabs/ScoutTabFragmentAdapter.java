package edu.uw.scout.activities.tabs;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * This adapter handles the tab instances for MainActivity.
 */
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
        return "";
    }
}
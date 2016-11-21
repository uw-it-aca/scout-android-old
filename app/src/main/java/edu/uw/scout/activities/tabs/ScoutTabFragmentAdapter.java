package edu.uw.scout.activities.tabs;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter handles the tab instances for MainActivity.
 */
public class ScoutTabFragmentAdapter extends FragmentStatePagerAdapter {

    private String[] scoutTabs;
    private List<Fragment> fragmentList;

    public ScoutTabFragmentAdapter(FragmentManager fragmentManager, String[] scoutTabs) {
        super(fragmentManager);
        this.scoutTabs = scoutTabs;
        this.fragmentList = new ArrayList<>();
        for (int i = 0; i < scoutTabs.length; i++){
            Fragment fragment = new ScoutTabFragment();
            Bundle args = new Bundle();
            args.putInt(ScoutTabFragment.TAB_ID, i);
            fragment.setArguments(args);
            fragmentList.add(fragment);
        }
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
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
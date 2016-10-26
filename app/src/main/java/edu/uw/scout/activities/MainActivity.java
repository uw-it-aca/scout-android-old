package edu.uw.scout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uw.scout.R;
import edu.uw.scout.activities.tabs.ScoutTabFragment;
import edu.uw.scout.activities.tabs.ScoutTabFragmentAdapter;
import edu.uw.scout.utils.UserPreferences;

public class MainActivity extends ScoutActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String CAMPUS_INDEX = "campus";
    private static final String TAB_POSITION = "tabPos";
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindArray(R.array.scout_tabs) String[] scoutTabs;

    private int campusIndex = -1;
    private int tabPosition = -1;
    private ScoutTabFragmentAdapter scoutTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey(CAMPUS_INDEX))
                campusIndex = savedInstanceState.getInt(CAMPUS_INDEX);


            if (savedInstanceState.containsKey(TAB_POSITION))
                tabPosition = savedInstanceState.getInt(TAB_POSITION);
        } else {
            Log.d(LOG_TAG, "No savedInstanceState!");
        }

        scoutTabAdapter = new ScoutTabFragmentAdapter(getSupportFragmentManager(), scoutTabs);
        viewPager.setAdapter(scoutTabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        try {
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_white_24dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_restaurant_white_24dp);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_local_library_white_24dp);
            tabLayout.getTabAt(3).setIcon(R.drawable.ic_computer_white_24dp);
        } catch (NullPointerException e){
            Log.d(LOG_TAG , "Tab missing!");
        }

        Log.d(LOG_TAG , "onCreate Called");

        if(!userPreferences.hasUserOpenedApp())
            showCampusChooser();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putInt(CAMPUS_INDEX, UserPreferences.getInstance().getCampusSelectedIndex());
        outState.putInt(TAB_POSITION, viewPager.getCurrentItem());
        Log.d(LOG_TAG, "Saving instance state!");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "Pausing view!");
        campusIndex = UserPreferences.getInstance().getCampusSelectedIndex();
        tabPosition = viewPager.getCurrentItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Resuming! " + campusIndex + " ,  " + tabPosition);

        if(campusIndex != -1) {
            if(campusIndex != UserPreferences.getInstance().getCampusSelectedIndex()) {
                for (int i = 0; i < scoutTabAdapter.getCount(); i++) {
                    ScoutTabFragment fragment = (ScoutTabFragment) scoutTabAdapter.getItem(i);

                    if (fragment.getActivity() != null)
                        fragment.reloadTab();
                }
            }
        }

        if(tabPosition != -1)
            viewPager.setCurrentItem(tabPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(CONSTANTS.INTENT_URL_KEY, location);
        this.startActivity(intent);
    }

    /**
     * Shows a MaterialDialog that allows the user to select a campus
     */
    private void showCampusChooser(){
        int campusIndexSelected = userPreferences.getCampusSelectedIndex();
        new MaterialDialog.Builder(this)
                .title(R.string.choose_campus)
                .items(R.array.campus)
                .itemsCallbackSingleChoice(campusIndexSelected, campusChoiceCallback)
                .show();
    }

    private MaterialDialog.ListCallbackSingleChoice campusChoiceCallback =
            new MaterialDialog.ListCallbackSingleChoice() {

        @Override
        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
            if(which != -1) {
                userPreferences.setCampusByIndex(which);
                onPause();
                onResume();
            }

            dialog.dismiss();
            return true;
        }
    };

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(LOG_TAG , "Stopping!");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(LOG_TAG , "onRestart");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG , "destroyed");
    }


}

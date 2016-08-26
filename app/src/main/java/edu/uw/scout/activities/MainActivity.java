package edu.uw.scout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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

public class MainActivity extends ScoutActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindArray(R.array.scout_tabs) String[] scoutTabs;

    private ScoutTabFragmentAdapter scoutTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        scoutTabAdapter = new ScoutTabFragmentAdapter(getSupportFragmentManager(), scoutTabs);
        viewPager.setAdapter(scoutTabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        showCampusChooser();

    }

    @Override
    protected void onResume() {
        super.onResume();

        reloadTabs();
    }

    private void reloadTabs(){

        for(int i = 0; i < scoutTabAdapter.getCount(); i++){
            ScoutTabFragment fragment = (ScoutTabFragment) scoutTabAdapter.getItem(i);

            if(fragment.getActivity() != null)
                fragment.reloadTab();
        }
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
        intent.putExtra(INTENT_URL_KEY, location);
        this.startActivity(intent);
    }


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



}

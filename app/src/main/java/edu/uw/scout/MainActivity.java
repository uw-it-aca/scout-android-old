package edu.uw.scout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import java.util.ArrayList;

import butterknife.BindArray;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements TurbolinksAdapter {


    private static final String BASE_URL = "http://curry.aca.uw.edu:8001/h/seattle/";
    private static final String INTENT_URL = "intentUrl";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String location;
    private ViewPager viewPager;
    private TurbolinksView turbolinksView;
    private TabLayout tabLayout;
    @BindArray(R.array.scout_tabs) String[] scoutTabs;
    DemoCollectionPagerAdapter demoCollectionPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        viewPager = (ViewPager) findViewById(R.id.viewPager);


        demoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        viewPager.setAdapter(demoCollectionPagerAdapter);


        tabLayout.setupWithViewPager(viewPager);
        /*
        TurbolinksSession.getDefault(this).setDebugLoggingEnabled(true);

        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        location = getIntent().getStringExtra(INTENT_URL) != null ? getIntent().getStringExtra(INTENT_URL) : BASE_URL;

        TurbolinksSession.getDefault(this).progressView(LayoutInflater.from(this).inflate(com.basecamp.turbolinks.R.layout.turbolinks_progress, turbolinksView, false), com.basecamp.turbolinks.R.id.turbolinks_default_progress_indicator,Integer.MAX_VALUE)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // Since the webView is shared between activities, we need to tell Turbolinks
        // to load the location from the previous activity upon restarting
        /*
        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .restoreWithCachedSnapshot(true)
                .view(turbolinksView)
                .visit(location);*/
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageFinished() {

    }

    @Override
    public void onReceivedError(int errorCode) {

    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {

    }

    @Override
    public void visitCompleted() {
        Log.d(LOG_TAG , turbolinksView.toString());
    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_URL, location);
        this.startActivity(intent);
    }


    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            String tabURL = URLUtils.getTabURL(getApplicationContext(), i);
            args.putString(DemoObjectFragment.URL, tabURL);
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

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class DemoObjectFragment extends Fragment implements TurbolinksAdapter{
        public static final String URL = "turbolinks_url";
        private TurbolinksView turbolinksView;
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_collection_object, container, false);
            turbolinksView = (TurbolinksView) rootView.findViewById(R.id.turbolinks_view);

            TurbolinksSession.getNew(getContext())
                    .activity(getActivity())
                    .adapter(this)
                    .view(turbolinksView)
                    .visit(getArguments().getString(URL, BASE_URL));

            return rootView;
        }

        @Override
        public void onPageFinished() {

        }

        @Override
        public void onReceivedError(int errorCode) {

        }

        @Override
        public void pageInvalidated() {

        }

        @Override
        public void requestFailedWithStatusCode(int statusCode) {

        }

        @Override
        public void visitCompleted() {
            Log.d(LOG_TAG , turbolinksView.toString());
        }

        @Override
        public void visitProposedToLocationWithAction(String location, String action) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(INTENT_URL, location);
            this.startActivity(intent);
        }

    }
}

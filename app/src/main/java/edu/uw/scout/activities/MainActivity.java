package edu.uw.scout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import butterknife.BindArray;
import butterknife.ButterKnife;
import edu.uw.scout.R;
import edu.uw.scout.utils.URLUtils;

public class MainActivity extends AppCompatActivity implements TurbolinksAdapter {


    private static final String INTENT_URL = "intentUrl";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String location;
    private ViewPager viewPager;
    private TurbolinksView turbolinksView;
    private TabLayout tabLayout;
    @BindArray(R.array.scout_tabs) String[] scoutTabs;
    ScoutViewAdapter demoCollectionPagerAdapter;

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
                new ScoutViewAdapter(
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
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG , "onResume!");
        for(int i = 0; i < viewPager.getAdapter().getCount(); i++){
            ScoutViewFragment fragment = (ScoutViewFragment) ((ScoutViewAdapter) viewPager.getAdapter()).getItem(i);

            if(fragment.getContext() != null)
                fragment.onResume();
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
    public class ScoutViewAdapter extends FragmentStatePagerAdapter {

        public ScoutViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new ScoutViewFragment();
            Bundle args = new Bundle();
            args.putInt(ScoutViewFragment.TAB_ID, i);
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
    public static class ScoutViewFragment extends Fragment implements TurbolinksAdapter{
        public static final String TAB_ID = "tab_id";
        private TurbolinksView turbolinksView;
        private TurbolinksSession turbolinksSession;

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_collection_object, container, false);
            turbolinksView = (TurbolinksView) rootView.findViewById(R.id.turbolinks_view);
            turbolinksSession = TurbolinksSession.getNew(getContext());

            return rootView;
        }

        @Override
        public void onResume(){
            super.onResume();

            int tabId = getArguments().getInt(TAB_ID);
            String tabURL = URLUtils.getTabURL(getContext() , tabId);

            Log.d(LOG_TAG , "Tab URL : " + tabURL);

            turbolinksSession.activity(getActivity())
                    .adapter(this)
                    .view(turbolinksView)
                    .visit(tabURL);
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
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(INTENT_URL, location);
            this.startActivity(intent);
        }

    }
}

package edu.uw.scout.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uw.scout.R;
import edu.uw.scout.activities.tabs.ScoutTabFragment;
import edu.uw.scout.activities.tabs.ScoutTabFragmentAdapter;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;


public class MainActivity extends ScoutActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String CAMPUS_INDEX = "campus";
    private static final String TAB_POSITION = "tabPos";
    private static final int LOCATION_REQ_CODE = 1592;

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindArray(R.array.scout_tabs) String[] scoutTabs;
    @BindString(R.string.help_email) String helpEmail;
    @BindString(R.string.help_subject) String helpSubject;
    @BindArray(R.array.scout_tab_titles) String[] scoutTabTitles;

    private GoogleApiClient googleApiClient;
    private int campusIndex = -1;
    private int tabPosition = -1;
    private ScoutTabFragmentAdapter scoutTabAdapter;
    private Menu menu;
    private Handler handler;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        handler = new Handler();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CAMPUS_INDEX))
                campusIndex = savedInstanceState.getInt(CAMPUS_INDEX);


            if (savedInstanceState.containsKey(TAB_POSITION))
                tabPosition = savedInstanceState.getInt(TAB_POSITION);
        }

        scoutTabAdapter = new ScoutTabFragmentAdapter(getSupportFragmentManager(), scoutTabs);
        viewPager.setAdapter(scoutTabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(onTabSelectedListener);

        // Set the tab icons
        try {
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_white_24dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_restaurant_white_24dp);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_local_library_white_24dp);
            tabLayout.getTabAt(3).setIcon(R.drawable.ic_computer_white_24dp);
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "Tab missing!");
        }

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

        switchTabs(0);
        changeTitle(0);

        // If the user has not opened the app, show the campus chooser.
        if (!userPreferences.hasUserOpenedApp()) {
            showCampusChooser();
        } else {
            campusIndex = userPreferences.getCampusSelectedIndex();
        }

        // If we are on discover, hide the filter button
        handler.postDelayed(hideFilterIcon, 50);
    }

    private Runnable hideFilterIcon = new Runnable() {
        @Override
        public void run() {
            if (menu != null && tabLayout.getSelectedTabPosition() == 0) {
                setIconVisible(false, 1);
            } else if (menu == null) {
                handler.postDelayed(hideFilterIcon, 50);
            }
        }
    };


    /**
     * This onTabSelectedListener changes the icon colors on tab movement
     */
    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switchTabs(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    /**
     * Change the ActionBar title depending upon which tab we have selected
     * @param position the index of the tab selected
     */
    private void changeTitle(int position) {
        if (position < 0 || position >= scoutTabTitles.length)
            throw new IllegalArgumentException("Tab index is outside of the scout tab titles array!");

        String title = scoutTabTitles[position];
        getSupportActionBar().setTitle(title);
    }

    /**
     * Switches the colors of the tabs to reflect which is selected
     * @param tabSelected
     */
    private void switchTabs(int tabSelected) {
        // Set the action bar icon to visible
        if (tabSelected == 0) {
            setIconVisible(true, 0);
            setIconVisible(false, 1);
        } else {
            setIconVisible(false, 0);
            setIconVisible(true, 1);
        }

        try {
            int tabIconColor = ContextCompat.getColor(this, R.color.unselectedIcon);
            int white = ContextCompat.getColor(this, R.color.white);
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                if (i == tabSelected) {
                    tabLayout.getTabAt(i).getIcon().setColorFilter(white, PorterDuff.Mode.SRC_IN);
                } else {
                    tabLayout.getTabAt(i).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Null Pointer Exception thrown when altering tab icons!");
        }

        // change the title
        changeTitle(tabSelected);
    }

    private void setIconVisible(boolean isVisible, int index) {
        if (menu != null && menu.getItem(index) != null)
            menu.getItem(index).setVisible(isVisible);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(CAMPUS_INDEX, userPreferences.getCampusSelectedIndex());
        outState.putInt(TAB_POSITION, viewPager.getCurrentItem());
    }

    @Override
    protected void onPause() {
        super.onPause();
        campusIndex = userPreferences.getCampusSelectedIndex();
        tabPosition = viewPager.getCurrentItem();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (campusIndex != -1) {
            if (campusIndex != userPreferences.getCampusSelectedIndex()) {
                for (int i = 0; i < scoutTabAdapter.getCount(); i++) {
                    ScoutTabFragment fragment = (ScoutTabFragment) scoutTabAdapter.getItem(i);

                    if (fragment.getActivity() != null)
                        fragment.reloadTab();
                }
            }
        }

        if (tabPosition != -1)
            viewPager.setCurrentItem(tabPosition);
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing())
            userPreferences.deleteFilters();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
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
        } else if (id == R.id.action_filter) {
            Intent filterIntent = new Intent(this, FilterActivity.class);
            filterIntent.putExtra(CONSTANTS.INTENT_URL_KEY, getFilterURL());
            filterIntent.putExtra(CONSTANTS.FILTER_TYPE_KEY, tabLayout.getSelectedTabPosition());
            startActivity(filterIntent);
        } else if (id == R.id.action_help) {
            String mailto = "mailto:" + helpEmail + "?subject=" + helpSubject;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mailto));
            startActivity(browserIntent);
        } else if (id == R.id.action_campus) {
            showCampusChooser();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        if (location.contains("?")) {
            Intent intent = new Intent(this, DiscoverCardActivity.class);
            intent.putExtra(CONSTANTS.INTENT_URL_KEY, location);
            this.startActivity(intent);
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(CONSTANTS.INTENT_URL_KEY, location);
            this.startActivity(intent);
        }
    }

    /**
     * Shows a MaterialDialog that allows the user to select a campus
     */
    private void showCampusChooser() {
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
                    if (which != -1) {
                        if (campusIndex != which) {
                            campusIndex = which;
                            userPreferences.setCampusByIndex(which);
                            onPause();
                            onResume();
                        }
                    }

                    dialog.dismiss();
                    return true;
                }
            };

    public String getFilterURL() {
        String campusURL = userPreferences.getCampusURL();
        switch (tabLayout.getSelectedTabPosition()) {
            case 1:
                return campusURL + "food/filter/";
            case 2:
                return campusURL + "study/filter/";
            case 3:
                return campusURL + "tech/filter/";
        }
        return campusURL + "study/filter/";
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, LOCATION_REQ_CODE);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
            if (lastLocation != null) {
                passLocation(lastLocation);
            } else {
                startLocationUpdates();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Pass the lastLocation to the turbolinks view of the current tab
     * @param location - the current location of the device
     */
    private void passLocation(Location location){
        ScoutTabFragment tab = (ScoutTabFragment) scoutTabAdapter.getItem(tabLayout.getSelectedTabPosition());
        TurbolinksSession session = tab.getTurbolinksSession();
        currentLocation = location;
        if(session == null){
            handler.postDelayed(evaluateJavascript, 1500);
        } else {
            handler.post(evaluateJavascript);
        }
    }

    private Runnable evaluateJavascript = new Runnable() {
        @Override
        public void run() {
            ScoutTabFragment tab = (ScoutTabFragment) scoutTabAdapter.getItem(tabLayout.getSelectedTabPosition());
            TurbolinksSession session = tab.getTurbolinksSession();
            if(session == null){
                handler.postDelayed(this, 50);
                return;
            }
            WebView view = session.getWebView();
            String setLocation = embedLocationInJavascript(currentLocation);
            String locationEnabled = "Geolocation.set_is_using_location(true);";

            Log.d(LOG_TAG, setLocation);

            view.evaluateJavascript(locationEnabled, null);
            view.evaluateJavascript(setLocation, null);
        }
    };

    private String embedLocationInJavascript(Location location){
        String js = "Geolocation.send_client_location(";

        js += location.getLatitude() + "," + location.getLongitude() + ");";

        return js;
    }

    private LocationRequest requestLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        return locationRequest;
    }

    private void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, requestLocation(), this);
        } catch (SecurityException e){
            e.printStackTrace();
            Log.d(LOG_TAG , "Requires additional permissions!");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        passLocation(location);
    }

    private void stopLocationUpdates() {
        if(googleApiClient != null && googleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                              int[] grantResults){
        switch(requestCode){
            case LOCATION_REQ_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
            }
    }

}

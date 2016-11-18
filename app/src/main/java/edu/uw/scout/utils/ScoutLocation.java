package edu.uw.scout.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.WebView;

import com.basecamp.turbolinks.TurbolinksSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.util.ArrayList;
import java.util.List;

import edu.uw.scout.Scout;
import edu.uw.scout.activities.tabs.ScoutTabFragment;

/**
 * Created by ezturner on 11/18/16.
 */

public class ScoutLocation implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private static final String LOG_TAG = ScoutLocation.class.getSimpleName();
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private Handler handler;
    private List<WebView> toUpdate;
    private static ScoutLocation instance;

    public static ScoutLocation getInstance(){
        return instance;
    }

    public ScoutLocation(){
        // Create an instance of GoogleAPIClient.
        googleApiClient = new GoogleApiClient.Builder(Scout.getInstance().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

        handler = new Handler();

        toUpdate = new ArrayList<>();

        instance = this;
    }
    /**
     * Begin querying the user's location
     */
    private void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, getLocationRequest(), this);
        } catch (SecurityException e){
            e.printStackTrace();
            Log.d(LOG_TAG , "Requires additional permissions!");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "Location: " + location);
        currentLocation = location;
        stopLocationUpdates();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(Scout.getInstance().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Scout.getInstance().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "onConnected called");
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
            if(currentLocation == null)
                startLocationUpdates();
        }
    }

    /**
     * Stop querying the user's location
     */
    private void stopLocationUpdates() {
        if(googleApiClient != null && googleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
    }

    public void start(){
        googleApiClient.connect();
    }

    public void stop(){
        googleApiClient.disconnect();
    }

    /**
     * Pass the lastLocation to the turbolinks view of the current tab
     * @param view - the WebView involved
     */
    public void passLocation(WebView view){
        if(currentLocation == null) {
            Log.d(LOG_TAG, "CurrentLocation is null!");
            return;
        }

        String setLocation = embedLocationInJavascript(currentLocation);
        String isUsingLocation = "Geolocation.set_is_using_location(true);";

        Log.d(LOG_TAG, setLocation);

        view.evaluateJavascript(isUsingLocation, null);
        view.evaluateJavascript(setLocation, null);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Provides a locaiton request that can be used to query the user's location
     * @return
     */
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        return locationRequest;
    }

    private String embedLocationInJavascript(Location location){
        String js = "Geolocation.send_client_location(";

        js += location.getLatitude() + "," + location.getLongitude() + ");";

        return js;
    }

    private Runnable setLocation = new Runnable() {
        @Override
        public void run() {
            if(currentLocation == null){
                handler.postDelayed(this, 150);
            } else {
                for(WebView view : toUpdate){
                    passLocation(view);
                }
            }

        }
    };

}

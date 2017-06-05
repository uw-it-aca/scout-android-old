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

    public ScoutLocation(Context context){

        if(ScoutLocation.hasPermissions(context)) {
            createGoogleClient(context);
            // Create an instance of GoogleAPIClient.
        } else {
            Log.e(LOG_TAG, "No permissions!");
        }

        handler = new Handler();

        toUpdate = new ArrayList<>();

        instance = this;
    }

    public void permissionGranted(Context context){
        createGoogleClient(context);
    }

    /**
     * Creates and connects a GoogleApiClient for the purpose of querying the user location
     * @param context an Android Context
     */
    private void createGoogleClient(Context context){
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    /**
     * Begin querying the user's location
     */
    private void startLocationUpdates() {
        Log.e(LOG_TAG, "Starting location updates!");
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, getLocationRequest(), this);
        } catch (SecurityException e){
            e.printStackTrace();
            Log.d(LOG_TAG , "Requires additional permissions!");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(LOG_TAG, "Location: " + location);
        currentLocation = location;

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
        locationRequest.setInterval(45 * 1000);
        locationRequest.setFastestInterval(30 * 1000);
        locationRequest.setSmallestDisplacement(46);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        return locationRequest;
    }

    /**
     * Returns the user's location formatted for the scout web application, with h_lat and h_lng.
     * If we do not currently have the user's location, returns an empty string
     * @return locationParams
     */
    public String getLocationParams(){
        if(currentLocation == null) {
            Log.e(LOG_TAG, "LocationParams error!");
            return "";
        }

        String locationParams = "";
        locationParams += "?h_lat=" + currentLocation.getLatitude();
        locationParams += "&h_lng=" + currentLocation.getLongitude();

        return locationParams;
    }

    public boolean hasGoogleClient(){
        return googleApiClient == null;
    }

    public static boolean hasPermissions(Context context){
        return !(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Scout.getInstance().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }
}
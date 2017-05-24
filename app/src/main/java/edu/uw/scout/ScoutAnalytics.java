package edu.uw.scout;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import edu.uw.scout.utils.UserPreferences;

/**
 * This is the analytics class that will handle all analytics events and ensure that we do not
 * track users who are opted out.
 * Created by ezturner on 12/9/16.
 */

public class ScoutAnalytics {

    private static ScoutAnalytics instance;

    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    public static ScoutAnalytics getInstance(){
        return instance;

    }


    public ScoutAnalytics(final Context context){
        instance = this;
        analytics = GoogleAnalytics.getInstance(context);

        // Check whether the Analytics settings match the user's preferences, and set the opt out if not
        boolean isOptedOut = analytics.getAppOptOut();
        if(isOptedOut != isOptedOut()){
            analytics.setAppOptOut(isOptedOut());
        }

        SharedPreferences userPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        userPrefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener () {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(context.getResources().getString(R.string.pref_analytics_key))) {
                    GoogleAnalytics.getInstance(context.getApplicationContext()).setAppOptOut(sharedPreferences.getBoolean(key, false));
                }
            }
        });


    }

    /**
     * Gets the default {@link Tracker} for Scout.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (tracker == null) {
            tracker = analytics.newTracker(R.xml.global_tracker);
        }

        return tracker;
    }

    private boolean isOptedOut(){
        return Scout.getInstance().getPreferences().isOptedOut();
    }

}

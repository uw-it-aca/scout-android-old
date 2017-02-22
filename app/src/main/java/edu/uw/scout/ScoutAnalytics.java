package edu.uw.scout;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import edu.uw.scout.utils.UserPreferences;

/**
 * This is the analytics class that will handle all analytics events and ensure that we do not
 * track users who are opted out.
 * Created by ezturner on 12/9/16.
 */

public class ScoutAnalytics {

    private static ScoutAnalytics instance;

    public static ScoutAnalytics getInstance(){
        return instance;
    }

    private FirebaseAnalytics firebaseAnalytics;

    public ScoutAnalytics(Context context){
        instance = this;
        if(!isOptedOut())
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    private boolean isOptedOut(){
        return Scout.getInstance().getPreferences().isOptedOut();
    }


}

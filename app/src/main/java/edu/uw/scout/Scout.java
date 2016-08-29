package edu.uw.scout;

import android.app.Application;

import edu.uw.scout.utils.UserPreferences;

/**
 * Created by ezturner on 8/23/16.
 */
public class Scout extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        new UserPreferences(getApplicationContext());
    }
}

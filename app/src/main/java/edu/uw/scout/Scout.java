package edu.uw.scout;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.basecamp.turbolinks.TurbolinksSession;

import edu.uw.scout.services.TurbolinksSessionManager;
import edu.uw.scout.utils.ScoutLocation;
import edu.uw.scout.utils.UserPreferences;

/**
 * Created by ezturner on 8/23/16.
 */
public class Scout extends Application {

    private static final String LOG_TAG = Scout.class.getSimpleName();
    private static Scout instance;

    public static Scout getInstance(){
        return instance;
    }

    private UserPreferences userPreferences;
    private TurbolinksSessionManager sessionManager;
    private ScoutAnalytics scoutAnalytics;
    private ScoutLocation scoutLocation;

    @Override
    public void onCreate(){
        super.onCreate();
        new UserPreferences(getApplicationContext());
        instance = this;
        sessionManager = new TurbolinksSessionManager();
        userPreferences = new UserPreferences(this);

        scoutAnalytics = ScoutAnalytics.getInstance();
        if(scoutAnalytics == null)
            scoutAnalytics = new ScoutAnalytics(this);

        scoutLocation = new ScoutLocation(getApplicationContext());
    }

    public UserPreferences getPreferences(){
        return userPreferences;
    }

    public TurbolinksSessionManager getTurbolinksManager(){
        return sessionManager;
    }

}

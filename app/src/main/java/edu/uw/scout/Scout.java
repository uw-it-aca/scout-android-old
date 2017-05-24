package edu.uw.scout;

import android.app.Application;

import com.basecamp.turbolinks.TurbolinksSession;

import edu.uw.scout.services.TurbolinksSessionManager;
import edu.uw.scout.utils.UserPreferences;

/**
 * Created by ezturner on 8/23/16.
 */
public class Scout extends Application {

    private static Scout instance;

    public static Scout getInstance(){
        return instance;
    }

    private UserPreferences userPreferences;
    private TurbolinksSessionManager sessionManager;
    private ScoutAnalytics scoutAnalytics;

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

    }

    public UserPreferences getPreferences(){
        return userPreferences;
    }

    public TurbolinksSessionManager getTurbolinksManager(){
        return sessionManager;
    }

}

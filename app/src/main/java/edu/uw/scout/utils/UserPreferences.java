package edu.uw.scout.utils;

import android.content.Context;
import android.util.Log;

import edu.uw.scout.R;

/**
 * Created by ezturner on 8/26/16.
 */
public class UserPreferences {

    private static final String LOG_TAG = UserPreferences.class.getSimpleName();
    private static UserPreferences instance;

    public static UserPreferences getInstance(){
        return instance;
    }

    private Context applicationContext;

    public UserPreferences(Context context){
        instance = this;
        applicationContext = context;
        Log.d(LOG_TAG , "UserPreferences created");
    }

    public String getCampusURL(){
        String baseURL = applicationContext.getResources().getString(R.string.scout_url);

        String campus = PrefUtils.getFromPrefs(applicationContext, PrefUtils.PREF_CAMPUS, "seattle");
        campus += "/";
        campus = campus.toLowerCase();

        return baseURL + campus;
    }

    public void setCampus(String campus){
        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_CAMPUS, campus);
    }

    public void setCampusByIndex(int index){
        String[] campuses = applicationContext.getResources().getStringArray(R.array.campus);
        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_CAMPUS, campuses[index]);
    }

    public int getCampusSelectedIndex(){
        String campus = PrefUtils.getFromPrefs(applicationContext, PrefUtils.PREF_CAMPUS, "seattle");

        String[] campuses = applicationContext.getResources().getStringArray(R.array.campus);

        int index = 0;

        for(String aCampus : campuses){
            if(campus.equals(aCampus))
                return index;
            index++;
        }

        return -1;
    }

    public String getTabURL(int tab){
        String[] tabs = applicationContext.getResources().getStringArray(R.array.scout_tab_urls);
        return getCampusURL() + tabs[tab];
    }

    public boolean hasUserOpenedApp(){
        return PrefUtils.getFromPrefs(applicationContext, PrefUtils.PREF_HAS_OPENED_APP_KEY, false);
    }
}

package edu.uw.scout.utils;

import android.content.Context;
import android.util.Log;

import edu.uw.scout.R;

/**
 * This class manages user preferences, notably surrounding the campus, campus URL and whether
 * the user has opened the app before.
 */
public class UserPreferences {

    private static final String LOG_TAG = UserPreferences.class.getSimpleName();

    private Context applicationContext;

    public UserPreferences(Context context){
        applicationContext = context;
    }

    /**
     * Retrieves the base url of the campus.
     * @return the campus url
     */
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

    /**
     * Sets the campus by the index of the String array campus
     */
    public void setCampusByIndex(int index){
        String[] campuses = applicationContext.getResources().getStringArray(R.array.pref_campus_list_values);
        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_CAMPUS, campuses[index]);
    }

    /**
     * Retrieves the index of the campus selected in terms of the campus string aray
     */
    public int getCampusSelectedIndex(){
        String campus = PrefUtils.getFromPrefs(applicationContext, PrefUtils.PREF_CAMPUS, "seattle");

        String[] campuses = applicationContext.getResources().getStringArray(R.array.pref_campus_list_values);

        int index = 0;

        for(String aCampus : campuses){
            if(campus.equals(aCampus))
                return index;
            index++;
        }

        return -1;
    }

    /**
     * Retreives the URL for a given tab index on the MainActivity
     * @param tab
     * @return
     */
    public String getTabURL(int tab){
        String[] tabs = applicationContext.getResources().getStringArray(R.array.scout_tab_urls);

        String url = getCampusURL() + tabs[tab];
        String params = getFilterParams(tab);

        if(!params.equals(""))
            url += "?" + params;

        return url;
    }

    private String getFilterParams(int tab){
        switch (tab){
            case 1:
                return getFoodFilter();
            case 2:
                return getStudyFilter();
            case 3:
                return getTechFilter();
        }
        return "";
    }
    /**
     * Return whether the user has opened the app before. Use this for onboarding checks.
     * @return userHasOpenedApp
     */
    public boolean hasUserOpenedApp(){
        boolean hasOpened =PrefUtils.getFromPrefs(applicationContext, PrefUtils.PREF_HAS_OPENED_APP_KEY, false);
        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_HAS_OPENED_APP_KEY, true);
        return hasOpened;
    }

    /**
     * Returns the food filter saved in the SharedPreferences, returns an empty string otherwise
     * @return foodFilter
     */
    public String getFoodFilter(){
        return getFilter(PrefUtils.PREF_FOOD_FILTER, PrefUtils.PREF_FOOD_FILTER_TIME);
    }

    /**
     * Returns the food filter saved in the SharedPreferences, returns an empty string otherwise
     * @return foodFilter
     */
    public String getStudyFilter(){
        return getFilter(PrefUtils.PREF_STUDY_FILTER, PrefUtils.PREF_STUDY_FILTER_TIME);
    }

    /**
     * Returns the food filter saved in the SharedPreferences, returns an empty string otherwise
     * @return foodFilter
     */
    public String getTechFilter(){
        return getFilter(PrefUtils.PREF_TECH_FILTER, PrefUtils.PREF_TECH_FILTER_TIME);
    }

    public void saveStudyFilter(String filters){
        if(filters == null)
            throw new IllegalArgumentException("Filters cannot be null! Pass an empty string instead");

        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_STUDY_FILTER, filters);
        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_STUDY_FILTER_TIME, System.currentTimeMillis());
    }

    public void saveFoodFilter(String filters){
        if(filters == null)
            throw new IllegalArgumentException("Filters cannot be null! Pass an empty string instead");

        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_FOOD_FILTER, filters);
        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_FOOD_FILTER_TIME, System.currentTimeMillis());
    }

    public void saveTechFilter(String filters){
        if(filters == null)
            throw new IllegalArgumentException("Filters cannot be null! Pass an empty string instead");

        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_TECH_FILTER, filters);
        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_TECH_FILTER_TIME, System.currentTimeMillis());
    }

    /**
     * Returns a boolean indicating whether this user has opted out of firebase analytics
     * @return
     */
    public boolean isOptedOut(){
        return PrefUtils.getFromPrefs(applicationContext, "analytics_pref_key", false);
    }

    private String getFilter(String filterKey, String filterTimeKey){
        long time = PrefUtils.getFromPrefs(applicationContext, filterTimeKey, System.currentTimeMillis() - 20 * 60 * 1000);

        // if the filter was saved more than 15 minutes ago then return empty
        if(time - System.currentTimeMillis() < -1 * (15 * 60 * 1000)){
            PrefUtils.saveToPrefs(applicationContext, filterKey, "");
            Log.d(LOG_TAG, "Filters are too old! Clearing");
            deleteFilter(filterKey, filterTimeKey);
            return "";
        }
        return PrefUtils.getFromPrefs(applicationContext, filterKey, "");
    }

    public void deleteFilters() {
        PrefUtils.deleteFromPrefs(applicationContext, PrefUtils.PREF_TECH_FILTER);
        PrefUtils.deleteFromPrefs(applicationContext, PrefUtils.PREF_STUDY_FILTER);
        PrefUtils.deleteFromPrefs(applicationContext, PrefUtils.PREF_FOOD_FILTER);
        PrefUtils.deleteFromPrefs(applicationContext, PrefUtils.PREF_TECH_FILTER_TIME);
        PrefUtils.deleteFromPrefs(applicationContext, PrefUtils.PREF_STUDY_FILTER_TIME);
        PrefUtils.deleteFromPrefs(applicationContext, PrefUtils.PREF_FOOD_FILTER_TIME);
    }

    private void deleteFilter(String filterKey, String filterTimeKey){
        PrefUtils.deleteFromPrefs(applicationContext, filterKey);
        PrefUtils.deleteFromPrefs(applicationContext, filterTimeKey);
    }

    /**
     * This function is called whenever we ask the user for location permissions.
     */
    public void hasAskedForLocationPermission(){
        int numTimes = PrefUtils.getFromPrefs(applicationContext, PrefUtils.PREF_NUM_TIMES_PERMISSIONS, 0);
        PrefUtils.saveToPrefs(applicationContext, PrefUtils.PREF_NUM_TIMES_PERMISSIONS, numTimes + 1);
    }

    /**
     * This function returns true if we should ask the user for permissions, false otherwise.
     * At the moment we will ask three times.
     * @return shouldAsk
     */
    public boolean shouldAskPermissions(){
        int numTimes = PrefUtils.getFromPrefs(applicationContext, PrefUtils.PREF_NUM_TIMES_PERMISSIONS, 0);
        return numTimes <= 4;
    }
}

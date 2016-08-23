package edu.uw.scout.utils;

import android.content.Context;

import edu.uw.scout.R;

/**
 * Created by ezturner on 8/23/16.
 */
public class URLUtils {

    public static String getTabURL(Context context, int index){
        String[] tabsURLs = context.getResources().getStringArray(R.array.scout_tab_urls);

        if(index > 0 && index < tabsURLs.length){
            return getCampusURL(context) + tabsURLs[index];
        } else {
            return getCampusURL(context);
        }
    }

    public static String getCampusURL(Context context){
        String baseURL = context.getResources().getString(R.string.scout_url);
        String campus = PrefUtils.getFromPrefs(context, PrefUtils.PREF_CAMPUS, "seattle/");
        return baseURL + campus;
    }
}

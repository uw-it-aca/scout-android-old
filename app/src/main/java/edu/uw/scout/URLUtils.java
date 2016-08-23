package edu.uw.scout;

import android.content.Context;

/**
 * Created by ezturner on 8/23/16.
 */
public class URLUtils {

    public static String getTabURL(Context context, int index){
        String baseURL = context.getString(R.string.base_url);
        String[] tabsURLs = context.getResources().getStringArray(R.array.scout_tab_urls);

        if(index > 0 && index < tabsURLs.length){
            return baseURL + "seattle/" + tabsURLs[index];
        } else {
            return baseURL;
        }
    }
}

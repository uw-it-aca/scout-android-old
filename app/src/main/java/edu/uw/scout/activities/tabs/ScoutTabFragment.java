package edu.uw.scout.activities.tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import edu.uw.scout.R;
import edu.uw.scout.Scout;
import edu.uw.scout.activities.CONSTANTS;
import edu.uw.scout.activities.DetailActivity;
import edu.uw.scout.activities.DiscoverCardActivity;
import edu.uw.scout.activities.ScoutActivity;
import edu.uw.scout.utils.ErrorHandler;
import edu.uw.scout.utils.ScoutLocation;
import edu.uw.scout.utils.UserPreferences;

/**
 * This tab fragment handles a single tab, either Discover, Food, Study or Tech.
 */
public class ScoutTabFragment extends Fragment implements TurbolinksAdapter {
    public static final String TAB_ID = "tab_id";
    private static final String LOG_TAG = ScoutTabFragment.class.getSimpleName();
    private String url;
    private TurbolinksView turbolinksView;
    private TurbolinksSession turbolinksSession;
    private long lastVisit;
    private UserPreferences userPreferences;
    private ScoutLocation scoutLocation;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_collection_object, container, false);
        turbolinksView = (TurbolinksView) rootView.findViewById(R.id.turbolinks_view);

        userPreferences = new UserPreferences(getActivity().getApplicationContext());
        scoutLocation = ScoutLocation.getInstance();

        if(scoutLocation == null){
            scoutLocation = new ScoutLocation(getActivity().getApplicationContext());
        }

        Scout scout = Scout.getInstance();
        if(scout == null) {
            turbolinksSession = TurbolinksSession.getDefault(getContext());
        } else {
            turbolinksSession = scout.getTurbolinksManager().getSession(getTabURL(), getContext());
        }

        turbolinksSession.setPullToRefreshEnabled(false);


        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        if(System.currentTimeMillis() - lastVisit <  15 * 1000 * 60 || (url != null && url.equals(getTabURL())))
            return;

        url = getTabURL();

        turbolinksSession
                .activity(getActivity())
                .adapter(this)
                .view(turbolinksView)
                .visit(url);

        lastVisit = System.currentTimeMillis();

    }

    @Override
    public void onResume(){
        super.onResume();
        reloadTab();
    }

    public void reloadTab(){
        if(System.currentTimeMillis() - lastVisit <  150) {
            Log.d(LOG_TAG, "Not visiting");
            return;
        }

        if(!url.equals(getTabURL())){
            url = getTabURL();

            turbolinksSession
                    .activity(getActivity())
                    .adapter(this)
                    .view(turbolinksView)
                    .visit(url);
            lastVisit = System.currentTimeMillis();
        }
        lastVisit = System.currentTimeMillis();


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        url = "";
        lastVisit = System.currentTimeMillis();
    }

    @Override
    public void onPageFinished() {
    }

    @Override
    public void onReceivedError(int errorCode) {

    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {
        switch (statusCode){
            case 404:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.not_found)
                        .positiveText(R.string.action_okay)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                getActivity().onBackPressed();
                            }
                        })
                        .show();
                break;
        }
        Log.d(LOG_TAG, "Request failed with status code: " + statusCode);
    }

    @Override
    public void visitCompleted() {

    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        if(location.contains("?")){
            Intent intent = new Intent(getContext(), DiscoverCardActivity.class);
            intent.putExtra(CONSTANTS.INTENT_URL_KEY, location);
            this.startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra(CONSTANTS.INTENT_URL_KEY, location);
            this.startActivity(intent);
        }
    }

    private String getTabURL(){
        int tabIndex = getArguments().getInt(TAB_ID);
        String tabURL = userPreferences.getTabURL(tabIndex);

        if(scoutLocation != null) {
            if (!tabURL.contains("?")) {
                tabURL += "?";
            } else {
                tabURL += "&";
            }

            tabURL += scoutLocation.getLocationParams();
        }

        return tabURL;
    }

    public void refresh(){
        turbolinksSession.getWebView().loadUrl(url);
    }

}
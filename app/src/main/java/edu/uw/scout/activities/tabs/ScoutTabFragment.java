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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_collection_object, container, false);
        turbolinksView = (TurbolinksView) rootView.findViewById(R.id.turbolinks_view);
        Log.d(LOG_TAG, "Tab here.");
        Scout scout = Scout.getInstance();
        if(scout == null) {
            turbolinksSession = TurbolinksSession.getDefault(getContext());
            Log.d(LOG_TAG, "Created session");
        } else {
            turbolinksSession = scout.getTurbolinksManager().getSession(getTabURL(), getContext());
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "onStart!");
        if(System.currentTimeMillis() - lastVisit <  15 * 1000 * 60 || (url != null && url.equals(getTabURL())))
            return;

        url = getTabURL();

        turbolinksSession
                .activity(getActivity())
                .adapter(this)
                .view(turbolinksView)
                .visit(url);

    }

    @Override
    public void onResume(){
        super.onResume();
        reloadTab();
    }

    public void reloadTab(){
        if(System.currentTimeMillis() - lastVisit <  150)
            return;

        url = getTabURL();

        turbolinksSession
                .activity(getActivity())
                .adapter(this)
                .view(turbolinksView)
                .visit(url);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        url = "";
        lastVisit = System.currentTimeMillis();
    }

    @Override
    public void onPageFinished() {

        Log.d(LOG_TAG , "PageFinished");
    }

    @Override
    public void onReceivedError(int errorCode) {
        Log.d(LOG_TAG, "Error received! : " + errorCode);
    }

    @Override
    public void pageInvalidated() {
        Log.d(LOG_TAG, "Page was invalidated!");
    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {
        switch (statusCode){
            case 404:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.choose_campus)
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
        if(ScoutLocation.getInstance() != null)
            ScoutLocation.getInstance().passLocation(turbolinksSession.getWebView());
        Log.d(LOG_TAG , "Turbolinks visit completed!");
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
        return new UserPreferences(getActivity()).getTabURL(tabIndex);
    }
}
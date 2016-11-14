package edu.uw.scout.activities.tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import edu.uw.scout.R;
import edu.uw.scout.Scout;
import edu.uw.scout.activities.CONSTANTS;
import edu.uw.scout.activities.DetailActivity;
import edu.uw.scout.activities.DiscoverCardActivity;
import edu.uw.scout.activities.ScoutActivity;
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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_collection_object, container, false);
        turbolinksView = (TurbolinksView) rootView.findViewById(R.id.turbolinks_view);
        Scout scout = Scout.getInstance();
        if(scout == null) {
            turbolinksSession = TurbolinksSession.getDefault(getContext());
        } else {
            turbolinksSession = scout.getTurbolinksManager().getSession(getTabURL(), getContext());
        }

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        reloadTab();
    }

    public void reloadTab(){

        String tabURL = getTabURL();

        Log.d(LOG_TAG, tabURL);

        if(url != null && url.equals(tabURL))
            return;

        url = tabURL;

        turbolinksSession
                .activity(getActivity())
                .adapter(this)
                .view(turbolinksView)
                .visit(url);
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

    }

    @Override
    public void visitCompleted() {
        Log.d(LOG_TAG , turbolinksView.toString());
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
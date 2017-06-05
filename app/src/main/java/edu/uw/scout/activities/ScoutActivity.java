package edu.uw.scout.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewTreeObserver;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;

import edu.uw.scout.Scout;
import edu.uw.scout.ScoutAnalytics;
import edu.uw.scout.services.TurbolinksSessionManager;
import edu.uw.scout.utils.ErrorHandler;
import edu.uw.scout.utils.ScoutLocation;
import edu.uw.scout.utils.UserPreferences;

/**
 * A superclass for turbolinks activities containing common variables and implementing stub
 * methods for TurbolinksAdapter.
 */
public class ScoutActivity extends AppCompatActivity implements TurbolinksAdapter {

    private static final String LOG_TAG = ScoutActivity.class.getSimpleName();
    protected UserPreferences userPreferences;
    protected TurbolinksSession turbolinksSession;
    protected String location;
    protected ScoutLocation scoutLocation;
    protected ScoutAnalytics scoutAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        userPreferences = new UserPreferences(this);

        location = getIntent().getStringExtra(CONSTANTS.INTENT_URL_KEY);
        scoutLocation = ScoutLocation.getInstance();

        if(scoutLocation != null)
            location += scoutLocation.getLocationParams();

        scoutAnalytics = ScoutAnalytics.getInstance();

        if(scoutAnalytics != null){

        }

        Scout scout = Scout.getInstance();
        if(scout == null) {
            turbolinksSession = TurbolinksSession.getDefault(this);
        } else {
            turbolinksSession = scout.getTurbolinksManager().getSession(location, this);
        }

        turbolinksSession.setPullToRefreshEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageFinished() {

    }

    @Override
    public void onReceivedError(int errorCode) {
        switch (errorCode){
            case 404:
                turbolinksSession.getWebView().loadUrl("about:blank");
                ErrorHandler.show404(this);
                break;
        }
    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {
        switch (statusCode){
            case 404:
                ErrorHandler.show404(this);
                break;
        }
    }

    @Override
    public void visitCompleted() {

    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {

    }
}

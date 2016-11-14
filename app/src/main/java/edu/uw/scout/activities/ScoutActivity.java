package edu.uw.scout.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;

import edu.uw.scout.Scout;
import edu.uw.scout.services.TurbolinksSessionManager;
import edu.uw.scout.utils.UserPreferences;

/**
 * A superclass for turbolinks activities containing common variables and implementing stub
 * methods for TurbolinksAdapter.
 */
public class ScoutActivity extends AppCompatActivity implements TurbolinksAdapter{

    protected UserPreferences userPreferences;
    protected TurbolinksSession turbolinksSession;
    protected String location;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        userPreferences = new UserPreferences(this);

        location = getIntent().getStringExtra(CONSTANTS.INTENT_URL_KEY);
        Scout scout = Scout.getInstance();
        if(scout == null) {
            turbolinksSession = TurbolinksSession.getDefault(this);
        } else {
            turbolinksSession = scout.getTurbolinksManager().getSession(location, this);
        }
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

    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {

    }

}

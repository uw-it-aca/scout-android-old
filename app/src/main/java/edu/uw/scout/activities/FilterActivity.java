package edu.uw.scout.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uw.scout.R;
import edu.uw.scout.utils.UserPreferences;

public class FilterActivity extends ScoutActivity {

    private static final String LOG_TAG = FilterActivity.class.getSimpleName();
    private String location;
    @BindView(R.id.turbolinks_view)
    TurbolinksView turbolinksView;
    private String queryParams = "";
    @BindView(R.id.filter_submit)
    FloatingActionButton fab;
    private int filterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar) ;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        turbolinksSession.setScreenshotsEnabled(false);
        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        location = getIntent().getStringExtra(CONSTANTS.INTENT_URL_KEY);
        filterType = getIntent().getIntExtra(CONSTANTS.FILTER_TYPE_KEY, 1);

        queryParams = userPreferences.getFilter(filterType);
        Log.d(LOG_TAG, "Initial Filter Type " + filterType + " and query params: " + queryParams) ;

        turbolinksSession.addJavascriptInterface(this, "scoutBridge");
        turbolinksSession.progressView(LayoutInflater.from(this).inflate(com.basecamp.turbolinks.R.layout.turbolinks_progress, turbolinksView, false), com.basecamp.turbolinks.R.id.turbolinks_default_progress_indicator, Integer.MAX_VALUE)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            queryParams = "";
            onBackPressed();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Upon page load, set the ActionBar title to the page title
     */
    @Override
    public void visitCompleted() {
        String pageTitle = turbolinksSession.getWebView().getTitle();
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setTitle(pageTitle);
    }

    /**
     * Visits the URI provided with the Android system interpreter.
     */
    @Override
    public void visitProposedToLocationWithAction(String location, String action) {

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    public void submitFilters(View view){
        onBackPressed();
    }

    /**
     * Retrieve the filter URL from the app and then 
     */
    private void submitForm(String params){
        switch (filterType){
            case 1:
                userPreferences.saveFoodFilter(params);
                break;
            case 2:
                userPreferences.saveStudyFilter(params);
                break;
            case 3:
                userPreferences.saveTechFilter(params);
                break;
        }
    }

    @JavascriptInterface
    public void setParams(String params){
        submitForm(params);
    }

}

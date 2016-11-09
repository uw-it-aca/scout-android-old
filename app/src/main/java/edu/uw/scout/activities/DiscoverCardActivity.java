package edu.uw.scout.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;

import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uw.scout.R;
import edu.uw.scout.Scout;

public class DiscoverCardActivity extends ScoutActivity {

    private static final String LOG_TAG = DiscoverCardActivity.class.getSimpleName();
    private String location;
    @BindView(R.id.turbolinks_view) TurbolinksView turbolinksView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        location = getIntent().getStringExtra(CONSTANTS.INTENT_URL_KEY);

        TurbolinksSession.getDefault(this).progressView(LayoutInflater.from(this).inflate(com.basecamp.turbolinks.R.layout.turbolinks_progress, turbolinksView, false), com.basecamp.turbolinks.R.id.turbolinks_default_progress_indicator, Integer.MAX_VALUE)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);
    }


    /**
     * Upon page load, set the ActionBar title to the page title
     */
    @Override
    public void visitCompleted() {
        String pageTitle = TurbolinksSession.getDefault(this).getWebView().getTitle();
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setTitle(pageTitle);
    }

    /**
     * Visits the URI provided with the Android system interpreter.
     */
    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(CONSTANTS.INTENT_URL_KEY, location);
        this.startActivity(intent);
    }
}

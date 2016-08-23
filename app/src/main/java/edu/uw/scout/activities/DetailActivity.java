package edu.uw.scout.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import edu.uw.scout.R;

public class DetailActivity extends AppCompatActivity implements TurbolinksAdapter{

    private static final String BASE_URL = "https://scout-test.s.uw.edu/h/seattle/";
    private static final String INTENT_URL = "intentUrl";
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private String location;
    private TurbolinksView turbolinksView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        location = getIntent().getStringExtra(INTENT_URL) != null ? getIntent().getStringExtra(INTENT_URL) : BASE_URL;

        TurbolinksSession.getDefault(this).progressView(LayoutInflater.from(this).inflate(com.basecamp.turbolinks.R.layout.turbolinks_progress, turbolinksView, false), com.basecamp.turbolinks.R.id.turbolinks_default_progress_indicator,Integer.MAX_VALUE)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);
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
        String pageTitle = TurbolinksSession.getDefault(this).getWebView().getTitle();
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setTitle(pageTitle);
    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(location));
        startActivity(browserIntent);
    }
}

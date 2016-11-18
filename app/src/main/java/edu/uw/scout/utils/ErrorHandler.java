package edu.uw.scout.utils;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import edu.uw.scout.R;

/**
 * Handles errors with a Material Design popup
 * Created by ezturner on 11/18/16.
 */

public class ErrorHandler {

    public static void show404(final Activity activity){
        new MaterialDialog.Builder(activity)
                .title(R.string.error_requesting)
                .content(R.string.temporarily_unavailable)
                .neutralText(R.string.action_okay)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        activity.onBackPressed();
                    }
                })
                .show();
    }
}

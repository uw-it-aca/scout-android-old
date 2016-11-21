package edu.uw.scout.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.basecamp.turbolinks.TurbolinksSession;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * This class will manage the turbolinks sessions so that we avoid having the wrong page
 * show up as cached.
 *
 * Implements the Singleton pattern.
 *
 * Created by ezturner on 11/9/16.
 */

public class TurbolinksSessionManager {
    // The map where URLs are mapped to TurbolinksSessions
    private Map<String, TurbolinksSession> sessionMap;

    // the session queue we use to avoid using too much memory
    private Queue<TurbolinksSession> sessionQueue;

    public TurbolinksSessionManager(){
        sessionMap = new HashMap<>();
        sessionQueue = new LinkedList<>();
    }

    public TurbolinksSession getSession(String url, Context context) {
        if (sessionMap.containsKey(url))
            return sessionMap.get(url);

        TurbolinksSession session = TurbolinksSession.getNew(context);
        sessionMap.put(url, session);
        popSession();

        return session;
    }

    /**
     * Pops the oldest session if there are more than 5
     */
    private void popSession(){
        if(sessionQueue.size() > 5){
            TurbolinksSession session = sessionQueue.poll();
            sessionMap.remove(session);
        }
    }


}

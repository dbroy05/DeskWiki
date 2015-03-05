package com.deskwiki.deskwiki.framework;

import android.app.Application;

import com.deskwiki.deskwiki.service.WikiSearchManager;


/**
 * The main application underneath wiring up all dependency components
 * Created by dibyenduroy on 3/4/15.
 */
public class MainApplication extends Application {

    private static WikiSearchManager wikiSearchManager;
    public WikiSearchManager getWikiSearchManager() {
        if (wikiSearchManager == null) {
            wikiSearchManager = new WikiSearchManager();
        }
        return wikiSearchManager;
    }
}

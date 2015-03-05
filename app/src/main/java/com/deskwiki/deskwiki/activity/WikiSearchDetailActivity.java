package com.deskwiki.deskwiki.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.deskwiki.deskwiki.R;
import com.deskwiki.deskwiki.framework.MainApplication;
import com.deskwiki.deskwiki.model.WikiItem;
import com.deskwiki.deskwiki.service.WikiSearchManager;


/**
 * This shows the Search details on WebView
 * Created by dibyenduroy on 3/4/15.
 */
public class WikiSearchDetailActivity extends ActionBarActivity {

    private static String wikiURI = "http://en.wikipedia.org/wiki/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Search Details");
        setContentView(R.layout.wiki_search_detail);

        //Retrieving the position on the list to get the search title
        int position = getIntent().getIntExtra(WikiListActivity.SELECTED_POSITION,0);
        WikiItem.QueryItem.SearchItem wikiItem = getWikiSearchManager().getItemByPosition(position);
        //Setting up the Webview to be embedded within app
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        //Loading the webview based on search title
        webView.loadUrl(wikiURI+wikiItem.title);
    }

    private WikiSearchManager getWikiSearchManager() {
        MainApplication application = (MainApplication) getApplication();
        return application.getWikiSearchManager();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

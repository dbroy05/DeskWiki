package com.deskwiki.deskwiki.activity;

import android.content.Intent;

import com.deskwiki.deskwiki.R;

/**
 * Created by dibyenduroy on 3/5/15.
 */
public class WikiSearchDetailActivityTest extends BaseTestActivity<WikiSearchDetailActivity> {


    public WikiSearchDetailActivityTest() {
        super(WikiSearchDetailActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent searchTitleIntent = new Intent();
        searchTitleIntent.putExtra(WikiListActivity.SELECTED_POSITION, 0);
        setActivityIntent(searchTitleIntent);

        solo.waitForActivity(WikiSearchDetailActivity.class);
        solo.sleep(3000);
    }

    //Check if webview is found
    public void testWebView(){
        assertTrue(solo.getView(R.id.webview)!=null);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

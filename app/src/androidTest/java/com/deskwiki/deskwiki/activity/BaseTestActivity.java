package com.deskwiki.deskwiki.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by dibyenduroy on 3/5/15.
 */
public class BaseTestActivity<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
    protected Solo solo;

    public BaseTestActivity(Class<T> activityClass) {
        super(activityClass);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(this.getInstrumentation(), this.getActivity());
        solo.unlockScreen();
    }
}

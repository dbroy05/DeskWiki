package com.deskwiki.deskwiki.activity;

import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.deskwiki.deskwiki.R;

/**
 * Created by dibyenduroy on 3/5/15.
 */
public class WikiListActivityTest extends BaseTestActivity<WikiListActivity>{
    private WikiListActivity mActivity;
    private ListView mSearchList;
    private ListAdapter mSearchListAdapter;


    public WikiListActivityTest(){
        super(WikiListActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mActivity = getActivity();
        mSearchList = (ListView) mActivity.findViewById(R.id.search_result_list);
        mSearchListAdapter = mSearchList.getAdapter();
    }

    //Test UI precondtion
    public void testUIPreConditions(){
        assertTrue(mSearchList.getOnItemClickListener()!=null);
        assertTrue(mSearchListAdapter != null);
    }

    //Test if search view visible
    public void testSearchView(){
        solo.clickOnActionBarItem(R.id.search);
        View serachView = solo.getView(R.id.search);
        assertEquals(true, serachView!=null);
        assertEquals(View.VISIBLE, serachView.getVisibility());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}

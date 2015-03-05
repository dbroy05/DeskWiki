package com.deskwiki.deskwiki.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.deskwiki.deskwiki.R;
import com.deskwiki.deskwiki.adapters.WikiSearchListAdapter;
import com.deskwiki.deskwiki.framework.MainApplication;
import com.deskwiki.deskwiki.listeners.WikiResponseListener;
import com.deskwiki.deskwiki.model.WikiItem;
import com.deskwiki.deskwiki.model.WikiResponse;
import com.deskwiki.deskwiki.service.WikiSearchManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The main activity to handle the initial screen as well as the displaying the search result list.
 * It handles pagination based on pageoffset of the screen the user is currently on and shows the
 * Next or Previous button accordingly.
 */
public class WikiListActivity extends ActionBarActivity {
    public static final String SELECTED_POSITION = "selected_pos";
    private WikiSearchListAdapter wikiSearchListAdapter;
    private TextView errorMessage;
    private ListView searchList;
    private View todayStory;
    private SearchView searchView;
    private View resultLayout;
    private View nextButton;
    private View prevButton;
    static int currentOffset;
    static String currentSearchTerm;
    private View paginationBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki_list);

        //Mapping the view fields with instance variables
        searchList = (ListView) findViewById(R.id.search_result_list);
        resultLayout = findViewById(R.id.result_layout);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        paginationBox = findViewById(R.id.pagination_box);
        errorMessage = (TextView) findViewById(R.id.error_message);
        todayStory = findViewById(R.id.todays_story);

        //Setting up the adapter for search list
        wikiSearchListAdapter = new WikiSearchListAdapter(new WeakReference<Activity>(this)
                ,new ArrayList<WikiItem.QueryItem.SearchItem>());
        searchList.setAdapter(wikiSearchListAdapter);
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WikiListActivity.this, WikiSearchDetailActivity.class);
                intent.putExtra(SELECTED_POSITION, position);
                startActivity(intent);
            }
        });

        //Setting handlers for next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentOffset += 10;
                prevButton.setVisibility(View.VISIBLE);
                getWikiSearchResult(currentSearchTerm,currentOffset);
            }
        });

        //Setting handlers for prev button
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentOffset -= 10;
                if(currentOffset==0)    //When in first page make prev gone
                    prevButton.setVisibility(View.GONE);
                getWikiSearchResult(currentSearchTerm,currentOffset);
            }
        });

        //Set the prev button invisible first
        prevButton.setVisibility(View.GONE);

    }

    /**
     * Searches the query using WikiSearchManager
     * @param query - to be searched
     * @param pageOffset
     */
    private void getWikiSearchResult(String query, int pageOffset) {
        //Setting the progress dialog
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();
        getWikiSearchManager().getSearchResult(query, pageOffset, new WikiResponseListener() {
            @Override
            public void onSuccessResult(WikiResponse resp) {
                //Dismiss dialog when result returns
                dialog.dismiss();
                if (resp.wikiItems.length == 0) { //Showw no result found
                    resultLayout.setVisibility(View.GONE);
                    todayStory.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.VISIBLE);
                    return;
                } else { //On successful result show the list
                    showList();
                    searchView.clearFocus();
                }
                wikiSearchListAdapter.clearAddAllItems(Arrays.asList(resp.wikiItems));

                if(resp.wikiItems.length<10)
                    nextButton.setVisibility(View.GONE);
            }

            @Override
            public void onError(String faultMessage) {
                //Dismiss dialog when error returns
                dialog.dismiss();
                errorMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    private WikiSearchManager getWikiSearchManager() {
        MainApplication application = (MainApplication) getApplication();
        return application.getWikiSearchManager();
    }


    /**
     * This is required for creating the search view.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wiki_list, menu);
        final MenuItem menuItem = menu.findItem(R.id.search);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        //Setting listener to handle any query sumbit/change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    resultLayout.setVisibility(View.GONE);
                    todayStory.setVisibility(View.VISIBLE);
                    currentOffset = 0;
                    currentSearchTerm = "";
                    errorMessage.setVisibility(View.GONE);
                    prevButton.setVisibility(View.GONE);
                }
                return false;
            }
        });

        return true;
    }

    /**
     * Hides the keyboard when query submitted
     * @param view
     */
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * This is called whenever a new query is searched.
     * @param intent
     */
    private void handleIntent(Intent intent) {
        // Special processing of the incoming intent only occurs if the if the action specified
        // by the intent is ACTION_SEARCH.
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // SearchManager.QUERY is the key that a SearchManager will use to send a query string
            // to an Activity.
            hideKeyboard(searchView);
            String query = intent.getStringExtra(SearchManager.QUERY);
            currentSearchTerm = query;
            //Fire off the query search
            getWikiSearchResult(query,0);


        }
    }

    /**
     * Showing the list when there is data returned
     */
    private void showList() {
        errorMessage.setVisibility(View.GONE);
        todayStory.setVisibility(View.GONE);
        resultLayout.setVisibility(View.VISIBLE);
    }

}

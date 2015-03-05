package com.deskwiki.deskwiki.service;

import com.deskwiki.deskwiki.listeners.WikiResponseListener;
import com.deskwiki.deskwiki.model.WikiItem;
import com.deskwiki.deskwiki.model.WikiResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * WikiSearchManager handles all the query and delegates to Service Task to process, once retrieved,
 * it sets the listener
 *
 * Created by dibyenduroy on 3/4/15.
 */
public class WikiSearchManager {
    private static WikiResponse wikiResponse;

    /**
     * Returns the search result based on query and pageoffset. On success, it sets the result on listener.
     * @param query
     * @param pageOffset
     * @param listener
     */
    public void getSearchResult(String query, int pageOffset, final WikiResponseListener listener){

        String[] queryParams = {query,String.valueOf(pageOffset)};
        //Fires off the main task for searching
        new BaseServiceTask(new WikiResponseListener() {

            @Override
            public void onSuccessResult(WikiResponse resp) {
                wikiResponse = resp;
                listener.onSuccessResult(resp);
            }

            @Override
            public void onError(String faultMessage) {
                //Should handle the error gracefully
            }
        }).execute(queryParams);
    }


    public WikiItem.QueryItem.SearchItem getItemByPosition(int position){
        return wikiResponse.wikiItems[position];
    }
}

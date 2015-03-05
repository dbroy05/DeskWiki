package com.deskwiki.deskwiki.service;

import com.deskwiki.deskwiki.listeners.WikiResponseListener;
import com.deskwiki.deskwiki.model.WikiItem;
import com.deskwiki.deskwiki.model.WikiResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WikiSearchManager handles all the query and delegates to Service Task to process, once retrieved,
 * it sets the listener
 *
 * Created by dibyenduroy on 3/4/15.
 */
public class WikiSearchManager {
    private static WikiResponse wikiResponse;
    private static Map<String, WikiResponse> searchCache = new HashMap<>();

    /**
     * Returns the search result based on query and pageoffset. On success, it sets the result on listener.
     * It maintains a cache based on query term for first page
     * @param query
     * @param pageOffset
     * @param listener
     */
    public void getSearchResult(final String query, final int pageOffset, final WikiResponseListener listener){
        WikiResponse cachedResult = searchCache.get(query);
        //If its first page and cache hit, return it
        if(pageOffset==0 && cachedResult != null){
            listener.onSuccessResult(cachedResult);
        }

        String[] queryParams = {query,String.valueOf(pageOffset)};
        //Fires off the main task for searching
        new BaseServiceTask(new WikiResponseListener() {

            @Override
            public void onSuccessResult(WikiResponse resp) {
                wikiResponse = resp;
                if(pageOffset==0)
                    searchCache.put(query,resp);
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

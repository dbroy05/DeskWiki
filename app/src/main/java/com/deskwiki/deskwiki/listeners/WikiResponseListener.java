package com.deskwiki.deskwiki.listeners;


import com.deskwiki.deskwiki.model.WikiResponse;

/**
 * The main listener to be used by UI activity and set by service task.
 * Created by dibyenduroy on 3/4/15.
 */
public interface WikiResponseListener {

    public void onSuccessResult(WikiResponse resp);
    public void onError(String faultMessage);

}

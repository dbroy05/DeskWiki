package com.deskwiki.deskwiki.service;

import android.os.AsyncTask;
import android.util.Log;

import com.deskwiki.deskwiki.listeners.WikiResponseListener;
import com.deskwiki.deskwiki.model.WikiItem;
import com.deskwiki.deskwiki.model.WikiResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

/**
 * The main task to search the query asynchronously. It makes the GET call on the service endpoint,
 * parses the JSON response using GSON library and maps it to the object model to return to the UI.
 *
 * Created by dibyenduroy on 3/4/15.
 */
public class BaseServiceTask extends AsyncTask<String, String, String> {

    private static final int CONNECTION_TIMEOUT_MILLIS = 1000 * 60; //45 second timeout
    private String serviceEndpoint = "http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=";
    private static final boolean DEBUG_NETWORK = true;
    private static final String TAG = "BaseTask";

    private static Gson sGson;
    private boolean mNetworkFailureOccurred;
    private int mServerResponseCode = 0;
    private WikiResponseListener mListener;
    private WikiResponse mResponse;

    public BaseServiceTask(WikiResponseListener listener){
        this.mListener = listener;
    }
    protected static Gson getGson() {
        if (sGson == null) {
            sGson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();
        }
        return sGson;
    }
    private static BasicHttpContext sUrlContext;
    private static DefaultHttpClient sUrlClient;
    private static DefaultHttpClient getUrlClient() {
        if (sUrlClient == null) {
            SchemeRegistry schemeRegistry = new SchemeRegistry();

            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

            BasicHttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT_MILLIS);
            ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

            sUrlContext = new BasicHttpContext();
            sUrlClient = new DefaultHttpClient(cm, params);
        }
        return sUrlClient;
    }

    private void setRequestHeaders(HttpRequestBase request) {
        //JSON
        request.setHeader("Accept", "application/json");
    }

    @Override
    protected String doInBackground(String... params) {
        HttpRequestBase request = new HttpGet();
        String finalUri = null;
        try {
            finalUri = serviceEndpoint
                    + URLEncoder.encode(params[0], "utf8")
                    +"&srprop=timestamp&format=json&rawcontinue"
                    +"&sroffset="+params[1] ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        request.setURI(URI.create(finalUri));
        setRequestHeaders(request);

        HttpResponse response;
        try {
            response = getUrlClient().execute(request, sUrlContext);
        } catch (Exception e) {
            mNetworkFailureOccurred = true;
            return null;
        }

        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        String statusReason = statusLine.getReasonPhrase();
        HttpEntity responseEntity = response.getEntity();
        String responseString = getStringFromHttpEntity(responseEntity);
        mServerResponseCode = statusCode;
        if (statusCode < 200) {
            //100-level code is informational
            if (DEBUG_NETWORK) Log.i(TAG, "Response: "+statusCode+" "+statusReason);
            if (DEBUG_NETWORK) Log.i(TAG, ""+responseString);
        } else if (statusCode > 199 && statusCode < 300) {
            //200-level code is success
            if (DEBUG_NETWORK) Log.i(TAG, "Response: "+statusCode+" "+statusReason);
            if (DEBUG_NETWORK) Log.i(TAG, ""+responseString);
        } else if (statusCode > 299 && statusCode < 400) {
            //300-level code is redirect - shouldn't get here as the framework should automatically follow the redirect
            if (DEBUG_NETWORK) Log.i(TAG, "Response: "+statusCode+" "+statusReason);
            if (DEBUG_NETWORK) Log.i(TAG, ""+responseString);
            return null;
        } else if (statusCode > 399 && statusCode < 500) {
            //400-level code is client error
            if (DEBUG_NETWORK) Log.w(TAG, "Response: "+statusCode+" "+statusReason);
            if (DEBUG_NETWORK) Log.w(TAG, ""+responseString);
            return null;
        } else if (statusCode > 499) {
            //500-level code is server error
            if (DEBUG_NETWORK) Log.w(TAG, "Response: "+statusCode+" "+statusReason);
            if (DEBUG_NETWORK) Log.w(TAG, ""+responseString);

            return null;
        }
        try {
            handleResponseString(responseString);
        } catch (Exception e) {
            mNetworkFailureOccurred = true;
        }

        return null;
    }

    protected void handleResponseString(String responseString) {
        WikiItem wikiItem = getGson().fromJson(responseString, WikiItem.class);
        mResponse = new WikiResponse();
        mResponse.wikiItems = wikiItem.query!=null?wikiItem.query.search:null;
    }

    protected void onPostExecute(String result) {
        if (mNetworkFailureOccurred) {
            if (mListener != null) {
                mListener.onError("Error in connecting to the server..");
            }
        } else {
            if(mResponse == null){
                if(mListener != null){
                    mListener.onError(null);
                    return;
                }
            }else {
                mListener.onSuccessResult(mResponse);
            }

        }
    }

    private static String getStringFromHttpEntity(HttpEntity entity) {
        final char[] buffer = new char[1024];
        final StringBuilder out = new StringBuilder();
        try {
            InputStream inputStream;
            if (entity.getContentEncoding() != null && entity.getContentEncoding().getValue().contains("gzip")) {
                Log.d(TAG, "Response Content Encoding: " + entity.getContentEncoding().getValue());
                inputStream = new GZIPInputStream(entity.getContent());
            } else {
                inputStream = entity.getContent();
            }
            final Reader in = new InputStreamReader(inputStream, "UTF-8");
            try {
                for (;;) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0)
                        break;
                    out.append(buffer, 0, rsz);
                }
            }
            finally {
                in.close();
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "exception", ex);
            return null;
        }
        return out.toString();
    }
}

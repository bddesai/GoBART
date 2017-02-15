package com.source.bhavin.gobart.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Bhavin on 4/29/2015.
 */
public class ServiceHandler {
    private static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler(){
    }

    public String makeServiceCall(String url, int method){
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = null;

            if (method == GET) {
                HttpGet httpGet = new HttpGet(url);
                // set the headers
                httpGet.setHeader("Accept", "application/xml, q=0.9,text/html;q=0.8,*/*;q=0.7");
                httpGet.setHeader("Content-Type", "text/xml; charset=utf-8");

                httpResponse = httpClient.execute(httpGet);
            }

            // process the response to String
            HttpEntity httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

            //Log.d("check", "Response: > " + response);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return response;
    }
}

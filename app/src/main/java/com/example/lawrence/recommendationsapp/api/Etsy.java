package com.example.lawrence.recommendationsapp.api;

import android.app.DownloadManager;

import com.example.lawrence.recommendationsapp.HideKeys;
import com.example.lawrence.recommendationsapp.model.ActiveListings;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class Etsy {

    private final static String API_KEY = HideKeys.ETSY_API_KEY;

    private static RequestInterceptor getInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addEncodedQueryParam("api_key", API_KEY);
            }
        };
    }

    private static Api getApi(){
        return new RestAdapter
                .Builder()
                .setEndpoint("https://openapi.etsy.com/v2")
                .setRequestInterceptor(getInterceptor())
                .build()
                .create(Api.class);
    }

    // call to our API. this is void (doesn't return anything),
    // we pass in a callback which is executed when network request completes.
    public static void getActiveListings(Callback<ActiveListings> callback){
        getApi().activeListings("Images,Shop", callback);
    }


    // helper methods to hide private api key in another file not add to github project.
    public static void testApiKey(){
        System.out.println("etsy apikey: " + API_KEY);
    }

    public static String getApiKey() {
        return API_KEY;
    }

}

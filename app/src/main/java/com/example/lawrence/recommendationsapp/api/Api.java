package com.example.lawrence.recommendationsapp.api;

import com.example.lawrence.recommendationsapp.model.ActiveListings;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

// using Retrofit library to simplify where the URI is and what HTTP protocol to use.
public interface Api {

    @GET("/listings/active")
    void activeListings(@Query("includes") String includes,
                        Callback<ActiveListings> callback);
    // "includes" is the image and shop data coming through the API with our listings call.

}

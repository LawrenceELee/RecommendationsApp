package com.example.lawrence.recommendationsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lawrence.recommendationsapp.api.Etsy;
import com.example.lawrence.recommendationsapp.model.ActiveListings;
import com.example.lawrence.recommendationsapp.model.Listing;

public class MainActivity extends AppCompatActivity {

    private static final String STATE_ACTIVE_LISTINGS = "StateActiveListings";

    private ListingAdapter adapter;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mErrorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mErrorView = (TextView) findViewById(R.id.error_view);

        // set up recycler view
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ListingAdapter(this);
        mRecyclerView.setAdapter(adapter);

        if(savedInstanceState == null){         // first time loading: show loading, call network and fetch JSON data.
            showLoading();
            Etsy.getActiveListings(adapter);
        } else {                                // it was loaded before.
            if( savedInstanceState.containsKey(STATE_ACTIVE_LISTINGS) ){
                adapter.success((ActiveListings) savedInstanceState.getParcelable(STATE_ACTIVE_LISTINGS), null);
                showLoading();
            } else {
                showLoading();
                Etsy.getActiveListings(adapter);
            }
        }

    }

    // if we have that data, we'll pass that data on the out-state.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ActiveListings activeListings = adapter.getActiveListings();
        if( activeListings != null ){
            outState.putParcelable(STATE_ACTIVE_LISTINGS, activeListings);
        }
    }

    // this is what we show by default
    public void showLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    // after JSON data has loaded show list of etsy items
    public void showList(){
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
    }

    // if there is an error, show it.
    public void showError(){
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

}

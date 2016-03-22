package com.example.lawrence.recommendationsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lawrence.recommendationsapp.model.ActiveListings;
import com.example.lawrence.recommendationsapp.model.Listing;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ListingAdapter
    extends RecyclerView.Adapter<ListingAdapter.ListingHolder>
    implements Callback<ActiveListings> {

    private LayoutInflater inflater;
    private ActiveListings activeListings;

    private MainActivity mMainActivity;

    // constructor
    public ListingAdapter(MainActivity activity){
        mMainActivity = activity;
        inflater = LayoutInflater.from(activity);
    }

    // to extend recyclerview adapter
    @Override
    public ListingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListingHolder(inflater.inflate(R.layout.layout_listing, parent, false));
    }

    // to extend recyclerview adapter
    @Override
    public void onBindViewHolder(ListingHolder holder, int position) {
        final Listing listing = activeListings.results[position];
        holder.titleView.setText(listing.title);
        holder.priceView.setText(listing.price);
        holder.shopNameView.setText(listing.Shop.shop_name);

        // use picasso to add image view
        Picasso.with(holder.imageView.getContext())
                .load(listing.Images[0].url_570xN)
                .into(holder.imageView);

    }

    // to extend recyclerview adapter
    @Override
    public int getItemCount() {
        if( activeListings == null )            return 0;
        if( activeListings.results == null )    return 0;

        return activeListings.results.length;
    }

    // to implement the retrofit callback
    @Override
    public void success(ActiveListings activeListings, Response response) {
        this.activeListings = activeListings;
        notifyDataSetChanged();
        mMainActivity.showList();
    }

    // to implement the retrofit callback
    @Override
    public void failure(RetrofitError error) {
        mMainActivity.showError();
    }

    public ActiveListings getActiveListings(){
        return activeListings;
    }

    public class ListingHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public TextView shopNameView;
        public TextView priceView;

        public ListingHolder(View itemView) {
            super(itemView);

            // not using ButterKnife because it's relatively few views so doing it by hand.
            imageView = (ImageView) itemView.findViewById(R.id.listing_image);
            titleView = (TextView) itemView.findViewById(R.id.listing_title);
            shopNameView = (TextView) itemView.findViewById(R.id.listing_shop_name);
            priceView = (TextView) itemView.findViewById(R.id.listing_price);
        }
    }
}

package com.example.lawrence.recommendationsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lawrence.recommendationsapp.api.Etsy;
import com.example.lawrence.recommendationsapp.google.GoogleServicesHelper;
import com.example.lawrence.recommendationsapp.model.ActiveListings;
import com.example.lawrence.recommendationsapp.model.Image;
import com.example.lawrence.recommendationsapp.model.Listing;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusShare;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ListingAdapter
        extends RecyclerView.Adapter<ListingAdapter.ListingHolder>
        implements Callback<ActiveListings>,
        GoogleServicesHelper.GoogleServicesListener {

    public static final int REQUEST_CODE_PLUS_ONE = 10;
    public static final int REQUEST_CODE_SHARE = 11;

    private LayoutInflater inflater;
    private ActiveListings activeListings;

    private MainActivity mMainActivity;

    private boolean isGooglePlayServicesAvailable;

    // constructor
    public ListingAdapter(MainActivity activity){
        mMainActivity = activity;
        inflater = LayoutInflater.from(activity);

        this.isGooglePlayServicesAvailable = false;  // assume google play services not available by default.
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openListing = new Intent(Intent.ACTION_VIEW);
                openListing.setData(Uri.parse(listing.url));
                mMainActivity.startActivity(openListing);
            }
        });

        if( isGooglePlayServicesAvailable ){
            // for "+1" button
            holder.plusOneButton.setVisibility(View.VISIBLE);   // show "+1" button if play services is available.
            holder.plusOneButton.initialize(listing.url, REQUEST_CODE_PLUS_ONE);
            holder.plusOneButton.setAnnotation(PlusOneButton.ANNOTATION_NONE);

            // for share button
            holder.shareButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new PlusShare.Builder(mMainActivity)
                            .setType("text/plain")
                            .setText("Checkout this item on Etsy " + listing.title)
                            .setContentUrl(Uri.parse(listing.url))
                            .getIntent();

                    mMainActivity.startActivityForResult(intent, REQUEST_CODE_SHARE);
                }
            });

        } else {
            holder.plusOneButton.setVisibility(View.GONE);   // don't show "+1" button if play services is not available.

            holder.shareButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Checkout this item on Etsy " + listing.title + " " + listing.url);
                    intent.setType("text/plain");

                    mMainActivity.startActivityForResult(Intent.createChooser(intent, "Share"), REQUEST_CODE_SHARE);
                }
            });
        }

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

    @Override
    public void onConnected() {
        if( getItemCount() == 0 ){          // if we don't have any items, start network request.
            Etsy.getActiveListings(this);
        }

        isGooglePlayServicesAvailable = true;
        notifyDataSetChanged();         // tell app to update view now that google play services is available.
    }

    @Override
    public void onDisconnected() {
        if( getItemCount() == 0 ){
            Etsy.getActiveListings(this);
        }

        isGooglePlayServicesAvailable = false;
        notifyDataSetChanged();
    }

    public class ListingHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public TextView shopNameView;
        public TextView priceView;
        public PlusOneButton plusOneButton;
        public ImageButton shareButton;

        public ListingHolder(View itemView) {
            super(itemView);

            // not using ButterKnife because it's relatively few views so doing it by hand.
            imageView = (ImageView) itemView.findViewById(R.id.listing_image);
            titleView = (TextView) itemView.findViewById(R.id.listing_title);
            shopNameView = (TextView) itemView.findViewById(R.id.listing_shop_name);
            priceView = (TextView) itemView.findViewById(R.id.listing_price);
            plusOneButton = (PlusOneButton) itemView.findViewById(R.id.listing_plus_one_btn);
            shareButton = (ImageButton) itemView.findViewById(R.id.listing_share_btn);
        }
    }
}

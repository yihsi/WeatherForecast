package com.yihsi.weatherforecast;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yihsi on 5/12/16.
 */
public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {
    private ArrayList<String> mSearchedLocations;

    public LocationListAdapter(ArrayList<String> locations) {
        mSearchedLocations = locations;
    }

    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_list_item, parent, false);

        return new LocationListAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LocationListAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(mSearchedLocations.get(position));
    }

    @Override
    public int getItemCount() {
        return mSearchedLocations.size();
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public ViewHolder(View view) {
            super(view);

            mTextView = (TextView)view.findViewById(R.id.searched_location);
        }

        public TextView getTextView() {
            return mTextView;
        }
    }
}

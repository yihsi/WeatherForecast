package com.yihsi.weatherforecast;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yihsi on 5/12/16.
 */
public class LocationListFragment extends Fragment {
    private final String FILENAME = "searchedLocation.txt";

    private ArrayList<String> mSearchedLocations;

    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list, parent, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.location_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        try {
            mSearchedLocations = SearchedLocationsLab.getInstance(getActivity(), FILENAME)
                    .loadLocations();
        } catch (IOException e) {
            Toast.makeText(getActivity(), R.string.error_load, Toast.LENGTH_SHORT).show();
        }
        if (mSearchedLocations != null) {
            mRecyclerView.setAdapter(new LocationListAdapter(mSearchedLocations));
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            // Swipe to delete an item
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                LocationListAdapter adapter = (LocationListAdapter)mRecyclerView.getAdapter();
                mSearchedLocations.remove(swipedPosition);
                adapter.notifyDataSetChanged();

                try {
                    SearchedLocationsLab.getInstance(getActivity(), FILENAME).saveLocations
                            (mSearchedLocations);
                } catch (IOException e) {
                    Toast.makeText(getActivity(), R.string.error_save, Toast.LENGTH_SHORT).show();
                }

            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        return view;
    }
}
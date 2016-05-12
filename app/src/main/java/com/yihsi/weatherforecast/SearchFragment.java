package com.yihsi.weatherforecast;

import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yihsi on 5/7/16.
 */
public class SearchFragment extends ListFragment {
    private final String FILENAME = "searchedLocation.txt";

    // Column names in database
    private static final String PROVCN = "PROVCN";
    private static final String DISTRICTCN = "DISTRICTCN";
    private static final String NAMECN = "NAMECN";

    private ImageView mCloseButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Suggestion list is initially null
        setListAdapter(null);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(
                Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity()
                .getComponentName()));
        // Do not iconify the widget; expand it by default
        searchView.setIconified(false);

        // Get the close button's int id
        int closeButtonId = getActivity().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        mCloseButton = (ImageView) searchView.findViewById(closeButtonId);

        // Close button initially gone
        if (searchView.getQuery().toString().isEmpty()) {
            mCloseButton.setVisibility(View.GONE);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor cursor = DatabaseManager.getInstance(getActivity()).queryAreas(newText);
                CursorAdapter adapter = null;
                if (cursor != null) {
                    // Create a cursor adapter for the suggestions and apply them to the SearchView
                    adapter = new SuggestionsCursorAdapter(getActivity(), cursor);

                    setListAdapter(adapter);

                    return true;
                }
                else {
                    mCloseButton.setVisibility(View.GONE);

                    setListAdapter(null);

                    return false;
                }
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("closebutton", String.valueOf(mCloseButton));
                mCloseButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = (SQLiteCursor)getListAdapter().getItem(position);

        String name = cursor.getString(cursor.getColumnIndex(NAMECN));
        Log.i("name", name);

        ArrayList<String> loadedLocations = null;
        try {
            loadedLocations = SearchedLocationsLab.getInstance(getActivity(), FILENAME)
                    .loadLocations();
        } catch (IOException e) {
            Log.e("SearchedLocationLab", "error loading locations: ", e);
        }

        try {
            if (loadedLocations == null) {
                loadedLocations = new ArrayList<>();
            }
            loadedLocations.add(name);
            SearchedLocationsLab.getInstance(getActivity(), FILENAME).saveLocations
                    (loadedLocations);
        } catch (IOException e) {
            Toast.makeText(getActivity(), R.string.error_save, Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        intent.putExtra(WeatherActivity.EXTRA_SEARCH_LOCATION, name);
        startActivity(intent);

        getActivity().finish();
    }

    private class SuggestionsCursorAdapter extends CursorAdapter {
        public SuggestionsCursorAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.suggestion_list_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView provinceCN = (TextView) view.findViewById(R.id.province_cn);
            TextView districtCN = (TextView) view.findViewById(R.id.district_cn);
            TextView nameCN = (TextView) view.findViewById(R.id.name_cn);

            String province = cursor.getString(cursor.getColumnIndex(PROVCN));
            String district = cursor.getString(cursor.getColumnIndex(DISTRICTCN));
            String name = cursor.getString(cursor.getColumnIndex(NAMECN));

            provinceCN.setText(province + "-");
            districtCN.setText(district + "-");
            nameCN.setText(name);
        }
    }
}
package com.yihsi.weatherforecast;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity {
    private final String FILENAME = "searchedLocation.txt";

    public static final String EXTRA_SEARCH_LOCATION = "com.yihsi.weatherforecast.search_location";

    private Fragment mFragment;
    private FragmentManager mManager;

    private DrawerLayout mDrawer;
    private ListView mDrawerList;
    private ArrayList<String> mSearchedLocations;

    private final int PERMISSION_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        // Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout
        toggle.syncState();

        mManager = getFragmentManager();

        String location = getIntent().getStringExtra(EXTRA_SEARCH_LOCATION);

        // If permission location not granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getSupportActionBar().hide();

            // Use a dialog to request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }

        else {
            getSupportActionBar().show();

            if (mFragment == null) {
                mFragment = (location == null ? new WeatherFragment()
                        : WeatherFragment.newInstance(location));

                mManager.beginTransaction().add(R.id.fragmentContainer, mFragment).commit();
            }
        }

        Drawable ic_location = ContextCompat.getDrawable(this, R.drawable.ic_location);
        Drawable ic_edit_location = ContextCompat.getDrawable(this, R.drawable.ic_edit_location);
        ic_location.setColorFilter(ContextCompat.getColor(this, R.color.colorLocationIcon),
                PorterDuff.Mode.SRC_ATOP);
        ic_edit_location.setColorFilter(ContextCompat.getColor(this, R.color.colorLocationIcon),
                PorterDuff.Mode.SRC_ATOP);

        LinearLayout editLocation = (LinearLayout)findViewById(R.id.edit_location);
        assert editLocation != null;
        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, LocationListActivity.class);
                startActivity(intent);

                mDrawer.closeDrawer(GravityCompat.START);
            }
        });

        LinearLayout currentLocation = (LinearLayout)findViewById(R.id.current_location);
        assert currentLocation != null;
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If fragment is displaying current location's temperature, just close drawer.
                if (mFragment.getArguments() == null) {
                    mDrawer.closeDrawer(GravityCompat.START);
                }
                else {
                    // Replace current fragment to show current location's temperature
                    mFragment = new WeatherFragment();
                    mManager.beginTransaction().replace(R.id.fragmentContainer,
                            mFragment).commit();

                    mDrawer.closeDrawer(GravityCompat.START);
                }
            }
        });

        try {
            mSearchedLocations = SearchedLocationsLab.getInstance(this, FILENAME)
                    .loadLocations();
        } catch (IOException e) {
            Log.e("SearchedLocationLab", "error loading locations: ", e);
        }

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        if (mSearchedLocations != null) {
            // Set the adapter for the list view
            mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item,
                    R.id.location_name, mSearchedLocations));

            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String location = mSearchedLocations.get(position);
                    mFragment = WeatherFragment.newInstance(location);
                    mManager.beginTransaction().replace(R.id.fragmentContainer,
                            mFragment).commit();

                    mDrawer.closeDrawer(GravityCompat.START);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION:
                getSupportActionBar().show();

                mFragment = mManager.findFragmentById(R.id.fragmentContainer);

                if (mFragment == null) {
                    mFragment = new WeatherFragment();

                    mManager.beginTransaction().add(R.id.fragmentContainer, mFragment).commit();
                }

                return;
            default:
                break;
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();

        try {
            mSearchedLocations = SearchedLocationsLab.getInstance(this, FILENAME)
                    .loadLocations();
        } catch (IOException e) {
            Toast.makeText(this, R.string.error_load, Toast.LENGTH_SHORT).show();
        }

        if (mSearchedLocations != null) {
            // Set the adapter for the list view
            mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item,
                    R.id.location_name, mSearchedLocations));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
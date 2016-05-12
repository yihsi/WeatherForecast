package com.yihsi.weatherforecast;

import android.app.Fragment;

/**
 * Created by yihsi on 5/12/16.
 */
public class LocationListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LocationListFragment();
    }
}

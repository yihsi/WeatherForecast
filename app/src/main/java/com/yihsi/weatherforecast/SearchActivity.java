package com.yihsi.weatherforecast;

import android.app.ListFragment;

/**
 * Created by yihsi on 5/7/16.
 */
public class SearchActivity extends SingleFragmentActivity {

    @Override
    protected ListFragment createFragment() {
        return new SearchFragment();
    }
}
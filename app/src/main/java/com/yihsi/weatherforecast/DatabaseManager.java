package com.yihsi.weatherforecast;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.IOException;

/**
 * Created by yihsi on 5/3/16.
 */
public class DatabaseManager {
    private static DatabaseManager sManager;
    private Context mContext;
    private AreaIdDatabaseHelper mHelper;

    private DatabaseManager(Context context) {
        mContext = context;
        mHelper = new AreaIdDatabaseHelper(mContext);

        try {
            mHelper.createDatabase();
        } catch (IOException ioe) {
            Log.e("DatabaseManager", "unable to create database", ioe);
        }
    }

    public static DatabaseManager getInstance(Context context) {
        if (sManager == null) {
            sManager = new DatabaseManager(context.getApplicationContext());
        }
        return sManager;
    }

    public String queryAreaId(String name) {
        return mHelper.queryAreaId(name);
    }

    public Cursor queryAreas(String query) {
        return mHelper.queryAreas(query);
    }
}
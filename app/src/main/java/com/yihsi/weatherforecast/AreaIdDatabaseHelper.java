package com.yihsi.weatherforecast;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yihsi on 4/29/16.
 */
public class AreaIdDatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    private static final String DB_NAME = "weather.db";
    private String DATABASE_PATH;

    private static final String TABLE_NAME="areaid_v";

    public AreaIdDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        mContext = context;
        DATABASE_PATH = mContext.getDatabasePath(DB_NAME).getPath();
    }

    public void createDatabase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            Log.i("weather.db", "Database is already exist.");
        }
        else {
            Log.i("bgetReadableDatabase", String.valueOf(checkDataBase()));
            getReadableDatabase();
            Log.i("agetReadableDatabase", String.valueOf(checkDataBase()));
            try {
                copyDatabase();
                Log.i("copyDatabase", "copy done");
            } catch (IOException e) {
                Log.e("onCreateDataBase", "copy data base error", e);
            }
        }
    }

    // Check database whether exists
    private boolean checkDataBase() {
        File file = mContext.getDatabasePath(DB_NAME);
        return file.exists();
    }

    private void copyDatabase() throws IOException {

        FileOutputStream outputStream = new FileOutputStream(DATABASE_PATH);
        InputStream inputStream = mContext.getAssets().open(DB_NAME);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0 ,count);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public String queryAreaId(String name) {
        Log.i("queryAreaId", "query start");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(DATABASE_PATH, null,
                SQLiteDatabase.OPEN_READONLY);
        // Column NAMECN
        String NAMECN = "NAMECN";
        Cursor cursor = database.query(TABLE_NAME, null, NAMECN + " = ?",
                new String[]{name}, null, null, null, "1");

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            // Column AREAID
            String AREAID = "AREAID";
            int index = cursor.getColumnIndex(AREAID);
            Log.i("AreaIdDatabaseHelper", String.valueOf(index));
            return cursor.getString(index);
        }
        cursor.close();

        return null;
    }

    public Cursor queryAreas(String query) {
        Cursor cursor = null;

        if (query != null && !query.equals("")) {
            String[] selectArgs = { query + "%", query + "%" };
            String sql = "select AREAID as _id, PROVCN, DISTRICTCN, NAMECN from areaid_v where " +
                    "NAMEEN like ? or NAMECN like ? order by AREAID";

            SQLiteDatabase database = SQLiteDatabase.openDatabase(DATABASE_PATH, null,
                    SQLiteDatabase.OPEN_READONLY);
            cursor = database.rawQuery(sql, selectArgs);

            return cursor;
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
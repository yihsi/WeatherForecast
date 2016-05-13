package com.yihsi.weatherforecast;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by yihsi on 5/12/16.
 */
public class SearchedLocationsLab {
    private String mFileName;

    private static SearchedLocationsLab sLocationsLab;
    private Context mContext;

    private SearchedLocationsLab(Context context, String fileName) {
        mContext = context;
        mFileName = fileName;
    }

    public static SearchedLocationsLab getInstance(Context context, String fileName) {
        if (sLocationsLab == null) {
            sLocationsLab = new SearchedLocationsLab(context, fileName);
        }

        return sLocationsLab;
    }

    public ArrayList<String> loadLocations() throws IOException {
        ArrayList<String> locations = new ArrayList<>();
        BufferedReader reader  = null;
        try {
            // Open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                // Store non-duplicate locations
                if (!locations.contains(line)) {
                    // Line breaks are omitted and irrelevant
                    locations.add(line);
                }
            }
        } finally {
            if (reader != null)
                reader.close();
        }

        return locations;
    }

    public void saveLocations(ArrayList<String> locations) throws IOException{
        StringBuilder builder = new StringBuilder();
        for (String location : locations) {
            builder.append(location + "\n");
        }

        //Write the file to disk
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(builder.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}

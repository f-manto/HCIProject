package com.example.francesco.mapboxapp;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * Created by pietrodimarco on 11/17/17.
 */

public class LoadJson {



    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = AppContext.getAppContext().getAssets().open("app/sampledata/floor1_waypoints.geojson");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


}

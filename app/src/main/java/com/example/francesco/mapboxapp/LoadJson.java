package com.example.francesco.mapboxapp;

import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * Created by pietrodimarco on 11/17/17.
 */

public class LoadJson extends Fragment {



    public String loadJSONFromAsset(Context applicationContext) {
        String json = null;
        try {
            InputStream is = applicationContext.getAssets().open("floor1_waypoints.geojson");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {

            ex.printStackTrace();
            return null;
        }
        Log.d("JSON",json);
        return json;
    }



}

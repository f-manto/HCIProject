package com.example.francesco.mapboxapp;

import android.util.Log;

import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;

import org.json.JSONException;

/**
 * Created by pietrodimarco on 11/17/17.
 */

public class WayFinder {



    public void printPoints() {

        LoadJson string=new LoadJson();

        try {
            GeoJSONObject geoJSON = GeoJSON.parse(string.loadJSONFromAsset());
            Log.d("nav",geoJSON.getType());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

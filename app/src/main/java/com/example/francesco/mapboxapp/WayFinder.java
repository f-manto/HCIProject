package com.example.francesco.mapboxapp;

import android.content.Context;
import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Point;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pietrodimarco on 11/17/17.
 */

public class WayFinder {

    ArrayList<Tag> tags;

    public WayFinder(Context applicationContext) {
        tags = new ArrayList<>();
        LoadJson string=new LoadJson();
        try {
            FeatureCollection geoJSON = (FeatureCollection) GeoJSON.parse(string.loadJSONFromAsset(applicationContext));
            for (int i = 0; i < geoJSON.getFeatures().size(); i++) {
                tags.add(new Tag(geoJSON.getFeatures().get(i).getProperties().getInt("id"), geoJSON.getFeatures().get(i).getProperties().getInt("optE"), geoJSON.getFeatures().get(i).getProperties().getInt("optW"), geoJSON.getFeatures().get(i).getProperties().getInt("optS"), geoJSON.getFeatures().get(i).getProperties().getInt("optN"), (Point) geoJSON.getFeatures().get(i).getGeometry()));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

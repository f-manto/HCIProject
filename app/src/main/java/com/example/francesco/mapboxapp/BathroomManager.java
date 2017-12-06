package com.example.francesco.mapboxapp;

import android.content.Context;

import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.Point;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Drako on 05/12/2017.
 */

public class BathroomManager implements Runnable{
    ArrayList<Bathroom> bathrooms;
    Context appContext;

    public BathroomManager(Context applicationContext) {
        this.appContext=applicationContext;
    }

    @Override
    public void run() {
        bathrooms = new ArrayList<>();
        String bathroom;
        LoadJson string=new LoadJson();
        try {
            FeatureCollection geoJSON = (FeatureCollection) GeoJSON.parse(string.loadJSONFromAsset(appContext));
            for (int i = 0; i < geoJSON.getFeatures().size(); i++) {
                if(geoJSON.getFeatures().get(i).getProperties().isNull("room"))
                    bathroom="";
                else{
                    bathroom = geoJSON.getFeatures().get(i).getProperties().getString("room");
                }

                bathrooms.add(new Bathroom(geoJSON.getFeatures().get(i).getProperties().getInt("floor"), bathroom, geoJSON.getFeatures().get(i).getProperties().getString("gender"), (Point) geoJSON.getFeatures().get(i).getGeometry()));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

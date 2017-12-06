package com.example.francesco.mapboxapp;

import android.content.Context;
import android.util.Log;

import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Drako on 05/12/2017.
 */

public class Bathroom {
    String gender;
    int floor;
    String room;
    Point point;

    public Bathroom(int floor, String room, String gender, Point point) {
        this.floor=floor;
        this.room=room;
        this.gender= gender;
        this.point=point;
    }

    public int getFloor() {
        return floor;
    }

    public Point getPoint() {
        return point;
    }

    public String getGender() {
        return gender;
    }

    public String getRoom() {
        return room;
    }
}

package com.example.francesco.mapboxapp;

import com.cocoahero.android.geojson.Point;

/**
 * Created by pietrodimarco on 11/17/17.
 */

public class Tag {
    int id;
    int optE;
    int optW;
    int optS;
    int optN;
    Point point;



    public Tag(int id, int optE, int optW, int optS, int optN, Point point) {
        this.id=id;
        this.optE=optE;
        this.optN=optN;
        this.optS=optS;
        this.optW=optW;
        this.point=point;
    }
}

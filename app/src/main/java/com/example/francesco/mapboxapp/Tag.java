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
    String room;
    Point point;
    Tag father;


    public Tag(int id, int optE, int optW, int optS, int optN, String room, Point point) {
        this.id=id;
        this.optE=optE;
        this.optN=optN;
        this.optS=optS;
        this.room=room;
        this.optW=optW;
        this.point=point;
    }

    public String getRoom() {
        return room;
    }


    public void addFather(Tag iterationNode) {
        this.father=iterationNode;
    }

    public int getId() {
        return id;
    }
}

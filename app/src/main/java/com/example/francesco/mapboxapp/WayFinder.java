package com.example.francesco.mapboxapp;

import android.content.Context;
import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Point;

import org.json.JSONException;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by pietrodimarco on 11/17/17.
 */

public class WayFinder {

    ArrayList<Tag> tags;
    ArrayList<Path> possiblePaths;

    public WayFinder(Context applicationContext) {
        tags = new ArrayList<>();
        LoadJson string=new LoadJson();
        try {
            FeatureCollection geoJSON = (FeatureCollection) GeoJSON.parse(string.loadJSONFromAsset(applicationContext));
            for (int i = 0; i < geoJSON.getFeatures().size(); i++) {
                tags.add(new Tag(geoJSON.getFeatures().get(i).getProperties().getInt("id"), geoJSON.getFeatures().get(i).getProperties().getInt("optE"), geoJSON.getFeatures().get(i).getProperties().getInt("optW"), geoJSON.getFeatures().get(i).getProperties().getInt("optS"), geoJSON.getFeatures().get(i).getProperties().getInt("optN"), geoJSON.getFeatures().get(i).getProperties().getInt("room"), (Point) geoJSON.getFeatures().get(i).getGeometry()));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void startNavigation(int startingPoint,int destRoom) {
        Tag startingTag = null;
        for (int i = 0; i < tags.size(); i++)
            if (tags.get(i).getRoom() == startingPoint)
                startingTag = tags.get(i);

        //the frontier represent the nodes that have to be visited
        LinkedList<Tag> frontier = new LinkedList<>();

        //the frontier represent the future possible states
        ArrayList<Tag> futureStates;

        Tag iterationNode;

        if (!goalState(startingTag, destRoom)) {

            frontier.add(startingTag);
            while (!frontier.isEmpty()) {
                iterationNode = frontier.getFirst();
                frontier.removeFirst();
                futureStates = getFutureStates(iterationNode);//array futuri nodi
                for (int i = 0; i < futureStates.size(); i++) {
                        if (goalState(futureStates.get(i),destRoom)) {
                            printSolution(startingPoint,futureStates.get(i));
                            return;
                        } else
                            frontier.add(futureStates.get(i));
                    }
                }
            }

        }

    private void printSolution(int startingPoint, Tag goal) {
        while (goal.id!=startingPoint){
            Log.d("best",String.valueOf(goal.id));
            goal=goal.father;
        }

    }


    private ArrayList<Tag> getFutureStates(Tag iterationNode) {
        ArrayList<Tag> fut=new ArrayList<>();
        if(iterationNode.optW!=0){
            for (int i=0;i<tags.size();i++)
                if(tags.get(i).id==iterationNode.optW){
                    tags.get(i).addFather(iterationNode);
                    fut.add(tags.get(i));
                }

        }
        if(iterationNode.optS!=0){
            for (int i=0;i<tags.size();i++)
                if(tags.get(i).id==iterationNode.optS){
                    tags.get(i).addFather(iterationNode);
                    fut.add(tags.get(i));
                }
        }
        if(iterationNode.optN!=0){
            for (int i=0;i<tags.size();i++)
                if(tags.get(i).id==iterationNode.optN){
                    tags.get(i).addFather(iterationNode);
                    fut.add(tags.get(i));
                }
        }
        if(iterationNode.optE!=0){
            for (int i=0;i<tags.size();i++)
                if(tags.get(i).id==iterationNode.optE) {
                    tags.get(i).addFather(iterationNode);
                    fut.add(tags.get(i));
                }
        }
        return fut;
    }


    private boolean goalState(Tag child, int destRoom) {
    if (destRoom==child.room)
        return true;
    else return false;
    }



}

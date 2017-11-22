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

/**
 * Created by pietrodimarco on 11/17/17.
 */

public class WayFinder implements Runnable{

    ArrayList<Tag> tags;
    ArrayList<Path> possiblePaths;
    Context appContext;

    public WayFinder(Context applicationContext) {
        this.appContext=applicationContext;
    }

    public void startNavigation(int startingPoint,int destRoom) {

        Tag startingTag = null;
        Tag dest=null;
        for (int i = 0; i < tags.size(); i++){
            if(tags.get(i).getRoom()==destRoom)
                dest=tags.get(i);
        }

        for (int i = 0; i < tags.size(); i++){
            if (tags.get(i).getId() == startingPoint)
                startingTag = tags.get(i);
        }



        //the frontier represent the nodes that have to be visited
        LinkedList<Tag> frontier = new LinkedList<>();

        //the frontier represent the future possible states
        ArrayList<Tag> futureStates;

        ArrayList<Integer> alreadyVis=new ArrayList<>();



        Tag iterationNode;
        if (!goalState(startingTag, dest)) {
            frontier.add(startingTag);
            while (!frontier.isEmpty() ) {
                iterationNode = frontier.getFirst();
                alreadyVis.add(iterationNode.getId());

                frontier.removeFirst();
                futureStates = getFutureStates(iterationNode,alreadyVis);
                for (int i = 0; i < futureStates.size(); i++) {
                        if (goalState(futureStates.get(i),dest)) {
                            printSolution(startingPoint,futureStates.get(i));
                            return;
                        } else
                            frontier.add(futureStates.get(i));
                    }
                }
        }else printSolution(startingPoint,startingTag);
        Log.d("result","path non trovato");
    }

    private void printSolution(int startingPoint, Tag goal) {
        while (goal.id!=startingPoint){
            Log.d("goal",String.valueOf(goal.id));
            goal=goal.father;
        }
        Log.d("goal",String.valueOf(goal.getId()));

    }


    private ArrayList<Tag> getFutureStates(Tag iterationNode, ArrayList<Integer> alreadyVis) {
        ArrayList<Tag> fut=new ArrayList<>();
        if(iterationNode.optW!=0 && !alreadyVis.contains(iterationNode.optW)){
            for (int i=0;i<tags.size();i++)
                if(tags.get(i).id==iterationNode.optW){
                    tags.get(i).addFather(iterationNode);
                    fut.add(tags.get(i));
                }

        }
        if(iterationNode.optS!=0 && !alreadyVis.contains(iterationNode.optS)){
            for (int i=0;i<tags.size();i++)
                if(tags.get(i).id==iterationNode.optS){
                    tags.get(i).addFather(iterationNode);
                    fut.add(tags.get(i));
                }
        }
        if(iterationNode.optN!=0 && !alreadyVis.contains(iterationNode.optN)){
            for (int i=0;i<tags.size();i++)
                if(tags.get(i).id==iterationNode.optN){
                    tags.get(i).addFather(iterationNode);
                    fut.add(tags.get(i));
                }
        }
        if(iterationNode.optE!=0 && !alreadyVis.contains(iterationNode.optE)){
            for (int i=0;i<tags.size();i++)
                if(tags.get(i).id==iterationNode.optE) {
                    tags.get(i).addFather(iterationNode);
                    fut.add(tags.get(i));
                }
        }
        return fut;
    }


    private boolean goalState(Tag child, Tag destRoom) {
        if(destRoom.optE==child.getId() || destRoom.optN==child.getId() || destRoom.optS==child.getId() || destRoom.optW==child.getId())
            return true;
        else return false;
    }


    @Override
    public void run() {
        tags = new ArrayList<>();
        int room;
        LoadJson string=new LoadJson();
        try {
            FeatureCollection geoJSON = (FeatureCollection) GeoJSON.parse(string.loadJSONFromAsset(appContext));
            for (int i = 0; i < geoJSON.getFeatures().size(); i++) {
                if(geoJSON.getFeatures().get(i).getProperties().isNull("room"))
                    room=0;
                else
                    room=geoJSON.getFeatures().get(i).getProperties().getInt("room");
                tags.add(new Tag(geoJSON.getFeatures().get(i).getProperties().getInt("id"), geoJSON.getFeatures().get(i).getProperties().getInt("optE"), geoJSON.getFeatures().get(i).getProperties().getInt("optW"), geoJSON.getFeatures().get(i).getProperties().getInt("optS"), geoJSON.getFeatures().get(i).getProperties().getInt("optN"), room, (Point) geoJSON.getFeatures().get(i).getGeometry()));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        startNavigation(10,1060);
    }
}

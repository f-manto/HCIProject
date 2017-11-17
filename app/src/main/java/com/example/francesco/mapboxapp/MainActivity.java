package com.example.francesco.mapboxapp;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , IALocationListener
    , OnMapReadyCallback
        {

    private static final String SOURCE_ID = "com.mapbox.mapboxsdk.style.layers.symbol.source.id";
    private static final String LAYER_ID = "com.mapbox.mapboxsdk.style.layers.symbol.layer.id";

    private MapView mapView;
    private final int CODE_PERMISSIONS=1;
    private MarkerViewOptions marker;
    private IALocationManager mIALocationManager;
    private MapboxMap mapboxMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myTag", "This is my message");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions( this, neededPermissions, CODE_PERMISSIONS );



        Mapbox.getInstance(this, "pk.eyJ1IjoiZ3JvdXAzaGNpIiwiYSI6ImNqOXhkZTU0MDB0bnAzM3Bva2JyY2M2Mm8ifQ.wimKY4mWCu4Pr8SIOlR_Qg");
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        mIALocationManager = IALocationManager.create(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Handle if any of the permissions are denied, in grantResults
    }


    /**
     * Called when the activity will start interacting with the use
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mIALocationManager.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
       if (mIALocationManager != null) {
            mIALocationManager.removeLocationUpdates(this);
        }

    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {


        //WayFinder navigation=new WayFinder();

        //navigation.printPoints();

               // Point.fromCoordinates(Position.fromCoordinates(41.869912,-87.647903))) // Boston Common Park

        this.mapboxMap = mapboxMap;
        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Icon icon = iconFactory.fromResource(R.drawable.mapbox_mylocation_icon_default);
        marker = new MarkerViewOptions()
                .position(new LatLng(41.869912, -87.647903))
                .title("Location")
                .snippet("Welcome to you")
                .icon(icon);
        mapboxMap.addMarker(marker);

    }


/*
    *
     * Callback for receiving locations.
     * This is where location updates can be handled by moving markers or the camera.
*/
@Override
    public void onLocationChanged(IALocation location) {
       LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());



    Log.d("myTag", "Location updated");

        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Icon icon = iconFactory.fromResource(R.drawable.mapbox_mylocation_icon_default);

        MarkerViewOptions marker2 = new MarkerViewOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Location")
                .snippet(" ")
                .icon(icon);
        mapboxMap.addMarker(marker2);

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // N/A
    }


}

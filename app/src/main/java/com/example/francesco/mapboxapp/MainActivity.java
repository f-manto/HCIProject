package com.example.francesco.mapboxapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.gson.JsonElement;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.Filter;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.francesco.mapboxapp.data.BaseItem;
import com.example.francesco.mapboxapp.data.CustomDataProvider;
import com.example.francesco.mapboxapp.views.LevelBeamView;
import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.OnItemClickListener;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;
import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , IALocationListener
        , OnMapReadyCallback, MapboxMap.OnMapClickListener, LocationListener {

    private static final String SOURCE_ID = "com.mapbox.mapboxsdk.style.layers.symbol.source.id";
    private static final String LAYER_ID = "com.mapbox.mapboxsdk.style.layers.symbol.layer.id";

    private MapView mapView;
    private final int CODE_PERMISSIONS = 1;
    private MarkerViewOptions marker;
    private IALocationManager mIALocationManager;
    private IAResourceManager mResourceManager;
    private MapboxMap mapboxMap;
    private boolean mShowIndoorLocation = false;

    private Marker featureMarker;
    private PolylineOptions path;

    private LatLng currentLocation = new LatLng(41.869912, -87.647903);

    private MultiLevelListView multiLevelListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myTag", "This is my message");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        ActivityCompat.requestPermissions(this, neededPermissions, CODE_PERMISSIONS);


        Mapbox.getInstance(this, "pk.eyJ1IjoiZ3JvdXAzaGNpIiwiYSI6ImNqOXhkZTU0MDB0bnAzM3Bva2JyY2M2Mm8ifQ.wimKY4mWCu4Pr8SIOlR_Qg");
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        mIALocationManager = IALocationManager.create(this);
        mResourceManager = IAResourceManager.create(this);
        startListeningPlatformLocations();

        MapboxNavigation navigation = new MapboxNavigation(this, "pk.eyJ1IjoiZ3JvdXAzaGNpIiwiYSI6ImNqOXhkZTU0MDB0bnAzM3Bva2JyY2M2Mm8ifQ.wimKY4mWCu4Pr8SIOlR_Qg");


        FloatingActionButton locateButton = (FloatingActionButton) findViewById(R.id.locateButton);
        locateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                int start = getStartingPoint(currentLocation);
                getWPAndDrawPath(start, "1043");
            }
        });

        final Button floor1Button = findViewById(R.id.floor1Button);
        final Button floor2Button = findViewById(R.id.floor2Button);
        //LinearLayout barLayour=findViewById(R.id.searchBar);

        //barLayour.addView(new FloatingSearchView((this.getApplicationContext())));

        floor1Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                floorVisibility(1,VISIBLE);
                floorVisibility(2,NONE);
                floor1Button.setBackgroundColor(Color.GRAY);
                floor2Button.setBackgroundColor(Color.WHITE);
            }
        });

        floor2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                floorVisibility(2,VISIBLE);
                floorVisibility(1,NONE);
                floor2Button.setBackgroundColor(Color.GRAY);
                floor1Button.setBackgroundColor(Color.WHITE);

            }
        });






        confMenu();
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

    private void confMenu() {
        multiLevelListView = (MultiLevelListView) findViewById(R.id.multiLevelMenu);

        // custom ListAdapter
        ListAdapter listAdapter = new ListAdapter();

        multiLevelListView.setAdapter(listAdapter);
        multiLevelListView.setOnItemClickListener(mOnItemClickListener);

        listAdapter.setDataItems(CustomDataProvider.getInitialItems());
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        private void showItemDescription(Object object, ItemInfo itemInfo) {
            StringBuilder builder = new StringBuilder("\"");
            builder.append(((BaseItem) object).getName());
            builder.append("\" clicked!\n");
            builder.append(getItemInfoDsc(itemInfo));

            Toast.makeText(MainActivity.this, builder.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
            showItemDescription(item, itemInfo);
        }

        @Override
        public void onGroupItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
            showItemDescription(item, itemInfo);
        }
    };

    private class ListAdapter extends MultiLevelListAdapter {

        private class ViewHolder {
            TextView nameView;
            TextView infoView;
            ImageView arrowView;
            LevelBeamView levelBeamView;
        }

        @Override
        public List<?> getSubObjects(Object object) {
            // DIEKSEKUSI SAAT KLIK PADA GROUP-ITEM
            return CustomDataProvider.getSubItems((BaseItem) object);
        }

        @Override
        public boolean isExpandable(Object object) {
            return CustomDataProvider.isExpandable((BaseItem) object);
        }

        @Override
        public View getViewForObject(Object object, View convertView, ItemInfo itemInfo) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.data_item, null);
                //viewHolder.infoView = (TextView) convertView.findViewById(R.id.dataItemInfo);
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.dataItemName);
                viewHolder.arrowView = (ImageView) convertView.findViewById(R.id.dataItemArrow);
                viewHolder.levelBeamView = (LevelBeamView) convertView.findViewById(R.id.dataItemLevelBeam);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.nameView.setText(((BaseItem) object).getName());
            //viewHolder.infoView.setText(getItemInfoDsc(itemInfo));

            if (itemInfo.isExpandable()) {
                viewHolder.arrowView.setVisibility(View.VISIBLE);
                viewHolder.arrowView.setImageResource(itemInfo.isExpanded() ?
                        R.drawable.ic_expand_less : R.drawable.ic_expand_more);
            } else {
                viewHolder.arrowView.setVisibility(View.GONE);
            }

            viewHolder.levelBeamView.setLevel(itemInfo.getLevel());

            return convertView;
        }
    }

    private String getItemInfoDsc(ItemInfo itemInfo) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("level[%d], idx in level[%d/%d]",
                itemInfo.getLevel() + 1, /*Indexing starts from 0*/
                itemInfo.getIdxInLevel() + 1 /*Indexing starts from 0*/,
                itemInfo.getLevelSize()));

        if (itemInfo.isExpandable()) {
            builder.append(String.format(", expanded[%b]", itemInfo.isExpanded()));
        }
        return builder.toString();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

/*        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

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
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), this);
        mIALocationManager.registerRegionListener(mRegionListener);
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
            // unregister location & region changes
            mIALocationManager.removeLocationUpdates(this);
            mIALocationManager.registerRegionListener(mRegionListener);
        }

    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {


        this.mapboxMap = mapboxMap;
        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Icon icon = iconFactory.fromResource(R.drawable.mapbox_mylocation_icon_default);
        marker = new MarkerViewOptions()
                .position(new LatLng(41.869912, -87.647903))
                .title("Location")
                .snippet("Welcome to you")
                .icon(icon);


        mapboxMap.addMarker(marker);





        getWPAndDrawPath(21, "1033");

        floorVisibility(2, NONE);
        floorVisibility(1, VISIBLE);

        mapboxMap.setOnMapClickListener(this);

        mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull final Marker marker) {

                // The info window layout is created dynamically, parent is the info window
                // container
                LinearLayout parent = new LinearLayout(MainActivity.this);
                parent.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                parent.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MainActivity.this);
                title.setText(marker.getSnippet());

                Button button = new Button(MainActivity.this);
                button.setText("go");
                button.setPadding(2,2,2,2);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        Log.d("click:",marker.getSnippet());
                        getWPAndDrawPath(12, marker.getSnippet());
                    }
                });

                // add the image view to the parent layout
                parent.addView(title);
                parent.addView(button);
                parent.setBackgroundColor(Color.WHITE);


                return parent;
            }
        });

        Button floor1Button = findViewById(R.id.floor1Button);
        floor1Button.performClick();


    }


    /*
        *
         * Callback for receiving locations.
         * This is where location updates can be handled by moving markers or the camera.
    */
    @Override
    public void onLocationChanged(IALocation location) {

        Log.d("myTag", "Location updated");

        final LatLng center = new LatLng(location.getLatitude(), location.getLongitude());

        if (mShowIndoorLocation) {
            showLocationCircle(center, location.getAccuracy());

        }

        /*IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Icon icon = iconFactory.fromResource(R.drawable.mapbox_mylocation_icon_default);

        MarkerViewOptions marker2 = new MarkerViewOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Location")
                .snippet(" ")
                .icon(icon);
        mapboxMap.addMarker(marker2);*/

    }

    @Override
    public void onLocationChanged(Location location) {
        if(mapboxMap == null)
            return;
        if (!mShowIndoorLocation) {
            Log.d(TAG, "new LocationService location received with coordinates: " + location.getLatitude()
                    + "," + location.getLongitude());

            showLocationCircle(
                    new LatLng(location.getLatitude(), location.getLongitude()),
                    location.getAccuracy());
        }

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // N/A
    }

    @Override
    public void onProviderEnabled(String s) {

    }


    @Override
    public void onProviderDisabled(String s) {

    }

    public void showLocationOnMap(boolean show) {


    }

    public int getStartingPoint(LatLng latLng){

        WayFinder wayfinder = new WayFinder(getApplicationContext());

        return wayfinder.getStartingPoint(latLng);

    }

    public void getWPAndDrawPath(int startingPoint, String destRoom) {
        WayFinder navigation = new WayFinder(getApplicationContext(), startingPoint, destRoom, this);
        Thread nuovoThread = new Thread(navigation);
        nuovoThread.start();


    }

    public void drawPath(ArrayList<LatLng> points1, Iterable<LatLng> points2) {
        if (path!=null){
            mapboxMap.removePolyline(path.getPolyline());
        }

       /* path = new PolylineOptions()
                .addAll(points)
                .color(Color.parseColor("#3bb2d0"))
                .width(5);*/

       /* mapboxMap.addPolyline(path);*/

    }

    public void floorVisibility(int floor, String vis) {


        mapboxMap.getLayer("FLOOR" + floor + "_rooms").setProperties(visibility(vis));
        mapboxMap.getLayer("FLOOR" + floor + "_walls").setProperties(visibility(vis));
        mapboxMap.getLayer("FLOOR" + floor + "_labels").setProperties(visibility(vis));

    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (featureMarker != null) {
            mapboxMap.removeMarker(featureMarker);
        }

        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel);

        if (features.size() > 0) {
            for (Feature feature : features) {
                if (feature.getProperties() != null) {
                    for (Map.Entry<String, JsonElement> entry : feature.getProperties().entrySet()) {
                        if (entry.getKey().equals("Room")) {
                            featureMarker = mapboxMap.addMarker(new MarkerViewOptions()
                                    .position(point)
                                    .title("Room")
                                    .snippet(String.valueOf(entry.getValue()))
                            );
                            mapboxMap.selectMarker(featureMarker);
                            return;
                        }
                    }
                }
            }
        }
        featureMarker = mapboxMap.addMarker(new MarkerViewOptions()
                .position(point)
                .snippet("hello")
        );


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private String TAG = "IA";

    private IARegion.Listener mRegionListener = new IARegion.Listener() {
        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                final String newId = region.getId();


                mShowIndoorLocation = true;
            }
        }

        @Override
        public void onExitRegion(IARegion region) {


            mShowIndoorLocation = false;

        }

    };


    private void startListeningPlatformLocations() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }


    private void showLocationCircle(LatLng latLng, float accuracy) {

        marker.getMarker().getPosition().setLatitude(latLng.getLatitude());
        marker.getMarker().getPosition().setLongitude(latLng.getLongitude());
        //drawCircle(mapboxMap, latLng, Color.parseColor("#3bb2d0"),10);



    }

    private PolygonOptions generatePerimeter(LatLng centerCoordinates, double radiusInKilometers, int numberOfSides) {
        List<LatLng> positions = new ArrayList<>();
        double distanceX = radiusInKilometers / (111.319 * Math.cos(centerCoordinates.getLatitude() * Math.PI / 180));
        double distanceY = radiusInKilometers / 110.574;

        double slice = (2 * Math.PI) / numberOfSides;

        double theta;
        double x;
        double y;
        LatLng position;
        for (int i = 0; i < numberOfSides; ++i) {
            theta = i * slice;
            x = distanceX * Math.cos(theta);
            y = distanceY * Math.sin(theta);

            position = new LatLng(centerCoordinates.getLatitude() + y,
                    centerCoordinates.getLongitude() + x);
            positions.add(position);
        }
        return new PolygonOptions()
                .addAll(positions)
                .fillColor(Color.BLUE)
                .alpha(0.4f);
    }
}

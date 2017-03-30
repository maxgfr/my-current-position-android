package com.maxgfr.mapsmanipulation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Permission pour accéder au GPS
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 123 ;
    // Distance de rafraichissement
    private static final long LOCATION_REFRESH_DISTANCE = 1; // 10 meters
    // Temps de rafraichissement
    private static final long LOCATION_REFRESH_TIME = 1; // 1 minute

    private GoogleMap mMap;
    private HashMap<LatLng, String> allPosition;



    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        allPosition = new HashMap<>();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if(checkPermission()) {
            //Ma Position
            LatLng maPosition = getMyCurrentPosition();
            //Zoom la Camera sur ma position
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(maPosition, 16));
            //Ajoutons l'icone rouge
            mMap.addMarker(new MarkerOptions()
                    .position(maPosition)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }

        //Initialisation des markers
        //initMarker();

        for (Map.Entry<LatLng, String> entry : allPosition.entrySet()) {
            mMap.addMarker(new MarkerOptions()
                    .title(entry.getValue())
                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                    .position(entry.getKey()));
        }
    }

    private ArrayList<LocationProvider> getProviderGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ArrayList<LocationProvider> providers = new ArrayList<LocationProvider>();
        ArrayList<String> names = (ArrayList<String>) locationManager.getProviders(true);

        for (String name : names)
            providers.add(locationManager.getProvider(name));

        return providers;
    }

    private LatLng getMyCurrentPosition() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }
        //pour update la position
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
        //recuperons notre position grâce au gps
        Location localisation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //transformant la en Latlng
        LatLng maPos = new LatLng(localisation.getLatitude(), localisation.getLongitude());
        return maPos;
    }

    public HashMap<LatLng,String> getListElements () {
        return allPosition;
    }

    public void addPosition (LatLng p, String nom) {
        allPosition.put(p,nom);
    }

    private void initMarker () {
        LatLng lat = new LatLng(48.411509, -71.0156794);
        allPosition.put(lat,"marker1");
        LatLng lat2 = new LatLng(48.442509, -71.0226794);
        allPosition.put(lat2,"marker2");
        LatLng lat3 = new LatLng(48.498509, -71.0336794);
        allPosition.put(lat3,"marker3");
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    }

}
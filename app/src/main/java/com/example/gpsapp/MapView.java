package com.example.gpsapp;

import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<Location> savedLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MyApplication application= (MyApplication) getApplicationContext();
        savedLocation=application.getLocationList();
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng lastlocation=new LatLng(23,71);
        for (Location location:savedLocation) {
            LatLng latLng= new LatLng(location.getLatitude(),location.getLongitude());
            MarkerOptions markerOptions= new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Latitude: "+ location.getLatitude()+ " Longitude: "+ location.getLongitude());
            mMap.addMarker(markerOptions);
            lastlocation=latLng;
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastlocation,12.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Integer clicks= (Integer) marker.getTag();
                if(clicks==null){
                    clicks=0;
                }
                clicks ++;
                marker.setTag(clicks);
                Toast.makeText(MapView.this,"Marker "+ marker.getTitle() +" was clicked "+ marker.getTag()+"time(s)",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        

    }
}
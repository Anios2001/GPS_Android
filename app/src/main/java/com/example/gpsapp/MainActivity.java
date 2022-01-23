package com.example.gpsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int INT = 5;
    public static final int INT1 = 30;
    private static final int PERMISSON_FINE_LOCATION = 12;
    private static final int PERMISSON_COARSE_LOCATION = 13;
    TextView latitudeTxt, longitudeTxt, altitudeTxt, acurracyTxt, sensorTxt, updatesTxt,addressTxt,waypointCount;
    SwitchCompat locationUpdatesChk;
    SwitchCompat gpsChk;
    Button newWaypoint,showWaypoints,showMap;
    //Location Request is a config class that influence the way fused location provider works
    LocationRequest locationRequest;

    //Location Call back a object for location updates
    LocationCallback locationCallback;

    //Google's API For Location Services . The majority of app is based on this....
    FusedLocationProviderClient fusedLocationProviderClient;

    //current Location for keeping track of current locaton
    Location currentlocation;

    //list of current saved Locations
    List<Location> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitudeTxt = findViewById(R.id.editTextLatitude);
        longitudeTxt =  findViewById(R.id.editTextLongitude);
        altitudeTxt =  findViewById(R.id.editTextAltitude);
        acurracyTxt = findViewById(R.id.editTextAccuracy);
        sensorTxt = findViewById(R.id.editTextSensor);
        waypointCount=findViewById(R.id.waypointCount);
        updatesTxt =  findViewById(R.id.editTextUpdates);
        newWaypoint =  findViewById(R.id.newWaypointbtn);
        showWaypoints=findViewById(R.id.sh_waypoints);
        locationUpdatesChk =  findViewById(R.id.locationSwitch);
        gpsChk =  findViewById(R.id.gpsChk);
        addressTxt=findViewById(R.id.editTextAddress);
         showMap= findViewById(R.id.sh_Map);
        //set all propertjies of location Request
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INT1 * 1000);
        locationRequest.setFastestInterval(INT * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        showWaypoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ShowLocationlist.class);
                startActivity(intent);
            }
        });
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MapView.class);
                startActivity(intent);
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValues(locationResult.getLastLocation());
            }
        };
        newWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the gps location and
                // add the location to global list
                MyApplication application= (MyApplication) getApplicationContext();
                savedLocations= application.getLocationList();
                savedLocations.add(currentlocation);
            }
        });
        gpsChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsChk.isChecked()) {
                    //most accurate GPS use
                    sensorTxt.setText("Using GPS Sensors");
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                } else {
                    sensorTxt.setText("Using Network and Wifi sensors");
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }
            }
        });
        locationUpdatesChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationUpdatesChk.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });


        getUpdatedGpsLocation();
    }

    private void stopLocationUpdates() {
        updatesTxt.setText(R.string.stopTrackMessage);
        latitudeTxt.setText(R.string.noTrackMessage);
        longitudeTxt.setText(R.string.noTrackMessage);
        altitudeTxt.setText(R.string.noTrackMessage);
        acurracyTxt.setText(R.string.noTrackMessage);
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {
        updatesTxt.setText(R.string.UpdateMessage);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSON_FINE_LOCATION);
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSON_COARSE_LOCATION);
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        getUpdatedGpsLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSON_FINE_LOCATION:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    getUpdatedGpsLocation();
                }
                else
                {
                    Toast.makeText(this,"This app requires location Permisson",Toast.LENGTH_LONG).show();
                    finish();
                }
             break;
        }
    }

    //END OF ON CREATE METHOD
    public void getUpdatedGpsLocation(){
        //get permissons from user to track gps
        //get location from fused client
        //update the Ui
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            //user provided permisson
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                   //we got location now update
                    updateUIValues(location);
                    currentlocation=location;
                }
            });
        }
        else{
              //permissons not granted yet
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSON_FINE_LOCATION);
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSON_COARSE_LOCATION);
            }
        }
    }

    private void updateUIValues(Location location) {
        //Updatees all txt views with  results
       if(location!=null) {
           latitudeTxt.setText(String.valueOf(location.getLatitude()));
           longitudeTxt.setText(String.valueOf(location.getLongitude()));
           acurracyTxt.setText(String.valueOf(location.getAccuracy()));
           if (location.hasAltitude()) {
               altitudeTxt.setText(String.valueOf(location.getAltitude()));
           } else {
               altitudeTxt.setText(R.string.notAvailableMessage);
           }
       }
       else
       {
           latitudeTxt.setText(R.string.ErrorMessage);
           longitudeTxt.setText(R.string.ErrorMessage);
           acurracyTxt.setText(R.string.ErrorMessage);
           Toast.makeText(this,"Please Turn on the location services ",Toast.LENGTH_LONG).show();
       }
        Geocoder geocoder =new Geocoder(this);
       try{
           List<Address> addressList=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),2);
           addressTxt.setText(addressList.get(0).getAddressLine(0));
       }
       catch(Exception ex){
           addressTxt.setText("Unable to get Geo location");
       }
       //set no of locations saved in waypoint count
        MyApplication application= (MyApplication) getApplicationContext();
        savedLocations= application.getLocationList();
        waypointCount.setText(Integer.toString(savedLocations.size()));

    }
}
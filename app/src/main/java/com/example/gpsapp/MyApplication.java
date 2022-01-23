package com.example.gpsapp;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static MyApplication singleton;

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    private List<Location> locationList;


    public MyApplication getInstance(){
        return singleton;
    }
    public void onCreate(){
       super.onCreate();
       singleton=this;
       locationList= new ArrayList<>();
    }

}

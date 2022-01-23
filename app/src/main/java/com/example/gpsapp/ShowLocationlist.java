package com.example.gpsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.location.Location;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ShowLocationlist extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_locationlist);
        listView=findViewById(R.id.list_view);
        MyApplication application= (MyApplication)getApplicationContext();
        List<Location> savedLocations= application.getLocationList();
        listView.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1,savedLocations));


    }
}
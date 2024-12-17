package com.example.seguimientoderutas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String routeId = getIntent().getStringExtra("routeId");
        databaseReference = FirebaseDatabase.getInstance().getReference("rutas");

        if (routeId != null) {
            databaseReference = databaseReference.child(routeId);
        } else {
            databaseReference = databaseReference.child("actual");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        loadRouteFromFirebase();
    }

    private void loadRouteFromFirebase() {
        String routeId = getIntent().getStringExtra("routeId");
        DatabaseReference routeRef = routeId != null ? databaseReference.child(routeId) : databaseReference;

        routeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<LatLng> routePoints = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    double latitude = snapshot.child("latitude").getValue(Double.class);
                    double longitude = snapshot.child("longitude").getValue(Double.class);
                    routePoints.add(new LatLng(latitude, longitude));
                }

                Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(routePoints).width(5).color(0xFF0077FF));
                if (!routePoints.isEmpty()) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routePoints.get(0), 15));
                }
            }
        });
    }
}
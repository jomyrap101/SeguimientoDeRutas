package com.example.seguimientoderutas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference databaseReference;
    private boolean isTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnViewHistory = findViewById(R.id.btnViewHistory);
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, History.class);
            startActivity(intent);
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("rutas");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnShowMap = findViewById(R.id.btnShowMap);

        btnStart.setOnClickListener(v -> startTracking());
        btnStop.setOnClickListener(v -> stopTracking());
        btnShowMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, map.class);
            startActivity(intent);
        });

        setupLocationCallback();
    }

    private void startTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        isTracking = true;
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Toast.makeText(this, "Seguimiento iniciado", Toast.LENGTH_SHORT).show();
    }

    private void stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        isTracking = false;
        Toast.makeText(this, "Seguimiento detenido", Toast.LENGTH_SHORT).show();
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (isTracking) {
                    for (Location location : locationResult.getLocations()) {
                        saveLocationToFirebase(location);
                    }
                }
            }
        };
    }

    private void saveLocationToFirebase(Location location) {
        DatabaseReference routeRef = databaseReference.push();
        routeRef.setValue(location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTracking();
        } else {
            Toast.makeText(this, "Permiso de ubicación necesario", Toast.LENGTH_SHORT).show();
        }
    }
}
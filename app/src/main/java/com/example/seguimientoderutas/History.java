package com.example.seguimientoderutas;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> routeIds;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listViewRoutes);
        routeIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, routeIds);
        listView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("rutas");

        // Leer todas las rutas de Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                routeIds.clear();
                for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                    routeIds.add(routeSnapshot.getKey()); // Agregar ID de la ruta
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(History.this, "Error al cargar el historial", Toast.LENGTH_SHORT).show();
            }
        });

        // Manejar la selección de una ruta
        listView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            String routeId = routeIds.get(position);
            Intent intent = new Intent(History.this, map.class);
            intent.putExtra("routeId", routeId);
            startActivity(intent);
        });
    }
}
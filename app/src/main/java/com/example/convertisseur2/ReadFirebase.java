package com.example.convertisseur2;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ReadFirebase extends AppCompatActivity {

    Button logOut;
    Button add;
    EditText editCurrency;
    EditText editRate;
    ListView lstView;
    protected String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_data);
        logOut = (Button) findViewById(R.id.backToMenu);
        add = (Button) findViewById(R.id.addData);
        editCurrency = (EditText) findViewById(R.id.editC);
        editRate= (EditText) findViewById(R.id.editR);
        lstView = (ListView) findViewById(R.id.listViewFirebase);




        logOut.setOnClickListener(v -> {
            startActivity(new Intent(ReadFirebase.this, ConversionMainActivity.class));

        });

        add.setOnClickListener(v -> {
            HashMap<String, Object> addMap = new HashMap<>();
            String txtC = editCurrency.getText().toString();
            String txtR = editRate.getText().toString();
            addMap.put(txtC,txtR);
            if(txtC.isEmpty() || txtR.isEmpty()){
                Toast.makeText(ReadFirebase.this, "edit text empty", Toast.LENGTH_SHORT).show();
            }else{

                FirebaseDatabase.getInstance().getReference().child("currency data").child(txtC).updateChildren(addMap);
            }
        });


        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        lstView.setAdapter(adapter);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("currency data");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                FirebaseData fb = new FirebaseData();
                String currency = fb.getCurrency();
                String rate = fb.getRate();
                for(DataSnapshot snap : snapshot.getChildren()){
                    list.add(snap.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "App started");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "App paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "App destroyed");
    }
}

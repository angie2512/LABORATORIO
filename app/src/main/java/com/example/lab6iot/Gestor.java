package com.example.lab6iot;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6iot.databinding.ActivityIniciarSesionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Gestor extends AppCompatActivity {

    ActivityIniciarSesionBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIniciarSesionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


    }
}
package com.example.lab6iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6iot.databinding.ActivityIniciarSesionBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

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

        binding.btnIngresar.setOnClickListener(view -> {
            String usuarioIngresado = ((TextInputEditText) binding.inputEmail.getEditText()).getText().toString();
            String contrasenaIngresada = ((TextInputEditText) binding.inputPasswd.getEditText()).getText().toString();
            validarUsuario(usuarioIngresado, contrasenaIngresada);
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuarioIngresado,contrasenaIngresada);
        });

    }

    private void validarUsuario(String usuario, String password) {
        auth.signInWithEmailAndPassword(usuario, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("IniciarSesionActivity", "Inicio de sesión exitoso");

                        if (auth.getCurrentUser() != null) {
                            String correo = auth.getCurrentUser().getEmail();
                            agregarUsuarioAColeccion( usuario, password,"cliente");
                            redirigirSegunRol(correo);
                        }
                    } else {
                        Log.w("IniciarSesionActivity", "Inicio de sesión fallido", task.getException());

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Correo o contraseña incorrecta. Vuelva a ingresar sus datos.")
                                .setTitle("Aviso")
                                .setPositiveButton("Aceptar", (dialog, which) -> {
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
    }


    private void agregarUsuarioAColeccion(String email, String contrasena, String rol) {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("password", contrasena);
        usuario.put("email", email);
        usuario.put("rol", rol);

        db.collection("usuarios")
                .add(usuario)
                .addOnSuccessListener(documentReference -> {
                    Log.d("MainActivity", "Usuario agregado con ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("MainActivity", "Error al agregar usuario", e);
                });
    }


    private void redirigirSegunRol(String correo) {
        db.collection("usuarios")
                .whereEqualTo("email", correo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String rol = document.getString("rol");

                            if (rol != null) {
                                switch (rol) {

                                    case "cliente":
                                        Intent intentCliente = new Intent(MainActivity.this, Cliente.class);
                                        startActivity(intentCliente);
                                        break;
                                    case "gestor":
                                        Intent intentGestor= new Intent(MainActivity.this, Gestor.class);
                                        startActivity(intentGestor);
                                        break;
                                }
                                finish();
                            }
                        }
                    } else {
                        Log.w("IniciarSesionActivity", "Error al obtener el rol", task.getException());
                    }
                });
    }
}
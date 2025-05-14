package com.example.planpalmobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.example.planpalmobile.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import com.example.planpalmobile.EmailSender;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { getSupportActionBar().hide(); }

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Objects.requireNonNull(binding.etLoginEmail.getText()).toString().trim();
                String password = Objects.requireNonNull(binding.etLoginPassword.getText()).toString().trim();
                String opcInfo = Objects.requireNonNull(binding.etDescriptInfo.getText()).toString();

                binding.progressVarReg.setVisibility(View.VISIBLE);

                Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Rellene todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                    binding.progressVarReg.setVisibility(View.INVISIBLE);
                } else if (!emailPattern.matcher(username).matches()) {
                    Toast.makeText(RegisterActivity.this, "Introduzca un correo electrónico válido", Toast.LENGTH_SHORT).show();
                    binding.progressVarReg.setVisibility(View.INVISIBLE);
                } else if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "La contraseña debe tener al menos 6 carácteres", Toast.LENGTH_SHORT).show();
                    binding.progressVarReg.setVisibility(View.INVISIBLE);
                } else {


                    mAuth.createUserWithEmailAndPassword(username, password)
                            .addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Usario registrado con exito", Toast.LENGTH_SHORT).show();

                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        guardarUsuarioEnDatabase(username, opcInfo);
                                    }

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Error de base de datos " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                    binding.progressVarReg.setVisibility(View.INVISIBLE);
                                }

                            });


                }

            }

        });

    }

    public void guardarUsuarioEnDatabase(String username, String opcInfo) {
        String id = username.split("@")[0].replace(".", "");

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("id", id);
        usuario.put("email", username);
        usuario.put("opcInfo", opcInfo);

        db.collection("usuarios")
                .document(id)
                .set(usuario)
                .addOnCompleteListener(save -> {
                    Toast.makeText(RegisterActivity.this, "Guardado completado!", Toast.LENGTH_SHORT).show();

                    // Enviar correo de bienvenida
                    String asunto = "¡Bienvenido a PlanPal!";
                    String cuerpo = "Hola,\n\nGracias por registrarte en PlanPal. ¡Esperamos que disfrutes de nuestra aplicación para gestionar eventos!\n\nSaludos,\nEl equipo de PlanPal";

                    new EmailSender(username, asunto, cuerpo).enviar();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(save -> {
                    Toast.makeText(RegisterActivity.this, "Hubo un error al guardar al usuario", Toast.LENGTH_SHORT).show();
                });
    }

}
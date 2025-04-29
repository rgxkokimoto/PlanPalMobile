package com.example.planpalmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planpalmobile.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "Login";

    private ActivityLoginBinding binding;

    private final  FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {

            String usuario = mAuth.getCurrentUser().getEmail();
            if (usuario != null) {
                String id = usuario.split("@")[0].replace(".", "");
                Log.d(TAG, "ID: " + id);

                mandarUsuarioInicio(id);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { getSupportActionBar().hide(); }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(binding.etLoginEmail.getText());
                String password = String.valueOf(binding.etLoginPassword.getText());

                if (!email.isEmpty()) {
                    if (!password.isEmpty()) {

                        binding.progressVarLog.setVisibility(View.VISIBLE);

                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        new Handler().postDelayed(() -> {
                                            binding.progressVarLog.setVisibility(View.INVISIBLE);

                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "signInWithEmail:success");
                                                String id = email.split("@")[0].replace(".", "");
                                                mandarUsuarioInicio(id);
                                            } else {
                                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                            }
                                        }, 1000); // 1 segundo
                                    }
                                });
                    } else {
                        Toast.makeText(LoginActivity.this, "Introduzca la contraseña", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Introduzca el correo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tvRegistro.setOnClickListener( v -> startActivity(new Intent(this, RegisterActivity.class)));

    }

    public void mandarUsuarioInicio(String idUsuario) {
        SharedPreferences sharedPreferences = getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usuario", idUsuario);
        editor.apply();

        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

}
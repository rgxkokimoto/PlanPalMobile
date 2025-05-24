package com.example.planpalmobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.planpalmobile.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Uri imagenPerfilUri = null;
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        binding.ivProfilePic.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                abrirGaleria();
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                } else {
                    abrirGaleria();
                }
            }
        });

        binding.btnRegister.setOnClickListener(v -> {
            // Evitar múltiples clicks mientras se procesa
            binding.btnRegister.setEnabled(false);
            binding.progressVarReg.setVisibility(View.VISIBLE);

            String username = Objects.requireNonNull(binding.etUserName.getText()).toString().trim();
            String password = Objects.requireNonNull(binding.etLoginPassword.getText()).toString().trim();
            String opcInfo = Objects.requireNonNull(binding.etDescriptInfo.getText()).toString();

            Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellene todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                binding.progressVarReg.setVisibility(View.INVISIBLE);
                binding.btnRegister.setEnabled(true);
            } else if (!emailPattern.matcher(username).matches()) {
                Toast.makeText(this, "Introduzca un correo electrónico válido", Toast.LENGTH_SHORT).show();
                binding.progressVarReg.setVisibility(View.INVISIBLE);
                binding.btnRegister.setEnabled(true);
            } else if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                binding.progressVarReg.setVisibility(View.INVISIBLE);
                binding.btnRegister.setEnabled(true);
            } else {
                mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    String uid = firebaseUser.getUid();
                                    guardarUsuarioEnDatabase(uid, username, opcInfo);
                                }
                            } else {
                                Toast.makeText(this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                binding.progressVarReg.setVisibility(View.INVISIBLE);
                                binding.btnRegister.setEnabled(true);
                            }
                        });
            }
        });

        binding.tvToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a las imágenes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagenPerfilUri = data.getData();
            binding.ivProfilePic.setImageURI(imagenPerfilUri);
        }
    }

    private void guardarUsuarioEnDatabase(String id ,String name, String opcInfo) {

        if(binding.etLoginName.getText() == null) {
            name = name.split("@")[0];  // 'usuario' de usuario@gmail.com
        }

        if (imagenPerfilUri != null) {

            String finalName = name;
            FirebaseStorage.getInstance().getReference("fotosPerfil/" + name + ".jpg")
                    .putFile(imagenPerfilUri)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> guardarInfoConURL(id, finalName, opcInfo, uri.toString()))
                    )
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
                        binding.progressVarReg.setVisibility(View.INVISIBLE);
                        binding.btnRegister.setEnabled(true);
                    });
        } else {
            // Si no hay imagen, guardamos sin URL
            guardarInfoConURL(id, name, opcInfo, null);
        }
    }


    private void guardarInfoConURL(String uid, String name, String opcInfo, @Nullable String urlImagen) {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("name", name);
        usuario.put("opcInfo", opcInfo);
        if (urlImagen != null) usuario.put("fotoPerfil", urlImagen);

        db.collection("usuarios")
                .document(uid)
                .set(usuario)
                .addOnCompleteListener(task -> {
                    Toast.makeText(this, "¡Guardado completado!", Toast.LENGTH_SHORT).show();
                    binding.progressVarReg.setVisibility(View.INVISIBLE);

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                    binding.progressVarReg.setVisibility(View.INVISIBLE);
                    binding.btnRegister.setEnabled(true);
                });
    }

}


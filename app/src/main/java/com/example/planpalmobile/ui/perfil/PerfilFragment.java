package com.example.planpalmobile.ui.perfil;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.planpalmobile.LoginActivity;
import com.example.planpalmobile.R;
import com.example.planpalmobile.databinding.FragmentPerfilBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView correoTextView = root.findViewById(R.id.correoTextView);
        TextView descripcionTextView = root.findViewById(R.id.descripcionTextView);
        TextView nombreUsuarioTextView = root.findViewById(R.id.nombreUsuarioTextView);
        ImageView imagenPerfil = root.findViewById(R.id.imagenPerfil);
        Button btnCerrarSesion = root.findViewById(R.id.btnCerrarSesion);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser userLoged = FirebaseAuth.getInstance().getCurrentUser();

        if (userLoged != null) {
            String uid = userLoged.getUid();

            db.collection("usuarios")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String descripcion = document.getString("opcInfo");
                            String nombreUsuario = document.getString("name");
                            String urlFoto = document.getString("fotoPerfil");

                            descripcionTextView.setText("Descripción: " + (descripcion != null ? descripcion : ""));
                            nombreUsuarioTextView.setText(nombreUsuario != null ? nombreUsuario : "");
                            correoTextView.setText(userLoged.getEmail());

                            if (urlFoto != null && !urlFoto.isEmpty()) {
                                Glide.with(this)
                                        .load(urlFoto)
                                        .placeholder(R.drawable.imgperfil) // imagen por defecto mientras carga
                                        .error(R.drawable.imgperfil) // imagen si falla la carga
                                        .into(imagenPerfil);
                            } else {
                                imagenPerfil.setImageResource(R.drawable.imgperfil);
                            }
                        } else {
                            correoTextView.setText("No se encontraron datos.");
                            descripcionTextView.setText("");
                            imagenPerfil.setImageResource(R.drawable.imgperfil);
                        }
                    })
                    .addOnFailureListener(e -> {
                        correoTextView.setText("Error al cargar datos.");
                        descripcionTextView.setText("");
                        imagenPerfil.setImageResource(R.drawable.imgperfil);
                        Log.e("NotificationsFragment", "Error al obtener datos de Firestore", e);
                    });
        }

        btnCerrarSesion.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


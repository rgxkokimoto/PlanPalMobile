package com.example.planpalmobile.ui.perfil;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.planpalmobile.LoginActivity;
import com.example.planpalmobile.R;
import com.example.planpalmobile.databinding.FragmentPerfilBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import android.util.Log;

import java.util.Objects;


public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView correoTextView = root.findViewById(R.id.correoTextView);
        TextView descripcionTextView = root.findViewById(R.id.descripcionTextView);
        TextView nombreUsuarioTextView = root.findViewById(R.id.nombreUsuarioTextView);
        Button btnCerrarSesion = root.findViewById(R.id.btnCerrarSesion); // <-- Botón

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String email = user.getEmail();

            db.collection("usuarios")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String correo = document.getString("email");
                            String descripcion = document.getString("opcInfo");
                            String nombreUsuario = document.getString("id");

                            correoTextView.setText(getString(R.string.correo_alert) + correo);
                            descripcionTextView.setText(getString(R.string.descripci_n_alert) + descripcion);
                            nombreUsuarioTextView.setText(nombreUsuario);
                        } else {
                            correoTextView.setText("No se encontraron datos.");
                            descripcionTextView.setText("");
                        }
                    })
                    .addOnFailureListener(e -> {
                        correoTextView.setText(R.string.error_al_cargar_datos);
                        descripcionTextView.setText("");
                        Log.e("NotificationsFragment", "Error al obtener datos de Firestore", e);
                    });

        } else {
            correoTextView.setText(R.string.usuario_no_autenticado);
            descripcionTextView.setText("");
        }

        // ---------- Acción del botón Cerrar sesión ----------
        btnCerrarSesion.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        requireActivity().finish();
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
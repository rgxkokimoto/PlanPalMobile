package com.example.planpalmobile.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.planpalmobile.R;
import com.example.planpalmobile.databinding.FragmentNotificationsBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import android.util.Log;


public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Referencias a los nuevos TextViews
        TextView correoTextView = root.findViewById(R.id.correoTextView);
        TextView descripcionTextView = root.findViewById(R.id.descripcionTextView);
        TextView nombreUsuarioTextView = root.findViewById(R.id.nombreUsuarioTextView);

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

                            correoTextView.setText("Correo: " + correo);
                            descripcionTextView.setText("Descripción: " + descripcion);
                            nombreUsuarioTextView.setText("Nombre de usuario: " + nombreUsuario);
                        } else {
                            correoTextView.setText("No se encontraron datos.");
                            descripcionTextView.setText("");
                        }
                    })
                    .addOnFailureListener(e -> {
                        correoTextView.setText("Error al cargar datos.");
                        descripcionTextView.setText("");
                        Log.e("NotificationsFragment", "Error al obtener datos de Firestore", e);
                    });


        } else {
            correoTextView.setText("Usuario no autenticado.");
            descripcionTextView.setText("");
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
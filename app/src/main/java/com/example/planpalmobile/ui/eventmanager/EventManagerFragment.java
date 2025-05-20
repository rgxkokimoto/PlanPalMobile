package com.example.planpalmobile.ui.eventmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.planpalmobile.R;
import com.example.planpalmobile.data.entities.Evento;
import com.example.planpalmobile.databinding.FragmentEventManagerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventManagerFragment extends Fragment {

    private FragmentEventManagerBinding binding;
    private EventoAdapter adapter;
    private List<Evento> listaEventos = new ArrayList<>();
    private EventManagerViewModel eventManagerViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventManagerViewModel = new ViewModelProvider(this).get(EventManagerViewModel.class);

        // Configurar el RecyclerView y el adapter ANTES de observar LiveData
        EventoAdapter.OnEventoClickListener listener = new EventoAdapter.OnEventoClickListener() {
            @Override
            public void onVerDetallesClick(Evento evento) {
                mostrarDetalles(evento);
            }

            @Override
            public void onEliminarClick(Evento evento) {
                eventManagerViewModel.eliminarEventoDeFirestore(evento.getCodigo());
                adapter.removeEvento(evento); // método que tú ya tengas para actualizar el adapter

            }
        };

        adapter = new EventoAdapter(listaEventos, listener);
        binding.recyclerViewEventos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewEventos.setAdapter(adapter);

        // Obtener el userId desde SharedPreferences o FirebaseAuth
        String userId = obtenerUserIdActual();

        if (userId != null && !userId.isEmpty()) {
            eventManagerViewModel.cargarEventosDelUsuario(userId);
        }

        eventManagerViewModel.getEventosUsuario().observe(getViewLifecycleOwner(), eventos -> {
            Log.d("EventManagerFragment", "Observer eventos llamado con: " + (eventos != null ? eventos.size() : "null"));
            listaEventos.clear();
            if (eventos != null) {
                listaEventos.addAll(eventos);
            }
            adapter.notifyDataSetChanged();
        });


        // Botón FAB
        binding.fabButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_navigation_event_manager_to_createEventDetailFragment);
        });
    }


    private String obtenerUserIdActual() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            return user.getEmail().replace("@gmail.com", "");
        }
        return null;
    }


    private void mostrarDetalles(Evento evento) {
        Toast.makeText(requireContext(), "Detalles: " + evento.getCodigo(), Toast.LENGTH_SHORT).show();
        // Aquí puedes usar SafeArgs si estás pasando el evento a otro fragmento
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


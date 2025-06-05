package com.example.planpalmobile.ui.calendar.pmedf;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.planpalmobile.data.entities.Evento;
import com.example.planpalmobile.databinding.FragmentPickMeetEventDetailBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class PickMeetEventDetailFragment extends Fragment {

    private FragmentPickMeetEventDetailBinding binding;
    private MeetAdapter adapter;
    private PickMeetViewModelDetailFragment viewModel;
    private boolean isDescriptionExpanded = false;

    @Override
    public
    View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPickMeetEventDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(PickMeetViewModelDetailFragment.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();

        String id = getArguments().getString("codigo_evento");
        Log.d("PickMeetEventDetailFragment", "Código del evento recibido: " + id);

        if (id != null) {
            viewModel.cargarEvento(id);
        }

        viewModel.getEvento().observe(getViewLifecycleOwner(), this::bindEvento);

        binding.btnReservarCita.setOnClickListener(v -> {

            Evento evento = viewModel.getEvento().getValue();

            if (evento == null) {
                Toast.makeText(requireContext(), "Evento no cargado", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Date> fechasDisponibles = evento.getHorasDisponibles();

            if (fechasDisponibles == null || fechasDisponibles.isEmpty()) {
                Toast.makeText(requireContext(), "No hay fechas disponibles", Toast.LENGTH_SHORT).show();
                return;
            }

            PickDateDialogFragment dialog = new PickDateDialogFragment(fechasDisponibles, fechaSeleccionada -> {
                viewModel.reservarCita(id, fechaSeleccionada);
                Toast.makeText(requireContext(), "Cita selecinada.", Toast.LENGTH_SHORT).show();
            });

            dialog.show(getParentFragmentManager(), "PickDateDialog");
        });

    }

    private void bindEvento(Evento evento) {
        if (evento == null) {
            mostrarErrorCarga();
            return;
        }

        binding.tvTitlepPMED.setText(evento.getCodigo());
        binding.btnCategoria.setText(evento.getEtiqueta());
        mostrarFechas(evento);
        binding.tvDescription.setText(evento.getDescripcion());
        configurarToggleDescripcion();
        mostrarReservas(evento);
    }

    private void mostrarErrorCarga() {
        Toast.makeText(requireContext(), "Error cargando evento", Toast.LENGTH_SHORT).show();
    }

    private void mostrarFechas(Evento evento) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,EEE HH:mm", Locale.getDefault());
        String fechaInicio = sdf.format(evento.getHoraInicio());
        String fechaFin = sdf.format(evento.getHoraFin());
        String fechaFormateada = fechaInicio + " to " + fechaFin;
        binding.tvDateEvent.setText(fechaFormateada);
    }

    private void configurarToggleDescripcion() {
        binding.tvToggleDesc.setOnClickListener(v -> {
            isDescriptionExpanded = !isDescriptionExpanded;
            binding.tvDescription.setMaxLines(isDescriptionExpanded ? Integer.MAX_VALUE : 3);
            binding.tvToggleDesc.setText(isDescriptionExpanded ? "Ver menos" : "Ver más");
        });
    }

    private void mostrarReservas(Evento evento) {
        Map<Date, String> reservasMap = evento.getCitasReservadas();
        List<Map.Entry<Date, String>> listaReservas = reservasMap != null
                ? new ArrayList<>(reservasMap.entrySet())
                : new ArrayList<>();

        if (listaReservas.isEmpty()) {
            binding.viewStubEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.viewStubEmpty.setVisibility(View.GONE);
            adapter = new MeetAdapter(listaReservas);
            binding.rvListReservedMeets.setAdapter(adapter);
        }
    }

    private void setupRecyclerView() {
        adapter = new MeetAdapter(new ArrayList<>());
        binding.rvListReservedMeets.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListReservedMeets.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}


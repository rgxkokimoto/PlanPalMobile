package com.example.planpalmobile.ui.eventmanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.planpalmobile.databinding.FragmentCreateEventDetailBinding;

import java.util.Date;
import java.util.List;

public class CreateEventDetailFragment extends Fragment {

    private FragmentCreateEventDetailBinding binding;
    private List<Date> listDates;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCreateEventDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnNewDate.setOnClickListener(v -> {
            // TODO Desplegar un Time picker input
        });

        binding.btnCrear.setOnClickListener(v -> {
            // TODO Crear metodo para insertar datos al evento

            // TODO Volver a la pantalla anterior si todo es correcto
            Navigation.findNavController(v).popBackStack();
        });

        binding.btnCancelar.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        return root;
    }
}
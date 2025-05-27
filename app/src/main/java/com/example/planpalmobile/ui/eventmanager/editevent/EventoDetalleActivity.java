package com.example.planpalmobile.ui.eventmanager.editevent;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planpalmobile.databinding.ActivityEventoDetalleBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventoDetalleActivity extends AppCompatActivity {

    private ActivityEventoDetalleBinding binding;
    private final SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventoDetalleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setData();

        binding.btnVolver.setOnClickListener(v -> finish());

        binding.btnCodigo.setOnClickListener(v -> {
            // TODO Lógica para editar el código
        });

        binding.btnEditarDescripcion.setOnClickListener(v -> {
            // TODO Lógica para editar la descripción
        });

        binding.btnPickDay.setOnClickListener(v -> {
            // TODO Lógica para editar la fecha
        });

        binding.btnPickTime.setOnClickListener(v -> {
            // TODO Lógica para editar la hora
        });

        binding.btnPickDayEn.setOnClickListener(v -> {
            // TODO Lógica para editar la hora de finalización
        });

        binding.btnPickTimeEnd.setOnClickListener(v -> {
            // TODO Lógica para editar el código
        });

        binding.tvToggleDesc.setOnClickListener(v -> toggleDescripcion());
    }

    private void setData() {
        String codigo = getIntent().getStringExtra("codigo");
        String descripcion = getIntent().getStringExtra("descripcion");
        long inicioMillis = getIntent().getLongExtra("horaInicio", 0);
        long finMillis = getIntent().getLongExtra("horaFin", 0);
        Date inicio = new Date(inicioMillis);
        Date fin = new Date(finMillis);

        binding.btnCodigo.setText((codigo != null ? codigo : "Sin título"));
        binding.tvDescription.setText((descripcion != null ? descripcion : "Sin descripción"));
        binding.btnPickDay.setText(sdfFecha.format(inicio));
        binding.btnPickTime.setText(sdfHora.format(inicio));
        binding.btnPickDayEn.setText(sdfFecha.format(fin));
        binding.btnPickTimeEnd.setText(sdfHora.format(fin));
    }

    private void toggleDescripcion() {
        int maxLines = binding.tvDescription.getMaxLines();
        if (maxLines == 3) {
            binding.tvDescription.setMaxLines(Integer.MAX_VALUE);
            binding.tvToggleDesc.setText("Ver menos");
        } else {
            binding.tvDescription.setMaxLines(3);
            binding.tvToggleDesc.setText("Ver más");
        }
    }
}

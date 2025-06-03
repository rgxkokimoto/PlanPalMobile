package com.example.planpalmobile.ui.eventmanager.editevent;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.planpalmobile.R;
import com.example.planpalmobile.databinding.ActivityEventoDetalleBinding;
import com.example.planpalmobile.ui.eventmanager.comon.EventManagerViewModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class EventoDetalleActivity extends Fragment {

    private ActivityEventoDetalleBinding binding;
    private final SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private EventManagerViewModel eMvm;
    private String eventoId;


    @Override
    public View onCreateView(@NonNull LayoutInflater name, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityEventoDetalleBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        eMvm = new ViewModelProvider(requireActivity()).get(EventManagerViewModel.class);

        setData();

        binding.btnVolver.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        binding.btnCodigo.setOnClickListener(v -> {
            setNewTitle();
        });

        binding.btnEditarDescripcion.setOnClickListener(v -> {

            eMvm.putNewDesc(requireContext(), newDesc -> {

                if (newDesc != null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("descripcion", newDesc);
                    saveNewFieldResponse(data, updated -> {
                        if (updated) {
                            binding.tvDescription.setText(newDesc);
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar el campo descripcion", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar el campo descripcion", Toast.LENGTH_SHORT).show();
                }

            });


        });

        binding.btnPickDay.setOnClickListener(v -> {
            // Validar si la hora de inicio es posterior a la hora de fin dia + hora
            eMvm.showDatePicker(requireContext(), binding.btnPickDay, binding.btnPickDayEn);
            blanckButtons(R.color.blank_background);
        });

        binding.btnPickTime.setOnClickListener(v -> {
            // Validar si la hora de inicio es posterior a la hora de fin dia + hora
            eMvm.showTimePicker(requireContext(), binding.btnPickTime);
            blanckButtons(R.color.blank_background);
        });

        binding.btnPickDayEn.setOnClickListener(v -> {
            // Validar si la hora de inicio no es posterior a la hora de fin dia + hora
            eMvm.showDatePicker(requireContext(), binding.btnPickDayEn, null);
            blanckButtons(R.color.blank_background);
        });

        binding.btnPickTimeEnd.setOnClickListener(v -> {
            // Validar si la hora de inicio no es posterior a la hora de fin dia + hora
            eMvm.showTimePicker(requireContext(), binding.btnPickTimeEnd);
            blanckButtons(R.color.blank_background);
        });

        // TODO Alterar horas disponibles

        binding.tvToggleDesc.setOnClickListener(v -> toggleDescripcion());

        return root;
    }


    private void setNewTitle() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setText(binding.btnCodigo.getText());
        new AlertDialog.Builder(requireContext())
                .setTitle("Introduce una descripción")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {


                    Map<String, Object> data = new HashMap<>();
                    data.put("codigo", input.getText().toString());
                    saveNewFieldResponse(data, updated -> {
                        if (updated) {
                            binding.btnCodigo.setText(input.getText().toString());
                            eMvm.setTitle(input.getText().toString());
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar el campo titulo", Toast.LENGTH_SHORT).show();
                        }
                    });

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void saveNewFieldResponse(Map<String, Object> data, Consumer<Boolean> onUpdate) {

        eMvm.updateFieldEvent(eventoId, data, resp -> {
            if ("OK".equals(resp)) {
                onUpdate.accept(true);
            } else {
                onUpdate.accept(false);
            }
        });

    }

    private void setData() {
        Bundle args = getArguments();
        if (args != null) {
            String codigo = args.getString("codigo");
            String descripcion = args.getString("descripcion");
            long inicioMillis = args.getLong("horaInicio", 0);
            long finMillis = args.getLong("horaFin", 0);
            Date inicio = new Date(inicioMillis);
            Date fin = new Date(finMillis);
            String etiqueta = args.getString("etiqueta");
            eventoId = args.getString("id");
            Log.d("id", eventoId);

            eMvm.setEtiquetas(etiqueta);
            eMvm.setTitle(codigo);
            eMvm.setStartDate(inicio);
            eMvm.setEndDate(fin);
            eMvm.setDescription(descripcion);

            binding.btnCategoria.setText(etiqueta);
            binding.btnCodigo.setText((codigo != null ? codigo : "Sin título"));
            binding.btnPickDay.setText(sdfFecha.format(inicio));
            binding.btnPickTime.setText(sdfHora.format(inicio));
            binding.btnPickDayEn.setText(sdfFecha.format(fin));
            binding.btnPickTimeEnd.setText(sdfHora.format(fin));
            binding.tvDescription.setText((descripcion != null ? descripcion : "Sin descripción"));
            // TODO Lógica para cargar horas disponibles
        }
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

    private void blanckButtons(int blank_background) {
        binding.btnPickDay.setBackgroundColor(getResources().getColor(blank_background));
        binding.btnPickTime.setBackgroundColor(getResources().getColor(blank_background));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        eMvm.clearMutableData();
    }

}

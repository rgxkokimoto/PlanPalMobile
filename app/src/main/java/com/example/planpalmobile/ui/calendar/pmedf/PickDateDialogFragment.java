// PickDateDialogFragment.java
package com.example.planpalmobile.ui.calendar.pmedf;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PickDateDialogFragment extends DialogFragment {

    public interface OnDateSelectedListener {
        void onDateSelected(Date fechaSeleccionada);
    }

    private List<Date> fechasDisponibles;
    private OnDateSelectedListener listener;

    public PickDateDialogFragment(List<Date> fechasDisponibles, OnDateSelectedListener listener) {
        this.fechasDisponibles = fechasDisponibles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        List<String> opcionesFormateadas = new ArrayList<>();

        for (Date fecha : fechasDisponibles) {
            opcionesFormateadas.add(sdf.format(fecha));
        }

        String[] opcionesArray = opcionesFormateadas.toArray(new String[0]);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Elige una cita")
                .setItems(opcionesArray, (dialog, which) -> {
                    if (listener != null) {
                        listener.onDateSelected(fechasDisponibles.get(which));
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create();
    }
}

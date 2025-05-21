package com.example.planpalmobile.ui.eventmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planpalmobile.R;
import com.example.planpalmobile.data.entities.Evento;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    public void removeEvento(Evento evento) {
        int position = eventoList.indexOf(evento);
        if (position != -1) {
            eventoList.remove(position);
            notifyItemRemoved(position);
        }
    }


    public interface OnEventoClickListener {
        void onVerDetallesClick(Evento evento);
        void onEliminarClick(Evento evento);
    }



    private List<Evento> eventoList;
    private final OnEventoClickListener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public EventoAdapter(List<Evento> eventoList, OnEventoClickListener listener) {
        this.eventoList = eventoList != null ? eventoList : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<Evento> newList) {
        this.eventoList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventoList.get(position);

        // Mostrar título (o "Sin título")
        holder.tvTitulo.setText(evento.getCodigo() != null ? evento.getCodigo() : "Sin título");

        // Mostrar solo fecha de inicio
        if (evento.getHoraInicio() != null) {
            holder.tvFechas.setText(sdf.format(evento.getHoraInicio()));
        } else {
            holder.tvFechas.setText("Fecha no disponible");
        }

        holder.btnVerDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventoDetalleActivity.class);
            intent.putExtra("codigo", evento.getCodigo());
            intent.putExtra("descripcion", evento.getDescripcion());
            if (evento.getHoraInicio() != null) {
                intent.putExtra("horaInicio", evento.getHoraInicio().getTime());
            }
            if (evento.getHoraFin() != null) {
                intent.putExtra("horaFin", evento.getHoraFin().getTime());
            }
            v.getContext().startActivity(intent);
        });


        // holder.btnVerDetalles.setOnClickListener(v -> listener.onVerDetallesClick(evento));
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de que deseas eliminar este evento?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        listener.onEliminarClick(evento); // Aquí se llama a la lógica de eliminación
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }




    @Override
    public int getItemCount() {
        return eventoList.size();
    }

    static class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFechas, tvDescripcion;
        Button btnVerDetalles, btnEliminar;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloEvento);
            tvFechas = itemView.findViewById(R.id.tvFechasEvento);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}

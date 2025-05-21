package com.example.planpalmobile.ui.eventmanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.planpalmobile.R;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventoDetalleActivity extends AppCompatActivity {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detalle);

        TextView tvCodigo = findViewById(R.id.tvCodigo);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        TextView tvFechaInicio = findViewById(R.id.tvFechaInicio);
        TextView tvFechaFin = findViewById(R.id.tvFechaFin);

        String codigo = getIntent().getStringExtra("codigo");
        String descripcion = getIntent().getStringExtra("descripcion");
        long inicioMillis = getIntent().getLongExtra("horaInicio", 0);
        long finMillis = getIntent().getLongExtra("horaFin", 0);

        tvCodigo.setText("Título: " + (codigo != null ? codigo : "Sin título"));
        tvDescripcion.setText("Descripcion: " + (descripcion != null ? descripcion : "Sin descripcion"));


       /* if (descripcion != null && !descripcion.trim().isEmpty()) {
            tvDescripcion.setText("Descripción: " + descripcion);
        } else {
            tvDescripcion.setText("Descripción: Sin descripción");
        }*/

        tvFechaInicio.setText("Inicio: " + sdf.format(new java.util.Date(inicioMillis)));

        tvFechaFin.setText("Fin: " + sdf.format(new java.util.Date(finMillis)));


       /* if (finMillis > 0) {
            tvFechaFin.setText("Fin: " + sdf.format(new java.util.Date(finMillis)));
        } else {
            tvFechaFin.setText("Fin: Sin fecha");
        }*/


    }
}
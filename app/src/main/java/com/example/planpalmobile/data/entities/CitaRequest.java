package com.example.planpalmobile.data.entities;

import java.util.Date;

public class CitaRequest {

    private Date hora;
    private String usuarioId;

    public CitaRequest(Date hora, String usuarioId) {
        this.hora = hora;
        this.usuarioId = usuarioId;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void validate() {
        if (hora == null) {
            throw new IllegalArgumentException("La hora de la cita es requerida");
        }
        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del usuario es requerido");
        }
    }
}

package com.example.planpalmobile.data.entities;

import org.threeten.bp.LocalDateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Evento {

    private String id;
    private String codigo;
    private String descripcion;
    private Date horaInicio;
    private Date horaFin;
    private List<Date> horasDisponibles;
    private Map<Date, String> citasReservadas;
    private String creadorId;
    private String etiqueta;

    public Evento() {}

    public Evento(String codigo, String descripcion, Date horaInicio, Date horaFin, List<Date> horasDisponibles, Map<Date, String> citasReservadas, String creadorId
        , String etiqueta
    ) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.horasDisponibles = horasDisponibles;
        this.citasReservadas = citasReservadas;
        this.creadorId = creadorId;
        this.etiqueta = etiqueta;
    }

    public Evento(String id, String codigo, String descripcion, Date horaInicio, Date horaFin, List<Date> horasDisponibles, Map<Date, String> citasReservadas, String creadorId
                  , String etiqueta)
    {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.horasDisponibles = horasDisponibles;
        this.citasReservadas = citasReservadas;
        this.creadorId = creadorId;
        this.etiqueta = etiqueta;
    }

    public String getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraFin() { return horaFin; }

    public void setHoraFin(Date horaFin) {
        this.horaFin = horaFin;
    }

    public List<Date> getHorasDisponibles() {
        return horasDisponibles;
    }

    public void setHorasDisponibles(List<Date> horasDisponibles) {
        this.horasDisponibles = horasDisponibles;
    }

    public Map<Date, String> getCitasReservadas() {
        return citasReservadas;
    }

    public void setCitasReservadas(Map<Date, String> citasReservadas) {
        this.citasReservadas = citasReservadas;
    }

    public String getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(String creadorId) {
        this.creadorId = creadorId;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}

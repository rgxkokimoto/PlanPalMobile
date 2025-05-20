package com.example.planpalmobile.data.dto;

import java.util.Date;

/**
 *  Las clases dto sirven para traspasar datos entre la aplicación de forma más flexible
 *  permitiendo indexar los datos requeridos
 */
public class EventoDTOItem {
    public String id;
    public String codigo;
    public Date horaInicio;

    public EventoDTOItem(String codigo, Date horaInicio, String id) {
        this.codigo = codigo;
        this.id = id;
        this.horaInicio = horaInicio;
    }

    public String getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }
}

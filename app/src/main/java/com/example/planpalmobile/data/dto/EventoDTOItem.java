package com.example.planpalmobile.data.dto;

import java.util.Date;

/**
 *  Las clases dto sirven para traspasar datos entre la aplicación de forma más flexible
 *  permitiendo indexar los datos requeridos
 */
public class EventoDTOItem {
    public String codigo;
    public String descripcion;
    public Date horaInicio;

    public EventoDTOItem(String codigo, String descripcion, Date horaInicio) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.horaInicio = horaInicio;
    }

}

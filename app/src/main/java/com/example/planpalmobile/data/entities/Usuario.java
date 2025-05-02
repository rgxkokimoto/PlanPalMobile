package com.example.planpalmobile.data.entities;

import java.util.List;

public class Usuario {
    private String id;

    private String nombre;

    private String correo;

    private String contrasena;

    private String fotoPerfil;

    private String informacionAdicional;

    private List<String> eventosCreadosIds;

    // Constructor vacío
    public Usuario() {}

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getInformacionAdicional() {
        return informacionAdicional;
    }

    public void setInformacionAdicional(String informacionAdicional) {
        this.informacionAdicional = informacionAdicional;
    }

    public List<String> getEventosCreadosIds() {
        return eventosCreadosIds;
    }

    public void setEventosCreadosIds(List<String> eventosCreadosIds) {
        this.eventosCreadosIds = eventosCreadosIds;
    }

}

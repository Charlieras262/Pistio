package com.javier.pistio.modelos;

public class Soporte {
    private String nombre, apellido, usuario, pass;

    public Soporte() {
    }

    public Soporte(String nombre, String apellido, String usuario, String pass) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.pass = pass;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "{" +
                "\"nombre\" : \"" + nombre + "\", " +
                "\"apellido\" : \"" + apellido + "\", " +
                "\"usuario\" : \"" + usuario + "\", " +
                "\"pass\" : \"" + pass + "\", " +
                "\"type\" : \"S\"" +
                '}';
    }
}

package com.javier.pistio.modelos;

import javafx.beans.property.SimpleStringProperty;

public class Soporte {
    private String _id, nombre, apellido, usuario, pass, type;
    private boolean pref;

    public Soporte() {
    }

    public Soporte(String nombre, String apellido, String usuario, String pass, boolean pref) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.pass = pass;
        this.type = "S";
        this.pref = pref;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public SimpleStringProperty idProperty() {
        return new SimpleStringProperty(_id);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public SimpleStringProperty nombreProperty() {
        return new SimpleStringProperty(nombre);
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public SimpleStringProperty apellidoProperty() {
        return new SimpleStringProperty(apellido);
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public SimpleStringProperty usuarioProperty() {
        return new SimpleStringProperty(usuario);
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public SimpleStringProperty passProperty() {
        return new SimpleStringProperty(pass);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SimpleStringProperty typeProperty() {
        return new SimpleStringProperty(type.equals("C") ? "Caja" : type.equals("S") ? "Atención al Cliente" : type.equals("R") ? "Créditos" : type.equals("G") ? "Gestor" : "Turno");
    }

    public boolean isPref() {
        return pref;
    }

    public void setPref(boolean pref) {
        this.pref = pref;
    }

    public SimpleStringProperty prefsProperty() {
        String prefStr = pref ? "Preferencia" : "Sin Preferencia";
        return new SimpleStringProperty(prefStr);
    }

    @Override
    public String toString() {
        return "{" +
                "\"nombre\" : \"" + nombre + "\", " +
                "\"apellido\" : \"" + apellido + "\", " +
                "\"usuario\" : \"" + usuario + "\", " +
                "\"pass\" : \"" + pass + "\", " +
                "\"type\" : \"" + type + "\"" +
                '}';
    }

    public String toJSON() {
        return "{" +
                "\"_id\" : \"" + _id + "\", " +
                "\"nombre\" : \"" + nombre + "\", " +
                "\"apellido\" : \"" + apellido + "\", " +
                "\"usuario\" : \"" + usuario + "\", " +
                "\"pass\" : \"" + pass + "\", " +
                "\"type\" : \"" + type + "\", " +
                "\"pref\" : " + pref + "" +
                '}';
    }
}

package com.javier.pistio.modelos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Turno {
    private String _id, type;
    private int correl;
    private boolean pref;

    public Turno() {
    }

    public Turno(String _id, String type, int correl, boolean pref) {
        this._id = _id;
        this.type = type;
        this.correl = correl;
        this.pref = pref;
    }

    public String get_id() {
        return _id;
    }

    public SimpleStringProperty _idProperty() {
        return new SimpleStringProperty(_id);
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public SimpleStringProperty typeProperty() {
        return new SimpleStringProperty(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCorrel() {
        return correl;
    }

    public SimpleIntegerProperty correlProperty() {
        return new SimpleIntegerProperty(correl);
    }

    public void setCorrel(int correl) {
        this.correl = correl;
    }

    public boolean isPref() {
        return pref;
    }

    public void setPref(boolean pref) {
        this.pref = pref;
    }
}

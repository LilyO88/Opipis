package com.lidorttol.opipis.data;

public class Banio {

    String direccion;
    String id_banio;
    double latitud;
    double longitud;
    double puntuacion;

    public Banio() {
    }

    public Banio(String id_banio, String direccion, double latitud, double longitud, double puntuacion) {
        this.direccion = direccion;
        this.id_banio = id_banio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.puntuacion = puntuacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getId_banio() {
        return id_banio;
    }

    public void setId_banio(String id_banio) {
        this.id_banio = id_banio;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(double puntuacion) {
        this.puntuacion = puntuacion;
    }
}

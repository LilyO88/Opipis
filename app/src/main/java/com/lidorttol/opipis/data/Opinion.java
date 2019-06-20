package com.lidorttol.opipis.data;

import java.util.Date;

public class Opinion {

    private String id_banio;
    private String id_opinion;
    private String usuario;
    private String idUsuario;
    private String comentario;
    private Date fecha;
    private double global;
    private double limpieza;
    private double tamanio;
    private boolean pestillo;
    private boolean papel;
    private boolean minusvalido;
    private boolean unisex;

    public Opinion() {
    }

    public Opinion(String id_banio, String id_opinion, String usuario, String idUsuario, String comentario, Date fecha,
                   double global, double limpieza, double tamanio, boolean pestillo, boolean papel,
                   boolean minusvalido, boolean unisex) {
        this.id_banio = id_banio;
        this.id_opinion = id_opinion;
        this.usuario = usuario;
        this.idUsuario = idUsuario;
        this.comentario = comentario;
        this.fecha = fecha;
        this.global = global;
        this.limpieza = limpieza;
        this.tamanio = tamanio;
        this.pestillo = pestillo;
        this.papel = papel;
        this.minusvalido = minusvalido;
        this.unisex = unisex;
    }

    public String getId_banio() {
        return id_banio;
    }

    public void setId_banio(String id_banio) {
        this.id_banio = id_banio;
    }

    public String getId_opinion() {
        return id_opinion;
    }

    public void setId_opinion(String id_opinion) {
        this.id_opinion = id_opinion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getGlobal() {
        return global;
    }

    public void setGlobal(double global) {
        this.global = global;
    }

    public double getLimpieza() {
        return limpieza;
    }

    public void setLimpieza(double limpieza) {
        this.limpieza = limpieza;
    }

    public double getTamanio() {
        return tamanio;
    }

    public void setTamanio(double tamanio) {
        this.tamanio = tamanio;
    }

    public boolean isPestillo() {
        return pestillo;
    }

    public void setPestillo(boolean pestillo) {
        this.pestillo = pestillo;
    }

    public boolean isPapel() {
        return papel;
    }

    public void setPapel(boolean papel) {
        this.papel = papel;
    }

    public boolean isMinusvalido() {
        return minusvalido;
    }

    public void setMinusvalido(boolean minusvalido) {
        this.minusvalido = minusvalido;
    }

    public boolean isUnisex() {
        return unisex;
    }

    public void setUnisex(boolean unisex) {
        this.unisex = unisex;
    }
}

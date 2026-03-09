package com.aeroruta.model;

public class Ruta {
    private int id;
    private Aeropuerto origen;
    private Aeropuerto destino;
    private double costoBase;
    private double descuento;
    private String aerolinea;
    private double duracionHoras;
    private boolean activa;

    public Ruta(Aeropuerto origen, Aeropuerto destino, double costoBase, double descuento, String aerolinea, double duracionHoras) {
        this.origen = origen;
        this.destino = destino;
        this.costoBase = costoBase;
        this.descuento = descuento;
        this.aerolinea = aerolinea;
        this.duracionHoras = duracionHoras;
        this.activa = true;
    }

    public double getCostoFinal() {
        return costoBase - descuento;
    }

    public double getPesoGrafo() {
        return getCostoFinal();
    }

    public boolean esValida() {
        return origen != null && destino != null && !origen.equals(destino);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Aeropuerto getOrigen() {
        return origen;
    }

    public void setOrigen(Aeropuerto origen) {
        this.origen = origen;
    }

    public Aeropuerto getDestino() {
        return destino;
    }

    public void setDestino(Aeropuerto destino) {
        this.destino = destino;
    }

    public double getCostoBase() {
        return costoBase;
    }

    public void setCostoBase(double costoBase) {
        this.costoBase = costoBase;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public String getAerolinea() {
        return aerolinea;
    }

    public void setAerolinea(String aerolinea) {
        this.aerolinea = aerolinea;
    }

    public double getDuracionHoras() {
        return duracionHoras;
    }

    public void setDuracionHoras(double d) {
        this.duracionHoras = d;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return origen.getCodigoIATA() + " → " + destino.getCodigoIATA() + " ($" + String.format("%.2f", getCostoFinal()) + ")";
    }
}
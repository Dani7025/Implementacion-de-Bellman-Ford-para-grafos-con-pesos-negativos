package com.aeroruta.model;

import java.util.ArrayList;
import java.util.List;

public class ResultadoRuta {
    private Aeropuerto origen;
    private Aeropuerto destino;
    private List<Aeropuerto> escalas;
    private List<Ruta> rutasUsadas;
    private double costoTotal;
    private boolean hayError;
    private String mensajeError;

    public ResultadoRuta() {
        this.escalas = new ArrayList<>();
        this.rutasUsadas = new ArrayList<>();
        this.costoTotal = 0.0;
        this.hayError = false;
        this.mensajeError = "";
    }

    public int getCantidadEscalas() {
        return Math.max(0, rutasUsadas.size() - 1);
    }

    public boolean esDirecto() {
        return getCantidadEscalas() == 0 && !rutasUsadas.isEmpty();
    }

    public String getResumen() {
        if (hayError) return "ERROR: " + mensajeError;
        StringBuilder sb = new StringBuilder();
        sb.append("Ruta: ").append(origen.getCodigoIATA());
        for (Aeropuerto e : escalas) sb.append(" → ").append(e.getCodigoIATA());
        sb.append(" → ").append(destino.getCodigoIATA());
        sb.append("\nCosto total: $").append(String.format("%.2f", costoTotal));
        sb.append("\nEscalas: ").append(getCantidadEscalas());
        return sb.toString();
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

    public List<Aeropuerto> getEscalas() {
        return escalas;
    }

    public void setEscalas(List<Aeropuerto> escalas) {
        this.escalas = escalas;
    }

    public List<Ruta> getRutasUsadas() {
        return rutasUsadas;
    }

    public void setRutasUsadas(List<Ruta> rutasUsadas) {
        this.rutasUsadas = rutasUsadas;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }

    public boolean isHayError() {
        return hayError;
    }

    public void setHayError(boolean hayError) {
        this.hayError = hayError;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
}
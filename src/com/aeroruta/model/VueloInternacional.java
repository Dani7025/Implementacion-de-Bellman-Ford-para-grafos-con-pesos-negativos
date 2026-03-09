package com.aeroruta.model;

public class VueloInternacional extends Ruta {
    private double impuestoInternacional;
    private double tasaAeropuerto;

    public VueloInternacional(Aeropuerto origen, Aeropuerto destino, double costoBase, double descuento, String aerolinea, double duracionHoras, double impuestoInternacional, double tasaAeropuerto) {
        super(origen, destino, costoBase, descuento, aerolinea, duracionHoras);
        this.impuestoInternacional = impuestoInternacional;
        this.tasaAeropuerto = tasaAeropuerto;
    }

    @Override
    public double getPesoGrafo() {
        return (getCostoFinal() * (1 + impuestoInternacional)) + tasaAeropuerto;
    }

    public double getImpuestoInternacional() {
        return impuestoInternacional;
    }

    public void setImpuestoInternacional(double v) {
        this.impuestoInternacional = v;
    }

    public double getTasaAeropuerto() {
        return tasaAeropuerto;
    }

    public void setTasaAeropuerto(double v) {
        this.tasaAeropuerto = v;
    }

    @Override
    public String toString() {
        return "[INTL] " + super.toString() + " +Imp:" + (int) (impuestoInternacional * 100) + "%" + " +Tasa:$" + String.format("%.2f", tasaAeropuerto);
    }
}
package com.aeroruta.model;

public class VueloDomestico extends Ruta {
    private double impuestoNacional;

    public VueloDomestico(Aeropuerto origen, Aeropuerto destino, double costoBase, double descuento, String aerolinea, double duracionHoras, double impuestoNacional) {
        super(origen, destino, costoBase, descuento, aerolinea, duracionHoras);
        this.impuestoNacional = impuestoNacional;
    }

    @Override
    public double getPesoGrafo() {
        return getCostoFinal() * (1 + impuestoNacional);
    }

    public double getImpuestoNacional() {
        return impuestoNacional;
    }

    public void setImpuestoNacional(double impuestoNacional) {
        this.impuestoNacional = impuestoNacional;
    }

    @Override
    public String toString() {
        return "[DOM] " + super.toString() + " +Imp:" + (int) (impuestoNacional * 100) + "%";
    }
}
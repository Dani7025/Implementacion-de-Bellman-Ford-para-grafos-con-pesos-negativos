package com.aeroruta.model;

import java.util.ArrayList;
import java.util.List;

public class GrafoAereo {
    private List<Aeropuerto> aeropuertos;
    private List<Ruta> rutas;

    public GrafoAereo() {
        this.aeropuertos = new ArrayList<>();
        this.rutas = new ArrayList<>();
    }

    public void agregarAeropuerto(Aeropuerto aeropuerto) {
        if (!existeAeropuerto(aeropuerto.getCodigoIATA())) aeropuertos.add(aeropuerto);
    }

    public void agregarRuta(Ruta ruta) {
        if (ruta.esValida() && !existeRutaDuplicada(ruta)) rutas.add(ruta);
    }

    public void eliminarAeropuerto(String codigoIATA) {
        aeropuertos.removeIf(a -> a.getCodigoIATA().equalsIgnoreCase(codigoIATA));
        rutas.removeIf(r -> r.getOrigen().getCodigoIATA().equalsIgnoreCase(codigoIATA) || r.getDestino().getCodigoIATA().equalsIgnoreCase(codigoIATA));
    }

    public void eliminarRuta(int id) {
        rutas.removeIf(r -> r.getId() == id);
    }

    public boolean existeAeropuerto(String codigoIATA) {
        for (Aeropuerto a : aeropuertos)
            if (a.getCodigoIATA().equalsIgnoreCase(codigoIATA)) return true;
        return false;
    }

    public boolean existeRutaDuplicada(Ruta nueva) {
        for (Ruta r : rutas)
            if (r.getOrigen().equals(nueva.getOrigen()) && r.getDestino().equals(nueva.getDestino()) && r.getAerolinea().equalsIgnoreCase(nueva.getAerolinea()))
                return true;
        return false;
    }

    public Aeropuerto buscarAeropuerto(String codigoIATA) {
        for (Aeropuerto a : aeropuertos) if (a.getCodigoIATA().equalsIgnoreCase(codigoIATA)) return a;
        return null;
    }

    public List<Ruta> getRutasDesde(String codigoOrigen) {
        List<Ruta> salida = new ArrayList<>();
        for (Ruta r : rutas)
            if (r.isActiva() && r.getOrigen().getCodigoIATA().equalsIgnoreCase(codigoOrigen)) salida.add(r);
        return salida;
    }

    public List<Aeropuerto> getAeropuertos() {
        return aeropuertos;
    }

    public List<Ruta> getRutas() {
        return rutas;
    }

    public int getCantidadNodos() {
        return aeropuertos.size();
    }
}
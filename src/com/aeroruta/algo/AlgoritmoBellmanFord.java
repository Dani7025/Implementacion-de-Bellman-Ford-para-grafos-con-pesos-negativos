package com.aeroruta.algo;

import com.aeroruta.model.*;

import java.util.*;

public class AlgoritmoBellmanFord implements IAlgoritmoGrafo {
    private GrafoAereo grafo;

    public AlgoritmoBellmanFord(GrafoAereo grafo) {
        this.grafo = grafo;
    }

    @Override
    public ResultadoRuta ejecutar(String codigoOrigen, String codigoDestino) {
        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> predecesores = new HashMap<>();
        Map<String, Ruta> rutaPredecesora = new HashMap<>();
        ResultadoRuta resultado = new ResultadoRuta();
        if (!grafo.existeAeropuerto(codigoOrigen) || !grafo.existeAeropuerto(codigoDestino)) {
            resultado.setHayError(true);
            resultado.setMensajeError("El aeropuerto de origen o destino no existe en el sistema.");
            return resultado;
        }
        for (Aeropuerto a : grafo.getAeropuertos()) distancias.put(a.getCodigoIATA(), Double.MAX_VALUE);
        distancias.put(codigoOrigen, 0.0);
        int cantidadNodos = grafo.getCantidadNodos();
        for (int i = 1; i < cantidadNodos; i++)
            for (Ruta ruta : grafo.getRutas())
                if (ruta.isActiva()) relajar(ruta, distancias, predecesores, rutaPredecesora);

        for (Ruta ruta : grafo.getRutas()) {
            if (!ruta.isActiva()) continue;
            String u = ruta.getOrigen().getCodigoIATA();
            String v = ruta.getDestino().getCodigoIATA();
            double peso = ruta.getPesoGrafo();
            if (distancias.get(u) != Double.MAX_VALUE && distancias.get(u) + peso < distancias.get(v)) {
                resultado.setHayError(true);
                resultado.setMensajeError("¡ALERTA! Se detectó un ciclo negativo (Ruta fraudulenta o bonificación infinita).");
                return resultado;
            }
        }
        if (distancias.get(codigoDestino) == Double.MAX_VALUE) {
            resultado.setHayError(true);
            resultado.setMensajeError("No existe ninguna ruta posible que conecte " + codigoOrigen + " con " + codigoDestino + ".");
            return resultado;
        }
        return reconstruirCamino(codigoOrigen, codigoDestino, resultado, distancias, predecesores, rutaPredecesora);
    }

    // Método interno de Relajación Matemática
    private void relajar(Ruta ruta, Map<String, Double> distancias, Map<String, String> predecesores, Map<String, Ruta> rutaPredecesora) {
        String u = ruta.getOrigen().getCodigoIATA();
        String v = ruta.getDestino().getCodigoIATA();
        double peso = ruta.getPesoGrafo();
        if (distancias.get(u) != Double.MAX_VALUE && distancias.get(u) + peso < distancias.get(v)) {
            distancias.put(v, distancias.get(u) + peso);
            predecesores.put(v, u);
            rutaPredecesora.put(v, ruta);
        }
    }

    private ResultadoRuta reconstruirCamino(String origen, String destino, ResultadoRuta resultado, Map<String, Double> distancias, Map<String, String> predecesores, Map<String, Ruta> rutaPredecesora) {
        List<Aeropuerto> caminoNodos = new ArrayList<>();
        List<Ruta> rutasUsadas = new ArrayList<>();
        String nodoActual = destino;
        while (nodoActual != null && !nodoActual.equals(origen)) {
            caminoNodos.add(0, grafo.buscarAeropuerto(nodoActual));
            Ruta rutaUsada = rutaPredecesora.get(nodoActual);
            if (rutaUsada != null) rutasUsadas.add(0, rutaUsada);
            nodoActual = predecesores.get(nodoActual);
        }
        caminoNodos.add(0, grafo.buscarAeropuerto(origen));
        resultado.setOrigen(grafo.buscarAeropuerto(origen));
        resultado.setDestino(grafo.buscarAeropuerto(destino));
        resultado.setCostoTotal(distancias.get(destino));
        resultado.setRutasUsadas(rutasUsadas);
        List<Aeropuerto> escalas = new ArrayList<>();
        for (int i = 1; i < caminoNodos.size() - 1; i++) escalas.add(caminoNodos.get(i));
        resultado.setEscalas(escalas);
        return resultado;
    }
}
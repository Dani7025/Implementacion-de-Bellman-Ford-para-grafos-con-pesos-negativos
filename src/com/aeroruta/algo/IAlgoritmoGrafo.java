package com.aeroruta.algo;

import com.aeroruta.model.ResultadoRuta;

public interface IAlgoritmoGrafo {
    ResultadoRuta ejecutar(String codigoOrigen, String codigoDestino);
}
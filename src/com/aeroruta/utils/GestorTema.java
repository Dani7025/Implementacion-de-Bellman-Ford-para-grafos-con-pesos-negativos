package com.aeroruta.utils;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class GestorTema {
    private static GestorTema instancia;
    private String temaActual;

    private GestorTema() {
        this.temaActual = "oscuro";
    }

    public static synchronized GestorTema getInstancia() {
        if (instancia == null) instancia = new GestorTema();
        return instancia;
    }

    public void aplicarTemaOscuro() {
        try {
            FlatDarculaLaf.setup();
            temaActual = "oscuro";
        }
        catch (Exception e) {
            System.err.println("Error tema oscuro: " + e.getMessage());
        }
    }

    public void aplicarTemaClaro() {
        try {
            FlatIntelliJLaf.setup();
            temaActual = "claro";
        }
        catch (Exception e) {
            System.err.println("Error tema claro: " + e.getMessage());
        }
    }

    public void cambiarTema() {
        if (temaActual.equals("oscuro")) aplicarTemaClaro();
        else aplicarTemaOscuro();
        for (java.awt.Window w : java.awt.Window.getWindows()) javax.swing.SwingUtilities.updateComponentTreeUI(w);
    }

    public Map<String, Color> getPaletaColores() {
        Map<String, Color> paleta = new HashMap<>();
        if (temaActual.equals("oscuro")) {
            paleta.put("fondo", new Color(43, 43, 43));
            paleta.put("nodo", new Color(74, 158, 255));
            paleta.put("aristaNormal", new Color(85, 85, 85));
            paleta.put("aristaFinal", new Color(80, 250, 123));
            paleta.put("textoNodo", new Color(255, 255, 255));
        }
        else {
            paleta.put("fondo", new Color(240, 242, 245));
            paleta.put("nodo", new Color(26, 115, 232));
            paleta.put("aristaNormal", new Color(70, 70, 70, 120));
            paleta.put("aristaFinal", new Color(80, 250, 123));
            paleta.put("textoNodo", new Color(40, 40, 40));
            paleta.put("panelFondo", new Color(255, 255, 255, 200));
        }
        return paleta;
    }

    public String getTemaActual() {
        return temaActual;
    }
}


package com.aeroruta.view;

import com.aeroruta.model.*;
import com.aeroruta.utils.GestorTema;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JPanel;

public class PanelMapa extends JPanel {
    private GrafoAereo grafo;
    private ResultadoRuta resultadoActual;
    private Map<String, Point> coordenadas;
    private static final int RADIO_NODO = 22;
    private static final int RADIO_PULSE = 34;
    private static final Font FONT_CODIGO = new Font("Arial", Font.BOLD, 11);
    private static final Font FONT_CIUDAD = new Font("Arial", Font.PLAIN, 9);
    private static final Font FONT_COSTO = new Font("Monospaced", Font.BOLD, 10);
    private static final Font FONT_TITULO = new Font("Arial", Font.BOLD, 13);

    public PanelMapa(GrafoAereo grafo) {
        this.grafo = grafo;
        this.coordenadas = new HashMap<>();
        setPreferredSize(new Dimension(600, 400));
    }

    public void setResultado(ResultadoRuta resultado) {
        this.resultadoActual = resultado;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Antialiasing máximo
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Map<String, Color> paleta = GestorTema.getInstancia().getPaletaColores();
        calcularCoordenadas();
        dibujarFondo(g2d, paleta);
        if (grafo == null || grafo.getAeropuertos().isEmpty()) {
            dibujarMensajeVacio(g2d, paleta);
            return;
        }
        dibujarRutasNormales(g2d, paleta);
        if (resultadoActual != null && !resultadoActual.isHayError()) dibujarRutaOptima(g2d, paleta);
        dibujarNodos(g2d, paleta);
        if (resultadoActual != null) dibujarPanelResultado(g2d, paleta);
        dibujarTitulo(g2d, paleta);
    }

    private void dibujarFondo(Graphics2D g2d, Map<String, Color> paleta) {
        Color fondo = paleta.getOrDefault("fondo", Color.DARK_GRAY);
        GradientPaint gradiente = new GradientPaint(0, 0, fondo.darker(), getWidth(), getHeight(), fondo);
        g2d.setPaint(gradiente);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(new Color(255, 255, 255, 15));
        for (int x = 20; x < getWidth(); x += 30)
            for (int y = 20; y < getHeight(); y += 30) g2d.fillOval(x, y, 2, 2);
    }

    private void calcularCoordenadas() {
        coordenadas.clear();
        if (grafo == null) return;
        List<Aeropuerto> aeropuertos = grafo.getAeropuertos();
        int n = aeropuertos.size();
        if (n == 0) return;
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radio = (int) (Math.min(cx, cy) * 0.65);
        for (int i = 0; i < n; i++) {
            Aeropuerto a = aeropuertos.get(i);
            double angulo = (2 * Math.PI * i / n) - Math.PI / 2;
            int x = cx + (int) (radio * Math.cos(angulo));
            int y = cy + (int) (radio * Math.sin(angulo));
            coordenadas.put(a.getCodigoIATA(), new Point(x, y));
        }
    }

    private void dibujarRutasNormales(Graphics2D g2d, Map<String, Color> paleta) {
        Color colorArista = paleta.getOrDefault("aristaNormal", Color.GRAY);
        for (Ruta r : grafo.getRutas()) {
            Point p1 = coordenadas.get(r.getOrigen().getCodigoIATA());
            Point p2 = coordenadas.get(r.getDestino().getCodigoIATA());
            if (p1 == null || p2 == null) continue;

            g2d.setStroke(new BasicStroke(1.2f));
            g2d.setColor(colorArista);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

            dibujarFlecha(g2d, p1, p2, colorArista);

            String costo = "$" + String.format("%.0f", r.getPesoGrafo());
            int mx = (p1.x + p2.x) / 2;
            int my = (p1.y + p2.y) / 2;
            dibujarEtiquetaCosto(g2d, costo, mx, my, paleta, false);
        }
    }

    private void dibujarRutaOptima(Graphics2D g2d, Map<String, Color> paleta) {
        List<Ruta> rutasUsadas = resultadoActual.getRutasUsadas();
        if (rutasUsadas == null) return;
        Color colorOptimo = paleta.getOrDefault("aristaFinal", Color.GREEN);
        for (Ruta r : rutasUsadas) {
            Point p1 = coordenadas.get(r.getOrigen().getCodigoIATA());
            Point p2 = coordenadas.get(r.getDestino().getCodigoIATA());
            if (p1 == null || p2 == null) continue;

            g2d.setStroke(new BasicStroke(8f));
            g2d.setColor(new Color(colorOptimo.getRed(), colorOptimo.getGreen(), colorOptimo.getBlue(), 50));
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

            // Línea principal
            g2d.setStroke(new BasicStroke(3f));
            g2d.setColor(colorOptimo);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

            dibujarFlecha(g2d, p1, p2, colorOptimo);
            int mx = (p1.x + p2.x) / 2;
            int my = (p1.y + p2.y) / 2;
            dibujarEtiquetaCosto(g2d, "$" + String.format("%.0f", r.getPesoGrafo()), mx, my, paleta, true);
        }
    }

    private void dibujarFlecha(Graphics2D g2d, Point p1, Point p2, Color color) {
        double dx = p2.x - p1.x, dy = p2.y - p1.y;
        double angulo = Math.atan2(dy, dx);
        double mx = p1.x + dx * 0.6, my = p1.y + dy * 0.6;
        int tam = 8;
        int[] xP = {(int) mx, (int) (mx - tam * Math.cos(angulo - 0.52)), (int) (mx - tam * Math.cos(angulo + 0.52))};
        int[] yP = {(int) my, (int) (my - tam * Math.sin(angulo - 0.52)), (int) (my - tam * Math.sin(angulo + 0.52))};
        g2d.setColor(color);
        g2d.fillPolygon(xP, yP, 3);
    }

    private void dibujarEtiquetaCosto(Graphics2D g2d, String texto, int x, int y, Map<String, Color> paleta, boolean esOptimo) {
        g2d.setFont(FONT_COSTO);
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(texto) + 8, h = fm.getHeight() + 2;
        g2d.setColor(esOptimo ? paleta.get("aristaFinal") : new Color(50, 50, 50, 180));
        g2d.fillRoundRect(x - w / 2, y - h / 2, w, h, 6, 6);
        g2d.setColor(Color.WHITE);
        g2d.drawString(texto, x - fm.stringWidth(texto) / 2, y + fm.getAscent() / 2 - 1);
    }

    private void dibujarNodos(Graphics2D g2d, Map<String, Color> paleta) {
        List<Aeropuerto> rutaAeropuertos = obtenerAeropuertosEnRuta();
        for (Aeropuerto a : grafo.getAeropuertos()) {
            Point p = coordenadas.get(a.getCodigoIATA());
            if (p == null) continue;
            boolean esOrigen = resultadoActual != null && a.equals(resultadoActual.getOrigen());
            boolean esDestino = resultadoActual != null && a.equals(resultadoActual.getDestino());
            boolean enRuta = rutaAeropuertos.contains(a);
            if (esOrigen || esDestino) {
                g2d.setColor(esOrigen ? new Color(255, 200, 50, 60) : new Color(80, 250, 123, 60));
                g2d.fillOval(p.x - RADIO_PULSE / 2, p.y - RADIO_PULSE / 2, RADIO_PULSE, RADIO_PULSE);
            }
            Color colorNodo = paleta.getOrDefault("nodo", Color.BLUE);
            if (esOrigen) colorNodo = new Color(255, 200, 50);
            else if (esDestino) colorNodo = paleta.getOrDefault("aristaFinal", Color.GREEN);
            else if (enRuta) colorNodo = paleta.getOrDefault("nodoSeleccion", Color.CYAN);
            RadialGradientPaint gradNodo = new RadialGradientPaint(
                    new Point(p.x - 4, p.y - 4), RADIO_NODO,
                    new float[]{0f, 1f}, new Color[]{colorNodo.brighter(), colorNodo.darker()}
            );
            g2d.setPaint(gradNodo);
            g2d.fillOval(p.x - RADIO_NODO / 2, p.y - RADIO_NODO / 2, RADIO_NODO, RADIO_NODO);
            g2d.setColor(Color.WHITE);
            g2d.setFont(FONT_CODIGO);
            g2d.drawString(a.getCodigoIATA(), p.x - g2d.getFontMetrics().stringWidth(a.getCodigoIATA()) / 2, p.y + 5);
            g2d.setFont(FONT_CIUDAD);
            g2d.setColor(paleta.getOrDefault("textoNodo", Color.LIGHT_GRAY));
            g2d.drawString(a.getCiudad(), p.x - g2d.getFontMetrics().stringWidth(a.getCiudad()) / 2, p.y + RADIO_NODO / 2 + 13);
        }
    }

    // PANEL DE RESULTADO SUPERPUESTO
    private void dibujarPanelResultado(Graphics2D g2d, Map<String, Color> paleta) {
        int px = 15, py = getHeight() - 125;
        int pw = 280, ph = 100;
        g2d.setColor(paleta.getOrDefault("panelFondo", new Color(0, 0, 0, 160)));
        g2d.fillRoundRect(px, py, pw, ph, 12, 12);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(resultadoActual.isHayError() ? Color.RED : paleta.get("aristaFinal"));
        g2d.drawRoundRect(px, py, pw, ph, 12, 12);
        g2d.setColor(paleta.get("textoNodo"));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 13));

        if (resultadoActual.isHayError()) g2d.drawString(resultadoActual.getMensajeError(), px + 15, py + 55);
        else {
             g2d.setFont(new Font("Monospaced", Font.BOLD, 13));
             g2d.drawString("\u2708 RUTA OPTIMA", px + 15, py + 30);
             g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
             g2d.drawString(construirItinerario(), px + 15, py + 55);
             g2d.setColor(new Color(218, 165, 32));
             g2d.drawString("Costo Total: $" + String.format("%.2f", 
                resultadoActual.getCostoTotal()), px + 10, py + 65);
        }
    }

    private void dibujarTitulo(Graphics2D g2d, Map<String, Color> paleta) {
        Color colorTexto = paleta.getOrDefault("textoNodo", Color.BLACK);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2d.setColor(new Color(colorTexto.getRed(), colorTexto.getGreen(), colorTexto.getBlue(), 120));
        String titulo = "\u2708 Red de Rutas AeroRuta";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(titulo, getWidth() - fm.stringWidth(titulo) - 15, 25);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        int lx = getWidth() - 155, ly = 45;
        g2d.setColor(paleta.get("nodo"));
        g2d.fillOval(lx, ly, 10, 10);
        g2d.setColor(colorTexto);
        g2d.drawString("Aeropuerto", lx + 18, ly + 9);
        g2d.setColor(new Color(255, 200, 50));
        g2d.fillOval(lx, ly + 18, 10, 10);
        g2d.setColor(colorTexto);
        g2d.drawString("Origen", lx + 18, ly + 27);
        g2d.setColor(paleta.get("aristaFinal"));
        g2d.fillOval(lx, ly + 36, 10, 10);
        g2d.setColor(colorTexto);
        g2d.drawString("Ruta óptima", lx + 18, ly + 45);
    }

    private void dibujarMensajeVacio(Graphics2D g2d, Map<String, Color> paleta) {
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.GRAY);
        g2d.drawString("Cargando sistema de vuelos...", getWidth() / 2 - 80, getHeight() / 2);
    }

    private List<Aeropuerto> obtenerAeropuertosEnRuta() {
        List<Aeropuerto> lista = new ArrayList<>();
        if (resultadoActual == null || resultadoActual.isHayError()) return lista;
        if (resultadoActual.getOrigen() != null) lista.add(resultadoActual.getOrigen());
        if (resultadoActual.getEscalas() != null) lista.addAll(resultadoActual.getEscalas());
        if (resultadoActual.getDestino() != null) lista.add(resultadoActual.getDestino());
        return lista;
    }

    private String construirItinerario() {
        if (resultadoActual == null || resultadoActual.getOrigen() == null) return "N/A";
        StringBuilder sb = new StringBuilder(resultadoActual.getOrigen().getCodigoIATA());
        for (Aeropuerto e : resultadoActual.getEscalas()) sb.append(" → ").append(e.getCodigoIATA());
        if (resultadoActual.getDestino() != null) sb.append(" → ").append(resultadoActual.getDestino().getCodigoIATA());
        return sb.toString();
    }
}
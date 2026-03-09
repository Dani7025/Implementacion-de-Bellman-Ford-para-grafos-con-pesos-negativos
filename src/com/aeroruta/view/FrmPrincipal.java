package com.aeroruta.view;

import com.aeroruta.algo.AlgoritmoBellmanFord;
import com.aeroruta.dao.AeropuertoDAO;
import com.aeroruta.dao.RutaDAO;
import com.aeroruta.model.*;
import com.aeroruta.utils.GestorTema;

import java.awt.*;
import java.util.List;
import javax.swing.*;

public class FrmPrincipal extends javax.swing.JFrame {
    private GrafoAereo grafo;
    private PanelMapa panelMapa;
    private JComboBox<Aeropuerto> comboBoxOrigen;
    private JComboBox<Aeropuerto> comboBoxDestino;
    private JTextArea txtResultado;
    private JTabbedPane tabbedPane;
    private PanelAeropuertos panelAeropuertos;
    private PanelRutas panelRutas;

    public FrmPrincipal() {
        configurarVentana();
        inicializarGrafo();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("AeroRuta - Sistema de Optimizacion de Vuelos");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarGrafo() {
        grafo = new GrafoAereo();
        AeropuertoDAO aeropuertoDAO = new AeropuertoDAO();
        RutaDAO rutaDAO = new RutaDAO();
        List<Aeropuerto> aeropuertos = aeropuertoDAO.cargarTodos();
        for (Aeropuerto a : aeropuertos) grafo.agregarAeropuerto(a);
        List<Ruta> rutas = rutaDAO.cargarTodas(aeropuertos);
        for (Ruta r : rutas) grafo.agregarRuta(r);
    }

    private void inicializarComponentes() {
        //  PANEL SUPERIOR 
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        comboBoxOrigen = new JComboBox<>(grafo.getAeropuertos().toArray(new Aeropuerto[0]));
        comboBoxDestino = new JComboBox<>(grafo.getAeropuertos().toArray(new Aeropuerto[0]));
        JButton btnCalcular = new JButton("Calcular Ruta Optima");
        JButton btnTema = new JButton("Cambiar Tema");
        panelTop.add(new JLabel("Origen:"));
        panelTop.add(comboBoxOrigen);
        panelTop.add(new JLabel("Destino:"));
        panelTop.add(comboBoxDestino);
        panelTop.add(btnCalcular);
        panelTop.add(btnTema);

        // PANEL MAPA + RESULTADO 
        panelMapa = new PanelMapa(grafo);
        txtResultado = new JTextArea(4, 50);
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollResultado = new JScrollPane(txtResultado);
        scrollResultado.setBorder(BorderFactory.createTitledBorder("Detalle de la Ruta"));
        JPanel panelMapaCompleto = new JPanel(new BorderLayout());
        panelMapaCompleto.add(panelMapa, BorderLayout.CENTER);
        panelMapaCompleto.add(scrollResultado, BorderLayout.SOUTH);

        // PANELES CRUD
        panelAeropuertos = new PanelAeropuertos(grafo, this);
        panelRutas = new PanelRutas(grafo, this);
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Mapa de Rutas", panelMapaCompleto);
        tabbedPane.addTab("Aeropuertos", panelAeropuertos);
        tabbedPane.addTab("Rutas", panelRutas);
        add(panelTop, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        btnCalcular.addActionListener(e -> ejecutarAlgoritmo());
        btnTema.addActionListener(e -> {
            GestorTema.getInstancia().cambiarTema();
            SwingUtilities.updateComponentTreeUI(this);
        });
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) panelMapa.repaint();
        });
    }

    private void ejecutarAlgoritmo() {
        Aeropuerto origen = (Aeropuerto) comboBoxOrigen.getSelectedItem();
        Aeropuerto destino = (Aeropuerto) comboBoxDestino.getSelectedItem();
        if (origen == null || destino == null) {
            JOptionPane.showMessageDialog(this, "Debes tener al menos dos aeropuertos registrados.");
            return;
        }
        if (origen.equals(destino)) {
            JOptionPane.showMessageDialog(this, "El origen y el destino no pueden ser iguales.");
            return;
        }
        AlgoritmoBellmanFord bf = new AlgoritmoBellmanFord(grafo);
        ResultadoRuta resultado = bf.ejecutar(origen.getCodigoIATA(), destino.getCodigoIATA());
        panelMapa.setResultado(resultado);
        txtResultado.setText(resultado.getResumen());
        tabbedPane.setSelectedIndex(0);
    }

    public void refrescarComboBox() {
        comboBoxOrigen.removeAllItems();
        comboBoxDestino.removeAllItems();
        for (Aeropuerto a : grafo.getAeropuertos()) {
            comboBoxOrigen.addItem(a);
            comboBoxDestino.addItem(a);
        }
        panelRutas.refrescarComboBox();
        panelMapa.repaint();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (Exception ex) {
            java.util.logging.Logger.getLogger(FrmPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new FrmPrincipal().setVisible(true));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
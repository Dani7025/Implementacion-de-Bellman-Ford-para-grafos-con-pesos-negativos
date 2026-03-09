package com.aeroruta.view;

import com.aeroruta.dao.RutaDAO;
import com.aeroruta.model.*;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelRutas extends JPanel {
    private GrafoAereo grafo;
    private FrmPrincipal ventanaPrincipal;
    private RutaDAO dao;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<Aeropuerto> cbOrigen;
    private JComboBox<Aeropuerto> cbDestino;
    private JComboBox<String> cbTipo;
    private JTextField txtAerolinea;
    private JTextField txtCostoBase;
    private JTextField txtDescuento;
    private JTextField txtDuracion;
    private JTextField txtImpuesto;
    private JTextField txtTasaFija;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JLabel lblTasaFija;

    public PanelRutas(GrafoAereo grafo, FrmPrincipal ventanaPrincipal) {
        this.grafo = grafo;
        this.ventanaPrincipal = ventanaPrincipal;
        this.dao = new RutaDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construirUI();
        cargarTabla();
    }

    private void construirUI() {
        JLabel lblTitulo = new JLabel("Gestion de Rutas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitulo, BorderLayout.NORTH);
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de la Ruta"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cbOrigen = new JComboBox<>(grafo.getAeropuertos().toArray(new Aeropuerto[0]));
        cbDestino = new JComboBox<>(grafo.getAeropuertos().toArray(new Aeropuerto[0]));
        cbTipo = new JComboBox<>(new String[]{"DOM", "INTL"});
        txtAerolinea = new JTextField(15);
        txtCostoBase = new JTextField(10);
        txtDescuento = new JTextField(10);
        txtDuracion = new JTextField(10);
        txtImpuesto = new JTextField(10);
        txtTasaFija = new JTextField(10);
        lblTasaFija = new JLabel("Tasa Aeropuerto ($):");

        // Fila 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Origen:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panelFormulario.add(cbOrigen, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Destino:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        panelFormulario.add(cbDestino, gbc);

        // Fila 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Aerolinea:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelFormulario.add(txtAerolinea, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        panelFormulario.add(cbTipo, gbc);

        // Fila 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(new JLabel("Costo Base ($):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panelFormulario.add(txtCostoBase, gbc);
        gbc.gridx = 2;
        gbc.gridy = 2;
        panelFormulario.add(new JLabel("Descuento ($):"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        panelFormulario.add(txtDescuento, gbc);

        // Fila 3
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelFormulario.add(new JLabel("Duracion (hrs):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panelFormulario.add(txtDuracion, gbc);
        gbc.gridx = 2;
        gbc.gridy = 3;
        panelFormulario.add(new JLabel("Impuesto (0.15 = 15%):"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 3;
        panelFormulario.add(txtImpuesto, gbc);

        // Tasa fija solo para INTL
        gbc.gridx = 0;
        gbc.gridy = 4;
        panelFormulario.add(lblTasaFija, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panelFormulario.add(txtTasaFija, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnGuardar = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnEliminar.setEnabled(false);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        String[] columnas = {"ID", "Origen", "Destino", "Aerolinea", "Tipo", "Costo Base", "Descuento", "Peso Grafo", "Activa"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setRowHeight(24);

        // Ocultar columna ID de la vista
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Rutas Registradas"));
        add(scroll, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        cbTipo.addActionListener(e -> {
            boolean esIntl = "INTL".equals(cbTipo.getSelectedItem());
            lblTasaFija.setVisible(esIntl);
            txtTasaFija.setVisible(esIntl);
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) btnEliminar.setEnabled(tabla.getSelectedRow() >= 0);
        });
        cbTipo.getActionListeners()[0].actionPerformed(null);
    }

    public void refrescarComboBox() {
        cbOrigen.removeAllItems();
        cbDestino.removeAllItems();
        for (Aeropuerto a : grafo.getAeropuertos()) {
            cbOrigen.addItem(a);
            cbDestino.addItem(a);
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Aeropuerto> aeropuertos = grafo.getAeropuertos();
        List<Ruta> lista = dao.cargarTodas(aeropuertos);
        for (Ruta r : lista) {
            modeloTabla.addRow(new Object[]{
                    r.getId(),
                    r.getOrigen().getCodigoIATA(),
                    r.getDestino().getCodigoIATA(),
                    r.getAerolinea(),
                    r instanceof VueloInternacional ? "INTL" : "DOM",
                    String.format("%.2f", r.getCostoBase()),
                    String.format("%.2f", r.getDescuento()),
                    String.format("%.2f", r.getPesoGrafo()),
                    r.isActiva() ? "Si" : "No"
            });
        }
    }

    private void guardar() {
        if (!validarCampos()) return;
        Aeropuerto origen = (Aeropuerto) cbOrigen.getSelectedItem();
        Aeropuerto destino = (Aeropuerto) cbDestino.getSelectedItem();
        if (origen == null || destino == null) {
            JOptionPane.showMessageDialog(this, "Debes registrar al menos dos aeropuertos primero.", "Sin aeropuertos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (origen.equals(destino)) {
            JOptionPane.showMessageDialog(this, "El origen y destino no pueden ser iguales.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double costoBase = Double.parseDouble(txtCostoBase.getText().trim());
            double descuento = Double.parseDouble(txtDescuento.getText().trim());
            double duracion = Double.parseDouble(txtDuracion.getText().trim());
            double impuesto = Double.parseDouble(txtImpuesto.getText().trim());
            String aerolinea = txtAerolinea.getText().trim();
            String tipo = cbTipo.getSelectedItem().toString();
            Ruta r;
            if (tipo.equals("INTL")) {
                double tasa = Double.parseDouble(txtTasaFija.getText().trim());
                r = new VueloInternacional(origen, destino, costoBase, descuento, aerolinea, duracion, impuesto, tasa);
            }
            else r = new VueloDomestico(origen, destino, costoBase, descuento, aerolinea, duracion, impuesto);
            if (dao.guardar(r)) {
                List<Ruta> actualizadas = dao.cargarTodas(grafo.getAeropuertos());
                grafo.getRutas().clear();
                for (Ruta ruta : actualizadas) grafo.agregarRuta(ruta);
                ventanaPrincipal.refrescarComboBox();
                cargarTabla();
                limpiar();
                JOptionPane.showMessageDialog(this, "Ruta guardada correctamente.");
            }
            else JOptionPane.showMessageDialog(this, "La ruta ya existe o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Los campos numericos deben contener valores validos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        String origen = modeloTabla.getValueAt(fila, 1).toString();
        String destino = modeloTabla.getValueAt(fila, 2).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Eliminar la ruta " + origen + " -> " + destino + "?", "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        if (dao.eliminar(id)) {
            grafo.eliminarRuta(id);
            ventanaPrincipal.refrescarComboBox();
            cargarTabla();
            limpiar();
            JOptionPane.showMessageDialog(this, "Ruta eliminada correctamente.");
        }
        else JOptionPane.showMessageDialog(this, "Error al eliminar la ruta.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void limpiar() {
        txtAerolinea.setText("");
        txtCostoBase.setText("");
        txtDescuento.setText("");
        txtDuracion.setText("");
        txtImpuesto.setText("");
        txtTasaFija.setText("");
        cbTipo.setSelectedIndex(0);
        tabla.clearSelection();
        btnEliminar.setEnabled(false);
        if (cbOrigen.getItemCount() > 0) cbOrigen.setSelectedIndex(0);
        if (cbDestino.getItemCount() > 1) cbDestino.setSelectedIndex(1);
    }

    private boolean validarCampos() {
        if (txtAerolinea.getText().trim().isEmpty() || txtCostoBase.getText().trim().isEmpty() || txtDescuento.getText().trim().isEmpty() || txtDuracion.getText().trim().isEmpty() || txtImpuesto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if ("INTL".equals(cbTipo.getSelectedItem()) && txtTasaFija.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La tasa de aeropuerto es obligatoria para vuelos internacionales.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
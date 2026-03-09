package com.aeroruta.view;

import com.aeroruta.dao.AeropuertoDAO;
import com.aeroruta.model.Aeropuerto;
import com.aeroruta.model.GrafoAereo;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelAeropuertos extends JPanel {
    private GrafoAereo grafo;
    private FrmPrincipal ventanaPrincipal;
    private AeropuertoDAO dao;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtCiudad;
    private JTextField txtPais;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    public PanelAeropuertos(GrafoAereo grafo, FrmPrincipal ventanaPrincipal) {
        this.grafo = grafo;
        this.ventanaPrincipal = ventanaPrincipal;
        this.dao = new AeropuertoDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construirUI();
        cargarTabla();
    }

    private void construirUI() {
        JLabel lblTitulo = new JLabel("Gestion de Aeropuertos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Aeropuerto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigo = new JTextField(8);
        txtNombre = new JTextField(20);
        txtCiudad = new JTextField(20);
        txtPais = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Codigo IATA:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panelFormulario.add(txtCodigo, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        panelFormulario.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Ciudad:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelFormulario.add(txtCiudad, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Pais:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        panelFormulario.add(txtPais, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        String[] columnas = {"Codigo IATA", "Nombre", "Ciudad", "Pais", "Activo"};
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

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Aeropuertos Registrados"));
        add(scroll, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Aeropuerto> lista = dao.cargarTodos();
        for (Aeropuerto a : lista) {
            modeloTabla.addRow(new Object[]{
                    a.getCodigoIATA(),
                    a.getNombre(),
                    a.getCiudad(),
                    a.getPais(),
                    a.isActivo() ? "Si" : "No"
            });
        }
    }

    private void guardar() {
        if (!validarCampos()) return;
        String codigo = txtCodigo.getText().trim().toUpperCase();
        if (dao.existeCodigo(codigo)) {
            JOptionPane.showMessageDialog(this, "Ya existe un aeropuerto con el codigo " + codigo, "Duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Aeropuerto a = new Aeropuerto(codigo, txtNombre.getText().trim(), txtCiudad.getText().trim(), txtPais.getText().trim());
        if (dao.guardar(a)) {
            grafo.agregarAeropuerto(a);
            ventanaPrincipal.refrescarComboBox();
            cargarTabla();
            limpiar();
            JOptionPane.showMessageDialog(this,
                    "Aeropuerto guardado correctamente.");
        }
        else JOptionPane.showMessageDialog(this, "Error al guardar el aeropuerto.", "Error", JOptionPane.ERROR_MESSAGE);

    }

    private void actualizar() {
        if (!validarCampos()) return;
        Aeropuerto a = new Aeropuerto(txtCodigo.getText().trim().toUpperCase(), txtNombre.getText().trim(), txtCiudad.getText().trim(), txtPais.getText().trim());
        if (dao.actualizar(a)) {
            Aeropuerto enGrafo = grafo.buscarAeropuerto(a.getCodigoIATA());
            if (enGrafo != null) {
                enGrafo.setNombre(a.getNombre());
                enGrafo.setCiudad(a.getCiudad());
                enGrafo.setPais(a.getPais());
            }
            ventanaPrincipal.refrescarComboBox();
            cargarTabla();
            limpiar();
            JOptionPane.showMessageDialog(this, "Aeropuerto actualizado correctamente.");
        }
        else JOptionPane.showMessageDialog(this, "Error al actualizar el aeropuerto.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        String codigo = modeloTabla.getValueAt(fila, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Eliminar el aeropuerto " + codigo + " y todas sus rutas asociadas?", "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        if (dao.eliminar(codigo)) {
            grafo.eliminarAeropuerto(codigo);
            ventanaPrincipal.refrescarComboBox();
            cargarTabla();
            limpiar();
            JOptionPane.showMessageDialog(this, "Aeropuerto eliminado correctamente.");
        }
        else JOptionPane.showMessageDialog(this, "Error al eliminar el aeropuerto.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtCodigo.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtCiudad.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtPais.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtCodigo.setEditable(false);
        btnGuardar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtCiudad.setText("");
        txtPais.setText("");
        txtCodigo.setEditable(true);
        tabla.clearSelection();
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private boolean validarCampos() {
        if (txtCodigo.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty() || txtCiudad.getText().trim().isEmpty() || txtPais.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtCodigo.getText().trim().length() != 3) {
            JOptionPane.showMessageDialog(this, "El codigo IATA debe tener exactamente 3 letras.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
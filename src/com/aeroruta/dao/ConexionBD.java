package com.aeroruta.dao;

import java.sql.*;

public class ConexionBD {
    private static ConexionBD instancia;
    private Connection connection;
    private final String url = "jdbc:sqlite:aeroruta.db";

    private ConexionBD() {
        conectar();
        crearTablas();
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) instancia = new ConexionBD();
        return instancia;
    }

    private void conectar() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
            connection.createStatement().execute("PRAGMA foreign_keys = ON;");
        }
        catch (Exception e) {
            System.err.println("Error conexión: " + e.getMessage());
        }
    }

    public Connection getConexion() {
        try {
            if (connection == null || connection.isClosed()) conectar();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void desconectar() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void crearTablas() {
        String sqlAeropuerto = "CREATE TABLE IF NOT EXISTS aeropuertos (" + "codigo_iata TEXT PRIMARY KEY, nombre TEXT, ciudad TEXT, pais TEXT, activo INTEGER);";
        String sqlRuta = "CREATE TABLE IF NOT EXISTS rutas ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, origen TEXT, destino TEXT, "
                + "costo_base REAL, descuento REAL, aerolinea TEXT, duracion_hrs REAL, "
                + "tipo TEXT, impuesto REAL, tasa_fija REAL, activa INTEGER, "
                + "FOREIGN KEY(origen) REFERENCES aeropuertos(codigo_iata), "
                + "FOREIGN KEY(destino) REFERENCES aeropuertos(codigo_iata));";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlAeropuerto);
            statement.execute(sqlRuta);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
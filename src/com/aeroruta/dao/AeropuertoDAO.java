package com.aeroruta.dao;

import com.aeroruta.model.Aeropuerto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AeropuertoDAO {
    public boolean guardar(Aeropuerto a) {
        String sql = "INSERT INTO aeropuertos(codigo_iata, nombre, ciudad, pais, activo) VALUES(?,?,?,?,?)";
        try (PreparedStatement preparedStatement = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            preparedStatement.setString(1, a.getCodigoIATA());
            preparedStatement.setString(2, a.getNombre());
            preparedStatement.setString(3, a.getCiudad());
            preparedStatement.setString(4, a.getPais());
            preparedStatement.setInt(5, a.isActivo() ? 1 : 0);
            return preparedStatement.executeUpdate() > 0;
        }
        catch (SQLException e) {
            return false;
        }
    }

    public boolean actualizar(Aeropuerto a) {
        String sql = "UPDATE aeropuertos SET nombre=?, ciudad=?, pais=?, activo=? WHERE codigo_iata=?";
        try (PreparedStatement preparedStatement = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            preparedStatement.setString(1, a.getNombre());
            preparedStatement.setString(2, a.getCiudad());
            preparedStatement.setString(3, a.getPais());
            preparedStatement.setInt(4, a.isActivo() ? 1 : 0);
            preparedStatement.setString(5, a.getCodigoIATA());
            return preparedStatement.executeUpdate() > 0;
        }
        catch (SQLException e) {
            return false;
        }
    }

    public boolean eliminar(String codigo) {
        String sql = "DELETE FROM aeropuertos WHERE codigo_iata=?";
        try (PreparedStatement preparedStatement = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            preparedStatement.setString(1, codigo);
            return preparedStatement.executeUpdate() > 0;
        }
        catch (SQLException e) {
            return false;
        }
    }

    public boolean existeCodigo(String codigo) {
        String sql = "SELECT 1 FROM aeropuertos WHERE codigo_iata=?";
        try (PreparedStatement preparedStatement = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            preparedStatement.setString(1, codigo.toUpperCase());
            return preparedStatement.executeQuery().next();
        }
        catch (SQLException e) {
            return false;
        }
    }

    public List<Aeropuerto> cargarTodos() {
        List<Aeropuerto> lista = new ArrayList<>();
        String sql = "SELECT * FROM aeropuertos";
        try (ResultSet resultSet = ConexionBD.getInstancia().getConexion().createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                Aeropuerto a = new Aeropuerto(
                        resultSet.getString("codigo_iata"),
                        resultSet.getString("nombre"),
                        resultSet.getString("ciudad"),
                        resultSet.getString("pais")
                );
                a.setActivo(resultSet.getInt("activo") == 1);
                lista.add(a);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
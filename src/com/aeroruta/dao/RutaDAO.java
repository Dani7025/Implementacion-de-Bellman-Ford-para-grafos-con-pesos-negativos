package com.aeroruta.dao;

import com.aeroruta.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RutaDAO {
    public boolean existeRutaDuplicada(String origen, String destino, String aerolinea) {
        String sql = "SELECT 1 FROM rutas WHERE origen=? AND destino=? AND aerolinea=?";
        try (PreparedStatement preparedStatement = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            preparedStatement.setString(1, origen);
            preparedStatement.setString(2, destino);
            preparedStatement.setString(3, aerolinea);
            return preparedStatement.executeQuery().next();
        }
        catch (SQLException e) {
            return false;
        }
    }

    public boolean guardar(Ruta r) {
        if (existeRutaDuplicada(r.getOrigen().getCodigoIATA(), r.getDestino().getCodigoIATA(), r.getAerolinea())) return false;
        String sql = "INSERT INTO rutas (origen, destino, costo_base, descuento, "
                + "aerolinea, duracion_hrs, tipo, impuesto, tasa_fija, activa) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?)";
        return ejecutarSentencia(r, sql, false);
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM rutas WHERE id=?";
        try (PreparedStatement preparedStatement = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
        catch (SQLException e) {
            return false;
        }
    }

    public List<Ruta> cargarTodas(List<Aeropuerto> aeropuertos) {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT * FROM rutas";
        try (ResultSet resultSet = ConexionBD.getInstancia().getConexion().createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String codigoOrigen = resultSet.getString("origen");
                String codigoDestino = resultSet.getString("destino");
                Aeropuerto origen = aeropuertos.stream()
                        .filter(a -> a.getCodigoIATA().equals(codigoOrigen))
                        .findFirst().orElse(null);
                Aeropuerto destino = aeropuertos.stream()
                        .filter(a -> a.getCodigoIATA().equals(codigoDestino))
                        .findFirst().orElse(null);
                if (origen == null || destino == null) continue;
                String tipo = resultSet.getString("tipo");
                Ruta r;
                if (tipo.equals("INTL")) {
                    r = new VueloInternacional(origen, destino,
                            resultSet.getDouble("costo_base"),
                            resultSet.getDouble("descuento"),
                            resultSet.getString("aerolinea"),
                            resultSet.getDouble("duracion_hrs"),
                            resultSet.getDouble("impuesto"),
                            resultSet.getDouble("tasa_fija"));
                }
                else {
                    r = new VueloDomestico(origen, destino,
                            resultSet.getDouble("costo_base"),
                            resultSet.getDouble("descuento"),
                            resultSet.getString("aerolinea"),
                            resultSet.getDouble("duracion_hrs"),
                            resultSet.getDouble("impuesto"));
                }
                r.setId(resultSet.getInt("id"));
                r.setActiva(resultSet.getInt("activa") == 1);
                lista.add(r);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private boolean ejecutarSentencia(Ruta r, String sql, boolean esUpdate) {
        try (PreparedStatement preparedStatement = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            preparedStatement.setString(1, r.getOrigen().getCodigoIATA());
            preparedStatement.setString(2, r.getDestino().getCodigoIATA());
            preparedStatement.setDouble(3, r.getCostoBase());
            preparedStatement.setDouble(4, r.getDescuento());
            preparedStatement.setString(5, r.getAerolinea());
            preparedStatement.setDouble(6, r.getDuracionHoras());
            if (r instanceof VueloInternacional) {
                preparedStatement.setString(7, "INTL");
                preparedStatement.setDouble(8, ((VueloInternacional) r).getImpuestoInternacional());
                preparedStatement.setDouble(9, ((VueloInternacional) r).getTasaAeropuerto());
            }
            else {
                preparedStatement.setString(7, "DOM");
                preparedStatement.setDouble(8, ((VueloDomestico) r).getImpuestoNacional());
                preparedStatement.setDouble(9, 0);
            }
            preparedStatement.setInt(10, r.isActiva() ? 1 : 0);
            if (esUpdate) preparedStatement.setInt(11, r.getId());
            return preparedStatement.executeUpdate() > 0;
        }
        catch (SQLException e) {
            return false;
        }
    }
}
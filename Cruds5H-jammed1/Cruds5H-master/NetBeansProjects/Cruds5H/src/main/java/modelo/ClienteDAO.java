package modelo;

/**
 *
 * @author ep712
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private final Connection con;

    public ClienteDAO(Connection con) {
        this.con = con;
    }

    // CREATE
    public boolean insertar(Clientes cliente) {
        String sql = "INSERT INTO clientes (nombre, correo, telefono) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getCorreo());
            ps.setString(3, cliente.getTelefono());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error insertar cliente: " + e.getMessage());
            return false;
        }
    }

    // READ
    public List<Clientes> listar() {
        List<Clientes> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Clientes(
                        rs.getInt("idcliente"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("telefono")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error listar clientes: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE
    public boolean actualizar(Clientes cliente) {
        String sql = "UPDATE clientes SET nombre=?, correo=?, telefono=? WHERE idcliente=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getCorreo());
            ps.setString(3, cliente.getTelefono());
            ps.setInt(4, cliente.getIdcliente());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int idCliente) {
        String sql = "DELETE FROM clientes WHERE idcliente=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error eliminar cliente: " + e.getMessage());
            return false;
        }
    }
}


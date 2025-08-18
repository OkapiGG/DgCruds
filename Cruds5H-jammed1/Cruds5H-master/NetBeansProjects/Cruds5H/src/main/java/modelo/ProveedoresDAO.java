package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedoresDAO {

    private final Connection con;

    public ProveedoresDAO(Connection con) {
        this.con = con;
    }

    // CREATE
    public boolean insertar(Proveedores proveedor) {
        String sql = "INSERT INTO proveedores (nombre, correo, telefono, direccion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getCorreo());
            ps.setString(3, proveedor.getTelefono());
            ps.setString(4, proveedor.getDireccion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar proveedor: " + e.getMessage());
            return false;
        }
    }

    // READ
    public List<Proveedores> listar() {
        List<Proveedores> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Proveedores(
                        rs.getInt("idproveedor"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("telefono"),
                        rs.getString("direccion")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error listar proveedores: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE
    public boolean actualizar(Proveedores proveedor) {
        String sql = "UPDATE proveedores SET nombre=?, correo=?, telefono=?, direccion=? WHERE idproveedor=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getCorreo());
            ps.setString(3, proveedor.getTelefono());
            ps.setString(4, proveedor.getDireccion());
            ps.setInt(5, proveedor.getIdproveedor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar proveedor: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int idProveedor) {
        String sql = "DELETE FROM proveedores WHERE idproveedor=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminar proveedor: " + e.getMessage());
            return false;
        }
    }
}

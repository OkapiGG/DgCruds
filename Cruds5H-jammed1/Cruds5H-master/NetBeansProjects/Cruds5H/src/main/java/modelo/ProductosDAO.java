package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductosDAO {
    private final Connection con;

    public ProductosDAO(Connection con) {
        this.con = con;
    }

    // CREATE
    public boolean insertar(Productos p) {
        String sql = "INSERT INTO productos (idproveedor, nombre, stock) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getIdproveedor());
            ps.setString(2, p.getNombre());
            ps.setInt(3, p.getStock());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error insertar producto: " + e.getMessage());
            return false;
        }
    }

    // READ
    public List<Productos> listar() {
        List<Productos> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Productos(
                        rs.getInt("idproducto"),
                        rs.getInt("idproveedor"),
                        rs.getString("nombre"),
                        rs.getInt("stock")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error listar productos: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE
    public boolean actualizar(Productos p) {
        String sql = "UPDATE productos SET idproveedor=?, nombre=?, stock=? WHERE idproducto=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getIdproveedor());
            ps.setString(2, p.getNombre());
            ps.setInt(3, p.getStock());
            ps.setInt(4, p.getIdproducto());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error actualizar producto: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int idProducto) {
        String sql = "DELETE FROM productos WHERE idproducto=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error eliminar producto: " + e.getMessage());
            return false;
        }
    }
}

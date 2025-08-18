/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Vista.ProductosEliminar;
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class ProductosEliminarControlador implements ActionListener {

    private final ProductosEliminar view;
    private final Connection con;

    private JButton btnListo;    
    private JButton btnRegresar;

    public ProductosEliminarControlador(ProductosEliminar view, Connection con) {
        this.view = view;
        this.con  = con;
        
        hookPrivateButtons();
        view.jTextField1.addActionListener(this); // Enter en ID → cargar

        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /** Enlaza botones privados jButton1 (Listo) y jButton2 (Regresar) */
    private void hookPrivateButtons() {
        try {
            Field fListo = ProductosEliminar.class.getDeclaredField("jButton1");
            fListo.setAccessible(true);
            btnListo = (JButton) fListo.get(view);
            btnListo.addActionListener(this);

            Field fRegresar = ProductosEliminar.class.getDeclaredField("jButton2");
            fRegresar.setAccessible(true);
            btnRegresar = (JButton) fRegresar.get(view);
            btnRegresar.addActionListener(this);

        } catch (NoSuchFieldException | IllegalAccessException ex) {
            JOptionPane.showMessageDialog(view,
                "No se pudieron enlazar los botones de la vista: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnListo) {
            eliminarProducto();

        } else if (src == btnRegresar) {
            Menu m = new Menu();
            m.setLocationRelativeTo(view);
            m.setVisible(true);
            view.dispose();

        } else if (src == view.jTextField1) {
            cargarProductoPorId();
        }
    }

    /** Carga los datos del producto al ingresar ID y presionar Enter */
    private void cargarProductoPorId() {
        final String idTxt = view.jTextField1.getText().trim();
        if (!idTxt.matches("\\d+")) {
            JOptionPane.showMessageDialog(view, "ID inválido. Debe ser numérico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField1.requestFocus();
            return;
        }
        final int id = Integer.parseInt(idTxt);

        final String sql = "SELECT idproveedor, nombre, stock FROM productos WHERE idproducto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    view.jTextField2.setText(String.valueOf(rs.getInt("idproveedor")));
                    view.jTextField3.setText(rs.getString("nombre") != null ? rs.getString("nombre") : "");
                    view.jTextField4.setText(String.valueOf(rs.getInt("stock")));
                    view.jTextField1.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(view, "No existe un producto con ese ID.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limpiarTodo();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error al cargar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Elimina el producto validando coincidencia de campos y FK */
    private void eliminarProducto() {
        final String idTxt = view.jTextField1.getText().trim();
        if (!idTxt.matches("\\d+")) {
            JOptionPane.showMessageDialog(view, "ID inválido. Debe ser numérico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField1.requestFocus();
            return;
        }
        final int id = Integer.parseInt(idTxt);

        int opt = JOptionPane.showConfirmDialog(
            view,
            "¿Seguro que deseas eliminar el producto con ID " + id + "?\nEsta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (opt != JOptionPane.YES_OPTION) return;

        if (!existeProducto(id)) {
            JOptionPane.showMessageDialog(view, "No existe un producto con ese ID.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!camposCoincidenConBD(id)) {
            JOptionPane.showMessageDialog(view,
                "Los campos en pantalla NO coinciden con los datos registrados para ese ID.\n" +
                "Presiona ENTER en el campo ID para recargar y verifica antes de eliminar.",
                "Validación de datos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String sql = "DELETE FROM productos WHERE idproducto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 1) {
                JOptionPane.showMessageDialog(view, "Producto eliminado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                limpiarTodo();
            } else {
                JOptionPane.showMessageDialog(view, "No se eliminó el registro (revisa el ID).", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            String sqlState = ex.getSQLState();
            if ("23503".equals(sqlState) || "23000".equals(sqlState)) {
                JOptionPane.showMessageDialog(view,
                    "No se puede eliminar: el producto tiene registros relacionados (e.g., detalle_venta).",
                    "Restricción de integridad",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view,
                    "Error SQL (" + sqlState + "): " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Verifica existencia por ID */
    private boolean existeProducto(int id) {
        final String q = "SELECT 1 FROM productos WHERE idproducto = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "No se pudo verificar existencia: " + e.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /** Compara lo escrito en la vista contra lo almacenado en BD para ese ID */
    private boolean camposCoincidenConBD(int id) {
        final String vIdProv = view.jTextField2.getText() != null ? view.jTextField2.getText().trim() : "";
        final String vNombre = view.jTextField3.getText() != null ? view.jTextField3.getText().trim() : "";
        final String vStock  = view.jTextField4.getText() != null ? view.jTextField4.getText().trim() : "";

        final String sql = "SELECT idproveedor, nombre, stock FROM productos WHERE idproducto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;

                String dbIdProv = String.valueOf(rs.getInt("idproveedor"));
                String dbNombre = rs.getString("nombre") != null ? rs.getString("nombre").trim() : "";
                String dbStock  = String.valueOf(rs.getInt("stock"));

                return vIdProv.equals(dbIdProv) && vNombre.equals(dbNombre) && vStock.equals(dbStock);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error al validar contra BD: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void limpiarTodo() {
        view.jTextField1.setText("");
        view.jTextField2.setText("");
        view.jTextField3.setText("");
        view.jTextField4.setText("");
        view.jTextField1.requestFocus();
    }
}


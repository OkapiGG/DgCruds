/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Vista.ProductosModificar;
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class ProductosModificarControlador implements ActionListener {

    private final ProductosModificar view;
    private final Connection con;

    private JButton btnListo;
    private JButton btnRegresar;

    public ProductosModificarControlador(ProductosModificar view, Connection con) {
        this.view = view;
        this.con  = con;

        hookPrivateButtons();
        view.jTextField1.addActionListener(this); // ID producto

        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /** Enlazar botones privados de la vista */
    private void hookPrivateButtons() {
        try {
            Field fListo = ProductosModificar.class.getDeclaredField("jButton1");
            fListo.setAccessible(true);
            btnListo = (JButton) fListo.get(view);
            btnListo.addActionListener(this);

            Field fRegresar = ProductosModificar.class.getDeclaredField("jButton2");
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
            actualizarProducto();

        } else if (src == btnRegresar) {
            Menu m = new Menu();
            m.setLocationRelativeTo(view);
            m.setVisible(true);
            view.dispose();

        } else if (src == view.jTextField1) {
            cargarProductoPorId();
        }
    }

    /** Cargar producto al escribir ID */
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
                    view.jTextField3.setText(rs.getString("nombre"));
                    view.jTextField4.setText(String.valueOf(rs.getInt("stock")));
                    view.jTextField2.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(view, "No existe un producto con ese ID.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limpiarTodo(); 
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error al cargar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Actualizar producto */
    private void actualizarProducto() {
        final String idTxt = view.jTextField1.getText().trim();
        if (!idTxt.matches("\\d+")) {
            JOptionPane.showMessageDialog(view, "ID inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField1.requestFocus();
            return;
        }
        final int id = Integer.parseInt(idTxt);

        final String sIdProv = view.jTextField2.getText().trim();
        final String nombre  = view.jTextField3.getText().trim();
        final String sStock  = view.jTextField4.getText().trim();

        if (!sIdProv.matches("\\d+")) {
            JOptionPane.showMessageDialog(view, "ID de proveedor inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField2.requestFocus();
            return;
        }
        int idProv = Integer.parseInt(sIdProv);

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(view, "El nombre es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField3.requestFocus();
            return;
        }

        int stock = 0;
        if (!sStock.isEmpty()) {
            if (!sStock.matches("\\d+")) {
                JOptionPane.showMessageDialog(view, "Stock inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                view.jTextField4.requestFocus();
                return;
            }
            stock = Integer.parseInt(sStock);
        }

        if (!existeProducto(id)) {
            JOptionPane.showMessageDialog(view, "No existe un producto con ese ID.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String sql = "UPDATE productos SET idproveedor=?, nombre=?, stock=? WHERE idproducto=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProv);
            ps.setString(2, nombre);
            ps.setInt(3, stock);
            ps.setInt(4, id);

            int filas = ps.executeUpdate();
            if (filas == 1) {
                JOptionPane.showMessageDialog(view, "Producto actualizado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                limpiarTodo();
            } else {
                JOptionPane.showMessageDialog(view, "No se actualizó el registro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            String state = ex.getSQLState();
            if ("23503".equals(state)) {
                JOptionPane.showMessageDialog(view, "El proveedor no existe (clave foránea).", "Error", JOptionPane.ERROR_MESSAGE);
            } else if ("23514".equals(state)) {
                JOptionPane.showMessageDialog(view, "Stock debe ser ≥ 0.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Error SQL (" + state + "): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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

    private void limpiarTodo() {
        view.jTextField1.setText("");
        view.jTextField2.setText("");
        view.jTextField3.setText("");
        view.jTextField4.setText("");
        view.jTextField1.requestFocus();
    }
}


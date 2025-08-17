package controlador;

import Vista.Menu;
import Vista.Productos;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.JOptionPane;

public class ProductosControlador implements ActionListener {

    private final Productos view;
    private final Connection con; 

    public ProductosControlador(Productos view, Connection con) {
        this.view = view;
        this.con  = con;
        view.jButton1.addActionListener(this);
        view.jButton2.addActionListener(this);
        view.jTextField2.setEditable(false);
        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == view.jButton1) {
            guardarConReglas();
        } else if (src == view.jButton2) {
            Menu m = new Menu();
            m.setLocationRelativeTo(view);
            m.setVisible(true);
            view.dispose();
        }
    }
    private void guardarConReglas() {
        final String sIdProveedor = view.jTextField1.getText().trim(); 
        final String nombre       = view.jTextField3.getText().trim();
        final String sStock       = view.jTextField4.getText().trim(); 
        if (sIdProveedor.isEmpty()) {
            JOptionPane.showMessageDialog(view, "El ID del proveedor es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField1.requestFocus();
            return;
        }
        int idProveedor;
        try {
            idProveedor = Integer.parseInt(sIdProveedor);
            if (idProveedor <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "ID de proveedor inválido (usa un entero positivo).", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField1.requestFocus();
            return;
        }
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(view, "El nombre del producto es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField3.requestFocus();
            return;
        }
        int stock = 0;
        if (!sStock.isEmpty()) {
            try {
                stock = Integer.parseInt(sStock);
                if (stock < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Stock inválido (usa un entero ≥ 0).", "Aviso", JOptionPane.WARNING_MESSAGE);
                view.jTextField4.requestFocus();
                return;
            }
        }
        final String sql = "INSERT INTO productos (idproveedor, nombre, stock) VALUES (?, ?, ?) RETURNING idproducto";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.setString(2, nombre);
            ps.setInt(3, stock);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    view.jTextField2.setText(String.valueOf(idGenerado)); // muestra idproducto
                }
            }
            JOptionPane.showMessageDialog(view, "Producto guardado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
            limpiarDespuesDeGuardar();
        } catch (SQLException ex) {
            String state = ex.getSQLState();
            if ("23503".equals(state)) { // foreign_key_violation
                JOptionPane.showMessageDialog(view, "El ID de proveedor no existe (clave foránea).", "Error", JOptionPane.ERROR_MESSAGE);
                view.jTextField1.requestFocus();
            } else if ("23514".equals(state)) { // check_violation (stock >= 0)
                JOptionPane.showMessageDialog(view, "Stock debe ser ≥ 0 (violación de CHECK).", "Error", JOptionPane.ERROR_MESSAGE);
                view.jTextField4.requestFocus();
            } else if ("23502".equals(state)) { // not_null_violation
                JOptionPane.showMessageDialog(view, "Faltan campos obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Error SQL (" + state + "): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarDespuesDeGuardar() {
        view.jTextField1.setText(""); 
        view.jTextField3.setText(""); 
        view.jTextField4.setText(""); 
        view.jTextField1.requestFocus();
    }
}


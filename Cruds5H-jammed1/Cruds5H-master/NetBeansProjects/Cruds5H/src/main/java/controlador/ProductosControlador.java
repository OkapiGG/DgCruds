package controlador;

import Vista.Productos;
import Vista.Menu;
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

        view.jTextField1.setEditable(false);
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
        final String sIdProv   = view.jTextField2.getText().trim();
        final String nombre    = view.jTextField3.getText().trim();
        final String sStock    = view.jTextField4.getText().trim();

        // Validar proveedor
        if (!sIdProv.matches("\\d+")) {
            JOptionPane.showMessageDialog(view, "ID de proveedor inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField2.requestFocus();
            return;
        }
        int idProv = Integer.parseInt(sIdProv);

        // Validar nombre
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(view, "El nombre es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField3.requestFocus();
            return;
        }

        // Validar stock
        int stock = 0;
        if (!sStock.isEmpty()) {
            if (!sStock.matches("\\d+")) {
                JOptionPane.showMessageDialog(view, "Stock inválido, debe ser numérico.", "Aviso", JOptionPane.WARNING_MESSAGE);
                view.jTextField4.requestFocus();
                return;
            }
            stock = Integer.parseInt(sStock);
        }

        final String sql = "INSERT INTO productos (idproveedor, nombre, stock) VALUES (?, ?, ?) RETURNING idproducto";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProv);
            ps.setString(2, nombre);
            ps.setInt(3, stock);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    view.jTextField1.setText(String.valueOf(idGenerado)); 
                }
            }

            JOptionPane.showMessageDialog(view, "Producto guardado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
            limpiarDespuesDeGuardar();

        } catch (SQLException ex) {
            String sqlState = ex.getSQLState();
            if ("23503".equals(sqlState)) { // FK constraint
                JOptionPane.showMessageDialog(view, "El proveedor especificado no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Error SQL (" + sqlState + "): " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarDespuesDeGuardar() {
        view.jTextField2.setText("");
        view.jTextField3.setText("");
        view.jTextField4.setText("");
        view.jTextField2.requestFocus();
    }
}

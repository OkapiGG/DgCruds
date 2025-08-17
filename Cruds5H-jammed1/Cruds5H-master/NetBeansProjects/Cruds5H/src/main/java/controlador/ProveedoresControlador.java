package controlador;

import Vista.Menu;
import Vista.Proveedores;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class ProveedoresControlador implements ActionListener {

    private final Proveedores view;
    private final Connection con; 

    private static final Pattern EMAIL_RX = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public ProveedoresControlador(Proveedores view, Connection con) {
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
        final String nombre     = view.jTextField2.getText().trim(); 
        String correo           = view.jTextField3.getText().trim(); 
        final String telefono   = view.jTextField4.getText().trim(); 
        final String direccion  = view.jTextField5.getText().trim(); 
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(view, "El nombre es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField2.requestFocus();
            return;
        }
        if (!correo.isEmpty()) {
            correo = correo.toLowerCase();
            if (!EMAIL_RX.matcher(correo).matches()) {
                JOptionPane.showMessageDialog(view, "Correo inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                view.jTextField3.requestFocus();
                return;
            }
            if (existeCorreo(correo)) {
                JOptionPane.showMessageDialog(view, "Ese correo ya existe. Ingresa otro.", "Aviso", JOptionPane.WARNING_MESSAGE);
                view.jTextField3.requestFocus();
                return;
            }
        } else {
            correo = null; 
        }
        if (!telefono.isEmpty() && !telefono.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(view, "Teléfono inválido. Debe tener exactamente 10 dígitos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField4.requestFocus();
            return;
        }
        final String sql = "INSERT INTO proveedores (nombre, correo, telefono, direccion) " +
                           "VALUES (?, ?, ?, ?) RETURNING idproveedor";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            if (correo == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, correo);
            if (telefono.isEmpty()) ps.setNull(3, Types.VARCHAR); else ps.setString(3, telefono);
            if (direccion.isEmpty()) ps.setNull(4, Types.VARCHAR); else ps.setString(4, direccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    view.jTextField1.setText(String.valueOf(idGenerado)); 
                }
            }
            JOptionPane.showMessageDialog(view, "Proveedor guardado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
            limpiarDespuesDeGuardar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error SQL (" + ex.getSQLState() + "): " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean existeCorreo(String correo) {
        final String q = "SELECT 1 FROM proveedores WHERE correo = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "No se pudo verificar correo único: " + e.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private void limpiarDespuesDeGuardar() {
        view.jTextField2.setText(""); 
        view.jTextField3.setText(""); 
        view.jTextField4.setText(""); 
        view.jTextField5.setText(""); 
        view.jTextField2.requestFocus();
    }
}


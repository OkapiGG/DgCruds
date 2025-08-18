/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author ep712
 */

import Vista.ProveedorMostrar;
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class ProveedorMostrarCon implements ActionListener {

    private final ProveedorMostrar view;
    private final Connection con;

    private JButton btnBuscar;    
    private JButton btnRegresar;  

    public ProveedorMostrarCon(ProveedorMostrar view, Connection con) {
        this.view = view;
        this.con  = con;

        hookPrivateButtons();
        view.jTextField1.addActionListener(this); 
        view.jTextField2.setEditable(false);
        view.jTextField3.setEditable(false);
        view.jTextField4.setEditable(false);
        view.jTextField5.setEditable(false);

        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void hookPrivateButtons() {
        try {
            Field fBuscar = ProveedorMostrar.class.getDeclaredField("jButton1");
            fBuscar.setAccessible(true);
            btnBuscar = (JButton) fBuscar.get(view);
            btnBuscar.addActionListener(this);

            Field fRegresar = ProveedorMostrar.class.getDeclaredField("jButton2");
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

        if (src == btnBuscar || src == view.jTextField1) {
            buscarPorId();

        } else if (src == btnRegresar) {
            Menu m = new Menu();
            m.setLocationRelativeTo(view);
            m.setVisible(true);
            view.dispose();
        }
    }

    private void buscarPorId() {
        final String idTxt = view.jTextField1.getText().trim();
        if (!idTxt.matches("\\d+")) {
            JOptionPane.showMessageDialog(view, "ID inválido. Debe ser numérico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField1.requestFocus();
            return;
        }
        final int id = Integer.parseInt(idTxt);

        final String sql = "SELECT nombre, correo, telefono, direccion FROM proveedores WHERE idproveedor = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    view.jTextField2.setText(rs.getString("nombre")    != null ? rs.getString("nombre")    : "");
                    view.jTextField3.setText(rs.getString("correo")    != null ? rs.getString("correo")    : "");
                    view.jTextField4.setText(rs.getString("telefono")  != null ? rs.getString("telefono")  : "");
                    view.jTextField5.setText(rs.getString("direccion") != null ? rs.getString("direccion") : "");
                } else {
                    JOptionPane.showMessageDialog(view, "No existe un proveedor con ese ID.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limpiarTodo();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error al buscar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarTodo() {
        view.jTextField1.setText("");
        view.jTextField2.setText("");
        view.jTextField3.setText("");
        view.jTextField4.setText("");
        view.jTextField5.setText("");
        view.jTextField1.requestFocus();
    }
}

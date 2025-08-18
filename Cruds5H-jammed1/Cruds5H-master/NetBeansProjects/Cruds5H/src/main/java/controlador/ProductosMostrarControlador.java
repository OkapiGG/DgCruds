/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Vista.ProductosMostrar; // misma convención/typo que ClientesMostar
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class ProductosMostrarControlador implements ActionListener {

    private final ProductosMostrar view;
    private final Connection con;

    private JButton btnBuscar;
    private JButton btnRegresar;

    public ProductosMostrarControlador(ProductosMostrar view, Connection con) {
        this.view = view;
        this.con  = con;

        hookPrivateButtons();
        view.jTextField1.addActionListener(this); // ID producto (Enter para buscar)

        // Sólo lectura para los datos mostrados
        view.jTextField2.setEditable(false); // idproveedor
        view.jTextField3.setEditable(false); // nombre
        view.jTextField4.setEditable(false); // stock

        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /** Enlaza los botones privados jButton1 (Buscar) y jButton2 (Regresar) */
    private void hookPrivateButtons() {
        try {
            Field fBuscar = ProductosMostrar.class.getDeclaredField("jButton1");
            fBuscar.setAccessible(true);
            btnBuscar = (JButton) fBuscar.get(view);
            btnBuscar.addActionListener(this);

            Field fRegresar = ProductosMostrar.class.getDeclaredField("jButton2");
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

        final String sql = "SELECT idproveedor, nombre, stock FROM productos WHERE idproducto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    view.jTextField2.setText(String.valueOf(rs.getInt("idproveedor")));
                    view.jTextField3.setText(rs.getString("nombre") != null ? rs.getString("nombre") : "");
                    view.jTextField4.setText(String.valueOf(rs.getInt("stock")));
                } else {
                    JOptionPane.showMessageDialog(view, "No existe un producto con ese ID.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
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
        view.jTextField1.requestFocus();
    }
}


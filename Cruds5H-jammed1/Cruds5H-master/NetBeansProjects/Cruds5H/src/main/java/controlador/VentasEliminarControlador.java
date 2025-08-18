package controlador;

import Vista.VentasEliminar;
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.JOptionPane;

public class VentasEliminarControlador implements ActionListener {

    private final VentasEliminar view;
    private final Connection con;

    public VentasEliminarControlador(VentasEliminar view, Connection con) {
        this.view = view;
        this.con  = con;
        view.jButton1.addActionListener(this);
        view.jButton2.addActionListener(this);
        if (view.jTextField1 != null) view.jTextField1.setEditable(true);
        if (view.jTextField4 != null) view.jTextField4.setEditable(false);
        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == view.jButton1) {
            eliminar();
        } else if (src == view.jButton2) {
            Menu m = new Menu();
            m.setLocationRelativeTo(view);
            m.setVisible(true);
            view.dispose();
        }
    }

    private void eliminar() {
        final String idVentaTxt = (view.jTextField1 != null) ? view.jTextField1.getText().trim() : "";
        if (idVentaTxt.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ingresa el ID de venta en jTextField1 para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            if (view.jTextField1 != null) view.jTextField1.requestFocus();
            return;
        }

        Integer idVenta = parseEnteroPositivo(idVentaTxt);
        if (idVenta == null) {
            JOptionPane.showMessageDialog(view, "ID de venta inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            if (view.jTextField1 != null) view.jTextField1.requestFocus();
            return;
        }

        if (!existeVenta(idVenta)) {
            JOptionPane.showMessageDialog(view, "No existe la venta con ID: " + idVenta, "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int r = JOptionPane.showConfirmDialog(view, "¿Eliminar la venta #" + idVenta + "?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        final String sql = "DELETE FROM ventas WHERE idventa = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                limpiarCampos();
                JOptionPane.showMessageDialog(view, "Venta eliminada correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "No se pudo eliminar la venta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error SQL (" + ex.getSQLState() + "): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean existeVenta(int idVenta) {
        final String q = "SELECT 1 FROM ventas WHERE idventa = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "No se pudo verificar la venta: " + e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private Integer parseEnteroPositivo(String txt) {
        if (txt == null || txt.isEmpty()) return null;
        try {
            int v = Integer.parseInt(txt);
            return (v > 0) ? v : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void limpiarCampos() {
        if (view.jTextField1 != null) view.jTextField1.setText("");
        if (view.jTextField2 != null) view.jTextField2.setText("");
        if (view.jTextField3 != null) view.jTextField3.setText("");
        if (view.jTextField4 != null) view.jTextField4.setText("");
    }
}


package controlador;

import Vista.VentasModificar;
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

public class VentasModificarControlador implements ActionListener {

    private final VentasModificar view;
    private final Connection con;

    public  VentasModificarControlador(VentasModificar view, Connection con) {
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
            modificar();
        } else if (src == view.jButton2) {
            Menu m = new Menu();
            m.setLocationRelativeTo(view);
            m.setVisible(true);
            view.dispose();
        }
    }

    private void modificar() {
        final String idVentaTxt   = (view.jTextField1 != null) ? view.jTextField1.getText().trim() : "";
        if (idVentaTxt.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ingresa el ID de venta en jTextField1 para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            if (view.jTextField1 != null) view.jTextField1.requestFocus();
            return;
        }

        Integer idVenta = parseEnteroPositivo(idVentaTxt);
        if (idVenta == null) {
            JOptionPane.showMessageDialog(view, "ID de venta inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            if (view.jTextField1 != null) view.jTextField1.requestFocus();
            return;
        }

        final String idClienteTxt = (view.jTextField2 != null) ? view.jTextField2.getText().trim() : "";
        final String totalTxt     = (view.jTextField3 != null) ? view.jTextField3.getText().trim() : "";

        if (idClienteTxt.isEmpty() && totalTxt.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No hay cambios. Escribe un nuevo ID de cliente y/o un nuevo total.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer idClienteNuevo = null;
        if (!idClienteTxt.isEmpty()) {
            idClienteNuevo = parseEnteroPositivo(idClienteTxt);
            if (idClienteNuevo == null) {
                JOptionPane.showMessageDialog(view, "ID de cliente inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                if (view.jTextField2 != null) view.jTextField2.requestFocus();
                return;
            }
            if (!existeCliente(idClienteNuevo)) {
                JOptionPane.showMessageDialog(view, "El cliente no existe.", "Aviso", JOptionPane.WARNING_MESSAGE);
                if (view.jTextField2 != null) view.jTextField2.requestFocus();
                return;
            }
        }

        BigDecimal totalNuevo = null;
        if (!totalTxt.isEmpty()) {
            try {
                totalNuevo = new BigDecimal(totalTxt).setScale(2, RoundingMode.HALF_UP);
                if (totalNuevo.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(view, "El total no puede ser negativo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    if (view.jTextField3 != null) view.jTextField3.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Total inválido. Ejemplo válido: 100.00", "Aviso", JOptionPane.WARNING_MESSAGE);
                if (view.jTextField3 != null) view.jTextField3.requestFocus();
                return;
            }
        }

        if (!existeVenta(idVenta)) {
            JOptionPane.showMessageDialog(view, "No existe la venta con ID: " + idVenta, "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String sql = "UPDATE ventas SET idcliente = COALESCE(?, idcliente), total = COALESCE(?, total) WHERE idventa = ? RETURNING idventa, idcliente, fecha, total";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (idClienteNuevo != null) ps.setInt(1, idClienteNuevo); else ps.setNull(1, Types.INTEGER);
            if (totalNuevo != null) ps.setBigDecimal(2, totalNuevo); else ps.setNull(2, Types.NUMERIC);
            ps.setInt(3, idVenta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idVentaDb        = rs.getInt("idventa");
                    int idClienteDb      = rs.getInt("idcliente");
                    Timestamp fechaDb    = rs.getTimestamp("fecha");
                    BigDecimal totalDb   = rs.getBigDecimal("total");

                    if (view.jTextField1 != null) view.jTextField1.setText(String.valueOf(idVentaDb));
                    if (view.jTextField2 != null) view.jTextField2.setText(String.valueOf(idClienteDb));
                    if (view.jTextField4 != null) view.jTextField4.setText(fmtFecha(fechaDb));
                    if (view.jTextField3 != null) view.jTextField3.setText(totalDb.setScale(2, RoundingMode.HALF_UP).toPlainString());

                    JOptionPane.showMessageDialog(view, "Venta modificada correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view, "No se pudo modificar la venta.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
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

    private String fmtFecha(Timestamp ts) {
        if (ts == null) return "";
        return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private boolean existeCliente(int idCliente) {
        final String q = "SELECT 1 FROM clientes WHERE idcliente = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "No se pudo verificar el cliente: " + e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }
}

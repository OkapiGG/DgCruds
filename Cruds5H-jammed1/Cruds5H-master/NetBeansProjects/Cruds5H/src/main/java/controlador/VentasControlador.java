package controlador;

import Vista.Ventas;
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

public class VentasControlador implements ActionListener {

    private final Ventas view;
    private final Connection con;

    public VentasControlador(Ventas view, Connection con) {
        this.view = view;
        this.con  = con;

        view.jButton1.addActionListener(this); 
        view.jButton2.addActionListener(this); 

        if (view.jTextField1 != null) view.jTextField1.setEditable(false);
        if (view.jTextField4 != null) view.jTextField4.setEditable(false);

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
        final String idClienteTxt = view.jTextField2.getText().trim();
        final String totalTxt     = view.jTextField3.getText().trim();

        if (idClienteTxt.isEmpty()) {
            JOptionPane.showMessageDialog(view, "El ID de cliente es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField2.requestFocus();
            return;
        }

        int idCliente;
        try {
            idCliente = Integer.parseInt(idClienteTxt);
            if (idCliente <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "ID de cliente inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField2.requestFocus();
            return;
        }

        if (!existeCliente(idCliente)) {
            JOptionPane.showMessageDialog(view, "El cliente no existe.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField2.requestFocus();
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        if (!totalTxt.isEmpty()) {
            try {
                total = new BigDecimal(totalTxt).setScale(2, RoundingMode.HALF_UP);
                if (total.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(view, "El total no puede ser negativo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    view.jTextField3.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Total inválido. Ejemplo válido: 100.00", "Aviso", JOptionPane.WARNING_MESSAGE);
                view.jTextField3.requestFocus();
                return;
            }
        }

        final String sql = "INSERT INTO ventas (idcliente, total) VALUES (?, ?) RETURNING idventa, idcliente, fecha, total";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setBigDecimal(2, total);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idVenta        = rs.getInt("idventa");
                    int idClienteDb    = rs.getInt("idcliente");
                    Timestamp fechaDb  = rs.getTimestamp("fecha");
                    BigDecimal totalDb = rs.getBigDecimal("total");

                    view.jTextField2.setText(String.valueOf(idClienteDb)); 
                    if (view.jTextField1 != null) view.jTextField1.setText(String.valueOf(idVenta)); 

                    if (view.jTextField4 != null) { 
                        String fechaFmt = fechaDb.toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                        view.jTextField4.setText(fechaFmt);
                    }

                    view.jTextField3.setText(totalDb.setScale(2, RoundingMode.HALF_UP).toPlainString()); 
                }
            }

            JOptionPane.showMessageDialog(view, "Venta guardada correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
            limpiarDespuesDeGuardar();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error SQL (" + ex.getSQLState() + "): " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean existeCliente(int idCliente) {
        final String q = "SELECT 1 FROM clientes WHERE idcliente = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "No se pudo verificar el cliente: " + e.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private void limpiarDespuesDeGuardar() {
        view.jTextField2.setText(""); 
        view.jTextField3.setText(""); 
        if (view.jTextField4 != null) view.jTextField4.setText(""); 
        view.jTextField2.requestFocus();
    }
}

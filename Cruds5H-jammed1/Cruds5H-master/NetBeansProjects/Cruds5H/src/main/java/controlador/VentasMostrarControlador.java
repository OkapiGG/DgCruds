package controlador;

import Vista.VentasMostrar;
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class VentasMostrarControlador implements ActionListener {

    private final VentasMostrar view;
    private final Connection con;

    public VentasMostrarControlador(VentasMostrar view, Connection con) {
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
            consultar();
        } else if (src == view.jButton2) {
            Menu m = new Menu();
            m.setLocationRelativeTo(view);
            m.setVisible(true);
            view.dispose();
        }
    }
    
    private void consultar() {
        final String idVentaTxt   = (view.jTextField1 != null) ? view.jTextField1.getText().trim() : "";
        final String idClienteTxt = (view.jTextField2 != null) ? view.jTextField2.getText().trim() : "";

        Integer idVenta = parseEnteroPositivo(idVentaTxt);
        Integer idCliente = parseEnteroPositivo(idClienteTxt);

        if (idVenta != null) {
            consultarPorIdVenta(idVenta);
            return;
        }

        if (idCliente != null) {
            consultarPorCliente(idCliente);
            return;
        }

        JOptionPane.showMessageDialog(view,
                "Ingresa un ID de venta (jTextField1) o un ID de cliente (jTextField2) para consultar.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
        if (view.jTextField1 != null) view.jTextField1.requestFocus();
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

    private void consultarPorIdVenta(int idVenta) {
        final String sql = "SELECT idventa, idcliente, fecha, total " +
                           "FROM ventas WHERE idventa = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cargarEnVista(rs);
                } else {
                    JOptionPane.showMessageDialog(view, "No existe la venta con ID: " + idVenta,
                            "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCamposResultado();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error SQL (" + ex.getSQLState() + "): " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultarPorCliente(int idCliente) {
        if (!existeCliente(idCliente)) {
            JOptionPane.showMessageDialog(view, "El cliente no existe.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField2.requestFocus();
            return;
        }

        final String sql = "SELECT idventa, idcliente, fecha, total " +
                           "FROM ventas WHERE idcliente = ? ORDER BY fecha DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {

                List<VentaRow> filas = new ArrayList<>();
                while (rs.next()) {
                    filas.add(new VentaRow(
                            rs.getInt("idventa"),
                            rs.getInt("idcliente"),
                            rs.getTimestamp("fecha"),
                            rs.getBigDecimal("total")
                    ));
                }

                if (filas.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "El cliente " + idCliente + " no tiene ventas.",
                            "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCamposResultado();
                    return;
                }

                if (filas.size() == 1) {
                    cargarEnVista(filas.get(0));
                    return;
                }

                String[] opciones = filas.stream()
                        .map(v -> "Venta #" + v.idventa + " | " +
                                  fmtFecha(v.fecha) + " | $" + v.total.setScale(2, RoundingMode.HALF_UP))
                        .toArray(String[]::new);

                String seleccion = (String) JOptionPane.showInputDialog(
                        view,
                        "Selecciona una venta del cliente " + idCliente + ":",
                        "Resultados (" + filas.size() + ")",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );

                if (seleccion == null) {
                    return;
                }
                
                int elegido = extraerIdVentaDeSeleccion(seleccion);
                filas.stream()
                        .filter(v -> v.idventa == elegido)
                        .findFirst()
                        .ifPresent(this::cargarEnVista);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error SQL (" + ex.getSQLState() + "): " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int extraerIdVentaDeSeleccion(String s) {
        try {
            int posHash = s.indexOf('#');
            int posPipe = s.indexOf('|', posHash);
            String id = s.substring(posHash + 1, posPipe).trim();
            return Integer.parseInt(id);
        } catch (Exception e) {
            return -1;
        }
    }

    private void cargarEnVista(ResultSet rs) throws SQLException {
        int idVenta        = rs.getInt("idventa");
        int idClienteDb    = rs.getInt("idcliente");
        Timestamp fechaDb  = rs.getTimestamp("fecha");
        BigDecimal totalDb = rs.getBigDecimal("total");

        if (view.jTextField1 != null) view.jTextField1.setText(String.valueOf(idVenta));
        if (view.jTextField2 != null) view.jTextField2.setText(String.valueOf(idClienteDb));
        if (view.jTextField4 != null) view.jTextField4.setText(fmtFecha(fechaDb));
        view.jTextField3.setText(totalDb.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    private void cargarEnVista(VentaRow v) {
        if (view.jTextField1 != null) view.jTextField1.setText(String.valueOf(v.idventa));
        if (view.jTextField2 != null) view.jTextField2.setText(String.valueOf(v.idcliente));
        if (view.jTextField4 != null) view.jTextField4.setText(fmtFecha(v.fecha));
        view.jTextField3.setText(v.total.setScale(2, RoundingMode.HALF_UP).toPlainString());
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
            JOptionPane.showMessageDialog(view, "No se pudo verificar el cliente: " + e.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private void limpiarCamposResultado() {
        if (view.jTextField1 != null) view.jTextField1.setText(""); 
        view.jTextField3.setText(""); 
        if (view.jTextField4 != null) view.jTextField4.setText(""); 
    }

    private static class VentaRow {
        final int idventa;
        final int idcliente;
        final Timestamp fecha;
        final BigDecimal total;

        VentaRow(int idventa, int idcliente, Timestamp fecha, BigDecimal total) {
            this.idventa = idventa;
            this.idcliente = idcliente;
            this.fecha = fecha;
            this.total = total != null ? total : BigDecimal.ZERO;
        }
    }
}


package controlador;

import Vista.ClientesModificar;
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class ClientesModificarControlador implements ActionListener {

    private final ClientesModificar view;
    private final Connection con;

    // Botones privados en la vista (acceso por reflection)
    private JButton btnListo;     // jButton1
    private JButton btnRegresar;  // jButton2

    private static final Pattern EMAIL_RX =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public ClientesModificarControlador(ClientesModificar view, Connection con) {
        this.view = view;
        this.con  = con;

        hookPrivateButtons();               
        view.jTextField1.addActionListener(this); 

        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }


    private void hookPrivateButtons() {
        try {
            Field fListo = ClientesModificar.class.getDeclaredField("jButton1");
            fListo.setAccessible(true);
            btnListo = (JButton) fListo.get(view);
            btnListo.addActionListener(this);

            Field fRegresar = ClientesModificar.class.getDeclaredField("jButton2");
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
            actualizarCliente();

        } else if (src == btnRegresar) {
            Menu m = new Menu();
            m.setLocationRelativeTo(view);
            m.setVisible(true);
            view.dispose();

        } else if (src == view.jTextField1) {
            cargarClientePorId();
        }
    }

    private void cargarClientePorId() {
        final String idTxt = view.jTextField1.getText().trim();
        if (!idTxt.matches("\\d+")) {
            JOptionPane.showMessageDialog(view, "ID inválido. Debe ser numérico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField1.requestFocus();
            return;
        }
        final int id = Integer.parseInt(idTxt);

        final String sql = "SELECT nombre, correo, telefono FROM clientes WHERE idcliente = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    view.jTextField2.setText(rs.getString("nombre")   != null ? rs.getString("nombre")   : "");
                    view.jTextField3.setText(rs.getString("correo")   != null ? rs.getString("correo")   : "");
                    view.jTextField4.setText(rs.getString("telefono") != null ? rs.getString("telefono") : "");
                    view.jTextField2.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(view, "No existe un cliente con ese ID.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCamposExceptoId(); // deja el ID para intentar otro
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error al cargar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCliente() {
        final String idTxt = view.jTextField1.getText().trim();
        if (!idTxt.matches("\\d+")) {
            JOptionPane.showMessageDialog(view, "ID inválido. Debe ser numérico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField1.requestFocus();
            return;
        }
        final int id = Integer.parseInt(idTxt);

        final String nombre   = view.jTextField2.getText().trim();
        String correo         = view.jTextField3.getText().trim();
        final String telefono = view.jTextField4.getText().trim();

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
            if (correoUsadoPorOtro(correo, id)) {
                JOptionPane.showMessageDialog(view, "Ese correo ya está registrado para otro cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                view.jTextField3.requestFocus();
                return;
            }
        } else {
            correo = null;
        }

        if (!telefono.isEmpty() && !telefono.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(view, "Teléfono inválido. Debe tener 10 dígitos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField4.requestFocus();
            return;
        }

        if (!existeCliente(id)) {
            JOptionPane.showMessageDialog(view, "No existe un cliente con ese ID.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String sql = "UPDATE clientes SET nombre = ?, correo = ?, telefono = ? WHERE idcliente = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            if (correo == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, correo);
            if (telefono.isEmpty()) ps.setNull(3, Types.VARCHAR); else ps.setString(3, telefono);
            ps.setInt(4, id);

            int filas = ps.executeUpdate();
            if (filas == 1) {
                JOptionPane.showMessageDialog(view, "Cliente actualizado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                limpiarTodo();
            } else {
                JOptionPane.showMessageDialog(view, "No se actualizó el registro (revisa el ID).", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error SQL (" + ex.getSQLState() + "): " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean correoUsadoPorOtro(String correo, int idActual) {
        final String q = "SELECT 1 FROM clientes WHERE correo = ? AND idcliente <> ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setString(1, correo);
            ps.setInt(2, idActual);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "No se pudo verificar correo único: " + e.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private boolean existeCliente(int id) {
        final String q = "SELECT 1 FROM clientes WHERE idcliente = ? LIMIT 1";
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

    private void limpiarCamposExceptoId() {
        view.jTextField2.setText("");
        view.jTextField3.setText("");
        view.jTextField4.setText("");
        view.jTextField1.requestFocus();
    }

    private void limpiarTodo() {
        view.jTextField1.setText("");
        view.jTextField2.setText("");
        view.jTextField3.setText("");
        view.jTextField4.setText("");
        view.jTextField1.requestFocus();
    }
}

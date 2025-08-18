package controlador;

import Vista.Clientes;
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class ClientesControlador implements ActionListener {

    private final Clientes view;
    private final Connection con;

    private static final Pattern EMAIL_RX = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public ClientesControlador(Clientes view, Connection con) {
        this.view = view;
        this.con = con;

        view.jButton1.addActionListener(this);
        view.jButton2.addActionListener(this);

        view.jTextField1.setEditable(false);

        view.jButton1.addActionListener(this);
        view.jButton2.addActionListener(this);
        view.jTextField1.setEditable(false);
        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        System.out.println("");
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
        final String nombre = view.jTextField2.getText().trim();
        String correo = view.jTextField3.getText().trim();
        final String telefono = view.jTextField4.getText().trim();

        // 1) nombre requerido
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

        final String sql = "INSERT INTO clientes (nombre, correo, telefono) VALUES (?, ?, ?) RETURNING idcliente";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            if (correo == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, correo);
            }
            if (telefono.isEmpty()) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, telefono);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    view.jTextField1.setText(String.valueOf(idGenerado));
                }
            }

            JOptionPane.showMessageDialog(view, "Cliente guardado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
            limpiarDespuesDeGuardar();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error SQL (" + ex.getSQLState() + "): " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean existeCorreo(String correo) {
        final String q = "SELECT 1 FROM clientes WHERE correo = ? LIMIT 1";
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
        view.jTextField2.requestFocus();
    }

    // ========================= NUEVOS MÉTODOS CRUD =========================
    private void actualizarCliente() {
        try {
            int id = Integer.parseInt(view.jTextField1.getText());
            String nombre = view.jTextField2.getText().trim();
            String correo = view.jTextField3.getText().trim();
            String telefono = view.jTextField4.getText().trim();

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
            } else {
                correo = null;
            }

            String sql = "UPDATE clientes SET nombre=?, correo=?, telefono=? WHERE idcliente=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                if (correo == null) {
                    ps.setNull(2, Types.VARCHAR);
                } else {
                    ps.setString(2, correo);
                }
                if (telefono.isEmpty()) {
                    ps.setNull(3, Types.VARCHAR);
                } else {
                    ps.setString(3, telefono);
                }
                ps.setInt(4, id);

                int res = ps.executeUpdate();
                if (res > 0) {
                    JOptionPane.showMessageDialog(view, "Cliente actualizado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                    limpiarDespuesDeGuardar();
                } else {
                    JOptionPane.showMessageDialog(view, "No se encontró cliente con ese ID.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "ID inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error SQL (" + e.getSQLState() + "): " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        try {
            int id = Integer.parseInt(view.jTextField1.getText());
            int opcion = JOptionPane.showConfirmDialog(view, "¿Desea eliminar el cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM clientes WHERE idcliente=?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    int res = ps.executeUpdate();
                    if (res > 0) {
                        JOptionPane.showMessageDialog(view, "Cliente eliminado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                        limpiarDespuesDeGuardar();
                        view.jTextField1.setText("");
                    } else {
                        JOptionPane.showMessageDialog(view, "No se encontró cliente con ese ID.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "ID inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error SQL (" + e.getSQLState() + "): " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarClientes() {
//        String sql = "SELECT idcliente, nombre, correo, telefono FROM clientes ORDER BY idcliente";
//        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
//            view.jTextArea1.setText(""); // tu área de texto
//            while (rs.next()) {
//                view.jTextArea1.append(
//                    rs.getInt("idcliente") + " - " +
//                    rs.getString("nombre") + " - " +
//                    rs.getString("correo") + " - " +
//                    rs.getString("telefono") + "\n"
//                );
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(view, "Error al listar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }

}

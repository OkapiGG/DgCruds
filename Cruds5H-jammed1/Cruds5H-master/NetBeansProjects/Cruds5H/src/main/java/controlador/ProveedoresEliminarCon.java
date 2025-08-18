package controlador;

import Vista.ProveedoresEliminar;   // <<< usa la vista correcta
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class ProveedoresEliminarCon implements ActionListener {

    private final ProveedoresEliminar view; 
    private final Connection con;

    private JButton btnListo;
    private JButton btnRegresar;

    public ProveedoresEliminarCon(ProveedoresEliminar view, Connection con) {
        this.view = view;
        this.con  = con;

        hookPrivateButtons();
        view.jTextField1.addActionListener(this);

        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void hookPrivateButtons() {
        try {
            Field fListo = view.getClass().getDeclaredField("jButton1");
            fListo.setAccessible(true);
            btnListo = (JButton) fListo.get(view);
            btnListo.addActionListener(this);

            Field fRegresar = view.getClass().getDeclaredField("jButton2");
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
            eliminarProveedor();

        } else if (src == btnRegresar) {
            Menu m = new Menu();
            m.setLocationRelativeTo(view);
            m.setVisible(true);
            view.dispose();

        } else if (src == view.jTextField1) {
            cargarProveedorPorId();
        }
    }

    private void cargarProveedorPorId() {
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
                    view.jTextField1.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(view, "No existe un proveedor con ese ID.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limpiarTodo();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error al cargar proveedor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProveedor() {
        final String idTxt = view.jTextField1.getText().trim();
        if (!idTxt.matches("\\d+")) {
            JOptionPane.showMessageDialog(view, "ID inválido. Debe ser numérico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextField1.requestFocus();
            return;
        }
        final int id = Integer.parseInt(idTxt);

        int opt = JOptionPane.showConfirmDialog(
            view,
            "¿Seguro que deseas eliminar al proveedor con ID " + id + "?\nEsta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (opt != JOptionPane.YES_OPTION) return;

        if (!existeProveedor(id)) {
            JOptionPane.showMessageDialog(view, "No existe un proveedor con ese ID.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!camposCoincidenConBD(id)) {
            JOptionPane.showMessageDialog(view,
                "Los campos en pantalla NO coinciden con los datos registrados para ese ID.\n" +
                "Presiona ENTER en el campo ID para recargar y verifica antes de eliminar.",
                "Validación de datos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String sql = "DELETE FROM proveedores WHERE idproveedor = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 1) {
                JOptionPane.showMessageDialog(view, "Proveedor eliminado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                limpiarTodo();
            } else {
                JOptionPane.showMessageDialog(view, "No se eliminó el registro (revisa el ID).", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            String sqlState = ex.getSQLState();
            if ("23503".equals(sqlState) || "23000".equals(sqlState)) {
                JOptionPane.showMessageDialog(view,
                    "No se puede eliminar: el proveedor tiene registros relacionados.",
                    "Restricción de integridad",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view,
                    "Error SQL (" + sqlState + "): " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean existeProveedor(int id) {
        final String q = "SELECT 1 FROM proveedores WHERE idproveedor = ? LIMIT 1";
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

    private boolean camposCoincidenConBD(int id) {
        final String vNombre    = view.jTextField2.getText() != null ? view.jTextField2.getText().trim() : "";
        final String vCorreo    = view.jTextField3.getText() != null ? view.jTextField3.getText().trim().toLowerCase() : "";
        final String vTel       = view.jTextField4.getText() != null ? view.jTextField4.getText().trim() : "";
        final String vDireccion = view.jTextField5.getText() != null ? view.jTextField5.getText().trim() : "";

        final String sql = "SELECT nombre, correo, telefono, direccion FROM proveedores WHERE idproveedor = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;

                String dbNombre    = rs.getString("nombre");
                String dbCorreo    = rs.getString("correo");
                String dbTel       = rs.getString("telefono");
                String dbDireccion = rs.getString("direccion");

                dbNombre    = dbNombre    != null ? dbNombre.trim()                       : "";
                dbCorreo    = dbCorreo    != null ? dbCorreo.trim().toLowerCase()         : "";
                dbTel       = dbTel       != null ? dbTel.trim()                          : "";
                dbDireccion = dbDireccion != null ? dbDireccion.trim()                    : "";

                return vNombre.equals(dbNombre)
                        && vCorreo.equals(dbCorreo)
                        && vTel.equals(dbTel)
                        && vDireccion.equals(dbDireccion);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error al validar campos contra BD: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
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

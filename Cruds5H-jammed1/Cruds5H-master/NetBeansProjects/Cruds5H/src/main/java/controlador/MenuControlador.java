package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Vista.Menu;
import Vista.ReporteVentaVista;
import Vista.SubMenuClientes;
import Vista.SubMenuProductos;
import Vista.SubmenuProveedor;
import Vista.SubMenuVentas;

public class MenuControlador implements ActionListener {

    private final Menu menu;

    public MenuControlador(Menu menu) {
        this.menu = menu;
        menu.jButton1.addActionListener(this);
        menu.jButton2.addActionListener(this);
        menu.jButton3.addActionListener(this);
        menu.jButton4.addActionListener(this);
        menu.jButton5.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == menu.jButton1) {
            SubMenuClientes v = new SubMenuClientes();
            v.setLocationRelativeTo(menu);
            v.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            menu.dispose();
        } else if (src == menu.jButton2) {
            SubMenuProductos v = new SubMenuProductos();
            v.setLocationRelativeTo(menu);
            v.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            menu.dispose();
        } else if (src == menu.jButton3) {
            SubmenuProveedor v = new SubmenuProveedor();
            v.setLocationRelativeTo(menu);
            v.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            menu.dispose();
        } else if (src == menu.jButton4) {
            SubMenuVentas v = new SubMenuVentas();
            v.setLocationRelativeTo(menu);
            v.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            menu.dispose();
        } else if (src == menu.jButton5) {
            try {
                ReporteVentaVista v = new ReporteVentaVista();
                java.sql.Connection con = modelo.conexion.getInstancia().getConexion();
                new ReporteVentaControlador(v, con);
                v.setLocationRelativeTo(menu);
                v.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                v.setVisible(true);
                menu.dispose();
            } catch (java.sql.SQLException ex) {
                javax.swing.JOptionPane.showMessageDialog(
                        menu,
                        "No se pudo abrir el reporte (BD): " + ex.getMessage(),
                        "Error de conexión",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}

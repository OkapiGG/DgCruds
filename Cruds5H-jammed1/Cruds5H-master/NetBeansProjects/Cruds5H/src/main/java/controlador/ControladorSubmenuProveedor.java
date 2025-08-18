package controlador;

import Vista.ProveedorModificar;
import Vista.ProveedorMostrar;
import Vista.Proveedores;
import Vista.ProveedoresEliminar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Vista.SubMenuClientes; 
import Vista.SubmenuProveedor;
import javax.swing.WindowConstants;

public class ControladorSubmenuProveedor implements ActionListener {

    private final SubmenuProveedor subMenu;

    public ControladorSubmenuProveedor(SubmenuProveedor subMenu) {
        this.subMenu = subMenu;
        subMenu.jButton1.addActionListener(this); 
        subMenu.jButton2.addActionListener(this); 
        subMenu.jButton3.addActionListener(this);
        subMenu.jButton4.addActionListener(this); 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == subMenu.jButton1) {         
            Proveedores v = new Proveedores();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();

        } else if (src == subMenu.jButton2) {   
            ProveedorMostrar v = new ProveedorMostrar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();

        } else if (src == subMenu.jButton3) {  
            ProveedorModificar v = new ProveedorModificar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();

        } else if (src == subMenu.jButton4) {   
            ProveedoresEliminar v = new ProveedoresEliminar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();
        }
    }
}

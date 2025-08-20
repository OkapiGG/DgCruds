/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;  
import Vista.SubMenuVentas;
import Vista.Ventas;
import Vista.VentasEliminar;
import Vista.VentasModificar;
import Vista.VentasMostrar;
import javax.swing.WindowConstants;

public class ControladorSubMenuVentas implements ActionListener {

    private final SubMenuVentas subMenu;

    public ControladorSubMenuVentas(SubMenuVentas subMenu) {
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
            Ventas v = new Ventas();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();

        } else if (src == subMenu.jButton2) {   
            VentasMostrar v = new VentasMostrar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();

        } else if (src == subMenu.jButton3) {  
            VentasModificar v = new VentasModificar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();

        } else if (src == subMenu.jButton4) {   
            VentasEliminar v = new VentasEliminar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();
        }
    }
}


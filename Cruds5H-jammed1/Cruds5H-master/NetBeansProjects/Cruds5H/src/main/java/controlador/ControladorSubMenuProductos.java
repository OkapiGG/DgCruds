/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Vista.SubMenuProductos;
import Vista.Productos;
import Vista.ProductosMostrar;
import Vista.ProductosModificar;
import Vista.ProductosEliminar;
import javax.swing.WindowConstants;

public class ControladorSubMenuProductos implements ActionListener {

    private final SubMenuProductos subMenu;

    public ControladorSubMenuProductos(SubMenuProductos subMenu) {
        this.subMenu = subMenu;
        subMenu.jButton1.addActionListener(this); // Insertar
        subMenu.jButton2.addActionListener(this); // Mostrar
        subMenu.jButton3.addActionListener(this); // Modificar
        subMenu.jButton4.addActionListener(this); // Eliminar
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == subMenu.jButton1) {          // Insertar
            Productos v = new Productos();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();

        } else if (src == subMenu.jButton2) {   // Mostrar
            ProductosMostrar v = new ProductosMostrar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();

        } else if (src == subMenu.jButton3) {   // Modificar
            ProductosModificar v = new ProductosModificar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();

        } else if (src == subMenu.jButton4) {   // Eliminar
            ProductosEliminar v = new ProductosEliminar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();
        }
    }
}


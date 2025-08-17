package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Vista.Menu;
import Vista.Clientes;
import Vista.Productos;
import Vista.Proveedores;
import Vista.Ventas;

public class MenuControlador implements ActionListener {

    private final Menu menu;

    public MenuControlador(Menu menu) {
        this.menu = menu;
        menu.jButton1.addActionListener(this); 
        menu.jButton2.addActionListener(this); 
        menu.jButton3.addActionListener(this);
        menu.jButton4.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == menu.jButton1) {          
            Clientes v = new Clientes();
            v.setLocationRelativeTo(menu);
            v.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            menu.dispose(); // 
        } else if (src == menu.jButton2) {    
            Productos v = new Productos();
            v.setLocationRelativeTo(menu);
            v.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            menu.dispose();
        } else if (src == menu.jButton3) {   
            Proveedores v = new Proveedores();
            v.setLocationRelativeTo(menu);
            v.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            menu.dispose();
        } else if (src == menu.jButton4) {   
            Ventas v = new Ventas();
            v.setLocationRelativeTo(menu);
            v.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            menu.dispose();
        }
    }
}
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Vista.SubMenuClientes;
import Vista.Clientes;       
import Vista.ClientesModificar; 
import Vista.ClientesMostar;   
import Vista.ClientesEliminar; 
import javax.swing.WindowConstants;

public class ControladorSubMenuClientes implements ActionListener {

    private final SubMenuClientes subMenu;

    public ControladorSubMenuClientes(SubMenuClientes subMenu) {
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
            Clientes v = new Clientes();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();

        } else if (src == subMenu.jButton2) {     
            ClientesModificar v = new ClientesModificar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();
        } else if (src == subMenu.jButton3) {    
            ClientesMostar v = new ClientesMostar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();
        } else if (src == subMenu.jButton4) {     
            ClientesEliminar v = new ClientesEliminar();
            v.setLocationRelativeTo(subMenu);
            v.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            v.setVisible(true);
            subMenu.dispose();
        }
    }
}


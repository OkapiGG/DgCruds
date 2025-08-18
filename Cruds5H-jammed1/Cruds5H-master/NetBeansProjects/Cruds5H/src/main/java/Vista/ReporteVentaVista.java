/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

import javax.swing.*;

public class ReporteVentaVista extends JFrame {
    public JTextField jTextFieldIdVenta = new JTextField(10);
    public JTable jTable1 = new JTable();
    public JLabel jLabelTotalItems = new JLabel("0");
    public JLabel jLabelTotalPagar = new JLabel("$ 0.00");
    public JButton jButtonBuscar = new JButton("Buscar");
    public JButton jButtonRegresar = new JButton("Regresar");

    public ReporteVentaVista() {
        setTitle("Reporte de Venta");
        JScrollPane sp = new JScrollPane(jTable1);

        JPanel top = new JPanel();
        top.add(new JLabel("ID Venta:"));
        top.add(jTextFieldIdVenta);
        top.add(jButtonBuscar);
        top.add(jButtonRegresar);

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Total items:"));
        bottom.add(jLabelTotalItems);
        bottom.add(new JLabel("   Total a pagar:"));
        bottom.add(jLabelTotalPagar);

        getContentPane().add(top, java.awt.BorderLayout.NORTH);
        getContentPane().add(sp, java.awt.BorderLayout.CENTER);
        getContentPane().add(bottom, java.awt.BorderLayout.SOUTH);
        setSize(750, 450);
    }
}


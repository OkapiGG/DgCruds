/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Vista.ReporteVentaVista;
import Vista.Menu;
import modelo.ReportesDAO;
import modelo.ReporteVenta;
import modelo.ReporteVentaItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.Connection;

public class ReporteVentaControlador implements ActionListener {

    private final ReporteVentaVista view;
    private final ReportesDAO dao;

    public ReporteVentaControlador(ReporteVentaVista view, Connection con) {
        this.view = view;
        this.dao  = new ReportesDAO(con);

        view.jButtonBuscar.addActionListener(this);
        view.jButtonRegresar.addActionListener(this);
        view.jTextFieldIdVenta.addActionListener(this);

        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        view.jTable1.setModel(new DefaultTableModel(
            new Object[]{"ID Producto","Nombre","Cantidad","Precio Unitario","Subtotal"}, 0
        ));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == view.jButtonBuscar || src == view.jTextFieldIdVenta) {
            buscar();
        } else if (src == view.jButtonRegresar) {
            Menu m = new Menu();
            m.setLocationRelativeTo(view);
            m.setVisible(true);
            view.dispose();
        }
    }

    private void buscar() {
        String idTxt = view.jTextFieldIdVenta.getText().trim();
        if (!idTxt.matches("\\d+")) {
            JOptionPane.showMessageDialog(view, "ID de venta inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            view.jTextFieldIdVenta.requestFocus(); return;
        }
        int idVenta = Integer.parseInt(idTxt);

        try {
            ReporteVenta rep = dao.reporteVenta(idVenta);
            DefaultTableModel m = (DefaultTableModel) view.jTable1.getModel();
            m.setRowCount(0);
            for (ReporteVentaItem it : rep.getItems()) {
                m.addRow(new Object[]{
                    it.getIdproducto(),
                    it.getNombre(),
                    it.getCantidad(),
                    it.getPrecioUnitario(),
                    it.getSubtotal()
                });
            }
            view.jLabelTotalItems.setText(String.valueOf(rep.getTotalItems()));
            BigDecimal total = rep.getTotalPagar() == null ? BigDecimal.ZERO : rep.getTotalPagar();
            view.jLabelTotalPagar.setText(String.format("$ %.2f", total));
            if (rep.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Venta sin detalle o inexistente.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error al obtener reporte: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


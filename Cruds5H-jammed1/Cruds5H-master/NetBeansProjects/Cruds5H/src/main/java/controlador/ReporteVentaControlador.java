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

    private void exportarCSV() {
        DefaultTableModel m = (DefaultTableModel) view.jTable1.getModel();
        if (m.getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "No hay datos para exportar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new java.io.File("reporte_venta.csv"));
        if (ch.showSaveDialog(view) != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter fw = new FileWriter(ch.getSelectedFile())) {
            fw.write("idproducto,nombre,cantidad,precio_unitario,subtotal\n");
            for (int i = 0; i < m.getRowCount(); i++) {
                fw.write(m.getValueAt(i,0)+","+
                         csv(m.getValueAt(i,1))+","+
                         m.getValueAt(i,2)+","+
                         m.getValueAt(i,3)+","+
                         m.getValueAt(i,4)+"\n");
            }
            fw.write("\nTotal items," + view.jLabelTotalItems.getText() + "\n");
            fw.write("Total a pagar," + view.jLabelTotalPagar.getText() + "\n");
            JOptionPane.showMessageDialog(view, "Exportado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "No se pudo exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csv(Object v) {
        if (v == null) return "";
        String s = v.toString();
        if (s.contains(",") || s.contains("\"")) s = "\""+s.replace("\"","\"\"")+"\"";
        return s;
    }
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class ReportesDAO {
    private final Connection con;
    public ReportesDAO(Connection con) { this.con = con; }

    public ReporteVenta reporteVenta(int idVenta) throws SQLException {
        String sql = "SELECT idproducto, nombre, cantidad, precio_unitario, subtotal, total_items, total_pagar " +
                     "FROM f_reporte_venta(?)";
        List<ReporteVentaItem> items = new ArrayList<>();
        int totalItems = 0;
        BigDecimal totalPagar = BigDecimal.ZERO;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    items.add(new ReporteVentaItem(
                        rs.getInt("idproducto"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getBigDecimal("precio_unitario"),
                        rs.getBigDecimal("subtotal")
                    ));
                    totalItems = rs.getInt("total_items");
                    totalPagar = rs.getBigDecimal("total_pagar");
                }
                if (!any) return new ReporteVenta(Collections.emptyList(), 0, BigDecimal.ZERO);
            }
        }
        return new ReporteVenta(items, totalItems, totalPagar);
    }
}


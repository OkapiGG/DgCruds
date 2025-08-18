/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;
import java.util.List;

public class ReporteVenta {
    private final List<ReporteVentaItem> items;
    private final int totalItems;
    private final BigDecimal totalPagar;

    public ReporteVenta(List<ReporteVentaItem> items, int totalItems, BigDecimal totalPagar) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalPagar = totalPagar;
    }
    public List<ReporteVentaItem> getItems() { return items; }
    public int getTotalItems() { return totalItems; }
    public BigDecimal getTotalPagar() { return totalPagar; }
}


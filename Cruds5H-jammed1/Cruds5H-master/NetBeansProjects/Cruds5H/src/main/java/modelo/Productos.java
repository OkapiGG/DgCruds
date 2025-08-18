package modelo;

public class Productos {
    private int idproducto;
    private int idproveedor;
    private String nombre;
    private int stock;

    public Productos() {
    }

    public Productos(int idproducto, int idproveedor, String nombre, int stock) {
        this.idproducto = idproducto;
        this.idproveedor = idproveedor;
        this.nombre = nombre;
        this.stock = stock;
    }

    public int getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(int idproducto) {
        this.idproducto = idproducto;
    }

    public int getIdproveedor() {
        return idproveedor;
    }

    public void setIdproveedor(int idproveedor) {
        this.idproveedor = idproveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}

package modelo;

/**
 *
 * @author ep712
 */
public class Clientes {

    private int idcliente;
    private String nombre;
    private String correo;
    private String telefono;

    public Clientes() {
    }

    public Clientes(int idcliente, String nombre, String correo, String telefono) {
        this.idcliente = idcliente;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
    }

    public int getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}

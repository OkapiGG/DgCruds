package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {

    private static conexion instancia;
    private Connection conexion;

    private final String URL = "jdbc:postgresql://localhost:5432/cruds5h";
    private final String USUARIO = "postgres";
    private final String CONTRASENA = "ema24";    

    private conexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Error al cargar el driver de PostgreSQL", ex);
        }
    }

    // Obtener la conexión
    public Connection getConexion() {
        return conexion;
    }

    // Singleton
    public static conexion getInstancia() throws SQLException {
        if (instancia == null || instancia.getConexion().isClosed()) {
            instancia = new conexion();
        }
        return instancia;
    }

    // Cerrar conexión
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }

//    public static void main(String[] args) {
//        
//        try {            
//            conexion objConexion = getInstancia();
//            if (objConexion.getConexion() != null && !objConexion.getConexion().isClosed()) {
//                System.out.println("¡Conexión exitosa!");
//            }
//            objConexion.cerrarConexion();
//        } catch (SQLException e) {
//            System.err.println("Error al conectar: " + e.getMessage());
//        }
//    }
}




public class Configuracion {

	
	
	public static String rutaBaseDeDatos = "C:\\SIRIA\\BaseDeDatos.csv";
    public static String rutaArchivos = "C:\\SIRIA";
    public static String rutaInventario = "C:\\SIRIA\\INVENTARIO\\Consulta_Inventario_id_dom.csv";
    // Agrega aquí todas las variables de configuración que necesites

    // Métodos para acceder y modificar las variables de configuración
    public static String getCampoAdicional1(String campo) {
        return campo;
    }

    public static void setCampoAdicional1(String valor) {
        rutaBaseDeDatos = valor;
    }

    // Agrega métodos get/set para las demás variables de configuración
}

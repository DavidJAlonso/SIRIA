


public class Configuracion {

	
	
	public static String rutaBaseDeDatos = "C:\\SIRIA\\BaseDeDatos.csv";
    public static String rutaArchivos = "C:\\SIRIA";
    public static String rutaInventario = "C:\\SIRIA\\INVENTARIO\\Consulta_Inventario_id_dom.csv";
    // Agrega aqu� todas las variables de configuraci�n que necesites

    // M�todos para acceder y modificar las variables de configuraci�n
    public static String getCampoAdicional1(String campo) {
        return campo;
    }

    public static void setCampoAdicional1(String valor) {
        rutaBaseDeDatos = valor;
    }

    // Agrega m�todos get/set para las dem�s variables de configuraci�n
}

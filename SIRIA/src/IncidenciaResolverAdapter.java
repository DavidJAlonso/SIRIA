import java.io.File;

public class IncidenciaResolverAdapter extends AbstractIncidenciaAPI {
    @Override
    public boolean cerrarIncidencia(String incidencia, String etiquetaCierre, String comentario, String cola) {
        // Implementaci�n espec�fica de cierre de incidencia utilizando las APIs o servicios correspondientes
        
        // L�gica de cierre de incidencia
        
        switch (etiquetaCierre) {
        	case "#INFO":
                System.out.println("Se cerr� la incidencia aportando la informaci�n solicitada");
                break;
        		
            case "c":
                System.out.println("Se ");
                break;
            case "Etiqueta2":
                System.out.println("Se cerr� la incidencia con la etiqueta 2.");
                break;
            case "Etiqueta3":
                System.out.println("Se cerr� la incidencia con la etiqueta 3.");
                break;
            default:
                System.out.println("Etiqueta de cierre desconocida: " + etiquetaCierre);
                break;
        }
        
        // Retorna verdadero o falso dependiendo del resultado del cierre de incidencia
        return true; // o false
    }

    @Override
    public File[] leerIncidencia(String rutaArchivos) {
        // Implementaci�n espec�fica de lectura de incidencia utilizando la ruta de archivos proporcionada
        // Puedes implementar la l�gica necesaria para leer los archivos de incidencia y devolver un array de File
        File[] files = new File(rutaArchivos).listFiles();
        return files;
    }
}

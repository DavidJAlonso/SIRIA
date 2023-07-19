import java.io.File;

public interface IncidenciasAPI {
    boolean cerrarIncidencia(String incidencia, String etiquetaCierre, String comentario, String cola);
    File[] leerIncidencia(String rutaArchivos);
}

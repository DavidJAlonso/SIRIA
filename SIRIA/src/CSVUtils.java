import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.opencsv.ICSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVUtils {
    // Método para obtener un array de una línea específica en un archivo CSV
    public static String[] obtenerArrayDesdeLineaCSV(String csvArchivo, int numeroLinea) throws CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(csvArchivo))) {
            String[] linea;
            int contador = 0;

            while ((linea = reader.readNext()) != null) {
                contador++;
                if (contador == numeroLinea) {
                    //System.out.println("Array obtenido: " + Arrays.toString(linea));
                    return linea;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Método para insertar un array en un archivo CSV
    public static void insertarArrayEnCSV(String csvArchivo, String[] array) {
        try (CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(csvArchivo, true))
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .build()) {
            writer.writeNext(array);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener el valor de un campo específico en base a la búsqueda de otro campo en un archivo CSV
    public static String obtenerValorCampo(String csvArchivo, String campoBusqueda, String campoObjetivo, String valorBusqueda) throws CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(csvArchivo))) {
            String[] cabecera = reader.readNext();
            int indiceCampoBusqueda = -1;
            int indiceCampoObjetivo = -1;
            //System.out.println("Cabecera del CSV: " + Arrays.toString(cabecera));

            for (int i = 0; i < cabecera.length; i++) {
                if (cabecera[i].equals(campoBusqueda)) {
                    indiceCampoBusqueda = i;
                }
                if (cabecera[i].equals(campoObjetivo)) {
                    indiceCampoObjetivo = i;
                }
            }

            if (indiceCampoBusqueda == -1 || indiceCampoObjetivo == -1) {
                System.out.println("No se encontraron los campos especificados en la cabecera del CSV.");
                return null;
            }

            String[] linea;
            while ((linea = reader.readNext()) != null) {
                if (linea.length > indiceCampoBusqueda && linea[indiceCampoBusqueda].equals(valorBusqueda)) {
                    if (linea.length > indiceCampoObjetivo) {
                        //System.out.println("Valor del campo objetivo encontrado: " + linea[indiceCampoObjetivo]);
                        return linea[indiceCampoObjetivo];
                    } else {
                        System.out.println("El campo objetivo no existe en la fila encontrada.");
                        return null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("No se encontró ninguna fila que coincida con el valor de búsqueda en el campo especificado.");
        return null;
    }

    // Método para obtener el índice de un campo en la cabecera de un archivo CSV
    public static int obtenerIndiceCampo(String csvArchivo, String campo) throws CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(csvArchivo))) {
            String[] cabecera = reader.readNext();
            if (cabecera != null) {
                for (int i = 0; i < cabecera.length; i++) {
                    if (cabecera[i].equals(campo)) {
                        return i;
                    }
                }
            } else {
                System.out.println("La cabecera del archivo CSV está vacía.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Método para obtener todas las filas de un archivo CSV
    public static List<String[]> obtenerTodasLasFilas(String csvArchivo) throws CsvValidationException {
        List<String[]> filas = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvArchivo))) {
            String[] linea;
            while ((linea = reader.readNext()) != null) {
                filas.add(linea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filas;
    }

    // Método para actualizar el valor de un campo en un archivo CSV
    public static void actualizarValorCampo(String csvArchivo, String campoBusqueda, String valorBusqueda,
            String campoObjetivo, String nuevoValor) throws CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(csvArchivo))) {
            List<String[]> filas = new ArrayList<>();
            String[] cabecera = reader.readNext();
            int indiceCampoBusqueda = -1;
            int indiceCampoObjetivo = -1;
            //System.out.println("Campo de búsqueda: " + campoBusqueda);
            //System.out.println("Campo objetivo: " + campoObjetivo);
            //System.out.println("Cabecera del CSV: " + Arrays.toString(cabecera));
            filas.add(cabecera); // Agregar la cabecera a la lista de filas

            for (int i = 0; i < cabecera.length; i++) {
                if (cabecera[i].trim().equals(campoBusqueda.trim())) {
                    indiceCampoBusqueda = i;
                }
                if (cabecera[i].trim().equals(campoObjetivo.trim())) {
                    indiceCampoObjetivo = i;
                }
            }

            if (indiceCampoBusqueda == -1 || indiceCampoObjetivo == -1) {
                System.out.println("No se encontraron los campos especificados en la cabecera del CSV para actualizar.");
                return;
            }

            String[] linea;
            while ((linea = reader.readNext()) != null) {
                if (linea.length > indiceCampoBusqueda && linea[indiceCampoBusqueda].equals(valorBusqueda)) {
                    if (linea.length > indiceCampoObjetivo) {
                        String[] nuevaLinea = Arrays.copyOf(linea, linea.length); // Crear una copia de la línea
                        nuevaLinea[indiceCampoObjetivo] = nuevoValor; // Actualizar el valor del campo objetivo en la nueva línea
                        filas.add(nuevaLinea); // Agregar la nueva línea a la lista de filas
                        //System.out.println("Valor del campo objetivo actualizado.");
                    } else {
                        System.out.println("El campo objetivo no existe en la fila encontrada.");
                    }
                } else {
                    filas.add(linea); // Agregar la línea original a la lista de filas sin cambios
                }
            }

            escribirFilasEnArchivo(csvArchivo, filas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para escribir las filas en un archivo CSV
    public static void escribirFilasEnArchivo(String csvArchivo, List<String[]> filas) {
        try (FileWriter writer = new FileWriter(csvArchivo)) {
            for (String[] fila : filas) {
                writer.write(String.join(",", fila));
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int obtenerNumeroLinea(String csvArchivo, int indiceCampo, String valorObjetivo) {
        try (CSVReader reader = new CSVReader(new FileReader(csvArchivo))) {
            String[] linea;
            int contador = 1; // Inicializar el contador en 1

            while ((linea = reader.readNext()) != null) {
                if (linea.length > indiceCampo && linea[indiceCampo].equals(valorObjetivo)) {
                    //System.out.println("Línea obtenida del archivo CSV: " + contador);
                    return contador;
                }
                contador++;
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        System.out.println("No se encontró una línea que coincida con el campo especificado.");
        return -1;
    }
    public static void imprimirCSVCompleto(String csvArchivo) {
        try (CSVReader reader = new CSVReader(new FileReader(csvArchivo))) {
            String[] cabecera = reader.readNext();
            String[] linea;

            while ((linea = reader.readNext()) != null) {
                for (int i = 0; i < cabecera.length; i++) {
                    System.out.println(cabecera[i] + ": " + linea[i]);
                }
                System.out.println();
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
    public static String obtenerValorCampoLineaColumna(String csvArchivo, int fila, int columna) throws IOException, CsvException {
        try (CSVReader csvReader = new CSVReader(new FileReader(csvArchivo))) {
            List<String[]> filas = csvReader.readAll();

            if (fila >= 0 && fila < filas.size()) {
                String[] filaSeleccionada = filas.get(fila);

                if (columna >= 0 && columna < filaSeleccionada.length) {
                    return filaSeleccionada[columna];
                } else {
                    throw new IllegalArgumentException("La columna especificada está fuera de rango.");
                }
            } else {
                throw new IllegalArgumentException("La fila especificada está fuera de rango.");
            }
        }
    }
    public static String[] obtenerArrayDesdeCodigoIncidencia(String csvArchivo, String codigoIncidencia) throws CsvValidationException {
        int indiceCampoCodigo = obtenerIndiceCampo(csvArchivo, "INCIDENCIA");
        int numeroLinea = obtenerNumeroLinea(csvArchivo, indiceCampoCodigo, codigoIncidencia);
        return obtenerArrayDesdeLineaCSV(csvArchivo, numeroLinea);
    }
    public static void imprimirEncabezadosCSV(String csvArchivo) {
        try (CSVReader reader = new CSVReader(new FileReader(csvArchivo))) {
            String[] encabezados = reader.readNext();
            if (encabezados != null) {
                System.out.println(Arrays.toString(encabezados));
            } else {
                System.out.println("No se encontraron encabezados en el archivo CSV.");
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
 
}

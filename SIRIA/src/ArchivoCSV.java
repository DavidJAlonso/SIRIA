import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoCSV {

    private String rutaArchivo;
    private List<List<String>> datos;

    public ArchivoCSV(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.datos = new ArrayList<>();
    }

    public void leerArchivoCSV() {
        BufferedReader br = null;
        String linea = "";
        String separador = ",";

        try {
            br = new BufferedReader(new FileReader(rutaArchivo));
            while ((linea = br.readLine()) != null) {
                String[] fila = linea.split(separador);
                List<String> filaLista = new ArrayList<>();
                for (String valor : fila) {
                    filaLista.add(valor);
                }
                datos.add(filaLista);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void modificarRegistro(int indiceFila, int indiceColumna, String nuevoValor) {
        if (indiceFila >= 0 && indiceFila < datos.size()) {
            List<String> fila = datos.get(indiceFila);
            if (indiceColumna >= 0 && indiceColumna < fila.size()) {
                fila.set(indiceColumna, nuevoValor);
            }
        }
    }

    public void guardarArchivoCSV() {
        BufferedWriter bw = null;
        String separador = ",";

        try {
            bw = new BufferedWriter(new FileWriter(rutaArchivo));
            for (List<String> fila : datos) {
                bw.write(String.join(separador, fila));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getCampo(int indiceFila, int indiceColumna) {
        if (indiceFila >= 0 && indiceFila < datos.size()) {
            List<String> fila = datos.get(indiceFila);
            if (indiceColumna >= 0 && indiceColumna < fila.size()) {
                return fila.get(indiceColumna);
            }
        }
        return null;
    }

    public boolean comprobarCamposCompletos() {
        if (!datos.isEmpty()) {
            List<String> campos = datos.get(0); // Obtener la primera fila (encabezados)
            for (int i = 0; i < 8; i++) {
                String campo = campos.get(i);
                if (campo == null || campo.isEmpty()) {
                    return false; // Si alguno de los campos está vacío, retorna false
                }
            }
            return true; // Si todos los campos están completos, retorna true
        }
        return false; // Si no hay datos, retorna false
    }

    public void imprimirArchivoCSV() {
        int[] maxLengths = obtenerLongitudesMaximas();
        for (List<String> fila : datos) {
            for (int i = 0; i < fila.size(); i++) {
                String campo = fila.get(i);
                System.out.format("%-" + (maxLengths[i] + 1) + "s", campo);
            }
            System.out.println();
        }
    }

    public List<String> getFila(int index) {
        if (index >= 0 && index < datos.size()) {
            return datos.get(index);
        }
        return null;
    }

    private int[] obtenerLongitudesMaximas() {
        int numColumnas = datos.get(0).size();
        int[] maxLengths = new int[numColumnas];
        for (List<String> fila : datos) {
            for (int i = 0; i < numColumnas; i++) {
                String campo = fila.get(i);
                if (campo.length() > maxLengths[i]) {
                    maxLengths[i] = campo.length();
                }
            }
        }
        return maxLengths;
    }
    public List<String> getArrayCompletoFila(String entrada, String campo) {
        List<String> filaEncontrada = null;
        int indiceCampo = -1;

        // Buscar el índice del campo en la segunda fila (datos)
        if (datos.size() > 1) {
            List<String> encabezados = datos.get(0);
            indiceCampo = encabezados.indexOf(campo);
        }

        // Buscar la fila que coincide con la entrada y el campo
        if (indiceCampo >= 0) {
            for (int i = 1; i < datos.size(); i++) {
                List<String> fila = datos.get(i);
                if (fila.get(indiceCampo).equals(entrada)) {
                    filaEncontrada = fila;
                    break;
                }
            }
        }

        // Imprimir los valores del array completo de la fila
        if (filaEncontrada != null) {
            System.out.println("Valores de la fila encontrada:");
            for (String valor : filaEncontrada) {
                System.out.println(valor);
            }
        } else {
            System.out.println("No se encontró ninguna fila que coincida con la entrada y el campo especificados.");
        }

        return filaEncontrada;
    }



    public boolean hayCamposVacios() {
        for (List<String> fila : datos) {
            for (int i = 0; i < 8; i++) {
                if (i >= fila.size() || fila.get(i).trim().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

   }
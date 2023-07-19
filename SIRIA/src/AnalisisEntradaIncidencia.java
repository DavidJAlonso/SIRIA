import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.opencsv.ICSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.opencsv.exceptions.CsvValidationException;



public class AnalisisEntradaIncidencia {
    private String[] incidencias;
    private String rutaArchivos = Configuracion.rutaArchivos; // Ruta de los archivos de incidencia
    public String csvArchivo = Configuracion.rutaBaseDeDatos;
    public AnalisisEntradaIncidencia(String codigoIncidencia) {
        this.incidencias = obtenerIncidenciaPorCodigo(codigoIncidencia);
    }

    private String[] obtenerIncidenciaPorCodigo(String codigoIncidencia) {
        String csvArchivo = rutaArchivos + "\\" + "BaseDeDatos.csv";
        try {
            return CSVUtils.obtenerArrayDesdeCodigoIncidencia(csvArchivo, codigoIncidencia);
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void analizar() throws CsvValidationException {
        if (incidencias == null) {
            System.out.println("No se encontró la incidencia con el código proporcionado.");
            return;
        }

        String tipologiaEntrada = obtenerTipologiaEntrada();
    
        System.out.println("Tipología de entrada" + tipologiaEntrada);
        switch (tipologiaEntrada) {
        case "FTTH_INS_PETICION INFORMACION":
        	
        	System.out.println("Se ha solicitado Información del cliente, estos son los datos que figuran en sistemas");
        	CSVUtils.imprimirEncabezadosCSV(csvArchivo);
        	System.out.println(Arrays.toString(incidencias));
        	CSVUtils.actualizarValorCampo(csvArchivo, "INCIDENCIA", incidencias[1], "TIPO DE RESOLUCION", "#INFO");
            break;
                     
        case "FTTH_INS_CAMBIO DE CTO":
            // Realizar acciones correspondientes a FTTH_INS_CAMBIO DE CTO
            break;
        case "FTTH_INS_CAMBIO DE PUERTO":
            // Realizar acciones correspondientes a FTTH_INS_CAMBIO DE PUERTO
            break;
        case "FTTH_INS_CD SATURADA":
            // Realizar acciones correspondientes a FTTH_INS_CD SATURADA
            break;
        case "FTTH_INS_CD SIN POTENCIA":
            // Realizar acciones correspondientes a FTTH_INS_CD SIN POTENCIA
            break;
        case "FTTH_INS_CTO ILOCALIZABLE":
            // Realizar acciones correspondientes a FTTH_INS_CTO ILOCALIZABLE
            break;

        case "FTTH_INS_CTO INACCESIBLE":
            // Realizar acciones correspondientes a FTTH_INS_CTO INACCESIBL
            break;

        case "FTTH_INS_CTO SATURADA":
            // Realizar acciones correspondientes a FTTH_INS_CTO SATURADA
            break;
        case "FTTH_INS_CTO SIN POTENCIA":
            // Realizar acciones correspondientes a FTTH_INS_CTO SIN POTENCIA
            break;

        case "FTTH_INS_CTO_ILOCALIZABLE":
            // Realizar acciones correspondientes a FTTH_INS_CTO_ILOCALIZABLE
            break;
        case "FTTH_INS_CTO_MAL_ESTADO":
            // Realizar acciones correspondientes a FTTH_INS_CTO_MAL_ESTADO
            break;
        case "FTTH_INS_CTO_SIN POTENCIA":
            // Realizar acciones correspondientes a FTTH_INS_CTO_SIN POTENCIA
            break;
        case "FTTH_INS_DOMICILIO SIN COBERTURA":
            // Realizar acciones correspondientes a FTTH_INS_DOMICILIO SIN COBERTURA
            break;

        case "FTTH_INS_NOK_CORTES":
            // Realizar acciones correspondientes a FTTH_INS_NOK_CORTES
            break;
        case "FTTH_INS_NOK_NO LEVANTA IP":
            // Realizar acciones correspondientes a FTTH_INS_NOK_NO LEVANTA IP
            break;
        case "FTTH_INS_NOK_NO NAVEGA":
            // Realizar acciones correspondientes a FTTH_INS_NOK_NO NAVEGA
            break;

        case "FTTH_INS_PUERTO OCUPADO":
            // Realizar acciones correspondientes a FTTH_INS_PUERTO OCUPADO
            break;
        case "FTTH_INS_PUERTO SIN SINCRONISMO":
        	
        	
       	
        	
        	
            // Realizar acciones correspondientes a FTTH_INS_PUERTO SIN SINCRONISMO
            break;
            
          

        case "FTTH_INS_VERTICAL OBSTRUIDA":
            // Realizar acciones correspondientes a FTTH_INS_VERTICAL OBSTRUIDA
            break;

        default:
            System.out.println("Tipología de entrada desconocida.");
            break;
        }
    }

    private String obtenerTipologiaEntrada() {
        if (incidencias.length >= 14) {
            return incidencias[0];
        } else {
            System.out.println("El campo de tipología de entrada no está presente en la incidencia.");
            return null;
        }
    }
}


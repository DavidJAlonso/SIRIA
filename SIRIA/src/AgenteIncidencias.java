import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.ArrayList;

import java.nio.file.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class AgenteIncidencias extends Agent {



    private long intervaloComprobacion = 6000; // Intervalo de comprobación en milisegundos
    private String rutaArchivo; // Variable para almacenar la ruta del archivo actual
    private java.util.ArrayList<String> archivosErroneos = new java.util.ArrayList<>(); // Lista para almacenar los nombres de los archivos erróneos
    boolean archivoErroneoEncontrado = false;

    protected void setup() {
        System.out.println("Agente Incidencias iniciado. Esperando archivos de incidencia...");


        // Comportamiento para comprobar archivos de incidencia cada minuto
        
        addBehaviour(new TickerBehaviour(this, intervaloComprobacion) {
        	
            protected void onTick() {
            	
                // Llamada a la API para traer las incidencias
            	IncidenciasAPI apiFiles = new IncidenciaResolverAdapter();
                File[] files = apiFiles.leerIncidencia(Configuracion.rutaArchivos);
                
                // Procesado de la lista de incidencias
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        // Verificar si el archivo está en la lista de archivos erróneos
                        if (archivosErroneos.contains(file.getName())) {
                            //System.out.println("Archivo de incidencia erróneo encontrado: " + file.getName() + ". Saltando archivo...");
                            continue; // Saltar al siguiente archivo sin procesarlo
                        }
                        if (file.getName().startsWith("Incidencia") && file.getName().endsWith(".csv")) {
                            System.out.println("Archivo de incidencia encontrado: " + file.getName());
                            String codigoIncidencia = null;
                            String Iua = null;
                            String TipoIncidencia = null;
                            // Guardar la ruta del archivo actual
                            
                            rutaArchivo = file.getAbsolutePath();
                            
                            // System.out.println(rutaArchivo);

                            // Crear objeto CSVReader y leer el archivo de incidencia
                            try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
                               
                            	List<String[]> rows = csvReader.readAll();
                                /*for (String[] row : rows) {
                                    // Procesar cada fila del archivo CSV
                                    for (String cell : row) {
                                        System.out.print(cell + "\t");
                                    }
                                    System.out.println("");
                                }*/
                                try {
                                    codigoIncidencia = rows.get(1)[1];
                                    TipoIncidencia = rows.get(1)[0];
                                    Iua = rows.get(1)[2];

                                    // Resto del código que utiliza los valores

                                } catch (IndexOutOfBoundsException e) {
                                    // El archivo CSV no tiene suficientes columnas
                                    if (!archivoErroneoEncontrado) {
                                        archivosErroneos.add(file.getName());
                                        //e.printStackTrace();
                                        System.out.println("Archivo de incidencia corrupto o erróneo: " + file.getName());
                                        archivoErroneoEncontrado = true;
                                    }
                                    continue;
                                }

                                
                                // Comprobar el encabezado y los primeros 4 campos para aceptar la incidencia
                                
                                boolean camposCompletos = hayCamposVacios(file,0, 15);
                                boolean hayVaciosInicio = hayCamposVacios(file,1, 4);
                                //System.out.println("El valor booleano de camposCompletos es: " + camposCompletos);
                                //System.out.println("El valor booleano de hayVaciosInicio es: " + hayVaciosInicio);

                                if (camposCompletos || hayVaciosInicio) {
                                	
                                    // Llamar al servicio CierreIncidencia
                                	String[] arrayDesdeLineaCSV = CSVUtils.obtenerArrayDesdeLineaCSV(rutaArchivo, 2);
                                    CSVUtils.insertarArrayEnCSV(Configuracion.rutaBaseDeDatos, arrayDesdeLineaCSV);
                                    SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
                                    sequentialBehaviour.addSubBehaviour(new CierreIncidencia(myAgent,codigoIncidencia));
                                    sequentialBehaviour.addSubBehaviour(new EsperarRespuestaCierre());
                                    addBehaviour(sequentialBehaviour);

                                    //System.out.println("El valor booleano es: " + camposCompletos);
                                    //System.out.println("El valor booleano es: " + hayVaciosInicio);
                                    // Pasar al siguiente archivo de la lista
                                    continue;
                                }

                                
                                boolean hayVacios = hayCamposVacios(file, 1, 15);
                                if (!hayVacios) {
                                    System.out.println( "No hay campos vacíos en los primeros 8 campos del archivo CSV.");
                                } else {
                                    System.out.println("Hay campos vacíos en los primeros 8 campos del archivo CSV.");
                                }
                               
                                
 
                                if (!hayVacios) {
                                	// Obtener el código de la incidencia
                               
                                	System.out.println("He entrado por aqui");
                                    // Llamar al servicio del Agente Analítico para el análisis de la incidencia
                                	String[] arrayDesdeLineaCSV = CSVUtils.obtenerArrayDesdeLineaCSV(rutaArchivo, 2);
                                    CSVUtils.insertarArrayEnCSV(Configuracion.rutaBaseDeDatos, arrayDesdeLineaCSV);
                                    SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
                                    sequentialBehaviour.addSubBehaviour(new LlamadaServicio(myAgent, "AnalisisIncidencia", codigoIncidencia ,"Hola" ,"Hola" ));
                                    sequentialBehaviour.addSubBehaviour(new EsperarRespuestaAnalitico());
                                    // Llamar al servicio CierreIncidencia

                                    sequentialBehaviour.addSubBehaviour(new CierreIncidencia(myAgent,codigoIncidencia));
                                    sequentialBehaviour.addSubBehaviour(new EsperarRespuestaCierre());
                                    addBehaviour(sequentialBehaviour);
                                } else {
                                    // Llamar al servicio RellenarCampos del Agente Inventario
                                	//System.out.println("He entrado por else");
                                	String[] arrayDesdeLineaCSV = CSVUtils.obtenerArrayDesdeLineaCSV(rutaArchivo, 2);
                                    CSVUtils.insertarArrayEnCSV(Configuracion.rutaBaseDeDatos, arrayDesdeLineaCSV);
                                    SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
                                    sequentialBehaviour.addSubBehaviour(new LlamadaServicio(myAgent, "RellenarCampos",codigoIncidencia, Iua, rutaArchivo));
                                    sequentialBehaviour.addSubBehaviour(new EsperarRespuestaInventario());
                                    sequentialBehaviour.addSubBehaviour(new LlamadaServicio(myAgent, "AnalisisIncidencia",codigoIncidencia,rutaArchivo,TipoIncidencia));
                                    sequentialBehaviour.addSubBehaviour(new EsperarRespuestaAnalitico());
                                    // Llamar al servicio CierreIncidencia

                                    sequentialBehaviour.addSubBehaviour(new CierreIncidencia(myAgent,codigoIncidencia));
                                    sequentialBehaviour.addSubBehaviour(new EsperarRespuestaCierre());
                                    addBehaviour(sequentialBehaviour);
                                }

                            } catch (IOException | CsvException e) {
                                if (!archivoErroneoEncontrado) {
                                    archivosErroneos.add(file.getName());
                                    //e.printStackTrace();
                                    System.out.println("Archivo de incidencia corrupto o erróneo: " + file.getName());
                                    archivoErroneoEncontrado = true;
                                }
                                continue;
                            }
                        }
                    }	
                }
            }
        });
    }
    
    // Comprueba los 8 campos de encabezado para comprobar si están vacíos, en cuyo caso rechaza la incidenia
  private boolean comprobarCamposCompletos(File file) {
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            List<String[]> rows = csvReader.readAll();
            String[] headers = rows.get(0); // Encabezados
            for (int i = 0; i < 8; i++) {
                String campo = headers[i];
                if (campo == null || campo.isEmpty()) {
                    System.out.println("El campo " + (i + 1) + " está vacío. Se requiere información adicional.");
                    return true;
                }
            }
            return false;
        } catch (IOException | CsvException e) {
            System.out.println("Hay Campos vacios");
            e.printStackTrace();
            return false;
        }
    }




    //Comprueba si existen determinados campos vacios
  private boolean hayCamposVacios(File file, int fila, int campos) {
	    try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
	        List<String[]> rows = csvReader.readAll();
	        String[] rowData = rows.get(fila);
            String[] firstRow = rows.get(0);
            String primerCampo = firstRow[0];
            primerCampo = primerCampo.replace("ï»¿", "");
            firstRow[0] = primerCampo;
	        
	        if (rowData.length < campos) {
	            System.out.println("El número de campos es menor que el solicitado.");
	            return true;
	        }
	        
	        for (int j = 0; j < campos; j++) {
	            String campo = rowData[j];
	            if (campo == null || campo.isEmpty()) {
	                //System.out.println("Hay campos vacíos en la fila.");
	                return true;
	            }
	        }
	        
	        return false;
	    } catch (IOException | CsvException e) {
	        e.printStackTrace();
	        return false;
	    }
	}




  private class LlamadaServicio extends OneShotBehaviour {
	    private String servicio;
	    private String campo;
	    private String campoAdicional1;
	    private String campoAdicional2;
	    
	    LlamadaServicio(Agent agent, String servicio, String campo, String campoAdicional1, String campoAdicional2) {
	        super(agent);
	        this.servicio = servicio;
	        this.campo = campo;
	        this.campoAdicional1 = campoAdicional1;
	        this.campoAdicional2 = campoAdicional2;
	    }

	    public void action() {
	        // Llamar al servicio del agente especificado
	        DFAgentDescription template = new DFAgentDescription();
	        ServiceDescription sd = new ServiceDescription();
	        sd.setType(servicio);
	        template.addServices(sd);

	        try {
	            DFAgentDescription[] result = DFService.search(myAgent, template);
	            if (result.length > 0) {
	                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
	                msg.setOntology(servicio);
	                msg.setContent("Solicitud de servicio");
	                msg.addReceiver(result[0].getName());

	                // Adjuntar la ruta del archivo como contenido del mensaje
	                msg.setContent(rutaArchivo);

	                // Adjuntar los campos adicionales utilizando el método addUserDefinedParameter()
	                msg.addUserDefinedParameter("campo", campo);
	                msg.addUserDefinedParameter("CampoAdicional1", campoAdicional1);
	                msg.addUserDefinedParameter("CampoAdicional2", campoAdicional2);


	                send(msg);

	                System.out.println("Llamada al servicio " + servicio);
	            } else {
	                System.out.println("No se encontró un agente que ofrezca el servicio " + servicio);
	            }
	        } catch (FIPAException fe) {
	            fe.printStackTrace();
	        }
	    }
	}

    private class EsperarRespuestaInventario extends OneShotBehaviour {
        public void action() {
            // Esperar la respuesta del Agente Inventario
        	MessageTemplate mt = MessageTemplate.MatchOntology("INFORM");
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println("Respuesta del Agente Inventario: " + msg.getContent());
            } else {
                block();
            }
        }
    }

    private class EsperarRespuestaAnalitico extends OneShotBehaviour {
        public void action() {
            // Esperar la respuesta del Agente Analítico
        	MessageTemplate mt = MessageTemplate.MatchOntology("INFORM");
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println("Respuesta del Agente Analítico: " + msg.getContent());
            } else {
                block();
            }
        }
    }

    private class CierreIncidencia extends OneShotBehaviour {
        private String codigoIncidencia;
        private String etiquetaCierre;

        CierreIncidencia(Agent agent, String codigoIncidencia) {
            super(agent);
            this.codigoIncidencia = codigoIncidencia;



        }

        public void action() {
            // Obtener la etiqueta de cierre de la incidencia
            String csvArchivo = Configuracion.rutaBaseDeDatos;
            String campoBusqueda = "INCIDENCIA";
            String campoObjetivo = "TIPO DE RESOLUCION";
            String valorBusqueda = codigoIncidencia;

            try {
                String valorCampo = CSVUtils.obtenerValorCampo(csvArchivo, campoBusqueda, campoObjetivo, valorBusqueda);
                if (valorCampo != null) {
                    System.out.println("Etiqueta de Cierre: " + valorCampo);
                    this.etiquetaCierre = valorCampo;
                } else {
                    System.out.println("No se encontró ningún valor para la búsqueda especificada.");
                }
            } catch (CsvValidationException e) {
                e.printStackTrace();
            }
            IncidenciasAPI api = new IncidenciaResolverAdapter();
            boolean resultado = api.cerrarIncidencia(codigoIncidencia, etiquetaCierre, "comentario", "cola");
            System.out.println("Se ha resuelto la incidencia: " + codigoIncidencia);
            //System.out.println("Cierre de la incidencia");

            File file = new File(rutaArchivo);
            if (file.exists()) {
                // Crear la carpeta "INCIDENCIAS CERRADAS" si no existe
                Path carpetaIncidenciasCerradas = Paths.get(Configuracion.rutaArchivos, "INCIDENCIAS CERRADAS");
                if (!Files.exists(carpetaIncidenciasCerradas)) {
                    try {
                        Files.createDirectory(carpetaIncidenciasCerradas);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Definir la ruta de destino para el archivo de incidencia
                Path rutaDestino = Paths.get(carpetaIncidenciasCerradas.toString(), file.getName());

                try {
                    // Realizar el corte y pegado del archivo
                    Files.move(file.toPath(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Archivo de incidencia movido a la carpeta 'INCIDENCIAS CERRADAS': " + rutaDestino.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("El archivo de incidencia no existe: " + file.getName());
            }
        }
    }

    private class EsperarRespuestaCierre extends OneShotBehaviour {
        public void action() {
        	MessageTemplate mt = MessageTemplate.MatchOntology("INFORM");
            ACLMessage msg = receive(mt);
            if (msg != null) {
                System.out.println("Respuesta recibida del Agente Incidencias: " + msg.getContent());
                // Procesar la respuesta recibida
                // ...
            } else {
                block();
            }
        }
    }

    protected void takeDown() {
        // Darse de baja del servicio de recepción de archivos de incidencia
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Agente Incidencias finalizado.");
    }
}

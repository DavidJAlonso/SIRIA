import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class AgenteInventario extends Agent {
    protected void setup() {
        // Registrar servicios de RellenarCampos y SolicitudInfo
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        // Servicio RellenarCampos
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType("RellenarCampos");
        sd1.setName("RellenarCampos");
        dfd.addServices(sd1);

        // Servicio SolicitudInfo	
        ServiceDescription sd2 = new ServiceDescription();
        sd2.setType("SolicitudInfo");
        sd2.setName("SolicitudInfo");
        dfd.addServices(sd2);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = receive(mt);
                if (msg != null) {
                    String content = msg.getContent();
                    System.out.println("Solicitud recibida por Inventario: " + content);

                    // Obtener los campos adicionales
                    String campo = msg.getUserDefinedParameter("campo");
                    String campoAdicional1 = msg.getUserDefinedParameter("CampoAdicional1");
                    String campoAdicional2 = msg.getUserDefinedParameter("CampoAdicional2");
                    

                    if (msg.getOntology().equals("RellenarCampos")) {
                        // Lógica para el servicio RellenarCampos
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);

                        // Agregar los campos adicionales al contenido del mensaje
                        reply.setContent(reply.getContent() + "\ncampo: " + campo + "\nCampoAdicional1: " + campoAdicional1 + "\nCampoAdicional2: " + campoAdicional2);

                        // Obtener el array del campo IUA del archivo
                        //String[] arrayIUA = obtenerArrayIUA(campoAdicional1);
                        String valor1 = null;
                        //CSVUtils.imprimirCSVCompleto("C:\\SIRIA\\INVENTARIO\\Consulta_Inventario_id_dom.csv");
                        //System.out.println("Campo Adicionallllll: " + campoAdicional1);
                        //System.out.println("Campo Adicionallllll: " + campoAdicional2);
                        procesarLlamadas(Configuracion.rutaInventario, campo, campoAdicional1,Configuracion.rutaBaseDeDatos );

                        send(reply);
                        
                        //System.out.println("Inventario ha respondido a incidencias " + campoAdicional1  + campoAdicional2);

                    } else if (msg.getOntology().equals("SolicitudInfo")) {
                        // Lógica para el servicio SolicitudInfo
                        boolean respuesta = obtenerRespuestaAleatoria();

                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(Boolean.toString(respuesta));

                        // Agregar los campos adicionales al contenido del mensaje
                        reply.setContent(reply.getContent() + "\nCampoAdicional1: " + campoAdicional1 + "\nCampoAdicional2: " + campoAdicional2);

                        send(reply);
                    }
                } else {
                    block();
                }
            }
        });
    }

    private boolean obtenerRespuestaAleatoria() {
        // Generar respuesta aleatoria (verdadero o falso)
        Random random = new Random();
        return random.nextBoolean();
    }

    private String[] obtenerArrayIUA(String campo) {
        String csvArchivo = Configuracion.rutaInventario;
        int indiceCampoIUA = 16; // Índice de la columna IUA en el archivo CSV
        
        try {
            int numeroLinea = 2;//obtenerNumeroLinea(csvArchivo, indiceCampoIUA, campo);

            if (numeroLinea != -1) {
                String[] array = CSVUtils.obtenerArrayDesdeLineaCSV(csvArchivo, numeroLinea);
                //imprimirLinea(array);
                return array;
            } else {
                System.out.println("No se encontró una línea que coincida con el campo especificado.");
            }
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    private void procesarLlamadas(String archivoCSV, String valorIncidencia, String campoBusqueda, String archivoCSVObjetivo) {
        try {
            String valorPuerto = CSVUtils.obtenerValorCampo(archivoCSV, "IUA", "PUERTO_CTO", campoBusqueda);

            CSVUtils.actualizarValorCampo(archivoCSVObjetivo, "INCIDENCIA", valorIncidencia, "PUERTO", valorPuerto);
            
            String valorOLT = CSVUtils.obtenerValorCampo(archivoCSV, "IUA", "OLT", campoBusqueda);

            CSVUtils.actualizarValorCampo(archivoCSVObjetivo, "INCIDENCIA", valorIncidencia, "OLT", valorOLT);
            
            String valorTarjeta = CSVUtils.obtenerValorCampo(archivoCSV, "IUA", "TARJETA", campoBusqueda);

            CSVUtils.actualizarValorCampo(archivoCSVObjetivo, "INCIDENCIA", valorIncidencia, "OLT_TARJETA", valorTarjeta);
            
            String valorOLTPuerto = CSVUtils.obtenerValorCampo(archivoCSV, "IUA", "PUERTO", campoBusqueda);

            CSVUtils.actualizarValorCampo(archivoCSVObjetivo, "INCIDENCIA", valorIncidencia, "OLT_PUERTO", valorOLTPuerto);
            
            
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
    }
    private void imprimirLinea(String[] linea) {
        System.out.println("Línea obtenida del archivo CSV:");
        for (String campo : linea) {
            System.out.print(campo + " ");
        }
        System.out.println();
    }
}

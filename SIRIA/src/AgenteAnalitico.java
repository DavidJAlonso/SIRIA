import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;

public class AgenteAnalitico extends Agent {
    protected static final String TipoIncidencia = null;
	private boolean solicitudEnviada = false;

    protected void setup() {
        // Registrar servicio de AnalisisIncidencia
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("AnalisisIncidencia");
        sd.setName("AnalisisIncidencia");
        dfd.addServices(sd);

        // Registrar servicio de LlamadaServicio
        ServiceDescription sd2 = new ServiceDescription();
        sd2.setType("LlamadaServicio");
        sd2.setName("LlamadaServicio");
        dfd.addServices(sd2);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour(this) {
            private String AnalisisIncidencia;
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg2 = receive(mt);
                
                if (msg2 != null) {
                    String content = msg2.getContent();
                    
                    System.out.println("Solicitud recibida por Analitico: " + content);
                    String csvArchivo = content;
                    // Obtener los campos adicionales
                    String campo = msg2.getUserDefinedParameter("campo");
                    String campoAdicional1 = msg2.getUserDefinedParameter("CampoAdicional1");
                    String campoAdicional2 = msg2.getUserDefinedParameter("CampoAdicional2");
                    // Crear instancia de AnalisisEntradaIncidencia
                    AnalisisEntradaIncidencia analisisIncidencia = new AnalisisEntradaIncidencia(campo);

                    // Llamar al método analizar()
                    try {
						analisisIncidencia.analizar();
					} catch (CsvValidationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    
                    //System.out.println("Analitico ha cargado" + TipoIncidencia);
                    //String campo = ""; // Variable para almacenar el valor del campo del mensaje
                    // Parsear el contenido del mensaje y obtener el valor del campo
                    // Ejemplo: Si el contenido del mensaje es "Campo: valor", puedes hacer:
                    //if (content.contains("Campo:")) {
                    //    campo = content.substring(content.indexOf("Campo:") + 7).trim();
                    //}
                    //System.out.println(campo);
                    
                    // Responder al Agente Incidencias
                    ACLMessage reply = msg2.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("OK");
                    send(reply);
                } else {
                    block();
                }
            }
        });

    }

    private String[] leerArchivoCSV(String rutaArchivo) {
        try (CSVReader reader = new CSVReader(new FileReader(rutaArchivo))) {
            return reader.readNext();
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return null;
    }
    private class LlamadaServicio extends OneShotBehaviour {
        private String servicio;
        private String campo;
        LlamadaServicio(Agent agent, String servicio, String campo) {
            super(agent);
            this.servicio = servicio;
            this.campo = campo;
            
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
                    send(msg);

                    System.out.println("Llamada al servicio desde Analitico " + servicio);
                } else {
                    System.out.println("No se encontró un agente que ofrezca el servicio " + servicio);
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }
    }
}

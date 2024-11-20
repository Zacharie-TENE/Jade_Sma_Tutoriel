
package Doctor;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Doctor extends Agent {

    Scanner sc= new Scanner(System.in);

    List<String> symptoms = Arrays.asList(
            "fièvre", "frisson", "sensation de froid", "céphalée", "fatigue musculaire", "manque d'appétit",
            "nez qui coule", "éternuements", "mal de gorge", "fatigue", "maux de tête",
            "démangeaisons des yeux", "toux légère", "nausées", "vomissements",
            "fièvre élevée", "perte d'appétit", "diarrhée",
            "diarrhée aqueuse abondante", "déshydratation", "crampes abdominales"
    );
    // Symptômes pour chaque maladie
    private static final List<String> fluSymptoms = Arrays.asList("fièvre", "frisson", "sensation de froid", "céphalée", "fatigue musculaire", "manque d'appétit");
    private static final List<String> coldSymptoms = Arrays.asList("nez qui coule", "éternuements", "mal de gorge", "fatigue", "maux de tête");
    private static final List<String> allergiesSymptoms = Arrays.asList("éternuements", "nez qui coule", "démangeaisons des yeux", "fatigue", "toux légère");
    private static final List<String> malariaSymptoms = Arrays.asList("fièvre", "frissons", "maux de tête", "fatigue", "nausées", "vomissements");
    private static final List<String> typhoidSymptoms = Arrays.asList("fièvre élevée", "maux de tête", "fatigue", "perte d'appétit", "diarrhée");
    private static final List<String> choleraSymptoms = Arrays.asList("diarrhée aqueuse abondante", "vomissements fréquents", "déshydratation", "crampes abdominales");
    private AID secretary=new AID("infirmière", AID.ISLOCALNAME);

    private AID patient;

    boolean isPatient=false;
    private void sendGoodbyeResponse(AID patient) {
        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
        response.setConversationId("goodbye-response");
        response.addReceiver(patient);
        response.setContent("Doctor");
        send(response);
    }
    private void informAvailability(boolean bool) {

        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
        response.setConversationId("DOCTOR");
        response.addReceiver(secretary);
        String input;
        if(bool){
            System.out.println("Secretaire: Dr svp Etes-vous libre? 1.oui, 2.non");
            input= sc.nextLine();
        }
        else{
            input="1";
        }


        if(Objects.equals(input, "1")){

            response.setContent("Doctor is available");

        }
        else{
            response.setContent("Doctor is not available");
        }
        send(response);

    }

    private String extractInformation(String content, String key){
        String champRecherche = "\""+key+"\":";
        int indexDebut = content.indexOf(champRecherche);
        if (indexDebut == -1) {
            return null; // Champ non trouvé
        }

        indexDebut += champRecherche.length();
        int indexFin = content.indexOf(",", indexDebut);

        if (indexFin == -1) {
            indexFin = content.indexOf("}", indexDebut);
        }

        if (indexFin == -1) {
            return null; // Fin de champ non trouvé
        }

        return content.substring(indexDebut, indexFin).replace("\"", "").trim();
    }

    private String performDiagnosis(List<String> symptoms) {
        if (containsAllSymptoms(symptoms, fluSymptoms)) {
            return "Vous pourriez avoir la grippe.";
        } else if (containsAllSymptoms(symptoms, coldSymptoms)) {
            return "Vous pourriez avoir un rhume.";
        } else if (containsAllSymptoms(symptoms, allergiesSymptoms)) {
            return "Vous pourriez avoir des allergies.";
        } else if (containsAllSymptoms(symptoms, malariaSymptoms)) {
            return "Vous pourriez avoir le paludisme.";
        }else if (containsAllSymptoms(symptoms, typhoidSymptoms)) {
            return "Vous pourriez avoir la fièvre typhoïde.";}
        else if (containsAllSymptoms(symptoms, choleraSymptoms)) {
            return "Vous pourriez avoir le choléra.";
        }
        else {
            return "Nous ne pouvons pas conclure à un diagnostic dans l immediat. svp vous devez vous rendre à l hopital pour une analys eapprofondie.";
        }
    }


    private boolean containsAllSymptoms(List<String> symptoms, List<String> requiredSymptoms) {
        for (String symptom : requiredSymptoms) {
            if (!symptoms.contains(symptom)) {
                return false;
            }
        }
        return true;
    }



    protected void setup() {
        addBehaviour(new HandleSecretaryMessagesBehaviour());
    }

    protected void takeDown() {
        // Perform cleanup tasks here
    }

    private class HandleSecretaryMessagesBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                switch (msg.getConversationId()) {

                    case "askAvailability":
                        informAvailability(true);

                    case "patient-arrival":
                        if(!isPatient){
                            isPatient=true;
                            // Step 6: Inform the doctor about the patient's arrival
                            String message="greetings";
                            String patientLocalName=extractInformation(msg.getContent(),"name");
                            System.out.println("salut : "+patientLocalName);
                            patient=new AID(patientLocalName, AID.ISLOCALNAME);
                            ACLMessage greetingsPatient = new ACLMessage(ACLMessage.INFORM);
                            greetingsPatient.setConversationId("Doctor");
                            greetingsPatient.addReceiver(new AID(patientLocalName, AID.ISLOCALNAME));
                            greetingsPatient.setContent(message);
                            send(greetingsPatient);
                        }

                        break;

                    case "goodbye-from-patient":
                        if(Objects.equals(patient.getLocalName(), msg.getSender().getLocalName())){
                            isPatient=false;
                            patient=null;
                            System.out.println("Aurevoir " +msg.getSender() +"! Prennez soin de vous");
                            sendGoodbyeResponse(msg.getSender());
                            informAvailability(false);
                        }
                        break;

                    case "patient":
                        System.out.println("pouvez vous me decrire vos symptomes !?");
                        //demande de symptoms aux patients
                        ACLMessage symtompMessage = new ACLMessage(ACLMessage.REQUEST);
                        symtompMessage.setConversationId("patientSymptoms");
                        symtompMessage.addReceiver(patient);
                        symtompMessage.setContent(String.join(",", symptoms));

                        send(symtompMessage);
                        break;

                    case "symptoms":
                        // Effectuer le diagnostic en fonction des symptômes du patient
                        System.out.println("symptomes notés !");
                        List<String> restoredList = Arrays.asList( msg.getContent().split(","));
                        String diagnosis = performDiagnosis(restoredList);
                        System.out.println("voici votre prescription");
                        ACLMessage diagnosismsg = new ACLMessage(ACLMessage.INFORM);
                        diagnosismsg.setConversationId("prescription");
                        diagnosismsg.addReceiver(patient);
                        diagnosismsg.setContent(diagnosis);
                        send(diagnosismsg);
                        break;

                    // Handle other types of messages here if needed
                }
            }
        }
    }
}
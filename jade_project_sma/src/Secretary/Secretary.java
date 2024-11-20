
package Secretary;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;


public class Secretary extends Agent {
    private LinkedHashMap<AID, String> scheduledAppointments = new LinkedHashMap<>();
    private LinkedHashMap<AID,Notebook> Notebooks = new LinkedHashMap<>();
    private boolean nobody=true;
    //  private boolean isAppointmentScheduled = true;
     // In this example, let's assume the appointment is scheduled successfully
     // private String appointmentTime = "2024-04-15 07:52 matin";

    @Override
    protected void setup() {
        System.out.println("Agent secretaire " + getAID().getName() + " est prêt");

        // Add behavior for handling patient's appointment request
        addBehaviour(new HandleAppointmentRequestBehaviour());
    }
    @Override
    protected void takeDown(){
        System.out.println("END LIFE !");
    }

    private class HandleAppointmentRequestBehaviour extends CyclicBehaviour {
        public void action() {

            //  MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage request = receive();

            if (request != null) {
                switch (request.getConversationId()) {

                    case "appointment-from-patient":
                        System.out.println("la secretaire agent vient de recevoir une demande de rendez-vous " + request.getSender().getName());
                        //Step 3: Schedule appointment or inform patient to wait
                        if (!checkHashMapEmpty()) {
                            System.out.println("Actuellement, aucun médecin n'est disponible. Allez-vous pouvoir attendre ?.");
                            informPatientToWait(request.getSender());

                        } else {
                                System.out.println("Pouvez-vous me donner votre carnet médical ?.");
                                TakeNoteBook(request.getSender());
                                String status="processing";
                                scheduledAppointments.put(request.getSender(), status);

                        }
                        break;

                    case "goodbye-from-patient":
                        if (request.getPerformative() == ACLMessage.REJECT_PROPOSAL) {


                           // System.out.println("Secretary Agent received a reject from " + request.getSender().getName());

                        } else {
                           // System.out.println("Secretary Agent received a goodbye message from " + request.getSender().getName());

                        }
                        System.out.println("Aurevoir " +request.getSender() +"! prennez soin de vous !");
                        sendGoodbyeResponse(request.getSender());
                        break;

                    case "accept-proposal-waiting-by-patient" :

                        String status="waiting";
                        scheduledAppointments.put(request.getSender(), status);
                        nobody=false;
                        System.out.println("Merci M. /MM "+ request.getSender().getLocalName()+" pour votre comprehension !");
                        System.out.println("pouvez vous me donner votre carnet medical ");
                        TakeNoteBook(request.getSender());
                        break;

                    case "DOCTOR":
                        if(request.getContent().equals("Doctor is available")) {
                            removeFirstElement();
                            String newStatus = "processing";
                            AID nextPatient = setFirstInsertedElementStatusAndGetAID(newStatus);
                            callPatient(nextPatient,request.getSender());

                        }
                        else if(request.getContent().equals("End")) {
                            String message="Doctors are no longer available. Sorry please comme later";
                            sendMessageToWaitingAppointments(message);

                    }
                        break;

                    case "notebook-response":

                        Notebook notebook=NotebookCreation(request);
                        Notebooks.put(request.getSender(),notebook);
                        System.out.println("carnet medical recu. Veuillez vous rendre chez le medecin!");

                        if(nobody){
                            informDoctor(new AID("medecin1", AID.ISLOCALNAME),request.getSender(),Notebooks.get(request.getSender()));
                            nobody=false;
                        }

                        break;

                    case "proceedToAppointment":
                        if(request.getContent().equals("1") ){
                            informDoctor(new AID(request.getContent(), AID.ISLOCALNAME),request.getSender(),Notebooks.get(request.getSender()));
                            break;

                        }
                        else{

                            sendGoodbyeResponse(request.getSender());

                        };
                        break;



                }
            }
        }

    }


    private void TakeNoteBook(AID patient){
        ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
        req.setConversationId("notebook");
        req.addReceiver(patient);
        req.setContent("");
        send(req);

    }

    private Notebook NotebookCreation(ACLMessage request){

        String name=request.getSender().getLocalName();

        // Extract the information from the message
        String size = extractInformation(request.getContent(), "size");
        String gender = extractInformation(request.getContent(), "gender");
        String weight = extractInformation(request.getContent(), "weight");
        String bloodType = extractInformation(request.getContent(), "bloodType");
        String patientName = extractInformation(request.getContent(), "patientName");
        String medicalHistory = extractInformation(request.getContent(), "medicalHistory");
        String insurance = extractInformation(request.getContent(), "insurance");
        String contactInfo = extractInformation(request.getContent(), "contactInfo");
        String temperature = extractInformation(request.getContent(), "temperature");
        String bloodPressure = extractInformation(request.getContent(), "bloodPressure");
        String reason = extractInformation(request.getContent(), "reason");

        return new Notebook(name,size,gender,weight,bloodType,patientName,medicalHistory, insurance, contactInfo, temperature,bloodPressure,reason);
    }
    private void callPatient(AID patient,AID doctor)    {
        // Step 3: Send a message to the patient
        ACLMessage notification = new ACLMessage(ACLMessage.REQUEST);
        notification.setConversationId("call-appointment");
        notification.addReceiver(patient);
        notification.setContent(doctor.getLocalName());
        send(notification);
    }
    private void informDoctor(AID doctor,AID patient,Notebook note){
            // Step 6: Inform the doctor about the patient's arrival
            ACLMessage informDoctor = new ACLMessage(ACLMessage.INFORM);
            informDoctor.setConversationId("patient-arrival");
            informDoctor.addReceiver(doctor);
            informDoctor.setContent(note.convertNotebookToJson());
            send(informDoctor);

    }
    private void informPatientToWait(AID patient) {
        ACLMessage notification = new ACLMessage(ACLMessage.PROPOSE);
        notification.setConversationId("appointment-waiting");
        notification.addReceiver(patient);
        notification.setContent("Actuellement, le Dr est indisponible?Pouvez-vous attendre? 1.oui ,2.non.");
        send(notification);
    }

    private void sendGoodbyeResponse(AID patient) {
        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
        response.setConversationId("goodbye-response");
        response.addReceiver(patient);
        response.setContent("Secretary");
        send(response);
    }

    public boolean checkHashMapEmpty() {
            return this.scheduledAppointments.isEmpty();
        }

    public void removeFirstElement() {
        Iterator<Map.Entry<AID, String>> iterator = scheduledAppointments.entrySet().iterator();

        if (iterator.hasNext()) {
            iterator.next();
            iterator.remove();


            if (scheduledAppointments.isEmpty()) {
                nobody = true;}
        } else {
            //System.out.println("Empty List.");
        }
    }

    public Map.Entry<AID, String> getFirstInsertedElement() {
        // Récupérer un ensemble de paires clé-valeur de la LinkedHashMap
        Set<Map.Entry<AID, String>> entrySet = scheduledAppointments.entrySet();

        // Vérifier si la liste n'est pas vide
        if (!entrySet.isEmpty()) {
            // Récupérer le premier élément inséré en utilisant un itérateur
            Iterator<Map.Entry<AID, String>> iterator = entrySet.iterator();
            return iterator.next();
        } else {
            //System.out.println("Empty list");
            return null;
        }
    }

    public AID setFirstInsertedElementStatusAndGetAID(String newStatus) {
        // Récupérer le premier élément inséré de la LinkedHashMap
        Map.Entry<AID, String> firstInsertedEntry = getFirstInsertedElement();

        if (firstInsertedEntry != null) {
            // Récupérer l'AID du premier élément inséré
            AID firstInsertedAID = firstInsertedEntry.getKey();

            // Modifier le statut du premier élément inséré
            firstInsertedEntry.setValue(newStatus);
            System.out.println("Status du patient" + firstInsertedAID + " reussi avec success");

            return firstInsertedAID;
        } else {
            //System.out.println("Empty list.");
            return null;
        }
    }
    public void sendMessageToWaitingAppointments( String message) {
        for (Map.Entry<AID, String> entry : scheduledAppointments.entrySet()) {
            if (entry.getValue().equalsIgnoreCase("waiting")) {
                // Send a message to the entry with a status of "waiting"

                ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                aclMessage.addReceiver(entry.getKey());
                aclMessage.setContent(message);
                send(aclMessage);
            }
        }
    }

    private String extractInformation(String content, String key) {
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

    public boolean uniqueElement(){
        return scheduledAppointments.size() == 1;
    }
    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        Date dateActuelle = new Date();
        return sdf.format(dateActuelle);

    }

    public boolean compareDates(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);

            return d1.equals(d2);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void scheduleAppointment(AID patient,String appointmentTime) {

        ACLMessage confirmation = new ACLMessage(ACLMessage.CONFIRM);
        confirmation.setConversationId("appointment-confirmation");
        confirmation.addReceiver(patient);
        confirmation.setContent("Your appointment is scheduled for " + appointmentTime + ". Please be ready at the appointed time.");
        send(confirmation);
    }

    private void checkDoctorAvailability() {
        // Send availability requests to doctors and wait for their responses
        ACLMessage availabilityRequest = new ACLMessage(ACLMessage.REQUEST);
        availabilityRequest.setConversationId("askAvailability");
        availabilityRequest.setContent("Doctor availability request");
        availabilityRequest.addReceiver(new AID("medecin1", AID.ISLOCALNAME));
        //availabilityRequest.addReceiver(new AID("medecin2", AID.ISLOCALNAME));
        send(availabilityRequest);
    }

}



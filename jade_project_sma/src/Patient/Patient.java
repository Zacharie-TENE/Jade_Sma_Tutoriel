package Patient;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;


public class Patient extends Agent {
    List<String> symptoms;
    AID doctor;
    @Override
    protected void setup() {
        System.out.println("Patient Agent " + getAID().getName() + " is ready.");

        // Step 1: Send an appointment request to the secretary
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.setConversationId("appointment-from-patient");
        request.setContent("Appointment request");
        request.addReceiver(new AID("infirmière", AID.ISLOCALNAME));
        send(request);
        System.out.println("Le Patient a envoyé une demande de rendez-vous chez à l'infirmière");

        // Add behavior for requesting an appointment
        addBehaviour(new RequestAppointmentBehaviour());
    }

    @Override
    protected void takeDown(){
        System.out.println("END LIFE !");
    }

    private String Notebook(){

        Scanner scanner = new Scanner(System.in);

        // Variables pour les informations du patient
        System.out.println("Veuillez entrer votre taille :");
        String size = scanner.nextLine();

        System.out.println("Veuillez entrer votre sexe  :");
        String gender = scanner.nextLine();

        System.out.println("Veuillez entrer votre poidsen  kg:");        String weight = scanner.nextLine();
        // Variables pour les  du patient
        //String size = "175";
       // String gender = "Male";
       // String weight = "70";
        String bloodType = "O+";
        String patientName = "John Smith";
        String medicalHistory = "Cardiovascular and respiratory conditions";
        String insurance = "Yes";
        String contactInfo = "john@example.com";
        String temperature = "37.5";
        String bloodPressure = "120/80";
        String reason = "Routine check-up";

        // Construire le contenu du message en utilisant la concaténation de chaînes
        String content = "{"
                + "\"size\": \"" + size + "\","
                + "\"gender\": \"" + gender + "\","
                + "\"weight\": \"" + weight + "\","
                + "\"bloodType\": \"" + bloodType + "\","
                + "\"patientName\": \"" + patientName + "\","
                + "\"medicalHistory\": \"" + medicalHistory + "\","
                + "\"insurance\": \"" + insurance + "\","
                + "\"contactInfo\": \"" + contactInfo + "\","
                + "\"temperature\": \"" + temperature + "\","
                + "\"bloodPressure\": \"" + bloodPressure + "\","
                + "\"reason\": \"" + reason + "\""
                + "}";

        return content;
    }

    private String getSymptoms(){
        List<String> selectedSymptoms = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Affichage du menu
            int counter = 1;
            for (String symptom : symptoms) {
                System.out.printf("%-2d. %-25s", counter, symptom);
                if (counter % 4 == 0) {
                    System.out.println();
                }
                counter++;
            }
            System.out.println();
            // Lecture des choix du patient
            String input = scanner.nextLine();



            // Analyse des choix du patient
            String[] choices = input.split(",");
            for (String choice : choices) {
                try {
                    int index = Integer.parseInt(choice.trim());
                    if ((index >= 1 && index <= symptoms.size()) || index>=30) {
                        String symptom = symptoms.get(index - 1);
                        if (!selectedSymptoms.contains(symptom)) {
                            selectedSymptoms.add(symptom);
                        }
                    } else {
                        System.out.println("Numéro de symptôme invalide : " + index);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Format de numéro invalide : " + choice);
                }
            }

            System.out.println("Avez-vous terminé? 1. oui ,2. non");
            String entry = scanner.nextLine();
            if (entry.equals("1")) {
                System.out.println("Symptômes sélectionnés :");
                for (String symptom : selectedSymptoms) {
                    System.out.println(symptom);
            }
                break;
            }

        }




        return String.join(",", selectedSymptoms);
    };

    private class RequestAppointmentBehaviour extends CyclicBehaviour {

        public void action() {
            ACLMessage response = receive();
            if (response != null) {
                switch (response.getConversationId()) {
                    case "appointment-confirmation":
                        System.out.println("Patient Agent a recu une confirmation de rdv de la secretaire");
                        System.out.println(response.getContent());
                        break;

                    case "appointment-waiting":
                        //logique secretaire
                        System.out.println("Secretaire : " + response.getContent());
                        Scanner scanner = new Scanner(System.in);
                        String input = scanner.nextLine();
                        ACLMessage notification;
                        if(Objects.equals(input, "1"))
                        {
                            notification = new ACLMessage(ACLMessage.CONFIRM);
                            notification.setConversationId("accept-proposal-waiting-by-patient");


                        }
                        else{
                            notification = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                            notification.setConversationId("goodbye-from-patient");

                        }
                        notification.addReceiver(response.getSender());
                        notification.setContent(input);
                        scanner.close();
                        send(notification);

                        break;

                    case "call-appointment":

                        System.out.println("Secretaire : " + " Vous y allez Mr ? 1.Oui , 2.non :");
                        Scanner scanners = new Scanner(System.in);
                        String inputs = scanners.nextLine();
                        ACLMessage decision = new ACLMessage(ACLMessage.INFORM);
                        decision.setConversationId("proceedToAppointment");
                        decision.addReceiver(response.getSender());
                        doctor=new AID(response.getContent(), AID.ISLOCALNAME);
                        decision.setContent(inputs);
                        scanners.close();
                        send(decision);

                        break;

                    case "Doctor":
                       if(Objects.equals(response.getContent(), "greetings")){
                           String patientResponse = "greetings";

                           ACLMessage answer = new ACLMessage(ACLMessage.INFORM);
                           answer.addReceiver(response.getSender());
                           answer.setConversationId("patient");
                           answer.setContent(patientResponse);
                           send(answer);
                       }
                        break;


                    case "patientSymptoms":
                    {

                           System.out.println("Veuillez entrer les numéros des symptômes (séparés par des virgules) ou '30' pour terminer :");
                           symptoms = Arrays.asList( response.getContent().split(","));
                           String Symptoms= getSymptoms();
                           ACLMessage answer = new ACLMessage(ACLMessage.INFORM);
                           answer.addReceiver(response.getSender());
                           answer.setConversationId("symptoms");
                           answer.setContent(Symptoms);
                           send(answer);

                       }
                        break;

                    case "prescription":
                        System.out.println(response.getContent());
                        System.out.println("je vais respecter la prescription");
                        // Step 6: Process the prescription
                        // Implement your logic to process the prescription here

                        // Step 7: Send a goodbye message to the secretary
                        ACLMessage goodbye = new ACLMessage(ACLMessage.INFORM);
                        goodbye.setConversationId("goodbye-from-patient");
                        goodbye.addReceiver(response.getSender());
                        goodbye.addReceiver(new AID("infirmière", AID.ISLOCALNAME));
                        goodbye.setContent("Aurevoir");
                        send(goodbye);
                        System.out.println("Agent patient dit aurevoir à la secretaire t au docteur");
                        break;

                    case "goodbye-response":
                        if(Objects.equals(response.getContent(), "Doctor")){
                            System.out.println("Merci docteur");
                        }
                        else if(Objects.equals(response.getContent(), "Secretary")){
                            System.out.println("Merci !");
                        }
                        break;


                    case "notebook":
                        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                        message.addReceiver(response.getSender());
                        message.setConversationId("notebook-response");
                        String content=Notebook();
                        System.out.println("Voici mon carnet medical!"+ content);
                        message.setContent(content);
                        send(message);
                        break;



                    default:
                        // Handle other cases here
                }


            }
        }
    }
}

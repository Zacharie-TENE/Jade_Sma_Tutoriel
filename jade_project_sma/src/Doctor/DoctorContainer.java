package Doctor;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

public class DoctorContainer {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl(false);
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("medecin1", "Doctor.Doctor", new Object[]{});
        agentController.start();
    }

    private static void startAgent(AgentContainer container, String agentName, Class agentClass) throws StaleProxyException {
        AgentController agentController = container.createNewAgent(agentName, agentClass.getName(), null);
        agentController.start();
    }

}

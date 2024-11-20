package Patient;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

public class PatientContainer {
    private static void startAgent(AgentContainer container, String agentName, Class agentClass, Object[] Object) throws StaleProxyException {
        AgentController agentController = container.createNewAgent(agentName, agentClass.getName(), Object);
        agentController.start();
    }
    public static void main(String[] args) throws ControllerException {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl(false);
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        //  AgentController agentController = agentContainer.createNewAgent("malade", "Patient.Patient", new Object[]{});

        startAgent(agentContainer,"malade", Patient.class, new Object[]{});
    }
}

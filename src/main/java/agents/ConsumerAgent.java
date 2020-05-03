package agents;

import containers.ConsumerGui;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent extends GuiAgent {
    private transient ConsumerGui gui;

    @Override
    protected void setup() {
        if (getArguments().length > 0) {

            gui = (ConsumerGui) getArguments()[0];
            gui.setConsumerAgent(this);

        }
        System.out.println("------------- AGENT SETUP -----------");
        System.out.println("------------- Name--> " + getAID().getName());
        System.out.println("Parameters------->" + getArguments()[0]);
        System.out.println("------------- AGENT SETUP -----------");
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if (message == null) block();
                else {
                    gui.logMessage(message);
                }
            }
        });
    }

    @Override
    protected void beforeMove() {
        System.out.println("-----------------");
        System.out.println("Avant Migration");
        System.out.println("-----------------");
    }

    @Override
    protected void afterMove() {
        System.out.println("-----------------");
        System.out.println("Apres Migration");
        System.out.println("-----------------");
    }

    @Override
    protected void takeDown() {
        System.out.println("-----------------");
        System.out.println("Im aboutta die == mimic Destructeur");
        System.out.println("-----------------");
    }

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        if (guiEvent.getType() == 1) {
            String livre = (String) guiEvent.getParameter(0);
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(livre);
            aclMessage.addReceiver(new AID("Acheteur1", AID.ISLOCALNAME));
            send(aclMessage);
        }
    }
}

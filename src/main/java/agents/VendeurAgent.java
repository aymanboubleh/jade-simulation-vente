package agents;

import containers.VendeurGui;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class VendeurAgent extends GuiAgent {
    private VendeurGui vendeurGui;

    @Override
    protected void setup() {
        if (getArguments().length > 0) {
            vendeurGui = (VendeurGui) getArguments()[0];
            vendeurGui.setVendeurAgent(this);
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();

        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription agentDescription = new DFAgentDescription();
                agentDescription.setName(getAID());
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-livres");
                agentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent, agentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage reply;
                ACLMessage message = receive();

                if (message == null) block();
                else {
                    switch (message.getPerformative()) {
                        case ACLMessage.CFP:
                            reply = message.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(message.getContent() + ":" + (500 + new Random().nextInt(300)));
                            System.out.println("VendeurProposeReplyMessage:" + reply.getContent());
                            send(reply);
                            vendeurGui.logMessage(myAgent, reply);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            reply = message.createReply();
                            if (Math.random() > 0.99) {
                                reply.setPerformative(ACLMessage.AGREE);
                                reply.setContent(message.getContent());
                            } else {
                                reply.setPerformative(ACLMessage.REFUSE);
                                reply.setContent(message.getContent() + "_REFUSE");
                            }
                            System.out.println("VendeurACCPTREFUSEReplyMessage:" + reply.getContent());
                            send(reply);
                            vendeurGui.logMessage(myAgent, reply);
                            break;

                    }
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}

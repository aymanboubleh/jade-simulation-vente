package agents;

import containers.AcheteurGui;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

public class AcheteurAgent extends GuiAgent {
    protected AcheteurGui acheteurGui;
    protected ArrayList<AID> vendeurs = new ArrayList<>();
    private ArrayList<AcheteurRequest> acheteurRequests = new ArrayList<>();
    private AcheteurRequest acheteurRequest;

    public AcheteurRequest findByProposal(ACLMessage proposal) {
        System.out.println("Proposal-->" + proposal.getContent());
        int indexdouble = proposal.getContent().indexOf(":");
        String livre;
        if (indexdouble > -1)
            livre = proposal.getContent().substring(0, proposal.getContent().indexOf(":"));
        else
            livre = proposal.getContent();
        System.out.println("livre-->" + livre);
        for (AcheteurRequest acheteurRequest : acheteurRequests)
            if (acheteurRequest.getLivre().equals(livre)) return acheteurRequest;
        return null;
    }

    @Override
    protected void setup() {
        if (getArguments().length > 0) {
            acheteurGui = (AcheteurGui) getArguments()[0];
            acheteurGui.setAcheteurAgent(this);
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-livres");
                template.addServices(serviceDescription);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    for (DFAgentDescription dfAgentDescription : result) {
                        if (!vendeurs.contains(dfAgentDescription.getName()))
                            vendeurs.add(dfAgentDescription.getName());
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }

            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                MessageTemplate.or(
                                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE),
                                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                                )
                        )
                );
                ACLMessage message = receive(messageTemplate);
                if (message == null) block();
                else {
                    acheteurGui.logMessage(message);
                    ACLMessage reply;
                    switch (message.getPerformative()) {
                        case ACLMessage.REQUEST:
                            if (vendeurs.size() == 0) {
                                //AUCUN VENDEUR
                                reply = message.createReply();
                                reply.setPerformative(ACLMessage.FAILURE);
                                reply.setContent("Pas de vendeurs");
                                System.out.println("PAS DE VENDEURS");
                                send(reply);
                                return;
                            }
                            ACLMessage message2 = new ACLMessage(ACLMessage.CFP);
                            message2.setContent(message.getContent());
                            acheteurRequests.add(new AcheteurRequest(message.getSender(), message.getContent(), vendeurs.size()));
                            for (AID vendeur : vendeurs) {
                                message2.addReceiver(vendeur);
                            }
                            send(message2);
                            break;
                        case ACLMessage.PROPOSE:
                            acheteurRequest = findByProposal(message);
                            acheteurRequest.addProposal(message);
                            if (acheteurRequest.isFull()) {
                                ACLMessage bestProposal = acheteurRequest.getBestProposal();
                                reply = bestProposal.createReply();
                                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                reply.setContent(bestProposal.getContent());
                                send(reply);
                            }
                            break;

                        case ACLMessage.AGREE:
                            acheteurRequest = findByProposal(message);

                            acheteurRequests.remove(acheteurRequest);
                            System.out.println("AcheteurRequest Done");
                            acheteurRequest.affichProps();
                            reply = new ACLMessage(ACLMessage.CONFIRM);
                            reply.addReceiver(acheteurRequest.getConsumerAgent());
                            reply.setContent(message.getContent() + ":" + message.getSender().getLocalName() + "_CONFIRM");
                            send(reply);

                            break;
                        case ACLMessage.REFUSE:
                            acheteurRequest = findByProposal(message);

                            System.out.println("ProposalSize before Deletion of refused proposal-->" +acheteurRequest.getProposals().size());
                            acheteurRequest.affichProps();
                            acheteurRequest.removeProposal(message);
                            System.out.println("ProposalSize After Deletion of refused proposal-->" +acheteurRequest.getProposals().size());
                            ACLMessage bestProposal = acheteurRequest.getBestProposal();
                            if (bestProposal == null) {
                                //Refus de tous les vendeurs
                                ACLMessage reply2 = new ACLMessage(ACLMessage.FAILURE);
                                reply2.addReceiver(acheteurRequest.getConsumerAgent());
                                reply2.setContent("Refus des vendeurs" + "_REFUSE");
                                System.out.println("AcheteurRequest Done");
                                System.out.println("REFUS DES VENDEURS");
                                send(reply2);
                                acheteurRequests.remove(acheteurRequest);
                                return;
                            } else {
                                reply = bestProposal.createReply();
                                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                reply.setContent(bestProposal.getContent() + "_ACCEPT_PROPOSAL");
                                send(reply);
                                System.out.println("AcheteurACCEPT_PROPOSAL:" + reply.getContent());
                            }
                            break;
                    }
                }
            }
        });

    }

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {

    }
}

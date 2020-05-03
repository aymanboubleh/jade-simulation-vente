package agents;

import agents.ConsumerAgent;
import agents.VendeurAgent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class AcheteurRequest {
    AID consumerAgent;
    String livre;
    int maxProposals;
    ArrayList<ACLMessage> proposals = new ArrayList<>();

    public ArrayList<ACLMessage> getProposals() {
        return proposals;
    }

    public AcheteurRequest(AID consumerAgent, String livre, int maxProposals) {
        this.consumerAgent = consumerAgent;
        this.livre = livre;
        this.maxProposals = maxProposals;
    }

    public int getMaxProposals() {
        return maxProposals;
    }

    public void setMaxProposals(int maxProposals) {
        this.maxProposals = maxProposals;
    }
    public boolean isFull(){
        return proposals.size() == maxProposals;
    }
    public AID getConsumerAgent() {
        return consumerAgent;
    }
    public void addProposal(ACLMessage proposalMessage){
        proposals.add(proposalMessage);
    }
    public ACLMessage getBestProposal(){
        if(proposals.size() == 0) return null;
        ACLMessage bestProposal = proposals.get(0);
        for(ACLMessage proposal:proposals){

            if(Integer.parseInt(proposal.getContent().substring(proposal.getContent().indexOf(":")+1)) < Integer.parseInt(bestProposal.getContent().substring(bestProposal.getContent().indexOf(":")+1)))
                bestProposal = proposal;
        }
        return bestProposal;
    }
    public void setConsumerAgent(AID consumerAgent) {
        this.consumerAgent = consumerAgent;
    }
    public void affichProps(){
            System.out.println("---------AFFICHAge--------------");
        for(ACLMessage message:proposals){
            System.out.println(message.getContent());
        }
            System.out.println("---------AFFICHAge--------------");
    }
    public String getLivre() {
        return livre;
    }
    public void removeProposal(ACLMessage proposal){
            for(ACLMessage proposalM:proposals){
                if(proposal.getSender().equals(proposalM.getSender())){
                    proposals.remove(proposalM);
                    return;
                }
            }
    }

    public void setLivre(String livre) {
        this.livre = livre;
    }


}

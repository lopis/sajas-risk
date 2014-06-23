package agents;

import java.util.LinkedList;

import perceptions.Perception;

import util.R;
import behaviours.playeragent.JoinGameBehaviour;
import behaviours.playeragent.SensorBehaviour;
import communication.PlayRequestResponder;

import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.domain.DFService;
import up.fe.liacc.sajas.domain.FIPAException;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.ServiceDescription;
import logic.Board;

public class PlayerAgent extends RepastAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5347118089332895954L;

	private PlayRequestResponder responder;
	private PlayerAgentBehaviours a;
	protected Board board;
	protected int currentRound;
	protected LinkedList<Perception> perceptions;

	public PlayerAgent(PlayerAgentBehaviours a) {
		this.a = a;
		a.setPlayerAgent(this);
		board = new Board();
		currentRound=0;
		perceptions= new LinkedList<Perception>();
	}

	public Board getBoard() {
		return board;
	}
	
	public void newRound(){
		currentRound++;
	}
	
	public int getCurrentRound(){
		return currentRound;
	}

	protected void setup() {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType(R.PLAYER_AGENT);
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		System.out.println(getLocalName() + " reporting in.");

		responder = new PlayRequestResponder(this,
				PlayRequestResponder.getMessageTemplate());
		SensorBehaviour sensor = new SensorBehaviour(this, a);
		responder.setSensor(sensor);
		addBehaviour(responder);
		addBehaviour(new JoinGameBehaviour(this));
		addBehaviour(sensor);
	}

	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	public Perception getPerception(){
		return perceptions.removeFirst();
	}
	
	public void addPerception(Perception p){
		if(perceptions.size() > 1000)
			getPerception();
		perceptions.add(p);
	}
	
	public boolean perceptEmpty(){
		return perceptions.isEmpty();
	}

	public void setBoard(Board b) {
		board=b;
	}
}

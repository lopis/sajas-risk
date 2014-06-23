package communication;

import java.io.IOException;
import java.util.ArrayList;

import perceptions.Perception;

import behaviours.gameagent.GameAgentFaseBehaviour;
import behaviours.playeragent.JoinGameBehaviour;
import actions.Action;
import util.R;
import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.domain.FIPANames;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.AchieveREInitiator;
import logic.Board;

public class RequestInitiator extends AchieveREInitiator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7797031078781475466L;

	private GameAgentFaseBehaviour b;
	private JoinGameBehaviour j;

	private boolean isForcedFinished = false;;

	public RequestInitiator(Agent a, ACLMessage msg) {
		super(a, msg);
		b = null;
		j = null;
		isForcedFinished = true;
	}

	public RequestInitiator(Agent a, ACLMessage msg, GameAgentFaseBehaviour b) {
		super(a, msg);
		this.b = b;
		j = null;
	}

	public RequestInitiator(Agent a, ACLMessage msg,
			JoinGameBehaviour joinGameBehaviour) {
		super(a, msg);
		b = null;
		j = joinGameBehaviour;
	}

	public static ACLMessage getChangedBoardMessage(ArrayList<AID> players,
			Board b) {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

		try {
			request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			request.setContentObject(b);
			for (AID to : players)
				request.addReceiver(to);
			return request;
		} catch (IOException e) {
			request.setPerformative(ACLMessage.FAILURE);
			request.setContent(e.getMessage());
			return request;
		}
	}

	public static ACLMessage getPerceptionMessage(ArrayList<AID> p,
			Perception perception) {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

		try {
			request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			request.setContentObject(perception);
			for (AID to : p)
				request.addReceiver(to);
			return request;
		} catch (IOException e) {
			request.setPerformative(ACLMessage.FAILURE);
			request.setContent(e.getMessage());
			return request;
		}
	}

	public static ACLMessage getJoinMessage(AID to, String agentName) {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.setContent(R.SUBSCRIPTION + " " + agentName);
		request.addReceiver(to);

		return request;
	}

	public static ACLMessage getPlayMessage(AID to) {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.setContent(R.PLAY);
		request.addReceiver(to);

		return request;
	}

	public static ACLMessage getReceiveMessage(AID to, int n) {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

		request.setContent(R.RECEIVE + " " + n);
		request.addReceiver(to);

		return request;
	}

	public static ACLMessage getAtackMessage(AID to) {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.setContent(R.ATACK);
		request.addReceiver(to);

		return request;
	}

	public static ACLMessage getFortifyMessage(AID to) {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.setContent(R.FORTIFY);
		request.addReceiver(to);
		return request;
	}

	protected void handleInform(ACLMessage inform) {
		try {
			Action action = (Action) inform.getContentObject();
			
			if (action == null) {
				switch (inform.getContent()) {
				case R.PLAY:
					System.out.println("Played");
					break;
				case R.JOIN:
					System.out.println("Joined with success!");
					j.joined();
					break;
				}
				return;
			}
			
			switch (action.getClass().getName()) {
			case R.RECEIVE_ACTION:
				handleAction(action);
				break;
			case R.PERFORM_ATACK:
				handleAction(action);
				break;
			case R.DONT_FORTIFY:
				handleAction(action);
				break;
			case R.DONT_ATACK:
				handleAction(action);
				break;
			case R.PERFORM_FORTIFICATION:
				handleAction(action);
				break;
			case R.CONTINUE_ACTION:
				handleAction(action);
				break;
			default:
				System.out.println("Desconhecido");
				break;
			}
		} catch (UnreadableException e) {
			
		}
	}

	private void handleAction(Action a) {
		if (b == null) {
			System.out.println("Null behaviour");
		} else {
			b.handleAction(a);
		}
	}

	protected void handleFailure(ACLMessage failure) {
		// System.out.println("Couldn't join!");
		switch (failure.getContent()) {
		case R.JOIN:
			j.couldntJoin();
			break;
		}
	}
	
	@Override
	public boolean done() {
		return isForcedFinished 
			|| super.done()
			|| j!= null && j.done()
			|| b!= null && b.done();
	}

}

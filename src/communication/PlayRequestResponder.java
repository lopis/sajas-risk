package communication;

import java.io.IOException;

import perceptions.Perception;

import behaviours.playeragent.SensorBehaviour;
import util.R;

import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.domain.FIPANames;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.MessageTemplate;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.AchieveREResponder;
import logic.Board;

/**
 * What players use to handle call for plays from the board
 * @author joaolopes
 *
 */
public class PlayRequestResponder extends AchieveREResponder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7663285688046592434L;

	private SensorBehaviour b;

	public PlayRequestResponder(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	public void setSensor(SensorBehaviour sensor) {
		b = sensor;
	}

	public static MessageTemplate getMessageTemplate() {
		return AchieveREResponder
				.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);
	}

	protected ACLMessage handleRequest(ACLMessage request) {		
		try {
			Object obj = request.getContentObject();
			if (obj == null) {
				String content = request.getContent();
				String type = content.split(" ")[0];

				switch (type) {
				case R.PLAY:
					return handlePlayRequest(request);
				case R.RECEIVE:
					return handleReceiveRequest(request);
				case R.ATACK:
					return handleAtackRequest(request);
				case R.FORTIFY:
					return handleFortifyRequest(request);
				}
			}
			if( obj.getClass().getName().equals(R.BOARD_CLASS)){
				Board board = (Board) request.getContentObject();
				return handleUpdate(request, board);
			}
			else if( obj.getClass().getSuperclass().getName().equals(R.PERCEPTION_CLASS)){
				Perception p = (Perception) request.getContentObject();
				return handlePerception(request,p);
			}
			
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		ACLMessage error = request.createReply();
		error.setPerformative(ACLMessage.FAILURE);
		error.setContent("Content not valid!");

		return error;
	}

	private ACLMessage handlePerception(ACLMessage request, Perception p) {
		ACLMessage inform = request.createReply();
		inform.setPerformative(ACLMessage.INFORM);
		inform.setContent(R.UPDATED);

		b.addPerception(p);

		return inform;
	}

	private ACLMessage handleUpdate(ACLMessage request, Board board) {
		ACLMessage inform = request.createReply();
		inform.setPerformative(ACLMessage.INFORM);
		inform.setContent(R.UPDATED);

		b.updateBoard(board);

		return inform;
	}

	private ACLMessage handleFortifyRequest(ACLMessage request) {
		ACLMessage fortify = request.createReply();

		try {
			fortify.setPerformative(ACLMessage.INFORM);
			fortify.setContentObject(b.fortify());
		} catch (IOException e) {
			fortify.setPerformative(ACLMessage.FAILURE);
			fortify.setContent("Could not serialize action!");
		}

		return fortify;
	}

	private ACLMessage handleAtackRequest(ACLMessage request) {

		ACLMessage atack = request.createReply();

		try {
			atack.setPerformative(ACLMessage.INFORM);
			atack.setContentObject(b.atack());
		} catch (IOException e) {
			atack.setPerformative(ACLMessage.FAILURE);
			atack.setContent("Could not serialize action!");
		}

		return atack;
	}

	private ACLMessage handleReceiveRequest(ACLMessage request) {
		ACLMessage receive = request.createReply();

		try {
			String nS = request.getContent().split(" ")[1];
			int n = Integer.parseInt(nS);

			receive.setPerformative(ACLMessage.INFORM);
			receive.setContentObject(b.receive(n));
		} catch (IOException e) {
			receive.setPerformative(ACLMessage.FAILURE);
			receive.setContent("Could not serialize action!");
		}

		return receive;
	}

	private ACLMessage handlePlayRequest(ACLMessage request) {
		ACLMessage join = request.createReply();

		try {
			join.setPerformative(ACLMessage.INFORM);
			join.setContentObject(b.respond());
		} catch (IOException e) {
			join.setPerformative(ACLMessage.FAILURE);
			join.setContent("Could not serialize action!");
		}

		return join;
	}

}

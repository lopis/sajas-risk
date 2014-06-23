package behaviours.playeragent;

import util.R;
import communication.RequestInitiator;

import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.core.behaviours.SimpleBehaviour;
import up.fe.liacc.sajas.domain.DFService;
import up.fe.liacc.sajas.domain.FIPAException;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.SearchConstraints;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.ServiceDescription;

public class JoinGameBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5782880757461631230L;

	private boolean hasJoined, waiting;
	private int i;
	private AID[] gameAgents;

	public JoinGameBehaviour(Agent agent) {
		super(agent);
		hasJoined = false;
		waiting = false;
		i = 0;
		gameAgents = null;
	}

	@Override
	public void action() {
		if (waiting)
			return;

		if (gameAgents == null) {
			gameAgents = getGameAgents();
		}

		if (i >= gameAgents.length) {
			gameAgents = null;
			i = 0;
			return;
		}

		myAgent.addBehaviour(new RequestInitiator(myAgent, RequestInitiator
				.getJoinMessage(gameAgents[i], myAgent.getLocalName()), this));
		waiting = true;
		i++;
	}

	private AID[] getGameAgents() {
		// Build the description used as template for the subscription
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription templateSd = new ServiceDescription();
		templateSd.setType(R.GAME_AGENT);
		template.addServices(templateSd);

		SearchConstraints sc = new SearchConstraints();

		try {
			DFAgentDescription[] results = DFService.search(myAgent, template,
					sc);
			AID[] agents = new AID[results.length];
			if (results.length > 0) {
				for (int i = 0; i < results.length; ++i) {
					agents[i] = results[i].getName();
				}
			}
			return agents;
		} catch (FIPAException e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	public void joined() {
		hasJoined = true;
	}

	@Override
	public boolean done() {
		return hasJoined;
	}

	public void couldntJoin() {
		waiting = false;
		hasJoined = false;
	}

}

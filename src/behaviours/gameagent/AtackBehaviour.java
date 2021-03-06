package behaviours.gameagent;

import util.R;

import actions.Action;
import actions.PerformAtackAction;
import actions.ValidateAction;
import agents.GameAgent;
import communication.RequestInitiator;

import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.core.Agent;

public class AtackBehaviour extends GameAgentFaseBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2042356053011233963L;

	public AtackBehaviour(Agent a, AID to) {
		super(a, to);
	}

	public AtackBehaviour(Agent a) {
		super(a, null);
	}

	@Override
	public void action() {
		
		if (!waiting){
			myAgent.addBehaviour(new RequestInitiator(myAgent, RequestInitiator
					.getAtackMessage(to), this));
		}

		waiting = true;
	}

	@Override
	public void handleAction(Action a) {
		this.action = a;
		end = true;
		//waiting =false;
	}

	@Override
	public int onEnd() {
		if (action.getClass().getName().equals(R.DONT_ATACK)) {
			return 1;
		}

		if (action.getClass().getName().equals(R.PERFORM_ATACK)
				&& ValidateAction.validate((PerformAtackAction) action,
						to.getLocalName(), ((GameAgent)myAgent).getBoard())) {
			return 1;
		} else {
			return 0;
		}
	}

}

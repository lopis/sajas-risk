package behaviours.gameagent;

import java.util.ArrayList;

import communication.RequestInitiator;
import agents.GameAgent;
import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.core.behaviours.SimpleBehaviour;

public class UpdateRoundBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4252143295214188705L;

	public static final int FINAL = 0;
	public static final int CONT = 1;
	
	static long initialTime = 0;
	static int roundNum = 0;

	private int currentRound, currentPlayer;
	private ArrayList<AID> players;
	private RequestActionBehaviour position, attack, fortify;

	public UpdateRoundBehaviour(Agent a, ArrayList<AID> players,
			RequestActionBehaviour position, RequestActionBehaviour attack,
			RequestActionBehaviour fortify) {
		super(a);
		currentRound = 0;
		currentPlayer = 0;
		this.players = players;
		this.position = position;
		this.attack = attack;
		this.fortify = fortify;
		if (initialTime == 0) {
			initialTime = System.currentTimeMillis();
		}
	}

	@Override
	public void action() {
		
		long elapsedTime = System.currentTimeMillis() - initialTime;
		System.out.println("# New Round # " + (elapsedTime * 0.001) + "\t" + (elapsedTime * 0.0005468741));
		
		if (System.currentTimeMillis() - initialTime > 10000) {
			System.out.println("## END ##");
			System.exit(0);
		}

		GameAgent agent = (GameAgent) myAgent;

		myAgent.addBehaviour(new RequestInitiator(myAgent, RequestInitiator
				.getChangedBoardMessage(players, agent.getBoard())));

		if (agent.getBoard().getPlayerTerritories(
						agent.getAgentNames().get(players.get(currentPlayer))).size() == 0){
			players.remove(currentPlayer);
			if( currentPlayer >= players.size())
				currentPlayer=0;
		}
		
		if(players.size() <= 1)
			return;

		currentRound++;
//		System.out.println("Round " + currentRound);
//		System.out.println("Time " + (System.currentTimeMillis() - initialTime));
//		System.out.println("Current Player: " + currentPlayer);
//		System.out.println("Size: " + players.size());

		position.setPlayer(players.get(currentPlayer));
		position.reset();
		position.resetCount();
		attack.setPlayer(players.get(currentPlayer));
		attack.reset();
		attack.resetCount();
		fortify.setPlayer(players.get(currentPlayer));
		fortify.reset();
		fortify.resetCount();

		currentPlayer++;
		if (currentPlayer >= players.size()) {
			currentPlayer = 0;
		}
		agent.notifyTurnEnded();
		agent.setCurrentRound(currentRound);
	}

	@Override
	public boolean done() {
		return true;
	}
	
	@Override
	public int onEnd(){
		if(players.size() <= 1 )
			return FINAL;
		else 
			return CONT;
	}

}

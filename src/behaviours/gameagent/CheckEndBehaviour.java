package behaviours.gameagent;

import up.fe.liacc.sajas.core.behaviours.SimpleBehaviour;

public class CheckEndBehaviour extends SimpleBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5078362659854033870L;
	private int i;
	private GameAgentFaseBehaviour b;
	
	public CheckEndBehaviour(GameAgentFaseBehaviour behaviour){
		i=0;
		b=behaviour;
	}
	
	public void resetCount(){
		i=0;
	}
	
	@Override
	public void action() {
		i++;
	}

	@Override
	public boolean done() {
		return (i>=3);
	}
	
	@Override
	public int onEnd(){
		b.reset();
		if(i>=3){
			System.out.println("Acao invalida");
			return 1;
		}
		else
			return 0;
	}

}

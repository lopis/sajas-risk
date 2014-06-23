package cli;

import gui.BoardGUI;
import gui.FileExporter;
import gui.GameStartChooserGUI;
import gui.GameStartGUI;
import gui.StatsGUI;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;

import up.fe.liacc.repast.Launcher;
import util.R;
import agents.AgressiveAgent;
import agents.DeliberativeAgent;
import agents.GameAgent;
import agents.HumanAgent;
import agents.RandomAgent;
import agents.ReactiveAgent;

public class MyLauncher extends Launcher {
	
	static Runtime runtime;
	private static Launcher container;
	protected static GameAgent gameAgent;
	private static JFrame configFrame;
	private static JFrame initFrame;
	private static JFrame statsFrame;
	private static JFrame gameFrame;
	private static JFrame fileFrame;
	
	@Override
	public void setup() {
		
		container = this;
		
		try { // Set System L&F 
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName()); 
		} catch (Exception e) {
			// handle exception
		}

		initConfigs();
//		configGame();
		

	}

	private static void initConfigs() {
		initFrame = new JFrame("RISK");
		initFrame.setSize(new Dimension(400, 400));
		initFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final GameStartChooserGUI startGameGui = new GameStartChooserGUI();
		initFrame.add(startGameGui);
		initFrame.setVisible(true);
	}

	private static void configGame(int numOfAgents, String appType) {
		configFrame = new JFrame("RISK");
		configFrame.setSize(new Dimension(400, 400));
		configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final GameStartGUI startGameGui = new GameStartGUI(numOfAgents, appType);
		configFrame.add(startGameGui);
		initFrame.setVisible(false);
		configFrame.setVisible(true);
	}

	private static GameAgent setupJADE() {
//		runtime = jade.core.Runtime.instance();
//		Profile profile = new ProfileImpl();
//		profile.setParameter(R.GUI_CONFIG, R.GUI_OFF);
//		profile.setParameter(R.PORT_CONFIG, R.PORT);
		
		gameAgent = new GameAgent();
		container.acceptNewAgent("Board", gameAgent);
		return gameAgent;
	}
	
	public static void startServer(ArrayList<String> agentTypes, String filePrefix) {
		createStatsFrame();
		createGameFrame();
		createFilePrinter(filePrefix);
		
		ArrayList<String> names = util.NameGenerator.randomName(agentTypes.size());
		for (int i = 0; i < agentTypes.size(); i++) {
			addAgent(names.get(i), agentTypes.get(i));
		}

		gameFrame.setVisible(true);
		statsFrame.setVisible(true);
		configFrame.setVisible(false);
	}

	private static void createFilePrinter(String filePrefix) {
		fileFrame = new JFrame("RISK");
		fileFrame.setSize(new Dimension(StatsGUI.PANEL_WIDTH, StatsGUI.PANEL_HEIGHT));
		fileFrame.setMinimumSize(new Dimension(StatsGUI.PANEL_WIDTH, StatsGUI.PANEL_HEIGHT));
		fileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final FileExporter gui = new FileExporter(gameAgent, filePrefix);
		fileFrame.add(gui);
		fileFrame.setVisible(false);
		
	}

	private static void createStatsFrame() {
		statsFrame = new JFrame("RISK - Stats");
		statsFrame.setSize(new Dimension(StatsGUI.PANEL_WIDTH, StatsGUI.PANEL_HEIGHT));
		statsFrame.setMinimumSize(new Dimension(StatsGUI.PANEL_WIDTH, StatsGUI.PANEL_HEIGHT));
		statsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final StatsGUI gui = new StatsGUI(gameAgent);
		statsFrame.add(gui);
		
	}

	private static void createGameFrame() {
		gameFrame = new JFrame("RISK");
		gameFrame.setSize(new Dimension(BoardGUI.PANEL_WIDTH, BoardGUI.PANEL_HEIGHT));
		gameFrame.setMinimumSize(new Dimension(BoardGUI.PANEL_WIDTH, BoardGUI.PANEL_HEIGHT));
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final BoardGUI gui = new BoardGUI(gameAgent);
		gameFrame.add(gui);
		
	}

	private static void addAgent(String name, String type) {
		if(type.equals("Random")) {
			container.acceptNewAgent(name + "-Rand", new agents.PlayerAgent(new RandomAgent()));
		} else if(type.equals("Human")) {
			container.acceptNewAgent(name, new agents.PlayerAgent(new HumanAgent()));
		} else if(type.equals("Agressive")) {
			container.acceptNewAgent(name + "-A", new agents.PlayerAgent(new AgressiveAgent()));
		} else if(type.equals("Reactive")) {
			container.acceptNewAgent(name + "-React", new agents.PlayerAgent(new ReactiveAgent(5)));
		} else if(type.equals("Deliberative")){
			container.acceptNewAgent(name + "-D", new agents.PlayerAgent(new DeliberativeAgent()));
		}
	}

	public static void setupClient(int numOfAgents, String ip, String port) {
		setupClientJADE(ip, port);
		configGame(numOfAgents, "client");
	}

	private static void setupClientJADE(String ip, String port) {

	}

	public static void setupServer(int numOfAgents) {
		GameAgent gameAgent = setupJADE();
		configGame(numOfAgents, "server");
	}

	public static void startClient(ArrayList<String> agentTypes, String text) {
		ArrayList<String> names = util.NameGenerator.randomName(agentTypes.size());
		for (int i = 0; i < agentTypes.size(); i++) {
			addAgent("Client-" + names.get(i), agentTypes.get(i));
		}
	}



}

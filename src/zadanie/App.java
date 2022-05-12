package zadanie;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

/**
 *   Program: Program prezentuj¹cy przyk³adowe wykorzystanie w¹tków i dzia³ania wspó³bie¿nego
 * 	    Plik: App.java
 * 			  g³ówne okienko aplikacji, w którym wyœwietlane s¹ wszystkie 
 * 			  "logi" - komunikaty od poszczególnych busów.
 * 			  W tym oknie mo¿liwa jest zmiana natê¿enia ruchu oraz wyœwietlane
 * 			  s¹ numery busów w kolejce oraz bezpoœrednio na moœcie.
 * 			
 *     Autor: Jakub Derda
 * nr albumu: 252819
 *      Data: 13.01.2021
 *Data zajêæ: 11.01.2021
 */

public class App extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final String GREETING_MESSAGE = 
			"Program prezentuj¹cy przyk³adowe wykorzystanie w¹tków i dzia³ania wspó³bie¿nego" +
			" - wersja okienkowa\n\n"+
			"Autor: Jakub Derda\n" + 
			"Data: 10.12.2020";
	
	private static final int MIN_BUS_DELAY = 500;
    private static final int MAX_BUS_DELAY = 5000;
    private static final int INITIAL_BUS_DELAY = 1000;
	
    private static int TRAFFIC = INITIAL_BUS_DELAY;
    
	NarrowBridge bridge;
	
	int busesOnTheBridge = 0;
	
	BusDirection currentDir = BusDirection.WEST;
	
	
	//GUI
	// Font dla etykiet o sta³ej szerokoœci znaków
	Font font = new Font("MonoSpaced", Font.BOLD, 12);
	
	//ETYKIETY
	JLabel trafficRestriction        = new JLabel("Ograniczenie ruchu:");
	JLabel traffic                   = new JLabel("   Natê¿enie ruchu:");
	JLabel onTheBridge               = new JLabel("         Na moœcie:");
	JLabel queue                     = new JLabel("           Kolejka:");
	
	//LISTA ROZWIJALNA
	JComboBox<Options> options        = new JComboBox<Options>();
	
	//POLA TEKSTOWE
	JTextField bridgeField           = new JTextField(20);
	JTextField queueField            = new JTextField(20);
 	
	//SUWAK
	static JSlider slider = new JSlider(JSlider.HORIZONTAL,MIN_BUS_DELAY,MAX_BUS_DELAY,TRAFFIC);
	
	//POLE NA MENU
	JMenuBar menuBar                 = new JMenuBar();
	
	//MENU
	JMenu menu                       = new JMenu("Menu");
	
	//ELEMENTY MENU
	JMenuItem menuAuthor		     = new JMenuItem("Informacje o autorze");
	JMenuItem menuEnd		         = new JMenuItem("Zakoñcz");
	
	
	//POLE Z KOMUNIKATAMI
	JTextArea textArea = new JTextArea(20,50);
	JScrollPane scrollPane; 
	
	Animation animation;
	
	
	public static void main(String[] args) {
		NarrowBridge bridge = new NarrowBridge();
		App app = new App(bridge);
		
		while(true) {
			Bus bus = new Bus(app);
			new Thread(bus).start();
			try {
				Thread.sleep(5500 - TRAFFIC);
			} catch(InterruptedException e) {}
		}
	}
	
	
	//KONSTRUKTOR OKIENKA
	public App(NarrowBridge bridge) {
		
		this.bridge = bridge;
		
		setTitle("Symulacja przejazdu przez most");
		setSize(400,550);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocation(10,10);
		
		//Elementy do listy rozwijalnej:
		for(Options opp : Options.values()) {
			options.addItem(opp);
		}
		
		options.setEditable(true);
		
		slider.setMinorTickSpacing(250);
		slider.setMajorTickSpacing(1000);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(MIN_BUS_DELAY, new JLabel("Ma³o"));
        labelTable.put(MAX_BUS_DELAY, new JLabel("Du¿o"));
        slider.setLabelTable(labelTable);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                App.TRAFFIC = slider.getValue();
            }
        });
		
        
        
		//Jednakowy Font dla wszystkich
		trafficRestriction.setFont(font);
		traffic.setFont(font);
		onTheBridge.setFont(font);
		queue.setFont(font);
		textArea.setFont(font);
		
		//Menu
		setJMenuBar(menuBar);
		menuBar.add(menu);
		
		menu.add(menuAuthor);
		menu.add(menuEnd);
		
		menuAuthor.addActionListener(this);
		menuEnd.addActionListener(this);
		
		options.setSelectedItem((Object)this.bridge.typeOfBridge);
		
		options.addActionListener(a -> {
			bridge.typeOfBridge = (Options)((Object)((Object)this.options.getSelectedItem()));
		});
		
		JPanel panel = new JPanel();
		
		panel.add(trafficRestriction);
		panel.add(options);
		panel.add(traffic);
		panel.add(slider);
		panel.add(onTheBridge);
		panel.add(bridgeField);
		panel.add(queue);
		panel.add(queueField);
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		scrollPane = new JScrollPane(textArea,22,30);
		
		panel.add(scrollPane);
		
		
		
		
		setContentPane(panel);
		
		animation = new Animation(this);
		
		setVisible(true);
	}

	
	
	void printBridgeInfo(Bus bus, String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("Bus["+bus.id+"->"+bus.dir+"]  ");
		sb.append(message+"\n");
		textArea.insert(sb.toString(), 0);
		
		sb = new StringBuilder();
		for(Bus b : this.bridge.busesWaiting) {
			sb.append(b.id + " ");
		}
		queueField.setText(sb.toString());
		
		sb = new StringBuilder();
		for(Bus b : this.bridge.busesOnTheBridge) {
			sb.append(b.id + " ");
		}
		bridgeField.setText(sb.toString());
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		Object source = event.getSource();
		
		if(source == menuAuthor) {
			JOptionPane.showMessageDialog(this, GREETING_MESSAGE);
		}
	
		if(source == menuEnd) {
			System.exit(0);
		}
	}

	
	synchronized void getOnTheBridge(Bus bus) {
		//Zmiana stanu
		synchronized(bus) {
			bus.time = System.currentTimeMillis();
			bus.state = BusState.GET_ON_BRIDGE;
		}
		
		boolean message = true;
		
		block : while(true) {
			switch(bridge.getTypeOfBridge()) {
			case WITHOUT_RESTRICTIONS:
				break block;
			case ONE_CAR_TRAFFIC:
				if(!bridge.busesOnTheBridge.isEmpty()) {
					break;
				}
				break block;
			case ONE_WAY_TRAFFIC:
				if(bridge.busesOnTheBridge.isEmpty() && bridge.busesWaiting.isEmpty()) {
					busesOnTheBridge = 0;
					break block;
				}
				if(!(bridge.busesOnTheBridge.isEmpty() ? bus.dir != currentDir || bus.dir == currentDir
						&& busesOnTheBridge < 10 : bus.dir == currentDir && busesOnTheBridge <10 && bridge.busesOnTheBridge.size()<3)) {
					break;
				}
				break block;
			case TWO_WAY_TRAFFIC:
				if(bridge.busesOnTheBridge.size()<3) {
					break block;
				}
			}
			
			this.bridge.busesWaiting.add(bus);
			if(message) {
				printBridgeInfo(bus, "CZEKA NA WJAZD");
				message = false;
			}
			
			try {
				wait();
			} catch(InterruptedException e) {}
			
			bridge.busesWaiting.remove(bus);
		}
		
		if (currentDir == bus.dir) {
            ++busesOnTheBridge;
        } else {
            currentDir = bus.dir;
            busesOnTheBridge = 1;
        }
		
		bridge.busesOnTheBridge.add(bus);
		printBridgeInfo(bus, "WJE¯D¯A NA MOST");
	}
	
	synchronized void getOffTheBridge(Bus bus) {
		synchronized(bus) {
			bus.time = System.currentTimeMillis();
			bus.state = BusState.GET_OFF_BRIDGE;
		}
		bridge.busesOnTheBridge.remove(bus);
		printBridgeInfo(bus, "OPUSZCZA MOST");
		notifyAll();
	}
	
}

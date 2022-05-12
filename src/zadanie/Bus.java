package zadanie;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.ThreadLocalRandom;

/**
 *   Program: Program prezentujący przykładowe wykorzystanie wątków i działania współbieżnego
 * 	    Plik: Bus.java
 * 			  Model danych, w którym znajduje się implementacja
 *            wszystkich cech busa oraz napisana jest metoda obliczająca
 *            współrzędne autobusu na potrzeby animacji.
 * 			
 *     Autor: Jakub Derda
 * nr albumu: 252819
 *      Data: 13.01.2021
 *Data zajęć: 11.01.2021
 */

enum BusDirection{
	EAST,
	WEST;
	
	@Override
	public String toString(){
		if(this == EAST) {
			return "W";
		} else if(this == WEST){
			return "Z";
		} else {
			return "";
		}
	}
}


enum BusState{
	BOARDING("BOARDING"),
	GOING_TO_BRIDGE("GOING_TO_BRIDGE"),
	GET_ON_BRIDGE("GET_ON_BRIDGE"), 
    RIDE_BRIDGE("RIDE_BRIDGE"), 
    GET_OFF_BRIDGE("GET_OFF_BRIDGE"), 
    GOING_TO_PARKING("GOING_TO_PARKING"), 
    UNLOADING("UNLOADING");
	
	String str;
	
	private BusState(String str) {
		this.str = str;
	}
}

public class Bus implements Runnable {

	//Licznik busów
	private static int numberOfBuses = 0;
	
	//Minimalny i maksymalny czas oczekiwania na pasażerów
	public static final int MIN_BOARDING_TIME = 1000;
	public static final int MAX_BOARDING_TIME = 10000;
	
	//Czas dojazdu busa do mostu
	public static final int GETTING_TO_BRIDGE_TIME = 5000;
	
	//Czas przejazdu przez most
	public static final int CROSSING_BRIDGE_TIME = 3000;
	
	//Czas przejazdu od mostu do końcowego parkingu
	public static final int GETTING_PARKING_TIME = 5000;
	
	//Czas wysiadania pasażerów z busa
	public static final int UNLOADING_TIME = 2000;
	
	//Unikalny identyfikator busa, który jest numerem busa w kolejności tworzenia
	int id;
	
	//Kierunek jazdy busa nadany w sposób losowy
	BusDirection dir;
	
	//Referencja na obiekt mostu
	//NarrowBridge bridge;
	
	//Referencja do klasy okienka
	App window;
	
	//Czas w milisekundach
	public long time;
	
	//Stan podróży busa
	BusState state;
	
	//Czas odniesienia dla obliczeń:
	private long referenceTime1 = 0;
	private long referenceTime2 = 0;
	private long referenceTime3 = 0;
	
	Bus(App window){
		this.time = System.currentTimeMillis();
		//this.bridge = bridge;
		this.window = window;
		this.id = ++numberOfBuses;
		if (ThreadLocalRandom.current().nextInt(0, 2) == 0)
			this.dir = BusDirection.EAST;
		else this.dir = BusDirection.WEST;
	}
	
	Bus(App window, BusDirection dir){
		this.time = System.currentTimeMillis();
		//this.bridge = bridge;
		this.window = window;
		this.id = ++numberOfBuses;
		this.dir = dir;
	}
	
	public long calculatePosition(long time) {
		
		if(this.dir.equals(BusDirection.EAST)) {
			
			switch(this.state) {
			
			case BOARDING:
				return 10;
			
			case GOING_TO_BRIDGE:
				
				if(this.referenceTime1 == 0) {
					this.referenceTime1 = time;
				} else {
				
				//Z definicji ruchu jednostajnego prostoliniowego:
				// V = s/t
				//Znam drogę: s = 280 pikseli
				//Znam czas: t = System.currentTimeMillis() - referenceTime
				//Całkowity czas: GETTING_TO_BRIDGE_TIME
				//Zatem prędkość: V = 280/GETTING_TO_BRIDGE_TIME
				//I obliczam przebytą drogę:
				// s = V * t;
				
					float V = (float) (280.0/(float)GETTING_TO_BRIDGE_TIME);
			
					long t = time - this.referenceTime1;
			
					float s = V*t;
				
					return (int)s+10;
				}
					
			case GET_ON_BRIDGE:
				return 290;
			
			case RIDE_BRIDGE:
				if(this.referenceTime2 == 0) {
					this.referenceTime2 = time;
				} else {
					float V = (float) (450.0/(float)GETTING_TO_BRIDGE_TIME);
					
					long t = time - this.referenceTime2;
			
					float s = V*t;
				
					return (int)s+290;
				}
			
			case GET_OFF_BRIDGE:
				return 740;
			
			case GOING_TO_PARKING:
				if(this.referenceTime3 == 0) {
					this.referenceTime3 = time;
				} else {
					float V = (float) (280.0/(float)GETTING_TO_BRIDGE_TIME);
					
					long t = time - this.referenceTime3;
			
					float s = V*t;
				
					return (int)s+560;
				}
			
			case UNLOADING:
				return window.animation.getWidth() - 70;
				
			default:
				break;
			}
		} else {
			
			switch(this.state) {
			
			case BOARDING:
				return window.animation.getWidth() - 70;
			
			case GOING_TO_BRIDGE:
				if(this.referenceTime1 == 0) {
					this.referenceTime1 = time;
				} else {
				
				//Z definicji ruchu jednostajnego prostoliniowego:
				// V = s/t
				//Znam drogę: s = 280 pikseli
				//Znam czas: t = System.currentTimeMillis() - referenceTime
				//Całkowity czas: GETTING_TO_BRIDGE_TIME
				//Zatem prędkość: V = 280/GETTING_TO_BRIDGE_TIME
				//I obliczam przebytą drogę:
				// s = V * t;
				
					float V = (float) (280.0/(float)GETTING_TO_BRIDGE_TIME);
			
					long t = time - this.referenceTime1;
			
					float s = V*t;
				
					return window.animation.getWidth() - 70 - (int)s;
				}
			
			case GET_ON_BRIDGE:
				return window.animation.getWidth() - 350;
			
			case RIDE_BRIDGE:
				if(this.referenceTime2 == 0) {
					this.referenceTime2 = time;
				} else {
					float V = (float) (430.0/(float)GETTING_TO_BRIDGE_TIME);
					
					long t = time - this.referenceTime2;
			
					float s = V*t;
				
					return window.animation.getWidth() - 360 - (int)s ;
				}
			
			case GET_OFF_BRIDGE:
				return window.animation.getWidth() - 790;
			
			case GOING_TO_PARKING:
				if(this.referenceTime3 == 0) {
					this.referenceTime3 = time;
				} else {
					float V = (float) (280.0/(float)GETTING_TO_BRIDGE_TIME);
					
					long t = time - this.referenceTime3;
			
					float s = V*t;
				
					return window.animation.getWidth() - 620 - (int)s;
				}
			
			case UNLOADING:
				return 10;
				
			default:
				break;
			}
		}
		
		return 0;
	}
	
	//Metoda, która usypia wątek na podany czas w milisekundach
	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) { }
	}
	
	//Metoda, która usypia wątek na losowo dobrany czas z przedziału 
	// [min,max) milisekund
	public static void sleep(int min_millis, int max_milis) {
		sleep(ThreadLocalRandom.current().nextInt(min_millis, max_milis));
	}
	
	
	
	
	
	// Wydruk w konsoli informacji o stanie busa
	void printBusInfo(String message){
		String str = "Bus[" + id + "->"+dir+"]: " + message + "\n";
		this.window.textArea.insert(str,0);
	}
	
	// Symulacja oczekiwania na nowych pasażerów.
	void boarding() {
		//Zmiana stanu, zatem w blok synchronizowany
		synchronized(this) {
			time = System.currentTimeMillis();
			this.state = BusState.BOARDING;
		}
		printBusInfo("Czeka na nowych pasazerow");
		sleep(MIN_BOARDING_TIME, MAX_BOARDING_TIME);
	}

	// Symulacja dojazdu ze stacji początkowej do mostu
	void goToTheBridge() {
		//Zmiana stanu, zatem w blok synchronizowany
		synchronized(this) {
			time = System.currentTimeMillis();
			this.state = BusState.GOING_TO_BRIDGE;
		}
		printBusInfo("Jazda w strone mostu");
		sleep(GETTING_TO_BRIDGE_TIME);
	}

	// Symulacja przejazdu przez most
	void rideTheBridge(){
		//Zmiana stanu, zatem w blok synchronizowany
		synchronized(this) {
			time = System.currentTimeMillis();
			this.state = BusState.RIDE_BRIDGE;
		}
		printBusInfo("Przejazd przez most");
		sleep(CROSSING_BRIDGE_TIME);
	}

	// Symulacja przejazdu od mostu do końcowego parkingu
	void goToTheParking(){
		//Zmiana stanu, zatem w blok synchronizowany
		synchronized(this) {
			time = System.currentTimeMillis();
			this.state = BusState.GOING_TO_PARKING;
		}
		printBusInfo("Jazda w stronę koncowego parkingu");
		sleep(GETTING_PARKING_TIME);
	}
		
	// Symulacja opuszczenia pojazdu na przystanku końcowym
	void unloading(){
		//Zmiana stanu, zatem w blok synchronizowany
		synchronized(this) {
			time = System.currentTimeMillis();
			this.state = BusState.UNLOADING;
		}
		printBusInfo("Rozladunek pasazerów");
		sleep(UNLOADING_TIME);
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//Zmiana stanu, zatem w blok synchronizowany
		synchronized(window.bridge.allBuses) {
			window.bridge.allBuses.add(this);
		}
		
		boarding();
		
		goToTheBridge();
		
		window.getOnTheBridge(this);
		
		rideTheBridge();
		
		window.getOffTheBridge(this);
		
		goToTheParking();
		
		unloading();
		
		//koniec życia wątku
		//Zmiana stanu, zatem w blok synchronizowany
		synchronized(window.bridge.allBuses) {
			window.bridge.allBuses.remove(this);
		}
	}
	
	void draw(Graphics2D g2, int x, int y) {
		
		if(this.dir.equals(BusDirection.EAST)) {
			//Kadłub
			if(this.state.equals(BusState.GET_ON_BRIDGE)) {
				g2.setColor(Color.YELLOW);
			} else {
				g2.setColor(Color.GREEN);
			}
			g2.fillRect(x, y, 60, 26);
			g2.setColor(Color.BLACK);
			g2.drawRect(x, y, 60, 26);

			//Okienko kierowcy
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRect(x+54, y, 6, 13);
			g2.setColor(Color.BLACK);
			g2.drawRect(x+54, y, 6, 13);
		
			//Koła
			g2.fillOval(x+10, y+22, 8, 8);
			g2.fillOval(x+46, y+22, 8, 8);
		
			//Światło przednie:
			g2.setColor(Color.YELLOW);
			g2.fillRect(x+57, y+22, 3, 4);
			g2.setColor(Color.BLACK);
			g2.drawRect(x+57, y+22, 3, 4);
		
			//Światło tylne
			g2.setColor(Color.RED);
			g2.fillRect(x, y, 3, 8);
			g2.setColor(Color.BLACK);
			g2.drawRect(x, y, 3, 8);
		
			g2.drawString(""+this.id, x+25, y+17);
		} else {
			//Kadłub
			if(this.state.equals(BusState.GET_ON_BRIDGE)) {
				g2.setColor(Color.YELLOW);
			} else {
				g2.setColor(Color.ORANGE);
			}
			g2.fillRect(x, y, 60, 26);
			g2.setColor(Color.BLACK);
			g2.drawRect(x, y, 60, 26);
			
			//Okienko kierowcy
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRect(x, y, 6, 13);
			g2.setColor(Color.BLACK);
			g2.drawRect(x, y, 6, 13);
			
			//Koła
			g2.fillOval(x+7, y+22, 8, 8);
			g2.fillOval(x+42, y+22, 8, 8);
			
			//Światło przednie:
			g2.setColor(Color.YELLOW);
			g2.fillRect(x, y+22, 3, 4);
			g2.setColor(Color.BLACK);
			g2.drawRect(x, y+22, 3, 4);
			
			//Światło tylne
			g2.setColor(Color.RED);
			g2.fillRect(x+57, y, 3, 8);
			g2.setColor(Color.BLACK);
			g2.drawRect(x+57, y, 3, 8);
			
			g2.drawString(""+this.id, x+25, y+17);
		}	
	}
}

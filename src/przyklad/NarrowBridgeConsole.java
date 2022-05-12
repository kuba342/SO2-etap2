package przyklad;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/* 
 *  Symulacja problemu przejazdu przez w�ski most
 *  Wersja konsolowa
 *
 *  Autor: Paweł Rogaliński
 *   Data: 1 grudnia 2019 r.
 */

public class NarrowBridgeConsole{
	
	// Parametr TRAFFIC okre�la nat�enie ruchu bus�w.
	// Mo�e przyjmowa� warto�ci z przedzia�u [0, 5000]
	//    0 - bardzo ma�e nat�enie (nowy bus co 5500 ms)
	// 5000 - bardzo du�e nat�enie (nowy bus co 500 ms )
	private static int TRAFFIC = 1000;
	
	public static void main(String[] args) {
		NarrowBridge bridge = new NarrowBridge();

		// Zadaniem tej p�tli jest tworzenie kolejnych bus�w,
		// kt�re maj� przewozi� przez most pasa�er�w:
		// Przerwy pomi�dzy kolejnymi busami s� generowane losowo.
		while (true) {
			// Utworzenie nowego busa i uruchomienie w�tku,
			// kt�ry symuluje przejazd busa przez most.
			Bus bus = new Bus(bridge);
			new Thread(bus).start();

			// Przerwa przed utworzeniem kolejnego busa
			try {
				Thread.sleep(5500 - TRAFFIC);
			} catch (InterruptedException e) {
			}
		}
	}
		
}  // koniec klasy NarrowBridgeConsole


enum BusDirection{
	EAST,
	WEST;
	
	@Override
	public String toString(){
		switch(this){
		case EAST: return "W";
		case WEST: return "Z";
		}
		return "";
	}
} // koniec typu wyliczeniowego BusDirection



class NarrowBridge {
	// Lista wszystkich bus�w, kt�rych w�tki aktualnie dzia�aj�
	List<Bus> allBuses = new LinkedList<Bus>();
	
	// Lista bus�w (kolejka) oczekuj�cych na wjazd na most
	List<Bus> busesWaiting = new LinkedList<Bus>();
	
	// Lista bus�w poruszaj�cych si� aktualnie po mo�cie
	List<Bus> busesOnTheBridge = new LinkedList<Bus>();

	// Wydruk informacji o busach oczekuj�cych w kolejce oraz
	// aktualnie przeje�d�aj�cych przez most. 
	void printBridgeInfo(Bus bus, String message){
		StringBuilder sb = new StringBuilder();
		sb.append("Bus["+bus.id+"->"+bus.dir+"]  ");
		sb.append(message+"\n");
		sb.append("           Na mo�cie: ");
		for(Bus b: busesOnTheBridge) sb.append(b.id + "  "); 
		sb.append("    Kolejka: ");
		for(Bus b: busesWaiting) sb.append(b.id + "  ");
		System.out.println(sb);
	}
	
	// Procedura monitora, kt�ry wpuszcza busy na most
	synchronized void getOnTheBridge(Bus bus){
		// Prosty warunek wjazudu na most:
		// DOP�KI LISTA BUS�W NA MO�CIE NIE JEST PUSTA
		// KOLEJNY BUS MUSI CZEKA� NA WJAZD
		while( !busesOnTheBridge.isEmpty()){
			// dodanie busa do listy oczekuj�cych
			busesWaiting.add(bus);
			printBridgeInfo(bus, "CZEKA NA WJAZD");
			try {
				wait();
			} catch (InterruptedException e) { }
			// usuni�cie busa z listy oczekuj�cych.
			busesWaiting.remove(bus);
		}
		// dodanie busa do listy jad�cych przez most
		busesOnTheBridge.add(bus);
		printBridgeInfo(bus, "WJE�D�A NA MOST");
	}
	
	// Procedura monitora, kt�ra rejestruje busy opuszczaj�ce most
	// i powiadamia inne busy oczekuj�ce w kolejce na wjazd
	synchronized void getOffTheBridge(Bus bus){
		// usuni�cie busa z listy poruszaj�cych si� przez most
		busesOnTheBridge.remove(bus);
		printBridgeInfo(bus, "OPUSZCZA MOST");
		// powiadomienie innych oczekuj�cych.
		notify();
	}

}  // koniec klasy NarrowBridge




class Bus implements Runnable {

	// Sta�e okre�laj�ce minimalny i maksymalny czas
	// oczeiwania na nowych pasa�er�w.
	public static final int MIN_BOARDING_TIME = 1000;
	public static final int MAX_BOARDING_TIME = 10000;

	// Sta�a okre�laj�ca czas dojazdu busa do mostu.
	public static final int GETTING_TO_BRIDGE_TIME = 500;
	
	// Sta�a okre�laj�ca czas przejazdu przez most.
	public static final int CROSSING_BRIDGE_TIME = 3000;
	
	// Sta�a okre�laj�ca czas przjezdu od mostu do ko�cowego parkingu.
	public static final int GETTING_PARKING_TIME = 500;
	
	// Sta�a okre�laj�ca czas wysiadania pasa�er�w z busa
	public static final int UNLOADING_TIME = 500;
	
	
	// Liczba wszystkich bus�w, kt�re zosta�u utworzone
	// od pocz�tku dzia�ania programu
	private static int numberOfBuses = 0;
	
	
	// Metoda usypia w�tek na podany czas w milisekundach
	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	// Metoda usypia w�tek na losowo dobrany czas
	// z przedzia�u [min, max) milsekund
	public static void sleep(int min_millis, int max_milis) {
		sleep(ThreadLocalRandom.current().nextInt(min_millis, max_milis));
	}
	
	
	// Referencja na obiekt reprezentuj�cy most.
	NarrowBridge bridge;
	
	// Unikalny identyfikator ka�dego busa. 
	// Jako identyfikator zostanie u�yty numer busa,
	// kt�ry zosta� utworzony od pocz�tku dzia�ania programu
	int id;
	
	// Kierunek jazdy busa nadany w spos�b losowy
	BusDirection dir;
	
	
	Bus(NarrowBridge bridge){
		this.bridge = bridge;
		this.id = ++numberOfBuses;
		if (ThreadLocalRandom.current().nextInt(0, 2) == 0)
			this.dir = BusDirection.EAST;
		else this.dir = BusDirection.WEST;
	}
	
	
	// Wydruk w konsoli informacji o stanie busa
	void printBusInfo(String message){
		System.out.println("Bus[" + id + "->"+dir+"]: " + message);
	}
	
	
	// Symulacja oczekiwania na nowych pasa�er�w.
	void boarding() {
		printBusInfo("Czeka na nowych pasa�er�w");
		sleep(MIN_BOARDING_TIME, MAX_BOARDING_TIME);
	}

	// Symulacja dojazdu ze stacji pocz�tkowej do mostu
	void goToTheBridge() {
		printBusInfo("Jazda w stron� mostu");
		sleep(GETTING_TO_BRIDGE_TIME);
	}

	// Symulacja przejazdu przez most
	void rideTheBridge(){
		printBusInfo("Przejazd przez most");
		sleep(CROSSING_BRIDGE_TIME);
	}

	// Symulacja przejazdu od mostu do ko�cowego parkingu
	void goToTheParking(){
		printBusInfo("Jazda w stron� ko�cowego parkingu");
		sleep(GETTING_PARKING_TIME);
	}
	
	// Symulacja opuszczenia pojazdu na przystanku ko�cowym
	void unloading(){
		printBusInfo("Roz�adunek pasa�er�w");
		sleep(UNLOADING_TIME);
	}

	
	// Metoda realizuje "cykl �ycia" pojedynczego busa
	public void run() {
		bridge.allBuses.add(this);
		
		// oczekiwanie na nowych pasa�er�w
		boarding();

		// jazda w kierunku mostu
		goToTheBridge();

		// 
		bridge.getOnTheBridge(this);

		// przejazd przez most
		rideTheBridge();

		bridge.getOffTheBridge(this);

		// jazda w kierunku parkingu ko�cowego
		goToTheParking();

		// wypuszczenie pasa�er�w
		unloading();
		bridge.allBuses.remove(this);
		
		// koniec "�ycia" w�tku
		bridge.allBuses.remove(this);
	}

}  // koniec klasy Bus
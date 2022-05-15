package zadanie;

import java.util.LinkedList;
import java.util.List;

/**
 *   Program: Program prezentujący przykładowe wykorzystanie wątków i działania współbieżnego
 * 	    Plik: NarrowBridge.java
 * 			  Model danych, w którym znajduje się implementacja
 *            wszystkich cech mostu - klasa kontener do przechowywania
 *            referencji poszczególnych busów. Ponadto klasa ta posiada
 *            informację o ograniczeniach ruchu przez most.
 * 			
 *     Autor: Jakub Derda
 * nr albumu: 252819
 *      Data: 15.05.2022
 */

enum Options{
	
	WITHOUT_RESTRICTIONS("Ruch bez ograniczen"),
	TWO_WAY_TRAFFIC("Ruch dwukierunkowy"),
	ONE_WAY_TRAFFIC("Ruch jednokierunkowy"),
	ONE_CAR_TRAFFIC("Tylko jedno auto na moscie");
	
	String option;
	
	private Options(String option) {
		this.option = option;
	}
	
	@Override
	public String toString() {
		return option;
	}
}

public class NarrowBridge {
	
	//Lista wszystkich busów, których wątki aktualnie działają
	List<Bus> allBuses = new LinkedList<Bus>();
	
	//Kolejka busów oczekujących na wjazd na most
	List<Bus> busesWaiting = new LinkedList<Bus>();
	
	//Lista busów na moście
	List<Bus> busesOnTheBridge = new LinkedList<Bus>();
	
	//Sposób poruszania się po moście
	Options typeOfBridge;
	
	public NarrowBridge() {
		setTypeOfBridge(Options.ONE_CAR_TRAFFIC);
	}
	
	public void setTypeOfBridge(Options option) {
		this.typeOfBridge = option;
	}
	
	public Options getTypeOfBridge() {
		return this.typeOfBridge;
	}
}

package przyklad;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/* 
 *  Problem producenta i konsumenta
 *
 *  Autor: Pawe� Rogali�ski
 *   Data: 1 listopada 2017 r.
 */


abstract class  Worker extends Thread {
	
	// Metoda usypia w�tek na podany czas w milisekundach
	public static void sleep(int millis){
		try {
			Thread.sleep(millis);
			} catch (InterruptedException e) { }
	}
	
	// Metoda usypia w�tek na losowo dobrany czas z przedzia�u [min, max) milsekund
	public static void sleep(int min_millis, int max_milis){
		sleep(ThreadLocalRandom.current().nextInt(min_millis, max_milis));
	}
	
	// Unikalny identyfikator przedmiotu wyprodukowanego
	// przez producenta i zu�ytego przez konsumenta
	// Ten identyfikator jest wsp�lny dla wszystkich producent�w
	// i b�dzie zwi�kszany przy produkcji ka�dego nowego przedmiotu
	static int itemID = 0;
	
	// Minimalny i maksymalny czas produkcji przedmiotu
	public static int MIN_PRODUCER_TIME = 100;
	public static int MAX_PRODUCER_TIME = 1000;
	
	// Minimalny i maksymalny czas konsumpcji (zu�ycia) przedmiotu
	public static int MIN_CONSUMER_TIME = 100;
	public static int MAX_CONSUMER_TIME = 1000;
	
	
	String name;
	Buffer buffer;
	
	@Override
	public abstract void run();
}


class Producer extends Worker {

	public Producer(String name , Buffer buffer){ 
		this.name = name;
		this.buffer = buffer;
	}
	
	@Override
	public void run(){ 
		int item;
		while(true){
			// Producent "produkuje" nowy przedmiot.
			item = itemID++;
			System.out.println("Producent <" + name + ">   produkuje: " + item);
			sleep(MIN_PRODUCER_TIME, MAX_PRODUCER_TIME);
			
			// Producent umieszcza przedmiot w buforze.
			buffer.put(this, item);
		}
	}
	
} // koniec klasy Producer


class Consumer extends Worker {
	
	public Consumer(String name , Buffer buffer){ 
		this.name = name;
		this.buffer = buffer;
	}

	@Override
	public void run(){ 
		int item;
		while(true){
			// Konsument pobiera przedmiot z bufora
			item = buffer.get(this);
			
			// Konsument zu�ywa popraany przedmiot.
			sleep(MIN_CONSUMER_TIME, MAX_CONSUMER_TIME);
			System.out.println("Konsument <" + name + ">       zu�y�: " + item);
		}
	}
	
} // koniec klasy Consumer


class Buffer {
	
	private int contents;
	private boolean available = false;

	public synchronized int get(Consumer consumer){
		System.out.println("Konsument <" + consumer.name + "> chce zabrac");
		while (available == false){
			try { System.out.println("Konsument <" + consumer.name + ">   bufor pusty - czekam");
				  wait();
				} catch (InterruptedException e) { }
		}
		int item = contents;
		available = false;
		System.out.println("Konsument <" + consumer.name + ">      zabral: " + contents);
		notifyAll();
		return item;
	}

	public synchronized void put(Producer producer, int item){
		System.out.println("Producent <" + producer.name + ">  chce oddac: " + item);
		while (available == true){
			try { System.out.println("Producent <" + producer.name + ">   bufor zajety - czekam");
				  wait();
				} catch (InterruptedException e) { }
		}
		contents = item;
		available = true;
		System.out.println("Producent <" + producer.name + ">       oddal: " + item);
		notifyAll();
	}
	
} // koniec klasy Buffer


public class ProducerConsumerTest{

	public static void main(String[] args){
		Buffer buffer = new Buffer();
		Producer p1 = new Producer("P1", buffer);
		Consumer c1 = new Consumer("K1", buffer);
		Producer p2 = new Producer("P2", buffer);
		Consumer c2 = new Consumer("K2", buffer);
		p1.start();
		c1.start();
		p2.start();
		c2.start();
		try { Thread.sleep( 5000 );
			} catch (InterruptedException e) { }
		System.exit(0);
	}
	
} // koniec klasy ProducerConsumerTest
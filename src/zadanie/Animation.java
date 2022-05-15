package zadanie;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *   Program: Program prezentujący przykładowe wykorzystanie wątków i działania współbieżnego
 * 	    Plik: Animation.java
 * 			  Drugie okienko aplikacji, które jest tylko i wyłącznie
 * 			  implementacją animacji do działań wykonywanych przez poszczególne wątki.
 * 			
 *     Autor: Jakub Derda
 * nr albumu: 252819
 *      Data: 15.05.2022
 */

public class Animation extends JPanel implements Runnable{

	private static final long serialVersionUID = 1L;

	App application;
	
	JFrame animationWindow          = new JFrame();
	
	public Animation(App application) {
		this.application = application;
		animationWindow.setTitle("Animacja");
		animationWindow.setSize(920, 800);
		animationWindow.setResizable(false);
		animationWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		animationWindow.setLocationRelativeTo(application);
		animationWindow.setLocation(application.getWidth()+5 ,10);
		animationWindow.setContentPane(this);
		animationWindow.setVisible(true);
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized(this) {
			while(true) {
				this.repaint();
				try {
					Thread.sleep(20L);
				} catch(InterruptedException e) {}
			}
		}
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		//Parkingi
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, 80, this.getHeight());
		g.fillRect(this.getWidth()-80, 0, 80, this.getHeight());
		
		//Droga
		g.setColor(Color.GRAY);
		g.fillRect(80, 0, 200, this.getHeight());
		g.fillRect(this.getWidth()-280, 0, 200, this.getHeight());
		
		//Bramki przed mostem:
		g.setColor(Color.WHITE);
		g.fillRect(280, 0, 80, this.getHeight());
		g.fillRect(this.getWidth()-360, 0, 80, this.getHeight());
		
		//Most
		g.setColor(Color.BLUE);
		g.fillRect(360, 0, 190, this.getHeight());
		
		Font f = this.getFont();
		Font font = new Font("MonoSpaced", Font.BOLD, 40);
		
		g.setFont(font);
		
		
		g2.rotate(-(Math.PI/2));
		
		g.setColor(Color.WHITE);
		g.drawString("PARKING    PARKING    PARKING", -750, 50);
		g.drawString("PARKING    PARKING    PARKING", -750, this.getWidth()-30);
		g.drawString("ROAD       ROAD       ROAD   ", -750, 190);
		g.drawString("ROAD       ROAD       ROAD   ", -750, this.getWidth()-170);
		g.setColor(Color.BLACK);
		g.drawString("      GATE       GATE        ", -750, 330);
		g.drawString("      GATE       GATE        ", -750, this.getWidth()-310);
		g.setColor(Color.WHITE);
		g.drawString("BRIDGE  BRIDGE  BRIDGE  BRIDGE", -750, 460);	
		
		g2.rotate((Math.PI/2));
	
		
		g.setFont(f);
		
		synchronized(application.bridge.allBuses) {
			int y = 10;
			for(Bus b : this.application.bridge.allBuses) {
				b.draw(g2, (int)b.calculatePosition(System.currentTimeMillis()), y);
				y+=40;
			}
		}
	}	
}

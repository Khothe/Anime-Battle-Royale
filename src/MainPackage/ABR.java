package MainPackage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import Graphics.*;
import EventosRatonTeclado.*;
import Img.*;
/**
 * Clase principal que contiene el bucle del juego
 * @author Gorka, Jon, Xabier
 *
 */
public class ABR implements Runnable, KeyListener {
	
	// constantes generales del juego
	public static final int pxAlturaVent = 563;
	public static final int pxAnchuraVent = 1000;
	public static final int pxAltoNaruto = 36;
	public static final int pxAnchoNaruto = 23;
	public static final int pxAltoGoku = 45;
	public static final int pxAnchoGoku = 30;
	public static final int coordXNaruto = 200;  // Coord X del Naruto 
	public static final int coordXGoku = 800;  // Coord X del Goku 
	public static final int coordYNaruto = 458 ;  // Coord Y del Naruto 
	public static final int coordYGoku = 458;  // Coord Y del Goku
	
	// atributos de la ventanaGrafica
	public static VentanaGrafica v;	
	private Naruto n;
	
	
			
			/**
			 * Control de jugador y acción sobre el juego
			 * @param ev evento registrado
			 * @param po pokemon protagonista
			 */
			private static void controlDeJugador( EventoVentana ev, ObjetoABR po ) {
				if (ev != null) {
					if (ev instanceof RatonPulsado ) {
						// Si se pulsa
						po.mover();
					}
				}
			}		
			
			/**
			 * Lanza un hilo que crea una ventana de tipo StartWindow
			 */
			private static void previoAlJuego(){
				
				// clase indexada que contiene el metodo run para el hilo
				 class VentanaRunnable implements Runnable{
						
						
						public void run(){
							
							VentanaGrafica v = new VentanaGrafica(1024, 724, 0, false, true, false, "ABR" );
							v.setVisible(true);
							long tiempoInicial = System.currentTimeMillis();
							EventoVentana ev = null;
							v.setFondo(new ObjetoGrafico("A_B_R_fondo.jpg" , true));
							while (!v.isClosed() && System.currentTimeMillis()-tiempoInicial<2000) {
							
								ev = v.readEvento( 40 );  // Lee evento o espera 40 msg  (algo moviéndose)
																		 
								 if (ev instanceof RatonPulsado) {
									RatonPulsado rp = (RatonPulsado) ev;
									ObjetoGrafico og = v.getObjetoEnPosicion( rp.getPosicion() );
									if (og == null) {
										System.out.println( "Click en posición: " + rp.getPosicion());
										v.dispose();
									}
								}
							}
							v.dispose();
						}
					}
				 	VentanaRunnable tarea = new VentanaRunnable();  //pasar a la clase del bucle de juego para que aparzcan ambas ventanas a la vez
				 	Thread hilo = new Thread(tarea);
					hilo.start();
			}
			/**
			 * Metodo que crea una ventana de tipo menuWindow
			 */
			private static void menuJuego(){
								

				menuWindow menu = new menuWindow();
				menu.setVisible(true);
				menu.setLocationRelativeTo(null);
			}
			
			//Nos ha resultado imposible hacer que los personajes se movieran, creo que hemos intentado hacerlo 
			//de todas las maneras posibles pero nada. Y por ello todo el trabajo posterior se ha 
			//quedado atascado (pelea, barras de vida, temporizador, etc)
			/**
			 * Bucle principal de juego
			 */
			public void run() {	
				
				v = new VentanaGrafica(pxAnchuraVent, pxAlturaVent, 0, true, true, false, "AnimeBattleRoyale" );
				v.setVisible(true);
				v.setFondo(new ObjetoGrafico( Img.getURLRecurso( "Fondo_batalla(1000x563).png" ), true, 1000, 563));
				try {
					VentanaGrafica.sonidos();
				} catch (LineUnavailableException | IOException
						| UnsupportedAudioFileException e1) {
					e1.printStackTrace();
				}
				EventoVentana ev = null;
				
				v.addKeyListener(this);
				
				v.showMessage( "Get Ready...  " );
				v.readEvento( 1500 );
				v.showMessage( "And...  " );
				v.readEvento( 1500 );
				n = new Naruto( coordXNaruto, coordYNaruto, v );
				Goku g = new Goku(coordXGoku, coordYGoku, v);
				v.showMessage( "FIGHT!" );
				do {
					ev = null;
					n.quitar();
					Naruto n1 = new Naruto( coordXNaruto+100, coordYNaruto, v );
				
					
					v.showMessage( "Game Over!!!!  Score = " );
					v.esperaUnRato( 2000 );
					//v.borraEventos();
					v.showMessage( "Haz click con el ratón si quieres seguir jugando!!" );
					ev = null; 
					ev = v.readEvento( 60000 );
					
				} while (ev != null && (ev instanceof EventoRaton));
					v.finish();
				
			
		
				
				
			}
			
			
			/** Método principal del juego ABR.
			 * @param args	No utilizado
			 */
			public static void main(String[] args){		

				
				previoAlJuego();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}			
				menuJuego();	
				
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_A)
				{
					//Introducir accion a realizar cuando se pulsa la A del teclado (Naruto)
				}
				else if (e.getKeyCode()==KeyEvent.VK_D)
				{
					//Introducir accion a realizar cuando se pulsa la D del teclado (Naruto)
				}
				else if (e.getKeyCode()==KeyEvent.VK_LEFT)
				{
					//Introducir accion a realizar cuando se pulsa la flecha --> del teclado (Goku)
				}
				else if (e.getKeyCode()==KeyEvent.VK_RIGHT)
				{
					//Introducir accion a realizar cuando se pulsa la flecha <-- del teclado (Goku)
				}
					
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}			
}

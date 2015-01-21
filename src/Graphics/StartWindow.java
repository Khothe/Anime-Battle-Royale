package Graphics;

import java.awt.Component;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.*;

import EventosRatonTeclado.EventoVentana;
import EventosRatonTeclado.RatonPulsado;
import Utils.FileManager;
import Utils.Score;
import Img.Img;
/**
 * Clase que crea una ventana de inicio que se cerrara 
 * sola al de unos seg
 * @author Gorka, Jon, Xabier
 *
 */
public class StartWindow extends JFrame{

	
	private static final long serialVersionUID = 2L;
	public static Score score;
	JPanel imagen;
	private ArrayList<EventoVentana> eventosVentana;
	private JLayeredPane layeredPane = new JLayeredPane();
	private static final Integer CAPA_FONDO = new Integer(-100); //JLayeredPane.DEFAULT_LAYER;

	
	public StartWindow(){
		
		score = FileManager.readScoreFromFile();
		this.setTitle("Anime Battle Royale");
		this.setSize(1024, 724);
		ObjetoGrafico fondo = new ObjetoGrafico(Img.getURLRecurso("A_B_R_fondo.jpg"), true);
		imagen = new JPanel();
		imagen.add(fondo);
		this.add(imagen);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(VentanaGrafica.class.getResource("ABR_logo.png")));
		
	}
	public void setFondo( ObjetoGrafico og ) {
		// Quitar posibles fondos anteriores
		for (Component c : layeredPane.getComponentsInLayer( CAPA_FONDO )) {
			layeredPane.remove( c );
		}
		layeredPane.add( og, CAPA_FONDO );
		layeredPane.repaint();
	}
	private synchronized EventoVentana remEvento( int index ) {
		return eventosVentana.remove( index );
	}	
	
	public EventoVentana readEvento( long maxEspera ) {
		long esperaHasta = System.currentTimeMillis()+maxEspera;
		boolean sigoEsperando = true;
		while (sigoEsperando && System.currentTimeMillis() < esperaHasta) {
			sigoEsperando = eventosVentana.isEmpty() && isVisible();
			
			if (sigoEsperando)
				// Espera hasta que el ratón o el teclado hagan algo
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) { }
		}		
		if (!isVisible()) return null;
		if (sigoEsperando)
			return null;
		else
			return remEvento(0);
	}

	
	public EventoVentana readEvento() {
		return readEvento( 3153600000000L );  // si pasan 100 años devuelve el control  :-)
	}
	public boolean isClosed() {
		return !isVisible();
	}	
	
	public static void main(String [] s){

		VentanaGrafica v = new VentanaGrafica(1024, 724, 0, false, true, false, "Anime Battle Royale" );
		EventoVentana ev = null;
		ObjetoGrafico moviendo = null;
		while (!v.isClosed()) {
			if (moviendo != null){
				ev = v.readEvento( 40 );  // Lee evento o espera 40 msg  (algo moviéndose)
			}
			else{
				ev = v.readEvento();  // Espera hasta que pase algo (nada moviéndose)
			}			 
			 if (ev instanceof RatonPulsado) {
				RatonPulsado rp = (RatonPulsado) ev;
				ObjetoGrafico og = v.getObjetoEnPosicion( rp.getPosicion() );
				if (og == null) {
					System.out.println( "Click en posición: " + rp.getPosicion());
					v.dispose();
				}
			}
		}
	}
}


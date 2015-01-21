package Graphics;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Utils.FileManager;
import Utils.Score;
import Img.Img;
/**
 * Clase que crea una ventana con creditos
 * La imagen no se ha modificado por ser algo totalmente secundario sin valor
 * añadido al juego
 * @author Gorka, Jon, Xabier
 *
 */
public class ventanaCreditos extends JFrame{

	private static final long serialVersionUID = 1L;
	public static Score score;
	JPanel imagen;
	
	public ventanaCreditos(){
		score = FileManager.readScoreFromFile();
		this.setTitle("Credits");
		this.setSize(720, 500);
		ObjetoGrafico fondo = new ObjetoGrafico(Img.getURLRecurso("gorkacreditos.jpg"), true);
		imagen = new JPanel();
		imagen.add(fondo);
		this.add(imagen);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(VentanaGrafica.class.getResource("ABR_logo.png")));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public static void creditosDelJuego(){				
			
			class creditosRunnable implements Runnable{
	
				@Override
				public void run() {
					
					ventanaCreditos credits = new ventanaCreditos();
					credits.setVisible(true);					
				}
			}
			creditosRunnable tarea = new creditosRunnable();
			Thread hilo = new Thread(tarea);
			hilo.start();
	}
	
	public static void main(String [] s){
		ventanaCreditos v=new ventanaCreditos();
		v.setVisible(true);
	}
}
package MainPackage;



import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;




import java.awt.event.KeyListener;

import Graphics.*;
import Img.Img;

/** Clase que crea objetos de tipo Naruto
 *  que sera el protagonista del juego
 * @author Gorka, Jon, Xabier
 *
 */
 public class Naruto extends ObjetoABR implements KeyListener{
	protected ObjetoGrafico og;
	protected boolean estoyMuerto = false;
	
	/** Constructor que crea un objeto tipo Naruto
	 * @param posX	Posición del objeto en coordenada X de la ventana
	 * @param posY	Posición del objeto en coordenada Y de la ventana
	 * @param ventana	Ventana gráfica en la que se integra
	 */		

	public Naruto(int posX, int posY, VentanaGrafica ventana) {
		super(posX, posY, ABR.pxAnchoNaruto*2, ABR.pxAltoNaruto*2, ventana);		
		og = new ObjetoGrafico( Img.getURLRecurso( "Naruto_facing_right.png" ), true, ABR.pxAnchoNaruto, ABR.pxAltoNaruto );
		og.setName( "Naruto" );
		og.setRectanguloDeChoque( ABR.pxAnchoNaruto/2, ABR.pxAltoNaruto/2, og.getAnchuraObjeto()-ABR.pxAnchoNaruto/2, og.getAlturaObjeto()-ABR.pxAltoNaruto/2 );
		ventana.addObjeto( og, new Point( posX, posY ) );	    
	    og.addKeyListener(this);
		
	}
	/** Devuelve el objeto gráfico del Naruto
	 * @return	Objeto gráfico rotable
	 */
	public ObjetoGrafico getObjetoGrafico() {
		return og;
	}
	
	/** Indica si el Naruto está muerto
	 * @return	true si está muerto, false en caso contrario
	 */
	public boolean estoyMuerto(){
		return this.estoyMuerto;
	}	
	
	/** Remueve el objeto (Naruto) de la ventana si muere
	 * 
	 */
	public void quitar() {
		muero();
		ventana.removeObjeto(og);
	
	}	
	/** Asigna el valor true a la variable booleana estoyMuerto
	 * 
	 */
	public void muero() {
		estoyMuerto = true;
	}
	
	/** Metodo que mueve a Naruto a través de la ventana
	 * 
	 */
	public void moverIzq() {
		if (!estoyMuerto) {
				
			}
						
	}
	@Override
	public void mover() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_A)
			this.quitar();
		System.out.println("XXXX");
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}	
}


package MainPackage;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import Graphics.*;
import Img.Img;

/** Clase que crea objetos de tipo Goku
 *  que sera el protagonista del juego
 * @author Gorka, Jon, Xabier
 *
 */
 public class Goku extends ObjetoABR{
	protected ObjetoGrafico og;
	protected boolean estoyMuerto = false;
	
	
	/** Constructor que crea un objeto tipo Goku
	 * @param posX	Posición del objeto en coordenada X de la ventana
	 * @param posY	Posición del objeto en coordenada Y de la ventana
	 * @param ventana	Ventana gráfica en la que se integra
	 */		

	public Goku(int posX, int posY, VentanaGrafica ventana) {
		super(posX, posY, ABR.pxAnchoGoku*2, ABR.pxAltoGoku*2, ventana);		
		og = new ObjetoGrafico( Img.getURLRecurso( "Goku_facing_left.png" ), true, ABR.pxAnchoGoku, ABR.pxAltoGoku );
		og.setName( "Goku" );
		og.setRectanguloDeChoque( ABR.pxAnchoGoku/2, ABR.pxAltoGoku/2, og.getAnchuraObjeto()-ABR.pxAnchoGoku/2, og.getAlturaObjeto()-ABR.pxAltoGoku/2 );
		ventana.addObjeto( og, new Point( posX, posY ) );
		
		og.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                actualiza(e.getKeyCode(), true);
            }

            public void keyReleased(KeyEvent e) {
                actualiza(e.getKeyCode(), false);
            }

            private void actualiza(int keyCode, boolean pressed) {
                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                       
                        break;

                    case KeyEvent.VK_RIGHT:
                        
                        break;
                }
            }
        });
		
	}
	/** Devuelve el objeto gráfico del Goku
	 * @return	Objeto gráfico rotable
	 */
	public ObjetoGrafico getObjetoGrafico() {
		return og;
	}
	
	/** Indica si el Goku está muerto
	 * @return	true si está muerto, false en caso contrario
	 */
	public boolean estoyMuerto(){
		return this.estoyMuerto;
	}	
	
	/** Remueve el objeto (Goku) de la ventana si muere
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
	
	/** Metodo que mueve a Goku a través de la ventana
	 * 
	 */
	public void mover() {
		if (!estoyMuerto) {
		
				}		
	}	
}

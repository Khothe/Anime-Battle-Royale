package Graphics;

import Img.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;


/** Clase de objeto visible en pantalla con capacidad de escalarse
 * al tamaño que se desee
 * @author Gorka, Jon, Xabier
 */
public class ObjetoGrafico extends JLabel {
	// la posición X,Y se hereda de JLabel
	protected String nombreImagenObjeto; // Nombre del fichero de imagen del objeto
	protected JPanel panelJuego;  // panel del juego donde se dibuja el objeto
	protected boolean esVisible;  // Info de si el objeto va a ser visible en el panel
	protected int anchuraObjeto;  // Anchura del objeto en pixels (depende de la imagen)
	protected int alturaObjeto;  // Altura del objeto en pixels (depende de la imagen)
	
	protected int xInicioChoque;  // Recuadro de choque dentro del objeto
	protected int xFinChoque;     // Deben estar incluidas 0 <= x < ancho, 0 <= y < alto 
	protected int yInicioChoque;
	protected int yFinChoque;
	
	protected ImageIcon icono;  // icono del objeto
	protected boolean escalado;  // escalado del icono
	protected BufferedImage imagenObjeto;  // imagen para el escalado
	private static final long serialVersionUID = 1L;  // para serializar

	/** Crea un nuevo objeto gráfico de ventana para juegos.<br>
	 * Si no existe el fichero de imagen, se crea un rectángulo blanco con borde rojo
	 * @param nombreImagenObjeto	Nombre fichero donde está la imagen del objeto (carpeta utils/img)
	 * @param visible	true si se quiere ver, false si se quiere tener oculto
	 * @param anchura	Anchura del objeto en píxels
	 * @param altura	Altura del objeto en píxels
	 */
	public ObjetoGrafico( String nombreImagenObjeto, boolean visible, int anchura, int altura ) {
		setName( nombreImagenObjeto );
		panelJuego = null;
		anchuraObjeto = anchura;
		alturaObjeto = altura;
		// Cargamos el icono (como un recurso - vale tb del .jar)
		this.nombreImagenObjeto = nombreImagenObjeto;
        URL imgURL = Img.getURLRecurso(nombreImagenObjeto);
        if (imgURL == null) {
        	icono = null;
    		setOpaque( true );
    		setBackground( Color.red );
    		setForeground( Color.blue );
        	setBorder( BorderFactory.createLineBorder( Color.blue ));
        	setText( nombreImagenObjeto );
        } else {
        	icono = new ImageIcon(imgURL);
    		setIcon( icono );
        	if (anchura==icono.getIconWidth() && altura==icono.getIconHeight()) {
        		escalado = false;
        	} else {  // Hay escalado: prepararlo
        		escalado = true;
            	try {  // pone la imagen para el escalado
        			imagenObjeto = ImageIO.read(imgURL);
        		} catch (IOException e) {
        			escalado = false;
        		}
        	}
        }
    	setSize( anchura, altura );
		this.xInicioChoque = 0;
		this.xFinChoque = anchura; 
		this.yInicioChoque = 0;
		this.yFinChoque = altura;
		esVisible = visible;
		setVisible( esVisible );
	}
	
	/** Crea un nuevo objeto gráfico de ventana para juegos.<br>
	 * Si no existe el fichero de imagen, se crea un rectángulo blanco con borde rojo de 10x10 píxels<br>
	 * Si existe, se toma la anchura y la altura de esa imagen.
	 * @param nombreImagenObjeto	Nombre fichero donde está la imagen del objeto (carpeta utils/img)
	 * @param visible	Panel en el que se debe dibujar el objeto
	 */
	public ObjetoGrafico( String nombreImagenObjeto, boolean visible ) {
		this( nombreImagenObjeto, visible, 10, 10 );
		if (icono != null) {  // En este constructor se adapta la anchura y altura al icono
			anchuraObjeto = icono.getIconWidth();
			alturaObjeto = icono.getIconHeight();
			setSize( anchuraObjeto, alturaObjeto );
			this.xFinChoque = anchuraObjeto; 
			this.yFinChoque = alturaObjeto;
		}
	}

	/** Crea un nuevo objeto gráfico de ventana para juegos.<br>
	 * Si la URL de imagen es null, se crea un rectángulo blanco con borde rojo
	 * @param urlImagenObjeto	URL donde está la imagen del objeto
	 * @param visible	true si se quiere ver, false si se quiere tener oculto
	 * @param anchura	Anchura del objeto en píxels
	 * @param altura	Altura del objeto en píxels
	 */
	public ObjetoGrafico( java.net.URL urlImagenObjeto, boolean visible, int anchura, int altura ) {
		if (urlImagenObjeto!=null) setName( urlImagenObjeto.getQuery());
		panelJuego = null;
		anchuraObjeto = anchura;
		alturaObjeto = altura;
		nombreImagenObjeto = "";
        if (urlImagenObjeto == null) {
        	icono = null;
    		setOpaque( true );
    		setBackground( Color.red );
    		setForeground( Color.blue );
        	setBorder( BorderFactory.createLineBorder( Color.blue ));
        	setText( nombreImagenObjeto );
        } else {
        	icono = new ImageIcon(urlImagenObjeto);
    		setIcon( icono );
        	if (anchura==icono.getIconWidth() && altura==icono.getIconHeight()) {
        		escalado = false;
        	} else {  // Hay escalado: prepararlo
        		escalado = true;
            	try {  // pone la imagen para el escalado
        			imagenObjeto = ImageIO.read(urlImagenObjeto);
        		} catch (IOException e) {
        			escalado = false;
        		}
        	}
        }
    	setSize( anchura, altura );
		this.xInicioChoque = 0;
		this.xFinChoque = anchura; 
		this.yInicioChoque = 0;
		this.yFinChoque = altura;
		esVisible = visible;
		setVisible( esVisible );
	}
	
	/** Crea un nuevo objeto gráfico de ventana para juegos.<br>
	 * Si no existe el fichero de imagen, se crea un rectángulo blanco con borde rojo de 10x10 píxels<br>
	 * Si existe, se toma la anchura y la altura de esa imagen.
	 * @param urlImagenObjeto	URL donde está la imagen del objeto
	 * @param visible	Panel en el que se debe dibujar el objeto
	 */
	public ObjetoGrafico( java.net.URL urlImagenObjeto, boolean visible ) {
		this( urlImagenObjeto, visible, 10, 10 );
		if (icono != null) {  // En este constructor se adapta la anchura y altura al icono
			anchuraObjeto = icono.getIconWidth();
			alturaObjeto = icono.getIconHeight();
			setSize( anchuraObjeto, alturaObjeto );
			this.xFinChoque = anchuraObjeto; 
			this.yFinChoque = alturaObjeto;
		}
	}
	
	/** Activa o desactiva la visualización del objeto 
	 * @param visible	true si se quiere ver, false si se quiere tener oculto
	 */
	public void setVisible( boolean visible ) {
		super.setVisible( visible );
		esVisible = visible;
	}

	/** Devuelve la anchura del rectángulo gráfico del objeto
	 * @return	Anchura
	 */
	public int getAnchuraObjeto() {
		return anchuraObjeto;
	}
	
	/** Devuelve la altura del rectángulo gráfico del objeto
	 * @return	Altura
	 */
	public int getAlturaObjeto() {
		return alturaObjeto;
	}

	/** Devuelve el rectángulo de choque interno del objeto gráfico
	 * @return	rectángulo de choque. Si no está definido, es el objeto completo:
	 * (0,0,anchura,altura)
	 */
	public Rectangle getRectanguloInternoChoque() {
		return new Rectangle( xInicioChoque, yInicioChoque, xFinChoque, yFinChoque );
	}
	
	/** Pone el espacio del objeto gráfico que detecta los choques (por omisión es todo el objeto).<br>
	 * Deben estar dentro del objeto: 0 <= x <= ancho, 0 <= y <= alto
	 * xInicio < xFin, yInicio < yFin
	 * @param xInicioChoque
	 * @param yInicioChoque
	 * @param xFinChoque
	 * @param yFinChoque
	 */
	public void setRectanguloDeChoque( int xInicioChoque, int yInicioChoque, int xFinChoque, int yFinChoque ) {
		this.xInicioChoque = xInicioChoque;
		this.xFinChoque = xFinChoque; 
		this.yInicioChoque = yInicioChoque;
		this.yFinChoque = yFinChoque;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#contains(java.awt.Point)
	 */
	@Override
	public boolean contains(Point p) {
		if (p==null) return false;
		return (p.getX()>=getX() && p.getX()<getX()+getWidth() &&
				p.getY()>=getY() && p.getY()<getY()+getHeight());
	}

	/** Comprueba si el objeto choca contra otro con un margen de diferencia
	 * @param o2	Objeto de comprobación
	 * @param margenPixels	Margen de pixels para el choque
	 * @return	true si chocan, false en caso contrario
	 */
	public boolean chocaCon( ObjetoGrafico o2, int margenPixels ) {
		boolean choca = !(getX()+xInicioChoque+margenPixels > o2.getX()+o2.xFinChoque ||
				getX()+xFinChoque-margenPixels < o2.getX()+o2.xInicioChoque ||
				getY()+yInicioChoque+margenPixels> o2.getY()+o2.yFinChoque ||
				getY()+yFinChoque -margenPixels < o2.getY()+o2.yInicioChoque);
		return choca;
	}	

	// Dibuja este componente de una forma no habitual (si es proporcional)
	@Override
	protected void paintComponent(Graphics g) {
		if (escalado) {
			Graphics2D g2 = (Graphics2D) g;  // El Graphics realmente es Graphics2D
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);	
	        g2.drawImage(imagenObjeto, 0, 0, anchuraObjeto, alturaObjeto, null);
        } else {  // sin escalado
			super.paintComponent(g);
		}
	}

	
}


package Graphics;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

import Graphics.ObjetoGrafico;
import EventosRatonTeclado.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/** Clase de utilidad para poder realizar juegos o animaciones
 * utilizando una ventana con elementos gr�ficos.
 * @author Gorka, Jon, Xabier
 */
public class VentanaGrafica extends JFrame implements WindowListener{	
	private static final long serialVersionUID = 1L;
	private JLabel lMensaje = new JLabel( " " );
	private JLabel lMensajeSombra = new JLabel( " " );
	private JPanel pAreaControl = new JPanel();
	private JPanel pCristal = new JPanel();  // Capa cristal (uso futuro para el HUD)
	private JLayeredPane layeredPane = new JLayeredPane();
	
	private ArrayList<EventoVentana> eventosVentana;  // lista de eventos pendientes de teclado/rat�n
	private Point posicionRaton = null;  // posici�n actual del rat�n (null si est� fuera del panel gr�fico)
	private boolean generarClicksYDrags;  // se generan o no los eventos de click y drag?
	
	private long tiempoAnimMsg = 500L;  // Tiempo para un paso de animaci�n (en milisegundos).
	private long tiempoFrameAnimMsg = tiempoAnimMsg/40L;  // Msg entre cada paso de refresco de animaci�n
	private HiloAnimacion hilo = null;  // Hilo de la animaci�n
	private ArrayList<Animacion> animacionesPendientes = new ArrayList<Animacion>();
	
	private static final Integer CAPA_FONDO = new Integer(-100); //JLayeredPane.DEFAULT_LAYER;
	private static final int PX_SOLAPE_FONDOS = 0;
	
			private synchronized void addEvento( EventoVentana ev ) {
				eventosVentana.add( ev );
			}
			private synchronized EventoVentana remEvento( int index ) {
				return eventosVentana.remove( index );
			}
			private synchronized boolean remEvento( EventoVentana ev ) {
				return eventosVentana.remove( ev );
			}
			private synchronized EventoVentana getEvento( int index ) {
				return eventosVentana.get ( index );
			}

	/** Construye una nueva ventana de juego de tablero,
	 * y la muestra en el centro de la pantalla.
	 * @param anchuraVent	Anchura de la ventana en pixels
	 * @param alturaVent	Altura de la ventana en pixels
	 * @param anchuraPanelControl	Anchura del panel de control de la derecha (0 si no se quiere utilizar)
	 * @param tamFijo	false si se hace redimensionable, true en caso contrario 
	 * @param cerrable	true si el usuario la puede cerrar (ver m�todo {@link #isClosed()}), false en caso contrario
	 * @param genCyD	true si se quieren generar eventos de click y drag, false si s�lo se procesan pulsaci�n y suelta
	 * @param titulo	T�tulo de la ventana
	 */
	public VentanaGrafica( int anchuraVent, int alturaVent, int anchuraPanelControl, boolean tamFijo, boolean cerrable, boolean genCyD, String titulo ) {
	
		setSize( anchuraVent, alturaVent );
		setTitle( titulo );
		setResizable( !tamFijo );
		this.addWindowListener(this);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(VentanaGrafica.class.getResource("ABR_logo.png")));
		pAreaControl.setMaximumSize( new Dimension(anchuraPanelControl, 5000));
		pAreaControl.setMinimumSize( new Dimension(anchuraPanelControl, 0));
		lMensaje.setBounds( 0, 0, anchuraVent, 35 );
		lMensajeSombra.setBounds( 2, 2, anchuraVent, 35 );
		generarClicksYDrags = genCyD;
		if (cerrable)
			setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		else
			setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );			
		try {
			EventQueue.invokeAndWait( new Runnable()  {
				@Override
				public void run() {
					eventosVentana = new ArrayList<EventoVentana>();
					setLocationRelativeTo( null );
					setLayeredPane( layeredPane );
					pCristal.setLayout( null );
					setGlassPane( pCristal );
					pCristal.setOpaque( false );
					pCristal.setVisible( true );
					pAreaControl.setOpaque( false );
					lMensaje.setOpaque( false );
					lMensajeSombra.setOpaque( false );
					lMensaje.setFont( new Font( "Arial", Font.BOLD, 30 ));
					lMensajeSombra.setFont( new Font( "Arial", Font.BOLD, 30 ));
					lMensaje.setForeground( new Color(255, 255, 0) );
					lMensajeSombra.setForeground( new Color(0, 0, 128) );
					lMensaje.setHorizontalAlignment( JLabel.CENTER );
					lMensajeSombra.setHorizontalAlignment( JLabel.CENTER );
					

					
					layeredPane.add( pAreaControl, JLayeredPane.PALETTE_LAYER );
					layeredPane.add( lMensaje, JLayeredPane.PALETTE_LAYER );
					layeredPane.add( lMensajeSombra, JLayeredPane.PALETTE_LAYER );
					pAreaControl.setLayout( null );  // layout de posicionamiento absoluto
					layeredPane.setFocusable( true );
					layeredPane.requestFocus();
					setVisible( true );
		    		pCristal.setBounds( 0, 0, getContentPane().getWidth(), getContentPane().getHeight() );
					layeredPane.addFocusListener( new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent arg0) {
							layeredPane.requestFocus();
						}
					});
					layeredPane.addMouseListener( new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent arg0) {
							boolean anyadirSuelta = true;
							if (generarClicksYDrags) {
								RatonPulsado rp = null;
								for (EventoVentana ev : eventosVentana) {
									if (ev instanceof RatonPulsado) {  // Se ha pulsado un rat�n y ahora se suelta: a ver qu� es
										rp = (RatonPulsado) ev;
										anyadirSuelta = false;
										if (rp.getPosicion().equals( arg0.getPoint() )) {  // Igual coordenada: click
											addEvento( new RatonClick(arg0) );
										} else { // Dif coordenada: drag
											addEvento( new RatonDrag( rp.getTime(), rp.getPosicion(), arg0 ) );
										}
										break;
									}
								}
								if (rp!=null) {  // Quitar evento de pulsaci�n de la cola
									remEvento( rp );
								}
							}
							if (anyadirSuelta) {
								addEvento( new RatonSoltado(arg0) );
							}
							posicionRaton = arg0.getPoint();
						}
						@Override
						public void mousePressed(MouseEvent arg0) {
							addEvento( new RatonPulsado(arg0) );
							posicionRaton = arg0.getPoint();
						}
						@Override
						public void mouseEntered(MouseEvent e) {
							posicionRaton = e.getPoint();
						}
						@Override
						public void mouseExited(MouseEvent e) {
							posicionRaton = null;
						}
					});
					layeredPane.addMouseMotionListener( new MouseMotionListener() {
						@Override
						public void mouseMoved(MouseEvent e) {
							posicionRaton = e.getPoint();
						}
						@Override
						public void mouseDragged(MouseEvent e) {
							posicionRaton = e.getPoint();
						}
					});				
				}
			} );
		} catch (Exception e) {
		}
	}
	
	/** Cierra y finaliza la ventana de juego (no acaba la aplicaci�n).
	 */
	public void finish() {
		if (hilo!=null) { hilo.interrupt(); }
		dispose();
	}
	
	/** Pone el fondo de la ventana de juego con un objeto gr�fico,
	 * ocupando todo el fondo de la ventana
	 * @param og
	 */
	public void setFondo( ObjetoGrafico og ) {
		fondoAnimado = false;
		// Quitar posibles fondos anteriores
		for (Component c : layeredPane.getComponentsInLayer( CAPA_FONDO )) {
			layeredPane.remove( c );
		}
		layeredPane.add( og, CAPA_FONDO );
		layeredPane.repaint();
	}
	 
	/** Pone el fondo de la ventana de juego con dos objetos gr�ficos,
	 * ocupando todo el fondo de la ventana y aline�ndose en lateral.<br>
	 * Inicialmente no se mueve, para ello usar el m�todo {@link #rodarFondoAnimado(boolean)}.
	 * @param og1	Objeto gr�fico de fondo
	 * @param og2	Objeto gr�fico de fondo 2 (a su derecha y sucesivamente en ciclo)
	 * @param pixDespAIzqda	P�xels que se desplazan a la izquierda cada iteraci�n de animaci�n
	 */
	public void setFondoAnimado( ObjetoGrafico og1, ObjetoGrafico og2, double pixDespAIzqda ) {
		// Quitar posibles fondos anteriores
		for (Component c : layeredPane.getComponentsInLayer( CAPA_FONDO )) {
			layeredPane.remove( c );
		}
		fondo1 = og1;
		fondo2 = og2;
		og1.setLocation( 0, 0 );
		og2.setLocation( og1.getWidth() - PX_SOLAPE_FONDOS, 0 );   // a la derecha de og1 [solapa un pixel]
		coorX1 = 0;
		coorX2 = og1.getWidth() - PX_SOLAPE_FONDOS;
		layeredPane.add( og1, CAPA_FONDO );
		layeredPane.add( og2, CAPA_FONDO );
		layeredPane.repaint();
		fondoAnimado = true;
		fondoRodando = false;
		this.pixDespAIzqda = pixDespAIzqda;
		if (hilo==null) { hilo = new HiloAnimacion(); hilo.start(); }
	}
		// Atributos de animaci�n de fondo:
		private boolean fondoAnimado = false;
		private boolean fondoRodando = true;
		private double pixDespAIzqda = 0D;
		private double coorX1 = 0D;
		private double coorX2 = 0D;
		private ObjetoGrafico fondo1 = null;
		private ObjetoGrafico fondo2 = null;
	
	/** Permite parar o seguir haciendo el desplazamiento lateral del fondo.<br>
	 * S�lo sirve si se ha llamado antes a {@link #setFondoAnimado(ObjetoGrafico, ObjetoGrafico, double)}
	 * @param seguir	true si se quiere animar, false si se quiere detener.
	 */
	public void rodarFondoAnimado( boolean seguir ) {
		fondoRodando = seguir;
	}
		
	/** Devuelve la posici�n actual del rat�n con respecto al panel gr�fico de la ventana 
	 * @return	Posici�n actual del rat�n en el panel gr�fico, null si est� fuera
	 */
	public Point getPosRaton() {
		return posicionRaton;
	}
	
	// 
	/** Indica si la coordenada est� dentro del panel gr�fico de la ventana
	 * @param p	Coordenada de ventana
	 * @return	true si la coordenada est� dentro del tablero, false en caso contrario
	 */
	public boolean estaEnVentana( Point p ) {
		return (p.getX() >= 0 && p.getX() < layeredPane.getWidth() &&
				p.getY() >= 0 && p.getY() < layeredPane.getHeight());
	}
	
	/** Devuelve el n�mero de pixels de ancho del panel gr�fico de la ventana
	 * @return	ancho en p�xels
	 */
	public int getAnchoPanelGrafico() {
		return layeredPane.getWidth();
	}
	
	/** Devuelve el n�mero de pixels de alto del panel gr�fico de la ventana
	 * @return	alto en p�xels
	 */
	public int getAltoPanelGrafico() {
		return layeredPane.getHeight();
	}
	
	/** Consulta si el usuario ha realizado alg�n evento de rat�n o teclado en la ventana
	 * @return	true si se ha realizado alguno, false en caso contrario
	 */
	public boolean hayEvento() {
		return !eventosVentana.isEmpty();
	}
	
	/** Borra los eventos de rat�n o teclado
	 */
	public void borraEventos() {
		eventosVentana.clear();
	}
	
	/** Devuelve el primer evento pendiente de rat�n o teclado en la ventana
	 * (y lo da por procesado)
	 * @return	Siguiente evento pendiente, null si no se ha realizado ninguno
	 */
	public EventoVentana getEvento() {
		if (eventosVentana.isEmpty()) return null;
		return remEvento(0);
	}

	/** Consulta si el usuario est� haciendo una interacci�n no acabada con el rat�n
	 * @return	true si el pr�ximo evento se ha pulsado el bot�n del rat�n pero todav�a no se ha soltado, false en caso contrario
	 */
	public boolean hayClickODragAMedias() {
		if (!eventosVentana.isEmpty() && 
				(getEvento(0) instanceof RatonPulsado)) {
			for (EventoVentana ev : eventosVentana) {
				if (ev instanceof RatonSoltado) {
					return false;
				}
			}
			return true;
		} else
			return false;
	}
	
	/** Espera a que haya un evento en la ventana (de rat�n o teclado) y lo devuelve.<br>
	 * Si pasa el tiempo m�ximo sin eventos, devuelve null al cabo de ese tiempo.<br>
	 * Si se ha configurado la ventana para procesar click o drag, no devuelve el evento
	 * si es de rat�n hasta que se finaliza el movimiento (de click o de drag),
	 * o null si no se completa en el tiempo l�mite.<br>
	 * Si la ventana se cierra antes de que haya habido un evento, devuelve null.
	 * @param maxEspera	
	 * @return	evento producido, o null si la ventana se ha cerrado
	 */
	public EventoVentana readEvento( long maxEspera ) {
		long esperaHasta = System.currentTimeMillis()+maxEspera;
		boolean sigoEsperando = true;
		while (sigoEsperando && System.currentTimeMillis() < esperaHasta) {
			sigoEsperando = eventosVentana.isEmpty() && isVisible();
			if (!sigoEsperando && generarClicksYDrags) { // Si click/drag mirar si hay algo que hacer
				if (hayClickODragAMedias())
						sigoEsperando = true;  // Hay uno a medias, sigo esperando
			}
			if (sigoEsperando)
				// Espera hasta que el rat�n o el teclado hagan algo
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) { }
		}
		if (generarClicksYDrags && sigoEsperando)   // final por tiempo pero a medias el drag o click
			if (!eventosVentana.isEmpty() && eventosVentana.get(0) instanceof RatonPulsado) {
				// eventosVentana.remove(0);
				return null;  // Tengo un evento a medias... devuelvo null
			}
		if (!isVisible()) return null;
		if (sigoEsperando)
			return null;
		else
			return remEvento(0);
	}
	
	/** Espera a que haya un evento en la ventana (de rat�n o teclado) y lo devuelve.<br>
	 * Si se ha configurado la ventana para procesar click o drag, no devuelve el evento
	 * si es de rat�n hasta que se finaliza el movimiento (de click o de drag).<br>
	 * Si la ventana se cierra antes de que haya habido un evento, devuelve null.
	 * @return	evento producido, o null si la ventana se ha cerrado
	 */
	public EventoVentana readEvento() {
		return readEvento( 3153600000000L );  // si pasan 100 a�os devuelve el control  :-)
	}
	
	/** Visualiza un mensaje enf la l�nea de mensajes
	 * @param s	String a visualizar en la l�nea inferior de la ventana
	 */
	public void showMessage( String s ) {
		lMensaje.setText( s );
		lMensajeSombra.setText( s );
	}
	
	/** Informa si la ventana ha sido cerrada por el usuario
	 * @return	true si la ventana se ha cerrado, false si sigue visible
	 */
	public boolean isClosed() {
		return !isVisible();
	}
	
	/** A�ade al panel gr�fico un objeto de juego, que se visualizar� 
	 * inmediatamente si est� marcado para ser visible.<br>
	 * Atenci�n, si el mismo objeto se a�ade dos veces s�lo se 
	 * tiene en cuenta una.
	 * @param oj	Objeto de juego a introducir
	 * @param p	Posici�n de panel en la que poner el objeto
	 */
	public void addObjeto( final ObjetoGrafico oj, Point p ) {
		oj.setLocation(p);
		try {
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					layeredPane.add( oj, new Integer( JLayeredPane.DEFAULT_LAYER ) );
					layeredPane.repaint( oj.getX(), oj.getY(), oj.getAnchuraObjeto(), oj.getAlturaObjeto() );
				}
			});
		} catch (Exception e) {
		}
	}
	
	/** Quita de la ventana el objeto gr�fico.<br>
	 * Si el objeto no estaba, no ocurre nada.
	 * @param oj	Objeto de juego a eliminar
	 */
	public void removeObjeto( final ObjetoGrafico oj ) {
		try {
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					layeredPane.remove( oj );
					layeredPane.repaint( oj.getX(), oj.getY(), oj.getAnchuraObjeto(), oj.getAlturaObjeto() );
					layeredPane.validate();  // TODO: chequear si hace falta
				}
			});
		} catch (Exception e) {
		}
	}
	
	/** Devuelve el n�mero de objetos activos en la ventana
	 * @return
	 */
	public int getNumObjetos() {
		return layeredPane.getComponentCountInLayer( JLayeredPane.DEFAULT_LAYER );
	}

	/** Quita de la ventana todos los objetos gr�ficos que hubiera
	 */
	public void clearObjetos() {
		try {
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					for (Component c : layeredPane.getComponentsInLayer( JLayeredPane.DEFAULT_LAYER )) {
						layeredPane.remove( c );
					}
					layeredPane.repaint();
				}
			});
		} catch (Exception e) {
		}
	}

	/** Trae al frente de la ventana el objeto gr�fico.<br>
	 * Si el objeto no estaba, no ocurre nada.
	 * @param oj	Objeto de juego a traer al frente
	 */
	public void traeObjetoAlFrente( final ObjetoGrafico oj ) {
		if (oj != null)
			try {
				SwingUtilities.invokeLater( new Runnable() {
					@Override
					public void run() {
						layeredPane.moveToFront(oj);
					}
				});
			} catch (Exception e) {
			}
	}

	/** Mueve un objeto de juego a la posici�n indicada.<br>
	 * El objeto debe ser != null y estar a�adido a la ventana.
	 * @param oj	Objeto de juego a mover
	 * @param p	Posici�n del panel gr�fico a la que mover el objeto
	 */
	public void setPosGrafico( ObjetoGrafico oj, Point p ) {
		oj.setLocation( p );
	}

/** Mueve un objeto de juego a la posici�n indicada
 * realizando una animaci�n (lineal).<br>
 * El objeto debe ser != null y estar a�adido a la ventana.<p>
 * Si el objeto ya ten�a una animaci�n en curso, se completa con esta
 * desde donde estuviera.<br>
 * El tiempo que dura la animaci�n es el tiempo fijado con {@link #setTiempoPasoAnimacion(long, int)}
 * (por defecto 500 msg).
 * @param oj	Objeto de juego a mover
 * @param p	Coordenada a la que mover el objeto
 */
	public void muevePosGrafico( ObjetoGrafico oj, Point p ) {
		if (oj!=null) {
			if (hilo==null) { hilo = new HiloAnimacion(); hilo.start(); }
			Animacion a = new Animacion( oj.getX(), p.getX(), 
					oj.getY(), p.getY(), tiempoAnimMsg, oj );
			if (animacionesPendientes.indexOf(a) == -1)
				// Si el objeto es nuevo se mete en animaciones pendientes
				animacionesPendientes.add( a );
			else {  // Si ya estaba se actualiza esa animaci�n (ojo, puede generar diagonales o cosas raras)
				int pos = animacionesPendientes.indexOf(a);
				animacionesPendientes.get(pos).xHasta = p.getX();
				animacionesPendientes.get(pos).yHasta = p.getY();
				animacionesPendientes.get(pos).msFaltan = tiempoAnimMsg;
			}
		}
	}

	/** Mueve un objeto de juego a la posici�n indicada
	 * realizando una animaci�n (lineal).<br>
	 * El objeto debe ser != null y estar a�adido a la ventana.<p>
	 * Si el objeto ya ten�a una animaci�n en curso, se completa con esta
	 * desde donde estuviera.<br>
	 * @param oj	Objeto de juego a mover
	 * @param p	Coordenada a la que mover el objeto
	 * @param msg	Tiempo que durar� la animaci�n de movimiento
	 */
	public void muevePosGrafico( ObjetoGrafico oj, Point p, long msg ) {
		if (oj!=null) {
			if (hilo==null) { hilo = new HiloAnimacion(); hilo.start(); }
			Animacion a = new Animacion( oj.getX(), p.getX(), 
					oj.getY(), p.getY(), msg, oj );
			if (animacionesPendientes.indexOf(a) == -1)
				// Si el objeto es nuevo se mete en animaciones pendientes
				animacionesPendientes.add( a );
			else {  // Si ya estaba se actualiza esa animaci�n (ojo, puede generar diagonales o cosas raras)
				int pos = animacionesPendientes.indexOf(a);
				animacionesPendientes.get(pos).xHasta = p.getX();
				animacionesPendientes.get(pos).yHasta = p.getY();
				animacionesPendientes.get(pos).msFaltan = msg;
			}
		}
	}

	/** Calcula el tiempo de movimiento de un objeto en su trayectoria lineal,
	 * dada la velocidad que se quiere conseguir.
	 * @param oj	Objeto de juego a mover
	 * @param p	Coordenada a la que mover el objeto
	 * @param vel	Velocidad a la que querr�a mover (en p�xels/segundo)
	 * @return
	 */
	public long calcTiempoDeMovimiento( ObjetoGrafico oj, Point p, double vel ) {
		double dist = Math.sqrt( Math.pow( oj.getX()-p.getX(), 2 ) + 
								 Math.pow( oj.getY()-p.getY(), 2 ) );
		return Math.round( dist / vel * 1000 );
	}

	/** Para el movimiento del objeto gr�fico indicado. Si
	 * no se estuviera haciendo una animaci�n, no ocurre nada.
	 * @param oj	Objeto de juego a detener donde est�
	 */
	public void paraMovimiento( ObjetoGrafico oj ) {
		if (oj == null) return;
		for (Animacion a : animacionesPendientes) {
			if (a.oj.equals( oj )) {
				animacionesPendientes.remove( a );
				return;
			}
		}
	}

	/** Pone los tiempos para realizar las animaciones visuales en pantalla.
	 * @param tiempoAnimMsg	Tiempo para un paso de animaci�n (en milisegundos).
	 * Debe ser >= que 100 msg (si no, este m�todo no hace nada).
	 * Por defecto es de 500 msg.
	 * @param numMovtos	N�mero de fotogramas para cada paso de animaci�n
	 * (veces que se refresca la animaci�n dentro de cada paso).
	 * Debe ser un valor entre 2 y tiempoAnimMsg (si no, este m�todo no hace nada).
	 * Por defecto es de 40 msg.
	 */
	public void setTiempoPasoAnimacion( long tiempoAnimMsg, int numMovtos ) {
		if (tiempoAnimMsg < 100L || numMovtos < 2 || numMovtos > tiempoAnimMsg) 
			return;  // Error: no se hace nada
		this.tiempoAnimMsg = tiempoAnimMsg;
		this.tiempoFrameAnimMsg = tiempoAnimMsg/numMovtos;
	}

	/** Devuelve la posici�n actual del objeto gr�fico indicado.<br>
	 * El objeto debe ser != null y estar a�adido a la ventana.
	 * @param oj	Objeto gr�fico del que devolver la posici�n
	 * @return	Posici�n de ese objeto, null si no existe
	 */
	public Point getPosicion( ObjetoGrafico oj ) {
		if (oj == null) return null;
		java.util.List<Component> l = Arrays.asList( layeredPane.getComponentsInLayer( JLayeredPane.DEFAULT_LAYER ) );
		if (!l.contains( oj )) return null;
		return oj.getLocation();
	}

	/** Comprueba si hay alg�n objeto gr�fico en la posici�n indicada.<br>
	 * Para la comprobaci�n se usa el rect�ngulo completo del objeto gr�fico.
	 * Si hay varios objetos gr�ficos que coinciden con la misma posici�n, se devuelve el 
	 * primero en posici�n de visualizaci�n (ver {@link #traeObjetoAlFrente(ObjetoGrafico)}).<br>
	 * Los gr�ficos de fondo no se tienen en cuenta.
	 * @param p	Posici�n en el panel gr�fico
	 * @return	Objeto que se encuentra en esa posici�n, null si no hay ninguno
	 */
	public ObjetoGrafico getObjetoEnPosicion( Point p ) {
		Component[] lC = layeredPane.getComponentsInLayer( JLayeredPane.DEFAULT_LAYER );
		for (Component c : lC) {
			if (c.contains( p ) && c instanceof ObjetoGrafico)
				return (ObjetoGrafico) c;
		}
		return null;
	}

	/** Espera sin hacer nada durante el tiempo indicado en milisegundos
	 * @param msg	Tiempo a esperar
	 */
	public void esperaUnRato( int msg ) {
		try {
			Thread.sleep( msg );
		} catch (InterruptedException e) {
		}
	}

	/** Espera sin hacer nada durante el tiempo indicado en milisegundos
	 * o hasta que se produce un evento (o nada si ya hay un evento producido).<br>
	 * El evento no se consume (cogerlo con {@link #readEvento()} o {@link #readEvento(long)}) 
	 * @param msg	Tiempo a esperar
	 */
	public void esperaAEvento( int msg ) {
		long esperaHasta = System.currentTimeMillis()+msg;
		boolean sigoEsperando = true;
		while (sigoEsperando && System.currentTimeMillis() < esperaHasta) {
			sigoEsperando = eventosVentana.isEmpty() && isVisible();
			if (!sigoEsperando && generarClicksYDrags) { // Si click/drag mirar si hay algo que hacer
				if (hayClickODragAMedias())
						sigoEsperando = true;  // Hay uno a medias, sigo esperando
			}
			if (sigoEsperando)
				// Espera hasta que el rat�n haga algo
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) { }
		}
	}

	/** Espera sin hacer nada a que acaben las animaciones
	 * @param msg	Tiempo a esperar
	 */
	public void esperaAFinAnimaciones() {
		do {
			try {
				Thread.sleep( tiempoFrameAnimMsg );
			} catch (InterruptedException e) {
			}
		} while (!animacionesPendientes.isEmpty());
	}

	//m�todo y atributos privados para facilitar los offsets de puntos
			// del programa de prueba
			private static int moviendoOffsetX = 0;
			private static int moviendoOffsetY = 0;
			private static Point aplicaOffset( Point p ) {
				return new Point( (int) Math.round(p.getX() - moviendoOffsetX),
						(int) Math.round(p.getY() - moviendoOffsetY) );
			}

		/** M�todo de prueba de la clase.
		 * @param args
		 */
		public static void main(String[] args) {
			boolean pruebaConDrag = false;
			if (!pruebaConDrag) {
				// PRUEBA SIN DRAG
				VentanaGrafica v = new VentanaGrafica(1000, 563, 0, false, true, false, "Ventana gr�fica de prueba" );
				v.setTiempoPasoAnimacion( 2000, 50 );
				EventoVentana ev = null;
				ObjetoGrafico moviendo = null;
				Point coordInicioRaton = null;
				ObjetoGrafico o1 = new ObjetoGrafico( "Naruto_facing_left.png", true );
				ObjetoGrafico o2 = new ObjetoGrafico( "Goku_facing_right.png", true );
				ObjetoGrafico o3 = new ObjetoGrafico( "Goku_left.png", true );
				v.addObjeto( o1, new Point( 0, 0 ) );
				v.addObjeto( o2, new Point( 100, 250 ) );
				v.addObjeto( o3, new Point( 250, 140 ) );
				v.setFondoAnimado(new ObjetoGrafico( "Fondo_batalla(1000x563).png", true ),
				new ObjetoGrafico( "Fondo_batalla(1000x563).png", true ), 3 );
				v.showMessage( "Pulsa y arrastra para mover cada objeto. Click para traer al frente. Si lo arrastras fuera volver� a su sitio." );
				v.esperaAEvento( 3000 );
				v.rodarFondoAnimado( true );
				v.showMessage( "Empezando prueba de ventana!" );
				while (!v.isClosed()) {
					if (moviendo != null)
						ev = v.readEvento( 40 );  // Lee evento o espera 40 msg  (algo movi�ndose)
					else
						ev = v.readEvento();  // Espera hasta que pase algo (nada movi�ndose)
					if (ev == null){  // Si no hay evento pero se est� moviendo, seguimos al rat�n
						if (moviendo != null) {
							Point posRaton = v.getPosRaton();
							if (posRaton != null) {
								v.setPosGrafico( moviendo, aplicaOffset( posRaton ) );
								v.showMessage( "Moviendo objeto " + moviendo.getName() );
							}
						}
					
					} else if (ev instanceof RatonPulsado) {
						RatonPulsado rp = (RatonPulsado) ev;
						coordInicioRaton = rp.getPosicion();
						ObjetoGrafico og = v.getObjetoEnPosicion( rp.getPosicion() );
						if (og == null) {
							v.showMessage( "Inicio click en posici�n: " + rp.getPosicion() + " sin objeto" );
							moviendo = null;
						} else {
							moviendo = og;
							v.paraMovimiento( moviendo );
							moviendoOffsetX = (int) Math.round(rp.getPosicion().getX() - og.getX());
							moviendoOffsetY = (int) Math.round(rp.getPosicion().getY() - og.getY());
							v.showMessage( "Tocando objeto " + og.getName() );
						}
					} else if (ev instanceof RatonSoltado) {
						if (moviendo != null) {
							RatonSoltado rs = (RatonSoltado) ev;
							if (coordInicioRaton != null && coordInicioRaton.equals( rs.getPosicion() )) {
								// click en vez de drag
								v.traeObjetoAlFrente( moviendo );
								v.showMessage( "Traido al frente objeto " + moviendo.getName() );
							} else {
								// drag
								if (v.estaEnVentana( rs.getPosicion() )) {
									v.setPosGrafico( moviendo, aplicaOffset( rs.getPosicion() ) );
									v.showMessage( "Soltado objeto " + moviendo.getName() );
								} else {  // Drag off-screen
									v.muevePosGrafico( moviendo, aplicaOffset( coordInicioRaton ) );
									v.showMessage( "DRAG FUERA: Moviendo objeto " + moviendo.getName() + " a su posici�n inicial." );
								}
							}
							moviendo = null;
						}
					}
				}
				v.finish();
			} 
			}

	class HiloAnimacion extends Thread {
		@Override
		public void run() {
			while (!interrupted()) {
				try {
					Thread.sleep( tiempoFrameAnimMsg );
				} catch (InterruptedException e) {
					break;  // No har�a falta, el while se interrumpe en cualquier caso y se acaba el hilo
				}
				for (int i=animacionesPendientes.size()-1; i>=0; i--) {  // Al rev�s porque puede haber que quitar animaciones si se acaban
					Animacion a = animacionesPendientes.get(i);
					if (a.oj != null) a.oj.setLocation( 
						a.calcNextFrame( tiempoFrameAnimMsg ) );  // Actualizar animaci�n
					if (a.finAnimacion()) animacionesPendientes.remove(i);  // Quitar si se acaba
				}
				if (fondoAnimado && fondoRodando) {
					coorX1 -= pixDespAIzqda;
					coorX2 -= pixDespAIzqda;
					int x1 = (int) Math.round( coorX1 );
					int x2 = (int) Math.round( coorX2 );
					if (x1 < -fondo1.getWidth()) {  // Se sale fondo1 por la izqda
						coorX1 = coorX2 + fondo2.getWidth() - PX_SOLAPE_FONDOS;  // solapa pixels
						x1 = (int) Math.round( coorX1 );
					} else if (x2 < -fondo2.getWidth()) {  // Se sale fondo2 por la izqda
						coorX2 = coorX1 + fondo1.getWidth() - PX_SOLAPE_FONDOS;  // solapa pixels
						x2 = (int) Math.round( coorX2 );
					}
					if (x1<x2) { // muevo primero el de m�s a la derecha
						fondo1.setLocation( x2, 0 );
						fondo2.setLocation( x1, 0 );
					} else {
						fondo1.setLocation( x1, 0 );
						fondo2.setLocation( x2, 0 );
					}
					layeredPane.repaint();
				}
			}
		}
	}
	

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	/**
	 * Metodo para detener la musica una vez se cierra la ventana
	 */
	public void windowClosed(WindowEvent e) {
		
			try {
				apagarSonidos();
			} catch (LineUnavailableException | IOException
					| UnsupportedAudioFileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		
	}
	@Override
	public void windowOpened(WindowEvent e) {
		
	}
	/**
	 * Carga un archivo de musica para reproducirlo
	 * @throws LineUnavailableException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static void sonidos() throws LineUnavailableException, IOException, UnsupportedAudioFileException{
		Clip musica = AudioSystem.getClip();
		musica.open(AudioSystem.getAudioInputStream(new File("src/sounds/ABRost.wav")));
		musica.start();
	}
	/**
	 * Metodo para apagar la musica
	 * @throws LineUnavailableException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static void apagarSonidos() throws LineUnavailableException, IOException, UnsupportedAudioFileException{
		Clip musica = AudioSystem.getClip();
		musica.open(AudioSystem.getAudioInputStream(new File("src/sounds/ABRost.wav")));
		musica.start();
		musica.stop();
		
	}

}




	class Animacion {
		
		double xDesde;    // Desde qu� x
		double xHasta;    // hasta qu� x
		double yDesde;    // Desde qu� y
		double yHasta;    // hasta qu� y
		long msFaltan;    // en cu�ntos msg
		ObjetoGrafico oj; // objeto a animar
		public Animacion(double xDesde, double xHasta, double yDesde,
				double yHasta, long msFaltan, ObjetoGrafico oj) {
			this.xDesde = xDesde;
			this.xHasta = xHasta;
			this.yDesde = yDesde;
			this.yHasta = yHasta;
			this.msFaltan = msFaltan;
			this.oj = oj;
		}
		Point calcNextFrame( long msPasados ) {
			if (msFaltan <= msPasados) {  // Llegar al final
				msFaltan = 0;
				return new Point( (int)Math.round(xHasta), (int)Math.round(yHasta) );
			} else if (msPasados <= 0) {  // No se ha movido
				return new Point( (int)Math.round(xDesde), (int)Math.round(yDesde) );
			} else {  // Movimiento normal
				xDesde = xDesde + (xHasta-xDesde)/msFaltan*msPasados;
				yDesde = yDesde + (yHasta-yDesde)/msFaltan*msPasados;
				msFaltan -= msPasados;
				return new Point( (int)Math.round(xDesde), (int)Math.round(yDesde) );
			}
		}
	boolean finAnimacion() {
		return (msFaltan <= 0);
	}
	public boolean equals(Object obj) {
		if (!(obj instanceof Animacion)) return false;
		return (oj == ((Animacion)obj).oj);
	}

	public String toString() {
		return "Animacion (" + xDesde + "," + yDesde + ") -> ("
				+ xHasta + "," + yHasta + ") msg: " + msFaltan;
	}
	
	
}
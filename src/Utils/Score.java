package Utils;

import java.io.Serializable;

/**Clase que crea un objeto de tipo Score
 * @author Gorka, Jon, Xabier
 *
 */
public class Score implements Serializable {
	private static final long serialVersionUID = 1L;
	private int puntuacionMaxima;

	/**Constructor de objetos de tipo Score con 
	 * la puntuacionMaxima como parámetro
	 * @param puntuacionMaxima (el record de columnas pasadas en una sola partida)
	 */
	public Score(int puntuacionMaxima) {
		super();
		this.puntuacionMaxima = puntuacionMaxima;
	}

	public Score() {
		super();
	}

	public int getPuntuacionMaxima() {
		return puntuacionMaxima;
	}

	public void setPuntuacionMaxima(int puntuacionMaxima) {
		this.puntuacionMaxima = puntuacionMaxima;
	}
	
}

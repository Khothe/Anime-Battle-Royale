package Utils;

import java.io.*;


/**Clase que crea un fichero.dat donde almacenar un dato de 	
 * tipo Score y leerlo para su posterior uso
 * @author Gorka, Jon, Xabier
 */

public class FileManager {
	/**Metodo que devuelve el dato de tipo Score s
	 * existente en el ficher
	 * @return Score s
	 */
	public static Score readScoreFromFile() {
		java.io.ObjectInputStream ois=null;
		try {
			java.io.InputStream is = new java.io.FileInputStream( "score.dat" );
		    ois = new java.io.ObjectInputStream( is );
			Score s = (Score) ois.readObject();
			try {
				ois.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return s;
			
		} catch (Exception e) {
			// Se acabó el fichero
			
		}
		return new Score(0);
	}
	
	/**Metodo para actualizar el objeto de
	 * tipo Score guardado en el .dat
	 * o crear uno nuevo en caso de su ausencia
	 * @param s
	 */
	public static void saveScoreToFile(Score s){
		try{
			java.io.OutputStream os = new java.io.FileOutputStream( "score.dat" );
			java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream( os );
			oos.writeObject( s );
			oos.close();
		}catch(Exception e){
			System.out.println("error: saveScoreToFile ");
		}
		
	}
	/**Metodo main para crear el fichero .dat por primera vez inicializado a 0
	 * @param args
	 */
	public static void main(String []args){
		Score s = new Score(0);
		saveScoreToFile(s);
	}
	
}

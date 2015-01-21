package Graphics;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
/**Clase para la carga de imagenes de la ventana del menu
 * con constructores diferentes en funcion de como se le quiera hacer llegar la imagen
 * @author Gorka, Jon, Xabier
 */
public class CargaImagenMenu extends JPanel {

    
	private static final long serialVersionUID = 1L;
	private Image imagen;

    public CargaImagenMenu() {
    }
    /**Carga una imagen con su direccion introducida mediante un String
     * 
     * @param nombreImagen
     */
    public CargaImagenMenu(String nombreImagen) {
        if (nombreImagen != null) {
            imagen = new ImageIcon(getClass().getResource(nombreImagen)).getImage();
        }
    }
    /**Carga una imagen al usar la propia imagen directamente
     * 
     * @param imagenInicial
     */
    public CargaImagenMenu(Image imagenInicial) {
        if (imagenInicial != null) {
            imagen = imagenInicial;
        }
    }

    public void setImagen(String nombreImagen) {
        if (nombreImagen != null) {
            imagen = new ImageIcon(getClass().getResource(nombreImagen)).getImage();
        } else {
            imagen = null;
        }

        repaint();
    }

    public void setImagen(Image nuevaImagen) {
        imagen = nuevaImagen;

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);

            setOpaque(false);
        } else {
            setOpaque(true);
        }

        super.paint(g);
    }
}

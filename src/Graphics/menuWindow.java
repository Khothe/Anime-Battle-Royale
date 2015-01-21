package Graphics;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;


import MainPackage.ABR;
//import flappyPokemons.FlappyPokemons;
import Utils.FileManager;
import Utils.Score;
/**
 * Clase para crear la ventana que servira de menu
 * @author Gorka, Jon, Xabier
 *
 */
public class menuWindow extends JFrame implements ActionListener {


	private static final long serialVersionUID = 1L;
	private JPanel panelFondo;
	private JButton creditos = new JButton();
	private JButton highscore = new JButton();
	private JButton start = new JButton();
	private JButton exit = new JButton();

    
    private void initComponents() {

        panelFondo = new CargaImagenMenu();        

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("MENU ANIME BATTLE ROYALE");
        this.setLocationRelativeTo(null);
        panelFondo.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        panelFondo.setOpaque(false);

        creditos.setText("Credits");
        creditos.addActionListener(this); 
//      creditos.setFont(new Font("Dungeon", Font.BOLD | Font.ITALIC, 25));

        highscore.setText("Highscore");
        highscore.addActionListener(this);
//      highscore.setFont(new Font("Dungeon", Font.BOLD | Font.ITALIC, 25));
        
        start.setText("START");
        start.addActionListener(this);      
//      start.setFont(new Font("Dungeon", Font.BOLD | Font.ITALIC, 25));
        
        exit.setText("Exit");
        exit.addActionListener(this);
//      exit.setFont(new Font("Dungeon", Font.BOLD | Font.ITALIC, 25));
        

        GroupLayout panelFondoLayout = new GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(panelFondoLayout.createParallelGroup(Alignment.LEADING).addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(start, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(highscore, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(creditos, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(exit, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelFondoLayout.setVerticalGroup(panelFondoLayout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                .addContainerGap(220, Short.MAX_VALUE).addGroup(panelFondoLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(creditos)
                    .addComponent(highscore)
                    .addComponent(start)
                    .addComponent(exit)).addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap()
                .addComponent(panelFondo, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
        
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap()
                .addComponent(panelFondo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));

        pack();
        ((CargaImagenMenu) panelFondo).setImagen("A_B_R_fondo.jpg");
       
    }
    /**
     * Metodo para poner un icono de miniatura a la ventana
     */
    public menuWindow() {
    	
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(VentanaGrafica.class.getResource("ABR_logo.png")));
    	initComponents();
    	
    }
    /**
     * Metodo de gestion de eventos para cada boton del menu
     */
    public void actionPerformed(ActionEvent arg0) {
    	
    	Object boton = arg0.getSource();
    	
    	if ((JButton)boton == creditos){
    		
    			ventanaCreditos.creditosDelJuego();
    	}
    	
    	else if((JButton)boton == highscore){
    		
    			Score s = FileManager.readScoreFromFile();    			
    			JOptionPane.showMessageDialog(this, "The Highscore is "+ s.getPuntuacionMaxima());
    	}
    	
    	else  if ((JButton)boton == start){
    			this.dispose();
    			ABR tarea = new ABR();
    			Thread hilo = new Thread(tarea);
    			hilo.start();
    	}
    	
    	else if ((JButton)boton == exit){
    		Thread hilo = new Thread(new Exit());
    		hilo.start();
    	}
    	
    }
    /**
     * Clase de procesado del boton exit de modo que visualize una cuenta atras
     * @author Gorka, Jon, Xabier
     *
     */
    class Exit implements Runnable{
    	
    	@Override
    	public void run() {
    		
    		for(int i =3; i>=1; i--){
    			exit.setText(i+"");
    			try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
    		}
    		System.exit(0);
    		
    	}
    	
    }

    public static void main(String args[]) {
    	
    	menuWindow v=new menuWindow();
    	v.setLocationRelativeTo(null);
		v.setVisible(true);
     }
}

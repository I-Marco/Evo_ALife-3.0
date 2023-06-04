import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*; //For test

//import javafx.event.*;
import java.awt.event.*; // import java.awt.event.ActionEvent;
import java.io.*;
import java.awt.image.*; //import java.awt.image.BufferedImage;
import javax.imageio.*; // import javax.imageio.ImageIO;
import java.util.*; //import java.util.List;import java.util.ArrayList;
//import org.apache.commons.io.*;//import org.apache.commons.io.FilenameUtils; ELIMINAR
/**
 * Description of class Evo_ALife:
 * Evo_ALife project its a artificial enviroment "world" 
 * with singular "ALife" individuals and limited resources (colors)
 * 
 * TFG for Informatical degree in UNIR
 * 
 * @author Iñigo Marco Veintimillas 
 * @version 29-04-23
 */
public class Evo_ALife extends JFrame{
    // Fields ----------------------------------------------------------------------------
    // Static fields
    private static final String VERSION = "Enviroment ALife proyect v 2.1"  ;
    //private static final int DISPLAY_DELAY = 500;

    // Fields    
    private Env_ALife env_ALife;
    
    private Env_Panel env_Panel;
    private Env_Report_Panel env_Report_Panel;
    private Env_Status_Panel env_Status_Panel;
    
    //  ** Faltan Botones
    //private MyThread myThread;
    //private Semaphore semaphore;
    
    // Methods ---------------------------------------------------------------------------

    // Construcotors ============================

    /**
     * Constructor for objects of class Evo_ALife
     * No parameters
     * Create a new JFrame window to show de Evo_ALife display
     */
    public Evo_ALife(){
        env_Panel = new Env_Panel (this);
        env_Report_Panel = new Env_Report_Panel(this);
        env_Status_Panel = new Env_Status_Panel(this);
        env_ALife = new Env_ALife(this);
        
        //JFrame construction, GUI
        new JFrame(VERSION); //  ** Creo que no necesito nombre 
        JPanel btnBar = new JPanel();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        
        makeMenuBar(this);
        makeBtnBar(this,btnBar);// ???? por que try 93
        makeReportBar(this);
        makeImgBar(this);
        makeStatusBar(this);
        layoutFrame(this,btnBar);
        
        env_ALife.start();
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width/2 - this.getWidth()/2, d.height/2 - this.getHeight()/2);
        setVisible(true);   
        //codigo de test
        // Crear el thread
        //myThread = new MyThread(this,10,semaphore);
        //myThread.start();
        
    } // End Constructo - public Evo_ALife(MyImgPanel mP)

    // Public Methods and Fuctions ==============
    /**
     * Getter for Env_ALife variable. Return the simulation enviroment.
     * @param    none       - No parameters
     * @return   Env_ALife  - The enviroment we are simulating
     * 
     */
    public Env_ALife get_Env_Alife(){
        return env_ALife;
    }// End public Env_ALife get_Env_Alife()
    
    /**
     * Setter for Env_ALife variable. Mark the enviroment to be simulated
     * @param    Env_ALife  - The enviroment we are simulated
     * @return   none       - No palameters
     * 
     */
    public void set_Env_Alive(Env_ALife e){
        env_ALife = e;
        env_ALife.start(); //We need our new enviroment be running
        this.env_Panel.show_Env(e);
        this.env_Report_Panel.show_Env(e);
        this.env_Status_Panel.show_Env(e);
        //show_Env(Env_ALife e);
        //
        repaint();
    } // End public void set_Env_Alive(Env_ALife e)
      
    /**
     * Getter for env_Panel variable. Return the panel for simulation display
     * @param    none       - No parameters
     * @return   Env_Panel  - The panel for display graphic simulation
     * 
     */
    public Env_Panel get_Env_Panel(){
        return env_Panel;
    }// End public Env_ALife get_Env_Alife()
    
    /**
     * Setter for env_Panel variable. Set the display panel
     * @param    env_Panel  - Display panel
     * @return   none       - No parameters
     * 
     */
    public void set_Env_Panel(Env_Panel e){
        env_Panel = e;
    } // End public void set_Env_Panel(Env_Panel e)
        
    /**
     * Getter for env_Report_Panel variable. Return the panel for simulation reports or information
     * @param    none              - No parameters
     * @return   Env_Report_Panel  - The panel for display simulation reports
     * 
     */
    public Env_Report_Panel get_Env_Report_Panel(){
        return env_Report_Panel;
    }// End public Env_ALife get_Env_Alife()
    
    /**
     * Setter for env_Report_Panel variable. Set the reports panel
     * @param    env_Report_Panel  - Report panel
     * @return   none              - No Parameters
     * 
     */
    public void set_Env_Report_Panel(Env_Report_Panel e){
        env_Report_Panel = e;
    } // End public void set_Env_Report_Panel(Env_Report_Panel e)

    /**
     * Getter for env_Status_Panel variable. Return the panel for status display
     * @param    none       - No parameters
     * @return   Env_Status_Panel  - The panel to display text report information
     * 
     */
    public Env_Status_Panel get_env_Status_Panel(){
        return env_Status_Panel;
    }// End public Env_ALife get_Env_Alife()
    
    /**
     * public void set_env_Status_Panel(Env_Status_Panel e)
     * Setter for env_Status_Panel variable. Set the status report panel
     * @param    env_Status_Panel  - Report panel 
     * @return   none       - No parameters
     * 
     */
    public void set_env_Status_Panel(Env_Status_Panel e){
        env_Status_Panel = e;
    } // End public void env_Status_Panel(Env_Status_Panel e)

    /**
     * Add report from enviroment to the frame.
     * @param    String ??  - No parameters
     * @return   none       - The enviroment we are simulating
     * 
     */
    public void addReport(String rep){
    
    }//End public void addReport(String rep)    
    
    /**
     * Add report from enviroment to the frame.
     * @param    String ??  - No parameters
     * @return   none       - The enviroment we are simulating
     * 
     */
    public void visualiceEnv(Env_ALife env){
        env_Panel.show_Env(env.getLandImg(), env.getLifeImg());
        env_Report_Panel.show_Env(env);
        env_Status_Panel.show_Env(env);
    } // End public void visualiceEnv()

    /**
     * public static String getExtension(String fileName)
     * 
     * @param    String     - Archive name to extract the extension
     * @return   String     - The extension of archive
     * 
     */    
    public static String getExtension(String fileName){
        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1) {
            return fileName.substring(i + 1).toLowerCase();
        }
        return null;
    } // End public static String getExtension(String fileName)

    /**
     * public static String getFileName(String fileName)
     * 
     * @param    String     - Archive name to extract the filename out extension
     * @return   String     - The filename out extension
     * 
     */    

    public static String getFileName(String fileName){
        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1) {
            return fileName.substring(0, i).toLowerCase();
        }
        return fileName; // Return full string. We have not found a '.'
    } // End public static String getExtension(String fileName)
    
    // Serialización de un objeto en un archivo
    public static void guardarObjeto(Object objeto, String archivo) throws IOException {
        ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo));
        salida.writeObject(objeto);
        salida.close();
    }
    
    // Deserialización de un objeto de un archivo
    public static Object cargarObjeto(String archivo) throws IOException, ClassNotFoundException {
        ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo));
        Object objeto = entrada.readObject();
        entrada.close();
        return objeto;
    }
    
    // Private Methods and Fuctions =============

    // JFrame GUI display related
    private void makeMenuBar(JFrame frame) {
        // Not class fields since we don't acces to it from any splicit code
        JMenuBar menu = new JMenuBar();
        frame.setJMenuBar(menu) ;
        
        JMenu fileMenu = new JMenu ("File");
        menu.add(fileMenu);
        
        JMenuItem openFile = new JMenuItem("Open File.");
        openFile.addActionListener( (ActionEvent e) -> this.load_Env() );
        fileMenu.add(openFile);
        JMenuItem saveFile = new JMenuItem("Save File.");
        saveFile.addActionListener( (ActionEvent e) -> this.save_Env() );
        fileMenu.add(saveFile);
        JMenuItem closeFile = new JMenuItem("Close File.");
        closeFile.addActionListener( (ActionEvent e) -> this.close_Env() );
        fileMenu.add(closeFile);        
        fileMenu.addSeparator();
        JMenuItem close = new JMenuItem("Close aplication.");
        close.addActionListener( (ActionEvent e) -> System.exit(0) );
        fileMenu.add(close);    
        
        JMenu playMenu = new JMenu("Let's go");
        menu.add(playMenu);
        JMenuItem runWorld = new JMenuItem("Time go on... TEST");
        runWorld.addActionListener( (ActionEvent e) -> System.exit(0) );//this.env_ALife.eventTest() );
        playMenu.add(runWorld);        
        JMenuItem stopWorld = new JMenuItem("Time stop!");
        stopWorld.addActionListener( (ActionEvent e) -> this.env_ALife.killThread() ); //Algo tiene que hacer
        playMenu.add(stopWorld);
        
        //Test Menu
        JMenu testMenu = new JMenu("Test Functions");
        menu.add(testMenu);
        
        JMenuItem testNewWorld_1 = new JMenuItem("Create new enviroment Type 1");
        testNewWorld_1.addActionListener( (ActionEvent e) -> this.makeDefaultEnv() );
        testMenu.add(testNewWorld_1);
        
        JMenuItem testNewWorld_0 = new JMenuItem("Create new enviroment Type 0- Minimal Land");
        testNewWorld_0.addActionListener( (ActionEvent e) -> this.makeDefaulLandtEnv() );
        testMenu.add(testNewWorld_0);
        
        JMenuItem testNewWorld_1C = new JMenuItem("Create new enviroment Type 1C- Minimal Creature");
        testNewWorld_1C.addActionListener( (ActionEvent e) -> this.makeSimpleCreatureEnv() );
        testMenu.add(testNewWorld_1C);
        
        //
        
    } // End private void makeMenuBar(JFrame frame)
    
    // Desing the botton bar with some fast acces bottons like play ans stop simulation
    private void makeBtnBar(JFrame frame, JPanel btnBar) {//throws InterruptedException {
        btnBar.setPreferredSize(new Dimension(1200, 35));
        btnBar.setBackground(Color.blue);
        
        JButton btnPlay = new JButton("Go!!!");
        JButton btnStop = new JButton("RE-Start ||");
        btnBar.add(btnPlay);
        btnBar.add(btnStop);
        btnPlay.addActionListener((ActionEvent e) -> {
            //Poner en marcha
            //MultiTaskUtil.isThreadAlive(this.env_ALife,Thread.currentThread());
            //boolean t = this.env_ALife.get_isPaused();
            if (this.env_ALife.get_isPaused() ) { //si esta parado hay que ponerlo en marcha
                btnPlay.setText("Stop");
                this.env_ALife.resumeThread();
                //myThread.resumeThread(); // continuar la ejecución del thread
            } else {
                btnPlay.setText("Go!!!");
                this.env_ALife.pauseThread();
                //myThread.pauseThread(); // pausar la ejecución del thread
            }
        });

        btnStop.addActionListener((ActionEvent e) -> {
            //Detener marcha
            /*
            if (!this.env_ALife.get_isPaused() ) { //si esta en marcha hay que pararlo
                this.env_ALife.pauseThread();
            }
            */
            this.env_ALife.start();
        });
        /*
        btnStop.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
                // Parar simulación
                // Si conseguimos despertar al Thread Env_ALife habria que notificarlo aqui. Ahora se despierta solo
            } 
        } );      
        */
    } // End private void makeBtnBar(JFrame frame)
    
    //Dessing de "World" displayed image
    //private JPanel makeImgBar(JFrame frame) {
    private void makeImgBar(JFrame frame) {
        
        //imgBar = new MyImgPanel();
        /*
        try{
            //File selectedFile = new File("d:\Datos\Java\Iñigo\Evo-Live\imageviewer-final-copy\imagen.jpg");//d:\Datos\Java\Iñigo\Evo-Live\imageviewer-final-copy\imagen.jpg
            originalImage = ImageIO.read(new File("imagen.jpg"));
            imgBar = new MyImgPanel(originalImage);
        }catch (Exception e){
            System.out.println(e); 
            e.printStackTrace();        
        }
        */
        // Carga la primera imagen de mundo desde el mundo.
        env_Panel.setPreferredSize(new Dimension (600, 600));
        env_Panel.setBackground(Color.WHITE);
    }//private void makeImgBar(JFrame frame) 
    
    //Dessing de side BIG report barr ina panel
    private void makeReportBar(JFrame frame) {
        env_Report_Panel.setPreferredSize(new Dimension(120, 400));
        env_Report_Panel.setBackground(Color.GRAY);         
        
        // Añadir primer reporte.
        //JTextArea textA = new JTextArea();        
        //textA.setPreferredSize(new Dimension (110,390));
        //textA.setText("Report bar");
        env_Report_Panel.show_ReportMsg("Report bar working...");
        //env_Report_Panel.add(textA);                    
    }//private void makeReportBar(JFrame frame)
    
    //Desing the status bar to report textual coments and results down of frame
    private void makeStatusBar(JFrame frame) {
        env_Status_Panel.setPreferredSize(new Dimension(1200, 30));        
        env_Status_Panel.setBackground(Color.LIGHT_GRAY);
        /* Falta mandar el primer mensaje
        appMsg = new JLabel("App. running");
        appMsg.setPreferredSize(new Dimension(statusBar.getWidth(),statusBar.getHeight()));
        // Layout de statusbar si fuera necesario.
        env_Status_Panel.add(appMsg); // Como vamos a modificarlo??
        //PENDIENTE ver comentario superior
        */
    }//private void makeStatusBar(JFrame frame) 
    
    //Dessing frame Layout and add GUI elements
    //private void layoutFrame(JFrame frame, JPanel p) {
    private void layoutFrame(JFrame frame, JPanel btnBar) {
        JPanel contentPane = (JPanel) frame.getContentPane();
        // Sistema con border Layout
        contentPane.setLayout(new BorderLayout());
        contentPane.add(btnBar,BorderLayout.PAGE_START);
        contentPane.add(new JPanel().add(new JScrollPane(env_Report_Panel)),
            BorderLayout.LINE_START);
        contentPane.add(new JPanel().add(new JScrollPane(env_Panel)), 
            BorderLayout.CENTER);
        //contentPane.add(p, BorderLayout.CENTER);//imgBAr en el scroll Panel
        contentPane.add(env_Status_Panel, BorderLayout.PAGE_END);//
    }
        
    
    private void load_Env(){ //// ***** PENDINTE
        
        try{
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                //Manipular fichero seleccionado
                String selectedFileName = getFileName(selectedFile.getAbsolutePath());
                
                //Load ALife_Nutrient_Enviroment
                BufferedImage originalImage = ImageIO.read(new File(selectedFileName+".png"));
                Env_Panel.imageDisplay(originalImage, "Loaded img...");
                //Load Life enviroment
                    //Aun se como va a ir
                //Load enviroment variables
                java.util.List<Object> env_Vars = 
                    (java.util.List<Object>) Env_ALife.cargarObjeto(selectedFile+".e_v");
                //this.cargarObjeto(new File(selectedFile+".e_v"));
                //originalImage = ImageIO.read(selectedFile);
                
                //Env_ALife temp_env_ALife = new Env_ALife(this,originalImage,lifeEnv,env_Vars);
                Env_ALife temp_env_ALife = new Env_ALife(this,originalImage,null,env_Vars);
                this.set_Env_Alive(temp_env_ALife);

            }
        } catch (Exception e) {
            System.out.println(e); 
            e.printStackTrace();
        }
        //this.env_Panel.show_Env(null,null);
        //this.env_Report_Panel.show();
        //this.env_Status_Panel.show()        
    } //End public void load_Env()    

    private void save_Env() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                //String aux = selectedFile.getAbsolutePath();
                // retorna un File aux = selectedFile.getAbsoluteFile();
                String aux1 = selectedFile.getName();
                String aux2 = selectedFile.getPath();
                /*
                String extension = getExtension(selectedFile.getName());
                if (!extension.equalsIgnoreCase("png")) {
                    selectedFile = new File(selectedFile.toString() + ".png");
                }
                */
               
                //String selectedFileName = getFileName(selectedFile.getName());
                String selectedFileName = getFileName(selectedFile.getAbsolutePath());
                //this.env_ALife.getLandImg()
                ImageIO.write(this.env_ALife.getLandImg(), "png", 
                    new File(selectedFileName + ".png"));
                //eventList
                    //Aun no lo tengo claro
                //this.env_ALife.getEnvVars()
                Env_ALife.guardarObjeto(this.env_ALife.getEnvVars(), selectedFileName + ".e_v");
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //End private void save_Env()
        
    private void close_Env() {
        this.env_ALife = null;
        this.env_Panel.show_Env(null,null);
        //this.env_Report_Panel.show();
        //this.env_Status_Panel.show()
    } // End private void close_Env() 

    /**
     * Make a Default env for Creature test
     */
    private void makeDefaultEnv(){
        //Remove old enviroment
        if (env_ALife != null) this.env_ALife.killThread();
        this.env_ALife = new Env_ALife(this);
        //Land + Live + env_Variables.
        BufferedImage land = null;
        java.util.List<Object> env_Vars = null; // List in java.util and java.awt
        //lifeEnv = living creatures in schedule 
        Object lifeEnv;// = this.env_ALife;
        
        if (env_ALife != null) {
            env_Vars = env_ALife.getEnvVars();
        } else {
            //env_Vars = null previusly fixed
        }
        
        
        // 1 default land
        land = Env_Panel.getDefaultEnvImage();
        Graphics2D g2d = land.createGraphics();
        int width = land.getWidth();
        int height =land.getHeight();
        
        g2d.setColor(Color.BLACK); // fix background to BLACK 0, 0, 0
        g2d.fillRect(0, 0, width, height);
        // draw mid gray rectangle
        g2d.setColor(Color.GRAY); // fix background to BLACK 0, 0, 0
        g2d.fillRect(0 , (height/2)-land.getHeight()/20, width, height/10);
        
        //some especified zones
        g2d.setColor(new Color(180, 0, 0)); // fix background to BLACK 0, 0, 0
        g2d.fillOval(width/3-land.getWidth()/16 , (height/3)-land.getHeight()/20, width/8, height/10);
        
        g2d.setColor(new Color(0, 180, 0)); // fix background to BLACK 0, 0, 0
        g2d.fillOval(width/2-land.getWidth()/16 , (height*2/3)-land.getHeight()/20, width/8, height/10);
        
        g2d.setColor(new Color(0, 0, 180)); // fix background to BLACK 0, 0, 0
        g2d.fillOval(width*2/3-land.getWidth()/16 , (height/3)-land.getHeight()/20, width/8, height/10);
        //Env_Panel.imageDisplay(land,"From Boton press...");
        // Default land done
        
        //Create some creatures.
        ArrayList<Creature> listOfCreatures = new ArrayList<Creature>();
        int[] haveR = {100,0,0};
        int[] needR = {1,0,0};
        int liveExp = 10000;
        
        //Mid
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/2, height/2),null,liveExp,haveR,needR));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/2+2, height/2),null,liveExp,haveR,needR));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/2-2, height/2),null,liveExp,haveR,needR));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/2, height/2+2),null,liveExp,haveR,needR));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/2, height/2-2),null,liveExp,haveR,needR));

        int [] haveG = {0,100,0};
        int[] needG = {0,1,0};
        
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/3, height/2),null,liveExp,haveG,needG));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/3+2, height/2),null,liveExp,haveG,needG));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/3-2, height/2),null,liveExp,haveG,needG));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/3, height/2+2),null,liveExp,haveG,needG));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/3, height/2-2),null,liveExp,haveG,needG));

        int [] haveB = {0,0,100};
        int[] needB = {0,0,1};
        
        listOfCreatures.add(new Creature(this.env_ALife,new Point(2*width/3, height/2),null,liveExp,haveB,needB));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(2*width/3+2, height/2),null,liveExp,haveB,needB));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(2*width/3-2, height/2),null,liveExp,haveB,needB));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(2*width/3, height/2+2),null,liveExp,haveB,needB));
        listOfCreatures.add(new Creature(this.env_ALife,new Point(2*width/3, height/2-2),null,liveExp,haveB,needB));
        
        lifeEnv = listOfCreatures; // For easy reading
        Env_ALife temp_env_ALife = new Env_ALife(this,land,lifeEnv,env_Vars);
        this.set_Env_Alive(temp_env_ALife);
    } // End private void makeDefaultEnv()

    /**
     * Make a Default env for Land test
     */
    private void makeDefaulLandtEnv(){
        java.util.List<Object> env_Vars = null; // List in java.util and java.awt
        
        if (env_ALife != null) {
            env_Vars = env_ALife.getEnvVars();
            this.env_ALife.killThread();
        } else {
            //env_Vars = null previusly fixed
        }
        
        //Remove old enviroment
        this.env_ALife = new Env_ALife(this);
        //Land + Live + env_Variables.
        BufferedImage land_Temp = new BufferedImage(
            (int)(this.env_Panel.getPreferredSize()).getWidth(), (int)(this.env_Panel.getPreferredSize()).getHeight(),
            BufferedImage.TYPE_INT_ARGB);
        
        //lifeEnv = living creatures in schedule 
        Object lifeEnv;// = this.env_ALife;
        
        // 1 default land
        //land_Temp = Env_Panel.getDefaultEnvImage(6,6);
        land_Temp = Env_Panel.getDefaultEnvImage(30,30);
        Graphics2D g2d = land_Temp.createGraphics();
        int width = land_Temp.getWidth();
        int height =land_Temp.getHeight();
        
        //g2d.setColor(Color.BLACK); // fix background to BLACK 0, 0, 0
        //g2d.fillRect(0, 0, width, height);

        //some especified zones
        g2d.setColor(new Color(180, 0, 0)); // fix background to BLACK 0, 0, 0
        g2d.fillRect(1 , 1, 1, 1);
        
        g2d.setColor(new Color(0, 180, 0)); // fix background to BLACK 0, 0, 0
        g2d.fillRect(1 , 4, 1, 1);
        
        g2d.setColor(new Color(0, 0, 180)); // fix background to BLACK 0, 0, 0
        g2d.fillRect(4 , 2, 1, 1);
        
        
        //Env_Panel.imageDisplay(land_Temp,"From Boton press...");
        // Default land_Temp done
            
        Env_ALife temp_env_ALife = new Env_ALife(this,land_Temp,null,env_Vars);
        this.set_Env_Alive(temp_env_ALife);        
        
    } //End private void makeDefaulLandtEnv()

    /**
     * Make a environment for simple creature test
     */
    private void makeSimpleCreatureEnv(){
        java.util.List<Object> env_Vars = null; // List in java.util and java.awt
        
        if (env_ALife != null) {
            env_Vars = env_ALife.getEnvVars();
            this.env_ALife.killThread();
        } else {
            //env_Vars = null previusly fixed
        }
        
        //Remove old enviroment
        
        this.env_ALife = new Env_ALife(this);
        //Land + Live + env_Variables.
        BufferedImage land_Temp = new BufferedImage(
            (int)(this.env_Panel.getPreferredSize()).getWidth(), (int)(this.env_Panel.getPreferredSize()).getHeight(),
            BufferedImage.TYPE_INT_ARGB);
        
        //lifeEnv = living creatures in schedule 
        Object lifeEnv;// = this.env_ALife;
        
        // 1 default land
        //land_Temp = Env_Panel.getDefaultEnvImage(6,6);
        land_Temp = Env_Panel.getDefaultEnvImage(30,30);
        Graphics2D g2d = land_Temp.createGraphics();
        int width = land_Temp.getWidth();
        int height =land_Temp.getHeight();
        
        //g2d.setColor(Color.BLACK); // fix background to BLACK 0, 0, 0
        //g2d.fillRect(0, 0, width, height);

        //some especified zones
        g2d.setColor(new Color(180, 0, 0)); // fix background to BLACK 0, 0, 0
        g2d.fillRect(1 , 1, 1, 1);
        
        g2d.setColor(new Color(0, 180, 0)); // fix background to BLACK 0, 0, 0
        g2d.fillRect(1 , 4, 1, 1);
        
        g2d.setColor(new Color(0, 0, 180)); // fix background to BLACK 0, 0, 0
        g2d.fillRect(4 , 2, 1, 1);
        
        
        //Env_Panel.imageDisplay(land_Temp,"From Boton press...");
        // Default land_Temp done
        
        //Create some creatures.
        ArrayList<Creature> listOfCreatures = new ArrayList<Creature>();
        int[] haveR = {100,0,0};
        int[] needR = {1,0,0};
        int liveExp = 4500;
        
        //Red Creature ...
        listOfCreatures.add(new Creature(this.env_ALife,new Point(1, 1),null,liveExp,haveR,needR));
        /*
        int [] haveG = {0,100,0};
        int[] needG = {0,1,0};
        
        listOfCreatures.add(new Creature(this.env_ALife,new Point(width/3, height/2),null,liveExp,haveG,needG));

        int [] haveB = {0,0,100};
        int[] needB = {0,0,1};
        
        listOfCreatures.add(new Creature(this.env_ALife,new Point(2*width/3, height/2),null,liveExp,haveB,needB));
        */
       
        lifeEnv = listOfCreatures; // For easy reading               
        
        // Create Test Environment
        Env_ALife temp_env_ALife = new Env_ALife(this,land_Temp,lifeEnv,env_Vars);
        //Env_ALife temp_env_ALife = new Env_ALife(this,land_Temp,null,env_Vars);
        this.set_Env_Alive(temp_env_ALife);        
        
    } //End private void makeDefaulLandtEnv()    
    // Main if needed --------------------------------------------------------------------    
    
    /**
     * An main method - main start method for the Evo_ALife project
     * 
     * @param  none     - No parametes needed
     * @return     void - No return data 
     */
    
    public static void main(String args[]){
        new Evo_ALife();
    }
}
/*
    Reflexiones.
    Panel de reportes y de estdisticas.
        Posible clase de reportes que genere los reportes en archivo y en panel 
    
*/
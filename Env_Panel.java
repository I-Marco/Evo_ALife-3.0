import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

/**
 * Write a description of class Env_Panel here.
 * 
 * @author Iñigo Marco
 * @version (a version number or a date)
 */
public class Env_Panel extends JPanel
{
    public static int DEFAULT_WIDTH = 600;//600
    public static int DEFAULT_HEIGHT = 400;//400

    // Fields ----------------------------------------------------------------------------
    
    private Evo_ALife caller = null;
    
    private BufferedImage landground, lifeground; 
    private BufferedImage back, front; // back and front images to display

    private int x = 0, y = 0; // Position to start display when not full
    private int width = DEFAULT_WIDTH, height =DEFAULT_HEIGHT; // Size to display when not full

    // Methods ---------------------------------------------------------------------------

    // Constructors =============================

    /**
     * Constructor : public Env_Panel(Evo_ALife e)
     * Info.
     * 
     * @param    - Evo_ALife , is the caller simulation JFrame
     * @return   - None 
     */
    public Env_Panel(Evo_ALife e){
        caller = e;
    } // End constructor: public Env_Panel(Evo_ALife e)


    
    // Public Methods and Fuctions ==============

    // Getters and Setters ----------------------
    
    /**
     * public void set_landground(BufferedImage land)
     * Set land as private variable of land enviroment imagen.
     * 
     * @param    - BufferedImage
     * @return   - None 
     */        
    public void set_landground(BufferedImage land){
        landground = land;
    } // End public void set_landground(BufferedImage land)

    /**
     * public BufferedImage get_landground()
     * Get the private variable of land enviroment imagen.
     * 
     * @param    - None 
     * @return   - BufferedImage
     */        
    public BufferedImage get_landground(){
        return landground;
    } // End public BufferedImage get_landground()

    /**
     * public void set_lifeground(BufferedImage life)
     * Set life as private variable of live creatures map.
     * 
     * @param    - BufferedImage
     * @return   - None 
     */    
    public void set_lifeground(BufferedImage life){
        lifeground = life;
    } // End public void set_lifeground(BufferedImage life)

    /**
     * public BufferedImage get_lifeground()
     * Get private variable of live creatures map.
     * 
     * @param    - None 
     * @return   - BufferedImage
     */    
    public BufferedImage get_lifeground(){
        return lifeground;
    } // End public BufferedImage get_lifeground()

    // End Getters and Setters ------------------


    /**
     * Paint method overrided
     * Display de new graphical element
     * 
     * @param    g: Graphics
     * @return   none 
     */
    public void paint(Graphics g){
        super.paint(g); //Call its mother class method
        //Mount back image copy to front and display....
        if (this.front == null) front = Env_Panel.getDefaultEnvImage();
        super.paintComponent(g);
        //Make fron same to background worked imagen and display it.
        g.drawImage(front, 0, 0, null);
        
    } //End public void paint(Graphics g)


    /**
     * public void show_Env(BufferedImage land, BufferedImage life)
     * Display in Panel the envoroment images passed by parameters
     * 
     * @param    - BufferedImage land, BufferedImage life
     * @return   - None
     */  
    public void show_Env(BufferedImage land, BufferedImage life){
        set_landground(land);
        set_lifeground(life);
        
        show_Env();
        //repaint();
    } // End public void show_Env(BufferedImage land, BufferedImage life)
    
    /**
     * public void show_Env()
     * Display in Panel the enviroment stored in internal variables
     * 
     * @param    - None
     * @return   - None
     */
    public void show_Env(){
        // Mezclar LifeImg sobre el fondo landImg 
        // Resizes resamples posicionamientos etc...
        // landground = getDefaultEnvImage();
        if (landground == null) landground = getDefaultEnvImage();
        if (lifeground == null) lifeground = getDefaultEnvTransparentImage();
        

        if (true){
        this.setSize(landground.getWidth(), landground.getHeight());
        
        this.back = new BufferedImage(landground.getWidth(), landground.getHeight(), 
            BufferedImage.TYPE_INT_ARGB); // Reset back
        this.back = landground;

        //Mix with Lifeground image 
        if(landground.getWidth() != lifeground.getWidth() || landground.getHeight() != lifeground.getHeight()){
            int breakpoint =1;
            this.lifeground = resizeImage(this.lifeground, landground.getWidth(), landground.getHeight());
        }
        
        Graphics2D g2d = this.back.createGraphics();
        g2d.drawImage(lifeground, 0, 0, null);
        g2d.dispose();
        
        //Resize back to fit in panel starting in x,y and with width and height
        if (this.back.getWidth() >= (x + width) && this.back.getHeight() >= (y +height))
            this.back = this.back.getSubimage(x, y, width, height);
        int pw =(int)(this.getPreferredSize()).getWidth();
        int ph =(int)(this.getPreferredSize()).getHeight();
        if (pw != 0 && ph != 0)
            this.back = resizeImage(this.back, pw, ph);
            
        else 
            this.back = resizeImage(this.back, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            
        //Resize life to fit in panel starting in x,y and with width and height??

        //this.back = resizeImage(this.back, 600, 400);
        

        //Make it front
        this.front = this.back;
        
        }

        
        //g2d.dispose(); 
        repaint();
    } // End public void show_Env()
    
    /**
     * public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight)
     * Resize a BufferedImage to new dimensions
     * 
     * @param    - BufferedImage originalImage, int newWidth, int newHeight
     * @return   - BufferedImage
     */
    public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        return resizedImage;
    }  // End public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight)  
    
    /**
     * public void show_Env(Env_ALife e)
     * Display the enviroment caracteristics in panel
     * 
     * @param    - Env_ALife, ALife enviroment to be displayed
     * @return   - None
     */
    public void show_Env(Env_ALife e){
        this.set_lifeground(e.getLifeImg());
        this.set_landground(e.getLandImg());
        show_Env();
    } // End public void show_Env(Env_ALife e)
    
    // TEST UTILITY FUNCTION AND METHODS -----------------------------------------------------------------
    
    public static void imageDisplay(BufferedImage i, String s){
        JLabel label = new JLabel(new ImageIcon(i));
        JFrame frame = new JFrame(s);
        frame.add(label);
        frame.pack();
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setVisible(true);        
        
    }// End public static void imageDisplay(BufferedImage i)
    
    public static BufferedImage getDefaultEnvImage(){
        BufferedImage i = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = i.createGraphics();
        g2d.setColor(Color.BLACK); // fix background to BLACK 0, 0, 0 -- Color(0, 0, 0, 0) Transparente
        g2d.fillRect(0, 0, i.getWidth(), i.getHeight()); 
        //Env_Panel.imageDisplay(i,"Default imagen");
        g2d.dispose();
        return i;
    } //public static BufferedImage getDefaultEnvImage()

    public static BufferedImage getDefaultEnvImage(int w, int h){
        BufferedImage i = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = i.createGraphics();
        g2d.setColor(Color.BLACK); // fix background to BLACK 0, 0, 0
        g2d.fillRect(0, 0, i.getWidth(), i.getHeight()); 
        //Env_Panel.imageDisplay(i,"DEfault imagen");
        g2d.dispose();
        return i;
    } //public static BufferedImage getDefaultEnvImage()
        
    public static BufferedImage getDefaultEnvTransparentImage(){
        BufferedImage i = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = i.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        //g2d.setColor(Color.BLACK); // fix background to BLACK 0, 0, 0
        g2d.fillRect(0, 0, i.getWidth(), i.getHeight()); 
        g2d.dispose();
        //Env_Panel.imageDisplay(i,"DEfault background imagen");
        return i;
    } //public static BufferedImage getDefaultEnvImage()
    
    public static BufferedImage getDefaultEnvTransparentImage(int w, int h){
        BufferedImage i = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = i.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        //g2d.setColor(Color.BLACK); // fix background to BLACK 0, 0, 0
        g2d.fillRect(0, 0, i.getWidth(), i.getHeight()); 
        g2d.dispose();
        //Env_Panel.imageDisplay(i,"DEfault background imagen");
        return i;
    } //public static BufferedImage getDefaultEnvImage()
    
    
    // Private Methods and Fuctions =============
    
} // End Class Env_Panel
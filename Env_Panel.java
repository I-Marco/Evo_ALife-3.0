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

    private boolean hiddeLand = false; // Hidde land image

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
     * public synchronized void setHiddeLand(boolean b)
     * 
     * Set hiddeLand as private variable of land enviroment imagen.
     * @param    - boolean
     */
    public synchronized void setHiddeLand(boolean b){
        hiddeLand = b;
    } // End public synchronized void setHiddeLand(boolean b)

    /**
     * public synchronized boolean getHiddeLand()
     * Get the private variable of land enviroment imagen.
     * @param   - None
     * @return  - boolean
     */
    public synchronized boolean getHiddeLand(){
        return hiddeLand;
    } // End public synchronized boolean getHiddeLand()

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
        // make a buffered image with the transparency of the landground dimensions
        BufferedImage aux_landground = null;
        if (this.hiddeLand)  aux_landground = Env_Panel.getDefaultEnvTransparentImage(landground.getWidth(), landground.getHeight(), false);
        else aux_landground = landground;

        //Fix enviroment image to panel size
        int pw =(int)(this.getPreferredSize()).getWidth();
        int ph =(int)(this.getPreferredSize()).getHeight();
        //Create new back image as landground size and content
        this.back = new BufferedImage(aux_landground.getWidth(), aux_landground.getHeight(), BufferedImage.TYPE_INT_ARGB); // Reset back
        this.back.getGraphics().drawImage(aux_landground, 0, 0, null);//put landground in back
        //Resize back to fit in panel starting in x,y and with width and height 
        if (pw != 0 && ph != 0)
            this.back = resizeImage(aux_landground, pw, ph);
        else 
            this.back = resizeImage(aux_landground, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        //Visualize final dimensions.
        int w = back.getWidth();
        int h = back.getHeight();
        this.setSize(w, h);
        //Adjust life image to visualize size
        //for test
        int breakpointLGw = lifeground.getWidth();
        int breakpointLGh = lifeground.getHeight();
        if(w != lifeground.getWidth() || h != lifeground.getHeight()){
                this.lifeground = resizeImage(this.lifeground, w, h);
        }
        //overlay lifeground over landground
        Graphics2D g2d = this.back.createGraphics();
        g2d.drawImage(lifeground, 0, 0, null); //Draw life over land
        g2d.dispose();
        //Where must be visualized the enviroment 
        //(x,y start position to visualize when zoom in and width and height to visualize)
        //if (aux_landground.getWidth() >= (x + width) || this.back.getHeight() >= (y +height))
        //    this.back = this.back.getSubimage(x, y, width, height); //better if scroll works

/*
        if (false){
            int w = landground.getWidth();
            int h = landground.getHeight();
            this.setSize(w, h);
        
            //put landground in back
            this.back = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); // Reset back
            this.back.getGraphics().drawImage(aux_landground, 0, 0, null);

            //Mix with Lifeground image and resize to landground dimensions
            if(w != lifeground.getWidth() || h != lifeground.getHeight()){
                int breakpoint =1;
                this.lifeground = resizeImage(this.lifeground, w, h);
            }
        
            //overlay lifeground over landground
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
            //for test
        } // End if (true)
*/
        this.front = this.back;
        int breakpointh = front.getHeight();
        int breakpointw = front.getWidth();
        int breakpoint = 1;
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
    
    public static BufferedImage getDefaultEnvTransparentImage(int w, int h, boolean allowScale){
        int scale = 1;
        //for test
        int breakpoint1 = DEFAULT_WIDTH * DEFAULT_HEIGHT;
        int breakpoint2 = (DEFAULT_WIDTH * DEFAULT_HEIGHT)/5;
        int breakpoint3 = w * h;
        boolean cond1 = (DEFAULT_WIDTH * DEFAULT_HEIGHT)/5 > w * h;
        boolean cond2 = DEFAULT_WIDTH * DEFAULT_HEIGHT > w * h;
        if (allowScale){
            if ( (DEFAULT_WIDTH * DEFAULT_HEIGHT)/5 > w * h ) scale = 10;
            else if ( DEFAULT_WIDTH * DEFAULT_HEIGHT > w * h ) scale = 4;
            w = w * scale;
            h = h * scale;
        }
        BufferedImage i = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = i.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        //g2d.setColor(Color.BLACK); // fix background to BLACK 0, 0, 0
        g2d.fillRect(0, 0, i.getWidth(), i.getHeight()); 
        g2d.dispose();
        //Env_Panel.imageDisplay(i,"DEfault background imagen");
        return i;
    } //public static BufferedImage getDefaultEnvImage()
    
    /**
     * public static BufferedImage copyBufferedImage(BufferedImage i)
     * 
     * Copy a BufferedImage to a new one
     * @param i BufferedImage to be copied
     * @return BufferedImage copy of i
     */
    public static BufferedImage copyBufferedImage(BufferedImage i){
        BufferedImage copy = new BufferedImage(i.getWidth(), i.getHeight(), i.getType());
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(i, 0, 0, null);
        g2d.dispose();
        return copy;
    } // End public static BufferedImage copyBufferedImage(BufferedImage i)

    // Private Methods and Fuctions =============
    
} // End Class Env_Panel
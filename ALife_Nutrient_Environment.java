import java.awt.image.*;
import java.awt.*;
import java.util.concurrent.*;

/**
 * Write a description of class ALife_Nutrient_Enviroment here.
 * 
 * @author Iñigo Marco
 * @version 29-05-23
 */
public class ALife_Nutrient_Environment implements Runnable
{
    // Constants
    public static final int SPREADMADRADIUS = 5; //Max time spread recursive //OverFlow error control
    public static Long DEFAULT_LifeDelay = Long.valueOf(100);


    // Env variables //necesitamos meterlas en las variables de entorno
    private int SPREADCuantity = 200; //Cuantity of resource to spread arond divided
    private int SPREADUmbral = 225; //Umbral of resource to spread
    private int INC_r = 5;//Increment of resource red
    private int INC_g = 5;//Increment of resource green
    private int INC_b = 5;//Increment of resource blue
    //private boolean doSpread = true;//Do spread or not NOT IMPLEMENTED
    //private boolean flatEnv = false;//Flat or rounded enviroment NOT IMPLEMENTED
    
    // Fields ----------------------------------------------------------------------------
    private Env_ALife env_ALive; // Enviroment 
    private BufferedImage backLand = null;// BackLand image
    private BufferedImage frontLand = null;// FrontLand image
    private Long lifeDelay = Long.valueOf(DEFAULT_LifeDelay);// -----> This parameters should be save in env_Vars
    
    private Semaphore semaphore; // Semaphore to control concurrency
    
    // Methods ---------------------------------------------------------------------------
    
    // Constructors ============================
    
    /**
     * Constructor for objects of class ALife_Nutrient_Enviroment
     * @param   - Env_ALife
     * Create e simulation nutrient enviroment
     */
    public ALife_Nutrient_Environment(Env_ALife env, Semaphore s){
        env_ALive = env;
        semaphore = s;
        frontLand = Env_Panel.getDefaultEnvImage();
    }
    
    /**
     * Constructor for objects of class ALife_Nutrient_Enviroment
     * @param   - Env_ALife
     * Create e simulation nutrient enviroment
     */
    public ALife_Nutrient_Environment(Env_ALife env, Semaphore s, BufferedImage land){
        env_ALive = env;
        semaphore = s;
        frontLand = land;
        lifeDelay = Long.valueOf(DEFAULT_LifeDelay);
        //Env_Panel.imageDisplay(frontLand,"From ALife_Nutrient");
    } // End Constructor public ALife_Nutrient_Enviroment()
    
    // END Constructors ============================
    
    // Public Methods and Fuctions ==============
    
    // =====Getter and Setters
    
    /**
     * public synchronized String getResourceAddbyTime()
     * Return a String with the increment of each resource type and the delay of them
     * 
     * @param    None
     * @return   String
     */
    public synchronized String getResourceAddByTime(){
        String r = "No computado.";
        r = "["+this.INC_r+"R, "+this.INC_g+"G, "+this.INC_b+"B]/"+this.lifeDelay;
        return r;
    }
    
    /**
     * public void setLand(BufferedImage i)
     * 
     * @param    BufferedImage
     * @return   None
     */
    public synchronized void setLandImg(BufferedImage i){
        //backLand = i;
        synchronized (frontLand){
            frontLand = i;
        }
    } // End public synchronized void setLand(BufferedImage i)
    
    /**
     * public BufferedImage getLandImg()
     * 
     * @param    None
     * @return   BufferedImage
     */
    public synchronized BufferedImage getLandImg(){
        return frontLand;
    } // End public synchronized BufferedImage getLandImg()
    
    /**
     * public void setSpread(boolean s)
     * 
     * @param    boolean
     * @return   None
     */
    public synchronized void setLifeDelay(Long ld){
        if (ld == null){
            ld = Long.valueOf(DEFAULT_LifeDelay);
        }
        this.lifeDelay = ld;
    } // End public synchronized void setLifeDelay(Long ld)
    
    /**
     * public synchronized Long getLifeDelay()
     * 
     * @param    None
     * @return   Long
     */
    public synchronized Long getLifeDelay(){
        return this.lifeDelay;
    } // End public synchronized void setLifeDelay(Long ld)
    
    /**
     * public synchronized int[] nutrient(int x,int y)
     * 
     * @param    int x - position x
     * @param    int y - Position y
     * @return   int[] - array with the nutrient in the position
    */
    public synchronized int[] nutrient(int x,int y){
        /*
                Color pixelColor = new Color(frontLand.getRGB(x, y));
   
        int sr = 0, r = pixelColor.getRed();
        int sg = 0, g = pixelColor.getGreen();
        int sb = 0, b = pixelColor.getBlue();
        */
        Color pixelColor = new Color(getLandImg().getRGB(x, y));
        int[] ground = {pixelColor.getRed(), pixelColor.getGreen(),pixelColor.getBlue()};
        return ground;
    }// End public synchronized int[] nutrient(int x,int y)

    // ===== END Getter and Setters

    /**
     * Run() - implements of Runnable method Run
     * 
     * @param    No parameter
     * @return   No returned value
     */
    @Override
    public void run(){
        try{
            semaphore.acquire();
            //this.env_ALive.acquire();
            //this.env_ALive.addWaitForThreads();
            
            // For concurrency Log
            //MultiTaskUtil.threadMsg("===== Semaphore ADQUIRED ("+semaphore.availablePermits()+").................");
            //work
            synchronized (this){
                grownMap();
            }
            
            try{
                Thread.sleep(Env_ALife.CTE_TIEMPO_CEDER); // ceder tiempo de computo
                //Thread.currentThread().sleep(Env_ALife.CTE_TIEMPO_ESPERA_LARGA); // ceder tiempo de computo
                //System.out.println(Thread.currentThread().getName()+" - "+env_ALive.getTime());
            } catch (java.lang.InterruptedException ie){
                ie.printStackTrace();
            }              
        
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{ //finally block to release resource. Always executed.
            //When we have to run again.    
            env_ALive.addEvent(env_ALive.getTime()+lifeDelay, this);  
            //
            semaphore.release();
            //this.env_ALive.release();
            //MultiTaskUtil.threadMsg("Finalizado Thread !!"+semaphore.availablePermits()); //For concurrency log
            this.env_ALive.MyNotifyAll();
        }

        //env_ALive.removeWaitForThreads();
    } // End public void run()    

    /**
     * public int[] getNutrient(Point p, int[] fResN, Creature eater)
     * try to get fResN reources from enviroment
     * @param    Point the position on ALife_Nutrient_Evironment where we want get resource
     * @param    int[] the resource cuantity that want to get
     * @param    Creature the creature that's trying to eat
     * @return   int that cratures got succesfully 
     */
    public int[] getNutrient(Point p, int[] fResN, Creature eater){
        int[] fResNeed = new int[fResN.length];
        MultiTaskUtil.copyIntArrayContent(fResNeed, fResN);
        synchronized (this){
            //int r,g,b;
            //Make new Backland copy of primary imga frontland
            this.backLand = new BufferedImage(this.frontLand.getWidth(),
            this.frontLand.getHeight(), this.frontLand.getType());
            Graphics2D g2d = this.backLand.createGraphics();
            g2d.drawImage(this.frontLand, 0, 0, null);
            //g2d.dispose();
            Color pixelColor = new Color(backLand.getRGB(p.x, p.y));
            int[] ground = {pixelColor.getRed(), pixelColor.getGreen(),pixelColor.getBlue()};
            for(int co = 0; co < ground.length ; co++){
                ground[co] -= fResNeed[co];
                if (ground[co]<0){
                    fResNeed[co] += ground[co]; //the number under 0 have not been taken
                    ground[co] = 0;
                }
            }
            pixelColor= new Color(ground[0], ground[1], ground[2]);
            backLand.setRGB(p.x, p.y, pixelColor.getRGB());
            this.setLandImg(backLand);
            //Refresh imagen??                       
        }
        return fResNeed;
    }
    
    /**
     * public int[] dropNutrient(Point p, int[] foodResourceDrop, Int_ALife_Creature eater)
     * Try to drop foodResourceDrop resources in the enviroment
     * @param    Point the position on ALife_Nutrient_Evironment where we want get resource
     * @param    int[] the resource cuantity that want to get
     * @param    Creature the creature that's trying to eat
     * @return   int that cratures got succesfully
     */
    public int[] dropNutrient(Point p, int[] foodResourceDrop, Int_ALife_Creature eater){
        synchronized (this){
            //int r,g,b;
            //Make new Backland copy of primary imga frontland
            this.backLand = new BufferedImage(this.frontLand.getWidth(),
                this.frontLand.getHeight(), this.frontLand.getType());
            Graphics2D g2d = this.backLand.createGraphics();
            g2d.drawImage(this.frontLand, 0, 0, null);
            //g2d.dispose();
            Color pixelColor = new Color(backLand.getRGB(p.x, p.y));
            int[] ground = {pixelColor.getRed(), pixelColor.getGreen(),pixelColor.getBlue()};
            for(int co = 0; co < ground.length ; co++){
                ground[co] += foodResourceDrop[co];
                    if (ground[co] >  SPREADUmbral *2){ //q se expanda pero solo 2 veces
                    foodResourceDrop[co] = ground[co] - SPREADUmbral;
                    ground[co] = SPREADUmbral;
            }
            }
            this.germine(p.x, p.y, 0, 0, 0, 01); //?? REVISA ESTO
            
            pixelColor= new Color(ground[0], ground[1], ground[2]);
            backLand.setRGB(p.x, p.y, pixelColor.getRGB());
            this.setLandImg(backLand);
            //Refresh imagen??                       
        }
        //return foodResourceNeed;
        return foodResourceDrop;
    } // End public int[] dropNutrient(Point p, int[] foodResourceDrop, Int_ALife_Creature eater)
    
    // Private Methods and Fuctions =============
    
    /**
     * private void  grownMap()
     * 
     * @param    None
     * @return   None
     */
    private void  grownMap(){
        //read al pixels and "grown them"
        int r,g,b;
        //this.backLand.dispose(); // tratar de liberar memoria
        //Make new Backland copy of primary imga frontland
        this.backLand = new BufferedImage(this.frontLand.getWidth(),
            this.frontLand.getHeight(), this.frontLand.getType());
        Graphics2D g2d = this.backLand.createGraphics();
        g2d.drawImage(this.frontLand, 0, 0, null);
        //g2d.dispose();
        
        int maxH = frontLand.getHeight()-1;
        int maxW = frontLand.getWidth()-1;
        for(int x=0; x <= maxW; x++)
            for(int y=0; y <= maxH; y++){
                grownPoint(x,y,this.INC_r,this.INC_g,this.INC_b);
        } //End for j and i
        
        //MultiTaskUtil.threadMsg(env_ALive.getTime()+" - "+ "Rune DONE"); //For concurrency log
                
        this.setLandImg(backLand); //send new image to frontline
        //repaint ??
    } // End private void  grownMap()
    
    /**
     * private void grownPoint(int x,int y)
     * 
     * @param    int x - position x
     * @param    int y - Position y
     */
    private void grownPoint(int x,int y, int incR, int incG, int incB){
        boolean germine = false;
        if(x<0||y<0||x>frontLand.getWidth()-1||y>frontLand.getHeight()-1){
            //MultiTaskUtil.threadMsg("Error fuera de la imagen"+x+"-"+y);
            x = (x + this.frontLand.getWidth() ) % this.frontLand.getWidth();
            y = (y + this.frontLand.getHeight() ) %  this.frontLand.getHeight();            
            //return;
        }
        //System.out.println(x+"-"+y);
        Color pixelColor = new Color(frontLand.getRGB(x, y));
   
        int sr = 0, r = pixelColor.getRed();
        int sg = 0, g = pixelColor.getGreen();
        int sb = 0, b = pixelColor.getBlue();
        
        if (r > 0) { 
            sr = sr + incR;
            germine = true;
        } 
        if (g > 0) { 
            sg = sg + incG;
            germine = true;
        } 
        if (b > 0) { 
            sb = sb + incB;
            germine = true;
        } 

        if (germine) germine (x, y, sr, sg, sb, 0);
        
    }// End private void grownPoint(int x,int y)
    
    /**
     * private void germine (int x, int y, int sr, int sg, int sb,int ctrol)
     * 
     * @param    int x - position x
     * @param    int y - Position y  
     * @param    int sr - red cuantity to increment
     * @param    int sg - green cuantity to increment
     * @param    int sb - blue cuantity to increment 
     * @param    int ctrol - control variable to avoid overflow error
     * @return   None
     */
    public void germine (int x, int y, int sr, int sg, int sb,int ctrol){
       
        if(ctrol >= SPREADMADRADIUS) {
            return;
        }
        boolean spread = false;
        //out of bound control
        if(x<0||y<0||x>frontLand.getWidth()-1||y>frontLand.getHeight()-1){
            //MultiTaskUtil.threadMsg("=========================== () OUT OF BOUND germine====================");
            x = (x + this.frontLand.getWidth() ) % this.frontLand.getWidth();
            y = (y + this.frontLand.getHeight() ) %  this.frontLand.getHeight();
            //return;
        }
        
        int sr_ = 0, r = 0;
        int sg_ =0, g = 0;
        int sb_ = 0, b = 0;
        
        Color pixelColor;
        try{
            pixelColor = new Color(backLand.getRGB(x, y));
            r = pixelColor.getRed();
            g = pixelColor.getGreen();
            b = pixelColor.getBlue();
        }catch(Exception e){
            MultiTaskUtil.threadMsg("=========================== () Germine Exception===================");
            MultiTaskUtil.threadMsg("* - = * - = ("+x+","+y+") {"+sr+","+sg+","+sb+"} COLOR("+r+","+g+","+b+")-"+ctrol); 
            return;
        }

        //for test
        if (sr >= 256 || sg >= 256 || sb >= 256){
            int breakp = 1;
            MultiTaskUtil.threadMsg("* - = * - = ("+x+","+y+") {"+sr+","+sg+","+sb+"} COLOR("+r+","+g+","+b+")-"+ctrol); 
        }
        //End for test
        
        r+= sr;
        if (r > SPREADUmbral) { //SPREADUmbral = number over we can't grow and spread
            spread = true; //spread = true if we have to spread
            r = SPREADUmbral -  SPREADCuantity; //SPREADCuantity = number of cuantity lost when spread
            sr_ =  SPREADCuantity / 4; //sr_ = number of cuantity to spread in red
        }
        
        g+=sg;
        if (g > SPREADUmbral) {
            spread = true;
            g = SPREADUmbral -  SPREADCuantity;
            sg_ =  SPREADCuantity / 4;//sg_ = number of cuantity to spread in green
        }
                
        b+= sb;
        if (b > SPREADUmbral) {
            spread = true;
            b = SPREADUmbral -  SPREADCuantity;
            sb_ =  SPREADCuantity / 4;//sb_ = number of cuantity to spread in blue
        }  
        //Change color
        pixelColor= new Color(r, g, b);
        backLand.setRGB(x, y, pixelColor.getRGB());
        
        //Multi-explosion control (ctrol) valor to avoid overflow error when map full white
        
        if (spread) try {
            germine (x-1,y,sr_, sg_, sb_, (1 + ctrol));
            germine (x,y-1,sr_, sg_, sb_, (1 + ctrol));
            germine (x+1,y,sr_, sg_, sb_, (1 + ctrol));
            germine (x,y+1,sr_, sg_, sb_, (1 + ctrol));
            } catch (Exception e){
                MultiTaskUtil.threadMsg("Spread Exception ("+x+"-"+y+") ...");
                MultiTaskUtil.threadMsg("* - = * - = ("+x+","+y+") {"+sr+","+sg+","+sb+"} COLOR("+r+","+g+","+b+")-"+ctrol);
                return;            
        } // end of try-catch and if
        
    } //End public void spread (int x, int y, int r, int g, int b)
    
} // End Class ALife_Nutrient_Enviroment
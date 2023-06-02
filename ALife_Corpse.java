import java.awt.*;
import java.awt.image.*;
//import java.util.*;
import java.util.concurrent.*;

/**
 * Evo_ALife project. 
 * 
 * 
 * @author Iñigo Marco 
 * @version (24-05-2023)
 */

public class ALife_Corpse extends Int_ALife_Creature
{
    public static long VANISHED_TIME = 10;//time during which the corpses simply disappeared or were banished.
    // Fields ----------------------------------------------------------------------------
    private Semaphore semaphore;
    
    
    // Methods ---------------------------------------------------------------------------
    // Constructors ============================
    public ALife_Corpse(Int_ALife_Creature c){
        //super();
        env_ALive = c.getEnv_ALife();
        idCreature = -1;
        semaphore = env_ALive.getSemaphore();
        
        MultiTaskUtil.copyIntArrayContent(this.foodResourceOwn, c.foodResourceOwn);
        this.pos = new Point(c.getPos().x,c.getPos().y);
        //lifeExp, this.foodResourceNeed, this.maxfoodResourceOwn, this.minfoodResourceOwn
        lifeTime = 0;
        lifeDelay = c.lifeDelay / 4;
        //Mind , TamComplex, hungry, reproductionGroup, specie
        env_ALive.addEvent(env_ALive.getTime()+1,this);
        
        //this.logical_Env.addCreture(c, c.getPos());
        //Add in life image
        //c.paint(getBackLifeImage()); //CAUTION we have to pass to front image to be seen        
    } // End public ALife_Corpse(Int_ALife_Creature)    
    
    // End Constructors ========================
    // Public Methods and Fuctions ==============
    
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
            //if too long in time life just vanished
            if (this.lifeTime > VANISHED_TIME) return;
            //Else drop to environment a cuantity of resources
            int[] foodResourceDrop = new int[3];
            int sum = 0;
            for(int i = 0; i < this.foodResourceOwn.length; i++){
                if (this.foodResourceOwn[i] > 256){
                    foodResourceDrop[i] = 256;
                    this.foodResourceOwn[i] -= 256;
                } else {
                    foodResourceDrop[i] = this.foodResourceOwn[i];
                    this.foodResourceOwn[i] = 0;
                }
                sum += this.foodResourceOwn[i];
            } 
            this.getEnv_ALife().getLand().dropNutrient(this.pos, foodResourceDrop, this);
            //int[] dropNutrient(Point p, int[] foodResourceDrop, Creature eater)
            if (sum > 0) env_ALive.addEvent(env_ALive.getTime()+this.lifeDelay,this);
            // if more resources schedule new run iteraction, else no
        } catch (Exception e){
            MultiTaskUtil.threadMsg("Exception in ALife_Corpse.run() " + e.getMessage());
        } finally {
            semaphore.release();
        }
    }
    // Getter and Setters - - - - - - - - - - - -
    // End Getter and Setters - - - - - - - - - -
    // Private Methods and Fuctions =============
    // Main if needed --------------------------------------------------------------------
    //private abstract void actionReproduce(Int_ALife_Creature[] progenitors);
    
    
    public long die(){
        return 0;
    }
    
    public void doAction(Mind_ALife.Action ac){}
    
    public boolean getReproductable(){
        return true;
    }
    
    public void eat(int x, int y, Int_ALife_Creature food){} //food can be null
    
    public void lookForBread(){} // Proliferation of life method
    
    public Int_ALife_Creature reproduze(Int_ALife_Creature couple){
        return null;
    } //How to make new life
    
    public String specieToString(Int_ALife_Creature c){
        return null;
    }
    
    /**
     * public abstract void paint(Graphics g);
     * 
     * @param     - Graphics
     * @return    - None
    **/ 
    //@Override
    public void paint(BufferedImage g, Color c){}
    
    
} // End Class
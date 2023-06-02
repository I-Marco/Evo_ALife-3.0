import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Inferface ALife_Creature:
 * 
 * Estructure for ALife_Creatures
 * 
 * @author Iñigo Marco 
 * @version 06-03-2023
 */
public abstract class Int_ALife_Creature extends Thread
{

    //end for test
    long idCreature = 0;
    //Creature caracteristics
    Env_ALife env_ALive; // Enviroment 
    Point pos;
    
    long lifeTime = 0;
    Long lifeDelay = Long.valueOf(ALife_Nutrient_Environment.DEFAULT_LifeDelay/2);
    long lifeExp = 100 * lifeDelay;
    
    long livePoints = 100;
    long livePointMax = 100;
    long def, attack;
    
    ALife_Specie specie = null;
    long tamComplex = evaluateTamComplex(this);
    
    int[] foodResourceOwn = {0,0,0};
    int[] minfoodResourceOwn = {0,0,0}; // when born and need to born
    int[] maxfoodResourceOwn = {0,0,0};
    int[] foodResourceNeed = {0,0,0};
    
    Trace creatureTrace = null;
    double hidden = 1L; //0..1, 1= No hidden
    int detectionRange = 1; //in pixels min 1
    int umbralDetectión = 1;
    
    long hungry = 100;
    
    int minReproductionGroup = 1;
    ArrayList<Int_ALife_Creature> reproductionGroup = new ArrayList<Int_ALife_Creature>();
    //Int_ALife_Creature[] reproductionGroup = {this};
    Mind_ALife mind = null;
    
    ArrayList<Int_ALife_Creature> progenitors = null;//progenitors of creature
    ArrayList<Int_ALife_Creature> descendents = null;//descendents of creature
    // Num creatures to have a baby
    //int inNeurons, outNeurons, midNeurons, statusNeurons;
    Long move_time; //delay betwen moves
    //more...
    
    //Methods ==================================================================================
    // Getter and Setters ----------------------------
    /**
     * public void setEnv_ALife(Env_ALife e)
     * 
     * @param    Env_ALife
     * @return   None
     */    
    public void setEnv_ALife(Env_ALife e){
        env_ALive = e;
    }
    
    /**
     * public Env_ALife getEnv_ALife()
     * 
     * @param    None
     * @return   Env_ALife
     */    
    public Env_ALife getEnv_ALife(){
        return env_ALive;
    }
    
    /**
     * public void setMind(Mind_ALife m)
     * 
     * @param    Mind_ALife
     * @return   None
     */  
    public void setMind(Mind_ALife m){
        this.mind = m;
    }    

    /**
     * public Mind_ALife getMind()
     * 
     * @param    None
     * @return   Mind_ALife
     */  
    public Mind_ALife getMind(){
        return this.mind;
    }    
    
    /**
     * public void setIdCreature(long id)
     * 
     * @param    long, the environment creature idetification number for this creature
     * @return   None
     */  
    public void setIdCreature(long id){
        this.idCreature = id;
    }

    /**
     * public long getIdCreature()
     * 
     * @param    None
     * @return   long, the environment creature idetification number for this creature
     */  
    public long getIdCreature(){
        return this.idCreature;
    }

    /**
     * public Trace getCreatureTrace()
     * 
     * @param    None
     * @return   Trace, a representation of creature's trace in enviroment
     */  
    public Trace getCreatureTrace(){
        double mark=0;
        //int weight = 0;
        for (int res:foodResourceOwn) 
            mark+=((double)res)/Env_ALife.TRACE_PODENRACION_PESO_DETECCIÓN;
            
        //mark = this.hidden * weight/Env_ALife.TRACE_PODENRACION_PESO_DETECCIÓN;
        
        //for test
        //Trace t = new Trace(mark,this,this.pos);
        this.creatureTrace = new Trace(mark,this,this.pos);
        return this.creatureTrace;
    } // End public Trace getCreatureTrace()

    /**
     * public int getDetectionRange()
     * 
     * @param    None
     * @return   int, the detección range of creature
     */       
    public synchronized int getDetectionRange(){
        return this.detectionRange;
    }
    
    /**
     * public int getUmbralDetection()
     * 
     * @param    None
     * @return   int, the detección umbral of creature lower is better
     */       
    public synchronized int getUmbralDetection(){
        return this.umbralDetectión;
    }    
    
    /**
     * public synchronized Point getPos()
     * 
     * @param    None
     * @return   Point, the position of the creature in the environment
     */       
    public synchronized Point getPos(){
        return this.pos;
    }
    // END Getter and Setters ----------------------------
    
    public static long evaluateTamComplex(Int_ALife_Creature c){
        long comp = 1;
        // internal physical caracteristics
        // Mind global capacity
        // Mind habilities and perception



        // *** FALTA
        /*
        private long lifeTime = 0;
        private Long lifeDelay = new Long (ALife_Nutrient_Enviroment.DEFAULT_LifeDealy/2);
        private long lifeExp = 100 * lifeDelay;

        private long livePoints = 100;
        private long def, attack;
        
        private ALife_Specie specie = null;
        private long tamComplex = 0;
        
        private int[] foodResourceOwn = {0,0,0};
        private int[] minfoodResourceOwn = {0,0,0}; // when born and need to born
        private int[] maxfoodResourceOwn = {0,0,0};
        private int[] foodResourceNeed = {0,0,0};
        
        private long hungry = 100;
        
        private Int_ALife_Creature[] reproductionGroup = null;
        private Mind_ALife mind = null;
        
        private Int_ALife_Creature[] progenitors = null;
        // Num creatures to have a baby
        private int inNeurons, outNeurons, midNeurons, memNeurons;
        private Long move_time; //delay betwen moves         
         */
        // Evaluate comples from variables
        // Get mid comples and factorize both values.
        //comp = c.getMind().evaluateMindComlex();
        
        return comp;
    }
    
    /**
     * An example of a method header - replace this comment with your own
     * 
     * @param  y    a sample parameter for a method
     * @return        the result produced by sampleMethod 
     */
    //private abstract void doAction(Mind_ALife.Action ac);
    
    //private abstract void actionReproduce(Int_ALife_Creature[] progenitors);
    
    
    public abstract long die();
    
    public abstract void doAction(Mind_ALife.Action ac);
    
    public abstract boolean getReproductable();
    
    public abstract void eat(int x, int y, Int_ALife_Creature food); //food can be null
    
    public abstract void lookForBread(); // Proliferation of life method
    
    public abstract Int_ALife_Creature reproduze(Int_ALife_Creature couple); //How to make new life
    
    public abstract String specieToString(Int_ALife_Creature c);
    
    /**
     * public abstract void paint(Graphics g, Color c);
     * 
     * @param     - Graphics
     * @return    - None
    **/ 
    //@Override
    public abstract void paint(BufferedImage g, Color c);
    
    public static void viewCreature(Int_ALife_Creature cre){
        //Scanner scanner = new Scanner(System.in);
        if (cre == null) return;
        try{
    
            System.out.println("---------------------------------------------------------------------------");
            System.out.println("ID: "+cre.idCreature);
            System.out.println("Env: "+cre.env_ALive.get_env_Name());
            System.out.println("Pos: ("+cre.pos.x+","+cre.pos.y+")");
            System.out.println("Live: "+cre.lifeTime+"/"+cre.lifeExp+" - "+cre.lifeDelay);
            System.out.println("Attk/Def:" +cre.attack+"/"+cre.def+" HP("+cre.livePoints+"/"+cre.livePointMax);
            System.out.println("Specie: ...");
            System.out.println("TamComplex: "+cre.tamComplex);
            System.out.print("food Own: ");
            for(int i = 0 ; i < cre.foodResourceOwn.length; i++) {
                System.out.print(cre.foodResourceOwn[i]+", ");
            }
            System.out.println(")");
            
            System.out.print("Min food Own: ");
            for(int i = 0 ; i < cre.minfoodResourceOwn.length; i++) {
                System.out.print(cre.minfoodResourceOwn[i]+", ");
            }
            System.out.println(")");
            
            System.out.print("Max food Own: ");
            for(int i = 0 ; i < cre.maxfoodResourceOwn.length; i++) {
                System.out.print(cre.maxfoodResourceOwn[i]+", ");
            }
            System.out.println(")");
    
            System.out.print("Food need: ");
            for(int i = 0 ; i < cre.foodResourceNeed.length; i++) {
                System.out.print(cre.foodResourceNeed[i]+", ");
            }
            System.out.println(")");
            //Trace
            //hiden, detectionrange, umbralDetection
            //hungry
            System.out.println("Min. rep. group: "+cre.minReproductionGroup);
            // ArrayList reproductionGroup = new ArrayList<Int_ALife_Creature>()
            // Mind_ALife mind = null;
            //Int_ALife_Creature[] progenitors = null;
            //Long move_time; //delay betwen moves
            System.out.println("---------------------------------------------------------------------------");
            //scanner.nextLine();
            //scanner.close();
            
        }catch (Exception e){
            e.printStackTrace();    
        } 
    } // End public static void viewCreature(Int_ALife_Creature cre)
} // End Class
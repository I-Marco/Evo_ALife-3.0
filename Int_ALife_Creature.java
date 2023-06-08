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
    // Constant for evaluations formulas
    public static double DEFAULT_max_Lexp_vs_Ldelay = 200 * ALife_Nutrient_Environment.DEFAULT_LifeDelay;
    public static double DEFAULT_max_LivePoints = 1000;
    public static double DEFAULT_max_Def_Attack = 100;
    public static double DEFAULT_max_foodResourceNeed = 256*3;
    public static double DEFAULT_max_Hungry = 250;
    public static double DEFAULT_max_minReproductionGroup = 5;
    public static double DEFAULT_max_descendants = 10;
    public static double DEFAULT_max_detectRange = 10;
    
    public static long DEFAULT_Hungry_Humbral = 100;
    public static long DEFAULT_Life_Turns = 100;
    
    // Fields ==================================================================================

    //end for test
    long idCreature = 0;
    //Creature caracteristics
    Env_ALife env_ALive; // Enviroment 
    Point pos;
    
    long lifeTime = 0;
    Long lifeDelay = Long.valueOf(ALife_Nutrient_Environment.DEFAULT_LifeDelay/2);
    long lifeExp = DEFAULT_Life_Turns * lifeDelay;
    
    long livePoints = DEFAULT_Hungry_Humbral;
    long livePointMax = DEFAULT_Hungry_Humbral;
    long def, attack;
    
    long specieNumberID = -2; //-1 for corpses, -2 for no specie
    ALife_Specie specie = null;
    double tamComplex = 1; //evaluateTamComplex(this);
    
    int[] foodResourceOwn = {0,0,0};
    int[] minfoodResourceOwn = {0,0,0}; // when born and need to born
    int[] maxfoodResourceOwn = {0,0,0};
    int[] foodResourceNeed = {0,0,0};
    
    Trace creatureTrace = null;
    double hidden = 0L; //0..1, 0 = No hidden
    int detectionRange = 1; //in pixels min 1
    int umbralDetection = 1; //0..1, 0 = Min detection
    
    long humgryUmbral = DEFAULT_Hungry_Humbral;
    long hungry = DEFAULT_Hungry_Humbral;
    
    
    int minReproductionGroup = 1;//min progenitors to have a baby
    ArrayList<Int_ALife_Creature> reproductionGroup = new ArrayList<Int_ALife_Creature>();
    int maxDescendants = 1; //max descendants to have in 1 reproduction
    //Int_ALife_Creature[] reproductionGroup = {this};
    Mind_ALife mind = null;
    
    ArrayList<Int_ALife_Creature> progenitors = new ArrayList<Int_ALife_Creature>();//progenitors of creature
    ArrayList<Int_ALife_Creature> descendents = new ArrayList<Int_ALife_Creature>();//descendents of creature
    
    double status = 0, ownedStatus = 0; //status of creature now and owned status out this moment
    //int inNeurons, outNeurons, midNeurons, statusNeurons;
    //Long move_time; //delay betwen moves
    //more...
    
    //Methods ==================================================================================
    
    // Constructors ============================
        //No constructors

    // Getter and Setters ----------------------------

    /**
     * public void setEnv_ALife(Env_ALife e)
     * 
     * @param    Env_ALife
     * @return   None
     */    
    public void setEnv_ALife(Env_ALife e){
        env_ALive = e;
    } // End public void setEnv_ALife(Env_ALife e)
    
    /**
     * public Env_ALife getEnv_ALife()
     * 
     * @param    None
     * @return   Env_ALife
     */    
    public Env_ALife getEnv_ALife(){
        return env_ALive;
    } // End public Env_ALife getEnv_ALife()
    
    /**
     * public void setMind(Mind_ALife m)
     * 
     * @param    Mind_ALife
     * @return   None
     */  
    public void setMind(Mind_ALife m){
        this.mind = m;
    } // End public void setMind(Mind_ALife m)

    /**
     * public Mind_ALife getMind()
     * 
     * @param    None
     * @return   Mind_ALife
     */  
    public Mind_ALife getMind(){
        return this.mind;
    } // End public Mind_ALife getMind()
    
    /**
     * public void setIdCreature(long id)
     * 
     * @param    long, the environment creature idetification number for this creature
     * @return   None
     */  
    public void setIdCreature(long id){
        this.idCreature = id;
    } // End public void setIdCreature(long id)

    /**
     * public long getIdCreature()
     * 
     * @param    None
     * @return   long, the environment creature idetification number for this creature
     */  
    public long getIdCreature(){
        return this.idCreature;
    } // End public long getIdCreature()

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
    } // End public int getDetectionRange()
    
    /**
     * public int getUmbralDetection()
     * 
     * @param    None
     * @return   int, the detección umbral of creature lower is better
     */       
    public synchronized int getUmbralDetection(){
        return this.umbralDetection; 
    } // End public int getUmbralDetection()
    
    /**
     * public synchronized Point getPos()
     * 
     * @param    None
     * @return   Point, the position of the creature in the environment
     */       
    public synchronized Point getPos(){
        return this.pos;
    } // End public synchronized Point getPos()
    
    /**
     * public long getSpecieIdNumber()
     * 
     * @param    None
     * @return   long, the specie identification number for this creature
     */
    public long getSpecieIdNumber(){
        return this.specieNumberID;
    } // End public long getSpecieIdNumber()

    /**
     * public void setSpecieIdNumber(long id)
     * 
     * @param    long, the specie identification number for this creature
     * @return   None
     */
    public void setSpecieIdNumber(long id){
        this.specieNumberID = id;
    } // End public void setSpecieIdNumber(long id)

    /**
     * getStatus()
     * @param c
     * @return
     */
    public synchronized double getStatus(){
        synchronized (this){
            return this.status;
        }
    } // End public synchronized double getStatus()
    
    // END Getter and Setters ----------------------------
    
    /**
     * public static double[] serializeCreature(Int_ALife_Creature c)
     * Serialize the creature in a double[] and return it
     * @param  c
     * @return double[]
     */
    public static double[] serializeCreature(Int_ALife_Creature c){
        if (c == null) return null;
        double[] carac; // = new double[caracArrayList.size()];
        carac = Creature.serializeCreature(c);
        return carac;
        //return Creature.serializeCreature(c);
    } // End public static double[] serializeCreature(Int_ALife_Creature c)


/**
     * public static double evaluateTamComplex(Int_ALife_Creature c)
     * Evaluate the tamComplex of creature, actualize it and return it
     * @param c
     * @return
     */    
    public static double evaluateTamComplex(Int_ALife_Creature c){
        double[] carac = Creature.serializeCreature(c);
        carac = ALifeCalcUtil.min_max_Array_Normalization(carac, Creature.creature_minCaracteristics, 
            Creature.creature_maxCaracteristics);
        //carac = ALifeCalcUtil.ponderation_Array(carac, ponderationArray);
        //double tamComplex = ALifeCalcUtil.arrayDistance(v1,v2);
        double tamComplex = ALifeCalcUtil.mean(carac);
        return tamComplex;
    
        
        
        /* 
        double DEFAULT_max_Lexp_vs_Ldelay = 200 * ALife_Nutrient_Environment.DEFAULT_LifeDelay;
        double DEFAULT_max_LivePoints = 1000;
        double DEFAULT_max_Def_Attack = 100;
        double DEFAULT_max_foodResourceNeed = 256*3;
        double DEFAULT_max_Hungry = 250;
        double DEFAULT_max_minReproductionGroup = 5;
        double DEFAULT_max_descendants = 10;
        double DEFAULT_max_detectRange = 10;

        double comp = 1; // 1= min complexity.
        //for test
        double aux = 0;
        // internal physical caracteristics ------
            aux = (double)c.lifeDelay / (double)(ALife_Nutrient_Environment.DEFAULT_LifeDelay * 2);
        comp += (double)c.lifeDelay / (double)(ALife_Nutrient_Environment.DEFAULT_LifeDelay * 2);
            aux = (double)c.lifeDelay / (double)(ALife_Nutrient_Environment.DEFAULT_LifeDelay * 2);
        comp += ((double)c.lifeExp / (double)c.lifeDelay)/(double)DEFAULT_max_Lexp_vs_Ldelay;
            aux = (double)c.livePoints / (double)DEFAULT_max_LivePoints;
        comp += (double)c.livePointMax / (double)DEFAULT_max_LivePoints;
            aux = (double)c.def / (double)DEFAULT_max_Def_Attack;
        comp += (double)c.def / (double)DEFAULT_max_Def_Attack;
            aux = (double)c.attack / (double)DEFAULT_max_Def_Attack;
        comp += (double)c.attack / (double)DEFAULT_max_Def_Attack;
            aux = (double)c.hungry / (double)DEFAULT_max_Hungry;
        comp += (double)c.hungry / (double)DEFAULT_max_Hungry;
            aux = (double)c.minReproductionGroup / (double)DEFAULT_max_minReproductionGroup;
        comp += (double)c.minReproductionGroup / (double)DEFAULT_max_minReproductionGroup;
            aux = (double)c.maxDescendants / (double)DEFAULT_max_descendants;
        comp += (double)c.maxDescendants / (double)DEFAULT_max_descendants;
            aux = 1-(double)c.hidden;//1-(double)c.hidden;
        comp += 1-(double)c.hidden; // 0..1, 1= No hidden
            aux = 1-(double)c.umbralDetection;//1-(double)c.umbralDetection;
        comp += 1-(double)c.umbralDetection; // 0..1, 1= No Detection
            aux = (double)c.detectionRange / (double)DEFAULT_max_detectRange;
        comp += (double)c.detectionRange / (double)DEFAULT_max_detectRange;
        for (int i = 0; i < c.foodResourceNeed.length; i++){
            comp += (double)c.foodResourceNeed[i] / (double)DEFAULT_max_foodResourceNeed;
            comp += ((double)c.foodResourceOwn[i] / (10*(double)DEFAULT_max_foodResourceNeed));
        }
        


        // Mind global capacity
        // Mind habilities and perception

        synchronized (c){
            c.tamComplex = comp;
        }
        
        return comp;
        */
    } // End public static double evaluateTamComplex(Int_ALife_Creature c)
    
    /**
     * An example of a method header - replace this comment with your own
     * 
     * @param  y    a sample parameter for a method
     * @return        the result produced by sampleMethod 
     */
    //private abstract void doAction(Mind_ALife.Action ac);
    
    //private abstract void actionReproduce(Int_ALife_Creature[] progenitors);
    
    /**
     * public static double evaluateStatus(Int_ALife_Creature c)
     * Evaluate the status and OwnedStatus of creature, actualize both and return status
     * @param   - c ,the creature to evaluate, for static context
     * @return  - double, the status of creature
     */
    public static double evaluateStatus(Int_ALife_Creature c){
        double DEFAULT_descendat_StatusPlus = 0.1; //For normalizing
        //check all dates to avoid crash
        if (c == null) return -1;

        // Variable status + Owned status
        double ownedStatus = 0;
        for(Int_ALife_Creature cr: c.descendents){
            if (cr == null) ownedStatus += DEFAULT_descendat_StatusPlus; //implement DEFAULT_descendat_StatusPlus
            else ownedStatus += cr.getStatus(); 
            ownedStatus += cr.getStatus(); // implement getStatus()
            //Posibly specie add to status value 
        }
        synchronized (c){
            c.ownedStatus = ownedStatus;
        }
        // Variable status
        //for test
        double VarTemporaldStatus = 0;
        VarTemporaldStatus = (double)c.lifeTime; //may be we need to normalize this values
        VarTemporaldStatus += ((double)c.hungry - 100)/DEFAULT_max_Hungry;
        VarTemporaldStatus += (double)c.livePoints / (double)c.livePointMax;
        double tempBody = 0, tempMinBody = 0;
        for (int i = 0; i < c.foodResourceOwn.length; i++){
            tempBody += (double)c.foodResourceOwn[i];
            tempMinBody += (double)c.minfoodResourceOwn[i];
        }
        //(tempBody - tempMinBody) /(DEFAULT_max_foodResourceNeed*100);
        VarTemporaldStatus += tempBody / tempMinBody;//DEFAULT_max_foodResourceNeed
        synchronized (c){
            c.status = VarTemporaldStatus + ownedStatus;
        }   
        return VarTemporaldStatus + ownedStatus; //for test
        //return c.status;
    }
    public abstract long die();
    
    public abstract void doAction(Mind_ALife.Action ac);
    
    public abstract boolean getReproductable();

    public abstract void actionEat(Point pos, int[] foodResourceEat, Creature cr); //food can be null
    
    public abstract void lookForBread(); // Proliferation of life method
    
    public abstract void actionReproduce(ArrayList<Int_ALife_Creature> progenitors);//How to make new life
    
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
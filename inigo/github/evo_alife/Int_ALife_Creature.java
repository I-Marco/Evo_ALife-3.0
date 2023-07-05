package inigo.github.evo_alife;

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
    
    public static Long MARKREMANECE = 5L; //5 turns of mark detectable
    public static double DEFAULT_Max_Hungry_factor = 1.1;
    public static long DEFAULT_Hungry_Humbral = 100;
    public static int DEFAULT_MEMArraySize = 5;
    public static long DEFAULT_Life_Turns = 100;
    
    public static double MUTATION_DISTANCE = 1; //1 * minimum value of caracteristic
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
    double tamComplex = 0; //evaluateTamComplex(this);
    
    int[] foodResourceOwn = {0,0,0};
    int[] minfoodResourceOwn = {0,0,0}; // when born and need to born
    int[] maxfoodResourceOwn = {0,0,0};
    int[] foodResourceNeed = {0,0,0};
    int maxfoodResourceGetFactor = 3;
    
    double hidden = 0L; //0..1, 0 = No hidden
    int detectionRange = 1; //in pixels min 1
    double umbralDetection = 0L; //0..1, 0 = Min detection
    ArrayList<Trace> detectedTraces = new ArrayList<Trace>();
    
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
    public Int_ALife_Creature(){
        //For subClasses init
    } // End public Int_ALife_Creature()

    /**
     * public Int_ALife_Creature(Int_ALife_Creature c)
     * 
     * @param    Int_ALife_Creature c the Int_ALife_Creature to copy
    */
    protected Int_ALife_Creature(Int_ALife_Creature c){
        this.attack = c.attack; // long def, attack;
        this.def = c.def; //long def, attack;
        descendents = new ArrayList<Int_ALife_Creature>(); // ArrayList<Int_ALife_Creature> descendents = new ArrayList<Int_ALife_Creature>();//descendents of creature
        for(Int_ALife_Creature cr: c.descendents){
            descendents.add(cr);
        }
        this.detectionRange = c.detectionRange; //int detectionRange = 1; //in pixels min 1
        this.env_ALive = c.env_ALive; // Env_ALife env_ALive; // Enviroment 
        this.foodResourceNeed = new int[c.foodResourceNeed.length]; // int[] foodResourceNeed = {0,0,0};
        MultiTaskUtil.copyIntArrayContent(this.foodResourceNeed, c.foodResourceNeed);
        this.foodResourceOwn = new int[c.foodResourceOwn.length]; // int[] foodResourceOwn = {0,0,0};
        MultiTaskUtil.copyIntArrayContent(this.foodResourceOwn, c.foodResourceOwn);
        this.hidden = c.hidden; // double hidden = 0L; //0..1, 0 = No hidden
        this.hungry = c.hungry; // long hungry = DEFAULT_Hungry_Humbral;
        this.humgryUmbral = c.humgryUmbral; //long humgryUmbral = DEFAULT_Hungry_Humbral;
        this.idCreature = c.idCreature; //long idCreature = 0;
        this.lifeDelay = Long.valueOf(c.lifeDelay); // Long lifeDelay = Long.valueOf(ALife_Nutrient_Environment.DEFAULT_LifeDelay/2);
        this.lifeExp = c.lifeExp; // long lifeExp = DEFAULT_Life_Turns * lifeDelay;
        this.lifeTime = c.lifeTime;  // long lifeTime = 0;
        this.livePointMax = c.livePointMax; //   long livePointMax = DEFAULT_Hungry_Humbral;
        this.livePoints = c.livePoints; // long livePoints = DEFAULT_Hungry_Humbral;
        this.maxDescendants = c.maxDescendants; //int maxDescendants = 1; //max descendants to have in 1 reproduction
        this.maxfoodResourceOwn = new int[c.maxfoodResourceOwn.length];
        MultiTaskUtil.copyIntArrayContent(this.maxfoodResourceOwn, c.maxfoodResourceOwn);
        //this.mind = c.mind; //FALTA dupe // Mind_ALife mind = null;
        
        //this.mind = Mind_ALife.dupeMind_ALife(c.mind);
        this.mind = c.mind.dupeMind_ALife();
        this.mind.setCreature(c);


        this.minfoodResourceOwn = new int[c.minfoodResourceOwn.length]; //  int[] minfoodResourceOwn = {0,0,0}; // when born and need to born
        MultiTaskUtil.copyIntArrayContent(this.minfoodResourceOwn, c.minfoodResourceOwn);
        this.minReproductionGroup = c.minReproductionGroup; // int minReproductionGroup = 1;//min progenitors to have a baby
        this.ownedStatus = c.ownedStatus; //ownedStatus = 0; //status of creature now and owned status out this moment
        this.pos = new Point(c.pos); // Point pos;
        this.progenitors = new ArrayList<Int_ALife_Creature>(); //  ArrayList<Int_ALife_Creature> progenitors = new ArrayList<Int_ALife_Creature>();//progenitors of creature
        for(Int_ALife_Creature cr: c.progenitors){
            progenitors.add(cr);
        }
        this.reproductionGroup = new ArrayList<Int_ALife_Creature>(); //ArrayList<Int_ALife_Creature> reproductionGroup = new ArrayList<Int_ALife_Creature>();
        this.specie = c.specie; //ALife_Specie specie = null;
        this.specieNumberID = c.specieNumberID; //long specieNumberID = -2; //-1 for corpses, -2 for no specie
        this.status = c.status; //double status = 0
        this.tamComplex = c.tamComplex; // double tamComplex = 0; //evaluateTamComplex(this);
        this.umbralDetection = c.umbralDetection; // double umbralDetection = 0; //0..1, 0 = Min detection               
    } // End public Int_ALife_Creature()

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
        if (mind ==null) {
            this.mind = m;
            return;
        }
        synchronized (mind){ //Mind change is a critical section
            this.mind = m;
        }
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
        synchronized(foodResourceOwn){
            for (int res:foodResourceOwn) 
                mark+=((double)res)/Env_ALife.TRACE_PODENRACION_PESO_DETECCIÓN;
        }    
        mark = (1 - this.hidden) * mark / Env_ALife.TRACE_PODENRACION_PESO_DETECCIÓN;
        
        //for test
        Trace t = null;
        t = new Trace(mark,this,this.pos, MARKREMANECE);
        //For test
        if(t == null) {
            int breakpoint = 1;
        }
        return t;
        //End test Uncomment next line
        // return new Trace(mark,this,this.pos, MARKREMANECE);
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
    public synchronized double getUmbralDetection(){
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
     * public synchronized void setPos(Point p)
     * 
     * @param    Point, the position of the creature in the environment
     * @return   None
     */
    public synchronized void setPos(Point p){
        this.pos = p;
    } // End public synchronized void setPos(Point p)

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
     * public synchronized double getStatus()
     * @param None
     * @return double, the status of the creature
     */
    public synchronized double getStatus(){
        synchronized (this){
            return this.status;
        }
    } // End public synchronized double getStatus()

    /**
     * public synchronized ArrayList<Trace> getDetectedTraces()
     * 
     * 
     * @param None
     * @return ArrayList<Trace>, the detected traces by the creature
     */
    public synchronized ArrayList<Trace> getDetectedTraces(){
        return this.detectedTraces;
    } // End public Trace getDetectedTraces()

    /**
     * public synchronized ArrayList<Int_ALife_Creature> getReproductionGroup()
     * 
     * Return the reproduction group of the creature
     * @param None
     * @return ArrayList<Int_ALife_Creature>, the reproduction group of the creature
     */
    public synchronized ArrayList<Int_ALife_Creature> getReproductionGroup(){
        return this.reproductionGroup;
    } // End public synchronized ArrayList<Int_ALife_Creature> getReproductionGroup()
    
    /**
     * public synchronized int getMinReproductionGroup()
     * 
     * Return the min reproduction group of the creature
     * @param  None
     * @return int, the min reproduction group of the creature
     */
    public synchronized int getMinReproductionGroup(){
        return this.minReproductionGroup;
    } // End public synchronized int getMinReproductionGroup()

    /**
     * public synchronized long getLifeTime()
     * 
     * Return the life time of the creature
     * @param None
     * @return long, the life time of the creature
     */
    public synchronized long getLifeTime(){
        return this.lifeTime;
    } // End public synchronized int getLifeTime()

    /**
     * public synchronized long getLifeExp()
     * 
     * Return the life exp of the creature
     * @param None
     * @return long, the life exp of the creature
     */
    public synchronized long getLifeExp(){
        return this.lifeExp;
    } // End public synchronized int getLifeTime()
    
    /**
     * public synchronized long getMaxLivePoints()
     * 
     * Return the Max live points of the creature
     * @param None
     * @return long, the Max live points of the creature
     */
    public synchronized long getMaxLivePoints(){
        return this.livePointMax;
    } // End public synchronized int getLivePoints()

    /**
     * public synchronized long getLivePoints()
     * 
     * Return the live points of the creature
     * @param None
     * @return long, the live points of the creature
     */
    public synchronized long getLivePoints(){
        return this.livePoints;
    } // End public synchronized int getLivePoints()

    /**
     * public synchronized long getAttack()
     * 
     * Return the attack of the creature
     * @param  None
     * @return long, the attack of the creature
     */
    public synchronized long getAttack(){
        return this.attack;
    } // End public synchronized int getAttack()

    /**
     * public synchronized long getDef()
     * 
     * Return the def of the creature
     * @param  None
     * @return long, the def of the creature
     */
    public synchronized long getDef(){
        return this.def;
    } // End public synchronized int getDef()

    // END Getter and Setters ----------------------------
    
    /**
     * public int addDetectedTrace(Trace t)
     * 
     * Add detected trace from ALife_Logical_Environment by observer system
     * @param   Trace t, the trace to add
     * @return  int, the number of traces detected
     */
    public synchronized int addDetectedTrace(Trace t){
        if (t == null) return -1;
        if (this.detectedTraces == null) this.detectedTraces = new ArrayList<Trace>();
        this.detectedTraces.add(t);
        return this.detectedTraces.size(); //max traces?? FALTA
    } // End public synchronized int addDetectedTrace(Trace t)
    
    /**
     * public synchronized int addDetectedTraces(ArrayList<Trace> ts)
     * May be later
    */
    
    /**
     * public synchronized int removeDetectedTraces(Trace t)
     * 
     * Remove detected trace from ALife_Logical_Environment by observer system
     * @param  Trace t, the trace to remove
     * @return int, the number of traces detected
     */
    public synchronized int removeDetectedTrace(Trace t){
        int MAX_TRACES = 10;
        //if trace is null just ajust the size
        if (this.detectedTraces == null) {
            this.detectedTraces = new ArrayList<Trace>();
            return -1;
        }
        if (t == null && this.detectedTraces.size() > MAX_TRACES) 
            removeDetectedTrace(this.detectedTraces.get(this.detectedTraces.size()-1));
        if (this.detectedTraces.contains(t) )this.detectedTraces.remove(t);
        else return -1;
        return this.detectedTraces.size();
    }  // End public synchronized int removeDetectedTrace(Trace t)

    /**
     * public static Int_ALife_Creature dupeInt_ALife_Creature(Int_ALife_Creature c)
     * 
     * @param    Int_ALife_Creature c
     * @return   Int_ALife_Creature
     */
    public static Int_ALife_Creature dupeInt_ALife_Creature(Int_ALife_Creature c){
        Int_ALife_Creature newC = null;
        //newC = new Int_ALife_Creature(c); NO FUNCIONA FALLA Y FALTA
        return newC;
    } // End public static Creature dupeCreature(Creature c)

    /**
     * public synchronized void beAttacked(Active_ALife_Creature agressor, long attackValue)
     * 
     * Creature suffer damage cause a agressor creature attack
     * @param agressor
     * @param attackValue
     * @return None
     */
    public synchronized void beAttacked(Active_ALife_Creature agressor, long attackValue){
        //Check
        if(agressor == null) return;
        if(attackValue < 0) return;
        //Register attack
        synchronized (this){
            this.livePoints -= attackValue - this.def;
            if (this.livePoints < 0) this.livePoints = 0;
        }
    } // End public synchronized void beAttacked(Active_ALife_Creature agressor, long attackValue)

    /**
     * public synchronized boolean beAddedToReproductionGroup(Int_ALife_Creature candidate, double candidateStatus)
     * 
     * Add a creature to reproduction group if it is possible and return true if candidate is accepted
     * @param candidate       - the candidate to add
     * @param candidateStatus - the status of the candidate
     * @return boolean, true if candidate is accepted
     */
    public synchronized boolean beAddedToReproductionGroup(Int_ALife_Creature candidate, double candidateStatus){
        //Check
        if (candidate == null) return false;
        if (this.reproductionGroup == null) this.reproductionGroup = new ArrayList<Int_ALife_Creature>();
        if (ALife_Species.getDistancetoCreature(this, candidate) > ALife_Species.SPECIE_DISTANCE) return false;
        //Have we space?
        if (this.reproductionGroup.size() < this.minReproductionGroup) {
            this.reproductionGroup.add(candidate);
            return true;
        }
        //Is candidate better than worst of group?
        double worstStatus = Double.MAX_VALUE;
        Int_ALife_Creature worst = null;
        for (Int_ALife_Creature c :this.reproductionGroup){
            if (c != this && c.getStatus() < worstStatus) {
                worstStatus = c.getStatus();
                worst = c;
            }
        }
        if (worst == null) return false;
        this.reproductionGroup.remove(worst);
        this.reproductionGroup.add(candidate);
        return true;
    } // End public synchronized boolean beAddedToReproductionGroup(Int_ALife_Creature candidate, double candidateStatus)

    /**
     * public static double[] serializeCreature(Int_ALife_Creature c)
     * Serialize the creature in a double[] and return it
     * @param  c
     * @return double[]
     */

    /**
     * public static double[] serializeCreature(Int_ALife_Creature c)
     * @param c Int_ALife_Creature
     * @return double[], the serialized creature
     */
    public static double[] serializeCreature(Int_ALife_Creature c){
        //super.serializeCreature(c); //Can't use super in static method
        if (c == null) return null;
        
        ArrayList<Double> caracArrayList = new ArrayList<Double>();
        //all caracteristics of Active_ALife_Creature

        caracArrayList.add(c.hidden);
        caracArrayList.add(c.tamComplex);
        caracArrayList.add((double)c.attack);
        caracArrayList.add((double)c.def);
        caracArrayList.add((double)c.detectionRange);
        caracArrayList.add((double)c.umbralDetection);
        caracArrayList.add((double)c.humgryUmbral);
        caracArrayList.add((double)c.lifeDelay);
        caracArrayList.add((double)c.lifeExp);
        caracArrayList.add((double)c.livePointMax);
        caracArrayList.add((double)c.maxDescendants);
        caracArrayList.add((double)c.minReproductionGroup);
        
        for(int i = 0; i < c.minfoodResourceOwn.length; i++){
            caracArrayList.add((double) c.minfoodResourceOwn[i]);
            caracArrayList.add((double) c.maxfoodResourceOwn[i]);
            caracArrayList.add((double) c.foodResourceNeed[i]);
        }
        /*
        int cmfo = 0, cMfo = 0, cfrn = 0;
        for(int i = 0; i < c.minfoodResourceOwn.length; i++){
            cmfo += c.minfoodResourceOwn[i];
            cMfo += c.maxfoodResourceOwn[i];
            cfrn += c.foodResourceNeed[i];
        }
        caracArrayList.add((double)cmfo);
        caracArrayList.add((double)cMfo);
        caracArrayList.add((double)cfrn);
        */
        //Mind_ALife
        if (c instanceof Active_ALife_Creature && c.mind != null){
            caracArrayList.add((double)c.mind.getInnerN());
            caracArrayList.add((double)c.mind.getMidN());
            caracArrayList.add((double)c.mind.getOutN());
            caracArrayList.add((double)c.mind.getStatusN());
        } else {
            caracArrayList.add(0.0);
            caracArrayList.add(0.0);
            caracArrayList.add(0.0);
            caracArrayList.add(0.0);
        } //for uniformity
        //Add all to double[]
        /* Version 1 of code with Double[] - wrapper
        Double[] carac = new Double[caracArrayList.size()];
        carac = caracArrayList.toArray(carac);
        */
        //Make new array abd chage from Double to double
        double[] carac = new double[caracArrayList.size()];
        for (int i = 0; i < caracArrayList.size(); i++){
            carac[i] = caracArrayList.get(i);
        }
        //For test
        if (carac.length != Active_ALife_Creature.creature_minCaracteristics.length) {
            int breakpoint = 1;
        }
        else{
            int breakpoint = Active_ALife_Creature.creature_minCaracteristics.length;
            breakpoint = Active_ALife_Creature.creature_maxCaracteristics.length;
            breakpoint = 1;
        }
        return carac;
    } // End public static double[] serializeCreature(Int_ALife_Creature c)

    /**
     * public double[] serializeCreature()
     * 
     * Serialize the creature in a double[] and return it
     * @return double[], the serialized creature
     */
    public synchronized double[] serializeCreature(){
        
        ArrayList<Double> caracArrayList = new ArrayList<Double>();
        //all caracteristics of Active_ALife_Creature

        caracArrayList.add(this.hidden);
        caracArrayList.add(this.tamComplex);
        caracArrayList.add((double)this.attack);
        caracArrayList.add((double)this.def);
        caracArrayList.add((double)this.detectionRange);
        caracArrayList.add((double)this.umbralDetection);
        caracArrayList.add((double)this.humgryUmbral);
        caracArrayList.add((double)this.lifeDelay);
        caracArrayList.add((double)this.lifeExp);
        caracArrayList.add((double)this.livePointMax);
        caracArrayList.add((double)this.maxDescendants);
        caracArrayList.add((double)this.minReproductionGroup);
        
        for(int i = 0; i < this.minfoodResourceOwn.length; i++){
            caracArrayList.add((double) this.minfoodResourceOwn[i]);
            caracArrayList.add((double) this.maxfoodResourceOwn[i]);
            caracArrayList.add((double) this.foodResourceNeed[i]);
        }
        
        //Mind_ALife
        synchronized(this.mind){
            if (this instanceof Active_ALife_Creature && this.mind != null){
                caracArrayList.add((double)this.mind.getInnerN());
                caracArrayList.add((double)this.mind.getMidN());
                caracArrayList.add((double)this.mind.getOutN());
                caracArrayList.add((double)this.mind.getStatusN());
            } else {
                caracArrayList.add(0.0);
                caracArrayList.add(0.0);
                caracArrayList.add(0.0);
                caracArrayList.add(0.0);
            } //for uniformity
        }
        //Add all to double[]
        //Make new array abd change from Double to double
        double[] carac = new double[caracArrayList.size()];
        for (int i = 0; i < caracArrayList.size(); i++){
            carac[i] = caracArrayList.get(i);
        }
        //For test
        if (carac.length != Active_ALife_Creature.creature_minCaracteristics.length) {
            int breakpoint = 1;
        }
        else{
            int breakpoint = Active_ALife_Creature.creature_minCaracteristics.length;
            breakpoint = Active_ALife_Creature.creature_maxCaracteristics.length;
            breakpoint = 1;
        }
        return carac;
    } // End public static double[] serializeCreature(Int_ALife_Creature c)


    /**
     * public static double evaluateTamComplex(Int_ALife_Creature c)
     * Evaluate the tamComplex of creature, actualize it and return it
     * @param c
     * @return
    */ 
    public static double evaluateTamComplex(Int_ALife_Creature c){
        //double[] carac = Active_ALife_Creature.serializeCreature(c);
        double[] carac = c.serializeCreature();
        carac = ALifeCalcUtil.min_max_Array_Normalization(carac, Active_ALife_Creature.creature_minCaracteristics, 
            Active_ALife_Creature.creature_maxCaracteristics);
        //carac = ALifeCalcUtil.ponderation_Array(carac, ponderationArray);
        //double tamComplex = ALifeCalcUtil.arrayDistance(v1,v2);
        double tamComplex = ALifeCalcUtil.mean(carac);
        return tamComplex;
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
            else ownedStatus += DEFAULT_descendat_StatusPlus + cr.getStatus(); 
            //ownedStatus += cr.getStatus(); // implement getStatus()
            //Posibly specie add to status value 
        }
        synchronized (c){
            c.ownedStatus = ownedStatus;
        }
        // Variable status
        //for test
        double VarTemporaldStatus = 0;
        //lifeTime  more better
        VarTemporaldStatus = (double)c.lifeTime / c.lifeExp; //may be we need to normalize this values
        //hungry more better 
        VarTemporaldStatus += ((double)c.hungry - DEFAULT_Hungry_Humbral)/DEFAULT_max_Hungry;
        //livePoints more better MAX 1
        VarTemporaldStatus += (double)c.livePoints / (double)c.livePointMax;
        //Reproduction group full better MAX 1
        VarTemporaldStatus += (double)c.reproductionGroup.size() / c.minReproductionGroup;
        //Body dimension more better
        double tempBody = 0, tempMinBody = 0, tempMaxBody = 0;
        for (int i = 0; i < c.foodResourceOwn.length; i++){
            tempBody += (double)c.foodResourceOwn[i];
            tempMinBody += (double)c.minfoodResourceOwn[i];
            tempMaxBody += (double)c.maxfoodResourceOwn[i];
        }
        //(tempBody - tempMinBody) /(DEFAULT_max_foodResourceNeed*100);
        VarTemporaldStatus += (tempBody - tempMinBody) / (tempMaxBody - tempMinBody);//DEFAULT_max_foodResourceNeed
        synchronized (c){
            c.status = VarTemporaldStatus + ownedStatus;
        }   
        return VarTemporaldStatus + ownedStatus; //for test
        //return c.status;
    } // End public static double evaluateStatus(Int_ALife_Creature c)

    /**
     * public static double[] mutateCreaturebySerialization(double[] carac)
     * Mutate the creature in a double[] and return it but most work automutation c
     * @param  c Int_ALife_Creature
     * @return double[]
     */
    public static double[] mutateCreaturebySerialization(double[] carac){
        //Serialized but not necessarily normalized
        if (carac == null) return null;
        if (carac.length != Active_ALife_Creature.creature_minCaracteristics.length) return null; // Unknow creature type
        int l = carac.length;
        // same possibility to mutate all caracteristics other way an array to set the possibilities of each caracteristic
        Random r = new Random();
        int mutateCaracteristic = r.nextInt(l);
        double[] caracMutated = new double[l];
        for (int j = 0; j < l; j++){
            if (j == mutateCaracteristic) {
                double tempMin = 0; //Cuantity of minimum modification, other way if min = 0 modifycantion is always 0
                if (Active_ALife_Creature.creature_minCaracteristics[mutateCaracteristic] == 0) {
                    tempMin = Active_ALife_Creature.creature_maxCaracteristics[mutateCaracteristic] / 100;
                    /*
                    tempMin = Creature.creature_maxCaracteristics[mutateCaracteristic] / 
                      (Creature.creature_maxCaracteristics[mutateCaracteristic] + 
                        (100 * Creature.creature_maxCaracteristics[mutateCaracteristic])); //may be 1000
                    */
                    if (tempMin == 0) tempMin = 0.01;
                }else tempMin = Active_ALife_Creature.creature_minCaracteristics[mutateCaracteristic];
                caracMutated[j] = 
                    carac[j] + (r.nextInt(3)-1) * MUTATION_DISTANCE * tempMin;//random +-1 * mutation distance
                //limits check
                if (caracMutated[j] < Active_ALife_Creature.creature_minCaracteristics[mutateCaracteristic]) 
                    caracMutated[j] = Active_ALife_Creature.creature_minCaracteristics[mutateCaracteristic];
                if (caracMutated[j] > Active_ALife_Creature.creature_maxCaracteristics[mutateCaracteristic]) 
                    Active_ALife_Creature.creature_maxCaracteristics[mutateCaracteristic] = caracMutated[j];
                    // This cause diferences un species asignation and can't be backsteps the done asignations
                
                //caracMutated[j] = 
                //    carac[j] + (r.nextInt(3)-1) * MUTATION_DISTANCE * 
                //    Creature.creature_minCaracteristics[mutateCaracteristic];//random +-1 * mutation distance
            } else caracMutated[j] = carac[j];
        }
        return caracMutated;
    } // End public static double[] mutateCreaturebySerialization(double[] carac)

    /**
     * public void mutateCreature() 
     * Mutate the creature by seriazing it and them aply chnages
     * @param  None
     * @return None
     */
    public void mutateCreature(){
        //double[] carac = Active_ALife_Creature.serializeCreature(this);
        double[] carac = this.serializeCreature();
        double[] caracMutated = Active_ALife_Creature.mutateCreaturebySerialization(carac);
        int mutatedCar; //number of caracteristic have been mutated
        for (mutatedCar = 0; mutatedCar < carac.length; mutatedCar ++){
            if (carac[mutatedCar] != caracMutated[mutatedCar]) 
                break;
        }
        //for test
        double d = ALifeCalcUtil.arrayDistance(carac, caracMutated);

        if (mutatedCar == carac.length || carac[mutatedCar] == caracMutated[mutatedCar]) return; //No mutation
        //Mutate creature
        if (mutatedCar < carac.length - Active_ALife_Creature.MIND_NEURONS_TYPES){
        /*
         public static final double[] creature_minCaracteristics =
            {0, 1, 0, 0, 0, 0, 1, //hidden, tamComplex, attack, def, detectionRange, umbralDetection, humgryUmbral, 
            1, 100, 1, 1, 1, //lifeDelay, lifeExp, livePointMax, maxDescendants, minReproductionGroup, 
            1, 1, 1, //minfoodResourceOwn, maxfoodResourceOwn, foodResourceNeed, x3
            1, 0, 2, 0 //mind.getInnerN(), mind.getMidN(), mind.getOutN(), mind.getStatusN()
        };
        */
            switch (mutatedCar){
                case 0 : //hidden
                    this.hidden = caracMutated[mutatedCar];
                    break;
                case 1 : //tamComplex must be calculated later
                    break;
                case 2 : //attack
                    this.attack = (long)caracMutated[mutatedCar];
                    break;
                case 3 : //def
                    this.def = (long)caracMutated[mutatedCar];
                    break;
                case 4 : //detectionRange
                    this.detectionRange = (int)caracMutated[mutatedCar];
                    break;
                case 5 : //umbralDetection
                    this.umbralDetection = caracMutated[mutatedCar];
                    break;
                case 6 : //humgryUmbral 
                    this.humgryUmbral = (long)caracMutated[mutatedCar]; 
                    break;
                case 7 : //lifeDelay
                    this.lifeDelay = (long)caracMutated[mutatedCar];
                    break;
                case 8 : //lifeExp
                    this.lifeExp = (long)caracMutated[mutatedCar];
                    break;
                case 9 : //livePointMax
                    this.livePointMax = (long)caracMutated[mutatedCar];
                    break;
                case 10 : //maxDescendants
                    this.maxDescendants = (int)caracMutated[mutatedCar];
                    break;
                case 11 : //minReproductionGroup
                    this.minReproductionGroup = (int)caracMutated[mutatedCar];
                    break;
                case 12 : //minfoodResourceOwn ///FALTA MUTAR FOOD Ha cambiado
                case 13 :
                case 14 :
                case 15 :
                case 16 :
                case 17 :
                case 18 :
                case 19 :
                case 20 :
                    this.minfoodResourceOwn = mutateFood(this.minfoodResourceOwn, 
                        new int[]{(int)caracMutated[12], (int)caracMutated[15], (int)caracMutated[18]},
                        mutatedCar);
                    this.maxfoodResourceOwn = mutateFood(this.maxfoodResourceOwn, 
                        new int[]{(int)caracMutated[13], (int)caracMutated[16], (int)caracMutated[19]},
                        mutatedCar);
                    this.foodResourceNeed = mutateFood(this.foodResourceNeed, 
                        new int[]{(int)caracMutated[14], (int)caracMutated[17], (int)caracMutated[20]},
                        mutatedCar);
                    break;
/*    
                case 13 : //maxfoodResourceOwn
                    this.maxfoodResourceOwn = mutateFood(this.maxfoodResourceOwn, (int)caracMutated[mutatedCar]);
                    break;
                case 14 : //foodResourceNeed
                    this.foodResourceNeed = mutateFood(this.foodResourceNeed, (int)caracMutated[mutatedCar]);
                    break;
                case 15 : //minfoodResourceOwn ///FALTA MUTAR FOOD Ha cambiado
                    this.minfoodResourceOwn = mutateFood(this.minfoodResourceOwn, (int)caracMutated[mutatedCar]);
                    break;
                case 16 : //maxfoodResourceOwn
                    this.maxfoodResourceOwn = mutateFood(this.maxfoodResourceOwn, (int)caracMutated[mutatedCar]);
                    break;
                case 17 : //foodResourceNeed
                    this.foodResourceNeed = mutateFood(this.foodResourceNeed, (int)caracMutated[mutatedCar]);
                    break;
                case 18 : //minfoodResourceOwn ///FALTA MUTAR FOOD Ha cambiado
                    this.minfoodResourceOwn = mutateFood(this.minfoodResourceOwn, (int)caracMutated[mutatedCar]);
                    break;
                case 19 : //maxfoodResourceOwn
                    this.maxfoodResourceOwn = mutateFood(this.maxfoodResourceOwn, (int)caracMutated[mutatedCar]);
                    break;
                case 20 : //foodResourceNeed
                    this.foodResourceNeed = mutateFood(this.foodResourceNeed, (int)caracMutated[mutatedCar]);
                    break;
                //Falta Mutar mind
*/                              
                case 21 :
                case 22 :
                case 23 :
                case 24 :                                                
                    mind.mutateMind(mutatedCar,
                        new int[]{(int)caracMutated[21], (int)caracMutated[22], (int)caracMutated[23], (int)caracMutated[24]}
                        );
                    break;  
                default :
                    MultiTaskUtil.threadMsg("Error in mutateCreature() in Int_ALife_Creature.java");
                    break;
            } // End switch (mutatedCar)
        } // End if // esto deberia ser para todos. Falta mutate Mind
        this.tamComplex = evaluateTamComplex(this);
    } // End public void mutateCreature()

    private synchronized int[] mutateFood(int[] food, int[] newcarac, int carac){
        boolean valid = true;
        int sum = 0;
        if (carac == 12 || carac == 15 || carac == 18) {
            for (int i=0; i < food.length; i++){
                sum += newcarac[i];
                if (newcarac[i] < 0 || newcarac[i] >= maxfoodResourceOwn[i] / 2 ) valid = false;
            }
            if (sum <= 0 || !valid) return minfoodResourceOwn;
            else {
                MultiTaskUtil.copyIntArrayContent(food, newcarac);
                return food;
            }
        } else {
            if (carac == 13 || carac == 16 || carac == 19) {
                for (int i=0; i < food.length; i++){
                    sum += newcarac[i];
                    if (newcarac[i] < 0 || newcarac[i] >= minfoodResourceOwn[i] * this.maxfoodResourceGetFactor ) valid = false;
                }
                if (sum <= 0 || !valid) return maxfoodResourceOwn;
                else {
                    MultiTaskUtil.copyIntArrayContent(food, newcarac);
                    return food;
                }
            }else {
                if (carac == 14 || carac == 17 || carac == 20) {
                    for (int i=0; i < food.length; i++){
                        sum += newcarac[i];
                        if (newcarac[i] < 0 || newcarac[i] >= minfoodResourceOwn[i] / this.maxfoodResourceGetFactor ) valid = false;
                    }
                }
                if (sum <= 0 || !valid) return foodResourceNeed;
                else {
                    MultiTaskUtil.copyIntArrayContent(food, newcarac);
                    return food;
                }
            }
        } //End if
        //return food;
    } // End 


    public abstract long die();
    
    public abstract void doAction(Mind_ALife.Action ac);
    
    public abstract boolean getReproductable();

    public abstract void actionEat(Point pos, int[] foodResourceEat, Int_ALife_Creature cr); //food can be null
    
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
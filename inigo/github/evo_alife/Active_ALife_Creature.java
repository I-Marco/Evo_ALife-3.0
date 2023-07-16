package inigo.github.evo_alife;

import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.awt.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

//import Mind_ALife.Action;

import java.util.*;

/**
 * Write a description of class Creature here.
 * 
 * @author Iñigo Marco 
 * @version 20-06-2023
 */
public class Active_ALife_Creature extends Int_ALife_Creature
{
    public static long DEFAULT_CreatureReportPercent = 400;

    public static final int DEFAULT_LifeDelayPerNutrient = 2;
    public static final Color CREATURE_DEFAULT_COLOR = new Color (199,199,199);
    // Constants for normalization Creature
    /*public static final double[] creature_Caracteristics =
        {hidden, tamComplex, attack, def, detectionRange, umbralDetection, humgryUmbral, 
            lifeDelay, lifeExp, livePointMax, maxDescendants, minReproductionGroup, [creatureTrace,]
            minfoodResourceOwn, maxfoodResourceOwn, foodResourceNeed,
            mind.getInnerN(), mind.getMidN(), mind.getOutN(), mind.getStatusN()};
    */


    public static final double[] creature_minCaracteristics =
        {0, 0, 0, 0, 0, 0, 1, //hidden, tamComplex, attack, def, detectionRange, umbralDetection, humgryUmbral, 
            1, 100, 1, 1, 1, //lifeDelay, lifeExp, livePointMax, maxDescendants, minReproductionGroup, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, //All R, All G, all B minfoodResourceOwn, maxfoodResourceOwn, foodResourceNeed,
            1, 0, 1, 0 //mind.getInnerN(), mind.getMidN(), mind.getOutN(), mind.getStatusN()
        };

    public static double[] creature_maxCaracteristics = //Can update if changes evolve
    {1, 1, 100, 100, 5, 1, 200, //hidden, tamComplex, attack, def, detectionRange, umbralDetection, humgryUmbral, 
        100, 10000, 200, 10, 3, //lifeDelay, lifeExp, livePointMax, maxDescendants, minReproductionGroup, 
        1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, //minfoodResourceOwn, maxfoodResourceOwn, foodResourceNeed,
        25, 15, Mind_ALife.Action.values().length, 5 //mind.getInnerN(), mind.getMidN(), mind.getOutN(), mind.getStatusN()
    };
    //Note: 2º value is TamComplex. Be care on changes same on MIND values
    //double WEIGHT = 0.1; //Weight of actual status in evolution
    public static long MEDIUM_TIME_UPDATE = 5; //Medium time for update data
    public static long LONG_TIME_UPDATE = 15; //Long time for update data
    
    public static int MIND_NEURONS_TYPES = 4; //Number of caracteristics reffered to Mind_ALife
    //End Constants for normalization Creature

    // Fields ----------------------------------------------------------------------------
    //-private Env_ALife env_ALive; // Enviroment 
    private BufferedImage backLand = null;
    private BufferedImage frontLand = null;    
    private Semaphore semaphore;

    private ReentrantLock lockCreature = new ReentrantLock();
    
    //Creature caracteristics in Int_ALife_Creature
    ArrayList <Long> timeOfDeccision = new ArrayList<Long>(); // Time of deccision
    ArrayList <Mind_ALife.Action> actions = new ArrayList<Mind_ALife.Action>(); // Actions that creature can do
    ArrayList <Double> statusValues = new ArrayList<Double>(); // Status of creature

    ReentrantLock lockStatusMemory;
    //Make a variable with 3 fields long time field, double statusvalue and Mind_ALife.Action doneAction
    //Known species ....
    ArrayList <ALife_Specie> enemySpecieList = new ArrayList<ALife_Specie>(); // List of enemy species
    ArrayList <ALife_Specie> friendlySpecieList = new ArrayList<ALife_Specie>(); // List of friendly species
    ArrayList <ALife_Specie> foodSpecieList = new ArrayList<ALife_Specie>(); // List species that can eat
    //Not defined especies ??

    //Known creatures ....
    ArrayList <Int_ALife_Creature> enemyCreatureList = new ArrayList<Int_ALife_Creature>(); // List of enemy creatures
    ArrayList <Int_ALife_Creature> friendlyCreatureList = new ArrayList<Int_ALife_Creature>(); // List of friendly creatures
    ArrayList <Int_ALife_Creature> familyCreatureList = new ArrayList<Int_ALife_Creature>(); // List of neutral creatures
    Int_ALife_Creature prey = null; // Prey creature
    Int_ALife_Creature favorite = null; // Predator creature

    private ALife_FileManager fileManager_Mind = null; //for creature mind evolution reports Hard to manage
        
    // Methods ---------------------------------------------------------------------------
    // Construcotors ============================
    /**
     * Constructor for objects of class ALife_Nutrient_Enviroment
     * @param   - Env_ALife
     * Create e simulation nutrient enviroment
     */
    //Creature(this.env_ALife,new Point(170,170),null,1000,haveR,needR
    //public Creature(Env_ALife env, Semaphore s, Point p){
    public Active_ALife_Creature(Env_ALife env, Point p, Mind_ALife m, long lifeexp, int[] frOwn, int[] frNeed){
        super();
        lockStatusMemory = new ReentrantLock();
        lockCreature = new ReentrantLock();
        env_ALive = env;
        idCreature = this.env_ALive.getNewCreatureID();
        semaphore = env_ALive.getSemaphore(); // Quiza deberia ser parámetro pero Evo_ALIFE no lo tiene
        
        //frontLand = Env_Panel.getDefaultEnvImage(); //?
        
        //Create creature
        pos = p;
        
        this.lifeExp = lifeexp;
        lifeDelay = Long.valueOf(ALife_Nutrient_Environment.DEFAULT_LifeDelay/DEFAULT_LifeDelayPerNutrient);
        lifeTime = 0;
        foodResourceOwn = new int[3];
        foodResourceNeed = new int[3];
        minfoodResourceOwn = new int[3];
        maxfoodResourceOwn = new int[3];

        if (frOwn == null){for(int b:foodResourceOwn){b = 0;}}
            else MultiTaskUtil.copyIntArrayContent(foodResourceOwn, frOwn);
        if (frNeed == null){for(int b:foodResourceNeed){b = 0;}}
            else MultiTaskUtil.copyIntArrayContent(foodResourceNeed, frNeed);
        //foodResourceOwn = {0,0,0};
        MultiTaskUtil.copyIntArrayContent(minfoodResourceOwn, foodResourceOwn);
        Random random = new Random();
        double fac = 3 + ( (double) random.nextInt(126) )/100; //Factor of maxfoodResourceOwn from foodResourceOwn
        for (int i = 0; i< foodResourceOwn.length; i++){
            maxfoodResourceOwn[i] = (int)(foodResourceOwn[i] * fac);
        }

        //foodResourceNeed = {0,0,0};            
                
        hungry = Int_ALife_Creature.DEFAULT_Hungry_Umbral + 1;
    
        if (m != null) { this.mind = m;}
        else {
            setMind(new Mind_ALife(this));
        }
        
        descendents = new ArrayList<Int_ALife_Creature>();
        reproductionGroup = new ArrayList<Int_ALife_Creature>();
        reproductionGroup.add(this);
        tamComplex = evaluateTamComplex(this); // Funcion de evaluacion?
        //this.env_ALive.addEvent(env_ALive.getTime() + 1, this); //se añade a la siguiente unidad de tiempo
        //specie = getALife_Specie;
        env_ALive.addCreature(this); 
        //env_ALive.getSpecies().addCreature(this); //Automatically assign specieIdNumber    
    }// Ene public Creature(Env_ALife env)
        
    /**
     * Constructor for objects of class ALife_Nutrient_Enviroment
     * @param   - Env_ALife
     * Create e simulation nutrient enviroment
     * @throws Exception
     */
    //Creature(this.env_ALife,new Point(170,170),null,1000,haveR,needR
    //public Creature(Env_ALife env, Semaphore s, Point p){
    public Active_ALife_Creature(ArrayList<Int_ALife_Creature> progenitors, boolean mutate) throws Exception{
        //check
        //Caracteristicas mix progenitores
        if (progenitors == null) {
            //Error msg.
            throw new Exception("Error in Creature constructor: progenitors is null");
            //return; //throwException mejor
        }
        if (progenitors.size() < this.minReproductionGroup) {
            //Error msg.
            throw new Exception("Error in Creature constructor: progenitors.size() < this.minReproductionGroup");
            //return;
        }
        
        lockStatusMemory = new ReentrantLock();
        lockCreature = new ReentrantLock();
        
        //Look for place to born
        this.setEnv_ALife(progenitors.get(0).getEnv_ALife()); //progenitors[0] = pregnant
        //for test
        int auxX = 0, auxY = 0; //Where will be born
        Point auxP = new Point();
        auxP = progenitors.get(0).getPos();
        this.pos = null; //Nowhere to born ---> misbirth
        int w = getEnv_ALife().getEnv_Width();
        int h = getEnv_ALife().getEnv_Height();

        ArrayList<Point> posiblePos = new ArrayList<Point>();

        for(int lo = 1;lo < 3; lo++){//Birth pos range increassing
            //if (pos != null) break;
            for (int i = auxP.x-lo; i < auxP.x+lo ; i++){
                //if (pos != null) break;
                for(int j = auxP.y-lo;j <= auxP.y+lo; j++){
                    Env_ALife e = this.getEnv_ALife();
                    ALife_Logical_Environment le = e.getLogical_Env();
                    boolean b = le.detectColision(progenitors.get(0),new Point((i + w) % w,(j + h) % h),0);
                    if (!b){
                    //if (!env_ALive.getLogical_Env().detectColision(progenitors.get(0),
                    //    new Point((i + w) % w,(j + h) % h), 0)){
                        //this.pos = new Point((i + w) % w,(j + h) % h);
                        posiblePos.add(new Point((i + w) % w,(j + h) % h));
                        break;
                    }
                }
            }
        }
        if (posiblePos.size() != 0) {
            this.pos = posiblePos.get((int)((Math.random() * posiblePos.size()*100 )) % posiblePos.size());
        }
        

        /*
        for(int lo = 1;lo < 3; lo++){//Birth pos range increassing
            if (pos != null) break;
            for (int i = auxP.x-lo; i < auxP.x+lo ; i++){
                if (pos != null) break;
                for(int j = auxP.y-lo;j <= auxP.y+lo; j++){
                    Env_ALife e = this.getEnv_ALife();
                    ALife_Logical_Environment le = e.getLogical_Env();
                    boolean b = le.detectColision(progenitors.get(0),new Point((i + w) % w,(j + h) % h),0);
                    if (!b){
                    //if (!env_ALive.getLogical_Env().detectColision(progenitors.get(0),
                    //    new Point((i + w) % w,(j + h) % h), 0)){
                        this.pos = new Point((i + w) % w,(j + h) % h);
                        break;
                    }
                }
            }
        }
         */
        //MEJORA lista posibles sitios y random

        if (this.pos == null){
            this.livePoints = 0; //born death as alternative
            this.pos = progenitors.get(0).getPos(); //born in same place but death MISBIRTH
            if (this.env_ALive == null) this.env_ALive = progenitors.get(0).getEnv_ALife();
            this.lifeDelay = progenitors.get(0).lifeDelay;
            MultiTaskUtil.copyIntArrayContent(this.foodResourceOwn, 
                progenitors.get(0).minfoodResourceOwn);
            new ALife_Corpse(this);
            throw new Exception("No place for descendant missbirth (pos == null)");
            //return; // Misbirth detection
        }  
        
        //Int_ALife_Creature ausC = progenitors[0];
        //this.setEnv_ALife(progenitors[0].getEnv_ALife());
        lifeTime = 0;

        //Data from progenitor[0]
        //setEnv_ALife(progenitors[0].getEnv_ALife());
        //pos = progenitors[0].pos;
        int numProg = progenitors.size();
        
        foodResourceOwn = new int[3];
        foodResourceNeed = new int[3];
        minfoodResourceOwn = new int[3];
        maxfoodResourceOwn = new int[3];
        
        MultiTaskUtil.copyIntArrayContent(foodResourceOwn, Env_ALife.FOOD_0);
        MultiTaskUtil.copyIntArrayContent(minfoodResourceOwn, Env_ALife.FOOD_0);
        MultiTaskUtil.copyIntArrayContent(maxfoodResourceOwn, Env_ALife.FOOD_0);
        MultiTaskUtil.copyIntArrayContent(foodResourceNeed, Env_ALife.FOOD_0);
        
        //Data from all progenitors
        if (numProg == 0) {
            throw new Exception("Error in Creature constructor: numProg == 0");
        }
        for(Int_ALife_Creature iac: progenitors){
            lifeDelay = iac.lifeDelay / numProg;
            lifeExp = iac.lifeExp / numProg;
            livePointMax = iac.livePointMax / numProg;
            attack = iac.attack / numProg;
            def = iac.def / numProg;
            for (int i = 0; i<3;i++){
                foodResourceOwn[i] += iac.minfoodResourceOwn[i]/ numProg;
                minfoodResourceOwn[i] += iac.minfoodResourceOwn[i]/ numProg;
                maxfoodResourceOwn[i] += iac.maxfoodResourceOwn[i]/ numProg;
                foodResourceNeed[i] += iac.foodResourceNeed[i]/ numProg;
            }
        } //End caracteritics that mixed all progenitors        
        
        livePoints = livePointMax;

        mind = new Mind_ALife(progenitors,this,progenitors.get(0).env_ALive.getAllowMutate());//new Mind_ALife(progenitors);
        
        //Autocalculated datas
        hungry = Int_ALife_Creature.DEFAULT_Hungry_Umbral + 1; //0auto calculate

        specie = null;//autocalculate deprecated for moment
        //Ming values neurosn neurons in neurosn out neurons status
        
        this.minReproductionGroup = progenitors.get(0).minReproductionGroup;
        descendents = new ArrayList<Int_ALife_Creature>();
        reproductionGroup = new ArrayList<Int_ALife_Creature>();
        reproductionGroup.add((Int_ALife_Creature)this);
        this.progenitors = progenitors;
        tamComplex = evaluateTamComplex(this); // Funcion de evaluacion?; //auto calculate
        //reproductionGroup = new Int_ALife_Creature[this.minReproductionGroup];
        //reproductionGroup[0] = (Int_ALife_Creature)this; //(Int_ALife_Creature)this;

        this.mutateCreature();
        idCreature = this.getEnv_ALife().getNewCreatureID();        
        semaphore = env_ALive.getSemaphore();
        env_ALive.addCreature(this); 
        //env_ALive.getSpecies().addCreature(this); //Automatically assign specieIdNumber
    } // End public Creature(ArrayList<Int_ALife_Creature> progenitors)
    
    /**
     * Active_ALife_Creature() Constructor
     * 
     * Default empty constructor for herence
     * @param    None
     * @return   Active_ALife_Creature
     */
    Active_ALife_Creature(){
        lockStatusMemory = new ReentrantLock();
        lockCreature = new ReentrantLock();
        //env_ALive.getSpecies().addCreature(null); //Automatically assign specieIdNumber ??
    }//Just to can make subclass constructors
    
    /**
     * private Creature(Creature c)
     * Make a duplication of creature
     * @param   - Creature
     */
    protected Active_ALife_Creature(Active_ALife_Creature c){
        //Dupe creature
        super(c); //Int_ALife_Creature(c)
        lockStatusMemory = new ReentrantLock();
        lockCreature = new ReentrantLock();
        this.backLand = c.backLand; //private BufferedImage backLand = null;
        this.frontLand = c.frontLand; // private BufferedImage frontLand = null;
        this.semaphore = c.semaphore; // private Semaphore semaphore;
        this.timeOfDeccision = new ArrayList<Long>(); // ArrayList <Long> timeOfDeccision = new ArrayList<Long>(); // Time of deccision
        for(Long l: c.timeOfDeccision){
            this.timeOfDeccision.add(l);
        }
        this.actions = new ArrayList<Mind_ALife.Action>(); //ArrayList <Mind_ALife.Action> actions = new ArrayList<Mind_ALife.Action>(); // Actions that creature can do
        for(Mind_ALife.Action a: c.actions){
            this.actions.add(a);
        }
        this.statusValues = new ArrayList<Double>(); //ArrayList <Double> statusValue = new ArrayList<Double>(); // Status of creature
        for(Double d: c.statusValues){
            this.statusValues.add(d);
        }        
    } // End private Creature(Creature c)

    // Public Methods and Fuctions ==============
    
    // Getter and Setters

    /**
     * public int getDetectionRange()
     * 
     * Return the detection range of creature
     * @param    None
     * @return   int the detection range of creature
     */
    public synchronized int getDetectionRange(){
        return this.detectionRange;
    } // End public getDetectionRange()

    /**
     * public synchronized ArrayList<Mind_ALife.Action> getActions()
     * 
     * Return the actions that creature havd done in last turns
     * @param    None
     * @return   ArrayList<Mind_ALife.Action> the actions that creature can do
     */
    public synchronized ArrayList<Mind_ALife.Action> getActions(){
        return this.actions;
    } // End public getActions()

    /**
     * public boolean getReproductable()
     * 
     * Devuelve true si la criatura es capaz de reproducirse
     * @param    None
     * @return   boolean
     */   
    public boolean getReproductable(){
        boolean r = true;
        if(this.reproductionGroup.size() < minReproductionGroup) return false;
        for(int i = 0; i < foodResourceOwn.length ; i++){
            if (foodResourceOwn[i] < minfoodResourceOwn[i] * (this.maxDescendants + 1)) r = false;
        }
        //for test
        if (r) {
            int breackpoint = 1;}        
        return r;
    } // End public boolean getReproductable()

       
    // End Getter and Setters
    
    //Other public Methods
    /**
     * Run() - implements of Runnable method Run
     * 
     * @param    No parameter
     * @return   No returned value
     */    
    @Override 
    public void run(){ //Creature Run
        try{
            if (semaphore == null) {
                MultiTaskUtil.threadMsg("Error in Creature Run: semaphore == null");
                return;
            }
            semaphore.acquire();       
            //For concurrency Log
            //MultiTaskUtil.threadMsg("===== Semaphore ADQUIRED BY CREATURE("+
            //    semaphore.availablePermits()+").................");
            //Run
            if(this.lifeTime == 0) {
                statusChangesUpdate(null); //First time in real enviroment
                
                if (this.getIdCreature() == 1)
                try{
                this.fileManager_Mind = new ALife_FileManager(this.getEnv_ALife().getName()+"_"+"Creature"+this.getIdCreature()+".csv", this);
                this.fileManager_Mind.start();
                //String[] HEADERS = {"Time", "CreID","LiveT","liveExp","liveP","maxLiveP","Hungry","HungryUmbral","MaxHungry",
                //    "pos", "liveDelay","Def","Attack","SpecieID","TamComplex","FrO(R)","FrO(G)","FrO(B)","mFrO(R)","mFrO(G)","mFrO(B)",
                //    "MFrO(R)","MFrO(G)","MFrO(B)","FrN(R)","FrN(G)","FrN(B)","mFrFactor", "Hidden","DetectionRange","UmbralDetection",
                //    "minRepG", "MaxDescen" ,"StatusVal","Action","InnerN","MidN","OutN","StatusN",
                //    "MindUChangefr","WeightChangefr","WeightChangeUnderfr", "forecastStatusMean", "forecastGeneralError", "forecastGeneralVariance","MindOutput",
                //    "NeuroSep","NiD","tipo","Out","N_U", "Nforecast", "Nact", "Naction","wigths"
                //};
                //this.fileManager.addLine(HEADERS);
                //this.fileManager.forceWrite();
                }catch (IOException ioe){
                MultiTaskUtil.threadMsg("Error creating file manager");
                } 
                       
            } //End if(this.lifeTime == 0) {
            //1.- Eventos involuntarios como morir envejecer ...
            this.lifeTime += this.lifeDelay;
            this.hungry -= 1; //aumentar hambre
            //this create or vanish resources
            //Arrays.setAll(this.foodResourceOwn, i -> foodResourceOwn[i] - foodResourceNeed[i]);

            if (this.hungry < this.humgryUmbral)
                this.livePoints -= 1;
                //this.livePoints -= (this.humgryUmbral - this.hungry);
            else {
                this.livePoints += this.hungry - this.humgryUmbral;
                if (this.livePoints > this.livePointMax) this.livePoints = this.livePointMax;
            }
            //Check if dead
            if (this.lifeExp-this.lifeTime < 0) this.livePoints = 0;//Die by age
            if(this.livePoints <= 0) {
                //crear cadaver en descomposicion Class extends Int_ALife_Cre...
                this.livePoints = 0; //standarize
                die(); // Morir
                return; //End fo this thread and no more scheduled events for this creature
            }

            this.statusChangeEvolution(); //Register status changes for mind learning and evolution
            //2.- Think and do voluntary actions
            //For test
            Mind_ALife mal = this.mind; //For test
            Mind_ALife.Action decidedAction = this.mind.run();

            //For test
            if (decidedAction.equals(Mind_ALife.Action.REPRODUCE)) {
                if (this.getReproductable()) {
                    int breakpoint = 1;
                }
            }

            doAction(decidedAction);
            //this.doAction(this.mind.run());  // Real code
            //3.- Generacion de consecuncias en entorno.
            //if alive add next event.            
            env_ALive.addEvent(env_ALive.getTime() + lifeDelay, this);

            // Time to learn and evolve
            this.statusChangesUpdate(decidedAction);
            try{
                Thread.sleep(Env_ALife.CTE_TIEMPO_CEDER); // ceder tiempo de computo
                //Thread.currentThread().sleep(Env_ALife.CTE_TIEMPO_ESPERA_LARGA); // ceder tiempo de computo
                //System.out.println(Thread.currentThread().getName()+" - "+env_ALive.getTime());
            } catch (java.lang.InterruptedException ie){
                ie.printStackTrace();
            }              

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            //When we have to run again.    
            //env_ALive.addEvent(env_ALive.getTime()+lifeDelay, this);  
            //:
            try {
                semaphore.release();
                //this.env_ALive.release();
                
                //For Concurrency Log
                //MultiTaskUtil.threadMsg("Finalizado Thread  Creature!!"+semaphore.availablePermits());
                
                this.getEnv_ALife().MyNotifyAll();
            } catch(NullPointerException e){
                MultiTaskUtil.threadMsg("Null Point exception in Creature Run");
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace(); //Unknown error
            }
        }//End try catch - FINALLY
        //Self paint
        if(this.livePoints > 0) {
            paint(this.getEnv_ALife().getBackLifeImage(),Color.YELLOW, this.getEnv_ALife().getLockLiveImage());
        } else {
            //como borramos? Color(0, 0, 0, 0)
            paint(this.getEnv_ALife().getBackLifeImage(),null, this.getEnv_ALife().getLockLiveImage()); //new Color(0, 0, 0, 255)
        }

        //for test
            long auxbp1 = this.getIdCreature();
            long auxbp2 = this.getIdCreature() % DEFAULT_CreatureReportPercent;
            boolean auxbp3 = this.getIdCreature() % DEFAULT_CreatureReportPercent == 1;

        if (this.getIdCreature() % DEFAULT_CreatureReportPercent == 1){
            if (this.fileManager_Mind != null) {
                makeCreatureReport(this.fileManager_Mind);
            } else {
                MultiTaskUtil.threadMsg("Error in Creature Run: fileManager_Mind == null CreId("+this.getIdCreature()+")");
            }
        /*
        try{
            //String[] HEADERS = {"Time", "CreID","LiveT","liveExp","liveP","maxLiveP","Hungry","HungryUmbral","MaxHungry",
            //    "pos", "liveDelay","Def","Attack","SpecieID","TamComplex","FrO(R)","FrO(G)","FrO(B)","mFrO(R)","mFrO(G)","mFrO(B)",
            //    "MFrO(R)","MFrO(G)","MFrO(B)","FrN(R)","FrN(G)","FrN(B)","mFrFactor", "Hidden","DetectionRange","UmbralDetection",
            //    "minRepG", "MaxDescen" ,"StatusVal","Action","InnerN","MidN","OutN","StatusN",
            //    "MindUChangefr","WeightChangefr","WeightChangeUnderfr", "forecastStatusMean", "forecastGeneralError", "forecastGeneralVariance","MindOutput",
            //    "NeuroSep","NiD","tipo","Out","N_U", "Nforecast", "Nact", "Naction","wigths"
            //};

            ArrayList<String> reportList = new ArrayList<String>();
            reportList.add(String.valueOf(this.getEnv_ALife().getTime()));
            reportList.add(String.valueOf(this.idCreature));
            reportList.add(String.valueOf(this.lifeTime));
            reportList.add(String.valueOf(this.lifeExp));
            reportList.add(String.valueOf(this.livePoints));
            reportList.add(String.valueOf(this.livePointMax));
            reportList.add(String.valueOf(this.hungry));
            reportList.add(String.valueOf(this.humgryUmbral));
            reportList.add(String.valueOf((double)DEFAULT_Max_Hungry_factor));
            reportList.add("("+String.valueOf(this.pos.x)+","+String.valueOf(this.pos.y)+")");
            reportList.add(String.valueOf(this.lifeDelay));
            reportList.add(String.valueOf(this.def));
            reportList.add(String.valueOf(this.attack));
            reportList.add(String.valueOf(this.specieNumberID));
            reportList.add(String.valueOf(this.tamComplex));
            reportList.add(String.valueOf(this.foodResourceOwn[0]));
            reportList.add(String.valueOf(this.foodResourceOwn[1]));
            reportList.add(String.valueOf(this.foodResourceOwn[2]));
            reportList.add(String.valueOf(this.minfoodResourceOwn[0]));
            reportList.add(String.valueOf(this.minfoodResourceOwn[1]));
            reportList.add(String.valueOf(this.minfoodResourceOwn[2]));
            reportList.add(String.valueOf(this.maxfoodResourceOwn[0]));
            reportList.add(String.valueOf(this.maxfoodResourceOwn[1]));
            reportList.add(String.valueOf(this.maxfoodResourceOwn[2]));
            reportList.add(String.valueOf(this.foodResourceNeed[0]));
            reportList.add(String.valueOf(this.foodResourceNeed[1]));
            reportList.add(String.valueOf(this.foodResourceNeed[2]));
            reportList.add(String.valueOf(this.maxfoodResourceGetFactor));
            reportList.add(String.valueOf(this.hidden));
            reportList.add(String.valueOf(this.detectionRange));
            reportList.add(String.valueOf(this.umbralDetection));
            reportList.add(String.valueOf(this.minReproductionGroup));
            reportList.add(String.valueOf(this.maxDescendants));
            reportList.add(String.valueOf(this.descendents.size() ));
            DecimalFormat df = new DecimalFormat("0.############");
            //String numeroFormateado = df.format(numero);
            reportList.add(""+df.format(this.status)); //excel x100000000000
            reportList.add(String.valueOf(this.actions.get(this.actions.size()-1)));
            reportList.add(String.valueOf(this.innerN));
            reportList.add(String.valueOf(this.midN));
            reportList.add(String.valueOf(this.outN));
            reportList.add(String.valueOf(this.statusN));
            //Mind and each neuron
            this.mind.makeMindReport(reportList);

            String[] auxReport = new String[reportList.size()];
            auxReport = reportList.toArray(auxReport);
            this.fileManager_Mind.addLine(auxReport);
        }catch (Exception ioe){
            MultiTaskUtil.threadMsg("Error creating file manager");
        }
        */
        } //End if (this.getIdCreature() == 1)
        

        //Env_Panel.imageDisplay(this.env_ALive.getBackLifeImage(),"From Creature Run (LiveImage) - LIVE Image");
    } // End public void run()    

    /**
     * public static Creature dupeCreature(Creature c)
     * 
     * @param    Active_ALife_Creature c
     * @return   Creature
     */
    public static Active_ALife_Creature dupeCreature(Active_ALife_Creature c){
        Active_ALife_Creature newC = new Active_ALife_Creature(c);
        return newC;
    } // End public static Creature dupeCreature(Creature c)


    // Public Methods and Fuctions ==============


    /**
     * public boolean evaluateReproductionGroupAceptation(Acrive_ALife_Creature c)
     * 
     * Return true if creature c can be acepted in reproduction group 
     * and it will make this creature be added to c's reproduction group
     * @param    Active_ALife_Creature c
     * @return   boolean
     */
    public boolean evaluateReproductionGroupAceptation(Active_ALife_Creature c, double status){
        //Check
        if (c == null) return false;
        boolean acceptation = false;
        //FALTA !!!
        // Same specie or proximity.
        

        //Status proximity
        
        //Place on reproduction group or better than the other

        return acceptation;
    } // End public boolean evaluateReproductionGroupAceptation(Active_ALife_Creature c)

    /**
     * public ALife_FileManager setFileManager_Mind(ALife_FileManager fm)
     * 
     * Set file manager for creature mind evolution reports
     * @param   fm ALife_FileManager
     * @return  ALife_FileManager
     */
    public ALife_FileManager setFileManager_Mind(){
        if (this.fileManager_Mind != null) return this.fileManager_Mind;
        try{
            this.fileManager_Mind = new ALife_FileManager(this.getEnv_ALife().getName()+"_"+"Creature"+this.getIdCreature()+".csv", this);
        }catch (IOException ioe){
            MultiTaskUtil.threadMsg("SetFileManager Error creating file manager"+ ioe.getMessage());
        }
        this.fileManager_Mind.start();
        return this.fileManager_Mind;
    } // End public ALife_FileManager setFileManager_Mind(ALife_FileManager fm)

    // Private Methods and Fuctions =============
    /**
     * private void doAction(Mind_ALife.Action ac)
     * Execute action that come from mind
     * @param    Mind_ALife.Action
     * @return   None
     */
    public void doAction(Mind_ALife.Action ac){
        // UP,DOWN,RIGHT,LEFT,EAT,REPRODUCE,ATTACK,ADDREPGROUP,MAKETRACE
        switch(ac){
            case UP: 
                actionMove(0, -1); //UP
                break;
            case DOWN:
                actionMove(0, 1); //DOWN 
                break;
            case RIGHT: 
                actionMove(1, 0); //RIGHT
                break;
            case LEFT: 
                actionMove(-1, 0); //LEFT
                break;
            case EAT: 
                actionEat(this.pos, this.foodResourceNeed, null);//if eat someone null--> creature ate
                break;
            case REPRODUCE: 
                //Check
                if (this.reproductionGroup.size() > 0){
                    if (this.reproductionGroup.get(0) != this){
                        int breakpoint = 1; // !!!!!!!!!!
                    }
                }
                actionReproduce(this.reproductionGroup); // more progenitors?
                break;
            case ATTACK: 
                //Attack to other creature 
                actionAttack(this.prey);
                break;

            case ADDREPGROUP: 
                //Add to reproduction group, who?
                actionAddToReproductionGroup(this.favorite);
                break;

            case MAKETRACE: 
                //Create a voluntary trace
                break;
            
            default : break;
            
        }
    } // End private void doAction(Mind_ALife.Action ac)
    
    /**
     * private void actionMove(int x, int y)
     * 
     * Try to move creature to x points in x axis and y points in y axis
     * @param    int x, int y
     */
    private void actionMove(int x, int y){
        //Check if can move //No need check inide FALTA
        if (this.getEnv_ALife().getLogical_Env().detectColision(this, 
            new Point((this.pos.x + x + this.getEnv_ALife().getEnv_Width()) % this.getEnv_ALife().getEnv_Width(),
            (this.pos.y + y + this.getEnv_ALife().getEnv_Height()) % this.getEnv_ALife().getEnv_Height()), 0)) return;
        //Move
        //remove old creature imagen
        this.getEnv_ALife().getLogical_Env().moveCreature(this,this.pos.x + x, this.pos.y + y); //update inside creature.pos
        //Add new creature imagen

    } // End private void actionMove(int x, int y)
    

    /**
     * public void actionEat(Point pos, int[] foodResourceEat, Int_ALife_Creature cr)
     * 
     * Implementation of creature eat action abstact in Int_ALife_Creature
     * where Point, what int[] how much resource, what creature -- Want eat
     */
    public void actionEat(Point pos, int[] foodResourceEat, Int_ALife_Creature cr){
        if (cr == null){
            //remove from nutrient
            int[] foodResourceEatMax = {
                foodResourceEat[0] * this.maxfoodResourceGetFactor,
                foodResourceEat[1] * this.maxfoodResourceGetFactor,
                foodResourceEat[2] * this.maxfoodResourceGetFactor
            }; // Max resources we can get in TOTAL
            foodResourceEat = this.getEnv_ALife().getLand().getNutrient(pos, foodResourceEatMax, this); 
                //Its diferet creature
            //test
            int sumRes = 0, minRes = 0; // how much resources we gett in TOTAL
            for (int i = 0; i < foodResourceEatMax.length; i++){
                sumRes += foodResourceEatMax[i];
                minRes += foodResourceEat[i];
            }
            
            //for test
            if (sumRes == 0) {
                int breakPoint = 1;
            }
            //add to body
            grow(this.pos,foodResourceEat);
            //actualize hungry value inside grown
        } else { //cr != null , carnivore
            //Eat other creature
            if(cr instanceof Active_ALife_Creature) actionAttack(cr);
            else
                if(cr instanceof ALife_Corpse) { //(cr.getSpecieIdNumber() == -1)
                    //get nutrient from corpse FALTA
                }
        } 
    } // End public void actionEat(Point pos, int[] foodResourceEat, Int_ALife_Creature cr)
    
    /**
     * private void actionAttack(Int_ALife_Creature cr)
     * 
     * Attack cr creature
     * @param    Int_ALife_Creature cr  
     * @return   None
     */
    private void actionAttack(Int_ALife_Creature cr){
        //check
        if (cr == null) return;
        if (prey == null) prey = cr;
        if (prey instanceof ALife_Corpse){
            this.actionEat(prey.getPos(), foodResourceNeed, prey); // *10 
        }
        Int_ALife_Creature auxC = ALife_Input_Neuron_Utils.findNearestFoodCreature(this); 
        if (auxC != null){
            if (prey == null) prey = auxC;
            if (this.pos.distance(auxC.getPos()) < this.pos.distance(prey.getPos())) prey = auxC;
        }
        //Attack
        if (prey != null) prey.beAttacked(this, this.attack);
        //cr.beAttacked(this, this.attack);
    }

    /**
     * private void actionAddToReproductionGroup(Active_ALife_Creature fav)
     * 
     * Add to reproducction group a fav creature or choose it from nearly creatures
     * @param fav - Favorite creature to add to reproduction group
     * @return   None
     */
    private void actionAddToReproductionGroup(Int_ALife_Creature fav){
        //Check
        // if(this.reproductionGroup.size() >= this.minReproductionGroup) return;
        if (fav == null) {
            //Add to reproduction group a creature near
            fav = ALife_Input_Neuron_Utils.findNearestFavCreature(this);
        }
        if (fav == null) return; //Status min
        //Add to reproduction group
        if (fav.beAddedToReproductionGroup(this, this.status)) {
            AddToReproductionGroup(fav);
        }
        
    } // End private void actionAddToReproductionGroup(Active_ALife_Creature fav)

    /**
     * public synchronized void AddToReproductionGroup(Active_ALife_Creature fav)
     * 
     * Add to reproduction group a fav creature and actualize reproduction group
     * @param fav - Favorite creature to add to reproduction group
     * @return   None
     */
    public void AddToReproductionGroup(Int_ALife_Creature fav){
        //Check
        if (fav == null) return;
        double dist = ALife_Species.getDistancetoCreature(this, fav);
        if (dist < ALife_Species.SPECIE_DISTANCE){
            this.reproductionGroup.add(fav);
            while (this.reproductionGroup.size() > this.minReproductionGroup) {
                double val = 0;
                Int_ALife_Creature candidate = null;
                for (Int_ALife_Creature c :this.reproductionGroup){
                    if (val < c.status * (ALife_Species.SPECIE_DISTANCE - ALife_Species.getDistancetoCreature(this, c))) {
                        val = c.status * (ALife_Species.SPECIE_DISTANCE - ALife_Species.getDistancetoCreature(this, c));
                        candidate = c;
                    }
                }
                if (candidate != null) this.reproductionGroup.remove(candidate);
            }
        }
    } // End public synchronized void AddToReproductionGroup(Active_ALife_Creature fav)

    /**
     * private void grow(Point pos, int[] foodResourceEated)
     * Try to grow creature with foodResourceEated, if not posible return resources to ground
     * @param pos
     * @param foodResourceEated
     */
    private void grow(Point pos, int[] foodResourceEated){
        int[] rest = {0, 0, 0};
        boolean returFoodToGround = false;
        boolean isHungry = false;
        for (int i = 0; i <  foodResourceEated.length; i++){
            this.foodResourceOwn [i] += foodResourceEated[i];
            if (foodResourceOwn [i] > this.maxfoodResourceOwn[i]) {
                rest[i] = foodResourceOwn [i] - maxfoodResourceOwn[i];
                foodResourceOwn [i]= maxfoodResourceOwn[i];
                returFoodToGround = true;
                //for test
                if (rest[i] > 255){
                    int breakpoint = 1;
                }
            }
            if (this.foodResourceNeed [i] > foodResourceEated [i]) {
                this.hungry += foodResourceEated [i] - foodResourceNeed [i]; //May be too much
                isHungry = true;
            }
        }
        //Reduce hungry
        //if (!isHungry && this.hungry < 1.1*100) this.hungry += 1; // PENDIENTE
        if (!isHungry) { // && this.hungry < 1.1*100
            int sumN = 0, sumG = 0;
            for (int i = 0; i <  foodResourceEated.length; i++){
                sumN += foodResourceNeed [i];
                sumG += foodResourceEated [i];
            }
            this.hungry += sumG / sumN;
            if (this.hungry > this.humgryUmbral * Int_ALife_Creature.DEFAULT_Max_Hungry_factor) 
                this.hungry = (long) (this.humgryUmbral * Int_ALife_Creature.DEFAULT_Max_Hungry_factor);
        }
        

        //return resources to env rest[]
        if (returFoodToGround) 
            this.getEnv_ALife().getLand().germine(pos.x, pos.y, rest[0], rest[1], rest[2], 0);
    } // End private void grow(Point pos, int[] foodResourceEated)
    
    /**
     * private void statusChangesUpdate()
     * Store in arraylist variables the time, the actual status value and the action done
     * @param    - Mind_ALife.Action
     * @return   - None
     */
    private void statusChangesUpdate(Mind_ALife.Action ac){
        if (ac == null && this.lifeTime != 0) return;
        this.lockStatusMemory.lock();
        try{
            if (this.timeOfDeccision == null) this.timeOfDeccision = new ArrayList<Long>();
            if (this.actions == null) this.actions = new ArrayList<Mind_ALife.Action>();
            if (this.statusValues == null) this.statusValues = new ArrayList<Double>();
            if (this.timeOfDeccision.size()!=this.actions.size() || 
                this.timeOfDeccision.size() != this.statusValues.size()){
                MultiTaskUtil.threadMsg("Error in Creature statusChangesUpdate: size of arraylist are diferent");
                return;
            } 

                timeOfDeccision.add(this.getEnv_ALife().getTime());
                actions.add(ac);
                statusValues.add(Int_ALife_Creature.evaluateStatus(this));

        } finally {
            this.lockStatusMemory.unlock();
        }
    } // End private void statusChangesUpdate(Mind_ALife.Action ac)

    /**
     * private void statusChangeEvolution()
     * Lauch mind update with status changes
     * 
     * @param    None
     * @return   None
     */
    private void statusChangeEvolution(){ //self update creature status
        double statusNow = Int_ALife_Creature.evaluateStatus(this);
        //Check
        if (this.timeOfDeccision == null || this.actions == null || this.statusValues == null){
            MultiTaskUtil.threadMsg("Error in Creature statusChangeEvolution: arraylist are null");
            return;
        }
        this.lockStatusMemory.lock();
        try{
            if (this.timeOfDeccision.isEmpty() || 
                this.actions.isEmpty() || this.statusValues.isEmpty()){
                if (this.lifeTime > this.lifeDelay) //If this is not first execution them error
                    MultiTaskUtil.threadMsg("Error in Creature statusChangeEvolution: Any ArrayList is empty");
                return;
            }
        
            // Fast minimal updates
            if (this.actions.size() > 1 && this.timeOfDeccision.size() > 1 && 
                this.statusValues.size() > 1)
            {
                //for test 
                double viewBefore = statusValues.get(statusValues.size()-1);
                double viewNow = statusNow - statusValues.get(statusValues.size()-1);
                //End test
                /*
                if (this.actions.size() <= 1)
                    this.mind.updateMind(
                        actions.get(actions.size()-1),
                        statusNow - statusValue.get(statusValue.size()-1),
                        Mind_ALife.DEFAULT_Weight_changeFraction, 0
                    ); //For weight update no divided by time (LONG_TIME_UPDATE)
                else 
                */
                    this.mind.updateMind(
                        actions.get(actions.size()-1),
                        statusValues.get(statusValues.size()-1) - statusValues.get(statusValues.size()-2),
                        Mind_ALife.DEFAULT_Weight_changeFraction , Double.valueOf(0)
                    ); //For weight update no divided by time
            } // End minimal updates

            // Medium updates
            //this.mind.updateMind(actions.get(actions.size()-1), statusValue.get(statusValue.size()-1)-actualStatus, 
            //    WEIGHT * (timeOfDeccision.get(timeOfDeccision.size()-1) - this.env_ALive.getTime()));
            // Medium updates
            /*
            if (this.actions.size() > MEDIUM_TIME_UPDATE && this.timeOfDeccision.size() > MEDIUM_TIME_UPDATE && 
                this.statusValue.size() > MEDIUM_TIME_UPDATE){
                this.mind.updateMind(
                    actions.get(actions.size()-(int)MEDIUM_TIME_UPDATE - 1),
                     statusValue.get(statusValue.size()-1) - statusValue.get((int) (statusValue.size()- MEDIUM_TIME_UPDATE - 1)),
                    Mind_ALife.DEFAULT_Weight_changeFraction / (int)LONG_TIME_UPDATE * MEDIUM_TIME_UPDATE,Double.valueOf(0)
                );
            }
            // Long time updates
            if (this.actions.size() > LONG_TIME_UPDATE && this.timeOfDeccision.size() > LONG_TIME_UPDATE && 
                this.statusValue.size() > LONG_TIME_UPDATE){
                this.mind.updateMind(
                    actions.get(actions.size()-(int)LONG_TIME_UPDATE - 1),
                    statusValue.get(statusValue.size()-1) - statusValue.get((int) (statusValue.size()- LONG_TIME_UPDATE - 1)),
                    Mind_ALife.DEFAULT_Weight_changeFraction,Double.valueOf(0)
                );
            }
            */
            // Remove old useless datas
            if (actions.size() > LONG_TIME_UPDATE && timeOfDeccision.size() > LONG_TIME_UPDATE && 
                statusValues.size() > LONG_TIME_UPDATE){
                actions.remove(0);
                timeOfDeccision.remove(0);
                statusValues.remove(0);
            }
        } finally {
            this.lockStatusMemory.unlock();
        }
    } // End private void statusChangeEvolution()

    /**
     * private void actionReproduce(ArrayList<Int_ALife_Creature> progenitors)
     * 
     * @param    ArrayList<Int_ALife_Creature> progenitors
     * @return   None
     */
    public void actionReproduce(ArrayList<Int_ALife_Creature> progenitors){
        if(!this. getReproductable()) return;
        if (progenitors.size() < this.minReproductionGroup) return;
        for(int d = 0; d < this.maxDescendants; d++){
            int body = 0;
            for(int i = 0; i < foodResourceOwn.length ; i++){
                this.foodResourceOwn[i] = this.foodResourceOwn[i] - this.minfoodResourceOwn[i];
                body += this.foodResourceOwn[i];
            }
            if (body < 0) {
                MultiTaskUtil.threadMsg("Error in Creature actionReproduce: body < 0 before all descendats");
                return;
            }
            Int_ALife_Creature baby;
            //body reproduce + mutate
            try{
                baby = new Active_ALife_Creature(progenitors, this.getEnv_ALife().getAllowMutate());
                if (descendents == null) descendents = new ArrayList<Int_ALife_Creature>(); //For security
                this.descendents.add(baby);
            } catch (Exception e){
                MultiTaskUtil.threadMsg("Error in Creature actionReproduce: "+e.getMessage());
            }
            //mind reproduce + mutate /// ---> creo que ahora mind lo invoca el constructor de creature
        } //End for maxDescendants 
    } // End private void actionReproduce(ArrayList<Int_ALife_Creature> progenitors)
    
    /**
     * public synchronized void beAttacked(Active_ALife_Creature agressor, long attackValue)
     * 
     * Creature suffer damage cause a agressor creature attack (Overrided)
     * @param agressor
     * @param attackValue
     * @return None
     */
    @Override
    public synchronized void beAttacked(Active_ALife_Creature agressor, long attackValue){
        super.beAttacked(agressor, attackValue);
        //Check
        if(agressor == null) return;
        if(attackValue < 0) return;
        //Register attack
        this.enemyCreatureList.add(agressor);
        this.enemySpecieList.add(agressor.getEnv_ALife().getSpecies().getSpecieList((int)getSpecieIdNumber()-1));
        this.prey = agressor;
    } // End public synchronized void beAttacked(Active_ALife_Creature agressor, long attackValue)
    
    // Method from abstras still dont implemented --------------------------------------------------------------------
    /**
     * public long die()
     * 
     * @param    None
     * @return   long -- may be we need change to none
     */
    public long die() {
         if (this.fileManager_Mind != null) this.fileManager_Mind.KillFileManager();
        //For test
        MultiTaskUtil.threadMsg(getEnv_ALife().getTime()+" Creature DIED("+this.getIdCreature()+"-"+this.getSpecieIdNumber()+")");
        new ALife_Corpse(this);
        
        env_ALive.removeCreature(this);
        // Be sure this creature dont continue
        
        //delete from lifeImage
        return 0; //May be we return total resourece own
    }
    
    /**
     * public void eat(int x, int y, Int_ALife_Creature food)
     * 
     * @param    int x, int y, Int_ALife_Creature food
     * @return   None
     */
    public void eat(int x, int y, Int_ALife_Creature food){}
    
    /**
     * 
     */
    public void lookForBread(){} // Proliferation of life method
    
    /**
     * public void reproduze(Int_ALife_Creature couple)
     * 
     * @param    Int_ALife_Creature couple
     * @return   None
     */
    public Int_ALife_Creature reproduze(Int_ALife_Creature couple){
        return null;
    } //How to make new life
    
    /**
     * public String specieToString(Int_ALife_Creature c)
     * 
     * @param    Int_ALife_Creature c
     * @return   String
     */
    public String specieToString(Int_ALife_Creature c){
        return null;
    }
    
    /**
     * public void paint(BufferedImage g, Color col, ReentrantLock lock)
     * 
     * Paint creature in g image with color col
     * @param    BufferedImage g
     * @param    Color col
     * @param    ReentrantLock lock
     * @return   None
     */
    //@Override
    public void paint(BufferedImage g, Color col, ReentrantLock lock){
        if (g == null) return;

        double scaleW = (double) g.getWidth() / this.getEnv_ALife().getLand().getLandImg().getWidth();
        double scaleH =  (double) g.getHeight() / this.getEnv_ALife().getLand().getLandImg().getHeight();
        //int scale = Math.min(scaleW, scaleH);
        lock.lock();
        try{
            g.getType();
            Graphics2D g2d = g.createGraphics();
            Color creatureColor = col; // Color del círculo
            g2d.setColor(creatureColor);
            if (col != null){
                if (this.tamComplex > 1){ //Scale by tamComplex obsolete for moment
                    //g2d.drawOval(this.pos.x - (int)this.tamComplex, this.pos.y - (int)this.tamComplex,
                    //    (int)this.tamComplex * 1, (int)this.tamComplex * 1);
                    g2d.fillOval(this.pos.x - (int)this.tamComplex, this.pos.y - (int)this.tamComplex,
                        (int)this.tamComplex * 1, (int)this.tamComplex * 1);
                } else {
                    g2d.fillOval((int) (this.pos.x * scaleW), (int) (this.pos.y * scaleH),
                         (int) (1*scaleW), (int) (1 * scaleH));
                    //g2d.drawOval((int) (this.pos.x * scaleW), (int) (this.pos.y * scaleH),
                    //     (int) (1*scaleW), (int) (1 * scaleH));                     
                }
            } else {
                g = refreshLiveImage(this.getEnv_ALife().getCreatureList(), this.getEnv_ALife().getBackLifeImage());
                int breakpoint = 1;
                
                //g2d.clearRect((int) (this.pos.x * scaleW), (int) (this.pos.y * scaleH),
                //         (int) (1*scaleW), (int) (1 * scaleH)); // Borrar el rectángulo
                //g2d.setComposite(AlphaComposite.SrcOver);
            }
            this.getEnv_ALife().setBackLifeImage(g);
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }catch(Exception e){
            MultiTaskUtil.threadMsg("Error desconocido en Creature.Paint.");
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    } // End public void paint(BufferedImage g, Color col)
    
    /**
     * public static BufferedImage refreshLiveImage(TreeMap<Long, ArrayList<Object>> eventList, BufferedImage img)
     * 
     * Refresh full live image and all creatures from eventList
     * @param    TreeMap<Long, ArrayList<Object>> eventList
     * @param    BufferedImage img the original live image
     * @return   None
     */
    public static BufferedImage refreshLiveImage(ArrayList<Active_ALife_Creature> creatureList, BufferedImage img){
        if (creatureList == null || img == null) return img;
        BufferedImage i = img;
        //For test
        //MultiTaskUtil.threadMsg("Refresh Live Image");
        //Syste
        try{
            i = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = i.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            //g2d.setColor(Color.BLACK); // fix background to BLACK 0, 0, 0
            g2d.fillRect(0, 0, i.getWidth(), i.getHeight()); 
            //paint all creatures
            for (Active_ALife_Creature o : creatureList){
                if (o instanceof Active_ALife_Creature){
                    ((Active_ALife_Creature) o).paint(i, Color.YELLOW,o.getEnv_ALife().getLockLiveImage());
                }
            }
            g2d.dispose();
        }catch(Exception e){
            MultiTaskUtil.threadMsg("Error desconocido en Creature.refreshLiveImage."+e.getMessage());
        }
        finally{
        }
        return i;
    } // End public static BufferedImage refreshLiveImage(TreeMap<Long, ArrayList<Object>> eventList, BufferedImage img)

    /**
     * public static Active_ALife_Creature test1Creature()
     * 
     * Create specific creature form for tests
     * @param    None
     * @return   Active_ALife_Creature
     */
    public static Active_ALife_Creature test1Creature(){
        Active_ALife_Creature c = new Active_ALife_Creature();
        return c;
    } // End public static Active_ALife_Creature test1Creature()

    /**
     * private makeCreatureReport()
     * 
     * Make a report of creature status and mind
     * @param    None
     * @return   None
     */
    private void makeCreatureReport(ALife_FileManager fileManager){
        ArrayList<String> reportList = new ArrayList<String>();
        makeCreaturePhysicalReport(reportList);
            //Mind and each neuron
            this.mind.makeMindReport(reportList);

            String[] auxReport = new String[reportList.size()];
            auxReport = reportList.toArray(auxReport);
            fileManager.addLine(auxReport);
    } // End private void makeCreatureReport()

    public void makeCreatureSORTReport(ALife_FileManager fileManager){
        ArrayList<String> reportList = new ArrayList<String>();
        reportList = makeCreaturePhysicalReport(reportList);
            //Mind and each neuron
            reportList = this.mind.makeMindSORTReport(reportList);

            String[] auxReport = new String[reportList.size()];
            auxReport = reportList.toArray(auxReport);
            fileManager.addLine(auxReport);
    } // End makeCreatureSORTReport(ALife_FileManager fileManager)

    /**
     * private ArrayList <String> makeCreaturePhysicalReport(ArrayList <String> rep)
     * 
     * Make a report of creature status
     * @param rep - ArrayList <String> rep
     * @return  ArrayList <String> rep
     */
    private ArrayList <String> makeCreaturePhysicalReport(ArrayList <String> rep){
        ArrayList<String> reportList = new ArrayList<String>();
        this.lockCreature.lock();
        try{
            reportList.add(String.valueOf(this.getEnv_ALife().getTime()));
            reportList.add(String.valueOf(this.idCreature));
            reportList.add(String.valueOf(this.lifeTime));
            reportList.add(String.valueOf(this.lifeExp));
            reportList.add(String.valueOf(this.livePoints));
            reportList.add(String.valueOf(this.livePointMax));
            reportList.add(String.valueOf(this.hungry));
            reportList.add(String.valueOf(this.humgryUmbral));
            reportList.add(String.valueOf((double)DEFAULT_Max_Hungry_factor));
            reportList.add("("+String.valueOf(this.pos.x)+","+String.valueOf(this.pos.y)+")");
            reportList.add(String.valueOf(this.lifeDelay));
            reportList.add(String.valueOf(this.def));
            reportList.add(String.valueOf(this.attack));
            reportList.add(String.valueOf(this.specieNumberID));
            reportList.add(String.valueOf(this.tamComplex));
            reportList.add(String.valueOf(this.foodResourceOwn[0]));
            reportList.add(String.valueOf(this.foodResourceOwn[1]));
            reportList.add(String.valueOf(this.foodResourceOwn[2]));
            reportList.add(String.valueOf(this.minfoodResourceOwn[0]));
            reportList.add(String.valueOf(this.minfoodResourceOwn[1]));
            reportList.add(String.valueOf(this.minfoodResourceOwn[2]));
            reportList.add(String.valueOf(this.maxfoodResourceOwn[0]));
            reportList.add(String.valueOf(this.maxfoodResourceOwn[1]));
            reportList.add(String.valueOf(this.maxfoodResourceOwn[2]));
            reportList.add(String.valueOf(this.foodResourceNeed[0]));
            reportList.add(String.valueOf(this.foodResourceNeed[1]));
            reportList.add(String.valueOf(this.foodResourceNeed[2]));
            reportList.add(String.valueOf(this.maxfoodResourceGetFactor));
            reportList.add(String.valueOf(this.hidden));
            reportList.add(String.valueOf(this.detectionRange));
            reportList.add(String.valueOf(this.umbralDetection));
            reportList.add(String.valueOf(this.minReproductionGroup));
            reportList.add(String.valueOf(this.maxDescendants));
            reportList.add(String.valueOf(this.descendents.size() ));
            DecimalFormat df = new DecimalFormat("0.############");
            //String numeroFormateado = df.format(numero);
            reportList.add(""+df.format(this.status)); //excel x100000000000
            if (this.actions.size() > 0){
                reportList.add(String.valueOf(this.actions.get(this.actions.size()-1)));
            } else {
                reportList.add("Null");
            }
            reportList.add(String.valueOf(this.innerN));
            reportList.add(String.valueOf(this.midN));
            reportList.add(String.valueOf(this.outN));
            reportList.add(String.valueOf(this.statusN));
        }catch (Exception ioe){
            MultiTaskUtil.threadMsg("Error creating file manager"+ ioe.getMessage());
        } finally {
            this.lockCreature.unlock();
        }
        return reportList;
    } // End private void makeCreaturePhysicalReport(ArrayList <String> rep)

} // End Class Active_ALife_Creature
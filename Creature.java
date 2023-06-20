import java.awt.image.*;
import java.awt.*;
import java.util.concurrent.*;

//import Mind_ALife.Action;

import java.util.*;

/**
 * Write a description of class Creature here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Creature extends Int_ALife_Creature
{
    public static final int DEFAULT_LifeDelayPerNutrient = 10;
    public static final Color CREATURE_DEFAULT_COLOR =new Color (199,199,199);
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
        25, 15, 25, 5 //mind.getInnerN(), mind.getMidN(), mind.getOutN(), mind.getStatusN()
    };
    //Note: 2º value is TamComplex. Be care on changes same on MIND values
    
    public static int MIND_NEURONS_TYPES = 4; //Number of caracteristics reffered to Mind_ALife
    //End Constants for normalization Creature

    // Fields ----------------------------------------------------------------------------
    //-private Env_ALife env_ALive; // Enviroment 
    private BufferedImage backLand = null;
    private BufferedImage frontLand = null;    
    private Semaphore semaphore;
    
    //Creature caracteristics in Int_ALife_Creature
    ArrayList <Long> timeOfDeccision = new ArrayList<Long>(); // Time of deccision
    ArrayList <Mind_ALife.Action> actions = new ArrayList<Mind_ALife.Action>(); // Actions that creature can do
    ArrayList <Double> statusValue = new ArrayList<Double>(); // Status of creature

    //Make a variable with 3 fields long time field, double statusvalue and Mind_ALife.Action doneAction



        
    // Methods ---------------------------------------------------------------------------
    // Construcotors ============================
    /**
     * Constructor for objects of class ALife_Nutrient_Enviroment
     * @param   - Env_ALife
     * Create e simulation nutrient enviroment
     */
    //Creature(this.env_ALife,new Point(170,170),null,1000,haveR,needR
    //public Creature(Env_ALife env, Semaphore s, Point p){
    public Creature(Env_ALife env, Point p, Mind_ALife m, long lifeexp, int[] frOwn, int[] frNeed){
        super();
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
        double fac = 3 + random.nextInt(126)/100; //Factor of maxfoodResourceOwn from foodResourceOwn
        for (int i = 0; i< foodResourceOwn.length; i++){maxfoodResourceOwn[i] = (int)(foodResourceOwn[i] * fac);}

        //foodResourceNeed = {0,0,0};            
                
        hungry = Int_ALife_Creature.DEFAULT_Hungry_Humbral + 1;
    
        if (m != null) { this.mind = m;}
        else {//
            setMind(new Mind_ALife(this));
        }
        
        descendents = new ArrayList<Int_ALife_Creature>();
        reproductionGroup = new ArrayList<Int_ALife_Creature>();
        reproductionGroup.add(this);
        tamComplex = evaluateTamComplex(this); // Funcion de evaluacion?
        //this.env_ALive.addEvent(env_ALive.getTime() + 1, this); //se añade a la siguiente unidad de tiempo
        //specie = getALife_Specie;
        env_ALive.getSpecies().addCreature(this); //Automatically assign specieIdNumber    
    }// Ene public Creature(Env_ALife env)
        
    /**
     * Constructor for objects of class ALife_Nutrient_Enviroment
     * @param   - Env_ALife
     * Create e simulation nutrient enviroment
     * @throws Exception
     */
    //Creature(this.env_ALife,new Point(170,170),null,1000,haveR,needR
    //public Creature(Env_ALife env, Semaphore s, Point p){
    public Creature(ArrayList<Int_ALife_Creature> progenitors, boolean mutate) throws Exception{
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
        
        
        //Look for place to born
        this.setEnv_ALife(progenitors.get(0).getEnv_ALife()); //progenitors[0] = pregnant
        
        int auxX = 0, auxY = 0; //Where will be born
        Point auxP = new Point();
        auxP = progenitors.get(0).getPos();
        this.pos = null; //Nowhere to born ---> misbirth
        int w = getEnv_ALife().getEnv_Width();
        int h = getEnv_ALife().getEnv_Height();
        
        for(int lo = 1;lo < 3; lo++){
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
        //MEJORA lista posibles sitios y random
        if (this.pos == null){
            this.livePoints = 0; //born death as alternative
            this.pos = progenitors.get(0).getPos(); //born in same place but death MISSBIRTH
            if (this.env_ALive == null) this.env_ALive = progenitors.get(0).getEnv_ALife();
            this.lifeDelay = progenitors.get(0).lifeDelay;
            MultiTaskUtil.copyIntArrayContent(this.foodResourceOwn, 
                progenitors.get(0).minfoodResourceOwn);
            new ALife_Corpse(this);
            throw new Exception("Error in Creature constructor: pos == null");
            //return; // Misbirth detection
        }
        /*
        auxP = progenitors[0].pos;
        for(int lo = 1;lo < 3; lo++)
            for (int i = auxP.x-lo; i < auxP.x+lo ; i++)
        {   auxX = auxP.x;
                for(int j = auxP.y-lo;j <= auxP.y+lo; j++){
                    if (!env_ALive.getLogical_Env().detectColision(progenitors[0],new Point(i,j))){
                        //Asignar posicion de nacimiento
                        this.pos = new Point(i,j);
                        break;
                    }
            }
        }
        */
        
        
        //Int_ALife_Creature ausC = progenitors[0];
        //this.setEnv_ALife(progenitors[0].getEnv_ALife());
        idCreature = this.env_ALive.getNewCreatureID();
        semaphore = env_ALive.getSemaphore();
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
        
        //For test CHECKEO
        int ExtrangeValue = 255;
        boolean check  = false;
        for (int i = 0; i<3;i++){
            if ( foodResourceOwn[i] >= ExtrangeValue) {
                check = true;}
            if ( minfoodResourceOwn[i] >= ExtrangeValue) {
                check = true;}
            if ( maxfoodResourceOwn[i] >= 4 * ExtrangeValue) {
                check = true;}
            if ( foodResourceNeed[i] >= ExtrangeValue) {
                check = true;}
            if (check){ //to verify after break
            
                for (int j = 0; i<3;i++){
                    if ( foodResourceOwn[j] >= ExtrangeValue) {
                        check = true;}
                    if ( minfoodResourceOwn[j] >= ExtrangeValue) {
                        check = true;}
                    if ( maxfoodResourceOwn[j] >= 4 * ExtrangeValue) {
                        check = true;}
                    if ( foodResourceNeed[j] >= ExtrangeValue) {
                        check = true;}
                    }
            }
        }
        
        //END For test CHECKEO
        
        
        livePoints = livePointMax;

        mind = new Mind_ALife(progenitors,this,progenitors.get(0).env_ALive.getAllowMutate());//new Mind_ALife(progenitors);
        
        //Autocalculated datas
        hungry = Int_ALife_Creature.DEFAULT_Hungry_Humbral + 1; //0auto calculate

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
        env_ALive.getSpecies().addCreature(this); //Automatically assign specieIdNumber
    } // End public Creature(ArrayList<Int_ALife_Creature> progenitors)
    
    Creature(){
        //env_ALive.getSpecies().addCreature(null); //Automatically assign specieIdNumber ??
    }//Just to can make subclass constructors
    
    /**
     * private Creature(Creature c)
     * Make a duplication of creature
     * @param   - Creature
     */
    private Creature(Creature c){
        //Dupe creature
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
        this.statusValue = new ArrayList<Double>(); //ArrayList <Double> statusValue = new ArrayList<Double>(); // Status of creature
        for(Double d: c.statusValue){
            this.statusValue.add(d);
        }        
    } // End private Creature(Creature c)

    // Public Methods and Fuctions ==============
    
    // Getter and Setters

    /**
     * public boolean getReproductable()
     * 
     * Devuelve true si la criatura es capaz de reproducirse
     * @param    None
     * @return   boolean
     */   
    public boolean getReproductable(){
        boolean r = true;
        for(int i = 0; i < foodResourceOwn.length ; i++){
            if (foodResourceOwn[i] < minfoodResourceOwn[i] * 2) r = false;
        }
        //for test
        if (r) {
            int breackpoint = 1;}
        if(this.reproductionGroup.size() < minReproductionGroup) r = false;
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
    public void run(){
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
            //1.- Eventos involuntarios como morir
            this.statusChangeEvolution(); //Register status changes for mind learning and evolution
            
            if (this.hungry < this.humgryUmbral)
                this.livePoints -= (this.humgryUmbral - this.hungry);          
            //Check if dead
            if (this.lifeExp-this.lifeTime < 0) this.livePoints = 0;//Die by age
            if(this.livePoints <= 0) {
                //crear cadaver en descomposicion Class extends Int_ALife_Cre...
                this.livePoints = 0; //standarize
                die(); // Morir
                return; //End fo this thread and no more scheduled events for this creature
            }         
            //2.- Think and do voluntary actions
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
            this.lifeTime += this.lifeDelay;
            env_ALive.addEvent(env_ALive.getTime()+lifeDelay, this);

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
                
                this.env_ALive.MyNotifyAll();
            } catch(NullPointerException e){
                MultiTaskUtil.threadMsg("Null Point exception in Creature Run");
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace(); //Unknown error
            }
        }//End try catch - FINALLY
        //Self paint
        if(this.livePoints > 0) {
            paint(this.env_ALive.getBackLifeImage(),Color.YELLOW);
        } else {
            //como borramos? Color(0, 0, 0, 0)
            paint(this.env_ALive.getBackLifeImage(),new Color(0, 0, 0, 0));
        }
        //Env_Panel.imageDisplay(this.env_ALive.getBackLifeImage(),"From Creature Run (LiveImage) - LIVE Image");
    } // End public void run()    

    /**
     * public static Creature dupeCreature(Creature c)
     * 
     * @param    Creature c
     * @return   Creature
     */
    public static Creature dupeCreature(Creature c){
        Creature newC = new Creature(c);
        return newC;
    } // End public static Creature dupeCreature(Creature c)

    /**
     * public static double[] serializeCreature(Int_ALife_Creature c)
     * @param c Int_ALife_Creature
     * @return double[], the serialized creature
     
    public static double[] serializeCreature(Int_ALife_Creature c){
        //super.serializeCreature(c); //Can't use super in static method
        if (c == null) return null;
        
        ArrayList<Double> caracArrayList = new ArrayList<Double>();
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
        //caracArrayList.add(c.creatureTrace); //Don't know add or not
        int cmfo = 0, cMfo = 0, cfrn = 0;
        for(int i = 0; i < c.minfoodResourceOwn.length; i++){
            cmfo += c.minfoodResourceOwn[i];
            cMfo += c.maxfoodResourceOwn[i];
            cfrn += c.foodResourceNeed[i];
        }
        caracArrayList.add((double)cmfo);
        caracArrayList.add((double)cMfo);
        caracArrayList.add((double)cfrn);
        //Mind_ALife
        if (c instanceof Creature){
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
        //Make new array abd chage from Double to double
        double[] carac = new double[caracArrayList.size()];
        for (int i = 0; i < caracArrayList.size(); i++){
            carac[i] = caracArrayList.get(i);
        }
        //For test
        if (carac.length != 19) {
            int breakpoint = 1;
        }
        else{
            int breakpoint = Creature.creature_minCaracteristics.length;
            breakpoint = Creature.creature_maxCaracteristics.length;
            breakpoint = 1;
        }
        return carac;
    } // End public static double[] serializeCreature(Int_ALife_Creature c)
    */
    /**
     * public static double evaluateTamComplex(Int_ALife_Creature c)
     * Evaluate the tamComplex of creature, actualize it and return it
     * @param c
     * @return
    
    public static double evaluateTamComplex(Int_ALife_Creature c){
        double[] carac = Creature.serializeCreature(c);
        carac = ALifeCalcUtil.min_max_Array_Normalization(carac, Creature.creature_minCaracteristics, 
            Creature.creature_maxCaracteristics);
        //carac = ALifeCalcUtil.ponderation_Array(carac, ponderationArray);
        //double tamComplex = ALifeCalcUtil.arrayDistance(v1,v2);
        double tamComplex = ALifeCalcUtil.mean(carac);
        return tamComplex;
    } // End public static double evaluateTamComplex(Int_ALife_Creature c)
    */

    // Public Methods and Fuctions ==============
    // Private Methods and Fuctions =============
    /**
     * private void doAction(Mind_ALife.Action ac)
     * Execute action that come from mind
     * @param    Mind_ALife.Action
     * @return   None
     */
    public void doAction(Mind_ALife.Action ac){
        // UP,DOWN,RIGHT,LEFT,EAT,REPRODUCE,ATTACK
        switch(ac){
            case UP: break;
            case DOWN: break;
            case RIGHT: break;
            case LEFT: break;
            case EAT: 
                actionEat(this.pos, this.foodResourceNeed, null);//if eat someone null--> creature ate
                break;
            case REPRODUCE: 
                actionReproduce(this.reproductionGroup); // more progenitors?
                break;
            case ATTACK: break;
            
            default : break;
            
        }
    } // End private void doAction(Mind_ALife.Action ac)
    
    /**
     * private void actionEat(Point pos, int[] foodResourceEat, Creature cr)
     * where Point, what int[] how much resource, what creature -- Want eat
     */
    public void actionEat(Point pos, int[] foodResourceEat, Creature cr){
        if (cr == null){
            //remove from nutrient            
            foodResourceEat = this.env_ALive.getLand().getNutrient(pos, foodResourceEat, this); 
                //Its diferet creature
            //test
            int sumRes = 0; // how much resources we gett in TOTAL
            for (int i = 0; i < foodResourceEat.length; i++){
                sumRes += foodResourceEat[i];
            }
            
            if (sumRes == 0) {
                int breakPoint = 1;
            }
            //add to body
            grow(this.pos,foodResourceEat);
            //actualize hungry value
            
        } else {
            //Eat other creature
        } 
        
    } // End private void actionEat(Point pos, int[] foodResourceNeed, Creature cr)
    
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
            if (foodResourceNeed [i] > foodResourceEated [i]) {
                this.hungry += foodResourceEated [i] - foodResourceNeed [i]; //May be too much
                isHungry = true;
            }
        }
        //Reduce hungry
        if (!isHungry && this.hungry < 1.1*100) this.hungry += 1; // PENDIENTE
        //ese 100 habria que cambiralo por cte o formula

        //return resources to env rest[]
        if (returFoodToGround) 
            this.env_ALive.getLand().germine(pos.x, pos.y, rest[0], rest[1], rest[2], 0);
    } // End private void grow(Point pos, int[] foodResourceEated)
    
    /**
     * private void statusChangesUpdate()
     * Store in arraylist variables the time, the actual status value and the action done
     * @param    - Mind_ALife.Action
     * @return   - None
     */
    private void statusChangesUpdate(Mind_ALife.Action ac){
        if (ac == null) return;
        if (this.timeOfDeccision == null) this.timeOfDeccision = new ArrayList<Long>();
        if (this.actions == null) this.actions = new ArrayList<Mind_ALife.Action>();
        if (this.statusValue == null) this.statusValue = new ArrayList<Double>();
        if (this.timeOfDeccision.size()!=this.actions.size() || 
            this.timeOfDeccision.size() != this.statusValue.size()){
            MultiTaskUtil.threadMsg("Error in Creature statusChangesUpdate: size of arraylist are diferent");
            return;
        } 
        synchronized(this) {
            timeOfDeccision.add(env_ALive.getTime());
            actions.add(ac);
            statusValue.add(Int_ALife_Creature.evaluateStatus(this));
        }
    } // End private void statusChangesUpdate(Mind_ALife.Action ac)

    /**
     * private void statusChangeEvolution()
     * Lauch mind update with status changes
     * 
     * @param    None
     * @return   None
     */
    private void statusChangeEvolution(){
        double WEIGHT = 0.1; //Weight of actual status in evolution
        long MEDIUM_TIME_UPDATE = 5; //Medium time for update data
        long LONG_TIME_UPDATE = 15; //Long time for update data
        double actualStatus = Int_ALife_Creature.evaluateStatus(this);
        //Check
        if (this.timeOfDeccision == null || this.actions == null || this.statusValue == null){
            MultiTaskUtil.threadMsg("Error in Creature statusChangeEvolution: arraylist are null");
            return;
        }
        
        if (this.timeOfDeccision.isEmpty() || 
            this.actions.isEmpty() || this.statusValue.isEmpty()){
            MultiTaskUtil.threadMsg("Error in Creature statusChangeEvolution: Any ArrayList is empty");
            return;
        }
        
        // Fast minimal updates
        this.mind.updateMind(actions.get(actions.size()-1), statusValue.get(statusValue.size()-1)-actualStatus, 
            WEIGHT * (timeOfDeccision.get(timeOfDeccision.size()-1) - this.env_ALive.getTime()));
        // Medium updates

        // Long time updates

        // Remove old useless datas
        if (actions.size() > LONG_TIME_UPDATE && timeOfDeccision.size() > LONG_TIME_UPDATE && 
            statusValue.size() > LONG_TIME_UPDATE){
            actions.remove(0);
            timeOfDeccision.remove(0);
            statusValue.remove(0);
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
        for(int i = 0; i < foodResourceOwn.length ; i++){
            this.foodResourceOwn[i] = this.foodResourceOwn[i] - this.minfoodResourceOwn[i];
        }
        Int_ALife_Creature baby;
        //body reproduce + mutate
        try{
            baby = new Creature(progenitors, this.env_ALive.getAllowMutate());
        } catch (Exception e){
            MultiTaskUtil.threadMsg("Error in Creature actionReproduce: "+e.getMessage());
            return;
        } 
        if (baby == null) return;
        if (descendents == null) descendents = new ArrayList<Int_ALife_Creature>(); //For security
        this.descendents.add(baby);
        //mind reproduce + mutate /// ---> creo que ahora mind lo invoca el constructor de creature
        //baby.setMind(new Mind_ALife(progenitors, baby, this.env_ALive.getAllowMutate()));
        this.getEnv_ALife().addCreature(baby);
    } // End private void actionReproduce(ArrayList<Int_ALife_Creature> progenitors)
    
    // Method from abstras still dont implemented --------------------------------------------------------------------
    /**
     * public long die()
     * 
     * @param    None
     * @return   long -- may be we need change to none
     */
    public long die() {
        //For test
        MultiTaskUtil.threadMsg(getEnv_ALife().getTime()+" Creature DIED("+this.getIdCreature()+")");
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
    
    public String specieToString(Int_ALife_Creature c){
        return null;
    }
    
    //@Override
    public void paint(BufferedImage g, Color col){
        //super.paint(g);
        if (col == null) col = CREATURE_DEFAULT_COLOR;
        if (g == null) return;
        try{
            Graphics2D g2d = g.createGraphics();
            Color creatureColor = col; // Color del círculo
            g2d.setColor(creatureColor);
            if (this.tamComplex > 1){
                g2d.drawOval(this.pos.x - (int)this.tamComplex, this.pos.y - (int)this.tamComplex,
                    (int)this.tamComplex * 1, (int)this.tamComplex * 1);
            } else {
                g2d.drawOval(this.pos.x, this.pos.y, 1, 1); // If it's too big them we draw a point
            }
            //Env_Panel.imageDisplay(g,"From Creature paint() - LIVE Image");
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }catch(Exception e){
            MultiTaskUtil.threadMsg("Error desconocido en Creature.Paint.");
            e.printStackTrace();
        }
        
    }    
} // End Class
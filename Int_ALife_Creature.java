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
    
    Trace creatureTrace = null;
    double hidden = 0L; //0..1, 0 = No hidden
    int detectionRange = 1; //in pixels min 1
    double umbralDetection = 1; //0..1, 0 = Min detection
    
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

    /**
     * public static double[] serializeCreature(Int_ALife_Creature c)
     * @param c Int_ALife_Creature
     * @return double[], the serialized creature
     */
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
        if (carac.length != 19) return null; // Unknow creature type
        int l = carac.length;
        // same possibility to mutate all caracteristics other way an array to set the possibilities of each caracteristic
        Random r = new Random();
        int mutateCaracteristic = r.nextInt(l);
        double[] caracMutated = new double[l];
        for (int j = 0; j < l; j++){
            if (j == mutateCaracteristic) {
                double tempMin = 0; //Cuantity of minimum modification, other way if min = 0 modifycantion is always 0
                if (Creature.creature_minCaracteristics[mutateCaracteristic] == 0) {
                    tempMin = Creature.creature_maxCaracteristics[mutateCaracteristic] / 100;
                    /*
                    tempMin = Creature.creature_maxCaracteristics[mutateCaracteristic] / 
                      (Creature.creature_maxCaracteristics[mutateCaracteristic] + 
                        (100 * Creature.creature_maxCaracteristics[mutateCaracteristic])); //may be 1000
                    */
                    if (tempMin == 0) tempMin = 0.01;
                }else tempMin = Creature.creature_minCaracteristics[mutateCaracteristic];
                caracMutated[j] = 
                    carac[j] + (r.nextInt(3)-1) * MUTATION_DISTANCE * tempMin;//random +-1 * mutation distance
                //limits check
                if (caracMutated[j] < Creature.creature_minCaracteristics[mutateCaracteristic]) 
                    caracMutated[j] = Creature.creature_minCaracteristics[mutateCaracteristic];
                if (caracMutated[j] > Creature.creature_maxCaracteristics[mutateCaracteristic]) 
                    Creature.creature_maxCaracteristics[mutateCaracteristic] = caracMutated[j];
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
        double[] carac = Creature.serializeCreature(this);
        double[] caracMutated = Creature.mutateCreaturebySerialization(carac);
        int mutatedCar; //number of caracteristic have been mutated
        for (mutatedCar = 0; mutatedCar < carac.length; mutatedCar ++){
            if (carac[mutatedCar] != caracMutated[mutatedCar]) break;
        }
        //for test
        double d = ALifeCalcUtil.arrayDistance(carac, caracMutated);

        if (mutatedCar == carac.length || carac[mutatedCar] == caracMutated[mutatedCar]) return; //No mutation
        //Mutate creature
        if (mutatedCar < carac.length - Creature.MIND_NEURONS_TYPES){
        /*
         public static final double[] creature_minCaracteristics =
            {0, 1, 0, 0, 0, 0, 1, //hidden, tamComplex, attack, def, detectionRange, umbralDetection, humgryUmbral, 
            1, 100, 1, 1, 1, //lifeDelay, lifeExp, livePointMax, maxDescendants, minReproductionGroup, 
            1, 1, 1, //minfoodResourceOwn, maxfoodResourceOwn, foodResourceNeed,
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
                case 12 : //minfoodResourceOwn
                    mutateFood(this.minfoodResourceOwn, (int)caracMutated[mutatedCar]);
                    break;
                case 13 : //maxfoodResourceOwn
                    mutateFood(this.maxfoodResourceOwn, (int)caracMutated[mutatedCar]);
                    break;
                case 14 : //foodResourceNeed
                    mutateFood(this.foodResourceNeed, (int)caracMutated[mutatedCar]);
                    break;
                default :
                    MultiTaskUtil.threadMsg("Error in mutateCreature() in Int_ALife_Creature.java");
                    break;
            } // End switch (mutatedCar)
        } // End if // esto deberia ser para todos. Falta mutate Mind
        this.tamComplex = evaluateTamComplex(this);
    } // End public void mutateCreature()

    private int[] mutateFood(int[] food, int newcarac){
        int[] returnedFood = new int[food.length];
        MultiTaskUtil.copyIntArrayContent(returnedFood, food);
        int totFood = Arrays.stream(returnedFood).sum();
        if (totFood == newcarac) {return returnedFood;} //No mutation
        Random r = new Random();
        int whereMutate = r.nextInt(newcarac);
        for (int i = 0; i < food.length; i++) {
            if (returnedFood[i] >= whereMutate){
                if (totFood > (int)newcarac) 
                    returnedFood[i] -= Creature.creature_minCaracteristics[15];
                else returnedFood[i] += Creature.creature_minCaracteristics[15]; //food minumum modification
            }
        }
        //for test
        totFood = Arrays.stream(returnedFood).sum();
        if (totFood != newcarac) {
            MultiTaskUtil.threadMsg("Error in mutateFood() in Int_ALife_Creature.java, "+totFood+" != "+newcarac);
        }
        //End for test
        return returnedFood;
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
package inigo.github.evo_alife;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

//import org.xml.sax.InputSource;

//import inigo.github.evo_alife.Detect_RResource_Neuron_ALife;
//import inigo.github.evo_alife.Detect_ReproductionGroupFull_Neuron_ALife;

/**
 * Write a description of class Mind_ALife here.
 * 
 * @author Iñigo Marco
 * @version 20-06-2023
 */
public class Mind_ALife
{
    // Red de neuronas In/Mid/Out El max out se lleva la salida
    // Neuronas in evaluan las entradas + estadps y dan una salida que va a Mid o a Out
    // MID reciven todas las in y dan una salida (si parametro 0 ignoran la IN) + estados
    //Out reciben de In y de out (eliminamos los estados por simplicidad Genera una salida 
    //    y un feedback para refuerzo de neuronas y actualización de estados.
    // Estados se actualizan con las salida.
    
    //DEfault values and constants
    public static final double DEFAULT_u = 0.1;
    public static final double DEFAULT_Weight = 0.2;
    public static final double TRUE_in_double = 1;
    public static final double FALSE_in_double = 0;
    public static enum Action {UP,DOWN,RIGHT,LEFT,EAT,REPRODUCE,ATTACK,ADDREPGROUP,MAKETRACE};//add to reproductionGroup
    public static final double DEFAULT_u_changeFraction = 0.05;
    public static final double DEFAULT_Weight_changeFraction = 0.25; //0.1
    public static final double DEFAULT_weight_changeUnderFraction = 0.5;

    public static final double RandomActionProbability = 0.1; //0.1
    
    // Fields ----------------------------------------------------------------------------
    
    ArrayList <Out_Neuron_ALife> outputNeurons;
    ArrayList <Neuron_ALife> inputNeurons;
    ArrayList <Neuron_ALife> midNeurons;
    ArrayList <Neuron_ALife> statusNeurons;
    ArrayList <Neuron_ALife> allNeurons; //dudas de si es necesario
    
    Int_ALife_Creature creature;
    
    double innerN = 0;
    double midN = 0;
    double outN = 0;
    double statusN = 0;
    
    Double output = null;
    Out_Neuron_ALife outNeuron = null;

    ReentrantLock lockMind;

    public double u_changeFraction = DEFAULT_u_changeFraction;
    public double weight_changeFraction = DEFAULT_Weight_changeFraction; 
    public double weight_changeUnderFraction = DEFAULT_weight_changeUnderFraction;
    
    public int activationCont = 0;
    public double forecastStatusMean = 0L;
    public double forecastGeneralError = 0L;
    public double forecastGeneralVariance = 0L;
    
    
    
    // Simple action = move, eat, attack, reproduce
    //Complex action = secuence of fimple actions and Decisión strong.
    
    // Methods ---------------------------------------------------------------------------
    // Constructors ============================
    
    /**
     * public Mind_ALife(Int_ALife_Creature creature)
     * 
     * Default constructor - premakes mind
     * @param creature - the creature that owns the mind
     */
    public Mind_ALife(Int_ALife_Creature creature) throws IllegalArgumentException{
        //Basic Mind Used to tests
        lockMind = new ReentrantLock();
        this.creature = creature;
        creature.setMind(this); //update creature mind
        this.output = null;
        this.outNeuron = null;
        //Add inputNeurons
        allNeurons = new ArrayList <Neuron_ALife>();
        inputNeurons = new ArrayList <Neuron_ALife>();
        outputNeurons = new ArrayList <Out_Neuron_ALife>();
        statusNeurons = new ArrayList <Neuron_ALife>();
        midNeurons = new ArrayList <Neuron_ALife>();
        
        Neuron_ALife auxN = new Detect_Reproductable_Neuron_ALife(creature);
        this.addNeuron(auxN);

        auxN = new Detect_Hungry_Neuron_ALife(creature);
        this.addNeuron(auxN);
        
        //Add status or memory neurons 
        //Add MidNeurons
        //add output Neurons
        //public Out_Neuron_ALife(ArrayList <Neuron_ALife> ns, Creature c, Mind_ALife.Action action)
        auxN = new Out_Neuron_ALife(this.inputNeurons,creature, Action.EAT);
        //faltan inpits y pesos
        
        //allNeurons.add(auxN);
        //outputNeurons.add((Out_Neuron_ALife)auxN);
        this.addNeuron(auxN);
        auxN = new Out_Neuron_ALife(this.inputNeurons,creature, Action.REPRODUCE);
        //allNeurons.add(auxN);
        //outputNeurons.add((Out_Neuron_ALife)auxN);
        this.addNeuron(auxN);
        this.output = null;
        setCreature(creature);
        setNeuronsCount();
        validateMind();
        if (!checkNeurons()){
            throw new IllegalArgumentException("No se puede crear el objeto debido a una condición inválida.");
        }
        creature.setMind(this); //update creature mind
        //Add In Neuron habmbre
        //Add In neuron comida en el suelo
        //Add out Neuron mitosis = reproduccion independiente
        //Add out Neuron comer
    }// End public Mind_ALife(Int_ALife_Creature creature)
    
    /**
     * public Mind_ALife(ArrayList<Int_ALife_Creature>  progenitors, Int_ALife_Creature creature, boolean mutate)
     * 
     * Constructor for a mind from other minds by reproduction
     * @param progenitors - The list of progenitors Int_ALife_Creature
     * @param creature - The creature that owns the mind
     * @param mutate - true if the mind can must mutate
     */
    public Mind_ALife(ArrayList<Int_ALife_Creature>  progenitors, Int_ALife_Creature creature, boolean mutate) 
        throws IllegalArgumentException
    {
        //Inners Neuron
        // Si la tienen los dos la tiene, si la tiene 1 50% si no la tiene 0% + mutate 
        lockMind = new ReentrantLock();
        this.creature = creature;
        creature.setMind(this); //update creature mind
        this.output = null;
        this.outNeuron = null;
        
        this.inputNeurons = new ArrayList <Neuron_ALife>();
        this.outputNeurons = new ArrayList <Out_Neuron_ALife>();
        this.midNeurons = new ArrayList <Neuron_ALife>();
        this.allNeurons = new ArrayList <Neuron_ALife>();
        this.statusNeurons = new ArrayList <Neuron_ALife>();

        // Create mix of progenitors Minds. Later will be mutated
        ArrayList <Mind_ALife> progenitorsMinds = new ArrayList <Mind_ALife>(); //Mind of progenitors
        for (Int_ALife_Creature p:progenitors){ //each progenitor
            if (p.getMind() != null){
                progenitorsMinds.add(p.getMind());
            }else{ //fake progenitor ???
                MultiTaskUtil.threadMsg("Creator of Mind" + this.creature.idCreature + 
                    "Mind_ALife: progenitor without mind."+p.getIdCreature());
                progenitors.remove(p); //test if affect to this list creation order
            } // End if (p.getMind() != null)
        } // End for (Int_ALife_Creature p:progenitors)

        //Add all progenitors input neurons to this mind own similar neurons
        createInnerNeurons(this, progenitorsMinds);

        //Mid Neuron
        createMidNeurons(this, progenitorsMinds);
        
        //Status Neuron
        this.createStatusNeurons(this, progenitorsMinds);

        //out Neuron
        createOutNeurons(this, progenitorsMinds);

        //update weights and bias (u)
        this.updateMixedWeights(this, progenitorsMinds);
        
        //Finalmente poda por complexity
        setCreature(creature);
        setNeuronsCount();
        if (!validateMind()){
            MultiTaskUtil.threadMsg("Mind_ALife: Mind created by birth. Validation FAil"+ this.creature.getIdCreature());
        }
        if (!checkNeurons()){
            throw new IllegalArgumentException("No se puede crear el objeto debido a una condición inválida.");
        }        
        //Reevaluar complexity
        creature.setMind(this); //update creature mind
    } // End public Mind_ALife(Int_ALife_Creature[] progenitors, Int_ALife_Creature creature, boolean mutate)
    
    /**
     * public Mind_ALife(Mind_ALife m)
     * 
     * Copy constructor
     * @param m Mind_ALife the mind to copy
     * @return Mind_ALife the new mind
     */
    protected  Mind_ALife(Mind_ALife m) throws IllegalArgumentException{
        lockMind = new ReentrantLock();
        this.allNeurons = new ArrayList <Neuron_ALife>();
        this.inputNeurons = new ArrayList <Neuron_ALife>();
        this.midNeurons = new ArrayList <Neuron_ALife>();
        this.outputNeurons = new ArrayList <Out_Neuron_ALife>();
        this.statusNeurons = new ArrayList <Neuron_ALife>();
        //this.creature = m.creature;
        ArrayList <Neuron_ALife> auxInputNList = new ArrayList <Neuron_ALife>();
        ArrayList <Neuron_ALife> auxMidNList = new ArrayList <Neuron_ALife>();
        ArrayList <Neuron_ALife> auxStatusNList = new ArrayList <Neuron_ALife>();
        ArrayList <Neuron_ALife> auxOutNList = new ArrayList <Neuron_ALife>();

        synchronized (m){
            for (Neuron_ALife n:m.inputNeurons){
                //Inputs neurons have no inputs
                Neuron_ALife auxN = n.dupeNeuron_ALife();
                //this.addNeuron(auxN);
                auxInputNList.add(auxN);
            }
            //At this moment auxNLis have all m.inputNeurons

            //Mid Neurons
            for (Neuron_ALife n:m.midNeurons){
                Neuron_ALife auxN = n.dupeNeuron_ALife();
                auxN.inputs = auxInputNList;
                //we need asing weights
                auxN = assingWeightsToDupeNeuron(auxN, n, auxInputNList);
                auxMidNList.add(auxN);
                //this.addNeuron(auxN);
            } // End for (Neuron_ALife n:m.midNeurons)
            //we have all midNeurons in auxMidNList

            //Status Neurons
            for (Neuron_ALife n:m.statusNeurons){
                Neuron_ALife auxN = n.dupeNeuron_ALife();
                ArrayList <Neuron_ALife> auxNList = new ArrayList <Neuron_ALife>();
                for (Neuron_ALife n_:auxInputNList) auxNList.add(n_);
                for (Neuron_ALife n_:auxMidNList) auxNList.add(n_);
                auxN.inputs = auxNList;
                //we need asing weights
                auxN = assingWeightsToDupeNeuron(auxN, n, auxNList);
                //this.addNeuron(auxN);
                auxStatusNList.add(auxN);
            }

            //Out Neurons
            for (Out_Neuron_ALife n:m.outputNeurons){
                //Neuron_ALife auxN = n.dupeNeuron_ALife();
                Out_Neuron_ALife auxN = n.dupeNeuron_ALife();
                ArrayList <Neuron_ALife> auxNList = new ArrayList <Neuron_ALife>();
                for (Neuron_ALife n_:auxInputNList) auxNList.add(n_);
                for (Neuron_ALife n_:auxMidNList) auxNList.add(n_);
                for (Neuron_ALife n_:auxStatusNList) auxNList.add(n_);
                auxN.inputs = auxNList;
                //we need asing weights
                auxN = (Out_Neuron_ALife)assingWeightsToDupeNeuron(auxN, n, auxNList);
                //this.addNeuron(auxN);
                auxOutNList.add(auxN);
                if (n == m.outNeuron){ //Actualize out neuron to this mind out neuron
                    this.outNeuron = (Out_Neuron_ALife)auxN;
                }            
            } // End for (Neuron_ALife n:m.outputNeurons)
            for (Neuron_ALife n:auxInputNList) this.addNeuron(n);
            for (Neuron_ALife n:auxMidNList) this.addNeuron(n);
            for (Neuron_ALife n:auxStatusNList) this.addNeuron(n);
            for (Neuron_ALife n:auxOutNList) this.addNeuron(n);
            this.creature = null; // we have no other creature. we will need to update later
            this.output = m.output;
        } // End synchronized (m)
        setNeuronsCount();
        validateMind();
        if (!checkNeurons()){
            throw new IllegalArgumentException("No se puede crear el objeto debido a una condición inválida.");
        }

    } // End public Mind_ALife(this.creature)

    public Mind_ALife(int inN, int miN, int stN, int ouN, Int_ALife_Creature creature) throws IllegalArgumentException
    {//int st2N, int ou2N
        //Check
        if (creature == null) throw new IllegalArgumentException("No se puede crear Mind_ALifem creature = NULL.");
        lockMind = new ReentrantLock();
        this.creature = creature;
        creature.setMind(this); //update creature mind
        this.output = null;
        this.outNeuron = null;
        //Add inputNeurons
        allNeurons = new ArrayList <Neuron_ALife>();
        inputNeurons = new ArrayList <Neuron_ALife>();
        outputNeurons = new ArrayList <Out_Neuron_ALife>();
        statusNeurons = new ArrayList <Neuron_ALife>();
        midNeurons = new ArrayList <Neuron_ALife>();

        ArrayList <Neuron_ALife> auxNList = new ArrayList <Neuron_ALife>();
        ArrayList <Neuron_ALife> auxNInputsList = new ArrayList <Neuron_ALife>();

        if (inN < 0) inN = (int) Active_ALife_Creature.creature_maxCaracteristics[Active_ALife_Creature.creature_maxCaracteristics.length - 4];
        if (miN < 0) miN = (int) Active_ALife_Creature.creature_maxCaracteristics[Active_ALife_Creature.creature_maxCaracteristics.length - 3];
        if (ouN < 0) ouN = (int) Active_ALife_Creature.creature_maxCaracteristics[Active_ALife_Creature.creature_maxCaracteristics.length - 2];
        if (stN < 0) stN = (int) Active_ALife_Creature.creature_maxCaracteristics[Active_ALife_Creature.creature_maxCaracteristics.length - 1];


        //all posible input neurons
        //Own status detection
        auxNList.add(new Detect_Reproductable_Neuron_ALife(creature));
        auxNList.add(new Detect_Hungry_Neuron_ALife(creature));
        auxNList.add(new Detect_ReproductionGroupFull_Neuron_ALife(creature));

        auxNList.add(new Detect_RResource_Neuron_ALife(creature));
        auxNList.add(new Detect_GResource_Neuron_ALife(creature));
        auxNList.add(new Detect_BResource_Neuron_ALife(creature));
        auxNList.add(new Detect_liveTimeFraction_Neuron_ALife(creature));
        auxNList.add(new Detect_livePointsFraction_Neuron_ALife(creature));
        auxNList.add(new Detect_attackPoints_Neuron_ALife(creature));
        auxNList.add(new Detect_defPoints_Neuron_ALife(creature));

        //proximity
        //auxNList.add(new ); //nearest neighbour x, -x, y, -y distance
        //auxNList.add(new ); //nearest neighbour same specie x, -x, y, -y distance
        //auxNList.add(new ); //nearest neighbour enemy x, -x, y, -y distance
        //auxNList.add(new );//nearest neighbour family x, -x, y, -y distance
        //auxNList.add(new );//nearest neighbour foodspecie x, -x, y, -y distance
        //auxNList.add(new );//neighbour count
        //auxNList.add(new );//neighbour count same specie
        //auxNList.add(new );//neighbour count enemy
        //auxNList.add(new );//neighbour count family
        //auxNList.add(new );//neighbour count foodspecie
        //Trace detection

        //For test
        int breakPoint = auxNList.size();
        while (auxNList.size() > inN){
            auxNList.remove((int)( (Math.random()*auxNList.size()*100 - 1)/100 ) );
        }
        //remove randomly till size() == inN. If inN<0 no removes
            //this.addNeuron form auxNList

        for(Neuron_ALife n:auxNList){
            this.addNeuron((Input_Neuron_ALife)n);
        }

        //Create miN midNeurons
        auxNInputsList = auxNList; // Mid neuron inputs usual inputs are inputNeurons
        auxNList= new ArrayList <Neuron_ALife>();
        for (int num = 0; num < miN; num++ ){
            Neuron_ALife auxN = new Neuron_ALife(auxNInputsList,creature); // May be other constructor
            auxN.normalizeWeights();
            auxNList.add(auxN);
        }
        for(Neuron_ALife n:auxNList){
            this.addNeuron(n);
        }

        //Create stN statusNeurons
        for (Neuron_ALife n:auxNList)auxNInputsList.add(n);
        auxNList= new ArrayList <Neuron_ALife>();
        for (int num = 0; num < miN; num++ ){
            Neuron_ALife auxN = new Neuron_ALife(auxNInputsList,creature); // May be other constructor
            auxN.normalizeWeights();
            auxNList.add(auxN);
        }
        for(Neuron_ALife n:auxNList){
            this.addNeuron(n);
        }

        //Create all posible outNeurons
        for (Neuron_ALife n:auxNList)auxNInputsList.add(n);
        auxNList= new ArrayList <Neuron_ALife>();
        //create neuron for all actions
        for(Action a:Mind_ALife.Action.values()){
            Neuron_ALife auxN = new Out_Neuron_ALife(auxNInputsList,creature,a);
            auxN.normalizeWeights();
            auxNList.add(auxN);
        }
        //remove randomly till size() == ouN
        while (auxNList.size() > ouN){
            auxNList.remove((int)( (Math.random()*auxNList.size()*100 - 1)/100 ) );
        }
        for(Neuron_ALife n:auxNList){
            this.addNeuron(n);
        }

        //make extra connections inputs and weights and neuronsids
        //Add status neurons to midNeurons inputs
        for (Neuron_ALife n:this.midNeurons){
            for (Neuron_ALife n_:this.statusNeurons){
                if (n.inputs.contains(n_)) continue; //already connected
                n.inputs.add(n_);
                n.weights.add(Mind_ALife.DEFAULT_Weight);
            }
            n.normalizeWeights();
        }
        //Add status neurons to statusNeurons inputs
        for (Neuron_ALife n:this.statusNeurons){
            for (Neuron_ALife n_:this.statusNeurons){
                if (n.inputs.contains(n_)) continue; //already connected
                n.inputs.add(n_);
                n.weights.add(Mind_ALife.DEFAULT_Weight);
            }
            n.normalizeWeights();
        }

        setCreature(creature);
        if (!validateMind())
            MultiTaskUtil.threadMsg("Mind_ALife: Mind created. In  Mind_ALife(int inN, int miN, int stN, int ouN, Int_ALife_Creature creature)-- NOT VALID");
        this.setNeuronsCount();
        
        if (!checkNeurons()){
            throw new IllegalArgumentException("No se puede crear el objeto debido a una condición inválida.");
        }
    } // End public Mind_ALife(int inN, int miN, int stN, int ouN, Int_ALife_Creature creature)

    /**
     * private boolean createInnerNeurons(Mind_ALife m, ArrayList <Mind_ALife> progenitorsMinds)
     * 
     * Create the inputNeurons of a mind from other minds
     * @param m Mind_ALife the mind to create the inputNeurons
     * @param progenitorsMinds ArrayList <Mind_ALife> the list of progenitors minds
     * @return boolean true if success
     */
    private boolean createInnerNeurons(Mind_ALife m, ArrayList <Mind_ALife> progenitorsMinds){
        boolean success = true;
        int[] i = new int[progenitorsMinds.size()];
        for (int j = 0; j < i.length; j++) i[j] = 0; // Numerical index for each progenitor mind array
        Input_Neuron_ALife n = (Input_Neuron_ALife)progenitorsMinds.get(0).inputNeurons.get(0);
        while (n != null){
            for (int j = 0; j < progenitorsMinds.size(); j++){ //each progenitor mind
                Mind_ALife mj= progenitorsMinds.get(j);//for test
                Input_Neuron_ALife nj= (Input_Neuron_ALife)(progenitorsMinds.get(j)).inputNeurons.get(i[j]);
                if ( !Input_Neuron_ALife.isBiggerThan(n,nj)){ //n <= nj
                    n = nj; //n is the smallest inputNeuron to be copied
                }
            }
            // Add n to this mind
            Input_Neuron_ALife auxN = n.dupeNeuron_ALife(); //new inputNeuron
            m.addNeuron(auxN); //Add and get neuron_ID
            //m.inputNeurons.add(auxN); 
            //m.allNeurons.add(auxN);
            //Actualize index values.
            for (int j = 0; j < progenitorsMinds.size(); j++){ //each progenitor mind
                //Mind_ALife mj= progenitorsMinds.get(j); //for text
                Input_Neuron_ALife nj= (Input_Neuron_ALife)(progenitorsMinds.get(j)).inputNeurons.get(i[j]);
                //for test
                //MultiTaskUtil.threadMsg(n.getClass()+" vs "+nj.getClass());
                if (n.getClass() == nj.getClass()) i[j]++; //if same inputNeuron type increase index
                else if (progenitorsMinds.get(j).allNeurons.contains(n)) {
                    // progenitor mind no ordered
                    MultiTaskUtil.threadMsg("Creator of Mind" + this.creature.idCreature + 
                        "Mind_ALife: progenitor mind no ordered.");
                    success = false;
                }
            }
            n = null;
            //Search for new inputNeuron to add
            for (int j = 0; j < i.length; j++){
                if (i[j] < progenitorsMinds.get(j).inputNeurons.size()){
                    n = (Input_Neuron_ALife)progenitorsMinds.get(j).inputNeurons.get(i[j]);
                    break; //inputNeuron found
                }
            }
            //We have new inputNeuron to add or null and we have finished
        }// End while (n != null)
        //this.inputNeurons done.
        //for test
        int breakpoint = this.inputNeurons.size();

        return success;
    } // End private boolean createInnerNeurons(Mind_ALife m, ArrayList <Mind_ALife> progenitorsMinds)

    /**
     * private boolean createMidNeurons(Mind_ALife m, ArrayList <Mind_ALife> progenitorsMinds)
     * 
     * Create the midNeurons of a mind from other minds
     * @param m Mind_ALife the mind to create the midNeurons
     * @param progenitorsMinds ArrayList <Mind_ALife> the list of progenitors minds
     * @return boolean true if success
     */
    private boolean createMidNeurons(Mind_ALife mind, ArrayList <Mind_ALife> progenitorsMinds){
        // Create Mid Neurons for this new mind. Same number of midNeurons than the progenitor with more midNeurons
        // Dont update weights and bias (u) yet
        int auxMidNCount = 0; //Number of midNeurons of the progenitor with more midNeurons
        int indProg = 0;      //Index of the progenitor with more midNeurons
        for (Mind_ALife m:progenitorsMinds){ //each progenitor mind
            if (m.midNeurons.size() > auxMidNCount){ 
                auxMidNCount = m.midNeurons.size();
                indProg = progenitorsMinds.indexOf(m);
            }
        }
        for (Neuron_ALife mn:progenitorsMinds.get(indProg).midNeurons){ //each midNeuron of the progenitor with more midNeurons
            //Add midNeuron
            Neuron_ALife auxN = mn.dupeNeuron_ALife(); //dupe MidNeuron of the progenitor with more midNeurons
            //Update bias = u - For moment same values as most midNeurons owners progenitor
            auxN.u = 0;
            double auxU = 0;
            double num = 0;
            for (Mind_ALife m:progenitorsMinds){ //each progenitor mind
                if (m.midNeurons.size() > mind.midNeurons.size()){ 
                    auxU += m.midNeurons.get(mind.midNeurons.size()).u;
                    num += 1;
                }
            }
            if (num == 0) auxU = Mind_ALife.DEFAULT_u;
            else auxU = auxU / num;
            auxN.u = auxU;
            mind.addNeuron(auxN);
        } // End for (Neuron_ALife mn:progenitorsMinds.get(indProg).midNeurons)
        return true;
    } //End public boolean createMidNeurons(this, progenitorsMinds)

    /**
     * private boolean createStatusNeurons(Mind_ALife m, ArrayList <Mind_ALife> progenitorsMinds)
     * 
     * Create the midNeurons of a mind from other minds
     * @param m Mind_ALife the mind to create the midNeurons
     * @param progenitorsMinds ArrayList <Mind_ALife> the list of progenitors minds
     * @return boolean true if success
     */
    private boolean createStatusNeurons(Mind_ALife mind, ArrayList <Mind_ALife> progenitorsMinds){
        // Create Status Neurons for this new mind. Same number of StatusNeurons than the progenitor with more StatusNeurons
        // Dont update weights and bias (u) yet
        int auxStatusNCount = 0; //Number of StatusNeurons of the progenitor with more StatusNeurons
        int indProg = 0;      //Index of the progenitor with more StatusNeurons
        for (Mind_ALife m:progenitorsMinds){ //each progenitor mind
            if (m.statusNeurons.size() > auxStatusNCount){ 
                auxStatusNCount = m.statusNeurons.size();
                indProg = progenitorsMinds.indexOf(m);
            }
        }
        for (Neuron_ALife mn:progenitorsMinds.get(indProg).statusNeurons){ //each StatusNeuron of the progenitor with more StatusNeurons
            //Add StatusNeuron
            Neuron_ALife auxN = mn.dupeNeuron_ALife(); //dupe statusNeuron of the progenitor with more statusNeurons
            //Update bias = u - For moment same values as most statusNeurons owners progenitor
            auxN.u = 0;
            double auxU = 0;
            double num = 0;
            for (Mind_ALife m:progenitorsMinds){ //each progenitor mind
                if (m.statusNeurons.size() > mind.statusNeurons.size()){ 
                    auxU += m.statusNeurons.get(mind.statusNeurons.size()).u;
                    num += 1;
                }
            }
            if (num == 0) auxU = Mind_ALife.DEFAULT_u;
            else auxU = auxU / num;
            auxN.u = auxU;
            mind.addNeuron(auxN);
        } // End for (Neuron_ALife mn:progenitorsMinds.get(indProg).statusNeurons)
        return true;
    } //End public boolean createstatusNeurons(this, progenitorsMinds)

    /**
     * private boolean createOutNeurons(Mind_ALife m, ArrayList <Mind_ALife> progenitorsMinds)
     * 
     * Create the outNeurons of a mind from other minds
     * @param m Mind_ALife the mind to create the midNeurons
     * @param progenitorsMinds ArrayList <Mind_ALife> the list of progenitors minds
     * @return boolean true if success
     */
    private boolean createOutNeurons(Mind_ALife mind, ArrayList <Mind_ALife> progenitorsMinds){
        // Create Out Neurons for this new mind.
        boolean success = true;
        int[] i = new int[progenitorsMinds.size()];
        for (int j = 0; j < i.length; j++) i[j] = 0; // Numerical index for each progenitor mind array
        Out_Neuron_ALife n = (Out_Neuron_ALife)progenitorsMinds.get(0).outputNeurons.get(0);
        while (n != null){ //Evaluate all out neurons of all progenitors
            for (int j = 0; j < progenitorsMinds.size(); j++){ //each progenitor mind
                //Mind_ALife mj= progenitorsMinds.get(j);//for test
                Out_Neuron_ALife nj= (Out_Neuron_ALife)(progenitorsMinds.get(j)).outputNeurons.get(i[j]);//i index of j progenitor
                if ( !Out_Neuron_ALife.isBiggerThan(n,nj)){ //n <= nj
                    n = nj;
                }
            }
            //Add n to this mind
            Out_Neuron_ALife auxN = n.dupeNeuron_ALife();
            mind.addNeuron(auxN); //Add and get neuron_ID
            //mind.outputNeurons.add(auxN); // test if dupe make a correct copy
            //mind.allNeurons.add(auxN);
            //update index.
            for (int j = 0; j < progenitorsMinds.size(); j++){ //each progenitor mind
                //Mind_ALife mj= progenitorsMinds.get(j); //for text
                Out_Neuron_ALife nj= (Out_Neuron_ALife)(progenitorsMinds.get(j)).outputNeurons.get(i[j]);
                //for test
                //MultiTaskUtil.threadMsg(n.getClass()+" vs "+nj.getClass());
                if (n.getAction() == nj.getAction()) i[j]++; //if same inputNeuron type increase index
                else if (progenitorsMinds.get(j).allNeurons.contains(n)) {
                    // progenitor mind no ordered
                    MultiTaskUtil.threadMsg("Creator of Mind (outNeurons)" + this.creature.idCreature + 
                        "Mind_ALife: progenitor mind no ordered.");
                    success = false;
                }
            }
            n = null;
            //Search for new inputNeuron
            for (int j = 0; j < i.length; j++){//all progenitors
                if (i[j] < progenitorsMinds.get(j).outputNeurons.size()){
                    n = (Out_Neuron_ALife)progenitorsMinds.get(j).outputNeurons.get(i[j]);
                    break; //outputNeuron found
                }
            }
            //We have new inputNeuron to add or null and we have finished
        }// End while (n != null)
        
        for (Out_Neuron_ALife mn:mind.outputNeurons){ //each outNeuron that have been added
            //just update bias
            //Update bias = u - For moment same values as most midNeurons owners progenitor
            double auxU = 0;
            double num = 0;
            for (Mind_ALife m:progenitorsMinds){ //each progenitor mind
                // si contienen esta out N añadir, si no olvidar
                for(Out_Neuron_ALife on:m.outputNeurons){
                    if (on.action == ((Out_Neuron_ALife)mn).action){
                        auxU += on.u;
                        num += 1;
                        break;
                    }
                }
            }
            if (num == 0) auxU = Mind_ALife.DEFAULT_u;
            else auxU = auxU / num;
            mn.u = auxU; //Update new bias
        } // End for (Neuron_ALife mn:progenitorsMinds.get(indProg).midNeurons)
        //this.inputNeurons done.
        //for test
        int breakpoint = this.outputNeurons.size();
        return success;
    } //End public boolean createOutNeurons(this, progenitorsMinds)

    private boolean areSimilarNeurons(Neuron_ALife n1, Neuron_ALife n2, Mind_ALife m1, Mind_ALife m2){
        //Check
        if (n1 == null || n2 == null || m1 == null || m2 == null) return false;
        if (n1.getClass() != n2.getClass()) return false;
        if (n1 instanceof Input_Neuron_ALife && n1.getClass() == n2.getClass()) return true;
        if (n1 instanceof Out_Neuron_ALife && ((Out_Neuron_ALife)n1).getAction() == ((Out_Neuron_ALife)n2).getAction()) return true;
        if (n1 instanceof Status_Neuron_ALife && 
            m1.statusNeurons.indexOf(n1) < m2.statusNeurons.size() && //Avoid out of indes error
            m1.statusNeurons.indexOf(n1) == m2.statusNeurons.indexOf(n2)) return true;
        if (n1 instanceof Neuron_ALife && 
            m1.midNeurons.indexOf(n1) < m2.midNeurons.size() && //Avoid out of indes error
            m1.midNeurons.indexOf(n1) == m2.midNeurons.indexOf(n2)) return true;
        //Unknown neuron type
        MultiTaskUtil.threadMsg("AreSimilarNeurons: Unknown neuron type"+n1+", "+n2+", "+m1+", "+m2);
        return false;
    }

    /**
     * private boolean updateMixedWeights(Mind_ALife mind, ArrayList <Mind_ALife> progenitorsMinds)
     * 
     * Update the weights of a mind from other minds
     * @param mind Mind_ALife the mind to create the midNeurons
     * @param progenitorsMinds ArrayList <Mind_ALife> the list of progenitors minds
     * @return boolean true if success
     */
    private boolean updateMixedWeights(Mind_ALife mind, ArrayList <Mind_ALife> progenitorsMinds){
        boolean success = true;

        for (Neuron_ALife n: mind.allNeurons){ //All neurons in new mind
            ArrayList <Neuron_ALife> auxInputs = new ArrayList <Neuron_ALife>(); //Auxiliar input list
            ArrayList <Double> auxWeights = new ArrayList <Double>(); //Auxiliar weights list
            if( !(n instanceof Input_Neuron_ALife)){ //Input neurons have no inputs
                //Find similar neurons in progenitors minds Inner by class, Mid and Status by order, out by action
                ArrayList <Neuron_ALife> similarNeurons = new ArrayList <Neuron_ALife>();
                ArrayList <Mind_ALife> ownerMinds = new ArrayList <Mind_ALife>();

                for (Mind_ALife m:progenitorsMinds){ 
                    for (Neuron_ALife n_:m.allNeurons){
                        if (areSimilarNeurons(n,n_,mind,m)){
                            similarNeurons.add(n_);
                            ownerMinds.add(m);
                            break;
                        }
                    }
                } //We have a list of similar neurons to merge weights on n neuron
                ArrayList <Neuron_ALife> newInputsList = new ArrayList <Neuron_ALife>();
                for (Neuron_ALife new_in_n:mind.inputNeurons) newInputsList.add(new_in_n);
                ///
                if (n instanceof Status_Neuron_ALife || n instanceof Out_Neuron_ALife){
                    for (Neuron_ALife new_in_n:mind.midNeurons) newInputsList.add(new_in_n);
                    if ((n instanceof Out_Neuron_ALife)){
                        for (Neuron_ALife new_in_n:mind.statusNeurons) newInputsList.add(new_in_n);
                    }
                } else {//End if (n instanceof Status_Neuron_ALife || n instanceof Out_Neuron_ALife)
                    if ((n instanceof Neuron_ALife)){ //We have not midNeuron class
                        for (Neuron_ALife new_in_n:mind.statusNeurons) newInputsList.add(new_in_n);
                    }
                } // All neurons type added
                //Back feed of statusNeuron situation
                if (n instanceof Status_Neuron_ALife){
                    boolean backFeed = false;
                    for (Mind_ALife m:progenitorsMinds){ 
                        if (backFeed) break;
                        if (m.statusNeurons.size() > 0){
                            Neuron_ALife auxNBusqueda = m.statusNeurons.get(0);
                            for (Neuron_ALife auxMBusquedaInputs:auxNBusqueda.inputs){
                                if (areSimilarNeurons(auxNBusqueda,auxMBusquedaInputs,m,m)){
                                    backFeed = true;
                                    break;
                                }
                            }
                        }
                        //add statusNeuron to inputs or status neurons
                        if (backFeed) {for (Neuron_ALife new_in_n:mind.statusNeurons) newInputsList.add(new_in_n);}
                    } //We have a list of similar neurons to merge weights on n neuron
                }
                //Al necesari inputs for n in newInputsList
                if (newInputsList != null) {
                    for (Neuron_ALife n_:newInputsList){ //n_ are input neurons to n
                        int count = 0;
                        double sum = 0;
                        for (Neuron_ALife n__:similarNeurons){ //n__ similar neurons on other minds
                            for (Neuron_ALife ni:n__.inputs){ //ni are input neurons to n__ 
                                if ( areSimilarNeurons(n_, ni, mind, ni.getMind() ) ){ //similar neurons in inputs 
                                    sum += n__.weights.get(n__.inputs.indexOf(ni));
                                    count += 1;
                                    break;
                                }
                            }                           
                        }
                        if (count == 0) sum = Mind_ALife.DEFAULT_Weight;
                        else sum = sum / count;
                        auxInputs.add(n_);
                        auxWeights.add(sum);
                    } //End for (Neuron_ALife n_:newInputsList)
                    n.inputs = auxInputs;
                    n.weights = auxWeights;
                } // End if (newInputsList != null)
                n.normalizeWeights();
            } // End if( !(n instanceof Input_Neuron_ALife))
            //Next neuron in new mind
        } // End for (Neuron_ALife n: mind.allNeurons) //All neurons in new mind
                
        /*
                    ArrayList <Neuron_ALife> auxNList = new ArrayList <Neuron_ALife>();
                    //AuxList = Neurons in progenitor minds same class as n
                    if (n instanceof Out_Neuron_ALife){
                        for (Out_Neuron_ALife n_O:m.outputNeurons) auxNList.add(n_O);
                    } else if (n instanceof Neuron_ALife){ //No inputNeuron and no OutNeuron -> midNeuron or statusNeuron
                        if (mind.midNeurons.contains(n) && m.midNeurons.contains(n)) //FALLA
                            for (Neuron_ALife n_O:m.midNeurons) auxNList.add(n_O);
                        if (mind.statusNeurons.contains(n) && m.statusNeurons.contains(n)) 
                            for (Neuron_ALife n_O:m.statusNeurons) auxNList.add(n_O);
                    } else { //Any error
                        //for test
                        MultiTaskUtil.threadMsg("Creator of Mind" + this.creature.idCreature + 
                            "Mind_ALife: neuron type not recognized.");
                        success = false;
                    }
                    //Look ins auxList Neurons similar to n, same time and same order to add them to similarNeurons list to mix them
                    if (n instanceof Out_Neuron_ALife){ //2 OutNeurons are similar if they have the same action
                        for(Neuron_ALife n_: auxNList){
                            if (((Out_Neuron_ALife)n).action == ((Out_Neuron_ALife)n_).action){
                                similarNeurons.add(n_);
                                ownerMinds.add(m);
                            }
                        }
                    } else if (n instanceof Neuron_ALife){ //2 midNeurons are similar if they have the same order
                        int index = 0;
                        if (mind.midNeurons.contains(n)){
                            index = mind.midNeurons.indexOf(n); //order out inputNeurons
                        }
                        if (mind.statusNeurons.contains(n)){
                            index = mind.midNeurons.indexOf(n); //order out inputNeurons
                        }
                        if(auxNList.size()> index){  //This way is unclear separation midN and statusN
                            Neuron_ALife n_ = auxNList.get(index);
                            similarNeurons.add(n_);
                            ownerMinds.add(m);
                        }
                    } else {
                        //Any error Unknown neuron type
                        int breakpoint = 1;
                    }
                }// End for (Mind_ALife m:progenitorsMinds)
                // we have a neuron and its similars to n in similarNeurons and its owners in ownerMindsç
                // update weights of n
                //check input size. for test
                if (n.inputs.size() != mind.allNeurons.size() - mind.outputNeurons.size()){
                    MultiTaskUtil.threadMsg("Creator of Mind" + this.creature.idCreature + 
                        "Mind_ALife: neuron inputs size not correct.- Remake inputs.");
                }
                // mix similarNeurons weights on n.weights //2 types input all non outNeurons and input just midNeurons
                if (n.inputs != null && n.inputs.size() > 0 
                    && n.inputs.get(0) instanceof Input_Neuron_ALife)
                {
                    //for(Neuron_ALife n_w: mind.allNeurons){
                    for(Neuron_ALife n_w: this.allNeurons){                        
                        boolean b = n_w instanceof Out_Neuron_ALife;
                        if(!(n_w instanceof Out_Neuron_ALife)){
                            auxInputs.add(n_w);
                        }
                    }
                } else { //Neron that have only midNeurons as inputs - multi layer
                    for(Neuron_ALife n_w: mind.allNeurons){
                        if (!(n_w instanceof Out_Neuron_ALife) || (n_w instanceof Input_Neuron_ALife)){
                            auxInputs.add(n_w);
                        }
                    }

                }
                // auxInputs have now the new input neuron list in neuron inputs are other mind's neurons
                double auxW = 0; //auxiliar weight
                int auxCont = 0; //auxiliar counter
                int[] i = new int[similarNeurons.size()];
                for (int j = 0; j < i.length; j++) i[j] = 0; // Numerical index for each neuron in similarNeurons array
                for (Neuron_ALife n1_w: auxInputs){//all input neurons that need weights depends on neuron type
                    for (int j = 0;j < i.length; j++ ){ //Pass over all similarNeurons
                        if (similarNeurons.get(j).inputs.size() > i[j] && similarNeurons.get(j).weights.size() > i[j]){
                            if (similarNeurons.get(j).inputs.get(i[j]).getClass() == n1_w.getClass()){ 
                                //Diferent intputNeuron types or midNeuron or statusNeuron
                                //Similar inputNeuron type in similarNeurons(j).inputs and n1_w.inputs
                                auxW += similarNeurons.get(j).weights.get(i[j]);
                                auxCont++;
                                i[j]++;
                            } // Valid weight mix
                        }  // End of dimensión condition
                    } // end of j neuron of auxInputs
                    auxW = auxW / auxCont;
                    auxWeights.add(auxW);
                    //check 
                    if (auxWeights.size() != auxInputs.indexOf(n1_w) + 1){
                        int breackpoint = 1;
                        MultiTaskUtil.threadMsg("Creator of Mind" + this.creature.idCreature + 
                            "Mind_ALife: neuron weights and neuron inputs not match.");
                        success = false;
                    }
                } // End for (Neuron_ALife n1_w: auxInputs) //All inputs for this neuron
                
                //InpuNeuron have no weights
                n.inputs = auxInputs;
                n.weights = auxWeights;
            } // if( !(n instanceof Input_Neuron_ALife))
            //n is an inputNeuron--> have no weights or inputs to update
        } // End for (Neuron_ALife n: mind.allNeurons)
        */

        return success;
    } //End public boolean updateMixedWeights(this, progenitorsMinds)


    // Public Methods and Fuctions ==============

    /**
     * public Action run()
     * 
     * Run the mind and return the action
     * @return Action the action to do
     */
    public Action run(){
        //For concurrency Log
        //MultiTaskUtil.threadMsg("Run Cre("+this.creature.getId()+") time["+this.creature.env_ALive.getTime()+"]");
        
        //for test
        if (!checkNeurons()){
            //throw new IllegalArgumentException("No se puede crear el objeto debido a una condición inválida.");
            int breakp =1;
        }        
        //END for test
        lockMind.lock();
        try{
            this.reset();
            this.activation();
            return  this.outNeuron.getAction();
        }finally{
            lockMind.unlock();
        }
    } // End public Action run()
    
    /**
     * public long evaluateMindComlex()
     * 
     * Evaluate the mind complexity of this mind
     * @param none
     * @return long the mind complexity
     */
    public long evaluateMindComlex(){
        // *** FALTA - Aproximación muy rústica
        long CTE_INN  = 10; // Cte de neurona de entrada (1..)
        long CTE_OUT = 15;  // Cte de neurona de salida (2..)
        long CTE_ST = 20;   // Cte de neurona de ESTADO o memoria (0..)
        long CTE_N = 1;     // Cte de neurona de, asigana el valor a todos (3..)
        
        long comp = 0;
        lockMind.lock();
        try{
            comp =  this.inputNeurons.size() * CTE_INN +
                    this.outputNeurons.size() * CTE_OUT +
                    this.statusNeurons.size() * CTE_ST +
                    this.allNeurons.size() * CTE_N;
        }
        finally{
            lockMind.unlock();
        }
        //comp =  this.inputNeurons.size() * CTE_INN +
        //        this.outputNeurons.size() * CTE_OUT +
        //        this.statusNeurons.size() * CTE_ST +
        //        this.allNeurons.size() * CTE_N;
        return comp;
    }// End public long evaluateMindComlex()
    
    /**
     * public void reset()
     * 
     * Reset the mind
     * @param none
     * @return void
     */
    public void reset(){
        lockMind.lock();
        try{
            //Status neurons first to update status values
            for (Neuron_ALife n:statusNeurons){
                n.reset(); 
            }
            for (Neuron_ALife n:allNeurons){
                if (n instanceof Status_Neuron_ALife) continue; //done first
                n.reset();
            }
        } finally{
            lockMind.unlock();
        }
    } // End public void reset()
    
    public double activation(){
        //Reset output and outNeuron
        lockMind.lock();
        try{
            output = Double.valueOf(0);
            outNeuron = null;
            ArrayList<Out_Neuron_ALife> outOptions = new ArrayList<Out_Neuron_ALife>();
            try{
                
                    for (Neuron_ALife n:outputNeurons){
                        if (n.activation() >= output){
                            //Doubs: put the output the max of outputs neurons        
                            //outNeuron = (Out_Neuron_ALife)n;
                            if (n.getOutput() > output) outOptions.clear();
                            output = n.getOutput();
                            outOptions.add((Out_Neuron_ALife)n);
                        }
                    }
            }catch(NullPointerException npe){
                npe.printStackTrace();
            }
            Random r = new Random(); //Some times random option to dont inactivate unused neurons
            //Random decision to avoid never do any action
            // for all possible actions
            //for test
            int breakPoint = r.nextInt(100);
            double breackpointd = 100 * Mind_ALife.RandomActionProbability;
            boolean b = breakPoint < 100 * Mind_ALife.RandomActionProbability;
            //if (r.nextInt(100) < 100 * Mind_ALife.RandomActionProbability){
            if (b){
                outOptions = new ArrayList<Out_Neuron_ALife>();
                for(Action a:Mind_ALife.Action.values()){
                    //synchronized(this.creature){
                    if (!((Active_ALife_Creature)this.creature).getActions().contains(a)) {
                        for (Out_Neuron_ALife oN:this.outputNeurons){
                            if (oN.action == a) outOptions.add(oN);
                        }
                    }//} // End synchronized(this.creature) If
                }
            } // End Random decision. Now outOptions less used actions
            //for test
            if (outOptions.size() == 0){
                outOptions = this.outputNeurons;
            }
            int r1 = ( (r.nextInt(outOptions.size()*100)) - 1 )/ 100;
            outNeuron = (Out_Neuron_ALife) outOptions.get(r1);
        } finally{
            lockMind.unlock();
        }
            // outNeuron = (Out_Neuron_ALife) outOptions.get(r.nextInt(outOptions.size()));//Activate when test out
        return output;
    } // End public double activation()
    
    /**
     * public void updateLearn(Double enhanced, Double change)
     * 
     * Update the mind in function of the enhanced and change
     * @param enhanced
     * @param change
     */
    public void updateLearn(Double enhanced, Double uupdate, Double change){
        // En funcion de enhanced y el peso mejora.
        lockMind.lock();
        try{
            for (Neuron_ALife n:allNeurons){
                n.updateLearn(enhanced, uupdate, change);
            }
        } finally{
            lockMind.unlock();
        }      
    } // End public void updateLearn(Double enhanced, Double change)
    
    /**
     * public void updateMind(Action action, Double statusChange, Double weightOfActionVsTime)
     * 
     * @param action Mind_ALife.Action
     * @param statusChange double
     * @param weightOfActionVsTime double
     * @param uOfActionVsTime double
     * @return void
     */
    public void updateMind(Action action, Double statusChange, Double weightOfActionVsTime, Double uOfActionVsTime){
        double MIND_F_TOLERANCE = 0.1;
        //check
        if (action == null || statusChange == null || weightOfActionVsTime == null){
            //for test
            MultiTaskUtil.threadMsg("Creator of Mind" + this.creature.idCreature + 
                "Mind_ALife: updateMind parameters null.");
            return;
        }

        //Find the neuron that has the action and update it and its inputs
        lockMind.lock();
        try{
            this.activationCont ++;
            this.forecastStatusMean = (this.forecastStatusMean * (this.activationCont -1)+ statusChange) / this.activationCont;
            this.forecastGeneralError = ((forecastGeneralError * (this.activationCont -1)) + Math.abs(statusChange - forecastStatusMean))/activationCont;
            //this.forecastGeneralVariance = this.forecastGeneralVariance;        
            if (statusChange == 0) return;
            Double auxWeightOfActionVsTime = weightOfActionVsTime;
            if(statusChange - forecastStatusMean < (-0.1*forecastStatusMean)){
                auxWeightOfActionVsTime = weightOfActionVsTime * 3;//this.weight_changeUnderFraction;
            }
            for(Out_Neuron_ALife oN:this.outputNeurons){
                if (oN.action == action){
                    oN.updateForecast(statusChange);
                    oN.updateLearn(auxWeightOfActionVsTime, uOfActionVsTime, statusChange);
                    //oN.setU(oN.getU()+oN.getU()*(forecastStatusMean-statusChange);
                    break;
                }
            }
            //actualize mind chage values
            if (Math.abs(this.forecastGeneralError/forecastStatusMean) > MIND_F_TOLERANCE){
                this.u_changeFraction = (1 + MIND_F_TOLERANCE) * this.u_changeFraction;
                this.weight_changeFraction = (1 + MIND_F_TOLERANCE) * this.weight_changeFraction;
                this.weight_changeUnderFraction = (1 + MIND_F_TOLERANCE) * this.weight_changeUnderFraction;
            }else if (Math.abs(this.forecastGeneralError/forecastStatusMean) < MIND_F_TOLERANCE/2){
                this.u_changeFraction = (1 - MIND_F_TOLERANCE) * this.u_changeFraction;
                this.weight_changeFraction = (1 - MIND_F_TOLERANCE) * this.weight_changeFraction;
                this.weight_changeUnderFraction = (1 - MIND_F_TOLERANCE) * this.weight_changeUnderFraction;
            }
        } finally{
            lockMind.unlock();
        }
    } // End public void updateMind(Action action, Double statusChange, Double weightOfActionVsTime)
    
    /**
     * public Mind_ALife dupeMind_ALife(Mind_ALife m)
     * 
     * @param m - the mind to dupe
     * @return - the new mind
     */
    /*
    public static Mind_ALife dupeMind_ALife(Mind_ALife m){
        //FALTA
        Mind_ALife newMind = new Mind_ALife(m);
        return newMind;
    } // End public Mind_ALife dupeMind_ALife(Mind_ALife m)
    */
    public Mind_ALife dupeMind_ALife(){
        lockMind.lock();
        try{
            Mind_ALife newMind = new Mind_ALife(this);
            return newMind;
        } finally{
            lockMind.unlock();
        }
        //Mind_ALife newMind = new Mind_ALife(this);
        //return newMind;
    } // End public Mind_ALife dupeMind_ALife(Mind_ALife m)

    public ArrayList<Input_Neuron_ALife> getAllPosibleInput_Neurons(){
        ArrayList<Input_Neuron_ALife> auxInputNList = new ArrayList<Input_Neuron_ALife>();
        //all posible input neurons
        //Own status detection
        auxInputNList.add(new Detect_Reproductable_Neuron_ALife(creature));
        auxInputNList.add(new Detect_Hungry_Neuron_ALife(creature));
        auxInputNList.add(new Detect_ReproductionGroupFull_Neuron_ALife(creature));

        auxInputNList.add(new Detect_RResource_Neuron_ALife(creature));
        auxInputNList.add(new Detect_GResource_Neuron_ALife(creature));
        auxInputNList.add(new Detect_BResource_Neuron_ALife(creature));
        auxInputNList.add(new Detect_liveTimeFraction_Neuron_ALife(creature));
        auxInputNList.add(new Detect_livePointsFraction_Neuron_ALife(creature));
        auxInputNList.add(new Detect_attackPoints_Neuron_ALife(creature));
        auxInputNList.add(new Detect_defPoints_Neuron_ALife(creature));

        //proximity
        //auxInputNList.add(new ); //nearest neighbour x, -x, y, -y distance
        //auxInputNList.add(new ); //nearest neighbour same specie x, -x, y, -y distance
        //auxInputNList.add(new ); //nearest neighbour enemy x, -x, y, -y distance
        //auxInputNList.add(new );//nearest neighbour family x, -x, y, -y distance
        //auxInputNList.add(new );//nearest neighbour foodspecie x, -x, y, -y distance
        //auxInputNList.add(new );//neighbour count
        //auxInputNList.add(new );//neighbour count same specie
        //auxInputNList.add(new );//neighbour count enemy
        //auxInputNList.add(new );//neighbour count family
        //auxInputNList.add(new );//neighbour count foodspecie
        //Trace detection

        return auxInputNList;
    } // End public static ArrayList<Input_Neuron_ALife> getAllPosibleInput_Neurons()
    

    /**
     * public void mutateMind(int mutatedCar, int[] caracMutated)
     * 
     * Mutate the mind to fix int[] Neuron count restrictions
     * @param mutatedCar - the carac that has been mutated
     * @param caracMutated - the new value of the carac mutated
     */
    public Mind_ALife mutateMind(int mutatedCar, int[] caracMutated){
        ArrayList <Neuron_ALife> auxInputNList = new ArrayList <Neuron_ALife>();
        lockMind.lock();
        try{
            if (mutatedCar == 0 && caracMutated[mutatedCar] != this.inputNeurons.size()){
                if (caracMutated[mutatedCar] >= Active_ALife_Creature.creature_minCaracteristics[mutatedCar+20]
                    && caracMutated[mutatedCar] <= Active_ALife_Creature.creature_maxCaracteristics[mutatedCar+20])
                {
                    //if bigger create input neuron add new random input neuron later remove if smaller
                    ArrayList <Input_Neuron_ALife> auxInputNPossibles = new ArrayList <Input_Neuron_ALife>();
                    auxInputNPossibles = getAllPosibleInput_Neurons();
                    //
                    while (this.inputNeurons.size() < caracMutated[mutatedCar] && auxInputNPossibles.size() > 0){
                        Neuron_ALife auxN = auxInputNPossibles.get((int)( (Math.random()*auxInputNPossibles.size()*100 - 1)/100 ) );
                        boolean valid = true;
                        for (Neuron_ALife n: this.inputNeurons){
                            if (n.getClass() == auxN.getClass()){
                                valid = false;
                            }
                        }
                        if (valid) {
                            auxN = ALife_Input_Neuron_Utils.createSameTypeNeuron(auxN, this.creature);
                            auxN.normalizeWeights();
                            this.addNeuron(auxN);
                        }
                        auxInputNPossibles.remove(auxN); //added or its present
                    }
                    //else remove input neuron uf smaller
                    while (this.inputNeurons.size() > caracMutated[mutatedCar]){
                        this.inputNeurons.remove((int)( (Math.random()*this.inputNeurons.size()*100 - 1)/100 ) ); 
                    }                
                }
            }
            if (mutatedCar == 1 && caracMutated[mutatedCar] != this.midNeurons.size()){
                if (caracMutated[mutatedCar] >= Active_ALife_Creature.creature_minCaracteristics[mutatedCar+20]
                    && caracMutated[mutatedCar] <= Active_ALife_Creature.creature_maxCaracteristics[mutatedCar+20])
                {
                    auxInputNList = new ArrayList <Neuron_ALife>();
                    for (Neuron_ALife n: this.inputNeurons) auxInputNList.add(n);
                    for (Neuron_ALife n: this.statusNeurons) auxInputNList.add(n);
                    //if bigger create input neuron
                    while (this.midNeurons.size() < caracMutated[mutatedCar]){
                        Neuron_ALife auxN = new Neuron_ALife(auxInputNList, this.creature);
                        auxN.normalizeWeights();
                        this.addNeuron(auxN);
                        //add this neuron to rest neurons inputs
                        for (Neuron_ALife n: this.statusNeurons) {
                            n.inputs.add(auxN);
                            //add tp weights de new one with deault value
                            n.weights.add(Mind_ALife.DEFAULT_Weight);
                            n.normalizeWeights();
                        }
                        for (Neuron_ALife n: this.outputNeurons) {
                            n.inputs.add(auxN);
                            //add tp weights de new one with deault value
                            n.weights.add(Mind_ALife.DEFAULT_Weight);
                            n.normalizeWeights();
                        }
                    } // End while (this.midNeurons.size() < caracMutated[mutatedCar])
                    //else remove mid neuron
                    while (this.midNeurons.size() > caracMutated[mutatedCar]){
                        this.midNeurons.remove((int)( (Math.random()*this.midNeurons.size()*100 - 1)/100 ) ); 
                        //weights in rest neurons are not updated !!!! FALTA
                        for (Neuron_ALife nwr: this.allNeurons){
                            for (Neuron_ALife nwr_I: nwr.inputs){
                                if (nwr_I == nwr){ //if (nwr_I.neuron_ID == nwr.neuron_ID){
                                    int i = nwr.inputs.indexOf(nwr_I);
                                    nwr.weights.remove(i);
                                    nwr.inputs.remove(i);
                                }
                            }
                        } // End for (Neuron_ALife nwr: this.allNeurons) REMOVE this neuron from all neurons inputs and weights
                    }                 
                }
            } // End if (mutatedCar == 1 && caracMutated[mutatedCar] != this.midNeurons.size())

            if (mutatedCar == 2 && caracMutated[mutatedCar] != this.outputNeurons.size()){
                if (caracMutated[mutatedCar] >= Active_ALife_Creature.creature_minCaracteristics[mutatedCar+20]
                    && caracMutated[mutatedCar] <= Active_ALife_Creature.creature_maxCaracteristics[mutatedCar+20])
                {
                    auxInputNList = new ArrayList <Neuron_ALife>();
                    for (Neuron_ALife n: this.inputNeurons) auxInputNList.add(n);
                    for (Neuron_ALife n: this.statusNeurons) auxInputNList.add(n);

                    //if bigger create output neuron
                    ArrayList <Out_Neuron_ALife> auxOutputNList = new ArrayList <Out_Neuron_ALife>();
                    //create neuron for all actions
                    for(Action a:Mind_ALife.Action.values()){
                        Out_Neuron_ALife auxN = new Out_Neuron_ALife(auxInputNList,creature,a);
                        auxN.normalizeWeights();
                        auxOutputNList.add(auxN);
                    }                
                    while (this.outputNeurons.size() < caracMutated[mutatedCar] && auxOutputNList.size() > 0){
                        int indix = (int)( (Math.random()*auxOutputNList.size()*100 - 1)/100 );
                        boolean found = false;
                        for (Out_Neuron_ALife n: this.outputNeurons){
                            if (n.getAction() == auxOutputNList.get(indix).getAction()){
                                found = true;
                                break;
                            }
                        }
                        if (!found) this.addNeuron(auxOutputNList.get(indix));
                        auxOutputNList.remove(indix); //found or added
                    }
                    //if there is any type or Neuron that get outneurons output like input add here (status?)
                
                    //else remove input neuron
                    while (this.outputNeurons.size() > caracMutated[mutatedCar]){
                        this.outputNeurons.remove((int)( (Math.random()*this.outputNeurons.size()*100 - 1)/100 ) ); 
                        //if some status can have this input remove it
                        for (Neuron_ALife nwr: this.allNeurons){
                            for (Neuron_ALife nwr_I: nwr.inputs){
                                if (nwr_I == nwr){ //if (nwr_I.neuron_ID == nwr.neuron_ID){
                                    int i = nwr.inputs.indexOf(nwr_I);
                                    nwr.weights.remove(i);
                                    nwr.inputs.remove(i);
                                }
                            }
                        } // End for (Neuron_ALife nwr: this.allNeurons) REMOVE this neuron from all neurons inputs and weights
                    }                 
                }
            } // End if (mutatedCar == 2 && caracMutated[mutatedCar] != this.outputNeurons.size())

            if (mutatedCar == 3 && caracMutated[mutatedCar] != this.statusNeurons.size()){
                if (caracMutated[mutatedCar] > Active_ALife_Creature.creature_minCaracteristics[mutatedCar+20]
                    && caracMutated[mutatedCar] > Active_ALife_Creature.creature_maxCaracteristics[mutatedCar+20])
                {
                    //if bigger create input neuron
                    auxInputNList = new ArrayList <Neuron_ALife>();
                    for (Neuron_ALife n: this.inputNeurons) auxInputNList.add(n);
                    for (Neuron_ALife n: this.midNeurons) auxInputNList.add(n);
                    //for (Neuron_ALife n: this.statusNeurons) auxInputNList.add(n);
                    //if bigger create input neuron
                    while (this.statusNeurons.size() < caracMutated[mutatedCar]){
                        Neuron_ALife auxN = new Neuron_ALife(auxInputNList, this.creature);
                        auxN.normalizeWeights();
                        this.addNeuron(auxN);
                        //add this neuron to rest inputs
                        for (Neuron_ALife n: this.midNeurons) {
                            n.inputs.add(auxN);
                            //add tp weights de new one with deault value
                            n.weights.add(Mind_ALife.DEFAULT_Weight);
                            n.normalizeWeights();
                        }
                        for (Neuron_ALife n: this.outputNeurons) {
                            n.inputs.add(auxN);
                            //add tp weights de new one with deault value
                            n.weights.add(Mind_ALife.DEFAULT_Weight);
                            n.normalizeWeights();
                        }
                    } // End while (this.statusNeurons.size() < caracMutated[mutatedCar])

                    //else remove input neuron
                    while (this.statusNeurons.size() > caracMutated[mutatedCar]){
                        this.statusNeurons.remove((int)( (Math.random()*this.statusNeurons.size()*100 - 1)/100 ) ); 
                        //remove this from other weights and inputs
                        for (Neuron_ALife nwr: this.allNeurons){
                            for (Neuron_ALife nwr_I: nwr.inputs){
                                if (nwr_I == nwr){ //if (nwr_I.neuron_ID == nwr.neuron_ID){
                                    int i = nwr.inputs.indexOf(nwr_I);
                                    nwr.weights.remove(i);
                                    nwr.inputs.remove(i);
                                }
                            }
                        } // End for (Neuron_ALife nwr: this.allNeurons) REMOVE this neuron from all neurons inputs and weights
                    }                  
                } // End if (caracMutated[mutatedCar] > Active_ALife_Creature.creature_minCaracteristics[mutatedCar+20]
            } // End if (mutatedCar == 3 && caracMutated[mutatedCar] != this.statusNeurons.size())
        } finally{
            lockMind.unlock();
        }
        return null;
    } // End public void mutateMind(int mutatedCar, int[] caracMutated)


    // Getter and setters =======================

    /**
     * public Double getOutput()
     * 
     * @return Double the output of the mind
     */
    public Double getOutput(){//?????
        lockMind.lock();
        try{
            return output;
        } finally{
            lockMind.unlock();
        }
        //return null;//output;
    } 
    
    /**
     * public void setCreature(Int_ALife_Creature c)
     * 
     * Set the creature that owns the mind
     * @param c - the creature that owns the mind
     */
    public void setCreature(Int_ALife_Creature c){
        creature = c;
        if (allNeurons == null) 
            return;
        lockMind.lock();
        try{
            for(Neuron_ALife n:allNeurons){
                n.setCreature(c);
                n.setMind(this); //redundant ??
                n.setNeuron_ID (-1); // -1 mark of unset neuron_ID
                this.getNewNeuronID(n);
            }
        } finally{
            lockMind.unlock();
        }
    } //End public void setCreature(Int_ALife_Creatire c)

    // Private Methods and Fuctions =============
    /**
     * private Neuron_ALife assingWeightsToDupeNeuron(Neuron_ALife auxN, Neuron_ALife n, ArrayList <Neuron_ALife> auxInputNList)
     * 
     * Auxial function to assing weights to a dupe neuron in its creation
     * 
     * @param auxN - the new neuron
     * @param n - the original neuron
     * @param auxInputNList - the list of input neurons, taken from new mind not from duped neuron
     * @return Neuron_ALife - AuxN with weights modified
     */
    private Neuron_ALife assingWeightsToDupeNeuron(Neuron_ALife auxN, Neuron_ALife n, ArrayList <Neuron_ALife> auxInputNList){
        lockMind.lock();
        try{//synchronized (auxN){
            ArrayList <Double> auxWeightList = new ArrayList <Double>();
            //We change list on time test it !!!!!!
            for (Neuron_ALife n_AuxIn:auxInputNList){//all neuron in new input lists
                int i = 0; 
                boolean found = false;
                //work with n
                synchronized (n){
                    for (Neuron_ALife n_OriIn:n.inputs){//search in both neurons same id Neuron in input list
                        if (n_OriIn.neuron_ID % 1000 == n_AuxIn.neuron_ID % 1000 && 
                            n_OriIn.getClass() == n_AuxIn.getClass()) //we have old id-s still not updated
                        {
                            found = true;
                            auxN.weights.add(n.weights.get(i));
                        } // End if (n_Inp.neuron_ID == n_.neuron_ID)
                        if (found) break;    
                    } // End for (Neuron_ALife n_Inp:n.inputs)
                } // End synchronized (n)
                if (!found) 
                    auxInputNList.remove(n_AuxIn); //other option it assign a default weight SHOULD NEVER HAPPEND
            } // End for (Neuron_ALife n_:auxWeightList)
            auxN.weights = auxWeightList;
        } // End synchronized (auxN)
        finally{
            lockMind.unlock();
        }        
        return auxN;
    } // End private Neuron_ALife assingWeightsToDupeNeuron(Neuron_ALife n, ArrayList <Neuron_ALife> auxInputNList)

    /**
     * private boolean validateMind() throws IllegalArgumentException
     * 
     * Validate the mind
     * @param  None
     * @return boolean true if success
     */
    private boolean validateMind() throws IllegalArgumentException{
        boolean validMind = true;
        lockMind.lock();
        try{
            if (this.creature == null) return false;//duping a creature
            for (Neuron_ALife n:this.allNeurons){
                if (n.creature == null || n.mind == null || n.neuron_ID < 0) 
                    validMind = false;
                if (n.creature != n.mind.creature ||n.creature.getMind() != n.mind) 
                    validMind = false;
                for (Neuron_ALife n_:n.inputs){//Check minds of inputs neurons
                    if (n_.mind != n.mind || n_.creature != n.creature) 
                        validMind = false;
                }
                //normalize input weights
                n.normalizeWeights();
                //double[] aux = n.weights.stream().mapToDouble(Double::doubleValue).toArray();
                //aux = ALifeCalcUtil.normalizeArrayToTotal_1(aux);
                //ArrayList <Double> auxAL = new ArrayList <Double>();
                //Arrays.stream(aux).forEach(value -> auxAL.add(value));
                //n.weights = auxAL;
            }
            if (!validMind){
                //for test
                MultiTaskUtil.threadMsg("Validate Mind" + this.creature.idCreature + 
                    "Mind not valid.");
                //throw new IllegalArgumentException("Mind not valid.");
            }
        } finally{
            lockMind.unlock();
        }   
        return validMind;
    } // End private boolean validateMind() throws IllegalArgumentException


    /**
     * private void setNeuronsCount()
     * 
     * Set the number of neurons
     * @param  None
     * @return None
     */
    private void setNeuronsCount() throws IllegalArgumentException{
        lockMind.lock();
        try{
            this.innerN = this.inputNeurons.size();
            //for (Neuron_ALife n:this.inputNeurons) innerN++;        
            this.midN = this.midNeurons.size();
            //for (Neuron_ALife n:this.midNeurons) midN++;
            this.outN = this.outputNeurons.size();
            //for (Neuron_ALife n:this.outputNeurons) outN++;
            this.statusN = this.statusNeurons.size();
            //for (Neuron_ALife n:this.statusNeurons) statusN++;
        } finally{
            lockMind.unlock();
        }
    } // End private void setNeuronsCount()
    
    /**
     * public synchronized double getInnerN()
     * 
     * Get the number of inner neurons
     * @param  None
     * @return double the number of inner neurons
     */
    public synchronized double getInnerN(){
        lockMind.lock();
        try{
            return innerN;
        } finally{
            lockMind.unlock();
        }
        //return innerN;
    }
    
    /**
     * public synchronized double getMidN()
     * 
     * Get the number of mid neurons
     * @param  None
     * @return double the number of mid neurons
     */
    public synchronized double getMidN(){
        lockMind.lock();
        try{
            return midN;
        } finally{
            lockMind.unlock();
        }
        //return midN;
    }
    
    /**
     * public synchronized double getOutN()
     * 
     * Get the number of output neurons
     * @param  None
     * @return double the number of output neurons
     */
    public synchronized double getOutN(){
        lockMind.lock();
        try{
            return outN;
        } finally{
            lockMind.unlock();
        }
        //return outN;
    }
    
    /**
     * public synchronized double getStatusN()
     * 
     * Get the number of status neurons
     * @param   None
     * @return  double the number of status neurons
     */
    public synchronized double getStatusN(){
        lockMind.lock();
        try{
            return statusN;
        } finally{
            lockMind.unlock();
        }
        //return statusN;
    }
    /*        
    innerN = p.getMind().getInnerN() / numProg;
            midN = p.getMind().getMidN() / numProg;
            outN = p.getMind().getOutN / numProg;
            statusN = p.getMind().getStatusN / numProg;
            
    */
   
    /**
     * private boolean checkNeurons()
     * 
     * Check if the number of neurons is correct
     * @return boolean true if the number of neurons is correct
     */
    private boolean checkNeurons(){
        // must be finished so neurons counted
        lockMind.lock();
        try{
            if (this.innerN < 1) return false;
            if (this.outN < 1)return false;
            if (this.allNeurons.size() != (innerN + midN + statusN + outN)) return false;
            //We can test integrity of all neuron weights.
        }finally{
            lockMind.unlock();
        }
        return true;
    } // End private boolean checkNeurons()

    /**
     * public long getNewNeuronID(Neuron_ALife n)
     * 
     * Get a new neuron ID for n and set it
     * @param n Neuron_ALife the neuron to set the ID
     * @return long the new neuron ID
     */
    public long getNewNeuronID(Neuron_ALife n)throws IllegalArgumentException{
        lockMind.lock();
        long id = -1; // creature_ID + type(1 = inn, 2 = mid, 3 = status, 4 = out) + neuron_number (<1000)
        try{
            if (n == null || this.creature == null) return id;
            if (!this.allNeurons.contains(n) && n.getNeuron_ID() != -1) return id;
            id = this.creature.getIdCreature() * 10;
            if (n instanceof Input_Neuron_ALife) id += 1;
            else if (n instanceof Status_Neuron_ALife) id += 3;
            else if (n instanceof Out_Neuron_ALife) id += 4;
            else if (n instanceof Neuron_ALife) id += 2; //Rest of neurons
            else throw new IllegalArgumentException("Mind_ALife: getNewNeuronID(Neuron_ALife n) Neuron_ALife n is unknown Neuron_ALife");
            id = id * 1000;
            id += this.allNeurons.indexOf(n);
            n.setNeuron_ID(id);
        } finally{
            lockMind.unlock();
        }
        return id;
    } // End public long getNewNeuronID(Neuron_ALife n)

    /**
     * public void addNeuron(Neuron_ALife n)
     * 
     * Add a neuron to the mind and set its ID. It's inputs should be added first
     * @param n Neuron_ALife the neuron to add
     * @return None
     */
    public void addNeuron(Neuron_ALife n) throws IllegalArgumentException{
        //check 
        if (n == null) return;
        if (this.allNeurons == null) this.allNeurons = new ArrayList <Neuron_ALife>();
        if (this.inputNeurons == null) this.allNeurons = new ArrayList <Neuron_ALife>();
        if (this.midNeurons == null) this.allNeurons = new ArrayList <Neuron_ALife>();
        if (this.outputNeurons == null) this.allNeurons = new ArrayList <Neuron_ALife>();
        if (this.statusNeurons == null) this.allNeurons = new ArrayList <Neuron_ALife>();
        if(this.allNeurons.contains(n)) return; //Already added
        
        lockMind.lock();
        try{
            this.allNeurons.add(n);
            this.getNewNeuronID(n);
            n.setMind(this);
            n.setCreature(this.creature);
            if (n instanceof Out_Neuron_ALife) this.outputNeurons.add((Out_Neuron_ALife)n);
            else if (n instanceof Input_Neuron_ALife) this.inputNeurons.add(n);
            else if (n instanceof Status_Neuron_ALife) this.statusNeurons.add(n);
            else if (n instanceof Neuron_ALife) this.midNeurons.add(n); //last option
            else 
                throw new IllegalArgumentException("Mind_ALife: addNeuron(Neuron_ALife n) Neuron_ALife n is unknown Neuron_ALife");
        } finally{
            lockMind.unlock();
        }
    } // End public void addNeuron(Neuron_ALife n)

    public ArrayList<String> makeMindReport(ArrayList<String> rep){
        makeMindAtribReport(rep);
        for (Neuron_ALife n:this.allNeurons){
            rep.add("Nw");
            n.makeNeuronReport(rep);
        }
        return rep;
    } // End public ArrayList<String> makeMindReport(ArrayList<String> rep)

    public ArrayList<String> makeMindSORTReport(ArrayList<String> rep){
        rep.add("Nw");
        for (Neuron_ALife n:this.allNeurons){
            n.makeNeuronSORTReport(rep);
        }
        return rep;
    } // End public ArrayList<String> makeMindReport(ArrayList<String> rep)

    /**
     * public ArrayList<String> makeMindAtribReport(ArrayList<String> rep)
     * 
     * Make a report of the mind global attributes
     * @param rep - the report
     * @return ArrayList<String> the report
     */
    public ArrayList<String> makeMindAtribReport(ArrayList<String> rep){
        DecimalFormat df = new DecimalFormat("0.############");
        lockMind.lock();
        try{
            rep.add("" + df.format(this.u_changeFraction));
            rep.add("" + df.format(this.weight_changeFraction));
            rep.add("" + df.format(this.weight_changeUnderFraction));
            rep.add("" + df.format(this.forecastStatusMean));
            rep.add("" + df.format(this.forecastGeneralError));
            rep.add("" + df.format(this.forecastGeneralVariance));
            //String numeroFormateado = df.format(numero);
            rep.add("" + df.format(this.output.doubleValue()) );
        } finally{
            lockMind.unlock();
        }
        return rep;
    } // End public ArrayList<String> makeMindAtribReport(ArrayList<String> rep)


    // Save Load Merge Mutate
    
    public static Mind_ALife test1Mind(Int_ALife_Creature c){
        //int innerN = 1;ALL
        int midN = 1;
        int stN = 1;
        //int outN = 1;

        Mind_ALife m = new Mind_ALife(c);
        m.allNeurons = new ArrayList<Neuron_ALife>();
        m.inputNeurons = new ArrayList<Neuron_ALife>();
        m.midNeurons = new ArrayList<Neuron_ALife>();
        m.outputNeurons = new ArrayList<Out_Neuron_ALife>();
        m.statusNeurons = new ArrayList<Neuron_ALife>();

        m.output = null;
        m.outNeuron = null;
        //Input neurons
        ArrayList <Input_Neuron_ALife> auxInputNList = new ArrayList <Input_Neuron_ALife>();
        //ALife_Input_Neuron_Utils.createSameTypeNeuron(new Detect_Reproductable_Neuron_ALife(c), c);
        //all posible input neurons
        if(m.inputNeurons.isEmpty()){
            auxInputNList = m.getAllPosibleInput_Neurons();
            for(Input_Neuron_ALife iN:auxInputNList){
                m.addNeuron(iN);
            }

            //Mid neurons
            for (int i = 0; i < midN; i++){
                Neuron_ALife n = new Neuron_ALife(m.inputNeurons, c);
                n.normalizeWeights();
                m.addNeuron(n);
            }
        }
        //status neurons
        ArrayList<Neuron_ALife> auxNList = new ArrayList <Neuron_ALife>();
        for (Neuron_ALife n:m.inputNeurons) auxNList.add(n);
        for (Neuron_ALife n:m.midNeurons) auxNList.add(n);
        for (int i = 0; i < stN; i++){
            Status_Neuron_ALife n = new Status_Neuron_ALife(auxNList, c);
            n.normalizeWeights();
            m.addNeuron(n);
        }

        //output neurons
        //public static enum Action {UP,DOWN,RIGHT,LEFT,EAT,REPRODUCE,ATTACK,ADDREPGROUP,MAKETRACE};
        //Mind_ALife.Action[] act = {Action.EAT,Action.REPRODUCE,Action.ATTACK,Action.ADDREPGROUP,Action.MAKETRACE};
        Mind_ALife.Action[] act = {Action.EAT,Action.REPRODUCE,Action.ADDREPGROUP};
        for (Neuron_ALife n:m.statusNeurons) auxNList.add(n);
        for(Action a:act){
            Neuron_ALife auxN = new Out_Neuron_ALife(auxNList,c,a);
            auxN.normalizeWeights();
            m.addNeuron(auxN);
        }
        //Some ajusts
        m.setNeuronsCount();
        if (!m.validateMind()) {
            int breakpoint = 1;
        }
        if (!m.checkNeurons()){
            int breakp = 1;
        }
        
        return m;
    } // End public static Mind_ALife test1Mind(Int_ALife_Creature c)

    public static Mind_ALife test2Mind(Int_ALife_Creature c){
        //int innerN = 1;ALL
        int midN = 1;
        int stN = 1;
        //int outN = 1;

        Mind_ALife m = new Mind_ALife(c);
        m.allNeurons = new ArrayList<Neuron_ALife>();
        m.inputNeurons = new ArrayList<Neuron_ALife>();
        m.midNeurons = new ArrayList<Neuron_ALife>();
        m.outputNeurons = new ArrayList<Out_Neuron_ALife>();
        m.statusNeurons = new ArrayList<Neuron_ALife>();

        m.output = null;
        m.outNeuron = null;
        //Input neurons
        ArrayList <Input_Neuron_ALife> auxInputNList = new ArrayList <Input_Neuron_ALife>();
        //ALife_Input_Neuron_Utils.createSameTypeNeuron(new Detect_Reproductable_Neuron_ALife(c), c);
        //all posible input neurons
        if(m.inputNeurons.isEmpty()){
            auxInputNList = m.getAllPosibleInput_Neurons();
            for(Input_Neuron_ALife iN:auxInputNList){
                m.addNeuron(iN);
            }

            //Mid neurons
            for (int i = 0; i < midN; i++){
                Neuron_ALife n = new Neuron_ALife(m.inputNeurons, c);
                n.normalizeWeights();
                m.addNeuron(n);
            }
        }
        //status neurons
        ArrayList<Neuron_ALife> auxNList = new ArrayList <Neuron_ALife>();
        for (Neuron_ALife n:m.inputNeurons) auxNList.add(n);
        for (Neuron_ALife n:m.midNeurons) auxNList.add(n);
        for (int i = 0; i < stN; i++){
            Status_Neuron_ALife n = new Status_Neuron_ALife(auxNList, c);
            n.normalizeWeights();
            m.addNeuron(n);
        }

        //output neurons
        //public static enum Action {UP,DOWN,RIGHT,LEFT,EAT,REPRODUCE,ATTACK,ADDREPGROUP,MAKETRACE};
        //Mind_ALife.Action[] act = {Action.EAT,Action.REPRODUCE,Action.ATTACK,Action.ADDREPGROUP,Action.MAKETRACE};
        double sumU = 0; //sum of all u or bias of all output neurons
        Mind_ALife.Action[] act = {Action.EAT,Action.REPRODUCE,Action.ADDREPGROUP};
        for (Neuron_ALife n:m.statusNeurons) auxNList.add(n);//have all inputs and mids previusly
        for(Action a:act){
            Neuron_ALife auxN = new Out_Neuron_ALife(auxNList,c,a);
            if (a == Action.REPRODUCE) {
                int auxWind = 0;
                for (Neuron_ALife n_:auxN.getInputs()){
                    if (n_ instanceof Detect_Reproductable_Neuron_ALife){
                        auxN.weights.set(auxN.getInputs().indexOf(n_), (double)10*Mind_ALife.DEFAULT_Weight);
                    }
                }
                //auxN.setU(Mind_ALife.DEFAULT_u*1.2);
            }
            if (a == Action.EAT) {
                int auxWind = 0;
                for (Neuron_ALife n_:auxN.getInputs()){
                    if (n_ instanceof Detect_Reproductable_Neuron_ALife){
                        auxN.weights.set(auxN.getInputs().indexOf(n_), (double)0.1*Mind_ALife.DEFAULT_Weight);
                    }
                    if (n_ instanceof Detect_RResource_Neuron_ALife ||
                        n_ instanceof Detect_BResource_Neuron_ALife ||
                        n_ instanceof Detect_GResource_Neuron_ALife) {
                        auxN.weights.set(auxN.getInputs().indexOf(n_), (double)1.3*Mind_ALife.DEFAULT_Weight);
                    }
                }
                //auxN.setU(Mind_ALife.DEFAULT_u*1.1);
            }
            auxN.setU(0.01);
            auxN.normalizeWeights();
            m.addNeuron(auxN);
            sumU += auxN.getU();
        }
        //u ajusts
        for (Out_Neuron_ALife oN:m.outputNeurons){
            //for test
            double auxU = oN.getU();
            auxU = auxU/sumU;
            oN.setU(auxU);
            //oN.setU(oN.getU()/sumU);
        } // End for (Out_Neuron_ALife oN:m.outputNeurons) U adjust

        //Some ajusts
        m.setNeuronsCount();
        if (!m.validateMind()) {
            int breakpoint = 1;
        }
        if (!m.checkNeurons()){
            int breakp = 1;
        }
        
        return m;
    } // End public static Mind_ALife test2Mind(Int_ALife_Creature c)
    

} // End Class
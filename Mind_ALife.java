import java.util.*;

import org.xml.sax.InputSource;

/**
 * Write a description of class Mind_ALife here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
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
    public static final double DEFAULT_u = 0.5;
    public static final double DEFAULT_Weight = 0.2;
    public static final double TRUE_in_double = 1;
    public static final double FALSE_in_double = 0;
    public static enum Action {UP,DOWN,RIGHT,LEFT,EAT,REPRODUCE,ATTACK};//add to reproductionGroup
    
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
    public Mind_ALife(Int_ALife_Creature creature){
        setCreature(creature);
        this.output = null;
        this.outNeuron = null;
        //Add inputNeurons
        allNeurons = new ArrayList <Neuron_ALife>();
        inputNeurons = new ArrayList <Neuron_ALife>();
        outputNeurons = new ArrayList <Out_Neuron_ALife>();
        statusNeurons = new ArrayList <Neuron_ALife>();
        midNeurons = new ArrayList <Neuron_ALife>();
        
        Neuron_ALife auxN = new Reproductable_Neuron_ALife(this.creature);
        
        //allNeurons.add(auxN);
        //inputNeurons.add(auxN);
        this.addNeuron(auxN);
        //Add status or memory neurons 
        //Add MidNeurons
        //add output Neurons
        //public Out_Neuron_ALife(ArrayList <Neuron_ALife> ns, Creature c, Mind_ALife.Action action)
        auxN = new Out_Neuron_ALife(this.inputNeurons,this.creature, Action.EAT);
        //faltan inpits y pesos
        
        allNeurons.add(auxN);
        outputNeurons.add((Out_Neuron_ALife)auxN);
        auxN = new Out_Neuron_ALife(this.inputNeurons,this.creature, Action.REPRODUCE);
        allNeurons.add(auxN);
        outputNeurons.add((Out_Neuron_ALife)auxN);
        this.output = null;
        setNeuronsCount();
        if (!checkNeurons()){
            throw new IllegalArgumentException("No se puede crear el objeto debido a una condición inválida.");
        }
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
    public Mind_ALife(ArrayList<Int_ALife_Creature>  progenitors, Int_ALife_Creature creature, boolean mutate){
        //Inners Neuron
        // Si la tienen los dos la tiene, si la tiene 1 50% si no la tiene 0% + mutate
        setCreature(creature);
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

        /*
        int[] i = new int[progenitorsMinds.size()];
        for (int j = 0; j < i.length; j++) i[j] = 0; // Numerical index for each progenitor mind array
        Input_Neuron_ALife n = (Input_Neuron_ALife)progenitorsMinds.get(0).inputNeurons.get(0);
        while (n != null){
            for (int j = 0; j < progenitorsMinds.size(); j++){ //each progenitor mind
                Mind_ALife mj= progenitorsMinds.get(j);//for test
                Input_Neuron_ALife nj= (Input_Neuron_ALife)(progenitorsMinds.get(j)).inputNeurons.get(i[j]);
                if ( !Input_Neuron_ALife.isBiggerThan(n,nj)){ //n <= nj
                    n = nj;
                }
            }
            //Add n to this mind
            Input_Neuron_ALife auxN = n.dupeNeuron_ALife(n);
            this.inputNeurons.add(auxN); // test if dupe make a correct copy
            this.allNeurons.add(auxN);
            //update index.
            for (int j = 0; j < progenitorsMinds.size(); j++){ //each progenitor mind
                Mind_ALife mj= progenitorsMinds.get(j); //for text
                Input_Neuron_ALife nj= (Input_Neuron_ALife)(progenitorsMinds.get(j)).inputNeurons.get(i[j]);
                //for test
                MultiTaskUtil.threadMsg(n.getClass()+" vs "+nj.getClass());
                if (n.getClass() == nj.getClass()) i[j]++; //if same inputNeuron type increase index
                else if (progenitorsMinds.contains(n)) {
                    // progenitor mind no ordered
                    MultiTaskUtil.threadMsg("Creator of Mind" + this.creature.idCreature + 
                        "Mind_ALife: progenitor mind no ordered.");
                }
            }
            n = null;
            //Search for new inputNeuron
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
        */

        //Mid Neuron
        createMidNeurons(this, progenitorsMinds);
        
        //Status Neuron

        //out Neuron
        createOutNeurons(this, progenitorsMinds);

        //update weights and bias (u)
        this.updateMixedWeights(this, progenitorsMinds);
        /*
        int auxMidNCount = 0;
        int indProg = 0;
        for (Mind_ALife m:progenitorsMinds){ //each progenitor mind
            if (m.midNeurons.size() > auxMidNCount){ 
                auxMidNCount = m.midNeurons.size();
                indProg = progenitorsMinds.indexOf(m);
            }
        }
        for (Neuron_ALife mn:progenitorsMinds.get(indProg).midNeurons){ //each midNeuron of the progenitor with more midNeurons
            //Add midNeuron
            Neuron_ALife auxN = mn.dupeNeuron_ALife(mn); //dupe MidNeuron of the progenitor with more midNeurons
            //Update bias = u
            double auxU = 0;
            double num = 0;
            for (Mind_ALife m:progenitorsMinds){ //each progenitor mind
                if (m.midNeurons.size() > auxMidNCount + 1){ 
                    auxU += m.midNeurons.get(auxMidNCount).u;
                    num += 1;
                }
            }
            auxU = auxU / num;
            auxN.u = auxU;
            */

            //Update weights
            /*
            for (Neuron_ALife n_: this.inputNeurons){
                //in is a int value of progenitorMinds midNeuron index
                int in = progenitorsMinds.get(indProg).inputNeurons.indexOf(n_);
                double w = 0;
                num = 0;
                for (Mind_ALife m:progenitorsMinds){ //each progenitor mind
                    if (m.midNeurons.size() > in) {
                        w += m.midNeurons.get(in).weights.get(m.inputNeurons.size()+m.midNeurons.indexOf(n_));
                        num = num + 1;
                    }
                }
                w = w / num;
                auxN.weights.set(this.inputNeurons.size()+in, w); //new weight is the avg of progenitors weights
            }

            this.midNeurons.add(auxN); // test if dupe make a correct copy
            this.allNeurons.add(auxN);
            
        }
        */


        /*NO ME GUASTA BUSCO MEJOR OPCION
        innerN = 0;
        midN = 0;
        outN = 0;
        statusN = 0;
        //Inner
        Random random = new Random();
        int numProg = progenitors.size();
        for (Int_ALife_Creature p:progenitors){ //each progenitor
            for (Neuron_ALife n:p.getMind().inputNeurons){//each inputneuron in each progenitor
                int i = 1;
                for (Int_ALife_Creature p_:progenitors){
                    if (p != p_){//for each other progenitors
                        for (Neuron_ALife n_:p.getMind().inputNeurons){//all other progenitors inner neurons
                            if (n.getClass() == n_.getClass()) i++;
                        }
                    }
                }
                
                int r =random.nextInt((numProg+1)*100)/100;
                if ( i >= r ){//r.nextInt(200)/100
                //if ( i > random.nextInt((numProg+1)*100)/100 ){//r.nextInt(200)/100
                    //Add Inner Neuron
                    boolean added = false;
                    for (Neuron_ALife ñ: this.inputNeurons) {
                        if (ñ == n) added = true;
                    }
                    if (!added){
                        //this.inputNeurons.add(n.dupeNeuron_ALife(n)); //n.dupe()
                        Neuron_ALife auxN = Neuron_ALife.dupeNeuron_ALife(n);
                        this.inputNeurons.add(auxN); //n.dupe()
                        this.allNeurons.add(auxN); //n.dupe()
                    }
                }else{ // Will not be added
                    //ensure that is not unique progenitor
                    if (progenitors.size() == 1){
                        int breakp =1;
                    }
                }
                
            } // End for (Neuron_ALife n:p.getMind().inputNeurons)
            //for later use avg of progenitors neurons types count
            innerN = p.getMind().getInnerN() / numProg;
            midN = p.getMind().getMidN() / numProg;
            outN = p.getMind().getOutN() / numProg;
            statusN = p.getMind().getStatusN() / numProg;
        } // progenitors inner neurons tested to be added.
        //mutate 
        
        //Mid Neuron
        int i_ForTest = (int) midN +random.nextInt(3)-1;
        //Numero entre el min y el max de sus progenitores pesos  media de lo que la tienen si no 0
        //Mid_StatusNeuron s
        
        //out Neuron ---------------------------------------------------------------------
        for (Int_ALife_Creature p:progenitors){ //each progenitor
            for (Out_Neuron_ALife n:p.getMind().outputNeurons){//each outputneuron in each progenitor
                int i = 1;
                for (Int_ALife_Creature p_:progenitors){
                    if (p != p_){//for each other progenitors
                        for (Out_Neuron_ALife n_:p.getMind().outputNeurons){//all other progenitors out neurons
                            if (n.action == n_.action) i++;
                        }
                    }
                }
                //for test
                int ri = random.nextInt((numProg+1)*100)/100;;//random.nextInt(numProg);
                if (i >= ri){
                //if (i > random.nextInt(numProg)){
                    //Add Out Neuron
                    boolean added = false;
                    for (Out_Neuron_ALife ñ: this.outputNeurons) {
                        if (ñ.action == n.action) added = true;
                    }
                    if (!added){
//                        //Faltan pesos
                        this.outputNeurons.add(n);
                        this.allNeurons.add(n);
                    }
                }
            }
        }
        */
        //--------------------------------------------------------------------
        // Si la tienen los dos la tiene, si la tiene 1 50% si no la tiene 0% + mutate
        //Numero entre el min y el max de sus progenitores pesos  media de lo que la tienen si no 0
        
        
        //Finalmente poda por complexity
        setNeuronsCount();
        if (!checkNeurons()){
            throw new IllegalArgumentException("No se puede crear el objeto debido a una condición inválida.");
        }        
        //Reevaluar complexity
        
    } // End public Mind_ALife(Int_ALife_Creature[] progenitors, Int_ALife_Creature creature, boolean mutate)
    
    /**
     * public Mind_ALife(Mind_ALife m)
     * 
     * Copy constructor
     * @param m Mind_ALife the mind to copy
     * @return Mind_ALife the new mind
     */
    public Mind_ALife(Mind_ALife m){
        this.creature = m.creature; //??
        this.outNeuron = m.outNeuron.dupeNeuron_ALife(m.outNeuron); //?? //this Neuron is from other Mind
        this.output = m.output; //??

        this.allNeurons = new ArrayList <Neuron_ALife>();
        this.inputNeurons = new ArrayList <Neuron_ALife>();
        this.midNeurons = new ArrayList <Neuron_ALife>();
        this.outputNeurons = new ArrayList <Out_Neuron_ALife>();
        this.statusNeurons = new ArrayList <Neuron_ALife>();
        this.creature = m.creature;
        
        for (Neuron_ALife n:m.inputNeurons){
            //Neuron_ALife auxN = n.dupeInputNeuron();dupeNeuron_ALife(
            Neuron_ALife auxN = Neuron_ALife.dupeNeuron_ALife(n);
            this.inputNeurons.add(auxN);
            this.allNeurons.add(auxN);
        }
        for (Neuron_ALife n:m.midNeurons){
            //Neuron_ALife auxN = n.dupeMidNeuron();
            Neuron_ALife auxN = Neuron_ALife.dupeNeuron_ALife(n);
            this.midNeurons.add(auxN);
            this.allNeurons.add(auxN);
        }
        /*
        for (Neuron_ALife n:m.statusNeurons){
            Neuron_ALife auxN = null;
            if (n == m.outNeuron) auxN = this.outNeuron;
            else auxN = n.dupeStatusNeuron();
            this.statusNeurons.add(auxN);
            this.allNeurons.add(auxN);
        }
        */
        for (Out_Neuron_ALife n:m.outputNeurons){
            Out_Neuron_ALife auxN = Out_Neuron_ALife.dupeNeuron_ALife(n);
            this.outputNeurons.add((Out_Neuron_ALife)auxN);
            this.allNeurons.add(auxN);
        }

        setNeuronsCount();
        if (!checkNeurons()){
            throw new IllegalArgumentException("No se puede crear el objeto debido a una condición inválida.");
        }

    } // End public Mind_ALife(this.creature)

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
                    n = nj;
                }
            }
            // Add n to this mind
            Input_Neuron_ALife auxN = n.dupeNeuron_ALife(n);
            m.inputNeurons.add(auxN); 
            m.allNeurons.add(auxN);
            
            for (int j = 0; j < progenitorsMinds.size(); j++){ //each progenitor mind
                Mind_ALife mj= progenitorsMinds.get(j); //for text
                Input_Neuron_ALife nj= (Input_Neuron_ALife)(progenitorsMinds.get(j)).inputNeurons.get(i[j]);
                //for test
                MultiTaskUtil.threadMsg(n.getClass()+" vs "+nj.getClass());
                if (n.getClass() == nj.getClass()) i[j]++; //if same inputNeuron type increase index
                else if (progenitorsMinds.get(j).allNeurons.contains(n)) {
                    // progenitor mind no ordered
                    MultiTaskUtil.threadMsg("Creator of Mind" + this.creature.idCreature + 
                        "Mind_ALife: progenitor mind no ordered.");
                    success = false;
                }
            }
            n = null;
            //Search for new inputNeuron
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
            Neuron_ALife auxN = mn.dupeNeuron_ALife(mn); //dupe MidNeuron of the progenitor with more midNeurons
            //Update bias = u - For moment same values as most midNeurons owners progenitor
            double auxU = 0;
            double num = 0;
            for (Mind_ALife m:progenitorsMinds){ //each progenitor mind
                if (m.midNeurons.size() > auxMidNCount + 1){ 
                    auxU += m.midNeurons.get(auxMidNCount).u;
                    num += 1;
                }
            }
            auxU = auxU / num;
            auxN.u = auxU;
            mind.midNeurons.add(auxN);
            mind.allNeurons.add(auxN);
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
        // FALTA

        //  ******************************


        return true;
    } //End public boolean createMidNeurons(this, progenitorsMinds)

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
                Mind_ALife mj= progenitorsMinds.get(j);//for test
                Out_Neuron_ALife nj= (Out_Neuron_ALife)(progenitorsMinds.get(j)).outputNeurons.get(i[j]);//i index of j progenitor
                if ( !Out_Neuron_ALife.isBiggerThan(n,nj)){ //n <= nj
                    n = nj;
                }
            }
            //Add n to this mind
            Out_Neuron_ALife auxN = n.dupeNeuron_ALife(n);
            mind.outputNeurons.add(auxN); // test if dupe make a correct copy
            mind.allNeurons.add(auxN);
            //update index.
            for (int j = 0; j < progenitorsMinds.size(); j++){ //each progenitor mind
                Mind_ALife mj= progenitorsMinds.get(j); //for text
                Out_Neuron_ALife nj= (Out_Neuron_ALife)(progenitorsMinds.get(j)).outputNeurons.get(i[j]);
                //for test
                MultiTaskUtil.threadMsg(n.getClass()+" vs "+nj.getClass());
                if (n.getClass() == nj.getClass()) i[j]++; //if same inputNeuron type increase index
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
            auxU = auxU / num;
            mn.u = auxU; //Update new bias
        } // End for (Neuron_ALife mn:progenitorsMinds.get(indProg).midNeurons)
        //this.inputNeurons done.
        //for test
        int breakpoint = this.outputNeurons.size();
        return success;
    } //End public boolean createOutNeurons(this, progenitorsMinds)

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

        for (Neuron_ALife n: mind.allNeurons){
            ArrayList <Neuron_ALife> auxInputs = new ArrayList <Neuron_ALife>();
            ArrayList <Double> auxWeights = new ArrayList <Double>();
            if( !(n instanceof Input_Neuron_ALife)){
                //buscar en cada progenitor la misma neuronas y hacer la media de los pesos respecto a entradas similares
                //out nerurons se identifican con la accion y midNeurons por ordinal
                ArrayList <Neuron_ALife> similarNeurons = new ArrayList <Neuron_ALife>();
                ArrayList <Mind_ALife> ownerMinds = new ArrayList <Mind_ALife>();
                for (Mind_ALife m:progenitorsMinds){ 
                    ArrayList <Neuron_ALife> auxNList = new ArrayList <Neuron_ALife>();
                    //AuxList = Neurons in progenitor minds same class as n
                    if (n instanceof Out_Neuron_ALife){
                        for (Out_Neuron_ALife n_O:m.outputNeurons) auxNList.add(n_O);
                    } else if (n instanceof Neuron_ALife){ //No inputNeuron and no OutNeuron -> midNeuron or statusNeuron
                        if (mind.midNeurons.contains(n) && m.midNeurons.contains(n)) 
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
                    }
                }// End for (Mind_ALife m:progenitorsMinds)
                // we have a neuron and its similars to n in similarNeurons and its owners in ownerMindsç
                // update weights
                //check input size. for test
                if (n.inputs.size() != mind.allNeurons.size() - mind.inputNeurons.size()){
                    MultiTaskUtil.threadMsg("Creator of Mind" + this.creature.idCreature + 
                        "Mind_ALife: neuron inputs size not correct.- Remake inputs.");
                }
                // mix similarNeurons weights on n.weights
                if (n.inputs != null && n.inputs.size() > 0 && n.inputs.get(0) instanceof Input_Neuron_ALife){
                    for(Neuron_ALife n_w: mind.allNeurons){
                        boolean b = n_w instanceof Out_Neuron_ALife;
                        if(!(n_w instanceof Out_Neuron_ALife)){
                            auxInputs.add(n_w);
                        }
                    }
                } else {
                    for(Neuron_ALife n_w: mind.allNeurons){
                        if (!(n_w instanceof Out_Neuron_ALife) || (n_w instanceof Input_Neuron_ALife)){
                            auxInputs.add(n_w);
                        }

                }
                // auxInputs have now the new input neuron list
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
                } // End if( !(n instanceof Input_Neuron_ALife))
                //InpuNeuron have no weights
            } // if( !(n instanceof Input_Neuron_ALife))
        } // End for (Neuron_ALife n: mind.allNeurons)
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
        
        
        this.reset();
        this.activation();
        return  this.outNeuron.getAction();
    }
    
    public long evaluateMindComlex(){
        // *** FALTA - Aproximación muy rústica
        long CTE_INN  = 10; // Cte de neurona de entrada (1..)
        long CTE_OUT = 15;  // Cte de neurona de salida (2..)
        long CTE_ST = 20;   // Cte de neurona de ESTADO o memoria (0..)
        long CTE_N = 1;     // Cte de neurona de, asigana el valor a todos (3..)
        
        long comp = 0;
        comp =  this.inputNeurons.size() * CTE_INN +
                this.outputNeurons.size() * CTE_OUT +
                this.statusNeurons.size() * CTE_ST +
                this.allNeurons.size() * CTE_N;
        return comp;
    }// End public long evaluateMindComlex()
    
    public void reset(){
        synchronized (this){
            for (Neuron_ALife n:allNeurons){
                n.reset();
            }
        }
    }
    
    public double activation(){
        //Max o sumatorio?? FAlta calcularlo
        output = Double.valueOf(0);
        outNeuron = null;
        ArrayList<Out_Neuron_ALife> outOptions = new ArrayList<Out_Neuron_ALife>();
        try{
            synchronized (this){
                for (Neuron_ALife n:outputNeurons){
                    if (n.activation() >= output){
                        //Doubs: put the output the max of outputs neurons        
                        //outNeuron = (Out_Neuron_ALife)n;
                        output = n.getOutput();
                        outOptions.add((Out_Neuron_ALife)n);
                    }
                }
            }
        }catch(NullPointerException npe){
            npe.printStackTrace();
        }
        Random r = new Random();
        //for test
        int r1 = r.nextInt(outOptions.size());
        outNeuron = (Out_Neuron_ALife) outOptions.get(r1);
        
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
    public void updateLearn(Double enhanced, Double change){
        // En funcion de enhanced y el peso mejora.
        synchronized (this){
            for (Neuron_ALife n:allNeurons){
                n.updateLearn(enhanced, change);
            }
        }        
    } // End public void updateLearn(Double enhanced, Double change)
    
    /**
     * public void updateMind(Action action, Double statusChange, Double weightOfActionVsTime)
     * 
     * @param action Mind_ALife.Action
     * @param statusChange double
     * @param weightOfActionVsTime double
     */
    public void updateMind(Action action, Double statusChange, Double weightOfActionVsTime){
        //Find the neuron that has the action and update it and its inputs
        
    } // End public void updateMind(Action action, Double statusChange, Double weightOfActionVsTime)
    
    /**
     * public Mind_ALife dupeMind_ALife(Mind_ALife m)
     * 
     * @param m - the mind to dupe
     * @return - the new mind
     */
    public static Mind_ALife dupeMind_ALife(Mind_ALife m){
        //FALTA
        Mind_ALife newMind = new Mind_ALife(m);
        return newMind;
    } // End public Mind_ALife dupeMind_ALife(Mind_ALife m)

    // Getter and setters

    /**
     * public Double getOutput()
     * 
     * @return Double the output of the mind
     */
    public Double getOutput(){//?????
        return null;//output;
    } 
    
    /**
     * public void setCreature(Int_ALife_Creature c)
     * 
     * Set the creature that owns the mind
     * @param c - the creature that owns the mind
     */
    public void setCreature(Int_ALife_Creature c){
        creature = c;
    } //End public void setCreature(Int_ALife_Creatire c)

    // Private Methods and Fuctions =============
    
    /**
     * private void setNeuronsCount()
     * 
     * Set the number of neurons in each type
     * @param  None
     * @return None
     */
    private void setNeuronsCount(){
        this.innerN = this.inputNeurons.size();
        
        //for (Neuron_ALife n:this.inputNeurons) innerN++;        
        this.midN = this.midNeurons.size();
        //for (Neuron_ALife n:this.midNeurons) midN++;
        this.outN = this.outputNeurons.size();
        //for (Neuron_ALife n:this.outputNeurons) outN++;
        this.statusN = this.statusNeurons.size();
        //for (Neuron_ALife n:this.statusNeurons) statusN++;
    } // End private void setNeuronsCount()
    
    /**
     * public synchronized double getInnerN()
     * 
     * Get the number of inner neurons
     * @param  None
     * @return double the number of inner neurons
     */
    public synchronized double getInnerN(){
        return innerN;
    }
    
    /**
     * public synchronized double getMidN()
     * 
     * Get the number of mid neurons
     * @param  None
     * @return double the number of mid neurons
     */
    public synchronized double getMidN(){
        return midN;
    }
    
    /**
     * public synchronized double getOutN()
     * 
     * Get the number of output neurons
     * @param  None
     * @return double the number of output neurons
     */
    public synchronized double getOutN(){
        return outN;
    }
    
    /**
     * public synchronized double getStatusN()
     * 
     * Get the number of status neurons
     * @param   None
     * @return  double the number of status neurons
     */
    public synchronized double getStatusN(){
        return statusN;
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
        if (this.innerN < 1) return false;
        if (this.outN < 1)return false;
        if (this.allNeurons.size() != (innerN + midN + statusN + outN)) return false;
        //We can test integrity of all neuron weights.
        
        return true;
    } // End private boolean checkNeurons()

    /**
     * public long getNewNeuronID(Neuron_ALife n)
     * 
     * Get a new neuron ID for n and set it
     * @param n Neuron_ALife the neuron to set the ID
     * @return long the new neuron ID
     */
    public long getNewNeuronID(Neuron_ALife n){
        long id = -1; // creature_ID + type(1 = inn, 2 = mid, 3 = status, 4 = out) + neuron_number (<1000)
        if (n == null) return id;
        if (!this.allNeurons.contains(n)) return id;
        id = this.creature.getIdCreature() * 10;
        if (n instanceof Input_Neuron_ALife) id += 1;
        //else if (n instanceof Mid_Neuron_ALife) id += 2;
        //else if (n instanceof Status_Neuron_ALife) id += 3;
        else if (n instanceof Out_Neuron_ALife) id += 4;
        else throw new IllegalArgumentException("Mind_ALife: getNewNeuronID(Neuron_ALife n) Neuron_ALife n is unknown Neuron_ALife");
        id = id * 1000;
        id += this.allNeurons.indexOf(n);
        n.setNeuron_ID(id);
        return id;
    } // End public long getNewNeuronID(Neuron_ALife n)

    /**
     * public void addNeuron(Neuron_ALife n)
     * 
     * Add a neuron to the mind and set its ID
     * @param n Neuron_ALife the neuron to add
     * @return None
     */
    public void addNeuron(Neuron_ALife n){
        if (n == null) return;
        this.allNeurons.add(n);
        this.getNewNeuronID(n);
        if (n instanceof Out_Neuron_ALife) this.outputNeurons.add((Out_Neuron_ALife)n);
        else if (n instanceof Input_Neuron_ALife) this.inputNeurons.add(n);
        //else if (n instanceof Status_Neuron_ALife) this.statusNeurons.add(n);
        else if (n instanceof Neuron_ALife) this.midNeurons.add(n);
        else throw new IllegalArgumentException("Mind_ALife: addNeuron(Neuron_ALife n) Neuron_ALife n is unknown Neuron_ALife");
    } // End public void addNeuron(Neuron_ALife n)

    // Save Load Merge Mutate
    
    
    
} // End Class
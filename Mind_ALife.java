import java.util.*;

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
    public static enum Action {UP,DOWN,RIGHT,LEFT,EAT,REPRODUCE,ATTACK};
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
    public Mind_ALife(Int_ALife_Creature creature){
        setCreature(creature);
        //Add inputNeurons
        outputNeurons = new ArrayList <Out_Neuron_ALife>();
        inputNeurons = new ArrayList <Neuron_ALife>();
        statusNeurons = new ArrayList <Neuron_ALife>();
        midNeurons = new ArrayList <Neuron_ALife>();
        allNeurons = new ArrayList <Neuron_ALife>(); //dudas de si es necesario
        Neuron_ALife auxN = new Reproductable_Neuron_ALife(this.creature);
        allNeurons.add(auxN);
        inputNeurons.add(auxN);
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
        
        setNeuronsCount();
        if (!checkNeurons()){
            throw new IllegalArgumentException("No se puede crear el objeto debido a una condición inválida.");
        }
        //Add In Neuron habmbre
        //Add In neuron comida en el suelo
        //Add out Neuron mitosis = reproduccion independiente
        //Add out Neuron comer
        //Add out neuron morir???
    }// End public Mind_ALife(Int_ALife_Creature creature)
    
    public Mind_ALife(ArrayList<Int_ALife_Creature>  progenitors, Int_ALife_Creature creature, boolean mutate){
        //Inners Neuron
        // Si la tienen los dos la tiene, si la tiene 1 50% si no la tiene 0% + mutate
        this.creature = creature;
        
        this.inputNeurons = new ArrayList <Neuron_ALife>();
        this.outputNeurons = new ArrayList <Out_Neuron_ALife>();
        this.midNeurons = new ArrayList <Neuron_ALife>();
        this.allNeurons = new ArrayList <Neuron_ALife>();
        this.statusNeurons = new ArrayList <Neuron_ALife>();
        
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
                        this.inputNeurons.add(n);
                        this.allNeurons.add(n);
                    }
                }else{
                    //ensure that is not unique progenitor
                    if (progenitors.size() == 1){
                        int breakp =1;
                    }
                }
                
            }
            //for later use avg of progenitors neurons types count
            innerN = p.getMind().getInnerN() / numProg;
            midN = p.getMind().getMidN() / numProg;
            outN = p.getMind().getOutN() / numProg;
            statusN = p.getMind().getStatusN() / numProg;      
        } // progenitors inner neurons tested to be added.
        //mutate 
        
        //Mid Neuron
        int i__ForTest = (int) midN +random.nextInt(3)-1;
        /*
        for ( int i = (int) midN +random.nextInt(3)-1; i > 0; i--){
            Neuron_ALife auxN = new Neuron_ALife(this.inputNeurons, creature);
//            //faltan pesos
            this.midNeurons.add(auxN);
            this.allNeurons.add(auxN);
        }
        */
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
    
    // Public Methods and Fuctions ==============
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
        output = new Double(0);
        outNeuron = null;
        ArrayList outOptions = new ArrayList<Out_Neuron_ALife>();
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
    }
    
    public void updateLearn(Double enhanced, Double change){
        // En funcion de enhanced y el peso mejora.
        synchronized (this){
            for (Neuron_ALife n:allNeurons){
                n.updateLearn(enhanced, change);
            }
        }        
    }
    
    // Getter and setters
    public Double getOutput(){//?????
        return null;//output;
    } 
    
    public void setCreature(Int_ALife_Creature c){
        creature = c;
    } //End public void setCreature(Int_ALife_Creatire c)
    // Private Methods and Fuctions =============
    
    private void setNeuronsCount(){
        this.innerN = 0;
        for (Neuron_ALife n:this.inputNeurons) innerN++;        
        this.midN = 0;
        for (Neuron_ALife n:this.midNeurons) midN++;
        this.outN = 0;
        for (Neuron_ALife n:this.outputNeurons) outN++;
        this.statusN = 0;
        for (Neuron_ALife n:this.statusNeurons) statusN++;
    }
    
    public synchronized double getInnerN(){
        return innerN;
    }
    
    public synchronized double getMidN(){
        return midN;
    }
    
    public synchronized double getOutN(){
        return outN;
    }
    
    public synchronized double getStatusN(){
        return statusN;
    }
    /*        
    innerN = p.getMind().getInnerN() / numProg;
            midN = p.getMind().getMidN() / numProg;
            outN = p.getMind().getOutN / numProg;
            statusN = p.getMind().getStatusN / numProg;
            
    */
   
    private boolean checkNeurons(){
        // must be finished so neurons counted
        if (this.innerN < 1) return false;
        if (this.outN < 1)return false;
        //We can test integrity of all neuron weights.
        
        return true;
    }
    // Save Load Merge Mutate
    
    // Main if needed --------------------------------------------------------------------
    
    
} // End Class
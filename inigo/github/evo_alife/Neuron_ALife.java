package inigo.github.evo_alife;

import java.util.*;

/**
 * Write a description of class Neuron_Alife here.
 * 
 * @author Iñigo Marco 
 * @version 19-06-2023
 */
public class Neuron_ALife
{
    // Fields ----------------------------------------------------------------------------
     double u = Mind_ALife.DEFAULT_u;
     ArrayList <Neuron_ALife> inputs =  new ArrayList <Neuron_ALife>();
     ArrayList<Double> weights = new ArrayList<Double>();
     Double output = null;
     long neuron_ID = -1; // creature_ID + type(1 = inn, 2 = mid, 3 = status, 4 = out) + neuron_number (<1000)
     Int_ALife_Creature creature = null;
     Mind_ALife mind = null;
     
     // type in mid out
     
    // Methods ---------------------------------------------------------------------------
    // Constructors ============================
    // necesitamos varios constructores un por defecto, otro que marque las entradas....
    // .... y otro que mezcle varias redes

    /**
     * public Neuron_ALife()
     * 
     * Empty constructor
     */
    public Neuron_ALife() {}

    
    
    /**
     * public Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c)
     * 
     * Default constructor with all datas
     * @param ns ArrayList <Neuron_ALife> an ArrayList with the input neurons
     * @param c  Int_ALife_Creature the creature owner of this neuron
     */
    public Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c) throws IllegalArgumentException {
        //Checks
        if (c==null) return; //(For moment we dont contemps unowned neurons
        if (ns == null || ns.isEmpty()) throw new IllegalArgumentException("Neuron_ALife: Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c) ns == null || ns.isEmpty()");
        //double u = Mind_ALife.DEFAULT_u;
        //Double output = null; 
        creature = c;
        mind = c.getMind();
        this.inputs = new ArrayList <Neuron_ALife>();
        this.weights = new ArrayList<Double>();
        for(Neuron_ALife n: ns){
            inputs.add(n); // Be careful with this adds as imput the neurons in parameter, not a copy
            weights.add(Mind_ALife.DEFAULT_Weight);
            if (n.mind != this.mind || n.creature != this.creature) MultiTaskUtil.threadMsg("Neuron_ALife: Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c) n.mind != this.mind || n.creature != this.creature"); //For test
        }
        //this.neuron_ID = this.mind.getNewNeuronID(this); Autoassing in Mind cration
    } // End public Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c)

    /**
     * public Neuron_ALife(Neuron_ALife n)
     * 
     * Constructor to copy or dupe a neuron
     * @param n Neuron_ALife the neuron to copy
     */
    public Neuron_ALife (Neuron_ALife n) throws IllegalArgumentException{
        if (n == null) 
            throw new IllegalArgumentException("Neuron_ALife: Neuron_ALife(Neuron_ALife n) n.creature == null");
        this.creature = n.creature;
        this.mind = n.mind;
        if (this.mind.inputNeurons == null || this.mind.inputNeurons.isEmpty()){
            //has not input neurons on this mind
            int breackpoint = 1;
        }
        if ( !(n instanceof Input_Neuron_ALife)){
            this.inputs = new ArrayList <Neuron_ALife>();
            this.weights = new ArrayList<Double>();

            if (this.mind.allNeurons.size() - this.mind.outputNeurons.size() != n.inputs.size()){
                //Diferent number of inputs neurons
                int breackpoint = 1;
            }else{
                for (int i = 0; i <this.mind.allNeurons.size() - this.mind.outputNeurons.size(); i++){
                    this.inputs.add(this.mind.allNeurons.get(i));
                    this.weights.add(Double.valueOf(n.weights.get(i))); //test if its new or copy
                }
            }
        }
        this.u = n.u;
        if (n.output == null) this.output = null;
        else this.output = Double.valueOf(n.output);
        this.neuron_ID = n.neuron_ID; // For full dupe, we have change in mind
        //Any more??
    }// End public Neuron_ALife (Neuron_ALife n)

    // Public Methods and Fuctions ==============
    
    /**
     * public void reset()
     * 
     * Reset the output of the neuron
     * @param  None
     * @return None
     */
    public void reset(){
        //synchronized (output){
            //For test
            if (this.creature.getMind() != this.mind){
                int breackpoint = 1;
                MultiTaskUtil.threadMsg("Neuron_ALife.reset() this.creature.getMind() != this.mind ("+this.neuron_ID+")");
            }
            if (this.neuron_ID == -1) {
                int breackpoint = 1;
                MultiTaskUtil.threadMsg("From Neuron_ALife.reset() this.neuron_ID == -1 ");
            }
            //End test
            output = null;
        //}
    } // End public void reset()
    
    /**
     * public double activation()
     * 
     * Calculate the activation of the neuron
     * @param  None
     * @return double the activation value of the neuron 0..1
     */
    public double activation(){
        if (this.output != null) return output;
        int i = 0;//Psoble checkeo nully empty 
        double sum = u;
        for(Neuron_ALife n:inputs){
            //for test
            //MultiTaskUtil.threadMsg("("+this.neuron_ID+")Neuron_ALife.activation() n = "+n.neuron_ID);
            if (n != this){
                if (n.getOutput() != null) {
                    sum += n.getOutput() * weights.get(i);
                } else {
                    sum += n.activation() * weights.get(i);
                }
            } // End if (n != this)
        }// End for(Neuron_ALife n:inputs) all inputs neurons
        output = sum;
        return sum;
    } // End public double activation()
    
    /**
     * public void updateLearn(Double enhanced, Double change)
     * 
     * Update the neuron weights and u
     * @param weightupdate  - neuron parameters change factor
     * @param uupdate       - u change
     * @param stChange    - status change
     * @return None
     */
    public void updateLearn(Double weightupdate, Double uupdate, Double stChange){
        // En funcion de enhanced y change el peso mejora. Debo ACOTARLOS
        // Based in back propagation algorithm
        // Based in back propagation algorithm
        if (weightupdate == null || stChange == null || weightupdate == 0L || stChange == 0L) return;
        if (this.inputs == null || this.inputs.isEmpty()) return; //No inputs neurons
        /* DE OUT NEURON
        this.u += u*enhanced * change * Mind_ALife.DEFAULT_u_changeFraction;
        weights.replaceAll(d -> d + d * enhanced * change * Mind_ALife.DEFAULT_u_changeFraction);
        */
        //u modificación por decidir
        //weights los que aportan mas de la media mejoran un x% y luego se acota para que la suma total de pesos sea 1

        
        double changeValue = weightupdate * stChange;
        for(int i = 0; i < this.inputs.size(); i++){
            Neuron_ALife n = this.inputs.get(i);
            //No propagation for status neurons inputs (endeless loop)
            if (n instanceof Status_Neuron_ALife) continue; 
            n.updateLearn(weightupdate * weights.get(i), uupdate, stChange);
        }
        //u makes some Neurons over others so we need change it carefully or we inactivate some neurons
        //may be when a big update in a single action
        //this.u = this.u + this.u* enhanced * change * Mind_ALife.DEFAULT_u_changeFraction;

        //Detectar si es solo 1 accion
            //modificación = modificación prevista / entrada (OJO 0)
        //si no es 1 acción seguir y modificar u??
        
        for(int i = 0 ; i<weights.size(); i++){
            if (inputs.get(i).getOutput() == null) continue;
            double aux = weights.get(i);
            //adjust each weight acording to the participation in output of the input neuron
            //for test 
            double viewOutputI = inputs.get(i).getOutput();
            double viewFactor1 = changeValue *( (inputs.get(i).getOutput()*aux));
            double viewFactor2 = this.output ;
            double viewFactor3 = changeValue *( (inputs.get(i).getOutput()*aux)/this.output );
            //end test
            aux = aux + changeValue *( (inputs.get(i).getOutput()*aux) /this.output ); 
            weights.set(i, aux);
        }
        
        double[] aux = this.weights.stream().mapToDouble(Double::doubleValue).toArray();
        //double mean = ALifeCalcUtil.mean(aux);
        //for (int i = 0; i < aux.length; i++){
        //    if (aux[i] > mean) aux[i] = aux[i] + aux[i]* changeValue * Mind_ALife.DEFAULT_u_changeFraction;  
        //    else aux[i] = aux[i] + aux[i]* changeValue * Mind_ALife.DEFAULT_Weight_changeFraction * Mind_ALife.DEFAULT_weight_changeUnderFraction;
        //}
        //ALifeCalcUtil.multiplyArrayByCte(null, chageValue)
        aux = ALifeCalcUtil.normalizeArrayToTotal_1(aux);

        ArrayList <Double> auxAL = new ArrayList <Double>();
        Arrays.stream(aux).forEach(value -> auxAL.add(value));
        this.weights = auxAL;
        
    } // End public void updateLearn(Double enhanced, Double change)
    
    /**
    * public synchronized void normalizeWeights()
    * 
    * Normalize the weights of the neuron
    * @param  None
    * @return None
    */
    public synchronized void normalizeWeights(){
        double[] aux = ALifeCalcUtil.normalizeArrayToTotal_1(this.weights.stream().mapToDouble(Double::doubleValue).toArray());
        ArrayList <Double> auxAL = new ArrayList <Double>();
        Arrays.stream(aux).forEach(value -> auxAL.add(value));
        //Arrays.stream(aux).forEach(value -> auxAL.add(value*(1-this.u))); // for output in 0..1
        this.weights = auxAL;
    } // End public Neuron_ALife normalizeWeights()

    /**
     * public static Neuron_ALife dupeNeuron_ALife(Neuron_ALife n)
     * 
     * Returns a copy of this neuron
     * @param  n Neuron_ALife the neuron to copy
     * @return Neuron_ALife the copy of the neuron
     
    public static Neuron_ALife dupeNeuron_ALife(Neuron_ALife n){
         // Returns a copy of this neuron
         //Check
        if (n == null) return null;
        if (n instanceof Out_Neuron_ALife){
            Out_Neuron_ALife n_Out = Out_Neuron_ALife.dupeNeuron_ALife((Out_Neuron_ALife)n);
            return n_Out;
        }
        if (n instanceof Status_Neuron_ALife){
            Status_Neuron_ALife n_Status = Status_Neuron_ALife.dupeNeuron_ALife((Status_Neuron_ALife)n);
            return n_Status;
        }
        //inn and mid can be cloned with the standar method
        if(n instanceof Input_Neuron_ALife){
            Input_Neuron_ALife n_In = Input_Neuron_ALife.dupeNeuron_ALife((Input_Neuron_ALife)n);
            return n_In;
        }
        //Default
        Neuron_ALife n2 = new Neuron_ALife(n);     
        return n2;
    } // End public static Neuron_ALife dupeNeuron_ALife(Neuron_ALife n)
*/
    //Test Dudoso
    public Neuron_ALife dupeNeuron_ALife(){
         // Returns a copy of this neuron
         //Check
         Neuron_ALife newN = null;
         try{
            newN = new Neuron_ALife(this);
         } catch (IllegalArgumentException e){
             MultiTaskUtil.threadMsg("Neuron_ALife.dupeNeuron_ALife() IllegalArgumentException e");
         }
         return newN;
    } // End public static Neuron_ALife dupeNeuron_ALife(Neuron_ALife n)

    // Getter and setters

    /**
     * public Double getOutput()
     * 
     * Returns the output of the neuron
     * @return Double the output of the neuron
     */
    public Double getOutput(){
        return output;
    }

    /**
     * public void setNeuron_ID(long neuron_ID)
     * 
     * Set the neuron_ID of the neuron
     * @param neuron_ID long the neuron_ID of the neuron
     * @return None
     */
    public void setNeuron_ID(long neuron_ID){
        this.neuron_ID = neuron_ID; // -1 is a neuron out of any mind for moment
    } // End public void setNeuron_ID(long neuron_ID)
    
    /**
     * public synchronized long getNeuron_ID()
     * 
     * Returns the neuron_ID of the neuron
     * @param   - None
     * @return  - long the neuron_ID of the neuron
     */
    public synchronized long getNeuron_ID(){
        return this.neuron_ID; // -1 is a neuron out of any mind for moment
    } // End public void setNeuron_ID(long neuron_ID)


    /**
     * public void setMind(Mind_ALife mind)
     * 
     * Set the mind of the neuron and check if all inputs neurons are into the mind 
     * other way force it
     * @param mind Mind_ALife the mind of the neuron
     * @return None
     */
    public synchronized void setMind(Mind_ALife mind){
        //Check
        if (mind == null) return;
        //Check if all inputs neurons are into the mind other way force it
        boolean inputsIntoMind = true;
        for (Neuron_ALife n: this.inputs){
            if (n.mind != mind) inputsIntoMind = false;
        }
        if (inputsIntoMind) this.mind = mind;
        else{
            //New inputs and weights on this mind
            ArrayList <Neuron_ALife> newInputs = new ArrayList <Neuron_ALife>();
            ArrayList <Double> newWeights = new ArrayList <Double>();
            if (this.inputs.size() == mind.allNeurons.size() - mind.outputNeurons.size()){
                //All inputs neurons are into the mind
                for (Neuron_ALife n: mind.allNeurons){
                    if ( !(n instanceof Out_Neuron_ALife) ){
                        newInputs.add(n);
                        //weights are ok;
                    };
                }
                inputs = newInputs;
                //weights are ok (No need weights = newWeights;)
            } else { //diferente number or correct inputs and weights
                for (Neuron_ALife n: mind.allNeurons){
                    if ( !(n instanceof Out_Neuron_ALife) ){
                        newInputs.add(n);
                        newWeights.add((double)(1/(mind.allNeurons.size() - mind.outputNeurons.size())));
                    };
                }
                inputs = newInputs;
                weights = newWeights;
            }
           
        }// end else of  if (inputsIntoMind)
    } // End public void setMind(Mind_ALife mind)

    /**
     * public Mind_ALife getMind()
     * 
     * Returns the mind of the neuron
     * @ param None
     * @return Mind_ALife the mind of the neuron
     */
    public Mind_ALife getMind(){
        return mind;
    } // End public Mind_ALife getMind()

    /**
     * public synchronized void setCreature(Int_ALife_Creature creature)
     * 
     * Set the creature of the neuron
     * @param creature Int_ALife_Creature the creature of the neuron
     * @return None
     */
    public synchronized void setCreature(Int_ALife_Creature creature){
        this.creature = creature;
    } // End public void setCreature(Int_ALife_Creature creature)

    /**
     * public synchronized Int_ALife_Creature getCreature()
     * 
     * Returns the creature of the neuron
     * @param None
     * @return Int_ALife_Creature the creature of the neuron
     */
    public synchronized Int_ALife_Creature getCreature(){
        return creature;
    } // End public Int_ALife_Creature getCreature()

    /**
     * public synchronized ArrayList<Neuron_ALife> getInputs()
     * 
     * Returns the inputs neurons of the neuron
     * @param None
     * @return ArrayList<Neuron_ALife> the inputs neurons of the neuron
     */
    public synchronized ArrayList<Neuron_ALife> getInputs(){
        return inputs;
    }// End public synchronized ArrayList<Neuron_ALife> getInputs()

    // Private Methods and Fuctions =============
    
    
} // End public class Neuron_ALife
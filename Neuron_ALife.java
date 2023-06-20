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
     ArrayList <Neuron_ALife> inputs;
     ArrayList<Double> weights;
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
    public Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c){
        //Checks
        if (c==null) return; //(For moment we dont contemps unowned neurons
        if (ns == null) return; else if (ns.isEmpty()) return;
        //double u = Mind_ALife.DEFAULT_u;
        //Double output = null; 
        creature = c;
        mind = c.getMind();
        this.inputs = new ArrayList <Neuron_ALife>();
        this.weights = new ArrayList<Double>();
        for(Neuron_ALife n: ns){
            inputs.add(n); // Be careful with this adds as imput the neurons in parameter, not a copy
            weights.add(Mind_ALife.DEFAULT_Weight);
        }
        //this.neuron_ID = this.mind.getNewNeuronID(this); Autoassing in Mind cration
    } // End public Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c)

    /**
     * public Neuron_ALife(Neuron_ALife n)
     * 
     * Constructor to copy or dupe a neuron
     * @param n Neuron_ALife the neuron to copy
     */
    public Neuron_ALife (Neuron_ALife n){
        if (n.creature == null) 
            throw new IllegalArgumentException("Neuron_ALife: Neuron_ALife(Neuron_ALife n) n.creature == null");
        this.creature = n.creature;
        this.mind = n.mind;
        if (this.mind.inputNeurons == null || this.mind.inputNeurons.isEmpty()){
            //has not input neurons on this mind
            int breackpoint = 1;
        }
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
        this.u = n.u;
        this.output = Double.valueOf(n.output);
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
        int i = 0;//Psoble checkeo nully empty 
        double sum = u;
        for(Neuron_ALife n:inputs){
            if (n.getOutput() != null) {
                sum += n.getOutput() * weights.get(i);
            } else {
                sum += n.activation() * weights.get(i);
            }
        }
        output = sum;
        return sum;
    } // End public double activation()
    
    /**
     * public void updateLearn(Double enhanced, Double change)
     * 
     * Update the neuron weights and u
     * @param enhanced
     * @param change
     * @return None
     */
    public void updateLearn(Double enhanced, Double change){
        // En funcion de enhanced y change el peso mejora. Debo ACOTARLOS
        // Based in back propagation algorithm
        this.u += u*enhanced * change;
        for (Double d: weights){
            d += d*enhanced * change;
        }
    } // End public void updateLearn(Double enhanced, Double change)
    
    /**
     * public static Neuron_ALife dupeNeuron_ALife(Neuron_ALife n)
     * 
     * Returns a copy of this neuron
     * @param  n Neuron_ALife the neuron to copy
     * @return Neuron_ALife the copy of the neuron
     */
    public static Neuron_ALife dupeNeuron_ALife(Neuron_ALife n){
        // Returns a copy of this neuron
        Neuron_ALife n2 = new Neuron_ALife(n);
        // for test
        System.out.println(n.getClass());
        System.out.println((n.dupeNeuron_ALife(n2)).getClass());
        n2 = n.dupeNeuron_ALife(n); //
        //End test
        if (n instanceof Out_Neuron_ALife){
            Out_Neuron_ALife n_Out = Out_Neuron_ALife.dupeNeuron_ALife((Out_Neuron_ALife)n);
            return n_Out;
        }       
        return n2;
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
    
    // Private Methods and Fuctions =============
    
    
} // End public class Neuron_ALife
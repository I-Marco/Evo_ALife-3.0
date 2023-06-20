import java.util.*;
import java.util.concurrent.RunnableScheduledFuture;

/**
 * Write a description of class Neuron_Alife here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Reproductable_Neuron_ALife extends Input_Neuron_ALife
{
    // Fields ----------------------------------------------------------------------------
     // type in mid out
     
    // Methods ---------------------------------------------------------------------------
    // Constructors ============================

    public Reproductable_Neuron_ALife(Reproductable_Neuron_ALife n){
        super(n);
    }

    // necesitamos varios constructores un por defecto, otro que marque las entradas....
    // .... y otro que mezcle varias redes
    public Reproductable_Neuron_ALife(Int_ALife_Creature c){
        //Checks
        if (c==null) {
            return; //(For moment we dont contemps unowned neurons
        }
        this.creature = c;
        //if (ns == null) return; else if (ns.isEmpty()) return;
        //double u = Mind_ALife.DEFAULT_u;
        //Double output = null; 
        //creature = c;
        //inputs = new ArrayList <Neuron_ALife>();
        //weights = new ArrayList<Double>();
        //for(Neuron_ALife n: ns){
        //    inputs.add(n);
        //    weights.add(Mind_ALife.DEFAULT_Weight);
        //}                
    }
    // Public Methods and Fuctions ==============
    /**
     * public double activation()
     *  
     * Calculate the activation of the neuron, In this case 1 if creature is reproductable and 0 if not
     * @return double the activation value of the neuron 0..1
     */
    @Override
    public double activation(){
        if (this.output != null) return output;
        
        output = Mind_ALife.FALSE_in_double;
        if (creature.getReproductable()) output = Mind_ALife.TRUE_in_double;
        return output; //this.output" is null" java.lang.NullPointerException ?????
    }
    
    /**
     * public void updateLearn(Double enhanced, Double change)
     *  
     * Update the neuron in function of the enhanced and change In this case just 1 if reproductable and 0 if not
     * @param enhanced Double the enhanced of the neuron
     * @param change Double the change of the neuron
     */
    @Override
    public void updateLearn(Double enhanced, Double change){
        // En funcion de enhanced y change el peso mejora. Debo ACOTARLOS
        // Based in back propagation algorithm
    }
    
    /**
     * public static Reproductable_Neuron_ALife dupeNeuron_ALife(Reproductable_Neuron_ALife n)
     * 
     * Copy a neuron of this class 
     * @param  n Reproductable_Neuron_ALife the neuron to copy
     * @return Reproductable_Neuron_ALife the copy of the neuron
     */
    public static Reproductable_Neuron_ALife dupeNeuron_ALife(Reproductable_Neuron_ALife n){
        //Its not override since input and output parameters classes are diferent
        if (n.creature == null) return null;
        Reproductable_Neuron_ALife newN = new Reproductable_Neuron_ALife(n);
        return newN;
    } // End public static Reproductable_Neuron_ALife dupeNeuron_ALife(Reproductable_Neuron_ALife n)

    // Getter and setters

    // Private Methods and Fuctions =============

    // Main if needed --------------------------------------------------------------------
    
    
} // End Class
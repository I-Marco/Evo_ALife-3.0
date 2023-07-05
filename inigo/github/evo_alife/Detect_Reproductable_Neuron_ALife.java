package inigo.github.evo_alife;

//import java.util.*;
//import java.util.concurrent.RunnableScheduledFuture;

/**
 * Write a description of class Neuron_Alife here.
 * 
 * @author Iñigo Marco 
 * @version 20-05-2023
 */
public class Detect_Reproductable_Neuron_ALife extends Input_Neuron_ALife
{
    // Fields ----------------------------------------------------------------------------
     
    // Methods ---------------------------------------------------------------------------

    // Constructors ============================
    /**
     * public Reproductable_Neuron_ALife(Reproductable_Neuron_ALife n)
     * 
     * Constructor to help in dupeNeuron_ALife methods
     * @param n Reproductable_Neuron_ALife the neuron to copy
     */
    public Detect_Reproductable_Neuron_ALife(Detect_Reproductable_Neuron_ALife n){
        super(n);
    } // End public Reproductable_Neuron_ALife(Reproductable_Neuron_ALife n)

    // necesitamos varios constructores un por defecto, otro que marque las entradas....
    // .... y otro que mezcle varias redes
    /**
     * public Reproductable_Neuron_ALife(Int_ALife_Creature c)
     * 
     * Default constructor of the class
     * @param c Int_ALife_Creature the creature that owns the neuron
     */
    public Detect_Reproductable_Neuron_ALife(Int_ALife_Creature c){
        //Checks
        if (c==null) {
            return; //(For moment we dont contemps unowned neurons
        }
        this.creature = c;
        this.mind = c.getMind();
    } // End public Reproductable_Neuron_ALife(Int_ALife_Creature c)
    
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
        if (creature.getReproductable()) 
            output = Mind_ALife.TRUE_in_double;
        return output; //this.output" is null" java.lang.NullPointerException ?????
    }
        
    /**
     * public static Reproductable_Neuron_ALife dupeNeuron_ALife(Reproductable_Neuron_ALife n)
     * 
     * Copy a neuron of this class 
     * @param  n Reproductable_Neuron_ALife the neuron to copy
     * @return Reproductable_Neuron_ALife the copy of the neuron
     */
    public static Detect_Reproductable_Neuron_ALife dupeNeuron_ALife(Detect_Reproductable_Neuron_ALife n){
        //Its not override since input and output parameters classes are diferent
        if (n.creature == null) return null;
        Detect_Reproductable_Neuron_ALife newN = new Detect_Reproductable_Neuron_ALife(n);
        return newN;
    } // End public static Reproductable_Neuron_ALife dupeNeuron_ALife(Reproductable_Neuron_ALife n)

    @Override
    public Detect_Reproductable_Neuron_ALife dupeNeuron_ALife(){
        
        Detect_Reproductable_Neuron_ALife newN = new Detect_Reproductable_Neuron_ALife(this);
        return newN;
    } // End public static Reproductable_Neuron_ALife dupeNeuron_ALife(Reproductable_Neuron_ALife n)

    // Getter and setters

    // Private Methods and Fuctions =============
    
} // End Class Reproductable_Neuron_ALife
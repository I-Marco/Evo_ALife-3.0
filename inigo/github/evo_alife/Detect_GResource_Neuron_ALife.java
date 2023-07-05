package inigo.github.evo_alife;

import java.awt.Point;

/**
 * Write a description of class Neuron_Alife here.
 * 
 * @author Iñigo Marco 
 * @version 20-05-2023
 */
public class Detect_GResource_Neuron_ALife extends Input_Neuron_ALife
{
    // Fields ----------------------------------------------------------------------------
     
    // Methods ---------------------------------------------------------------------------

    // Constructors ============================
    /**
     * public GResourceDetection_Neuron_ALife(GResourceDetection_Neuron_ALife n)
     * 
     * Constructor to help in dupeNeuron_ALife methods
     * @param n GResourceDetection_Neuron_ALife the neuron to copy
     */
    public Detect_GResource_Neuron_ALife(Detect_GResource_Neuron_ALife n){
        super(n);
    } // End public GResourceDetection_Neuron_ALife(GResourceDetection_Neuron_ALife n)

    /**
     * public GResourceDetection_Neuron_ALife(Int_ALife_Creature c)
     * 
     * Default constructor of the class
     * @param c Int_ALife_Creature the creature that owns the neuron
     */
    public Detect_GResource_Neuron_ALife(Int_ALife_Creature c){
        //Checks
        if (c==null) {
            return; //(For moment we dont contemps unowned neurons
        }
        this.creature = c;
    } // End public GResourceDetection_Neuron_ALife(Int_ALife_Creature c)
    
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
        Point pos = this.creature.getPos();
        synchronized(this.creature.getEnv_ALife().getLand()){
           int[] food = this.creature.getEnv_ALife().getLand().getNutrientIn(pos.x, pos.y);
           if (food[1] > 0) output = Mind_ALife.TRUE_in_double;
        }
        return output;
    }
        
    /**
     * public static GResourceDetection_Neuron_ALife dupeNeuron_ALife(GResourceDetection_Neuron_ALife n)
     * 
     * Copy a neuron of this class 
     * @param  n GResourceDetection_Neuron_ALife the neuron to copy
     * @return GResourceDetection_Neuron_ALife the copy of the neuron
     */
    public static Detect_GResource_Neuron_ALife dupeNeuron_ALife(Detect_GResource_Neuron_ALife n){
        //Its not override since input and output parameters classes are diferent
        if (n.creature == null) return null;
        Detect_GResource_Neuron_ALife newN = new Detect_GResource_Neuron_ALife(n);
        return newN;
    } // End public static GResourceDetection_Neuron_ALife dupeNeuron_ALife(GResourceDetection_Neuron_ALife n)

    // Test Dudoso
    public Detect_GResource_Neuron_ALife dupeNeuron_ALife(){
        Detect_GResource_Neuron_ALife newN = new Detect_GResource_Neuron_ALife(this);
        return newN;
    } // End public static GResourceDetection_Neuron_ALife dupeNeuron_ALife(GResourceDetection_Neuron_ALife n)


    // Getter and setters

    // Private Methods and Fuctions =============
    
} // End Class
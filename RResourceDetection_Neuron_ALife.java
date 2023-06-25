import java.awt.Point;

/**
 * Write a description of class Neuron_Alife here.
 * 
 * @author Iñigo Marco 
 * @version 20-05-2023
 */
public class RResourceDetection_Neuron_ALife extends Input_Neuron_ALife
{
    // Fields ----------------------------------------------------------------------------
     
    // Methods ---------------------------------------------------------------------------

    // Constructors ============================
    /**
     * public RResourceDetection_Neuron_ALife(RResourceDetection_Neuron_ALife n)
     * 
     * Constructor to help in dupeNeuron_ALife methods
     * @param n RResourceDetection_Neuron_ALife the neuron to copy
     */
    public RResourceDetection_Neuron_ALife(RResourceDetection_Neuron_ALife n){
        super(n);
    } // End public RResourceDetection_Neuron_ALife(RResourceDetection_Neuron_ALife n)

    /**
     * public RResourceDetection_Neuron_ALife(Int_ALife_Creature c)
     * 
     * Default constructor of the class
     * @param c Int_ALife_Creature the creature that owns the neuron
     */
    public RResourceDetection_Neuron_ALife(Int_ALife_Creature c){
        //Checks
        if (c==null) {
            return; //(For moment we dont contemps unowned neurons
        }
        this.creature = c;
    } // End public RResourceDetection_Neuron_ALife(Int_ALife_Creature c)
    
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
        int[] food = this.creature.getEnv_ALife().getLand().nutrient(pos.x, pos.y);
        if (food[0] > 0) output = Mind_ALife.TRUE_in_double;
        return output;
    }
        
    /**
     * public static RResourceDetection_Neuron_ALife dupeNeuron_ALife(RResourceDetection_Neuron_ALife n)
     * 
     * Copy a neuron of this class 
     * @param  n RResourceDetection_Neuron_ALife the neuron to copy
     * @return RResourceDetection_Neuron_ALife the copy of the neuron
     */
    public static RResourceDetection_Neuron_ALife dupeNeuron_ALife(RResourceDetection_Neuron_ALife n){
        //Its not override since input and output parameters classes are diferent
        if (n.creature == null) return null;
        RResourceDetection_Neuron_ALife newN = new RResourceDetection_Neuron_ALife(n);
        return newN;
    } // End public static RResourceDetection_Neuron_ALife dupeNeuron_ALife(RResourceDetection_Neuron_ALife n)

    // Getter and setters

    // Private Methods and Fuctions =============
    
} // End Class
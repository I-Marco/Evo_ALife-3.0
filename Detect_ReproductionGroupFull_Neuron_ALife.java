import java.awt.Point;
import java.util.ArrayList;

/**
 * Each input neuron have a diferent class. This is a reproduction group formed status detection
 * 
 * @author Iñigo Marco 
 * @version 28-06-2023
 */
public class Detect_ReproductionGroupFull_Neuron_ALife extends Input_Neuron_ALife
{
    // Fields ----------------------------------------------------------------------------
     
    // Methods ---------------------------------------------------------------------------

    // Constructors ============================
    /**
     * public BResourceDetection_Neuron_ALife(BResourceDetection_Neuron_ALife n)
     * 
     * Constructor to help in dupeNeuron_ALife methods
     * @param n BResourceDetection_Neuron_ALife the neuron to copy
     */
    public Detect_ReproductionGroupFull_Neuron_ALife(Detect_ReproductionGroupFull_Neuron_ALife n){
        super(n);
    } // End public BResourceDetection_Neuron_ALife(BResourceDetection_Neuron_ALife n)

    /**
     * public BResourceDetection_Neuron_ALife(Int_ALife_Creature c)
     * 
     * Default constructor of the class
     * @param c Int_ALife_Creature the creature that owns the neuron
     */
    public Detect_ReproductionGroupFull_Neuron_ALife (Int_ALife_Creature c) throws IllegalArgumentException{
        super();
        //Checks
        if (c==null) {
            throw new IllegalArgumentException("Creature can't be null");
        }

        this.creature = c;
    } // End public BResourceDetection_Neuron_ALife(Int_ALife_Creature c)
    
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
        //1 if reproduction group is full, 0 if its empty
        //check
        if (this.creature.getReproductionGroup() == null) {
            this.creature.reproductionGroup = new ArrayList <Int_ALife_Creature>();
        }        

        output = ALifeCalcUtil.min_max_Normalization(
            this.creature.getReproductionGroup().size(),
            0, this.creature.getMinReproductionGroup());
        output = ALifeCalcUtil.min_max_Normalization(output,
            Mind_ALife.FALSE_in_double, Mind_ALife.TRUE_in_double);
        return output;
    } // End public double activation()
        
    /**
     * public static BResourceDetection_Neuron_ALife dupeNeuron_ALife(BResourceDetection_Neuron_ALife n)
     * 
     * Copy a neuron of this class 
     * @param  n BResourceDetection_Neuron_ALife the neuron to copy
     * @return BResourceDetection_Neuron_ALife the copy of the neuron
     */
    public static Detect_ReproductionGroupFull_Neuron_ALife dupeNeuron_ALife(Detect_ReproductionGroupFull_Neuron_ALife n){
        //Its not override since input and output parameters classes are diferent
        if (n.creature == null) return null;
        Detect_ReproductionGroupFull_Neuron_ALife newN = new Detect_ReproductionGroupFull_Neuron_ALife(n);
        return newN;
    } // End public static BResourceDetection_Neuron_ALife dupeNeuron_ALife(BResourceDetection_Neuron_ALife n)

    // Getter and setters

    // Private Methods and Fuctions =============
    
} // End Class


import java.awt.Point;

/**
 * Write a description of class Neuron_Alife here.
 * 
 * @author Iñigo Marco 
 * @version 28-06-2023
 */
public class Detec_Nearest_R_NorthDist_Neuron_ALife extends Input_Neuron_ALife
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
    public Detec_Nearest_R_NorthDist_Neuron_ALife(Detec_Nearest_R_NorthDist_Neuron_ALife n){
        super(n);
    } // End public BResourceDetection_Neuron_ALife(BResourceDetection_Neuron_ALife n)

    /**
     * public BResourceDetection_Neuron_ALife(Int_ALife_Creature c)
     * 
     * Default constructor of the class
     * @param c Int_ALife_Creature the creature that owns the neuron
     */
    public Detec_Nearest_R_NorthDist_Neuron_ALife (Int_ALife_Creature c) throws IllegalArgumentException{
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
        //MAX if there is desired resource int its position, min i its in max detecion range
        output = Mind_ALife.FALSE_in_double;// not found
        Point pos = this.creature.getPos();
        for (int range = 0; range <= this.creature.getDetectionRange(); range++){
            for (int x = pos.x - range; x <= pos.x + range; x = x + 2 * range){
                for (int y = pos.y - range; y <= pos.y + range; y = y + 2 * range){
                    int[] food = this.creature.getEnv_ALife().getLand().getNutrientIn(x, y);
                    if (food[0] > 0) {
                        double dist = this.creature.getPos().distance(x, y);
                        output = Double.valueOf((this.creature.getDetectionRange() + 1 - dist) / (this.creature.getDetectionRange() + 1));
                        output = (Mind_ALife.TRUE_in_double - Mind_ALife.FALSE_in_double) * output;
                        return output;
                    }
                }
            }
        }
        return output;
    } // End public double activation()
        
    /**
     * public static BResourceDetection_Neuron_ALife dupeNeuron_ALife(BResourceDetection_Neuron_ALife n)
     * 
     * Copy a neuron of this class 
     * @param  n BResourceDetection_Neuron_ALife the neuron to copy
     * @return BResourceDetection_Neuron_ALife the copy of the neuron
     */
    public static Detec_Nearest_R_NorthDist_Neuron_ALife dupeNeuron_ALife(Detec_Nearest_R_NorthDist_Neuron_ALife n){
        //Its not override since input and output parameters classes are diferent
        if (n.creature == null) return null;
        Detec_Nearest_R_NorthDist_Neuron_ALife newN = new Detec_Nearest_R_NorthDist_Neuron_ALife(n);
        return newN;
    } // End public static BResourceDetection_Neuron_ALife dupeNeuron_ALife(BResourceDetection_Neuron_ALife n)

    // Getter and setters

    // Private Methods and Fuctions =============
    
} // End Class
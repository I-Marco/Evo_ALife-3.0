package inigo.github.evo_alife;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Each input neuron have a diferent class. This is a reproduction group formed status detection
 * 
 * @author Iñigo Marco 
 * @version 28-06-2023
 */
public class Detect_defPoints_Neuron_ALife extends Input_Neuron_ALife
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
    public Detect_defPoints_Neuron_ALife(Detect_defPoints_Neuron_ALife n){
        super(n);
    } // End public BResourceDetection_Neuron_ALife(BResourceDetection_Neuron_ALife n)

    /**
     * public BResourceDetection_Neuron_ALife(Int_ALife_Creature c)
     * 
     * Default constructor of the class
     * @param c Int_ALife_Creature the creature that owns the neuron
     */
    public Detect_defPoints_Neuron_ALife (Int_ALife_Creature c) throws IllegalArgumentException{
        super(c);
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
        this.lockNeuron.lock();
        try{
            if (this.output != null) return output;
            //Detect_defPoints_Neuron_ALife
            //check
            if (this.creature == null) {
                MultiTaskUtil.threadMsg("Error in Detect_defPoints_Neuron_ALife No creature defined.(null)");
                return Mind_ALife.FALSE_in_double;
            }

                output = ALifeCalcUtil.min_max_Normalization(//def is 4º caracteristic so 3 indix
                    this.creature.getDef(),
                    Active_ALife_Creature.creature_minCaracteristics[3],
                    Active_ALife_Creature.creature_maxCaracteristics[3]
                );
                output = ALifeCalcUtil.min_max_Normalization(output,
                    Mind_ALife.FALSE_in_double, Mind_ALife.TRUE_in_double);

            return output;
        } finally {
            this.lockNeuron.unlock();
        }
    } // End public double activation()
    
    /**
     * public Detect_defPoints_Neuron_ALife dupeNeuron_ALife()
     * 
     * This method is used to copy the neuron
     * @param n Detect_defPoints_Neuron_ALife the neuron to copy
     * @return Detect_defPoints_Neuron_ALife the new neuron
     */
    public Detect_defPoints_Neuron_ALife dupeNeuron_ALife(){
        lockNeuron.lock();
        try{
            Detect_defPoints_Neuron_ALife newN = new Detect_defPoints_Neuron_ALife(this);
            return newN;
        } finally {
            lockNeuron.unlock();
        }
    } // End public static BResourceDetection_Neuron_ALife dupeNeuron_ALife(BResourceDetection_Neuron_ALife n)


    // Getter and setters

    // Private Methods and Fuctions =============
    
} // End public class Detect_defPoints_Neuron_ALife extends Input_Neuron_ALife

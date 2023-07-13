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
        super(c);
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
        this.lockNeuron.lock();
        try{
            if (this.output != null) return output;
        
            output = Mind_ALife.FALSE_in_double;
            Point pos = this.creature.getPos();

               int[] food = this.creature.getEnv_ALife().getLand().getNutrientIn(pos.x, pos.y);
               if (food[1] > 0) output = Mind_ALife.TRUE_in_double;

            return output;
        } finally {
            this.lockNeuron.unlock();
        }
    } // End public double activation()
    
    /**
     * public Detect_GResource_Neuron_ALife dupeNeuron_ALife()
     * 
     * This method is used to copy the neuron
     * @param  None
     * @return Detect_GResource_Neuron_ALife the new neuron
     */
    public Detect_GResource_Neuron_ALife dupeNeuron_ALife(){
        this.lockNeuron.lock();
        try{
            Detect_GResource_Neuron_ALife newN = new Detect_GResource_Neuron_ALife(this);
            return newN;
        } finally {
            this.lockNeuron.unlock();
        }
        //Detect_GResource_Neuron_ALife newN = new Detect_GResource_Neuron_ALife(this);
        //return newN;
    } // End public static GResourceDetection_Neuron_ALife dupeNeuron_ALife(GResourceDetection_Neuron_ALife n)


    // Getter and setters

    // Private Methods and Fuctions =============
    
} // End public class GResourceDetection_Neuron_ALife extends Input_Neuron_ALife
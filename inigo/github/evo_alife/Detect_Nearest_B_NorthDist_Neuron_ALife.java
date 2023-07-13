package inigo.github.evo_alife;

import java.awt.Point;

/**
 * Each input neuron have a diferent class. This is a north distance detection to B type resource
 * 
 * @author Iñigo Marco 
 * @version 28-06-2023
 */
public class Detect_Nearest_B_NorthDist_Neuron_ALife extends Input_Neuron_ALife
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
    public Detect_Nearest_B_NorthDist_Neuron_ALife(Detect_Nearest_B_NorthDist_Neuron_ALife n){
        super(n);
    } // End public BResourceDetection_Neuron_ALife(BResourceDetection_Neuron_ALife n)

    /**
     * public BResourceDetection_Neuron_ALife(Int_ALife_Creature c)
     * 
     * Default constructor of the class
     * @param c Int_ALife_Creature the creature that owns the neuron
     */
    public Detect_Nearest_B_NorthDist_Neuron_ALife (Int_ALife_Creature c) throws IllegalArgumentException{
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
    public double activation(){ // X detection no north if side distance bigger than north not detected
        this.lockNeuron.lock();
        try{
            if (this.output != null) return output;
            //MAX if there is desired resource int its position, min i its in max detecion range
            output = Mind_ALife.FALSE_in_double;// not found
            Point foodPos = null;
            
                Point pos = this.creature.getPos();
                for (int range = 0; range <= this.creature.getDetectionRange(); range++){
                    if (foodPos != null) break;
                    for (int x = pos.x - range; x <= pos.x + range; x++){
                        if (foodPos != null) break;
                        for (int y = pos.y - range; y <= pos.y; y = y + Math.max(1, 2 * range)){
                            if (foodPos != null) break;
                            int[] food = this.creature.getEnv_ALife().getLand().getNutrientIn(x, y);
                            if (food[2] > 0) {
                                //double dist = this.creature.getPos().distance(x, y);
                                //output = Double.valueOf((this.creature.getDetectionRange() + 1 - dist) / (this.creature.getDetectionRange() + 1));
                                //output = (Mind_ALife.TRUE_in_double - Mind_ALife.FALSE_in_double) * output;
                                foodPos = new Point(x,y);
                                break;
                            }
                        }
                    }
                }// End for (int range = 0; range <= this.creature.getDetectionRange(); range++) WE have foodPos or null
                if (foodPos != null) output = Double.valueOf(pos.y - foodPos.y); // North distance
                if (output <= 0) {
                    output = Mind_ALife.FALSE_in_double; // if food is south no negative value
                    return output;
                }
                output = (this.creature.getDetectionRange() + 1  - output) / (this.creature.getDetectionRange() + 1); // Normalize
                output = (Mind_ALife.TRUE_in_double - Mind_ALife.FALSE_in_double) * output; // Scale
            
            return output;
        } finally {
            this.lockNeuron.unlock();
        }
    } // End public double activation()
       
    /**
     * public Detect_Nearest_B_NorthDist_Neuron_ALife dupeNeuron_ALife()
     * 
     * Copy the neuron
     * @param   None
     * @return  Detect_Nearest_B_NorthDist_Neuron_ALife the new neuron
     */
    public Detect_Nearest_B_NorthDist_Neuron_ALife dupeNeuron_ALife(){
        Detect_Nearest_B_NorthDist_Neuron_ALife newN = new Detect_Nearest_B_NorthDist_Neuron_ALife(this);
        return newN;
    } // End public static BResourceDetection_Neuron_ALife dupeNeuron_ALife(BResourceDetection_Neuron_ALife n)


    // Getter and setters

    // Private Methods and Fuctions =============
    
} // End public class BResourceDetection_Neuron_ALife extends Input_Neuron_ALife
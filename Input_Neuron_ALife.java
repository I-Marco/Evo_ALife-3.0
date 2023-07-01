import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * public class Input_Neuron_ALife extends Neuron_ALife
 * Evo_ALife project. 
 * This is a container class for all input neurons
 * 
 * @author Iñigo Marco 
 * @version (24-05-2023)
 */
public class Input_Neuron_ALife extends Neuron_ALife{
    /**
     * This is a arraylist with all input neurons in order of aparence 
     */
    static ArrayList <Input_Neuron_ALife> input_Neuron_ALife_Order = new ArrayList <Input_Neuron_ALife>();

    // Constructors ============================
    /**
     * public Input_Neuron_ALife()
     * 
     * Empty constructor
     * @param None
     */
    public Input_Neuron_ALife(){
        super();
    } // End public Input_Neuron_ALife()


    /**
     * public Input_Neuron_ALife(Input_Neuron_ALife n)
     * 
     * Add a new input neuron to the list if its not already in
     * @param n Input_Neuron_ALife the neuron to add
     */
    public Input_Neuron_ALife(Input_Neuron_ALife n){
        super(n);
        if (!input_Neuron_ALife_Order.contains(n)) input_Neuron_ALife_Order.add(n);
    } // End public Input_Neuron_ALife(Input_Neuron_ALife n)

    // Public Methods and Fuctions ==============

    /**
     * public static int addInput_Neuron_ALife(Input_Neuron_ALife n)
     * 
     * Add a new input neuron to the list if its not already in
     * @param  n Input_Neuron_ALife the neuron to add
     * @return int the position of the neuron in the list
     */
    public static int addInput_Neuron_ALife(Input_Neuron_ALife n){
        if (!input_Neuron_ALife_Order.contains(n)) input_Neuron_ALife_Order.add(n);
        return input_Neuron_ALife_Order.indexOf(n);
    } // End public static int addInput_Neuron_ALife(Input_Neuron_ALife n)

    /**
     * public static int getOrdedPosition(Input_Neuron_ALife n)
     * 
     * Get the position of the neuron in the input_Neuron_ALife_Order list
     * @param n Input_Neuron_ALife the neuron to search
     * @return int the position of the neuron in the list
     */
    public static int getOrdedPosition(Input_Neuron_ALife n){
        return input_Neuron_ALife_Order.indexOf(n);
        //-1 if not found
    } // End public static int getOrdedPosition(Input_Neuron_ALife n)

    /**
     * public static boolean isBiggerThan(Input_Neuron_ALife n1, Input_Neuron_ALife n2)
     * 
     * Check if the first neuron is bigger than the second
     * @param n1 Input_Neuron_ALife the first neuron
     * @param n2 Input_Neuron_ALife the second neuron
     * @return boolean true if the first neuron is bigger than the second
     */
    public static boolean isBiggerThan(Input_Neuron_ALife n1, Input_Neuron_ALife n2){
        return (getOrdedPosition(n1) > getOrdedPosition(n2));
    } // End public static boolean isBiggerThan(Input_Neuron_ALife n1, Input_Neuron_ALife n2)

    /**
     * public static Reproductable_Neuron_ALife dupeNeuron_ALife(Reproductable_Neuron_ALife n)
     * 
     * static method to dupe a neuron
     * @param n Reproductable_Neuron_ALife the neuron to dupe
     * @return Reproductable_Neuron_ALife the new neuron
     */
    //public static Input_Neuron_ALife dupeNeuron_ALife(Input_Neuron_ALife n){
    public static Input_Neuron_ALife dupeNeuron_ALife(Neuron_ALife n){
        if (n == null) return null;
        try{
            if (n instanceof Detect_Reproductable_Neuron_ALife) 
                return Detect_Reproductable_Neuron_ALife.dupeNeuron_ALife((Detect_Reproductable_Neuron_ALife) n);
            return new Input_Neuron_ALife((Input_Neuron_ALife)n); //May be crash
            //return new Input_Neuron_ALife( (Input_Neuron_ALife) n); //May be crash
        } catch (Exception e){
            System.out.println("Error in dupeNeuron_ALife: " + e);
            return null;
        }
    } // End public static Reproductable_Neuron_ALife dupeNeuron_ALife(Reproductable_Neuron_ALife n)
    
} // End public class Input_Neuron_ALife extends Neuron_ALife

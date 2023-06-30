import java.util.*;

/**
 * Write a description of class Neuron_Alife here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Out_Neuron_ALife extends Neuron_ALife
{
    // Fields ----------------------------------------------------------------------------
     //double u = Mind_ALife.DEFAULT_u;
     
     //ArrayList <Neuron_ALife> inputs;
     //ArrayList<Double> weights;
     //Double output = null; 
     Mind_ALife.Action action;
     
     //Creature creature = null;
     
     // type in mid out
     
    // Methods ---------------------------------------------------------------------------
    // Constructors ============================
    // necesitamos varios constructores un por defecto, otro que marque las entradas....
    // .... y otro que mezcle varias redes
    
    /**
     * public Out_Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c, Mind_ALife.Action action)
     * 
     * Default constructor with all datas
     * @param ns ArrayList <Neuron_ALife> an ArrayList with the input neurons
     * @param c Int_ALife_Creature the creature owner of this neuron
     * @param action Mind_ALife.Action the action that this neuron will do
     */
    public Out_Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c, Mind_ALife.Action action){
        //Checks
        super(ns,c);
        
        this.action = action;   
    } // public Out_Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c, Mind_ALife.Action action)

    /**
     * private Out_Neuron_ALife(Out_Neuron_ALife n)
     * 
     * private constructor for copy pr dupe a neuron
     * @param n Out_Neuron_ALife the neuron to copy
     */
    protected Out_Neuron_ALife(Out_Neuron_ALife n) throws IllegalArgumentException{
        super(n);
        this.action = n.action;
        /*
        this.creature = n.creature;
        this.mind = n.mind;
        if (n.output == null ) this.output = null; 
        else this.output = Double.valueOf(n.output);
        this.u = n.u;
        this.weights = new ArrayList<Double>();
        this.inputs = new ArrayList <Neuron_ALife>();

        if (this.mind.allNeurons.size() - this.mind.outputNeurons.size() != n.inputs.size()){
            //Diferent number of inputs neurons
            int breackpoint = 1;
            throw new IllegalArgumentException("Out_Neuron_ALife: Neuron_ALife(Neuron_ALife n) diferent number of inputs");
        }else{
            for (int i = 0; i <this.mind.allNeurons.size() - this.mind.outputNeurons.size(); i++){
                this.inputs.add(this.mind.allNeurons.get(i));
                this.weights.add(Double.valueOf(n.weights.get(i))); //test if its new or copy
            }
        }

        */
        //Any more??
    } // private Out_Neuron_ALife(n)

    // Public Methods and Fuctions ==============
    
    /**
     * public static Out_Neuron_ALife dupeNeuron_ALife(Out_Neuron_ALife n)
     * 
     * Static method to dupe a neuron
     * @param n Out_Neuron_ALife the neuron to dupe
     * @return Out_Neuron_ALife the new neuron
     */
    public static Out_Neuron_ALife dupeNeuron_ALife(Out_Neuron_ALife n){
        if (n == null) return null;
        Out_Neuron_ALife newN = new Out_Neuron_ALife(n);
        return newN;
    }

    /**
     * public static boolean isBiggerThan(Out_Neuron_ALife n1, Out_Neuron_ALife n2)
     * 
     * Check if the first neuron is bigger than the second
     * @param n1 Out_Neuron_ALife the first neuron
     * @param n2 Out_Neuron_ALife the second neuron
     * @return boolean true if the first neuron is bigger than the second
     */
    public static boolean isBiggerThan(Out_Neuron_ALife n1, Out_Neuron_ALife n2){
        return (n1.action.ordinal() > n2.action.ordinal());
    } // End public static boolean isBiggerThan(Input_Neuron_ALife n1, Input_Neuron_ALife n2)


    /**
     * public void reset()
     * 
     * Overrided Reset the neuron from Neuron_ALife
     * @param   None
     * @return  None
     * 
     */
    /*
    @Override
    public void reset(){
        //synchronized (output){
            output = null;
        //}
    }
    */

    /**
     * public double activation()
     * 
     * Overrided activation method from Neuron_ALife
     * @param   None
     * @return  double the output value of the neuron from 0 .. 1
     */
    @Override
    public double activation(){
        if (output != null) return output;
        //For test BIRTH detection
        if (this.action ==  Mind_ALife.Action.REPRODUCE){
            int breack = 1;
            if (this.creature.getReproductable()){
                breack = 2;
            }
        }
        // End test
        int i = 0;
        double sum = u;
         for(Neuron_ALife n:inputs){
            //for test
            MultiTaskUtil.threadMsg("("+this.neuron_ID+")Neuron_ALife.activation() n = "+n.neuron_ID); 

            if (n!= this){
                //for test - First time run may be weights unnormalized
                double aux = n.activation();
                double aux2 = weights.get(i);
                sum += aux * aux2;
                //End test the good is next line
                //sum += n.activation() * weights.get(i); //on when test out
            } // End if (n!= this)
        }
        output = sum;
        return sum;
    }
    
    /**
     * public void updateLearn(Double enhanced, Double change)
     * 
     * Overrided updateLearn method from Neuron_ALife, udate the neuron weights and bias 
     * based in enhanced and change
     * @param   weightupdate Double the enhanced value
     * @param   uupdate Double the change value
     * @param   change Double the change value
     * @return  None
     */
    @Override
    public void updateLearn(Double weightupdate, Double uupdate, Double change){
        // Based in back propagation algorithm
        super.updateLearn(weightupdate, uupdate, change);
        /*
        if (enhanced == null || change == null || enhanced == 0L || change == 0L) return;
        this.u += u*enhanced * change * Mind_ALife.DEFAULT_u_changeFraction;
        weights.replaceAll(d -> d + d * enhanced * change * Mind_ALife.DEFAULT_u_changeFraction);
        */
        /*
        for (Double d: weights){
            d =d + d*enhanced * change;
            int breackpoint = 1;
        }
        */
    }
    
    // Getter and setters -----------------------

    /**
     * public Mind_ALife.Action getAction()
     * 
     * Get the action of this neuron
     * @param   None
     * @return  Mind_ALife.Action the action of this neuron
     */
    public Mind_ALife.Action getAction(){
        return this.action;
    }
    // Private Methods and Fuctions =============

    // Main if needed --------------------------------------------------------------------
    
    
} // End Class
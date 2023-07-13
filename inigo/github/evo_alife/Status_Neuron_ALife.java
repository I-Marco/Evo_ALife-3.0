package inigo.github.evo_alife;

import java.util.*;

/**
 * public class Status_Neuron_ALife extends Neuron_ALife
 * 
 * Special type of neuron that can be used to store a status of the creature or mind
 * @author Iñigo Marco 
 * @version 19-06-2023
 */
public class Status_Neuron_ALife extends Neuron_ALife {
    public static final double DEFAULT_NEURON_STATUS = 0.0;
    
    double neuron_status = DEFAULT_NEURON_STATUS;
    
    // Constructors ============================
    /**
     * public Status_Neuron_ALife()
     * 
     * Empty constructor
     */
    public Status_Neuron_ALife() throws IllegalArgumentException {
        super();
        neuron_status = 0.0;
    } // End public Status_Neuron_ALife()

    /**
     * public Status_Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c) throws IllegalArgumentException
     * 
     * Constructor with all datas
     * @param ns ArrayList <Neuron_ALife> an ArrayList with the input neurons
     * @param c  Int_ALife_Creature the creature owner of this neuron
     */
    public Status_Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c) throws IllegalArgumentException {
        super(ns, c);
        neuron_status = DEFAULT_NEURON_STATUS;
    } // End public Status_Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c)

    /**
     * public protected Status_Neuron_ALife(n)
     * 
     * Constructor to copy or dupe a neuron
     * @param n Status_Neuron_ALife the neuron to copy
     */
    protected Status_Neuron_ALife (Status_Neuron_ALife n) throws IllegalArgumentException{
        super(n);
        this.neuron_status = n.neuron_status;
    } // End public Status_Neuron_ALife (Status_Neuron_ALife n)

    /**
     * public double activation()
     * 
     * Calculate the activation of the neuron
     * @param  None
     * @return double the activation value of the neuron 0..1
     */
    @Override
    public double activation(){
        this.lockNeuron.lock();
        try{
            if (output != null) return output;
            this.output = Double.valueOf(this.neuron_status);
            return this.output;
        }finally{
            this.lockNeuron.unlock();
        }
    } // End public double activation()}

    /**
     * public void reset()
     * 
     * Reset the neuron
     * @param None
     * @return None
     */
    public void reset(){
        //Status neurons are not reseted. They evaluate their status value in reset()
        this.lockNeuron.lock();
        try{
            if (this.neuron_ID == -1) {
                int breackpoint = 1;
                MultiTaskUtil.threadMsg("From Neuron_ALife.reset() this.neuron_ID == -1 ");
            }
            //End test

            int i = 0;
            double sum = 0;
            for(Neuron_ALife n:inputs){
                sum += n.activation() * weights.get(i);
            } // End for(Neuron_ALife n:inputs) All inputs are added
            this.neuron_status = this.neuron_status * this.getU() + sum * (1 - this.getU()); //u in this type of neuron is the stautus update rate
            output = this.neuron_status;
        } finally {
            this.lockNeuron.unlock();
        }
    } // End public void reset()
  
    /**
     * public Status_Neuron_ALife dupeNeuron_ALife()
     * 
     * Method to dupe a neuron
     * @param None
     * @return Status_Neuron_ALife the new neuron
     */
    public Status_Neuron_ALife dupeNeuron_ALife(){
        this.lockNeuron.lock();
        try{
            Status_Neuron_ALife newN = new Status_Neuron_ALife(this);
            return newN;
        }finally{
            this.lockNeuron.unlock();
        }
        //Status_Neuron_ALife newN = new Status_Neuron_ALife(this);
        //return newN;
    }
} // End public class Status_Neuron_ALife extends Neuron_ALife
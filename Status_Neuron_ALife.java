/**
 * public class Status_Neuron_ALife extends Neuron_ALife
 * 
 * Special type of neuron that can be used to store a status of the creature or mind
 * @author Iñigo Marco 
 * @version 19-06-2023
 */
public class Status_Neuron_ALife extends Neuron_ALife {
    double neuron_status = 0.0;
    
    /**
     * public double activation()
     * 
     * Calculate the activation of the neuron
     * @param  None
     * @return double the activation value of the neuron 0..1
     */
    @Override
    public double activation(){
        int i = 0;//Psoble checkeo nully empty 
        double sum = u;
        for(Neuron_ALife n:inputs){
            if (n.getOutput() != null) {
                sum += n.getOutput() * weights.get(i);
            } else {
                sum += n.activation() * weights.get(i);
            }
        }
        this.neuron_status = this.neuron_status * this.u + sum * (1 - this.u); //u in this type of neuron is the stautus update rate
        output = this.neuron_status;
        return this.neuron_status;
    } // End public double activation()}

} // End public class Status_Neuron_ALife extends Neuron_ALife
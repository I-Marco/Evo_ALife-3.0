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
     double u = Mind_ALife.DEFAULT_u;
     
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
    
    public Out_Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c, Mind_ALife.Action action){
        //Checks
        super(ns,c);
        
        /*
        if (c==null) return; //(For moment we dont contemps unowned neurons
        if (ns == null) return; else if (ns.isEmpty()) return;
        //double u = Mind_ALife.DEFAULT_u;
        //Double output = null; 
        creature = c;
        inputs = new ArrayList <Neuron_ALife>();
        weights = new ArrayList<Double>();
        for(Neuron_ALife n: ns){
            inputs.add(n);
            weights.add(Mind_ALife.DEFAULT_Weight);
        }
        */
        this.action = action;   
    }
    // Public Methods and Fuctions ==============
    
    @Override
    public void reset(){
        //synchronized (output){
            output = null;
        //}
    }
    
    public double activation(){
        //For test
        if (this.action ==  Mind_ALife.Action.REPRODUCE){
            int breack = 1;
            if (this.creature.getReprouctable()){
                breack = 2;
            }
        }
        
        
        int i = 0;
        double sum = u;
        for(Neuron_ALife n:inputs){
            if (n.getOutput() != null) {
                //for test
                double aux = n.getOutput();
                double aux2 = weights.get(i);
                sum = aux * aux2;
                //sum += n.getOutput() * weights.get(i); //on when test out
            } else {
                double aux = n.activation();
                double aux2 = weights.get(i);
                sum = aux * aux2;
                //sum += n.activation() * weights.get(i); //on when test out
            }
        }
        output = sum;
        return sum;
    }
    
    public void updateLearn(Double enhanced, Double change){
        // En funcion de enhanced y change el peso mejora. Debo ACOTARLOS
        // Based in back propagation algorithm
        this.u += u*enhanced * change;
        for (Double d: weights){
            d += d*enhanced * change;
        }
    }
    
    // Getter and setters
    public Double getOutput(){
        return output;
    }
    
    public Mind_ALife.Action getAction(){
        return this.action;
    }
    // Private Methods and Fuctions =============

    // Main if needed --------------------------------------------------------------------
    
    
} // End Class
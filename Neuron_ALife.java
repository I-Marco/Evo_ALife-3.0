import java.util.*;

/**
 * Write a description of class Neuron_Alife here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Neuron_ALife
{
    // Fields ----------------------------------------------------------------------------
     double u = Mind_ALife.DEFAULT_u;
     ArrayList <Neuron_ALife> inputs;
     ArrayList<Double> weights;
     Double output = null; 
     
     Int_ALife_Creature creature = null;
     
     // type in mid out
     
    // Methods ---------------------------------------------------------------------------
    // Constructors ============================
    // necesitamos varios constructores un por defecto, otro que marque las entradas....
    // .... y otro que mezcle varias redes
    public Neuron_ALife() {}
    
    public Neuron_ALife(ArrayList <Neuron_ALife> ns, Int_ALife_Creature c){
        //Checks
        if (c==null) return; //(For moment we dont contemps unowned neurons
        if (ns == null) return; else if (ns.isEmpty()) return;
        //double u = Mind_ALife.DEFAULT_u;
        //Double output = null; 
        creature = c;
        this.inputs = new ArrayList <Neuron_ALife>();
        this.weights = new ArrayList<Double>();
        for(Neuron_ALife n: ns){
            inputs.add(n);
            weights.add(Mind_ALife.DEFAULT_Weight);
        }                
    }
    // Public Methods and Fuctions ==============
    
    public void reset(){
        //synchronized (output){
            output = null;
        //}
    }
    
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
    
    // Private Methods and Fuctions =============

    // Main if needed --------------------------------------------------------------------
    
    
} // End Class
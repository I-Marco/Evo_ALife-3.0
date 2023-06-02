import java.util.*;

/**
 * Write a description of class Neuron_Alife here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Reproductable_Neuron_ALife extends Neuron_ALife
{
    // Fields ----------------------------------------------------------------------------
     // type in mid out
     
    // Methods ---------------------------------------------------------------------------
    // Constructors ============================
    // necesitamos varios constructores un por defecto, otro que marque las entradas....
    // .... y otro que mezcle varias redes
    public Reproductable_Neuron_ALife(Int_ALife_Creature c){
        //Checks
        super();
        if (c==null) {
            return; //(For moment we dont contemps unowned neurons
        }
        this.creature = c;
        //if (ns == null) return; else if (ns.isEmpty()) return;
        //double u = Mind_ALife.DEFAULT_u;
        //Double output = null; 
        //creature = c;
        //inputs = new ArrayList <Neuron_ALife>();
        //weights = new ArrayList<Double>();
        //for(Neuron_ALife n: ns){
        //    inputs.add(n);
        //    weights.add(Mind_ALife.DEFAULT_Weight);
        //}                
    }
    // Public Methods and Fuctions ==============
    
    @Override
    public double activation(){
        if (this.output != null) return output;
        
        output = Mind_ALife.FALSE_in_double;
        if (creature.getReproductable()) output = Mind_ALife.TRUE_in_double;
        return output; //this.output" is null" ?????
    }
    
    @Override
    public void updateLearn(Double enhanced, Double change){
        // En funcion de enhanced y change el peso mejora. Debo ACOTARLOS
        // Based in back propagation algorithm
    }
    
    // Getter and setters

    // Private Methods and Fuctions =============

    // Main if needed --------------------------------------------------------------------
    
    
} // End Class
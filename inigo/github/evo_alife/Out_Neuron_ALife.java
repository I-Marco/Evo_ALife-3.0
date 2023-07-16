package inigo.github.evo_alife;

import java.text.DecimalFormat;
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

     Double statusChangeforecast = null;
     Integer numberOfActivations = 0;     
     
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
        statusChangeforecast = null;
        numberOfActivations = 0;   
        
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
        statusChangeforecast = null;
        numberOfActivations = 0;   
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
     * public Out_Neuron_ALife dupeNeuron_ALife()
     * 
     * Copy or dupe a neuron
     * @param   None
     * @return  Out_Neuron_ALife the new neuron
     */
    public Out_Neuron_ALife dupeNeuron_ALife(){
        this.lockNeuron.lock();
        statusChangeforecast = null;
        numberOfActivations = 0;   
        try{
            Out_Neuron_ALife newN = new Out_Neuron_ALife(this);
            return newN;
        }finally{
            this.lockNeuron.unlock();
        }
    }// End public Out_Neuron_ALife dupeNeuron_ALife()

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
     * public double activation()
     * 
     * Overrided activation method from Neuron_ALife
     * @param   None
     * @return  double the output value of the neuron from 0 .. 1
     */
    @Override
    public double activation(){
        this.lockNeuron.lock();
        try{
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
            double sum = getU();
             for(Neuron_ALife n:inputs){
                //for test
                //MultiTaskUtil.threadMsg("("+this.neuron_ID+")Neuron_ALife.activation() n = "+n.neuron_ID); 

                if (n!= this){
                    //for test - First time run may be weights unnormalized
                    double aux = n.activation();
                    double aux2 = weights.get(i);
                    sum += aux * aux2;
                    int breackpoint = 1;
                    //End test the good is next line
                    //sum += n.activation() * weights.get(i); //on when test out
                } // End if (n!= this)
                i++;
            }
            output = sum;
            return sum;
        }finally{
            this.lockNeuron.unlock();
        }
    } // End public double activation()
    
    /**
     * public void updateForecast(Double statusChange)
     * 
     * Update the forecast of this neuron
     * @param statusChange Double the status change of the creature
     * @return  None
     */
    public void updateForecast(Double statusChange){
        this.lockNeuron.lock();
        try{
            Integer num = this.getNumberOfActivations();
            if (num == null) {this.setNumberOfActivations(Integer.valueOf(1));}
            Double auxForecast = this.getStatusChangeforecast();
            if (auxForecast == null) {auxForecast = statusChange;}
            auxForecast = (auxForecast * num + statusChange) / (num + 1);
            num++;
            this.setStatusChangeforecast(auxForecast);
            this.setNumberOfActivations(num);
        } finally {
            this.lockNeuron.unlock();
        }
    } // End public void updateForecast(Double statusChange)


    // Getter and setters -----------------------

    /**
     * public Mind_ALife.Action getAction()
     * 
     * Get the action of this neuron
     * @param   None
     * @return  Mind_ALife.Action the action of this neuron
     */
    public Mind_ALife.Action getAction(){
        this.lockNeuron.lock();
        try{
            return this.action;
        }finally{
            this.lockNeuron.unlock();
        }
        //return this.action;
    }

    /**
     * public void setStatusChangeforecast(Double d)
     * 
     * Set the statusChangeforecast of this neuron
     * @param d Double the new statusChangeforecast of this neuron
     * @return  None
     */
    public void setStatusChangeforecast(Double d){
        this.lockNeuron.lock();
        try{
            this.statusChangeforecast = d;
        }finally{
            this.lockNeuron.unlock();
        }
    } // End public void setStatusChangeforecast(Double d)

    /**
     * public Double getStatusChangeforecast()
     * 
     * Get the statusChangeforecast of this neuron
     * @param   None
     * @return  Double the statusChangeforecast of this neuron
     */
    public Double getStatusChangeforecast(){
        this.lockNeuron.lock();
        try{
            return this.statusChangeforecast;
        }finally{
            this.lockNeuron.unlock();
        }
    } // End public void setStatusChangeforecast(Double d)

    /**
     * public void setNumberOfActivations(Integer i)
     * 
     * Set the numberOfActivations of this neuron
     * @param   i Integer the new numberOfActivations of this neuron
     * @return  None
     */
    public void setNumberOfActivations(Integer i){
        this.lockNeuron.lock();
        try{
            this.numberOfActivations = i;
        }finally{
            this.lockNeuron.unlock();
        }
    } // End public void setNumberOfActivations(Integer i)

    /**
     * public Integer getNumberOfActivations()
     * 
     * Get the numberOfActivations of this neuron
     * @param   None
     * @return  Integer the numberOfActivations of this neuron
     */
    public Integer getNumberOfActivations(){
        this.lockNeuron.lock();
        try{
            return this.numberOfActivations;
        }finally{
            this.lockNeuron.unlock();
        }
    } // End public void setNumberOfActivations(Integer i)

/**
     * public void updateLearn(Double enhanced, Double change)
     * 
     * Update the neuron weights and u
     * @param weightupdate  - neuron parameters change factor
     * @param uupdate       - u change
     * @param stChange    - status change
     * @return None
     */
    public void updateLearn(Double weightupdate, Double uupdate, Double stChange){
        super.updateLearn(weightupdate, uupdate, stChange);
        if (weightupdate == null || stChange == null || weightupdate == 0L || stChange == 0L) return;
        if (this.inputs == null || this.inputs.isEmpty()) return; //No inputs neurons
        //u modificación por decidir
        //weights los que aportan mas de la media mejoran un x% y luego se acota para que la suma total de pesos sea 1
        double changeValue = weightupdate * stChange;
        lockNeuron.lock();
        try{
            for(int i = 0; i < this.inputs.size(); i++){
                Neuron_ALife n = this.inputs.get(i);
                //No propagation for status neurons inputs (endeless loop)
                if (n instanceof Status_Neuron_ALife) continue; 
                n.updateLearn(weightupdate * weights.get(i), uupdate, stChange);
            }
            //u makes some Neurons over others so we need change it carefully or we inactivate some neurons
            //may be when a big update in a single action
            //this.u = this.u + this.u* enhanced * change * Mind_ALife.DEFAULT_u_changeFraction;

            //Detectar si es solo 1 accion
                //modificación = modificación prevista / entrada (OJO 0)
            //si no es 1 acción seguir y modificar u??
        
            for(int i = 0 ; i<weights.size(); i++){
                if (inputs.get(i).getOutput() == null) continue;
                double aux = weights.get(i);
                //adjust each weight acording to the participation in output of the input neuron
                //for test 
                double viewOutputI = inputs.get(i).getOutput();
                double viewFactor1 = changeValue *( (inputs.get(i).getOutput()*aux));
                double viewFactor2 = this.output ;
                double viewFactor3 = changeValue *( (inputs.get(i).getOutput()*aux)/this.output );
                //end test
                aux = aux + changeValue *( (inputs.get(i).getOutput()*aux) /this.output ); 
                weights.set(i, aux);
            }
        
            double[] aux = this.weights.stream().mapToDouble(Double::doubleValue).toArray();
            //double mean = ALifeCalcUtil.mean(aux);
            //for (int i = 0; i < aux.length; i++){
            //    if (aux[i] > mean) aux[i] = aux[i] + aux[i]* changeValue * Mind_ALife.DEFAULT_u_changeFraction;  
            //    else aux[i] = aux[i] + aux[i]* changeValue * Mind_ALife.DEFAULT_Weight_changeFraction * Mind_ALife.DEFAULT_weight_changeUnderFraction;
            //}
            //ALifeCalcUtil.multiplyArrayByCte(null, chageValue)
            aux = ALifeCalcUtil.normalizeArrayToTotal_1(aux);

            ArrayList <Double> auxAL = new ArrayList <Double>();
            Arrays.stream(aux).forEach(value -> auxAL.add(value));
            this.weights = auxAL;
        } finally {
            lockNeuron.unlock();
        }    
    } // End public void updateLearn(Double enhanced, Double change)

    /**
     * public ArrayList<String> makeNeuronReport(ArrayList<String> rep)
     * 
     * Returns a report of the neuron
     * @param rep ArrayList<String> the report to add the neuron report
     * @return ArrayList<String> the report of the neuron
     */
    public ArrayList<String> makeNeuronReport(ArrayList<String> rep){
        //rep.add("NeuroSep");
        DecimalFormat df = new DecimalFormat("0.############");;
        rep.add(String.valueOf(this.neuron_ID));
        rep.add("OutputNeuron"+this.getAction());
        if (this.output == null){
            rep.add("null");
        }else {
            //String numeroFormateado = df.format(numero);
            rep.add(df.format(this.output.doubleValue()));
        }
        rep.add(String.valueOf(this.u));
        if (this.statusChangeforecast != null) rep.add(df.format(this.statusChangeforecast.doubleValue()));
        else rep.add("null");
        rep.add(String.valueOf(this.numberOfActivations));
        rep.add(String.valueOf(this.action));
        for (Double w: this.weights){
            rep.add(String.valueOf(w));
        }
        return rep;
    } // End public ArrayList<String> makeNeuronReport(ArrayList<String> rep)

    /**
     * public ArrayList<String> makeNeuronSORTReport(ArrayList<String> rep)
     * 
     * Returns a report of the neuron for SORT
     * @param  rep  ArrayList<String> the report to add the neuron report
     * @return ArrayList<String> the report of the neuron
     */
    public ArrayList<String> makeNeuronSORTReport(ArrayList<String> rep){
        rep.add("OutN."+this.getAction());
        return rep;
    } // End public ArrayList<String> makeNeuronSORTReport(ArrayList<String> rep)




    // Private Methods and Fuctions =============

    // Main if needed --------------------------------------------------------------------
    
    
} // End public class Out_Neuron_ALife extends Neuron_ALife
import java.util.*;

/**
 * Evo_ALife project. ALife_Specie class.
 * The specie of the creatures.
 * 
 * @author Iñigo Marco 
 * @version (24-05-2023)
 */


public class ALife_Specie
{
    
    // Fields ----------------------------------------------------------------------------
    private long specieIdNumber;
    private long numberOfCreatures = 0;
    private double medTamComplex = 0;
    private ArrayList<Long> reMutateCreation = new ArrayList<Long>();
    private ArrayList<Long> reExtintion = new ArrayList<Long>();
    private Int_ALife_Creature representativeCreature = null;
    
    // Methods ---------------------------------------------------------------------------
    // Construcotors ============================

    /**
     * public ALife_Specie(long number, long tam, long time)
     * Constructor of the class
     *  @param   - long, The number of the specie
     *  @param   - long, The medium size of the creatures
     *  @param   - long, The time of creation of the specie
     *  @return  - None
     */
    private ALife_Specie(Int_ALife_Creature c, double tam, long time){
        //Asegurarse que no existe ya la especie. Si existe añadir recreación (reMutateCreation) y ya
        this.specieIdNumber = c.getEnv_ALife().getNewSpecieIdNumber();
        this.numberOfCreatures = 1;
        this.medTamComplex = tam;
        //timeOfCreation = time;
        this.reMutateCreation.add(time);
        if ( c instanceof Active_ALife_Creature) this.representativeCreature = Active_ALife_Creature.dupeCreature((Active_ALife_Creature)c);
        else this.representativeCreature = c;//mejor clonarlo
        //add to  species list
    } // End public ALife_Specie(long number, long tam, long time)


    // Public Methods and Fuctions ==============
    //Getter and Setters ------------------------

    /**
     * public void setNumberOfCreatures(long number)
     * Set the number of creatures of the specie
     *   @param   - long, The number of creatures
     *   @return  - None
     */
    public void setNumberOfCreatures(long number){
        numberOfCreatures = number;
    }// End public void setNumberOfCreatures(long number)

    /**
     * public long getNumberOfCreatures()
     * Get the number of creatures of the specie
     *   @param   - None
     *   @return  - long, The number of creatures
    */
    public long getNumberOfCreaturesInSpecie(){
        return numberOfCreatures;
    }// End public long getNumberOfCreatures()

    /**
     * public void setMedTamComplex(long number)
     * Set the medium size of the creatures of the specie
     *  @param   - long, The medium size of the creatures
     * @return  - None
     */
    public void setMedTamComplex(long number){
        medTamComplex = number;
    } // End public void setMedTamComplex(long number)

    /**
     * public long getMedTamComplex()
     * Get the medium size of the creatures of the specie
     *   @param   - None
     *   @return  - long, The medium size of the creatures
     */
    public double getMedTamComplex(){
        return medTamComplex;
    } // End public long getMedTamComplex()

    /**
     * public long getSpecieIdNumber()
     * Get the number of the specie
     *  @param   - None
     * @return  - long, The number of the specie
     */
    public long getSpecieIdNumber(){
        return specieIdNumber;
    } // End public long getSpecieIdNumber()
    
     /**
     * public long getTimeOfCreation()
     * Get the time of creation of the specie 
     * @param   - None
     * @return  - long, The time of creation of the specie
     
    public long getTimeOfCreation(){
        return timeOfCreation;
    } // End public long getTimeOfCreation()

    
     * public long setTimeOfCreation()
     * Set the time of creation of the specie
     * @param   - long, The time of creation of the specie
     * @return  - None
    
    public void setTimeOfCreation(Long time){
        timeOfCreation = time; 
    } // End public void setTimeOfCreation(long time)
    */
    
    /**
     * public void setRepresentativeCreature(Int_ALife_Creature c)
     * Set the representative creature of the specie
     * @param   - Int_ALife_Creature, The representative creature of the specie
     * @return  - None
     */
    public void setRepresentativeCreature(Int_ALife_Creature c){
        representativeCreature = c;
    } // End public void setRepresentativeCreature(Int_ALife_Creature c)  

    /**
     * public Int_ALife_Creature getRepresentativeCreature()
     * Get the representative creature of the specie
     * @param   - None
     * @return  - Int_ALife_Creature, The representative creature of the specie
     */ 
    public Int_ALife_Creature getRepresentativeCreature(){
        return representativeCreature;
    } // End public Int_ALife_Creature getRepresentativeCreature()

     //End of Getter and Setters -----------------
    
    /**
     * public synchronized long addCreature(Int_ALife_Creature c)
     * Add a creature to the specie
     * @param   - Int_ALife_Creature, The creature to add
     * @return  - long, The number of creatures of the specie after the addition
     */
    public synchronized long addCreatureToSpecie(Int_ALife_Creature c){
        if (c == null) return this.numberOfCreatures;
        if (c.getEnv_ALife() == null) return this.numberOfCreatures;
        //for test c.getSpecieIdNumber()
        long t1 = c.getSpecieIdNumber();
        long t2 =specieIdNumber;
        //End for test

        if (c.getSpecieIdNumber() != specieIdNumber) return this.numberOfCreatures;
        
        //There are 0 creatures new creation of the specie by mutation
        if (numberOfCreatures == 0){
            if (this.reMutateCreation == null) reMutateCreation = new ArrayList<Long>();
            reMutateCreation.add(c.getEnv_ALife().getTime());
        }
        numberOfCreatures++;
        c.setSpecieIdNumber(this.specieIdNumber); //Redundant
        return numberOfCreatures;
        // reevaluate the the this.representativeCreature atributes?
    } // End public void addCreature(c)

     /**
     * public synchronized long removeCreature(Int_ALife_Creature c)
     * Remove a creature to the specie
     * @param   - Int_ALife_Creature, The creature to remove
     * @return  - long, The number of creatures of the specie after the removal
     */
    public synchronized long removeCreatureFromSpecie(Int_ALife_Creature c){
        if (c == null) return this.numberOfCreatures;
        if (c.getEnv_ALife() == null) return this.numberOfCreatures;
        if (c.getSpecieIdNumber() != specieIdNumber) return this.numberOfCreatures;
        if (numberOfCreatures == 0) return this.numberOfCreatures;
        numberOfCreatures--;// In specie 
        if (numberOfCreatures == 0){
            if (this.reExtintion == null) reExtintion = new ArrayList<Long>();
            reExtintion.add(c.getEnv_ALife().getTime());
        }
        return numberOfCreatures;
        // reevaluate the the this.representativeCreature atributes? 
    } // End public void removeCreature(c)

    /**
     * public static ALife_Specie createSpecie(Int_ALife_Creature ,long specieId)
     * Create a new specie
     * @param   - Int_ALife_Creature, The creature to add
     * @param   - long, The number of the specie
    */
    public static ALife_Specie createSpecie(Int_ALife_Creature c, long specieId){
        if (c == null) return null;
        if (c.getEnv_ALife() == null) return null;
        //    private ALife_Specie(Int_ALife_Creature c, long tam, long time){
        //Dupe the creature FALTA
        c.setSpecieIdNumber(specieId);
        return new ALife_Specie(c, Int_ALife_Creature.evaluateTamComplex(c), c.getEnv_ALife().getTime());
    } // End public static ALife_Specie createSpecie(Int_ALife_Creature c, long specieId)

    // Private Methods and Fuctions =============
    
    
 
} //End of ALife_Specie class
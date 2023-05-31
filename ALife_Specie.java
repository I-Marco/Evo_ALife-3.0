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
    private long medTamComplex = 0;
    private Long timeOfCreation = null;
    private ArrayList reMutateCreation = new ArrayList<Long>();
    
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
    public ALife_Specie(Int_ALife_Creature c, long tam, long time){
        //Asegurarse que no existe ya la especie. Si existe añadir recreación (reMutateCreation) y ya
        specieIdNumber = c.getEnv_ALife().getSpecieIdNumber();
        numberOfCreatures = 1;
        medTamComplex = tam;
        timeOfCreation = time;
        reMutateCreation.add(time);
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
    public long getNumberOfCreatures(){
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
    public long getMedTamComplex(){
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
     */ 
    public long getTimeOfCreation(){
        return timeOfCreation;
    } // End public long getTimeOfCreation()

    /**
     * public long setTimeOfCreation()
     * Set the time of creation of the specie
     * @param   - long, The time of creation of the specie
     * @return  - None
     */
    public void setTimeOfCreation(Long time){
        timeOfCreation = time; 
    } // End public void setTimeOfCreation(long time)

    //End of Getter and Setters -----------------

    // Private Methods and Fuctions =============
    
    
 
} //End of ALife_Specie class
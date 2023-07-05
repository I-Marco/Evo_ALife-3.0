package inigo.github.evo_alife;

import java.util.*;

/**
 * Write a description of class ALife_Specie here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ALife_Species
{
    public static double Max_Distante = 1; //The maximum distance between a creature of same specie
    public static double SPECIE_DISTANCE = 0.05 * Max_Distante; //The maximum distance between a creature of same specie
    // Fields ----------------------------------------------------------------------------
    Env_ALife env = null;
    long numberOfDifferentSpecies = 0;
    long numberOfDifferentActiveSpecies = 0;
    long lastSpecieNumberID = 0;
    ArrayList<ALife_Specie> speciesList = new ArrayList<ALife_Specie>();
    
    // Methods ---------------------------------------------------------------------------
    // Constructors  ============================
    /**
     * public ALife_Species(Env_ALife e)
     * Constructor of the class
     * @param e Env_ALife, the environment  
     */
    public ALife_Species(Env_ALife e){
        env = e;
    } // End public ALife_Species(Env_ALife e)

    // Public Methods and Fuctions ==============
    
    //Getter and Setters

    /**
     * public long getNumberOfDifferentSpecies()
     * Get the number of different species with or without creatures alive
     * @return long, the number of different species
     */ 
    public long getNumberOfDifferentSpecies(){
        //for test
        if (speciesList.size() != numberOfDifferentSpecies){
            MultiTaskUtil.threadMsg("Error en el número de especies. Contador distinto que longitud de la lista");  
        }
        //end for test
        return numberOfDifferentSpecies;  
    } // End public long getNumberOfDifferentSpecies()

    /**
     * public long getNumberOfDifferentActiveSpecies()
     * Get the number of different active species, the species with at least one creature alive
     * @return long, the number of different active species
     */
    public long getNumberOfDifferentActiveSpecies(){
        //for test
        int cont = 0;
        for (ALife_Specie s: speciesList){
            if (s.getNumberOfCreaturesInSpecie() > 0){
                cont++;
            }
        }
        if (cont != numberOfDifferentSpecies){
            MultiTaskUtil.threadMsg("Error en el número de especies. Contador distinto que longitud de la lista");  
        }
        //end for test
        return cont;
    } // End public long getNumberOfDifferentActiveSpecies()

    /**
     * public long getLastSpecieNumberID()
     * Get the last specie number ID
     * @return long, the last specie number ID
     */
    public long getLastSpecieNumberID(){
        return lastSpecieNumberID;
    } // End public long getLastSpecieNumberID()

    /**
     * public ALife_Specie getSpecieList(int i)
     * Get the specie in the position i of the list
     * @param i int, the position of the specie
     * @return ALife_Specie, the specie in the position i of the list
     */
    public ALife_Specie getSpecieList(int i){
        if (speciesList == null) return null;
        if (i < 0 || i >= speciesList.size()) return null;
        return speciesList.get(i);
    } // End public ALife_Specie getSpecieList(int i)

    /**
     * public Env_ALife getEnv_ALife()
     * Get the environment
     * @return Env_ALife, the environment
     */
    public Env_ALife getEnv_ALife(){
        return env;
    } // End public Env_ALife getEnv_ALife()

    /**
     * public void setEnv_ALife(Env_ALife e)
     * Set the environment
     * @param e Env_ALife, the environment
     */
    public void setEnv_ALife(Env_ALife e){
        env = e;
    } // End public void setEnv_ALife(Env_ALife e)

    // End Getter and Setters

    /**
     * public void addCreature(Int_ALife_Creature c)
     * Add a creature to the right specie or create a new one
     * @param c Int_ALife_Creature
     * @return None
     */
    public void addCreatureToSpecies(Int_ALife_Creature c){
        if (c == null) return;
        //Compare to existing species
        Long specieIdNumber = findSpecieForCreature(c);
        if (specieIdNumber == null){
            //Create new specie 
            this.createNewSpecie(c);
            //c.setSpecieIdNumber(specieIdNumber); Asigned in addSpecie
        } else {
            //Add creature to specie
            ALife_Specie s = this.speciesList.get(specieIdNumber.intValue() - 1);
            c.setSpecieIdNumber(specieIdNumber);
            s.addCreatureToSpecie(c);
            c.setSpecieIdNumber(s.getSpecieIdNumber()); //Redundant ??
        }
    } // End public void addCreature(Int_ALife_Creature c)

    /**
     * public void removeCreature(Int_ALife_Creature c)
     * Remove a creature from the right specie
     * @param c Int_ALife_Creature  
     * @return None
     */
    public void removeCreature(Int_ALife_Creature c){
        if (c == null) return;
        try{
            ALife_Specie s = this.speciesList.get((int)c.getSpecieIdNumber() - 1);
            if (s.removeCreatureFromSpecie(c) == 0){
                this.numberOfDifferentActiveSpecies--;
            }
        } catch (Exception e){
            System.out.println("Error removing creature from specie");
        }
    } // End public void removeCreature(Int_ALife_Creature c)

    /**
     * public void addSpecie(Int_ALife_Creature c)
     * Create new specie and add to the list
     * @param c Int_ALife_Creature
     * @return None
     */
    public void createNewSpecie(Int_ALife_Creature c){
        if (c == null) return;
        //We asume that creature has been compared to existing species
        Long specieIdNumber = findSpecieForCreature(c); //temporal for in case of error
        if (specieIdNumber != null && specieIdNumber != -2){ //Error
            MultiTaskUtil.threadMsg("Error adding specie and someone MATCHES");
            return;
        }
        this.lastSpecieNumberID++; //We add first as it starts in 0
        this.numberOfDifferentSpecies++;
        this.numberOfDifferentActiveSpecies++;
        ALife_Specie s = ALife_Specie.createSpecie(c,this.lastSpecieNumberID); //for test
        this.speciesList.add(s); //Auto set the specieIdNumber for creature
        c.setSpecieIdNumber(lastSpecieNumberID);
        // Should we add to creature the specieIdNumber?
    } // End public void addSpecie(Int_ALife_Creature c)
    
    /**
     * public double getDistancetoSpecie(ALife_Specie s, Int_ALife_Creature c)
     * @param s ALife_Specie
     * @param c Int_ALife_Creature
     * @return double, the distance between the specie and the creature
     */
    public static double getDistancetoSpecie(ALife_Specie s, Int_ALife_Creature c){
        if (s == null || c == null) return -1;
        if (s.getRepresentativeCreature() == null) return -1;
        Int_ALife_Creature sp_c= s.getRepresentativeCreature();
        
        return getDistancetoCreature(sp_c, c);
        /*ALifeCalcUtil.arrayDistance(
          ALifeCalcUtil.min_max_Array_Normalization(
            Int_ALife_Creature.serializeCreature(sp_c),
            Active_ALife_Creature.creature_minCaracteristics,
            Active_ALife_Creature.creature_maxCaracteristics
          ),
          ALifeCalcUtil.min_max_Array_Normalization(
            Int_ALife_Creature.serializeCreature(c),
            Active_ALife_Creature.creature_minCaracteristics,
            Active_ALife_Creature.creature_maxCaracteristics
          )
        );
        */
    } // End public double getDistancetoSpecie(ALife_Specie s, Int_ALife_Creature c)

    /**
     * public double getDistancetoCreature(Int_ALife_Creature c1, Int_ALife_Creature c2)
     * 
     * Return the distance between two creatures
     * @param c1 - Int_ALife_Creature
     * @param c2 - Int_ALife_Creature
     * @return double, the distance between the two creatures
     */
    public static double getDistancetoCreature(Int_ALife_Creature c1, Int_ALife_Creature c2){
        double res = ALifeCalcUtil.arrayDistance(
          ALifeCalcUtil.min_max_Array_Normalization(
            //Int_ALife_Creature.serializeCreature(c1),
            c1.serializeCreature(),
            Active_ALife_Creature.creature_minCaracteristics,
            Active_ALife_Creature.creature_maxCaracteristics
          ),
          ALifeCalcUtil.min_max_Array_Normalization(
            //Int_ALife_Creature.serializeCreature(c2),
            c2.serializeCreature(),
            Active_ALife_Creature.creature_minCaracteristics,
            Active_ALife_Creature.creature_maxCaracteristics
          )
        );
        return res;
    } // End public double getDistancetoCreature(Int_ALife_Creature c1, Int_ALife_Creature c2)
     // Private Methods and Fuctions =============
    
    /**
     * private Long findSpecieForCreature(Int_ALife_Creature c)
     * Return the specieIdNumber of the nearest specie to the creature if distance is under Constant
     * @param c Int_ALife_Creature
     * @return Long, the number of specieIdNumber
     */
    private Long findSpecieForCreature(Int_ALife_Creature c){
        if (c == null) return null;
        //Compare to existing species
        double minDistance = SPECIE_DISTANCE;
        Long specieIdNumber = Long.valueOf(-2);
        for (ALife_Specie s: speciesList){
            double distance = getDistancetoSpecie(s, c); //Aboid recalculation of long time function
            if (distance < minDistance){
                minDistance = distance;
                specieIdNumber = s.getSpecieIdNumber();
            }
        }
        if (specieIdNumber == -2) return null;
        return specieIdNumber;
    } // End private Long findSpecieForCreature(Int_ALife_Creature c)

    
    
} // End Class
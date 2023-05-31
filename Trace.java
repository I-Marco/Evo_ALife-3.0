import java.awt.*;

/**
 * Evo_ALife project. 
 * 
 * 
 * @author Iñigo Marco 
 * @version (24-05-2023)
 */

public class Trace
{
    // Fields ----------------------------------------------------------------------------
    double umbral; // Umbral minimo de detección necesario
    Int_ALife_Creature source;
    Point pos;
    // Methods ---------------------------------------------------------------------------
    // Construcotors ============================
    public Trace(double u, Int_ALife_Creature s, Point p){
            umbral = u;
            source = s;
            pos = p;
    } // End public Trace(int u, Int_ALife_Creature s, Point p)
    // End Constructors ========================
    // Public Methods and Fuctions ==============
    // Getter and Setters - - - - - - - - - - - -
    /**
     * public int getUmbral()
     * @param None
     * @return the umbral
     */
    public double getUmbral(){
            return umbral;
    } // End public int getUmb()

    /**
     * public void setUmbral(int u)
     * @param u the umbral to set
     * @return None
     */ 
    public void setUmbral(int u){
            umbral = u; 
    } // End public void setUmb(int u)

    /**
     * public Int_ALife_Creature getSource()
     * @param None
     * @return the source
     */
    public Int_ALife_Creature getSource(){
            return source;
    } // End public Int_ALife_Creature getSource()

    /**
     * public void setSource(Int_ALife_Creature s)
     * @param s the source to set
     * @return None
     */
    public void setSource(Int_ALife_Creature s){
            source = s;
    } // End public void setSource(Int_ALife_Creature s)

    /**
     * public Point getPos()
     * @param None
     * @return the pos
     */
    public Point getPos(){
            return pos;
    } // End public Point getPos()

    /**
     * public void setPos(Point p)
     * @param p the pos to set
     * @return None
     */ 
    public void setPos(Point p){
            pos = p;
    } // End public void setPos(Point p)



    // End Getter and Setters - - - - - - - - - -
    // Private Methods and Fuctions =============
    
    
    // Main if needed --------------------------------------------------------------------
    
    
} // End Class Trace
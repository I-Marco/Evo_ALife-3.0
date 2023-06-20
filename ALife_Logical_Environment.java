import java.util.*;
import java.awt.*;

/**
 * Evo_ALife project. 
 * 
 * 
 * @author Iñigo Marco 
 * @version (24-05-2023)
 */

public class ALife_Logical_Environment 
{
    // Fields ----------------------------------------------------------------------------
    Env_ALife env_ALife;
    ArrayList<Int_ALife_Creature>[][] ocupiers;
    ArrayList<Int_ALife_Creature>[][] observers;
    ArrayList<Trace>[][] traces;
    
    // Methods --------------------------------------------------------------------------
    // Construcotors ============================
    /**
     * public ALife_Logical_Environment(Env_ALife e)
     * 
     * @param e Env_ALife
     */
    public ALife_Logical_Environment(Env_ALife e){
        env_ALife = e;
        //create the arrays for ArrayList elements
        int with = e.getLand().getLandImg().getWidth();
        int height = e.getLand().getLandImg().getHeight();
        ocupiers = new ArrayList[with][height]; // Unchecked assignment No puedo corregirlo
        observers = new ArrayList[with][height];
        traces = new ArrayList[with][height];
        for (int i = 0; i<with; i++) 
                for (int j = 0; j < height; j++){
                    ocupiers[i][j] = new ArrayList<Int_ALife_Creature>();
                    observers[i][j] = new ArrayList<Int_ALife_Creature>();
                    traces[i][j] = new ArrayList<Trace>();
            }
    } // End public ALife_Logical_Environment(Env_ALife e)
    
    // End Construcotors ========================

    // Public Methods and Fuctions ==============

    // Getter and Setters - - - - - - - - - - - -
    
    /**
     * public void getTraces()
     * 
     * @param  None
     * @return None
     */
    public void getTraces(){
            int i;//get all traces around a Point.
    }
    
    // End Getter and Setters - - - - - - - - - -
    
    /**
     * public void addCreature(Int_ALife_Creature c,Point p)
     * 
     * @param c
     * @param p
     */
    public void addCreature(Int_ALife_Creature c,Point p){
        if (c == null || p == null){
            int breakpoint = 1 ;
            MultiTaskUtil.threadMsg("Aborto, falta creature o Point.");
            return; //aborto
        }
        if (p.x < 0 || p.x > (this.ocupiers.length - 1) || p.y < 0 || p.y > (this.ocupiers[0].length - 1)){
            return;
        }
        if (ocupiers[p.x][p.y] == null) {
            ocupiers[p.x][p.y] = new ArrayList<Int_ALife_Creature>(); 
        }
        if (ocupiers[p.x][p.y].isEmpty()){ //Null pointer exception x , y fuera de rango

            ocupiers[p.x][p.y].add(c);
            traces[p.x][p.y].add(c.getCreatureTrace());
            //for faster processing
            int w = c.getEnv_ALife().getEnv_Width();
            int h = c.getEnv_ALife().getEnv_Height();
            int r = c.getDetectionRange();
            
            int x,y;
            //observers updating
            for (int i = p.x - r - 1; i < (p.x +  r + 1); i++)
                for (int j = p.y - r - 1; j < (p.y +  r + 1); j++)
            {   x = (i + w) % w;
                y = (j + h) % h;
                if (r >= p.distance(x, x)){
                    observers[x][y].add(c);
                }else {
                    if (observers[x][y].contains(c)) observers[x][y].remove(c);
                }
                    
            }
                
            //(int u, Int_ALife_Creature s, Point p){
        }
    } // End public void addCreture(Int_ALife_Creature c,Point p)
        
    /**
     * public void removeCreature(Int_ALife_Creature c)
     * 
     * @param c
     */
    public void removeCreature(Int_ALife_Creature c){
        if (c == null) {
            MultiTaskUtil.threadMsg("Fallo, falta creature para borrar.");
            return;
        }
        Point p = c.getPos();
        if (p.x < 0 || p.x > (this.ocupiers.length - 1) || p.y < 0 || p.y > (this.ocupiers[0].length - 1)){
            return;
        } // Out of bounds
        if (ocupiers[p.x][p.y] == null || ocupiers[p.x][p.y].isEmpty()) {
            MultiTaskUtil.threadMsg("Fallo, no hay creature en la posicion.");
            return;
        }
        if (ocupiers[p.x][p.y].contains(c)){
            ocupiers[p.x][p.y].remove(c);
            traces[p.x][p.y].remove(c.getCreatureTrace());
            //for faster processing
            int w = c.getEnv_ALife().getEnv_Width();
            int h = c.getEnv_ALife().getEnv_Height();
            int r = c.getDetectionRange();
            
            int x,y;
            //observers updating
            for (int i = p.x - r - 1; i < (p.x +  r + 1); i++)
                for (int j = p.y - r - 1; j < (p.y +  r + 1); j++)
            {   x = (i + w) % w;
                y = (j + h) % h;
                if (observers[x][y].contains(c)) observers[x][y].remove(c);                    
            }
            //We should notify to observers any way of change? FALTA
        }
    } // End public void removeCreature(Int_ALife_Creature c)

    public void addTrace(Trace c, Point p){
    }
    
    
    public  boolean detectColision(Int_ALife_Creature c, Point p, int radio){
        //int radio = 0;
        boolean colision = false;
        int w = c.getEnv_ALife().getEnv_Width();
        int h = c.getEnv_ALife().getEnv_Height();
        int x = (p.x + w) % w;//int x = (p.x - radio + w) % w;
        int y = (p.y + h) % h;//int y = (p.y - radio + h) % h;
        for (int i = p.x - radio; i <= p.x + radio; i++)
            for (int j = p.y - radio; j <= p.y + radio; j++)
        {   x = (i - radio + w) % w;
            y = (j - radio + h) % h;
            if (this.ocupiers[x][y].size() > 0) colision = true;
        }
        return colision;
    }
    
    public Int_ALife_Creature collisionWith(Int_ALife_Creature c, Point p){
        boolean colision = false;
        Int_ALife_Creature colisioner = null;
        int radio = 1; // May be we have to take in count the creature dimensions Owned food resources and tamcomplex
        int w = c.getEnv_ALife().getEnv_Width();
        int h = c.getEnv_ALife().getEnv_Height();
        int x = (p.x - radio + w) % w;
        int y = (p.y - radio + h) % h;
        for (int i = p.x - radio; i < p.x + radio; i++)
            for (int j = p.y - radio; j < p.y + radio; j++)
        {   x = (i - radio + w) % w;
            y = (j - radio + h) % h;
            if (this.ocupiers[x][y].size() > 0) {
                colision = true;
                colisioner = this.ocupiers[x][y].get(0);
            }
        }
        return colisioner;
    }

    // Private Methods and Fuctions =============
        
} // End Class ALife_Logical_Environment 
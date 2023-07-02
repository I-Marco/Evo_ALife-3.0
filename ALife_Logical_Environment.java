import java.util.*;
import java.awt.*;

/**
 * Evo_ALife project. 
 * 
 * 
 * @author Iñigo Marco 
 * @version (24-05-2023)
 */

public class ALife_Logical_Environment extends Thread
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
    public synchronized void run(){
        //update traces life time
        int width = this.env_ALife.getLand().getLandImg().getWidth();
        int height = this.env_ALife.getLand().getLandImg().getHeight();        
        //ArrayList<Trace>[][] traces;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
        {   
            try{
                synchronized (this.traces){
                    //for test
                    if (this.traces[i][j] == null) {
                        int breakpoint = 1;
                    }
                    for (Trace t : this.traces[i][j]){    
                        if (t != null && t.run() <= 0) this.removeTrace(t, new Point(i,j));
                    }
                }
            }catch (Exception e){
                    MultiTaskUtil.threadMsg("Error en run de trace."+e.getMessage());
            }
        }

    }
    // Getter and Setters - - - - - - - - - - - -
    
    /**
     * public void getTraces()
     * 
     * @param  None
     * @return None
     */
    public synchronized void getTraces(){
            int i;//get all traces around a Point.
    }
    
    // End Getter and Setters - - - - - - - - - -
    
    /**
     * public void addCreature(Int_ALife_Creature c,Point p)
     * 
     * @param c
     * @param p
     */
    public synchronized void addCreatureToLogEnv(Int_ALife_Creature c,Point p){
        //Check
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
        if (ocupiers[p.x][p.y].isEmpty()){ 

            ocupiers[p.x][p.y].add(c);
            this.addTrace(c.getCreatureTrace(), c.getPos());
            //traces[p.x][p.y].add(c.getCreatureTrace());
            //for faster processing
            int w = c.getEnv_ALife().getEnv_Width();
            int h = c.getEnv_ALife().getEnv_Height();
            int r = c.getDetectionRange();
            
            int x,y; 
            //observers updating (self) --> this.addCreature(c, p); detection range + 1 --> clenning old self observer marks
            for (int i = p.x - r - 1; i <= (p.x +  r + 1); i++)
                for (int j = p.y - r - 1; j <= (p.y +  r + 1); j++)
            {   x = (i + w) % w; y = (j + h) % h;
                if (observers[x][y] == null) observers[x][y] = new ArrayList<Int_ALife_Creature>();
                if ((double)r >= p.distance(x, y)){
                    observers[x][y].add(c);
                }else {
                    if (observers[x][y].contains(c)) observers[x][y].remove(c);
                }
                    
            }
                
            //(int u, Int_ALife_Creature s, Point p){
        } // End if (ocupiers[p.x][p.y].isEmpty())
        else {
            if (ocupiers[p.x][p.y].contains(c)) return;
            MultiTaskUtil.threadMsg("Fallo, ya hay creature en la posicion.");
            return;
        }
    } // End public void addCreture(Int_ALife_Creature c,Point p)
        
    /**
     * public void removeCreature(Int_ALife_Creature c)
     * 
     * @param c
     */
    public synchronized void removeCreature(Int_ALife_Creature c){
        //Check
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
            //for test
            ArrayList<Int_ALife_Creature> breakpoint = this.ocupiers[p.x][p.y];
            int breakpoint2 = this.ocupiers[p.x][p.y].size();
            //end for test
            ocupiers[p.x][p.y].remove(c);
            //for test
            breakpoint2 = this.ocupiers[p.x][p.y].size();
            //end for test
            removeTrace(c.getCreatureTrace(),c.getPos());
            //traces[p.x][p.y].removeTrace(c.getCreatureTrace());
            //for faster processing
            int w = c.getEnv_ALife().getEnv_Width();
            int h = c.getEnv_ALife().getEnv_Height();
            int r = c.getDetectionRange();
            
            int x,y;
            //observers updating
            for (int i = p.x - r - 1; i <= (p.x +  r + 1); i++)
                for (int j = p.y - r - 1; j <= (p.y +  r + 1); j++)
            {   x = (i + w) % w;
                y = (j + h) % h;
                if (observers[x][y].contains(c)) {
                    //for test
                    breakpoint = this.ocupiers[p.x][p.y];
                    breakpoint2 = this.ocupiers[p.x][p.y].size();
                    //end for test
                    observers[x][y].remove(c);
                    //for test
                    breakpoint2 = this.ocupiers[p.x][p.y].size();
                    //end for test
                }
            }
            //We should notify to observers any way of change? FALTA
        }
    } // End public void removeCreature(Int_ALife_Creature c)

    /**
     * public void moveCreature(Creature c, int x, int y)
     * 
     * Move creature to new position (x,y), set creature new position and notify to observers
     * @param c Creature to move
     * @param x int 
     * @param y int
     */
    public synchronized void moveCreature(Active_ALife_Creature c, int x, int y){
        if (c == null) {
            MultiTaskUtil.threadMsg("Fallo, falta creature para mover.");
            return;
        }
        //check if the new position is valid
        int w = c.getEnv_ALife().getEnv_Width();
        int h = c.getEnv_ALife().getEnv_Height();
        Point newPos = new Point(
            (x + w) % w,
            (y + h) % h);
        if (this.ocupiers[newPos.x ][newPos.y].size() > 0) {
            //colision
            //for test
            ArrayList<Int_ALife_Creature> breakpoint = this.ocupiers[newPos.x ][newPos.y];
            return; //may be eat or fight
        }
        //Change position in the ocupiers array
        
        //Change position in the observers array
        
        //Change position in the traces array
        this.removeCreature(c);
        this.addCreatureToLogEnv(c, newPos);

        //c.setPos(newPos);
        c.setPos(newPos);
    } // End public void moveCreature(Creature c, int x, int y)
    
    /**
     * public void addTrace(Trace t, Point p)
     * 
     * Add trace to enviroment traces and notify to observers
     * @param t Trace
     * @param p Point
     * @return None
     */
    public synchronized void addTrace(Trace t, Point p){
        //Add trace and avise observers
        //Check
        if (t == null || p == null){
            int breakpoint = 1 ;
            MultiTaskUtil.threadMsg("No podemos añadir rastro, falta creature o Point.");
            return;
        }
        if (this.traces[p.x][p.y] == null) {this.traces[p.x][p.y] = new ArrayList<Trace>();}
        synchronized(this.traces[p.x][p.y]){
            this.traces[p.x][p.y].add(t);
        }
        //notify to observers
        if(this.observers[p.x][p.y] != null){
            for (Int_ALife_Creature o : this.observers[p.x][p.y]){
                synchronized(o){
                    if(t.source != o) o.addDetectedTrace(t);
                }
            }
        }
    } // End public void addTrace(Trace t, Point p)

    /**
     * public void removeTrace(Trace c, Point p)
     * 
     * Remove trace from enviroment traces and notify to observers
     * @param c Trace
     * @param p Point
     * @return None
     */
    public synchronized void removeTrace(Trace c, Point p){
        //Remove trace and avise observers
        //Check
        if (c == null || p == null){
            int breakpoint = 1 ;
            MultiTaskUtil.threadMsg("No podemos borrar rastro, falta creature o Point.");
            return;
        }
        traces[p.x][p.y].remove(c);
        //notify to observers
        if(this.observers[p.x][p.y] != null){
            for (Int_ALife_Creature o : this.observers[p.x][p.y]){
                if(c.source != o) o.removeDetectedTrace(c);
            }
        }
    }// End public void removeTrace(Trace c, Point p)
    
    
    public  synchronized boolean detectColision(Int_ALife_Creature c, Point p, int radio){
        //int radio = 0;
        boolean colision = false;
        int w = c.getEnv_ALife().getEnv_Width();
        int h = c.getEnv_ALife().getEnv_Height();
        int x = (p.x + w) % w;//int x = (p.x - radio + w) % w;
        int y = (p.y + h) % h;//int y = (p.y - radio + h) % h;
        for (int i = p.x - radio; i <= p.x + radio; i++)
            for (int j = p.y - radio; j <= p.y + radio; j++)
        {   x = (i + w) % w;
            y = (j + h) % h;
            if (this.ocupiers[x][y].size() > 0) colision = true;
        }
        return colision;
    }
    
    public synchronized Int_ALife_Creature collisionWith(Int_ALife_Creature c, Point p){
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
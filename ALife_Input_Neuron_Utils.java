import java.awt.Point;
import java.util.ArrayList;

/**
 * ALife_Input_Neuron_Utils.java
 * A utility class to avoid code duplication in the ALife_Input_Neuron classes fon input neurons
 * 
 * @author Iñigo Marco 
 * @version 28-06-2023
 */
public class ALife_Input_Neuron_Utils {

    /**
     * public static int northDistancePtoP(Point p1, Point p2)
     * 
     * Returns the distance between two points in the north direction
     * @param p1 Point the first point
     * @param p2 Point the second point
     * @return int the distance between the two points in the north direction
     */
    public static int northDistancePtoP(Point p1, Point p2){
        return p1.y - p2.y;
    } // End public static int northDistancePtoP(Point p1, Point p2)

    /**
     * public static int eastDistancePtoP(Point p1, Point p2)
     * 
     * Returns the distance between two points in the east direction
     * @param p1 Point the first point
     * @param p2 Point the second point
     * @return int the distance between the two points in the east direction
     */
    public static int eastDistancePtoP(Point p1, Point p2){
        return p2.x - p1.x;
    } // End public static int eastDistancePtoP(Point p1, Point p2)

     
    /**
     * public static Point nearestResourceExpand(Int_ALife_Creature c, int type)
     * 
     * Returns the distance to the nearest resource of the type specified
     * @param c Int_ALife_Creature the creature that owns the neuron
     * @param type int the type of resource to detect
     * @return double the distance to the nearest resource of the type specified
     */
    public static Point nearestResourceExpand(Int_ALife_Creature c, int type){
        //checks
        if (c == null) return null;
        if (type < 0 || type > 2) return null;

        Point pos = c.getPos();
        Point foodPos = null;
        for (int range = 0; range <= c.getDetectionRange(); range++){
            if (foodPos != null) break;
            for (int x = pos.x - range; x <= pos.x + range; x++){
                if (foodPos != null) break;
                for (int y = pos.y - range; y <= pos.y + range; y++){
                    if (foodPos != null) break;
                    int[] food = c.getEnv_ALife().getLand().getNutrientIn(x, y);
                    if (food[type] > 0){
                        foodPos = new Point(x,y);
                    }
                }
            }
        }
        return foodPos;
    } // End public static double nearestResource(Int_ALife_Creature c, int type)
    
    /**
     * public static Point nearestResourceExpand(Int_ALife_Creature c, int type)
     * 
     * Returns the distance to the nearest resource of the type specified
     * @param c Int_ALife_Creature the creature that owns the neuron
     * @param type int the type of resource to detect 0 = red, 1 = green, 2 = blue
     * @return Point the position of nearest resource of the type specified
     */
    public static Point nearestResourceSecuencial(Int_ALife_Creature c, int type){
        //checks
        if (c == null) return null;
        if (type < 0 || type > 2) return null;

        Point pos = c.getPos();
        Point foodPos = null;
        double dist = Double.MAX_VALUE;
        for (int x = pos.x - c.getDetectionRange(); x <= pos.x + c.getDetectionRange(); x++){
            for (int y = pos.y - c.getDetectionRange(); y <= pos.y + c.getDetectionRange(); y++){
                int[] food = c.getEnv_ALife().getLand().getNutrientIn(x, y);
                if (food[type] > 0){
                    if (dist < c.getPos().distance(x, y)){
                        foodPos = new Point(x,y);
                        dist = c.getPos().distance(x, y);
                    }
                }
            }
        }
        return foodPos;
    } // End public static double nearestResource(Int_ALife_Creature c, int type)

    /**
     * public static ArrayList<Int_ALife_Creature> detectedCreatures(Int_ALife_Creature c)
     * 
     * Returns the list of creatures detected by the creature c
     * @param c Int_ALife_Creature the creature that owns the neuron
     * @return ArrayList<Int_ALife_Creature> the list of creatures detected by the creature c
     */
    public static ArrayList<Int_ALife_Creature> detectedCreatures(Int_ALife_Creature c){
        //checks
        if (c == null || c.getDetectedTraces() == null)  return null;

        ArrayList<Int_ALife_Creature> creatures = new ArrayList<Int_ALife_Creature>();
        for (Trace t:c.getDetectedTraces()){
            if(t.source != null && t.source != c && !creatures.contains(t.source)){
                creatures.add(t.source);
            }
        }
        return creatures;
    } // End public static ArrayList<Int_ALife_Creature> detectedCreatures(Int_ALife_Creature c)

    /**
     * public static ArrayList<Int_ALife_Creature> detectedFriendlyCreatures(Int_ALife_Creature c)
     * 
     * Returns the list of friendly creatures detected by the creature c
     * @param c Int_ALife_Creature the creature that owns the neuron
     * @return ArrayList<Int_ALife_Creature> the list of friendly creatures detected by the creature c
     */
    public static ArrayList<Int_ALife_Creature> detectedFriendlyCreatures(Int_ALife_Creature c){
        ArrayList<Int_ALife_Creature> creatures = detectedCreatures(c);
        for (Int_ALife_Creature cr:creatures){
            if (!((Active_ALife_Creature)c).friendlyCreatureList.contains(cr)){
                creatures.remove(cr);
            }
        }
        return creatures;
    } // End public static ArrayList<Int_ALife_Creature> detectedFriendlyCreatures(Int_ALife_Creature c)


} // End public class ALife_Input_Neuron_Utils

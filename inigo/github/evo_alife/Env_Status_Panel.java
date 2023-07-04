package inigo.github.evo_alife;

import javax.swing.*;

/**
 * Write a description of class Env_Status_Panel here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Env_Status_Panel extends JPanel
{
    // Pretende externalizar el barra de estados para poder reportar cosas.
    // Fields ----------------------------------------------------------------------------
    private Evo_ALife caller = null;
    private JLabel msgLabel;
    
    // Methods ---------------------------------------------------------------------------
    // Construcotors ============================
    public Env_Status_Panel(Evo_ALife e){
        caller = e;
        msgLabel = new JLabel("System ok ...");
        add(msgLabel);
    }    
    // Public Methods and Fuctions ==============
    //Getters and Setters ------------
    /**
     * public void setCaller(Evo_ALife e)
     * Set the caller JFrame
     * 
     * @param    - Evo_ALife, the caller JFrame
     * @return   - None
     */         
    public void setCaller(Evo_ALife e){
        caller = e;
    }
    
    /**
     * public Evo_ALife getCaller()
     * Get the caller JFrame
     * 
     * @param    - None
     * @return   - Evo_ALife, the caller JFrame
     */         
    public Evo_ALife getCaller(){
        return caller;
    }
    
    /**
     * public void set(JLabel mLabel)
     * Set the label to display messages
     * 
     * @param    - JLabel
     * @return   - None
     */
    /*
    public void set(JLabel mLabel){
        msgLabel = mLabel;
    }
    */
    
    /**
     * public void set_StatusMsg(String msg)
     * Display the message in status report bar
     * 
     * @param    - String, message to be displayed
     * @return   - None
     */
   public void set_StatusMsg(String msg){
        msgLabel.setText(msg);
        repaint();
    }// End public void set_StatusMsg(String msg)
    
    
    
    /**
     * public void show_Status(String msg)
     * Add to text report panel next msg
     * 
     * @param    - String; 
     * @return   - None
     */     
    public void show_Status(String msg){
        // Show msgs.      
       set_StatusMsg(msg);
    }  
    
    /**
     * public void show_Env(Env_ALife e)
     * Display the enviroment caracteristics in panel
     * 
     * @param    - Env_ALife, ALife enviroment to be displayed
     * @return   - None
     */
    public void show_Env(Env_ALife e){
        show_Status(e.getName()+" running.");
        
        //FALTA a ver que sale...
    } // End public void show_Env(Env_ALife e)
    
    // Private Methods and Fuctions =============
    
    
    // Main if needed --------------------------------------------------------------------
    
    
} // End Class
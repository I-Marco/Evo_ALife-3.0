package inigo.github.evo_alife;

import javax.swing.*;
import java.awt.*;

/**
 * Write a description of class Env_Report_Panel here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Env_Report_Panel extends JPanel
{
    // Panel lateral con toda la información 1 funcion recibir reporte y lo saca 
    // 2º posibilidad, menos limpia el solita los datos.
    
    // Fields ----------------------------------------------------------------------------
    private Evo_ALife caller = null;
    
    private JPanel texReportsPanel, datasReportsPanel;
    private JTextArea texReportsArea, datasReportsArea;
    boolean textVisible = true;
    
    // Methods ---------------------------------------------------------------------------
    // Construcotors ============================
    /**
     * public Env_Report_Panel(Evo_ALife e)
     * Constructor
     * 
     * @param    - Evo_ALife; the caller frame
     * 
     */ 
    public Env_Report_Panel(Evo_ALife e){
        caller = e;
        
        datasReportsPanel = new JPanel();
        datasReportsArea = new JTextArea("Area de resultados...Data report\n");
        datasReportsArea.setLineWrap(true);
        datasReportsArea.setPreferredSize(new Dimension (110,320));
        datasReportsPanel.add(new JScrollPane(datasReportsArea));
        
        texReportsPanel = new JPanel();
        texReportsArea = new JTextArea("Area de mensajes...Text report\n");
        texReportsArea.setLineWrap(true);
        texReportsArea.setPreferredSize(new Dimension (110,400));
        texReportsPanel.add(new JScrollPane(texReportsArea));
        
        setLayout(new BorderLayout());
        add(datasReportsPanel,BorderLayout.PAGE_START);
        add(texReportsPanel, BorderLayout.CENTER);//BorderLayout.PAGE_END);
        
        // Falla el scroll pane
        
        //**** VOY AQui ******
    }    
    
    // Public Methods and Fuctions ==============
    /**
     * public void show_Report_Data(Env_ALife enviroment)
     * Show de enviroment report datas
     * 
     * @param    - Env_ALife; The simulation enviroment object
     * @return   - None
     */     
    public void show_Report_Data(Env_ALife enviroment){
        // Sow statistic of enviroment.
        datasReportsArea.setText("Name: "+enviroment.get_env_Name()+"\n");
        datasReportsArea.append("Time: "+enviroment.getTime()+"\n");
        datasReportsArea.append(" Creatures: "+enviroment.getCreatureNumberString()+"\n");
        datasReportsArea.append(" Species: \n   "+enviroment.getSpeciesNumberString()+"\n");
        datasReportsArea.append(" Total resources:\n   "+enviroment.getTotalResourcesString()+"\n");
        datasReportsArea.append(" Res. add/time: \n  " +enviroment.getResourceAddByTimeString()+"\n");
        datasReportsArea.append("T. Borns: \n"+enviroment.getCreature_BornString()+"\n");
        datasReportsArea.append("T. Deaths: \n"+enviroment.getCreature_DeathString()+"\n");
        datasReportsArea.append("\n");
        datasReportsArea.append("Reds: "+"\n");
        datasReportsArea.append("Greens: "+"\n");
        datasReportsArea.append("Blues: "+"\n");
        datasReportsArea.append("Explosions: "+"\n");
        //num of creatures
        //numb of species
        //num of resources may be to long...
    }
    
    /**
     * public void show_Report(String msg)
     * Add to text report panerl nes msg
     * 
     * @param    - String; 
     * @return   - None
     */     
    public void show_ReportMsg(String msg){
        // Show msgs.
        /*
                ** FALTA
        */
        texReportsArea.append(msg+"\n");
    }
    
    /**
     * public void show_Env(Env_ALife e)
     * Display the enviroment caracteristics in panel
     * 
     * @param    - Env_ALife, ALife enviroment to be displayed
     * @return   - None
     */
    public void show_Env(Env_ALife e){
        this.show_Report_Data(e);
        this.show_ReportMsg("Ennvironment reported");
    } // End public void show_Env(Env_ALife e)
    
    // Private Methods and Fuctions =============
    
    
    // Main if needed --------------------------------------------------------------------
    
    
} // End Class
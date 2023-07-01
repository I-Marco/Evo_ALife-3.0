import java.util.*;
import javax.swing.*; //JOptionPane
import java.awt.image.*; // BufferedImage
import java.io.*; //import java.io.File;
import javax.imageio.*;// import javax.imageio.ImageIO;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.*;

//import javax.swing.*;
//import java.awt.image.*;

//import java.awt.*;

//import java.io.File;
//import javax.imageio.ImageIO;/

/**
 * Class Env_ALife:
 * It is a try to be an artificial enviroment to suport ALife_Creatures
 * Private variables of "Universe"
 * 
 * @author Iñigo Marco 
 * @version 06-03-2023
 */
public class Env_ALife extends Thread
{
    public static final long CTE_TIEMPO_CEDER = 20;
    public static final long CTE_TIEMPO_ESPERA = 200;
    public static final long CTE_TIEMPO_ESPERA_LARGA = 2500;
    public static final int[] FOOD_0 = {0, 0, 0};
    public static final int TRACE_PODENRACION_PESO_DETECCIÓN = 256*3;
    public static final double MUTATION_PROBABILITY = 0.01;
    
    // Fields ----------------------------------------------------------------------------
    private Evo_ALife caller = null;
    private ALife_Nutrient_Environment land = null; //Mapa de rastros en lando o otro?
    private TreeMap<Long, ArrayList<Object>> eventList;
    private ArrayList<ALife_Specie> lifeTypeList;
    private ALife_Logical_Environment logical_Env =  null;
    private ALife_Species species = null;
    
    long last_CreatureID = 0;
    long creatureNumber = 0;
    long last_SpecieID = 0;//
    long specieNumber = 0;//
    
    //Enviroment variables, we have a next fuction to set and get all variables and a different one for images
    private String env_Name = "Noname";
    private BufferedImage landImg, lifeImg, backLandImage, backLifeImage; //May be we need one more as old values 
    private Long time  = null;
    private long timeCicles = 0;
    private int waitForThreads = 0; //Temporal tratamos de incluir un patron singleton pero dudas
    
    static private int maxThreads = 10; //Max number of simultaneus Threads allowed    

    private boolean allowMutate = true;
    //explode si/no . De momento no opcion multi explote limitado a 10 en ALife_Nutrient_Environment
    // entorno esferico si/no

    
    //Thread related fields
    private Semaphore semaphore;
    private final Object pauseSignal = new Object(); // señal de pausa/continuación externa
    private final Object maxThreadSignal = new Object(); // señal de pausa/continuación MaxThreads alcanzado
    private volatile boolean isPaused = false;    
    private MultiTaskUtil threadManager;
    
    private volatile boolean killSignal = false;

    //For test control.
    public int creature_Born = 0;
    public int creature_Death = 0;
    
    public long total_R = 0;
    public long total_G = 0;
    public long total_B = 0;
    
    // Methods ---------------------------------------------------------------------------
    // Constructors ============================
    /**
     * Constructor for objects of class Evo_ALife
     * @param   - Evo_ALife
     * Create e simulation enviroment
     */
    public Env_ALife(Evo_ALife e){
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.env_Name = this.env_Name + fechaHoraActual.format(formateador);
        caller = e;
        isPaused = true;
        semaphore = new Semaphore(maxThreads);
        this.setPriority(Thread.currentThread().getPriority()-2); //Forzar convergencia
        threadManager = new MultiTaskUtil();

        //landImg, lifeImg, time,lifeTypeList,land

        //Defauls values in global variables.
        time = Long.valueOf(0);
        species = new ALife_Species(this);
        
        land = new ALife_Nutrient_Environment(this, semaphore);
        eventList = new TreeMap<Long, ArrayList<Object>>(); //esta vacio debe tener al menos 1 evento
        MultiTaskUtil.threadMsg("New event List - Default Env_ALife Const");
        addEvent(time,land);

        lifeTypeList = new ArrayList<ALife_Specie>(); //Empty need values
        //Need Function generate_lifeTypeList(eventList)
        setLandImg(Env_Panel.getDefaultEnvImage()); //Not sure may be black badkground
        setLifeImg(Env_Panel.getDefaultEnvTransparentImage()); //Not sure
        backLandImage = Env_Panel.getDefaultEnvImage();
        backLifeImage = Env_Panel.getDefaultEnvTransparentImage();
        
        //logical_env
        if (this.land != null){
            this.logical_Env = new ALife_Logical_Environment(this);
        }
        Active_ALife_Creature.refreshLiveImage(this.eventList, this.lifeImg);
        this.backLandImage = Env_Panel.copyBufferedImage(this.getLandImg());
        caller.visualiceEnv(this);
    } // End public Env_ALife(Evo_ALife e) Constructor

    /**
     * Constructor for objects of class Evo_ALife
     * @param   - Evo_ALife,caller JFrame
     *          - BufferedImage , the visual representation of land
     *          - Object liveEnv, the timing creatures launcher
     *          - List<Object> envVars, the enviroment configuration variables
     * Create e simulation enviroment
     */ //(this,land,lifeEnv,envVars);
    public Env_ALife(Evo_ALife e,BufferedImage l, Object liveEnv, java.util.List<Object> envVars){
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.env_Name = this.env_Name +"_"+ fechaHoraActual.format(formateador);
        caller = e;
        isPaused = true;
        semaphore = new Semaphore(maxThreads);
        this.setPriority(Thread.currentThread().getPriority()-2); //Forzar convergencia
        threadManager = new MultiTaskUtil();

        //Set time to 0 and create new eventList
        setTime(Long.valueOf(0));
        species = new ALife_Species(this);
        eventList = new TreeMap<Long, ArrayList<Object>>(); //esta vacio debe tener al menos 1 evento
        
        //For Concurrency Long and track
        MultiTaskUtil.threadMsg("New event List - Env_ALife const()");
        
        // envVars
        setEnvVars(envVars);

        //Land
        //Env_Panel.imageDisplay(l," From Env_ALife Constructor");
        if (l != null){
            this.setLand(new ALife_Nutrient_Environment(this, semaphore, l));
            //land = new ALife_Nutrient_Environment(this, l);
        } else {  
            l = Env_Panel.getDefaultEnvTransparentImage();
            this.setLand(new ALife_Nutrient_Environment(this, semaphore, l));
        }
        addEvent(time,land);
        
        //logical_env
        if (this.land != null){
            this.logical_Env = new ALife_Logical_Environment(this);
        }
        
        //Need Function generate_lifeTypeList(eventList)
        setLandImg(l); 
        setLifeImg( Env_Panel.getDefaultEnvTransparentImage(l.getWidth(),l.getHeight(),true)); //Temporal
        //Env_Panel.imageDisplay(l,"Constructor imagen");
        
        //backLandImage = Env_Panel.copyBufferedImage(l); //Temporal
        backLandImage = Env_Panel.copyBufferedImage(this.getLandImg());
        backLifeImage = Env_Panel.copyBufferedImage(getLifeImg()); //= referencia?
        
        //Live
        if (liveEnv == null){
            //If liveEnv not defined take this Env_ALife as liveEnv
            liveEnv = this;
        }else{
            //We will keep eventList. Ususally make a new enviroment and them add events
            if (!(liveEnv instanceof ArrayList)){
                JOptionPane.showMessageDialog(null,
                    "Evento no reconocido.",
                    "Image Load Error",
                    JOptionPane.ERROR_MESSAGE);
                //this.pauseThread();
                return; // el return me saca del run en el thread            
            }
            try{for (Object o:(ArrayList)liveEnv) {
                    if (o instanceof Int_ALife_Creature) {
                        //Asignamos la cretura a este enviroment
                        this.addCreature(((Active_ALife_Creature)o));//Add to enviroment adding to eventList
                        //this.species.addCreature(((Creature)o)); //Add to species
                        //((Creature)o).setEnv_ALife(this);
                        //((Creature)o).setIdCreature(this.getCreatureID());                        
                        //addEvent(getTime()+1,(Creature)o);
                        
                    }else{
                        addEvent(getTime()+1,o);
                        
                        // Unexpected event
                        MultiTaskUtil.threadMsg("Unknown event Object.");
                    }
                }
            }catch(Exception ele){
                // Exception msg
                MultiTaskUtil.threadMsg("Creating env live env incorrects.");
                ele.printStackTrace();
            }
        }

        //if any problem default enviroment configuration
        //lifeTypeList = new ArrayList<ALife_Specie>(); //Empty need values
        //evaluate lifeTypeList(eventList);

        Active_ALife_Creature.refreshLiveImage(this.eventList, this.lifeImg);
        this.backLandImage = Env_Panel.copyBufferedImage(this.getLandImg());

        caller.visualiceEnv(this); //Display de new enviroment
    } // End public Env_ALife(Evo_ALife e,BufferedImage l, Object liveEnv, java.util.List<Object> envVars) Constructor

    // Public Methods and Fuctions ==============

    // From class Thread


    /**
     * Run() -
     *  Main work of a Thread
     * @param   - no parameters
     * @return  - no returns 
     **/ 
    @Override
    public void run(){ // synchronized ??
    
        while(true){ //Forever
            synchronized (pauseSignal) {
                while (isPaused) {
                    try { //Force manual wait if concurrence problems
                        MultiTaskUtil.threadMsg(" Esperando... "+this.getName());
                        //System.out.println(" Esperando... "+this.getName());
                        pauseSignal.wait(); // esperar señal de continuación
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MultiTaskUtil.threadMsg(" Continuando ... "+this.getName());
                    try{
                        sleep(Env_ALife.CTE_TIEMPO_ESPERA);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }            
            }
            
            if (this.killSignal) {
                break;
            }
            //For concurrency Log
            //MultiTaskUtil.threadMsg("Env Run ("+this.getTime()+") "+ semaphore.availablePermits()); //For test
            
            //this.backLifeImage = Env_Panel.getDefaultEnvTransparentImage(); //Blinck efect
            
            //Do something ...
            synchronized (eventList){
                if (eventList.isEmpty()){
                    try{
                        // Concurrency error Fixed try
                        MultiTaskUtil.threadMsg("FALLA concurrencia, Forzando espera"); //For test
                        sleep(Env_ALife.CTE_TIEMPO_ESPERA_LARGA);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } //If after sleep we have no events we will show an error message and exit enviroment
                if (eventList.isEmpty()) {
                    //no hay eventos HA fallado algo nada por hacer
                    //JOptionPane.showMessageDialog(null, "Lista de eventos temporales vacia");
                    JOptionPane.showMessageDialog(null,
                        "No quedan eventos!!! Error.",
                        "Image Load Error",
                        JOptionPane.ERROR_MESSAGE);
                    if (eventList.isEmpty()) return; //Get out of run method!!! //Comprueba de nuevo por concurrencia
                }
            }            
            
            //For Concurrency Log
            //MultiTaskUtil.threadMsg("Env Run ("+this.getTime()+")");//For test

            //for test counting control
            if(this.creatureNumber != this.getCalculateAliveCreatures()){
                int reakpoint = 1;
                MultiTaskUtil.threadMsg("Creature Number ("+this.creatureNumber+") != Alive Creatures ("+this.getCalculateAliveCreatures()+")");
            }
            if(this.species.getNumberOfDifferentActiveSpecies() != this.getCalculateActiveDifferentSpecies() ) {
                int reakpoint = 1;
                MultiTaskUtil.threadMsg("Species Number ("+this.species.getNumberOfDifferentActiveSpecies()+") != Active Different Species ("+this.getCalculateActiveDifferentSpecies()+")");
            }
            
            //end test
            // Make now event succeded
            ArrayList<Object> listaAhora = null;
            //synchronized (eventList){
            listaAhora = eventList.get(time); // si no hay eventos de time casca
            //}
            //Refresh live enviroment image
            if ( listaAhora.size() > 1 || 
                ( listaAhora.size() > 0 && (listaAhora.get(0) instanceof Int_ALife_Creature) ) ) 
            {
                //this.backLifeImage = Env_Panel.getDefaultEnvTransparentImage(
                //    getLandImg().getWidth(), getLandImg().getHeight(),true);
            }
            for(Object event : listaAhora){
                
                //For concurrency Log
                //MultiTaskUtil.threadMsg(" Manejando un evento ... "+this.getName());
                //System.out.println(" Manejando un evento ... "+this.getName());
                
                //We will create new threads Here, so we check if we are over max allowed threads

                //MyThread newThread = new MyThread(this, COUNTER,semaphore);
                //threads.add(newThread);
                //newThread.start();                   

                //Extract creature or Nutrient enviroment
                if (event instanceof Int_ALife_Creature){
                    //event is a Creature.
                    //(new Thread((Int_ALife_Creature) event)).start();
                    //Thread t = (new Thread((Int_ALife_Creature) event));
                    //t.setPriority(Thread.currentThread().getPriority()+1);
                    //t.start();//Crea nuevo hilo 
                    
                    if(((Int_ALife_Creature) event).getEnv_ALife().env_Name != this.env_Name) 
                        MultiTaskUtil.threadMsg("Env Disitinto"); //Unexpected Creature Fail

                    threadManager.startThread(((Int_ALife_Creature) event), 1);

                }else if (event instanceof ALife_Nutrient_Environment){
                    //event is a enviroment
                    //(new Thread((ALife_Nutrient_Environment) event)).start();
                    //Thread t = (new Thread((ALife_Nutrient_Environment) event));
                    //t.setPriority(Thread.currentThread().getPriority()+1);
                    //t.start(); 
                    threadManager.startThread(((ALife_Nutrient_Environment) event), 1);
                    //pintar el imagen provisional en el thread no aqui
                    //landImg = ((ALife_Nutrient_Environment) event).getLandImg();
                    //this.addWaitForThreads();
                } 

                //else ... ? May be we need add event type enviroment change
                else {//Event error Unknown event type
                    JOptionPane.showMessageDialog(null,
                        "Evento no reconocido.",
                        "Image Load Error",
                        JOptionPane.ERROR_MESSAGE);
                    //this.pauseThread();
                    return; // el return me saca del run en el thread
                } // End of if chain
            } // End for each envent, listaAhora

            //Wait to all event Threads done.
            //maxThreadSignal
            
            //For concurrency Log
            //MultiTaskUtil.threadMsg("Espea a converger ("+this.getTime()+"). AvaliableP "+
            //    semaphore.availablePermits()+" Condition ("+(semaphore.availablePermits() != maxThreads)+
            //    "). AvaliableP "+ semaphore.availablePermits());//For test
            //synchronized (maxThreadSignal) {

            threadManager.waitForThreadsToFinish();       
            //eventList.get(time)
            /* Necesario*/
            if (eventList.get(time).contains(this.getLand())) {
                synchronized(this){
                    this.logical_Env.run(); //Run logical enviroment. Prevent cuncurrency errors
                }
            }
            
            removeEvent(time);
            
            //For concurrency Log
            //MultiTaskUtil.threadMsg("Event List remove item");
            //MultiTaskUtil.threadMsg("Env Run ("+this.getTime()+") END CICLE");//For test

            synchronized (eventList){
                Long l= eventList.firstKey();
                if (l < getTime()){
                    int breakpoint = 1;
                    MultiTaskUtil.threadMsg("Time CICLE("+this.timeCicles+") ENDED!!! Time :"+getTime());
                    this.timeCicles += 1;
                }
                setTime(l);
            }

            setLandImg(this.land.getLandImg());

            //Env_Panel.imageDisplay(getLandImg(),"Land Image("+getTime()+")");

            setLifeImg(this.backLifeImage);
            //Env_Panel.imageDisplay(this.backLifeImage,"From Env_Alive run() - BACK LIVE Image("+getTime()+")");
            //Env_Panel.imageDisplay(getLifeImg(),"From Env_Alive run() - LIVE Image("+getTime()+")");
            caller.visualiceEnv(this);
            
            //For test stop each iteration
            //MultiTaskUtil.msgDialog("Final Turno :"+this.getTime());
        } //End While for ever 

        //MultiTaskUtil.threadMsg("OJO SALIENDO DEL METODO RUN ("+this.getTime()+")..."); //must be unreachable
    }//End public void run(){ // synchronized ??

    /*    
    public void acquire(){
    try{
    this.semaphore.acquire();
    }catch (java.lang.InterruptedException ie){
    ie.printStackTrace();
    }
    }

    public void release(){
    try{
    this.semaphore.release();
    }catch (Exception ie){
    ie.printStackTrace();
    }

    }
     */    

    /**
     * public void MyNotifyAll()
     * 
     * @param   - None
     * @return  - None
     */
    public void MyNotifyAll(){
        //maxThreadSignal
        //synchronized (maxThreadSignal) {
        synchronized (this) {
            notify();
            //maxThreadSignal.notifyAll(); // enviar señal de continuación
        }        
    } // End public void MyNotifyAll()

    /**
     * public void pauseThread() -
     * Pause Thread when we need doing another tasks
     * @param   - no parameters
     * @return  - no returns 
     **/         
    public void pauseThread() {
        //MultiTaskUtil.threadMsg("    TASK -Pause Env Run ("+this.getTime()+")");
        //boolean testB = this.isPaused; // just for test
        isPaused = true;
        //testB = this.isPaused; // just for test
    } // End public void pauseThread() 

    /**
     * public void resumeThread() -
     * Resume Thread after some task stop it
     * @param   - no parameters
     * @return  - no returns 
     **/         
    public void resumeThread() {
        //boolean testB = this.isPaused; // just for test
        isPaused = false;
        //testB = this.isPaused; // just for test

        //MultiTaskUtil.threadMsg("    TASK -Resume Env Run ("+this.getTime()+")");
        synchronized (pauseSignal) {
            pauseSignal.notifyAll(); // enviar señal de continuación
        }
    }// End public void resumeThread()

    public void killThread() {
        isPaused = false;
        this.killSignal = true; //Marcar señal para salir
        synchronized (pauseSignal) {
            pauseSignal.notifyAll(); // enviar señal de continuación
        }
    } // End public void killThread()

    // ============= GETTERS & SETTERS

    /**
     * public void set_env_Name(String n) -
     * Set new value enviroment env_Name variable
     * @param   - String
     * @return  - None 
     **/
    public void set_env_Name(String n){
        env_Name = n;
    } //End public void set_env_Name(String n)

    /**
     * public String get_env_Name() -
     * Get enviroments name
     * @param   - boolean
     * @return  - String
     **/
    public String get_env_Name(){
        return env_Name;
    } // End public String get_env_Name()

    
    /**
     * public void set_isPaused(boolean b) -
     * Set new value for isPaused variable
     * @param   - boolean
     * @return  - no returns 
     **/
    public void set_isPaused(boolean b){
        isPaused = b;
        //Env_ALive Tried to pause
        MultiTaskUtil.threadMsg("Set_isPaused ("+this.getTime()+") := "+b);
    } // End public void set_isPaused(boolean b)

    /**
     * public boolean get_isPaused() -
     * Get value in isPaused variable
     * @param   - None
     * @return  - boolean
     **/
    public boolean get_isPaused(){
        return isPaused;
    } // End public boolean get_isPaused()

    /**
     * public Long getTime() -
     * 
     * @param   - None
     * @return  - Returns this enviroment time
     **/ 
    public synchronized Long getTime(){
        return time;
    } // End public Long getTime()

    /**
     * public Long setTime(Long t) -
     * 
     * @param   - Long, time lapsed runned in enviroment
     * @return  - None
     **/     
    public synchronized void setTime(Long t){
        time = t;
    } // End public Long getTime()

    /**
     * public List<Object> getEnvVars() -
     * get the list of private enciroment variables. 
     * This fuction is to be used with serEnvVars
     * 
     * @param   - None
     * @return  - List of enviroment variables.
     **/
    public java.util.List<Object> getEnvVars(){
        java.util.List<Object> envVars = new ArrayList<Object>();
        // make the list of variables
        envVars.add(time);

        return envVars;
        //BE CAREFULL changes here must be updated in setter method
    } // End public List<Object> getEnvVars()

    /**
     * public void setEnvVars(List<Object> envVars) -
     * 
     * @param   - List<Object>
     * @return  - None
     **/     
    public void setEnvVars(java.util.List<Object> envVars){
        if (envVars == null){
            //Variable por defecto
            time = Long.valueOf(0);
            this.land.setLifeDelay(null);
            //resto de variables por defecto

            return;
        }
        try{
            time = (Long) envVars.get(0);
        }catch (Exception e){
            //Msg "Algo no salio bien al cargar las variables de entorno
            e.printStackTrace();
        }
    } // End public void setEnvVars(List<Object> envVars)

    /**
     * public void setLandImg(BufferedImage i)
     * 
     * @param   - BufferedImage
     * @return  - None
     **/
    public void setLandImg(BufferedImage i){
        if (landImg == null) { 
            this.landImg =  i;
            return;
        } else {
            synchronized (this.landImg){ //Null pointer exception when try to sync null object
                this.landImg =  i;
            }
        }
    }// End public BufferedImage getLandImg()

    /**
     * public BufferedImage getLandImg() 
     * 
     * @param   - None
     * @return  - BufferedImage of land
     **/
    public BufferedImage getLandImg(){
        return this.landImg;
    }// End public BufferedImage getLandImg()

    /**
     * public void setLandImg(BufferedImage i)
     * 
     * @param   - BufferedImage
     * @return  - None
     **/
    public void setLifeImg(BufferedImage i){ 
        if (lifeImg == null) {
            this.lifeImg =  i;
            return;
        }else {
            synchronized (this.lifeImg){//Null pointer exception when try to sync null object
                this.lifeImg =  i;
            }
        }
    }// End public BufferedImage getLandImg()

    /**
     * public BufferedImage getLifeImg()
     * 
     * @param   - None
     * @return  - BufferedImage of life representation
     **/
    public BufferedImage getLifeImg(){
        return this.lifeImg;
    }// End public BufferedImage getLifeImg()

    /**
     * public BufferedImage getBackLifeImage()
     * 
     * @param   - None
     * @return  - BufferedImage of back life representation
     **/
    public BufferedImage getBackLifeImage(){
        return this.backLifeImage;
    }// End public BufferedImage getLifeImg()

    /**
     * public void setBackLifeImage(BufferedImage i)
     * 
     * Set new value for backLifeImage variable (Carefull you can assign a null value)
     * @param   - i BufferedImage New back life image
     * @return  - None
     **/
    public synchronized void setBackLifeImage(BufferedImage i){
        //Carefull you can assign a null value
        this.backLifeImage = i;
    }// End public BufferedImage getLifeImg()

    /**
     * public void setLand(ALife_Nutrient_Environment n_e)
     * 
     * @param   - ALife_Nutrient_Environment the resource ground
     * @return  - None
     **/
    public void setLand(ALife_Nutrient_Environment n_e){
        this.land = n_e;
        this.landImg = land.getLandImg();
        // forzar repintado
    } // End public void setLand(ALife_Nutrient_Environment n_e)

    /**
     * public ALife_Nutrient_Environment getLand()
     * 
     * @param   - None
     * @return  - ALife_Nutrient_Environment the resource ground
     **/
    public ALife_Nutrient_Environment getLand(){
        return (this.land);
        // forzar repintado
    } // End public void setLand(ALife_Nutrient_Environment n_e)

    /**
     * public ALife_Nutrient_Environment setLand()
     * 
     * @param   - None
     * @return  - ALife_Nutrient_Environment the resource ground
     **/    
    public ALife_Nutrient_Environment setLand(){
        return this.land;
    } // End public ALife_Nutrient_Environment setLand()

    /**
     * public Semaphore getSemaphore()
     * 
     * @param   - None
     * @return  - Semaphore instance for concurrency
     **/    
    public Semaphore getSemaphore(){
        synchronized (semaphore){
            return this.semaphore;
        }
    } // End public Semaphore getSemaphore()

    /**
     * public synchronized boolean getAllowMutate() -
     * 
     * @param   - None
     * @return  - allowMutate; Permision to mutate in born creature
     **/     
    public synchronized boolean getAllowMutate(){
        return allowMutate;
    } // End public synchronized boolean getAllowMutate()

    /**
     * public synchronized String getCreatureNumber()
     * 
     * @param    None
     * @return   String
     */
    public synchronized String getCreatureNumber(){
        String r = "";
        r = ""+this.creatureNumber;
        return r;
    } // End public synchronized String getCreatureNumber()

    /**
     * public synchronized String getSpeciesNumber()
     * 
     * @param    None
     * @return   String
     */
    public synchronized String getSpeciesNumber(){
        String r = this.species.getNumberOfDifferentActiveSpecies() + " especies.";        
        return r;
    } // End public synchronized String getSpeciesNumber()

    /**
     * public synchronized String getTotalResources()
     * 
     * @param    None
     * @return   String
     */
    public synchronized String getTotalResources(){
        String r = "No computado";        
        return r;
    } // End public synchronized String getTotalResources()

    /**
     * public synchronized String getResourceAdd()
     * 
     * @param    None
     * @return   String
     */
    public synchronized String getResourceAddByTime(){
        String r = "";
        r =this.land.getResourceAddByTime();
        return r;
    } // End public synchronized String getResourceAdd()

    /**
     * public synchronized String getResourceDelay()
     * 
     * @param    None
     * @return   String
     */
    public synchronized String getResourceDelay(){
        String r = "";

        return r;
    } // End public synchronized String getResourceDelay()

    /**
     * public int getEnv_Width()
     * 
     * @param    None
     * @return   int Width of environment taken from Land image
     */
    public int getEnv_Width(){
        return this.land.getLandImg().getWidth();
    } // End public int getEnv_Width()
    
    /**
     * public int getEnv_Height()
     * 
     * @param    None
     * @return   int With of environment taken from Land image
     */
    public int getEnv_Height(){
        return this.land.getLandImg().getHeight();
    } // End public int getEnv_Height()
    
    /**
     * public ALife_Logical_Environment getLogical_Env()
     * 
     * @param    None
     * @return   ALife_Logical_Environment
     */
    public ALife_Logical_Environment getLogical_Env(){ 
        return this.logical_Env;
    } // End public ALife_Logical_Environment getLogical_Env()

    /**
     * public synchronized TreeMap<Long, ArrayList<Object> getEventList()
     * 
     * Retuern de eventList of the environment
     * @param    None
     * @return   TreeMap<Long, ArrayList<Object>
     */
    public synchronized TreeMap<Long, ArrayList<Object>> getEventList(){
        return this.eventList;
    } // End public synchronized TreeMap<Long, ArrayList<Object> getEventList()

    /**
     * public void setLogical_Env(ALife_Logical_Environment l_e)
     * 
     * @param    ALife_Logical_Environment
     * @return   None
     */
    public String getCreature_Born(){
        return ""+this.creature_Born;
    } // End public void setLogical_Env(ALife_Logical_Environment l_e)
    
    /**
     * public void setLogical_Env(ALife_Logical_Environment l_e)
     * 
     * @param    ALife_Logical_Environment
     * @return   None
     */
    public String getCreature_Death(){
        return ""+this.creature_Death;
    } // End public String getCreature_Death()
    
    /**
     * public synchronized ALife_Species getSpecies()
     * 
     * @param    None
     * @return   ALife_Species
     */
    public synchronized ALife_Species getSpecies(){
        return this.species;
    } // End public synchronized ALife_Species getSpecies()

    //land ALife_Nutrient_Environment
    //envet List?? specie list??
    //BufferedImage landImg, lifeImg setters?

    // End ============= GETTERS & SETTERS

    /**
     * public public synchronized int getWaitForThreads() -
     * 
     * @param   - None
     * @return  - waitForThreads; counter of event threads
     **/     
    public synchronized int getWaitForThreads(){
        return waitForThreads;
    }

    /**
     * public synchronized void setWaitForThreads(int i) -
     * Set int parameter as waitForThreads value
     * 
     * @param   - int
     * @return  - None
     **/   
     /*
    public synchronized void setWaitForThreads(int i){
        if(waitForThreads<0){
            //Error
            System.out.println("Error waitForThead < 0");
            return;
        }
        waitForThreads = i;

        //MultiTaskUtil.threadMsg("    TASK -removeWaitForThreads() = "+waitForThreads+", Env Run ("+this.getTime()+")");
        synchronized (maxThreadSignal) {
            maxThreadSignal.notifyAll(); // enviar señal de continuación
        }        

    }
    */
    /**
     * public synchronized int addWaitForThreads() -
     * Count new running event Thread and return the running thread number.
     * 
     * @param   - None
     * @return  - int waitForThreads variable, the number of event Threads running
     **/     
    public synchronized int addWaitForThreads(){
        waitForThreads += 1;
        return waitForThreads;
    }

    /**
     * public synchronized int removeWaitForThreads() -
     * Discount a running event Thread and return the running thread number.
     * 
     * @param   - None
     * @return  - int waitForThreads variable, the number of event Threads running
     **/  
    public synchronized int removeWaitForThreads(){
        waitForThreads -= 1;
        if(waitForThreads<0){
            //Error
            System.out.println("Error waitForThead < 0");
        }

        //MultiTaskUtil.threadMsg("    TASK -removeWaitForThreads() = "+waitForThreads+", Env Run ("+this.getTime()+")");
        synchronized (maxThreadSignal) {
            maxThreadSignal.notifyAll(); // enviar señal de continuación
        }        

        return waitForThreads;
    }

    /**
     * public synchronized void addEvent(Long t,Object obj) -
     * Add a new event to eventList
     * 
     * @param   - Long, when have to be added the event
     * @param   - Object, the event to be added
     * @return  - None
     **/      
    public synchronized void addEvent(Long t,Object obj){
        ArrayList<Object> list = null;
        if (eventList.containsKey(t)){
            list = eventList.get(t);
            if (!list.contains(obj)) list.add(obj);
        }else {
            list = new ArrayList<Object>();
            list.add(obj);
            eventList.put(t, list);
        }
        
        // For event track Log Concurrency
        //MultiTaskUtil.threadMsg("--> Evento añadido ("+eventList.size()+")");
        
        //System.out.printf("Task(%s)-Class(%s)--> Evento añadido - (%d).%n",
        //    Thread.currentThread().getName(),this.getClass().getName(),eventList.size());

    }

    /**
     * public synchronized void addEvent(Long t,Object obj) -
     * Add a new event to eventList
     * 
     * @param   - Long, when have to be added the event
     * @param   - Object, the event to be added
     * @return  - None
     **/      
    public synchronized void removeEvent(Long t){
        eventList.remove(time);
        
        // For track Log and concurrency Log
        //MultiTaskUtil.threadMsg("--> Evento eliminado ("+eventList.size()+")");
        //System.out.printf("Task(%s)-Class(%s)--> Evento eliminado  (%d) - Long (%d).%n",
        //    Thread.currentThread().getName(),this.getClass().getName(),time,eventList.size());

    }   

    /**
     * public long getCreatureID()
     * 
     * @param   - None
     * @return  - long creatureNumber variable, the number of creatures borned
     **/      
    public long getCreatureID(){;
        return last_CreatureID;
    }
    
    /**
     *  
     * @param   - Object, the event to be added
     * @return  - None
     **/      
    public long getSpecieIdNumber(){
        this.specieNumber++;
        this.last_SpecieID++;
        return last_CreatureID;  
    }
    
    /**
     * public long getNewCreatureID()
     * Get a new creature ID from environment and count new creature
     * @param   - None  
     * @return  - long creatureNumber variable, the number of creatures borned
     */
    public long getNewCreatureID(){
        this.creatureNumber++;
        this.creature_Born ++;
        this.last_CreatureID ++;
        return last_CreatureID;
    }

    /**
     *  
     * @param   - Object, the event to be added
     * @return  - None
     **/      
    public void addCreature(Int_ALife_Creature c){
        //make sure creature is linked with this Env_ALive
        //Int_ALife_Creature.viewCreature(c);
        if (c==null){ //???? FALTA
            int breakp  =1 ;
        }
        if (c.getEnv_ALife() != this) {
            c.setEnv_ALife(this);
            c.setIdCreature(this.getNewCreatureID());
        }
        //Add creature to eventList
        //"Nueva Criatura"+c.idCreature+" hijo de "+c.progenitors[0].idCreature
        addEvent(getTime()+1,c);   
        //Add creature to ALife_Logical_Environment
        this.logical_Env.addCreature(c, c.getPos());
        //Add in life image
        c.paint(this.getBackLifeImage(), Color.YELLOW); //CAUTION we have to pass to front image to be seen
        //add Creature to specie system
        this.species.addCreatureToSpecies(c);
        
    } //End public addCreature(Int_ALife_Creature c)
    
    /**
     *  public void removeCreature(Int_ALife_Creature c)
     *  
     * @param   - Object, the event to be added
     * @return  - None
     **/      
    public void removeCreature(Int_ALife_Creature c){
        this.creatureNumber -= 1;
        this.creature_Death += 1;
        this.species.removeCreature(c);
        this.logical_Env.removeCreature(c);
        //remove creature from this.lifeImg
        this.removeCreatureFromEventList(c);
        c.paint(getBackLifeImage(), null); //CAUTION we have to pass to front image to be seen
    } // End public void removeCreature(Int_ALife_Creature c)

    /**
     * public void removeCreatureFromEventList(Int_ALife_Creature c);
     * 
     * @param  - Int_ALife_Creature
     * @return - None
     */
    public void removeCreatureFromEventList(Int_ALife_Creature c){
        //Remove creature from eventList
        ArrayList<Object> list = null;
        for (Long t : eventList.keySet()){
            list = eventList.get(t);
            if (list.contains(c)) list.remove(c);
        }
    } // End public void removeCreatureFromEventList(Int_ALife_Creature c)


    //Some test enviroments creators.

    /**
     * public static Env_ALife createEnv_ALife_d_nCre(Evo_ALife e, int width, int height, int nCre)
     * 
     * Create a defined enviroment with a defined number of creatures and land dimension
     * @param   - int width, enviroment width
     * @param   - int height, enviroment height
     * @param   - int nCre, number of creatures to be created
     * @return  - Env_ALife, the new enviroment
     * (Evo_ALife e,BufferedImage l, Object liveEnv, java.util.List<Object> envVars)
     */
    public static Env_ALife createEnv_ALife_d_nCre(Evo_ALife e, int width, int height, int nCre){
        ArrayList<Point> creaturePositions = new ArrayList<Point>(); //List of creature positions
        //Create land environment
        BufferedImage land_Temp = new BufferedImage(
            width, height, BufferedImage.TYPE_INT_ARGB);
        while (creaturePositions.size() < nCre){
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            Point p = new Point(x,y);
            if (!creaturePositions.contains(p)){
                creaturePositions.add(p);
            }
        } //Random positions for creatures

        //food spots for creatures.
        Graphics2D g2d = land_Temp.createGraphics();
        for (Point pos:creaturePositions){
            for (int i = pos.x - 1; i < pos.x + 1; i++){
                for (int j = (pos.y - 1); j < (pos.y + 1) ; j++){
                    //Random color
                    g2d.setColor(new Color(
                        (int) (Math.random() * 230),
                        (int) (Math.random() * 230), 
                        (int) (Math.random() * 230)
                    )); // fix background to BLACK 0, 0, 0
                    int x= (i + width) % width;
                    g2d.fillRect((i + width) % width, (j + height) % height , 1, 1);
                }
            }
            g2d.setColor(new Color(50,50,50));
            g2d.fillRect(pos.x, pos.y , 1, 1);
        }

        //Create creatures
        ArrayList<Int_ALife_Creature> creatures = new ArrayList<Int_ALife_Creature>();
        int lifeExpectancy = 2000;
        if (true) // For test put false to test resource environment
        for (Point pos:creaturePositions){
            boolean valid = false;
            int[] foodNeed = {0 , 0 , 0}, foodOwn= {0 , 0 , 0};
            while (!valid){
                int[] foodNeed_aux = {(int) (Math.random() * 199)/100, (int) (Math.random() * 199)/100, (int) (Math.random() * 199)/100};
                foodNeed = foodNeed_aux;
                int[] foodOwn_aux = {foodNeed[0]*250,foodNeed[1]*250,foodNeed[2]*250};
                foodOwn = foodOwn_aux;
                valid = !Arrays.equals(foodNeed_aux, foodOwn_aux);
            }
            
            Int_ALife_Creature c = new Active_ALife_Creature(e.get_Env_Alife(), pos, null, lifeExpectancy, foodOwn, foodNeed);
            Mind_ALife m = new Mind_ALife(-1, 3, 3, -1, c);
            c.setPos(pos);
            c.setMind(m);
            creatures.add(c);
        }
        
        //Env_ALife(Evo_ALife e,BufferedImage l, Object liveEnv, java.util.List<Object> envVars)
        return new Env_ALife(e,land_Temp, creatures, e.get_Env_Alife().getEnvVars());
        //Env_ALife e = new Env_ALife();
    } // End public static Env_ALife createEnv_ALife_d_nCre(Evo_ALife e, int width, int height, int nCre)

    //for test functions
    public long getCalculateAliveCreatures(){
        long cont = 0;
        for (Long t : eventList.keySet()){
            ArrayList<Object> list = eventList.get(t);
            for (Object o : list){
                if (o instanceof Active_ALife_Creature){
                    cont++;
                }
            }
        }
        return cont;
    } // End public long getAliveCreatures()

    public long getCalculateActiveDifferentSpecies(){
        long cont = 0;
        ALife_Species species = this.getSpecies();
        for (ALife_Specie s : species.speciesList){
            cont += 1;
        }
        return cont;
    } // End public long getActiveDifferentSpecies()


    // Private Methods and Fuctions ====================================================================

    // Serialización de un objeto en un archivo
    public static void guardarObjeto(Object objeto, String archivo) throws IOException {
        ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo));
        salida.writeObject(objeto);
        salida.close();
    }

    // Deserialización de un objeto de un archivo
    public static Object cargarObjeto(String archivo) throws IOException, ClassNotFoundException {
        ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo));
        Object objeto = entrada.readObject();
        entrada.close();
        return objeto;
    }

    /**
     * We have to save here 3 parts of enviroment
     *     Enviroment variables
     *     Enviroment Ground
     *     Life ground
     * 
     * @param   - None
     * @return  - int waitForThreads variable, the number of event Threads running
     **/  
    private void save_Env(String filename){

        // Guardamos la imagen en un archivo PNG
        try {
            File outputFile = new File(filename + ".png");
            ImageIO.write(landImg, "png", outputFile);
        } catch (IOException e) {
            
            //MultiTaskUtil.threadMsg("Error al guardar la imagen: " + e.getMessage());
            System.out.println("Error al guardar la imagen: " + e.getMessage());
            //Modificar para dar el error en la frame            
        }
        // Guardamos parámetros del entorno
        try{
            guardarObjeto(this,(filename + ".env"));
        } catch (java.io.IOException ioe)
        {
            ioe.printStackTrace();
        }
        // Capa de seres vivos

    }

    private void load_Env(String filename){

        // Cargamos la imagen en un archivo PNG
        try {
            File inputFile = new File(filename + ".png");
            landImg = ImageIO.read(inputFile);
        } catch (IOException e) {
            //MultiTaskUtil.threadMsg("Error al cargar la imagen: " + e.getMessage());
            System.out.println("Error al cargar la imagen: " + e.getMessage());
            //Modificar para dar el error en la frame            
        }
        // Cargamos parámetros del entorno
        try{
            Env_ALife loadedEnv = (Env_ALife)cargarObjeto((filename + ".env"));
            caller.set_Env_Alive(loadedEnv);
        } catch (java.io.IOException ioe) {
            //File i/o error
            ioe.printStackTrace();
        } catch (java.lang.ClassNotFoundException cnfe) {
            //Class cast error
            cnfe.printStackTrace();
        }

        // Capa de seres vivos
    }

    
    // Main if needed --------------------------------------------------------------------

} // End Class
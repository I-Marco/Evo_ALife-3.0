package inigo.github.evo_alife;

import java.util.*;
import javax.swing.*; //JOptionPane
import java.awt.image.*; // BufferedImage
import java.io.*; //import java.io.File;
import javax.imageio.*;// import javax.imageio.ImageIO;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.*;

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
    int CONSTreportTimeDelay = 200;    
    public static final int MAXCREATUREAABALIABLE = 1;
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
    //long last_SpecieID = 0;//
    //long specieNumber = 0;//
    
    //Enviroment variables, we have a next fuction to set and get all variables and a different one for images
    private String env_Name = "Noname";
    private BufferedImage landImg, lifeImg, backLandImage, backLifeImage; //May be we need one more as old values 
    private Long time  = null;
    private long timeCicles = 0;
    private int waitForThreads = 0; //Temporal tratamos de incluir un patron singleton pero dudas
    static private int maxThreads = MAXCREATUREAABALIABLE; //Max number of simultaneus Threads allowed
   

    private boolean allowMutate = true;
    //explode si/no . De momento no opcion multi explote limitado a 10 en ALife_Nutrient_Environment
    // entorno esferico si/no

    
    //Thread related fields
    private Semaphore semaphore; //semaforo para controlar el numero de hilos
    private final ReentrantLock lockEventList;//semaforo para controlar el acceso a la lista de eventos
    private final ReentrantLock lockLiveImage;//semaforo para controlar el acceso a la imagen de vida
    private final Object pauseSignal = new Object(); // señal de pausa/continuación externa
    private final Object maxThreadSignal = new Object(); // señal de pausa/continuación MaxThreads alcanzado
    private volatile boolean isPaused = false;    
    private MultiTaskUtil threadManager;
    
    private volatile boolean killSignal = false;

    private ALife_FileManager fileManager_EnvGlobal = null;
    private ALife_FileManager fileManager_EnvCreatures = null;
    long nextReportTime = 0;

    //For test control.
    public int creature_Born = 0;
    public int creature_Death = 0;
    
    public long total_R = 0;
    public long total_G = 0;
    public long total_B = 0;

    boolean generateReport = false;
    
    // Methods ---------------------------------------------------------------------------
    // Constructors ============================
    /**
     * Constructor for objects of class Evo_ALife
     * @param   - Evo_ALife
     * Create e simulation enviroment
     */
    public Env_ALife(Evo_ALife e){
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss");
        this.env_Name = this.env_Name + fechaHoraActual.format(formateador);
        caller = e;
        isPaused = true;
        semaphore = new Semaphore(maxThreads);
        this.lockEventList = new ReentrantLock();
        this.lockLiveImage = new ReentrantLock();
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
        Active_ALife_Creature.refreshLiveImage(this.getCreatureList(), this.lifeImg);
        this.backLandImage = Env_Panel.copyBufferedImage(this.getLandImg());
        caller.visualiceEnv(this);
        try{
            this.fileManager_EnvGlobal = new ALife_FileManager(this.env_Name,this.caller);
            //this.fileManager_EnvGlobal.start();
            String[] HEADERS = {"Time", "NumberOfCreaturesAlive", "NumberOfSpeciesAlive", "NumberOfCreaturesBorn", "NumberOfCreaturesDeath"};
            this.fileManager_EnvGlobal.addLine(HEADERS);
            this.fileManager_EnvGlobal.forceWrite();
            this.fileManager_EnvCreatures = new ALife_FileManager(this.env_Name+"_Creatures",this.caller);
            //this.fileManager_EnvCreatures.start();
           
        }catch (IOException ioe){
            MultiTaskUtil.threadMsg("Error creating file manager");
        }
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
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss");
        this.env_Name = this.env_Name +"_"+ fechaHoraActual.format(formateador);
        caller = e;
        isPaused = true;
        semaphore = new Semaphore(maxThreads);
        this.lockEventList = new ReentrantLock();
        this.lockLiveImage = new ReentrantLock();
        this.setPriority(Thread.currentThread().getPriority()-2); //Forzar convergencia
        threadManager = new MultiTaskUtil();

        try{
            this.fileManager_EnvGlobal = new ALife_FileManager(this.env_Name,this.caller);
            //this.fileManager_EnvGlobal.start();
            String[] HEADERS = {"Time", "NumberOfCreaturesAlive", "NumberOfSpeciesAlive", "NumberOfCreaturesBorn", "NumberOfCreaturesDeath"};
            this.fileManager_EnvGlobal.addLine(HEADERS);
            this.fileManager_EnvGlobal.forceWrite();
            this.fileManager_EnvCreatures = new ALife_FileManager(this.env_Name+"_Creatures",this.caller);
        }catch (IOException ioe){
            MultiTaskUtil.threadMsg("Error creating file manager"+ioe.getMessage());
        }

        //Set time to 0 and create new eventList
        setTime(Long.valueOf(0));
        species = new ALife_Species(this);
        eventList = new TreeMap<Long, ArrayList<Object>>(); //esta vacio debe tener al menos 1 evento
        
        //For Concurrency Long and track
        //MultiTaskUtil.threadMsg("New event List - Env_ALife const()");
        
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

        Active_ALife_Creature.refreshLiveImage(this.getCreatureList(), this.lifeImg);
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
    public void run(){ 
        if (this.fileManager_EnvGlobal != null) 
            this.fileManager_EnvGlobal.start();
        if (this.fileManager_EnvCreatures != null) 
            this.fileManager_EnvCreatures.start();

        String[] report = {"0","0","0","0", "0"};
        //ArrayList <String> reportList = new ArrayList<String>();

        while(true){ //Forever
            synchronized (pauseSignal) {
                while (isPaused) {
                    this.fileManager_EnvGlobal.forceWrite();
                    this.fileManager_EnvCreatures.forceWrite();
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
                this.fileManager_EnvGlobal.close();
                this.fileManager_EnvCreatures.close();
                break;
            }
            //For concurrency Log
            //MultiTaskUtil.threadMsg("Env Run ("+this.getTime()+") "+ semaphore.availablePermits()); //For test
            
            //this.backLifeImage = Env_Panel.getDefaultEnvTransparentImage(); //Blinck efect
            
            //Do something ...
            lockEventList.lock();
            try{
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
            }finally{
                lockEventList.unlock();
            }         
            
            //For Concurrency Log
            //MultiTaskUtil.threadMsg("Env Run ("+this.getTime()+")");//For test

            //for test counting control
            if(this.creatureNumber != this.getCalculateAliveCreatures()){
                MultiTaskUtil.threadMsg("Creature Number ("+this.creatureNumber+") != Alive Creatures ("+this.getCalculateAliveCreatures()+")");
                int reakpoint = 1;                
            }
            if(this.species.getNumberOfDifferentActiveSpecies() != this.getCalculateActiveDifferentSpecies() ) {
                MultiTaskUtil.threadMsg("Species Number ("+this.species.getNumberOfDifferentActiveSpecies()+") != Active Different Species ("+this.getCalculateActiveDifferentSpecies()+")");
                int reakpoint = 1;                
            }
            
            //end test
            // Make now event succeded
            ArrayList<Object> listaAhora = null;
            //synchronized (eventList){
            listaAhora = this.getListaAhora(time);
            //listaAhora = eventList.get(time); // si no hay eventos de time casca
    
            //}
            //Refresh live enviroment image
            if ( listaAhora.size() > 1 || 
                ( listaAhora.size() > 0 && (listaAhora.get(0) instanceof Int_ALife_Creature) ) ) 
            {
                //this.backLifeImage = Env_Panel.getDefaultEnvTransparentImage(
                //    getLandImg().getWidth(), getLandImg().getHeight(),true);
            }
            for(Object event : listaAhora){//Concurrent modification exception
                
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
            /* Necesario - Provisional objetivo seria incluirlo como un evento más*/
            lockEventList.lock();
            try{
                if (eventList.get(time).contains(this.getLand())) {
                    this.logical_Env.run(); //Run logical enviroment. Prevent cuncurrency errors
                }
            } finally {
                lockEventList.unlock();
            }
            
            removeEvent(time);
            
            //For concurrency Log
            //MultiTaskUtil.threadMsg("Event List remove item");
            //MultiTaskUtil.threadMsg("Env Run ("+this.getTime()+") END CICLE");//For test

            Long l= eventList.firstKey();
            this.getNextEventTime();
            if (l < getTime()){
                int breakpoint = 1;
                MultiTaskUtil.threadMsg("Time CICLE("+this.timeCicles+") ENDED!!! Time :"+getTime());
                this.timeCicles += 1;
            }
            setTime(l);
        

            setLandImg(this.land.getLandImg());

            //Env_Panel.imageDisplay(getLandImg(),"Land Image("+getTime()+")");

            setLifeImg(this.backLifeImage);
            //Env_Panel.imageDisplay(this.backLifeImage,"From Env_Alive run() - BACK LIVE Image("+getTime()+")");
            //Env_Panel.imageDisplay(getLifeImg(),"From Env_Alive run() - LIVE Image("+getTime()+")");
            caller.visualiceEnv(this);
            
            //Save report to files.
            
            /* 
            if (this.generateReport){
                String[] report = {""+this.getTime(),""+this.getCreatureNumberString(),""+this.getSpecies().getNumberOfDifferentActiveSpecies(),
                    ""+this.getCreature_BornString(), ""+this.getCreature_DeathString()};
                this.fileManager.addLine(report);
                this.generateReport = false;
            }
            */

            report = reportEnvGlobal(report);
            /*
            if (this.getTime() > nextReportTime){
                if (report == null){
                    report = new String[5];
                    report[0] = ""+this.getTime();
                    report[1] = ""+this.getCreatureNumberString();
                    report[2] = ""+this.getSpecies().getNumberOfDifferentActiveSpecies();
                    report[3] = ""+this.getCreature_BornString();
                    report[4] = ""+this.getCreature_DeathString();
                }
                while (this.getTime() > nextReportTime+CONSTreportTimeDelay){
                    this.fileManager_EnvGlobal.addLine(report);    
                    nextReportTime += CONSTreportTimeDelay;
                }
                reportList = new ArrayList<String>();
                reportList.add(""+this.getTime());
                reportList.add(""+this.getCreatureNumberString());
                reportList.add(""+this.getSpecies().getNumberOfDifferentActiveSpecies());
                reportList.add(""+this.getCreature_BornString());
                reportList.add(""+this.getCreature_DeathString());
                for (int i = 0; i < this.species.getNumberOfDifferentSpecies();i++){
                    reportList.add(""+this.species.speciesList.get(i).getSpecieIdNumber());
                    reportList.add(""+this.species.speciesList.get(i).getNumberOfCreaturesInSpecie());
                }
                //String[] auxReport = {""+this.getTime(),""+this.getCreatureNumberString(),""+this.getSpecies().getNumberOfDifferentActiveSpecies(),
                //    ""+this.getCreature_BornString(), ""+this.getCreature_DeathString()};
                String[] auxReport = new String[reportList.size()];
                auxReport = reportList.toArray(auxReport);
                this.fileManager_EnvGlobal.addLine(report);
                nextReportTime += CONSTreportTimeDelay;
                report = auxReport;
                //this.generateReport = true;
            }
            */

            //=============================
            //For test control.
            if (this.getTime() > 20000){
                int breakpoint = 1;
            }

            //=============================

            //For test stop each iteration
            //MultiTaskUtil.msgDialog("Final Turno :"+this.getTime());
        } //End While for ever 
        this.fileManager_EnvGlobal.close();
        this.fileManager_EnvCreatures.close();
        //MultiTaskUtil.threadMsg("OJO SALIENDO DEL METODO RUN ("+this.getTime()+")..."); //must be unreachable
    }//End public void run(){ // synchronized ??

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
     * public Evo_ALife getCaller()
     * 
     * @param   - None
     * @return  - Evo_ALife
     */
    public Evo_ALife getCaller(){
        return this.caller;
    } // End public Evo_ALife getCaller()


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
    public void setBackLifeImage(BufferedImage i){
        //Carefull you can assign a null value
        this.lockLiveImage.lock();
        try{
            this.backLifeImage = i;
        } finally {
            this.lockLiveImage.unlock();
        }
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
     * public synchronized void setCreatureNumber(long c)
     * 
     * Set new value for creatureNumber variable
     * @param   - long
     * @return  - None
     */
    public synchronized void setCreatureNumber(long c){
        this.creatureNumber = c;
    } // End public synchronized void setCreatureNumber(long c)

    /**
     * public synchronized String getCreatureNumber()
     * 
     * @param    None
     * @return   String
     */
    public synchronized String getCreatureNumberString(){
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
    public synchronized String getSpeciesNumberString(){
        String r = this.species.getNumberOfDifferentActiveSpecies() + " especies.";        
        return r;
    } // End public synchronized String getSpeciesNumber()

    /**
     * public synchronized String getTotalResources()
     * 
     * @param    None
     * @return   String
     */
    public synchronized String getTotalResourcesString(){
        String r = "No computado";        
        return r;
    } // End public synchronized String getTotalResources()

    /**
     * public synchronized String getResourceAdd()
     * 
     * @param    None
     * @return   String
     */
    public synchronized String getResourceAddByTimeString(){
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
    public synchronized String getResourceDelayString(){
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

    public ArrayList<Active_ALife_Creature> getCreatureList(){
        ArrayList<Active_ALife_Creature> creatureList = new ArrayList<Active_ALife_Creature>();
        lockEventList.lock();
        try{
            for (Map.Entry<Long, ArrayList<Object>> entry : eventList.entrySet()) {
                ArrayList<Object> value = entry.getValue();
                if (value == null) continue;
                for (Object o : value){
                    if (o instanceof Active_ALife_Creature){
                        creatureList.add((Active_ALife_Creature) o);
                    }
                }
            }
        }finally{
            lockEventList.unlock();
        }
        return creatureList;

    } // End public ArrayList<Int_ALife_Creature> getCreatureList()

    /**
     * public synchronized TreeMap<Long, ArrayList<Object> getEventList()
     * 
     * Retuern de eventList of the environment
     * @param    None
     * @return   TreeMap<Long, ArrayList<Object>
     */
    //Borrar en cuanto pueda
    public synchronized TreeMap<Long, ArrayList<Object>> getEventList(){
        return this.eventList;
    } // End public synchronized TreeMap<Long, ArrayList<Object> getEventList()

    /**
     * public void setLogical_Env(ALife_Logical_Environment l_e)
     * 
     * @param    ALife_Logical_Environment
     * @return   None
     */
    public String getCreature_BornString(){
        return ""+this.creature_Born;
    } // End public void setLogical_Env(ALife_Logical_Environment l_e)
    
    /**
     * public String getCreature_Death()
     * 
     * get the number of creatures death by String
     * @param    None
     * @return   String
     */
    public String getCreature_DeathString(){
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
    public void addEvent(Long t,Object obj){
        this.lockEventList.lock();
        try{
            ArrayList<Object> list = null;
            if (eventList.containsKey(t)){
                list = eventList.get(t);
                if (!list.contains(obj)) list.add(obj);
            }else {
                list = new ArrayList<Object>();
                list.add(obj);
                eventList.put(t, list);
            }
        }finally{
            this.lockEventList.unlock();
        }
    }

    /**
     * public synchronized void addEvent(Long t,Object obj) -
     * Add a new event to eventList
     * 
     * @param   - Long, when have to be added the event
     * @param   - Object, the event to be added
     * @return  - None
     **/      
    public void removeEvent(Long t){
        //check
        if (t==null) return;
        this.lockEventList.lock();
        try{
            if (eventList == null) return;
            if (eventList.containsKey(t)){
                eventList.remove(t);
            }
        }finally{
            this.lockEventList.unlock();
        }
    }   

    /**
     * public ArrayList<Object> getListaAhora(Long t)
     * 
     * Generate an object list of events to be done in this time
     * @param   - Long, time to be checked
     * @return  - ArrayList<Object>, the list of events to be done in this time
     */
    public ArrayList<Object> getListaAhora(Long t){
        if (t==null) return null;
        ArrayList<Object> listaAhora = new ArrayList<Object>();
        this.lockEventList.lock();
        try{
            if (eventList == null) return null;
            if (eventList.containsKey(t)){
                for (Object o : eventList.get(t)){
                    listaAhora.add(o);
                }// get different list of same event for concurrence problems
            }
        }finally{
            this.lockEventList.unlock();
        }
        return listaAhora;
    } // End public ArrayList<Object> getListaAhora(Long t)
    
    /**
     * public Long getNextEventTime()
     * 
     * Get the next event time to be done. Next enviroment time unit
     * @param   - None
     * @return  - Long, the next event time to be done
     */
    public Long getNextEventTime(){
        Long t = null;
        this.lockEventList.lock();
        try{
            if (eventList == null) return null;
            if (eventList.isEmpty()) return null;
            t = eventList.firstKey();
        }finally{
            this.lockEventList.unlock();
        }
        return t;
    } // End public Long getNextEventTime()
    
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
    public long getNewSpecieIdNumber(){//this.species.lastSpecieNumberID CORREGIR
        return this.species.getLastSpecieNumberID();
        //this.specieNumber++;
        //this.last_SpecieID++;
        //return last_SpecieID;  
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
        //for test
        this.generateReport = true;
        MultiTaskUtil.threadMsg("New Creature ID Born("+this.last_CreatureID+")");
        return last_CreatureID;
    }

    /**
     * public ReentrantLock getLockLiveImage()
     * 
     * @param   - None
     * @return  - ReentrantLock, the lock for live image
     */
    public ReentrantLock getLockLiveImage(){
        return this.lockLiveImage;
    } // End public ReentrantLock getLockLiveImage()

    /**
     * public ReentrantLock getLockEventList()
     * 
     * @param   - None
     * @return  - ReentrantLock, the lock for event list
     */
    public ReentrantLock getLockEventList(){
        return this.lockEventList;
    } // End public ReentrantLock getLockLiveImage()

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
        this.logical_Env.addCreatureToLogEnv(c, c.getPos());
        //Add in life image
        c.paint(this.getBackLifeImage(), Color.YELLOW, this.lockLiveImage); //CAUTION we have to pass to front image to be seen
        //add Creature to specie system
        this.species.addCreatureToSpecies(c);
        if (c.getIdCreature() < 0) {int breakpoints = 1;} // FALTA new id creature
        this.reportEnvCreatureBorn(c);
        if (c instanceof Active_ALife_Creature && 
            c.getIdCreature()%Active_ALife_Creature.DEFAULT_CreatureReportPercent == 0) 
        {
            ((Active_ALife_Creature)c).setFileManager_Mind();
        }
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
        //For test
        this.generateReport = true;
        this.species.removeCreature(c);
        this.logical_Env.removeCreature(c);
        //remove creature from this.lifeImg
        this.removeCreatureFromEventList(c);
        c.paint(getBackLifeImage(), null, this.lockLiveImage); //CAUTION we have to pass to front image to be seen
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
        lockEventList.lock();
        try{
            for (Long t : eventList.keySet()){
                list = eventList.get(t);
                if (list.contains(c)) list.remove(c);
            }
        }finally{
            lockEventList.unlock();
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
        Env_ALife auxEnv = new Env_ALife(e,land_Temp, creatures, e.get_Env_Alife().getEnvVars());
        auxEnv.setTime(0L);
        return auxEnv;
        //Env_ALife e = new Env_ALife();
    } // End public static Env_ALife createEnv_ALife_d_nCre(Evo_ALife e, int width, int height, int nCre)

    //for test functions

    /**
     * public long getCalculateAliveCreatures()
     * 
     * Remake a count of alive creatures in enviroment
     * @param   - None
     * @return  - long, number of alive creatures in enviroment
     */
    public long getCalculateAliveCreatures(){
        long cont = 0;
        lockEventList.lock();
        try{
            for (Long t : eventList.keySet()){
                ArrayList<Object> list = eventList.get(t);
                for (Object o : list){
                    if (o instanceof Active_ALife_Creature){
                        cont++;
                    }
                }
            }
        }finally{
            lockEventList.unlock();
        }
        return cont;
    } // End public long getAliveCreatures()

    /**
     * public synchronized long getCalculateActiveDifferentSpecies()
     * 
     * Remake a count of different species with alife creatures in enviroment
     * @param   - None
     * @return  - long, number of different species with alife creatures in enviroment
     */
    public synchronized long getCalculateActiveDifferentSpecies(){
        long cont = 0;
        ALife_Species species = this.getSpecies();
        for (ALife_Specie s : species.speciesList){
            if(s.getNumberOfCreaturesInSpecie() > 0)cont += 1;
        }
        return cont;
    } // End public long getActiveDifferentSpecies()


    // Private Methods and Fuctions ====================================================================

    /**
     * public String[] reportEnvGlobal(String[] report)
     * 
     * Create a report of enviroment global creatures and species count
     * @param   - String[] report, the report to be updated
     * @return  - String[], the report updated
     */
    public String[] reportEnvGlobal(String[] report){
        String[] auxReport = null;
        if (this.getTime() > nextReportTime){
            if (report == null){
                report = new String[5];
                report[0] = ""+this.getTime();
                report[1] = ""+this.getCreatureNumberString();
                report[2] = ""+this.getSpecies().getNumberOfDifferentActiveSpecies();
                report[3] = ""+this.getCreature_BornString();
                report[4] = ""+this.getCreature_DeathString();
            }
            while (this.getTime() > nextReportTime+CONSTreportTimeDelay){
                this.fileManager_EnvGlobal.addLine(report);    
                nextReportTime += CONSTreportTimeDelay;
            }
            ArrayList<String> reportList = new ArrayList<String>();
            reportList.add(""+this.getTime());
            reportList.add(""+this.getCreatureNumberString());
            reportList.add(""+this.getSpecies().getNumberOfDifferentActiveSpecies());
            reportList.add(""+this.getCreature_BornString());
            reportList.add(""+this.getCreature_DeathString());
            for (int i = 0; i < this.species.getNumberOfDifferentSpecies();i++){
                reportList.add(""+this.species.speciesList.get(i).getSpecieIdNumber());
                reportList.add(""+this.species.speciesList.get(i).getNumberOfCreaturesInSpecie());
            }
            //String[] auxReport = {""+this.getTime(),""+this.getCreatureNumberString(),""+this.getSpecies().getNumberOfDifferentActiveSpecies(),
            //    ""+this.getCreature_BornString(), ""+this.getCreature_DeathString()};
            auxReport = new String[reportList.size()];
            auxReport = reportList.toArray(auxReport);
            this.fileManager_EnvGlobal.addLine(report);
            nextReportTime += CONSTreportTimeDelay;
        } // End if (this.getTime() > nextReportTime)
        return auxReport;
    } // End public void reportEnvGlobal()

    /**
     * private void reportEnvCreatureBorn(Int_ALife_Creature c)
     * 
     * Create a report of enviroment creature born
     * @param  - Int_ALife_Creature c, the creature to be reported
     * @return - None
     */
    private void reportEnvCreatureBorn(Int_ALife_Creature c){
        ArrayList<String> reportList = new ArrayList<String>();
        reportList.add(""+this.getTime());
        ((Active_ALife_Creature)c).makeCreatureSORTReport(this.fileManager_EnvCreatures);
        String[] auxReport = new String[reportList.size()];
        auxReport = reportList.toArray(auxReport);
        this.fileManager_EnvCreatures.addLine(auxReport);
    } // End public void reportEnvCreatureBorn(Int_ALife_Creature c)

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
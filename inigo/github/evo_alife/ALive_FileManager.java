package inigo.github.evo_alife;

/**
 * https://opencsv.sourceforge.net/#general
 */
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * public class ALive_FileManager extends Thread
 * 
 * This class is responsible for writing the data to the CSV file.
 * 
 * @author Iñigo Marco 
 * @version 20-06-2023
 */
public class ALive_FileManager extends Thread{
    public static final int DEFAULT_MAX_LONG = 50;//1000
    public static final int DEFAULT_MAX_LONG_FILE = 1000;//1000000

    //Attributes
    private 
    private Lock lockFileManager;
    private CSVWriter writer;
    private boolean writerOpen = false;
    private List <String[]> dataIn = new ArrayList<String[]>();
    private List <String[]> dataOut = new ArrayList<String[]>();
    private long cont = 0;
    private long fileNumber = 0;

    private boolean running = true;

    private Evo_ALife caller;
    private Env_ALife env;

    //Constructor

    /**
     * public ALive_FileManager(String fileName,Evo_ALife caller) throws IOException
     * 
     * This constructor is responsible for creating the file and the writer. This add .csv to the file name.
     * @param writer
     * @throws IOException
     */
    public ALive_FileManager(String fileName,Evo_ALife caller) throws IOException {
        this.fileNumber = 1;
        fileName = "DatosVidaEvoAlife";
        String formattedNumber = String.format("%03d", fileNumber);
        String filePath = "DATAS" + "/" + fileName + formattedNumber + ".csv";
        this.running = true;
        try {
            this.writer = new CSVWriter(new FileWriter(fileName+formattedNumber+".csv"));
        }catch (IOException e) {
            try{
                File directory = new File("DATAS");
                directory.mkdirs();  // Crea el directorio si no existe
                this.writer = new CSVWriter(new FileWriter(fileName+formattedNumber+".csv"));
            }catch(IOException e2){
                MultiTaskUtil.threadMsg("Error creating File."+e.getMessage());
                MultiTaskUtil.threadMsg("Second exception."+e2.getMessage() );
                throw new IOException("Error creating file: " + filePath);
            }
        }
        //CSVWriter writer = new CSVWriter(new FileWriter("output.csv"));
        this.writer = new CSVWriter(new FileWriter(fileName+formattedNumber+".csv"));
        this.writerOpen = true;
        this.caller = caller;
        this.env = caller.get_Env_Alife();
        cont = 0;
        this.lockFileManager = new ReentrantLock();
        dataIn = new ArrayList<String[]>();
        dataOut = new ArrayList<String[]>();
    } // End public ALive_FileManager(CSVWriter writer) throws IOException

    /**
     * public ALive_FileManager(String fileName,Evo_ALife caller) throws IOException
     * 
     * This constructor is responsible for creating the file and the writer. This add .csv to the file name.
     * @param writer
     * @throws IOException
     */
    public ALive_FileManager(String fileName,Int_ALife_Creature c) throws IOException {
        this.fileNumber = 1;
        fileName = "DatosVidaEvoAlife";
        String formattedNumber = String.format("%03d", fileNumber);
        String filePath = "DATAS" + "/" + fileName + formattedNumber + ".csv";
        this.running = true;
        try {
            this.writer = new CSVWriter(new FileWriter(fileName+formattedNumber+".csv"));
        }catch (IOException e) {
            try{
                File directory = new File("DATAS");
                directory.mkdirs();  // Crea el directorio si no existe
                this.writer = new CSVWriter(new FileWriter(fileName+formattedNumber+".csv"));
            }catch(IOException e2){
                MultiTaskUtil.threadMsg("Error creating File."+e.getMessage());
                MultiTaskUtil.threadMsg("Second exception."+e2.getMessage() );
                throw new IOException("Error creating file: " + filePath);
            }
        }
        //CSVWriter writer = new CSVWriter(new FileWriter("output.csv"));
        this.writer = new CSVWriter(new FileWriter(fileName+formattedNumber+".csv"));
        this.writerOpen = true;
        this.env = c.env_ALive;
        this.caller = this.env.getCaller();       
        cont = 0;
        this.lockFileManager = new ReentrantLock();
        dataIn = new ArrayList<String[]>();
        dataOut = new ArrayList<String[]>();
    } // End public ALive_FileManager(CSVWriter writer) throws IOException



    //Methods public
    /**
     * public void run()
     * 
     * This method is responsible for writing the data to the CSV file. Necesary for Thread class.
     */
    @Override
    public void run() {
        //For test
        MultiTaskUtil.threadMsg("ALive_FileManager.run() - Start");
        while (running) {
            try {
                //if(this.getEnv_ALife() == null || !this.getEnv_ALife().isAlive()) running = false;
                Thread.sleep(1000);
                //if (!running) break;
                lockFileManager.lock();
                try {
                    if (dataIn.size() > DEFAULT_MAX_LONG){
                        dataOut = dataIn;
                        dataIn = new ArrayList<String[]>();
                        writer.writeAll(dataOut);
                        cont += dataOut.size();
                        if (cont > DEFAULT_MAX_LONG_FILE){
                            //Create new File
                            //cont = 0;
                        }
                    }
                } finally {
                    lockFileManager.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } // End while (running)
        close();
        MultiTaskUtil.threadMsg("ALive_FileManager.run() - End execution"); 
    } // End public void run()

    //Generar metodo publico para agregar nuevas lineas al archivo
    /**
     * public void addLine(String[] line)
     * 
     * This method is responsible for adding new lines to the file.
     * 
     * @param line
     */
    public void addLine(String[] line){
        lockFileManager.lock();
        try {
            dataIn.add(line);
        } finally {
            lockFileManager.unlock();
        }
    } // End public void addLine(String[] line)

    /**
     * public void forceWrite()
     * 
     * This method is responsible for forcing the writing of the data to the CSV file.
     * @ param  - None
     * @ return - None
     */
    public void forceWrite(){
        lockFileManager.lock();
        try {
            if (dataIn.size() > 0){
                dataOut = dataIn;
                dataIn = new ArrayList<String[]>();
                writer.writeAll(dataOut);
                cont += dataOut.size();
            }
        } finally {
            lockFileManager.unlock();
        } 
    } // End public void forceWrite()

    /**
     * public void close()
     * 
     * This method is responsible for closing the file.
     * @ param  - None
     * @ return - None
     */
    public void close(){
        if (!writerOpen) return;
        try {
            this.forceWrite();
            writer.close();
            this.writerOpen = false;
            lockFileManager.lock();
            try {
                running = false;
            } finally {
                lockFileManager.unlock();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // End public void close()

    /**
     * public void KillFileManager()
     * 
     * This method is responsible for killing the file manager.
     * @ param  - None
     * @ return - None
     */
    public void KillFileManager(){
        lockFileManager.lock();
        try {
            running = false;
        } finally {
            lockFileManager.unlock();
        }
    } // End public void KillFileManager()

    //Add getters and setters

    /**
     * public Env_ALife getEnv_ALife()
     * 
     * This method is responsible for returning the Env_ALife object.
     * @ param  - None
     * @ return - Env_ALife
     */ 
    public Env_ALife getEnv_ALife(){
        //May be we need Lock
        return this.env;
    } // End public Env_ALife getEnv_ALife()
} // End public class ALive_FileManager extends Thread

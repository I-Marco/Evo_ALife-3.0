import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;

/**
 * Write a description of class MultiTaskUtil here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */


public class MultiTaskUtil
{
    /**
     * Mapa de los threads que se están ejecutando
     */
    private Map<Thread, Runnable> runningThreads = new ConcurrentHashMap<>();
    
    //Constructors
    /**
     * public MultiTaskUtil()
     *  Constructor de la clase vacio
     * @param     - None
     */
    public MultiTaskUtil(){}
    
    /**
     * public static void isThreadAlive(Thread t, Thread caller)
     * Comprueba si el thread t está en ejecución
     * @param t
     * @param caller
     */
    public static void isThreadAlive(Thread t, Thread caller){
        if (t.isAlive()) {
            // El thread ha finalizado
            System.out.printf("Task(%s)-Class(%s)--> Thread sigue en ejecución.(%s)%n",
                t.getName(),t.getClass().getName(),Thread.currentThread().getName());
            // System.out.println("El hilo está en ejecución.");
        } else {
            System.out.printf("Task(%s)-Class(%s)--> Thread No está ejecución !!!(%s)%n",
                t.getName(),t.getClass().getName(),Thread.currentThread().getName());
        }
        Thread.State state = t.getState();
        if (state == Thread.State.WAITING || state == Thread.State.TIMED_WAITING) {
            // El thread está esperando un signal
            System.out.printf("Task(%s)-Class(%s)--> Thread esta esperado ...(%s)%n",
                t.getName(),t.getClass().getName(),Thread.currentThread().getName());
        } else {
            System.out.printf("Task(%s)-Class(%s)--> Thread No tiene señal de espera!!!(%s)%n",
                t.getName(),t.getClass().getName(),Thread.currentThread().getName());        
        }
    }
    
    /**
     * public static void threadMsg(String msg)
     * Muestra un mensaje con el nombre del thread que lo ejecuta, 
     * ayuda a la trazabilidad de la multitarea
     * @param msg
     * @return    - None
     */
    public static void threadMsg(String msg){
        System.out.printf("Task(%s)-Class(%s)--> "+msg+" ...(%s)%n",
                Thread.currentThread().getName(),Thread.currentThread().getClass().getName(),Thread.currentThread().getName());    
    } // End public static void threadMsg(String msg)

    /**
     * public static void msgDialog(String msg)
     * Muestra un mensaje con el nombre del thread que lo ejecuta,
     * ayuda a la trazabilidad de la multitarea
     * El cuadro de diálogo detiene momentaneamente la ejecución del thread hasta que el usuario lo cierra
     * @param msg
     */
    public static void msgDialog(String msg){
        //String input = JOptionPane.showInputDialog(null, "Ingrese su nombre", "Entrada de texto", JOptionPane.QUESTION_MESSAGE);
        //JOptionPane.showMessageDialog(null, "Se produjo un error", "Error", JOptionPane.ERROR_MESSAGE);
        //JOptionPane.showMessageDialog(null, "¡Cuidado! Esto es una advertencia", "Advertencia", JOptionPane.WARNING_MESSAGE);
        //JOptionPane.showMessageDialog(null, "Este es un mensaje informativo", "Información", JOptionPane.INFORMATION_MESSAGE);

        JOptionPane.showMessageDialog(null, msg, "Información !!", JOptionPane.INFORMATION_MESSAGE);         
        
    }
    
    /**
     * public static boolean copyIntArrayContent(int[] a, int[] b)
     * Copia el contenido de un array a otro
     * Evita la copia de arrays por referencia en lugar de por valor.
     * @param a
     * @param b
     * @return
     */
    public static boolean copyIntArrayContent(int[] a, int[] b){
        //boolean done = false;
        if (a == null || b == null){
            return false;
        }
        
        for(int i =0; i<b.length; i++){a[i] = b[i];}
        return true;
    }

    //Non static methods --------------------------------------------------------
    
    /**
     * public void startThread(Runnable task)
     * Inicia un thread con la tarea pasada como parámetro, 
     * añadiendolo al mapa de threads en ejecución
     * @param task
     */
    public void startThread(Runnable task) {
        Thread thread = new Thread(task);
        runningThreads.put(thread, task);
        thread.start();
        //runningThreads.put(thread, task);
    }

    /**
     * public void startThread(Runnable task,int p)
     * Inicia un thread con la tarea pasada como parámetro,
     * añadiendolo al mapa de threads en ejecución
     * El thread se inicia con una prioridad p puntos por encima del thread que lo inicia
     * @param task
     * @param p, prioridad relativa del thread 
     */
    public void startThread(Runnable task,int p) {
        Thread thread = new Thread(task);
        thread.setPriority(Thread.currentThread().getPriority()+p);
        runningThreads.put(thread, task);
        thread.start();
        //runningThreads.put(thread, task);
    }

    /**
     * public void waitForThreadsToFinish()
     * Espera a que todos los threads controlados finalicen
     * @param     - None
     * @return    - None
     */
    public void waitForThreadsToFinish() {
        for (Thread thread : runningThreads.keySet()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // Manejar la interrupción si es necesario
            }
        }
        runningThreads.clear();
    }
    
} // End public class MultiTaskUtil
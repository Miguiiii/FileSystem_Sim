/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;

import Structures.HashMap;
import Structures.Queue;
import java.util.concurrent.Semaphore;
import main.GUI;

/**
 *
 * @author Miguel
 */
public class FileSystem {
    private Process running;
    private Queue<Process> readyList;
    private Queue<Process> newsList;
    private HashMap<Integer, Process> blockedList;
    private Queue<Process> exit;
    private Disk disk;
    private GUI ventana;
    private HashMap<Integer, Block> buffer;
    private Thread mainThread;
    private Semaphore newSem;

    public FileSystem() {
        this.readyList = new Queue();
        this.newsList = new Queue();
        this.blockedList = new HashMap(20);
        this.exit = new Queue();
        this.newSem = new Semaphore(1);
        this.disk = new Disk(512); //PROVICIONAL
    }
    
    public void boot() {
        mainThread = new Thread(()->{
            while (true) {
                try {
                    runTime();
                } catch (InterruptedException ex) {
                    System.out.println("Error in Main File System Thread");
                }
            }
        });
        mainThread.setDaemon(true);
        mainThread.start();
    }
    
    public void createProcess(File file, CRUD type, User owner) {
        
    }
    
    private void runProcess() {
        
    }
    
    private void runTime() throws InterruptedException {
        newSem.acquire();
        while (newsList.getLength()!=0) {
            readyList.enqueue(newsList.dequeue().setStatus(Status.READY));
        }
        newSem.release();
        if (!readyList.isEmpty()) {
            running = readyList.dequeue().setStatus(Status.RUNNING);
            runProcess();
        }
        int[] completed = disk.getCompleted();
        for(int id:completed) {
            readyList.enqueue(blockedList.deleteEntry(id).completeRequest());
        }
    }
    
}

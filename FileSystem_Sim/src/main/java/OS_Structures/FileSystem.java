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
    private Process running = null;
    private Queue<Process> readyList;
    private Queue<Process> newsList;
    private HashMap<Integer, Process> blockedList;
    private Queue<Process> exit;
    private Disk disk;
    private GUI ventana;
    private Buffer buffer;
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
        disk.bootDisk();
        mainThread = new Thread(()->{
            while (true) {
                try {
                    runTime();
                } catch (InterruptedException ex) {
                    System.getLogger(FileSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
        });
        mainThread.setDaemon(true);
        mainThread.start();
    }
    
    public void createProcess(DiskElement file, CRUD type, User owner, String newName, Folder newParent) {
        try {
            newSem.acquire();
            Process p = new Process(type, file, owner);
            switch (type) {
                case CRUD.CREATE -> p.setNewParent(newParent);
                case CRUD.UPDATE -> p.setNewName(newName);
            }
            newsList.enqueue(p);
            newSem.release();
        } catch (InterruptedException ex) {
            System.getLogger(FileSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public void createProcess(DiskElement file, CRUD type, User owner, String newName) {
        createProcess(file, type, owner, newName, null);
    }
    
    public void createProcess(DiskElement file, CRUD type, User owner, Folder newParent) {
        createProcess(file, type, owner, null, newParent);
    }
    
    private void runProcess() {
        if (running.isRequestCompleted()) {
            exitProcess();
        }
        if ((running.getElement().getOwner()!=running.getOwner() && !running.getOwner().isAdmin())||(!running.getElement().isFile() && !running.getOwner().isAdmin())) {
            System.out.println("Proceso no permitido, eliminando proceso");
            running = null;
            return;
        }
        if (running.getCrud()==CRUD.CREATE && running.getElement() instanceof File file1) {
            if (disk.getFreeSpace()<file1.getSize()) {
                System.out.println("Este archivo no cabe en el disco, eliminando proceso");
                running = null;
                return;
            }
        }
        if ((running.getCrud()==CRUD.READ||running.getCrud()==CRUD.UPDATE) && !running.isContentAFile()) {
            System.out.println("No se puede Leer una carpeta, eliminando proceso");
            running = null;
            return;
        }
        if (running.getCrud()==CRUD.READ) {
            IORequest newRequest = new IORequest(running.getId(), running.getElement(), running.getCrud(), running.getOwner(), 0);
            if (buffer.checkBuffer(newRequest.getHeadDir())) {
                //logica del buffer
            }
        }
        if(disk.addRequest(running)) {
            
        }
        blockedList.put(running.getId(), running.setStatus(Status.BLOCKED));
        running = null;
    }
    
    private void exitProcess() {
        exit.enqueue(running.setStatus(Status.EXIT));
        running = null;
        return;
    }
    
    public void setDiskSchedule(DISK_SCHEDULE sd) {
        disk.setSchedule(sd);
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

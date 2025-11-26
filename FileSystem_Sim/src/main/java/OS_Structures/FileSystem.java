package OS_Structures;

import Structures.HashMap;
import Structures.Queue;
import java.util.concurrent.Semaphore;

public class FileSystem {
    private Process running = null;
    private Queue<Process> readyList;
    private Queue<Process> newsList;
    private HashMap<Integer, Process> blockedList; 
    private Queue<Process> exit;
    private Disk disk;
    private Thread mainThread;
    private Semaphore newSem;
    private boolean systemActive = true;

    public FileSystem(int diskSize) {
        this.readyList = new Queue<>();
        this.newsList = new Queue<>();
        this.blockedList = new HashMap<>(20);
        this.exit = new Queue<>();
        this.newSem = new Semaphore(1);
        this.disk = new Disk(diskSize); 
    }
    
    public void boot() {
        System.out.println("[BOOT] Sistema Iniciado.");
        disk.bootDisk();
        mainThread = new Thread(() -> {
            while (systemActive) {
                try {
                    runTime();
                    Thread.sleep(100); 
                } catch (InterruptedException ex) {}
            }
        });
        mainThread.setDaemon(true);
        mainThread.start();
    }
    
    public void createProcess(DiskElement file, CRUD type, User owner, String newName, Folder newParent) {
        try {
            newSem.acquire();
            Process p = new Process(type, file, owner);
            p.setNewName(newName);
            p.setNewParent(newParent);
            newsList.enqueue(p);
            System.out.println("[SYSTEM] Nuevo proceso en cola NEW: ID " + p.getId());
            newSem.release();
        } catch (InterruptedException ex) {}
    }

    private void runTime() {
        while (!newsList.isEmpty()) {
            Process p = newsList.dequeue();
            p.setStatus(Status.READY);
            readyList.enqueue(p);
            System.out.println("[SYSTEM] Proceso " + p.getId() + " movido a READY.");
        }

        if (running == null && !readyList.isEmpty()) {
            running = readyList.dequeue();
            running.setStatus(Status.RUNNING);
            runProcess(); 
        }

        Queue<Integer> completed = disk.getCompleted();
        while (!completed.isEmpty()) {
            int pid = completed.dequeue();
            Process p = blockedList.getValueOfKey(pid);
            
            if (p != null) {
                p.completeRequest();
                System.out.println("[SYSTEM] Proceso " + pid + " completó I/O.");
                terminateProcess(p); 
                blockedList.deleteEntry(pid);
            }
        }
    }

    private void runProcess() {
        if (running == null) return;
        
        boolean isFile = running.getElement().isFile();
        CRUD opType = running.getCrud();

        if (opType == CRUD.CREATE && isFile) {
            File f = (File) running.getElement();
            if (disk.getFreeSpace() < f.getSize()) {
                System.out.println("[SYSTEM] Error: Espacio insuficiente para proceso " + running.getId());
                terminateProcess(running);
                running = null; 
                return;
            }
        }

        if (disk.addRequest(running)) {
            running.setStatus(Status.BLOCKED);
            blockedList.put(running.getId(), running);
            System.out.println("[SYSTEM] Proceso " + running.getId() + " bloqueado esperando Disco.");
            running = null; 
        } else {
            running.setStatus(Status.READY);
            readyList.enqueue(running);
            running = null;
        }
    }

    private void terminateProcess(Process p) {
        if (p == null) p = running;
        if (p != null) {
            p.setStatus(Status.EXIT);
            exit.enqueue(p);
            System.out.println("[SYSTEM] Proceso " + p.getId() + " terminado.");
        }
    }
    
    public Queue<Process> getNewsList() { return newsList; }
    public Queue<Process> getReadyList() { return readyList; }
    public Process getRunningProcess() { return running; }
    public Queue<Process> getExitList() { return exit; }
    public Disk getDisk() { return disk; }
}
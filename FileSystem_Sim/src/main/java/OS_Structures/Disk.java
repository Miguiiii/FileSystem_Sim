/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;
import Structures.List;
import Structures.Queue;
import java.util.concurrent.Semaphore;
/**
 *
 * @author Miguel
 */
public class Disk {
    
    private Block[] memory;
    private int freeSpace;
    private int usedSpace = 0;
    private int size;
    private Folder Root;
    private int realArm = 0;
    private int virtualArm = 0;
    private List<IORequest> requests;
    private IORequest current = null;
    private Queue<IORequest> completed;
    private DISK_SCHEDULE schedule = DISK_SCHEDULE.FIFO;
    private DISK_SCHEDULE newSched = DISK_SCHEDULE.FIFO;
    private static long pseudo_date = 0;
    private Thread diskThread;
    private Semaphore req;
    private Semaphore comp;
    private boolean started = false;

    public Disk(int size) {
        this.memory = new Block[size];
        this.freeSpace = this.size = size;
        for (int i=0; i<this.size; i++) {
            memory[i] = new Block(i);
        }
        Root = new Folder("Root", new User("Nop", true));
        requests = new List();
        completed = new Queue();
        req = new Semaphore(1);
        comp = new Semaphore(1);
    }
    /*  Constructor para crear disco cargando un archivo json
    public Disk(int size, int x) {
        this(size);
    }
    */
    public int[] getCompleted() {
        try {
            comp.acquire();
        } catch (InterruptedException ex) {
            System.getLogger(Disk.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        if (completed.getLength() == 0) return new int[0];
        int[] toReturn = new int[completed.getLength()];
        int i = 0;
        while (completed.getLength()!=0) {
            toReturn[i] = completed.dequeue().getId();
            i++;
        }
        completed = new Queue();
        comp.release();
        return toReturn;
    }
    
    public void bootDisk() {
        diskThread = new Thread(()->{
            while (true) {
                try {
                    diskOn();
                } catch (InterruptedException ex) {
                    System.getLogger(FileSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
        });
        diskThread.setDaemon(true);
        diskThread.start();
    }
    
    private void diskOn() throws InterruptedException {
        changeSchedule();
        req.acquire();
        getNextRequest();
    }
    
    private void getNextRequest() {
        
    }
    
    public void setSchedule(DISK_SCHEDULE ns) {
        newSched = ns;
    }
    
    private void changeSchedule() {
        if (newSched == schedule) return;
        if (newSched != DISK_SCHEDULE.FIFO) {
            
        }
        schedule = newSched;
    }
    
    public void deleteBlocks(Block head) {
        do {
            head.emptyBlock();
            head = head.getNext();
            realArm = head.getBlockDir();
            memory[realArm].emptyBlock();
        } while (head != null);
    }
    
    private void CRUDdelete(DiskElement file) {
        if (file instanceof Folder folder) {
            for(DiskElement dE:folder.getContents().getValues()) {
                deleteFile(dE);
            }
        }
        Block pointer = memory[file.getFileDir()];
        while (pointer != null) {
            pointer = pointer.getNext();
            realArm = pointer.getBlockDir();
            memory[realArm].emptyBlock();
        }
    }
    
    private List<Block> readFile(File file) {
        List<Block> blocks = new List();
        Block pointer = memory[file.getFileDir()];
        while (pointer != null) {
            blocks.insertFinal(pointer);
            realArm = pointer.getBlockDir();
            pointer = pointer.getNext();
        }
        return blocks;
    }
    
    private void modFile(File file, String newName) {
        Block pointer = memory[file.getFileDir()];
        while (pointer != null) {
            pointer = pointer.getNext();
            realArm = pointer.getBlockDir();
            memory[realArm].emptyBlock();
        }
    }
    
    private DiskElement addFile() {
        return null;
    }
    
    private void insertScan(IORequest nRequest) {
        int i = 0;
        for (IORequest r: requests) {
            
        }
    }
    
    public void addRequest(Process process) {
        IORequest newRequest = new IORequest(process.getId(), process.getElement(), process.getCrud(), process.getOwner(), pseudo_date);
        try {
            req.acquire();
        } catch (InterruptedException ex) {
            System.getLogger(Disk.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        switch (schedule) {
            case (DISK_SCHEDULE.FIFO) -> requests.insertFinal(newRequest);
            default -> insertScan(newRequest);
        }
        req.release();
        pseudo_date++;
    }

    public int getFreeSpace() {
        return freeSpace;
    }

    public int getUsedSpace() {
        return usedSpace;
    }

    public int getSize() {
        return size;
    }
    
    
    
    private int getVirtualArm() {
        int arm = 0;
        for (IORequest i: requests) {
            
        }
        return arm;
    }
    
    private Integer findFreeSpace(int start) {
        if (freeSpace == 0) {
            return null;
        }
        for (int i=start; i<this.size; i++) {
            if (memory[i].isFree()) {return i;}
        }
        return null;
    }
    
    private Integer findFreeSpace() {
        return findFreeSpace(0);
    } 
    
    //No se si pasarle como argumento el json o el path al archivo
    public void loadFromFile() {
        if (started) {
            return; //Para evitar que se sobreescriba el disco a mitad de ejecución
        }
        //CÓDIGO PARA CARGAR ARCHIVOS, FALTA IMPLEMENTAR CON JSON
        /*
        Se manda a cargar el root folder:
        
        loadFolder(root);
        */
    }
    //Falta por implementar
    //IMPORTANTE: COLOCARLE COMO ARGUMENTO EL PASARLE UNA CARPETA (FOLDER)
    private void loadFolder() {
        /*
        for (i in folder):
            if i.type==folder:
                loadFolder(i)       Se usa recurrencia para cargar la carpeta
                continue
            loadFile(i)             Como no es carpeta, se carga como archivo
        
        */
    }
    //Falta por implementar
    //IMPORTANTE: COLOCARLE COMO ARGUMENTO EL PASARLE UN ARCHIVO
    private void loadFile() {
        
    }
    
    /* POSIBLE ESTRUCTURA PARA EL JSON:
    {disk_size: 323,
    root: {
        isDir:true,
        children:{
            file1:{
                //INFO DEL ARCHIVO
                blocks: [5, 123, 6, 126, 9]     //Las posiciones de los bloques del archivo
            }
        }
    }
    
    */
    
}

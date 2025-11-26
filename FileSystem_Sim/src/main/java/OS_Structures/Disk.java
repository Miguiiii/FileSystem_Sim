package OS_Structures;

import Structures.*; 
import java.util.ArrayList; 
import java.util.Collections;
import java.util.Comparator;

public class Disk {
    
    private Block[] memory;     
    private int size;           
    private int freeSpace;      
    private Folder Root; 
    private DISK_SCHEDULE currentSchedule = DISK_SCHEDULE.FIFO;
    private int headPosition = 0; 
    private boolean directionUp = true;
    private java.util.List<IORequest> pendingRequests = new ArrayList<>(); 
    private Queue<Integer> completedRequests = new Queue<>(); 
    private boolean active = true;
    private Thread diskThread;
    private final int DELAY = 500; 

    public Disk(int size) {
        this.size = size;
        this.freeSpace = size;
        this.memory = new Block[size];
     
        for (int i = 0; i < size; i++) {
            memory[i] = new Block(i); 
        }
        
        User sysUser = new User("SYSTEM", true); 
        Root = new Folder("Root", sysUser, null);
    }
    
    public void bootDisk() {
        diskThread = new Thread(() -> {
            while (active) {
                if (!pendingRequests.isEmpty()) {
                    IORequest nextReq = selectNextRequest();
                    if (nextReq != null) {
                        moveHeadTo(nextReq.getHeadDir());
                        processRequest(nextReq);          
                        synchronized(pendingRequests) {
                            pendingRequests.remove(nextReq);
                        }
                    }
                }
                
                try {
                    Thread.sleep(100); 
                } catch (InterruptedException ex) {}
            }
        });
        diskThread.start();
    }
    
    public void setSchedule(DISK_SCHEDULE schedule) {
        this.currentSchedule = schedule;
    }
    
    private IORequest selectNextRequest() {
        if (pendingRequests.isEmpty()) return null;

        switch (currentSchedule) {
            case FIFO:
                return pendingRequests.get(0);
            case SHORTEST_SERVICE_TIME: // SSTF
                return getSSTF();
            case SCAN:
                return getSCAN();
            case C_SCAN:
                return getCSCAN();
            default:
                return pendingRequests.get(0);
        }
    }

    private IORequest getSSTF() {
        IORequest bestReq = null;
        int minDistance = Integer.MAX_VALUE;

        for (IORequest req : pendingRequests) {
            int distance = Math.abs(req.getHeadDir() - headPosition);
            if (distance < minDistance) {
                minDistance = distance;
                bestReq = req;
            }
        }
        return bestReq;
    }

    private IORequest getSCAN() {
        IORequest bestReq = null;
        int bestDistance = Integer.MAX_VALUE;

        for (IORequest req : pendingRequests) {
            int dir = req.getHeadDir();
            if (directionUp) {
                if (dir >= headPosition && (dir - headPosition < bestDistance)) {
                    bestDistance = dir - headPosition;
                    bestReq = req;
                }
            } else {
                if (dir <= headPosition && (headPosition - dir < bestDistance)) {
                    bestDistance = headPosition - dir;
                    bestReq = req;
                }
            }
        }

        if (bestReq == null) {
            directionUp = !directionUp;
            return getSCAN(); 
        }
        
        return bestReq;
    }

    private IORequest getCSCAN() {
        IORequest bestReq = null;
        int bestDistance = Integer.MAX_VALUE;

        for (IORequest req : pendingRequests) {
            int dir = req.getHeadDir();
            if (dir >= headPosition) {
                int dist = dir - headPosition;
                if (dist < bestDistance) {
                    bestDistance = dist;
                    bestReq = req;
                }
            }
        }

        if (bestReq == null) {
            bestDistance = Integer.MAX_VALUE;
            for (IORequest req : pendingRequests) {
                int dist = req.getHeadDir();
                if (dist < bestDistance) {
                    bestDistance = dist;
                    bestReq = req;
                }
            }
        }
        
        return bestReq;
    }
    
    private void moveHeadTo(int targetBlock) {
        if (targetBlock >= 0 && targetBlock < size) {
            this.headPosition = targetBlock;
        }
    }

    private void processRequest(IORequest req) {
        try {
            Thread.sleep(DELAY); 
        } catch (InterruptedException ex) {}

        boolean success = false;
        
        switch (req.getType()) {
            case CREATE: success = create(req); break; 
            case READ:   success = true; break; 
            case UPDATE: 
                success = true; 
                if (req.getNewName() != null && !req.getNewName().isEmpty()) {
                    DiskElement element = req.getElement();
                    Folder parent = element.getParent();
                    if (parent != null) {
                        if (parent.getContents().getValueOfKey(req.getNewName()) == null) {
                            parent.getContents().deleteEntry(element.getName()); 
                            element.setName(req.getNewName());
                            parent.saveElement(element); 
                        }
                    } else {
                        element.setName(req.getNewName());
                    }
                }
                break;
            case DELETE: success = delete(req); break;
        }
        
        if (success) {
            completedRequests.enqueue(req.getId());
        }
    }

    private boolean create(IORequest req) {
        DiskElement element = req.getElement();
        Folder parent = element.getParent();
        if (!req.isContentAFile()) {
            if (parent != null) {
                if (parent.getContents().getValueOfKey(element.getName()) != null) return false;
                parent.saveElement(element);
                return true;
            }
            if (element.getParent() == null && element == Root) return true;
            return false; 
        }

        File file = (File) element;
        int blocksNeeded = file.getSize(); 
        if (blocksNeeded > freeSpace) return false;
        int startBlock = findContiguousSpace(blocksNeeded);
        if (startBlock != -1) {
            for (int i = 0; i < blocksNeeded; i++) {
                memory[startBlock + i].fillBlock();
                freeSpace--;
                memory[startBlock + i].setNext(null); 
            }
            file.setHeadDir(memory[startBlock]);
            this.headPosition = startBlock; 
            if (parent != null) {
                parent.saveElement(file);
            }
            return true;
        }
        return false; 
    }
    
    private int findContiguousSpace(int needed) {
        int result = searchChunk(headPosition, size, needed);
        if (result != -1) return result;
        result = searchChunk(0, headPosition, needed);
        if (result != -1) return result;
        
        return -1;
    }
    
    private int searchChunk(int start, int end, int needed) {
        int currentFreeCount = 0;
        int chunkStart = -1;

        for (int i = start; i < end; i++) {
            if (memory[i].isFree()) {
                if (currentFreeCount == 0) {
                    chunkStart = i; 
                }
                currentFreeCount++;
                if (currentFreeCount == needed) {
                    return chunkStart;
                }
            } else {
                currentFreeCount = 0; 
                chunkStart = -1;
            }
        }
        return -1;
    }

    private boolean delete(IORequest req) {
        DiskElement element = req.getElement();
        Folder parent = element.getParent();
        if (parent != null) {
            parent.getContents().deleteEntry(element.getName()); 
        }

        if (!req.isContentAFile()) return true;
        File file = (File) element;
        int startDir = file.getFileDir(); 
        if (startDir < 0 || startDir >= size) return true;
        this.headPosition = startDir; 

        int blocksToFree = file.getSize();
        for(int i = 0; i < blocksToFree; i++) {
            int blockIndex = startDir + i;
             if (blockIndex < size) {
                memory[blockIndex].emptyBlock(); 
                freeSpace++;
            }
        }
        
        return true;
    }
    
    public boolean addRequest(Process p) {
        IORequest req = new IORequest(p.getId(), p.getElement(), p.getCrud(), p.getOwner(), System.currentTimeMillis());
        req.setNewName(p.getNewName());
        req.setNewParent(p.getNewParent());
        synchronized(pendingRequests) {
            pendingRequests.add(req);
        }
        return true;
    }

    public Queue<Integer> getCompleted() { return completedRequests; }
    public Folder getRoot() { return Root; }
    public Block[] getMemory() { return memory; }
    public int getFreeSpace() { return freeSpace; }
    public int getHeadPosition() { return headPosition; } 
}
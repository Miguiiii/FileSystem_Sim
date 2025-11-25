/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;

/**
 *
 * @author Miguel
 */
public class IORequest {
    
    private int id;
    private int headDir = 0;
    private CRUD type;
    private File file;
    private Folder folder = null;
    private User owner;
    private boolean isFile = false;
    private long arrival;

    public IORequest(int id, DiskElement file, CRUD type, User owner, long dateCreated) {
        this.id = id;
        if (file instanceof File) {
            this.file = (File) file;
            this.headDir = this.file.getFileDir();
            this.isFile = true;
        }
        if (file instanceof Folder) {
            this.folder = (Folder) file;
        }
        this.type = type;
        this.owner = owner;
        this.arrival = dateCreated;
    }
    
    public DiskElement getElement() {
        if (isFile) {
            return file;
        }
        return folder;
    }

    public int getId() {
        return id;
    }

    public int getHeadDir() {
        return headDir;
    }

    public CRUD getType() {
        return type;
    }

    public File getFile() {
        return file;
    }
    
    public Folder getFolder() {
        return folder;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public long getArrival() {
        return arrival;
    }
    
    public boolean isContentAFile() {
        return isFile;
    }
    
}

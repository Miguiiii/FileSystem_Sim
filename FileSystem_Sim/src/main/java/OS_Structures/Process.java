/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;

/**
 *
 * @author Miguel
 */
public class Process {
    
    private int id;
    private CRUD crud;
    private Status status;
    private File file;
    private Folder folder = null;
    private boolean isFile = false;
    private boolean requestCompleted = false;
    private User owner;

    public Process(CRUD crud, DiskElement file, User owner) {
        this.crud = crud;
        if (file instanceof File) {
            this.file = (File) file;
            this.isFile = true;
        }
        if (file instanceof Folder) {
            this.folder = (Folder) file;
        }
        this.status = status.NEW;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public CRUD getCrud() {
        return crud;
    }

    public File getFile() {
        return file;
    }
    
    public Folder getFolder() {
        return folder;
    }
    
    public boolean isContentAFile() {
        return isFile;
    }
    
    public DiskElement getElement() {
        if (isFile) {
            return file;
        }
        return folder;
    }
    
    public Process setStatus(Status status) {
        this.status = status;
        return this;
    }
    
    public Status getStatus() {
        return this.status;
    }

    public User getOwner() {
        return owner;
    }
    
    public Process completeRequest() {
        requestCompleted = true;
        return this;
    }
    
    public boolean isRequestCompleted() {
        return requestCompleted;
    }
    
}

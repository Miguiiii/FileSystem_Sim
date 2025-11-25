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
    private boolean requestCompleted = false;
    private User owner;

    public Process(CRUD crud, File file, User owner) {
        this.crud = crud;
        this.file = file;
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

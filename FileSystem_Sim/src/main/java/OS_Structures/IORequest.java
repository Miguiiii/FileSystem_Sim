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
    private int headDir;
    private CRUD type;
    private File file;

    public IORequest(int id, File file, CRUD type, long dateCreated) {
        this.id = id;
        this.file = file;
        this.headDir = file.getFileDir();
        this.type = type;
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
    
}

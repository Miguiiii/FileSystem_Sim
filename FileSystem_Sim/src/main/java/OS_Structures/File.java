package OS_Structures;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Miguel
 */
public class File implements DiskElement {
    
    private String name;
    private int headDir;
    private int size;
    private Folder parent;
    private User owner;

    public File(String name, int size, User owner) {
        this.name = name;
        this.size = size;
    }
    
    public int getFileDir() {
        return headDir;
    }
    
    public void setHeadDir(Block block) {
        this.headDir = block.getBlockDir();
    }

    public int getSize() {
        return size;
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Folder getParent() {
        return parent;
    }
    
}

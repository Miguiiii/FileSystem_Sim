/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;
import Structures.HashMap;
/**
 *
 * @author Miguel
 */
public class Folder implements DiskElement {
    
    private String name;
    private HashMap<String, DiskElement> contents;
    private Folder parent;

    public Folder(String name) {
        this.name = name;
        this.contents = new HashMap(15);
    }
    
    public void saveElement(DiskElement element) {
        contents.put(element.getName(), element);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public HashMap<String, DiskElement> getContents() {
        return contents;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public Folder getParent() {
        return parent;
    }
    
}

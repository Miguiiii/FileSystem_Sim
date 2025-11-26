/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;

/**
 *
 * @author Miguel
 */
public class Block {
    
    private int blockDir;
    private boolean isFree = true;
    private Block next = null;

    public Block(int blockDir) {
        this.blockDir = blockDir;
    }

    public int getBlockDir() {
        return blockDir;
    }

    public Block getNext() {
        return next;
    }

    public void setNext(Block next) {
        this.next = next;
    }
    
    public boolean isFree() {
        return isFree;
    }
    
    public void emptyBlock() {
        isFree = true;
        next = null;
    }
    
    public void fillBlock() {
        isFree = false;
    }
    
}

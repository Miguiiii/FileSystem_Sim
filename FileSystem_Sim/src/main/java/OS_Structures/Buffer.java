/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;

import Structures.HashMap;
import Structures.Queue;

/**
 *
 * @author Miguel
 */
public class Buffer {
    
    private HashMap<Integer, Block> buffer;
    private Queue<Block> fifo;
    private int maxSize;
    private int size = 0;

    public Buffer(int maxSize) {
        buffer = new HashMap(maxSize);
        this.maxSize = maxSize;
    }
    
    public Block checkBuffer(int dir) {
        return buffer.getValueOfKey(dir);
    }
    
    public void putInBuffer(Block block) {
        if (size == maxSize) {
            buffer.deleteEntry(fifo.dequeue().getBlockDir());
        }
        buffer.put(block.getBlockDir(), block);
        fifo.enqueue(block);
        size++;
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Structures;

/**
 *
 * @author Miguel
 * @param <T> Any Object
 */
public class BinaryHeap<T> extends Heap<T> {

    public BinaryHeap(int maxSize, boolean isMin) {
        super(maxSize, isMin);
    }
    
    public BinaryHeap(int maxSize){
        this(maxSize, true);
    }
    
    @Override
    protected HeapNode<T> createNode(T element, long priority) {
        return new BHNode(element, priority);
    }
    
}

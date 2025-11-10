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
public class BHNode<T> implements HeapNode<T> {
    
    private T element;
    private long priority;
    
    public BHNode(T element, long priority){
        this.element = element;
        this.priority = priority;
    }

    @Override
    public T getElement() {
        return element;
    }

    @Override
    public long getPriority() {
        return priority;
    }
    
    @Override
    public String toString() {
        return this.element.toString();
    }
    
}

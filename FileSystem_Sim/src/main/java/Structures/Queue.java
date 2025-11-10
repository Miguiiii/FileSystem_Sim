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
public class Queue<T> {
    
    private Node<T> head;
    private Node<T> tail;
    private int length;

    public Queue() {
        this.head = null;
        this.tail = null;
        this.length = 0;
    }

    public Node<T> getHead() {
        return head;
    }

    protected void setHead(Node<T> head) {
        this.head = head;
    }

    public Node<T> getTail() {
        return tail;
    }

    public void setTail(Node<T> tail) {
        this.tail = tail;
    }
    
    public int getLength() {
        return length;
    }

    protected void setLength(int length) {
        this.length = length;
    }
    
    public boolean isEmpty() {
        return getHead() == null;
    }
    
    public T peek() {
        try {
            return getHead().getElement();
        }
        catch(NullPointerException e) {
            throw new NullPointerException("Cannot peek an empty Stack");
        }
    }
    
    public void enqueue(T element) {
        Node<T> node = new Node(element);
        if (isEmpty()) {
            setHead(node);
            setTail(node);
            length++;
            return;
        }
        getTail().setNext(node);
        setTail(node);
        length++;
    }
    
    public T dequeue() {
        Node<T> temp = getHead();
        setHead(temp.getNext());
        length--;
        return temp.getElement();
    }
    
}

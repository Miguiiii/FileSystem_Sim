/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Structures;
import java.util.Iterator;
/**
 *
 * @author Miguel
 * @param <T> Any Object
 */
public class Queue<T> implements Iterable<T> {
    
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

    protected void setTail(Node<T> tail) {
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

    @Override
    public Iterator<T> iterator() {
        return new QueueIterator(this);
    }
    
}

class QueueIterator<T> implements Iterator<T> {
    
    Node<T> pointer;
    
    public QueueIterator(Queue list) {
        pointer = list.getHead();
    }
    
    @Override
    public boolean hasNext() {
        return pointer != null;
    }

    @Override
    public T next() {
        T current = pointer.getElement();
        pointer = pointer.getNext();
        return current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
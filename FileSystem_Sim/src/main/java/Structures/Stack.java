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
public class Stack<T> {
    
    private Node<T> head;
    private int length;

    public Stack() {
        this.head = null;
        this.length = 0;
    }
    
    public Node<T> getHead() {
        return head;
    }

    protected void setHead(Node<T> head) {
        this.head = head;
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
    
    public void push(T element) {
        Node<T> node = new Node(element);
        node.setNext(getHead());
        setHead(node);
        length++;
    }
    
    public T pop() {
        if (isEmpty()) {
            throw new UnsupportedOperationException("Cannot pop an element from an empty Stack");
        }
        Node<T> node = getHead();
        setHead(node.getNext());
        length--;
        return node.getElement();
    }

}

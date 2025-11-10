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
public class ArrayList<T> implements Iterable<T> {
    
    private Integer head;
    private int maxSize;
    private int size;
    private ArrayNode<T>[] array;

    public ArrayList(int maxSize) {
        this.head = null;
        this.maxSize = maxSize;
        this.size = 0;
        this.array = new ArrayNode[0]; 

    }

    public Integer getHead() {
        return head;
    }

    public void setHead(Integer head) {
        this.head = head;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayNode[] getArray() {
        return array;
    }

    public void setArray(ArrayNode[] array) {
        this.array = array;
    }
    
    public boolean isEmpty() {
        return getHead() == null;
    }
    
    public T getElmenetAtIndex(int index) {
        T element = null;
        int cont = 0;
        
        if (index < 0 || index >= getSize()) {
            return null;
        }
        
        for (T i:this) {
            if (cont == index) {
                element = i;
                break;
            }
            cont++;
        }
        return element;
    }
    
    private int searchSpace() {
        for (int i = 0; i < getArray().length; i++) {
            if(getArray()[i] == null) {return i;}
        }
        return -1;
    }
    
    public void insertBegin(T element) {
        ArrayNode<T> nodo = new ArrayNode(element);
        if (isEmpty()) {
            if (getArray().length == 0) {
                setArray(new ArrayNode[1]);
                getArray()[0] = nodo;
                setHead(0);
                size++;
                return;
            }
        } else if (getSize() == getMaxSize()) {
            System.out.println("Max Array size reached");
            return;
        }
        int position = searchSpace();
        if (position != -1) {
            nodo.setNext(getHead());
            getArray()[position] = nodo;
            setHead(position);
        } else {
            ArrayNode[] newArray = new ArrayNode[getSize() + 1];
            for (int i = 0; i < getSize(); i++) {
                newArray[i] = getArray()[i];
            }
            nodo.setNext(getHead());
            setHead(newArray.length - 1);
            newArray[newArray.length - 1] = nodo;
            setArray(newArray);
        }
        size++;
        
    }
    
    public void insertFinal(T element) {
        if (isEmpty()) {
            insertBegin(element);
        } else if (getSize() == getMaxSize()) {
            System.out.println("Max Array size reached");
        } else {
            ArrayNode<T> nodo = new ArrayNode(element);
            int position = searchSpace();
            if (position != -1) {
                int pointer = getHead();
                while (getArray()[pointer].getNext() != null) {
                    pointer = getArray()[pointer].getNext();
                }
                getArray()[position] = nodo;
                getArray()[pointer].setNext(position);
            } else {
                ArrayNode[] newArray = new ArrayNode[getSize() + 1];
                for (int i = 0; i < getSize(); i++) {
                    newArray[i] = getArray()[i];
                }
                newArray[newArray.length - 1] = nodo;
                setArray(newArray);
                int pointer = getHead();
                while (getArray()[pointer].getNext() != null) {
                    pointer = getArray()[pointer].getNext();
                }
                getArray()[pointer].setNext(newArray.length - 1);
            }
            size++;
        }
    }

    public void insertAtIndex(T element, int index) {
        ArrayNode nodo = new ArrayNode(element);
        if (isEmpty()) {
            insertBegin(element);
        } else if(getSize() == getMaxSize()) {
            System.out.println("Max Array size reached");
        } else if(index >= 0 && index <= getSize()) { 
            if (index == 0) {
                insertBegin(element);
                return; 
            }
            if (index == getSize()) {
                insertFinal(element);
                return;
            }

            int position = searchSpace();
            if (position != -1) {
                getArray()[position] = nodo;
                int cont = 0;
                int pointer = getHead();
                while (cont < index -1) {
                    pointer = getArray()[pointer].getNext();
                    cont++;
                }
                if (getArray()[pointer].getNext() != null) {
                    int temp = getArray()[pointer].getNext();
                    getArray()[position].setNext(temp);
                }
                getArray()[pointer].setNext(position);
            } else {
                ArrayNode[] newArray = new ArrayNode[getSize() + 1];
                for (int i = 0; i < getSize(); i++) {
                    newArray[i] = getArray()[i];
                }
                newArray[newArray.length - 1] = nodo;
                setArray(newArray);
                int cont = 0;
                int pointer = getHead();
                while (cont < index -1) {
                    pointer = getArray()[pointer].getNext();
                    cont++;
                }
                if (getArray()[pointer].getNext() != null) {
                    int temp = getArray()[pointer].getNext();
                    getArray()[newArray.length - 1].setNext(temp);
                }
                getArray()[pointer].setNext(newArray.length - 1);
            }
            size++;
        } else {
            System.out.println("Invalid index: " + index + " for size: " + getSize());
        }
    }

    public T deleteBegin() {
        if(isEmpty()) {
            System.out.println("The list is empty");
            return null;
        }
        
        ArrayNode<T> pointer = getArray()[getHead()];
        Integer temp = pointer.getNext();
        getArray()[getHead()] = null;
        setHead(temp);
        pointer.setNext(null);
        size--; 
        return pointer.getElement();
    }

    public T deleteFinal() {
        if(isEmpty()) {
            System.out.println("The list is empty");
            return null;
        }
        if (getSize() == 1) {return deleteBegin();}
        
        ArrayNode pointer = getArray()[getHead()];
        while (getArray()[pointer.getNext()].getNext() != null) {
            pointer = getArray()[pointer.getNext()];
        }
        ArrayNode<T> temp = getArray()[pointer.getNext()];
        getArray()[pointer.getNext()] = null;
        pointer.setNext(null);
        size--;
        return temp.getElement();
    }

    public T deleteAtIndex(int index) {
        if(isEmpty()) {
            System.out.println("The list is empty");
        } else if (index < 0 || index >= getSize()) {
             System.out.println("Invalid index: " + index + " for size: " + getSize());
        } else if (index == 0) {
            return deleteBegin();
        } else {
            int cont = 0;
            int pointer = getHead();
            while (cont < index - 1) {
                pointer = getArray()[pointer].getNext();
                cont++;
            }
            ArrayNode current = getArray()[pointer];
            ArrayNode<T> temp = getArray()[current.getNext()];
            getArray()[current.getNext()] = null;
            current.setNext(temp.getNext());
            temp.setNext(null);
            size--;
            return temp.getElement();
        }
        return null;
    }
    
    @Deprecated
    public ArrayNode deleteElement(T element) {
        if (isEmpty()) {
            System.out.println("The list is empty");
        } else {
            System.out.println("Element not found (deleteElement is deprecated and not implemented)");
        }
        
        return null;
    }
    
    public void print() {
        String array = "";
        for (T i:this) {
            array+="["+i+"]";
        }
        System.out.println(array);
    }
    
    public void printInMemory() {
        String array = "";
        for (int i = 0; i < getArray().length; i++) {
            // --- MODIFICACIÓN: Comprobar si es null ---
            if (getArray()[i] != null) {
                array+="["+getArray()[i].getElement()+"]";
            } else {
                array+="[null]";
            }
        }
        System.out.println(array);
        
    }
    
    @Override
    public Iterator iterator() {
        return new ArrayIterator(this);
    }
    
}

class ArrayIterator<T> implements Iterator<T> {
    
    ArrayNode<T>[] array;
    Integer pointer;

    public ArrayIterator(ArrayList array) {
        pointer = array.getHead();
        this.array = array.getArray();
    }
    
    @Override
    public boolean hasNext() {
        return pointer != null;
    }

    @Override
    public T next() {
        ArrayNode<T> current = array[pointer];
        pointer = current.getNext();
        return current.getElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
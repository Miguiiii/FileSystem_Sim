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
public abstract class Heap<T> implements Iterable<T> {
    
    protected ArrayList<HeapNode<T>> heap;
    protected int size;
    protected int maxSize;
    protected boolean isMin;

    public Heap(int maxSize, boolean isMin) {
        this.maxSize = maxSize;
        this.size = 0;
        this.heap = new ArrayList(this.maxSize);
        this.isMin = isMin;
    }
    
    public Heap(int maxSize){
        this(maxSize, true);
    }

    public ArrayList<HeapNode<T>> getHeap() {
        return heap;
    }

    private void setHeap(ArrayList<HeapNode<T>> heap) {
        this.heap = heap;
    }
    
    public int getSize() {
        return size;
    }

    public int getMaxSize() {
        return maxSize;
    }

//    public void setSize(int size) {
//        this.size = size;
//    }
    
    public boolean isMinHeap() {
        return this.isMin;
    }
    
    public HeapNode<T> peekNode() {
        return getHeap().getElmenetAtIndex(0);
    }
    
    public T peek() {
        return peekNode().getElement();
    }
    
    public boolean isElementInHeap(T element) {
        for (int i = 0; i < getSize(); i++) {
            if (getHeap().getElmenetAtIndex(i).getElement() == element) {return true;}
        }
        return false;
    }
    
    public boolean isEmpty() {
        return getSize() == 0;
    }
    
    private int parent(int index) {
        return (index - 1) / 2;
    }
    
    private int leftChild(int index) {
        return 2 * index + 1;
    }
    
    private int rightChild(int index) {
        return 2 * index + 2;
    }
    
    private void swap(int pIndex, int cIndex) {
        ArrayNode prev, parent, child, prevCh;
        prev = parent = child = prevCh = null;
        int count = 0;
        Integer pointer = getHeap().getHead();
        while (count != cIndex) {
            if (pIndex != 0 && count == pIndex - 1) {prev = getHeap().getArray()[pointer];}
            if (count == cIndex-1) {prevCh = getHeap().getArray()[pointer];}
            if (count == pIndex) {parent = getHeap().getArray()[pointer];}
            pointer = getHeap().getArray()[pointer].getNext();
            count++;
        }
        child = getHeap().getArray()[pointer];
        pointer = child.getNext();
        Integer pointer2;
        if (pIndex == 0) {
            if (cIndex == 1) {
                child.setNext(getHeap().getHead());
                getHeap().setHead(parent.getNext());
            } else {
                child.setNext(parent.getNext());
                pointer2 = prevCh.getNext();
                prevCh.setNext(getHeap().getHead());
                getHeap().setHead(pointer2);
            }
        } else {
            pointer2 = prev.getNext();
            prev.setNext(prevCh.getNext());
            child.setNext(parent.getNext());
            prevCh.setNext(pointer2);
        }
        parent.setNext(pointer);
    }
    
    public void Heapify(int index) {
        if (isMinHeap()) {
            minHeapify(index);
            return;
        }
        maxHeapify(index);
    }
    
    private void maxHeapify(int index) {
        int lChild = leftChild(index);
        int rChild = rightChild(index);
        
        int smallest = index;
        
        if (lChild < getSize() && getHeap().getElmenetAtIndex(lChild).getPriority() > getHeap().getElmenetAtIndex(smallest).getPriority()) {
            smallest = lChild;
        } if (rChild < getSize() && getHeap().getElmenetAtIndex(rChild).getPriority() > getHeap().getElmenetAtIndex(smallest).getPriority()) {
            smallest = rChild;
        }
        
        if (smallest != index) {
            swap(index, smallest);
            maxHeapify(smallest);
        }
    }
    
    private void minHeapify(int index) {
        int lChild = leftChild(index);
        int rChild = rightChild(index);
        
        int smallest = index;
        
        if (lChild < getSize() && getHeap().getElmenetAtIndex(lChild).getPriority() < getHeap().getElmenetAtIndex(smallest).getPriority()) {
            smallest = lChild;
        } if (rChild < getSize() && getHeap().getElmenetAtIndex(rChild).getPriority() < getHeap().getElmenetAtIndex(smallest).getPriority()) {
            smallest = rChild;
        }
        
        if (smallest != index) {
            swap(index, smallest);
            minHeapify(smallest);
        }
    }
    
    protected void insertedMin(int current) {
        while (current != 0 && getHeap().getElmenetAtIndex(current).getPriority() < getHeap().getElmenetAtIndex(parent(current)).getPriority()) {
            swap(parent(current), current);
            current = parent(current);
        }
    }
    
    protected void insertedMax(int current) {
        while (current != 0 && getHeap().getElmenetAtIndex(current).getPriority() > getHeap().getElmenetAtIndex(parent(current)).getPriority()) {
            swap(parent(current), current);
            current = parent(current);
        }
    }
    
    protected int insertNode(HeapNode<T> node) {
        if (getSize() == getMaxSize()) {
            ArrayList<HeapNode<T>> newHeap = new ArrayList(getMaxSize() + 5);
            for (int i = 0; i < getSize(); i++) {
                newHeap.insertAtIndex(getHeap().getElmenetAtIndex(i), i);
            }
            setHeap(newHeap);
            this.maxSize = this.maxSize + 5;
        }
        int current = size;
        getHeap().insertFinal(node);
        size++;
        
        return current;
    }
    
    protected abstract HeapNode<T> createNode(T element, long priority);
    
    public void insert(T element, long priority) {
        int current = insertNode(createNode(element, priority));
        if (isMinHeap()) {
            insertedMin(current);
            return;
        }
        insertedMax(current);
    }
    
    public T extractRoot() {
        HeapNode<T> root = extractRootNode();
        if (root==null) {
            return null;
        }
        return root.getElement();
    }
    
    protected HeapNode<T> deleteRootIfExists() {
        if (isEmpty()) {
            System.out.println("The Heap is Empty");
            return null;
        }
        HeapNode<T> root = getHeap().deleteBegin();
        size--;
        return root;
    }
    
    protected void fixHeap() {
        if (size != 0) {
            getHeap().insertBegin(getHeap().getElmenetAtIndex(size - 1));
            getHeap().deleteFinal();
            Heapify(0);
        }
    }
    
    public HeapNode<T> extractRootNode() {
        HeapNode<T> root = deleteRootIfExists();
        fixHeap();
        return root;
    }
    
    public void reloadHeap() {
        ArrayList<HeapNode<T>> heapCopy = new ArrayList(getMaxSize());
        for (int i = 0; i < getSize(); i++) {
            heapCopy.insertAtIndex(getHeap().getElmenetAtIndex(i), i);
        }
        setHeap(new ArrayList(getMaxSize()));
        for (HeapNode<T> i:heapCopy) {
            int current = insertNode(i);
            if (isMinHeap()) {
                insertedMin(current);
                continue;
            }
            insertedMax(current);
        }
    }
    
    public void inverseHeap() {
        this.isMin = !isMinHeap();
        reloadHeap();
    }
    
    @Deprecated
    public T extractElement(HeapNode extract) {
        if (isEmpty()) {
            System.out.println("The Heap is Empty");
            return null;
        }
        
        
        List<T> colaElementos = new List();
        List<Long> colaPrioridades = new List();
        T eliminado = null;

        while (!getHeap().getElmenetAtIndex(0).equals(extract)) {
            colaElementos.insertFinal(getHeap().getElmenetAtIndex(0).getElement());
            colaPrioridades.insertFinal(getHeap().getElmenetAtIndex(0).getPriority());
            extractRoot();
            if (getSize() == 0) {break;}
        }
        if (getSize() != 0) {eliminado = extractRoot();}
        
        while (colaElementos.getLength() != 0) {
            T newElement = colaElementos.getElmenetAtIndex(0);
            long newPrioridad = colaPrioridades.getElmenetAtIndex(0);
            insert(newElement, newPrioridad);
            colaElementos.deleteBegin();
            colaPrioridades.deleteBegin();
        }
        if (eliminado == null) {
            System.out.println("Elemento no encontrado");
            size++;
        }
        size--;
        return eliminado;
    }
    
    public void print() {
        getHeap().print();
    }
 
    public void printInMemory() {
        getHeap().printInMemory();
    }
    
    @Override
    public Iterator iterator() {
        return new ArrayIterator(getHeap());
    }
    
}
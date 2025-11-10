/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package Structures;
import java.util.Iterator;
/**
 *
 * @author Miguel
 * 
 */
public class HashMap<K, V> implements Iterable<HashNode<K, V>> {

    private List<HashNode<K, V>>[] buckets;

    
    private int capacity;
    private int size;

    public HashMap(int capacity) {
        this.capacity = capacity;
        this.size = 0;

        this.buckets = (List<HashNode<K, V>>[]) new List[capacity];

        for (int i = 0; i < capacity; i++) {
            this.buckets[i] = null;
        }

    }
    

    public int getCapacity() {
        return capacity;
    }

    public int getSize() {
        return size;
    }
    
    public boolean isEmpty() {
        return getSize()==0;
    }
    
    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode())%capacity;
    }
    
    public V getValueOfKey(K key) {
        int index = getBucketIndex(key);
        

        List<HashNode<K, V>> bucket = this.buckets[index];

        
        if (bucket == null) {

            return null;
        }
        for (int i = 0; i < bucket.getLength(); i++) {

            if (bucket.getElmenetAtIndex(i).getKey().equals(key)) {

                return bucket.getElmenetAtIndex(i).getValue();
            }
        }

        return null;
    }
    
    public void put(K key, V value) {
        int index = getBucketIndex(key);

        List<HashNode<K, V>> bucket = this.buckets[index];

        
        if (bucket == null) {
            bucket = new List();
            HashNode<K, V> node = new HashNode(key, value);
            bucket.insertBegin(node);

            this.buckets[index] = bucket;

            
            size++; 
        } else {
            int i = 0;
            for (; i < bucket.getLength(); i++) {
                if (bucket.getElmenetAtIndex(i).getKey().equals(key)) {
                    HashNode<K, V> entry = bucket.getElmenetAtIndex(i);
                    entry.setValue(value);
                    break; 
                }
            }
            if (i == bucket.getLength()) {
                HashNode<K, V> entry = new HashNode(key, value);
                bucket.insertBegin(entry);
                size++; 
            }
        }

    }
    
    public V deleteEntry(K key) {
        int index = getBucketIndex(key);
        

        List<HashNode<K, V>> bucket = this.buckets[index];

        
        if (bucket == null) {
            System.out.println("An Entry with this Key does not exist");
            return null;
        }
        int i = 0;
        for (; i < bucket.getLength(); i++) {
            if (bucket.getElmenetAtIndex(i).getKey().equals(key)) {  
                try {
                    HashNode<K, V> entry = bucket.deleteAtIndex(i);
                    size--; 
                    
                    if (bucket.isEmpty()) {
                        this.buckets[index] = null;
                    }
                    
                    return entry.getValue();
                } catch (Exception e) {

                    System.out.println("Error: 'List.java' no tiene el método 'deleteAtIndex' o falló.");
                    return null;
                }
            }
        }
        System.out.println("An Entry with this Key does not exist");
        return null;
    }
   
    
    public List<K> getKeys() {
        List<K> keys = new List();
        for (List<HashNode<K, V>> b : this.buckets) { 
            if (b == null) continue; 
            for (HashNode<K, V> node:b) {
                keys.insertFinal(node.getKey());
            }
        }
        return keys;
    }
    
    public List<V> getValues() {
        List<V> values = new List(); 
        for (List<HashNode<K, V>> b : this.buckets) { 
            if (b == null) continue;
            for (HashNode<K, V> node:b) {
                values.insertFinal(node.getValue());
            }
        }
        return values;
    }
    
    public List<HashNode<K, V>> getPairs() {
        List<HashNode<K, V>> hash = new List();
        for (List<HashNode<K, V>> b : this.buckets) { 
            if (b == null) continue; 
            for (HashNode<K, V> node:b) {
                hash.insertFinal(node);
            }
        }
        return hash;
    }
    
    @Override
    public Iterator<HashNode<K, V>> iterator() {
        List<HashNode<K, V>> hash = new List();
        for (List<HashNode<K, V>> b : this.buckets) { 
            if (b == null) continue; 
            for (HashNode<K, V> node:b) {
                hash.insertFinal(node);
            }
        }
        return new IteratorList(hash); 
    }
 
}
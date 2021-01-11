package HashMap;

import java.util.Iterator;

/*
  The implementation of this HashMap is made with an array of a linked node list (a new node is added to the hash bucket if a collision occurs).

  The class {@code HashMap} supports the following operations:
  <em>put</em> Average Case : O(1), Worst case : O(n)
  <em>get</em> Average Case : O(1), Worst case : O(n)
  <em>delete</em> Average Case : O(1), Worst case : O(n)
  <em>containsKey</em> Average Case : O(1), Worst case : O(n)
  <em>size</em> O(1)
  <em>isEmpty</em> O(1)
  <em>clear</em> O(1)

  The class {@code HashMap} also implements an HashMap iterator.
 */
public class HashMap<KeyType, DataType> implements Iterable<KeyType> {

    private static final int DEFAULT_CAPACITY = 20;
    private static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final int CAPACITY_INCREASE_FACTOR = 2;

    private Node<KeyType, DataType>[] map;
    private int size = 0;
    private int capacity;
    private final float loadFactor; // Compression factor

    public HashMap() { this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR); }

    public HashMap(int initialCapacity) {
        this(initialCapacity > 0 ? initialCapacity : DEFAULT_CAPACITY,
                DEFAULT_LOAD_FACTOR);
    }

    public HashMap(int initialCapacity, float loadFactor) {
        capacity = initialCapacity;
        this.loadFactor = 1 / loadFactor;
        map = new Node[capacity];
    }

    private int hash(KeyType key){
        int keyHash = key.hashCode() % capacity;
        return Math.abs(keyHash);
    }

    private boolean needRehash() {
        return size * loadFactor > capacity;
    }

    public int size() {
        return size;
    }

    public int capacity(){
        return capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void increaseCapacity() {
        capacity = nextPrime(capacity * CAPACITY_INCREASE_FACTOR);
    }

    private int nextPrime(int n) {
        if( n % 2 == 0 )
            n++;
        for( ; !isPrime( n ); n += 2 )
            ;
        return n;
    }

    private boolean isPrime(int n ){

        if( n == 2 || n == 3 )
            return true;

        if( n == 1 || n % 2 == 0 )
            return false;

        for(int i = 3; i * i <= n; i += 2 )
            if( n % i == 0 )
                return false;

        return true;
    }

    private void rehash() {
        Node<KeyType, DataType>[] oldMap = map;
        increaseCapacity();
        clear();
        for (Node<KeyType, DataType> node : oldMap) {
            while (node != null) {
                put(node.key, node.data);
                node = node.next;
            }
        }
    }

    public boolean containsKey(KeyType key) {
        return getNode(hash(key), key) != null;
    }

    public DataType get(KeyType key) {
        Node<KeyType, DataType> currentNode = getNode(hash(key), key);
        return currentNode == null ? null : currentNode.data;
    }

    public DataType put(KeyType key, DataType value)
    {
        int hash = hash(key);
        Node<KeyType, DataType> lastNode = null;
        Node<KeyType, DataType> currentNode = null;
        DataType oldData = null;
        boolean addedNode = false;

        currentNode = map[hash];
        if(currentNode == null) //No nodes with that key found
        {
            map[hash] = new Node<>(key, value);
            addedNode = true;
        }
        else if((currentNode = getNode(hash, key)) != null) { //Key was found: changing its value
            oldData = currentNode.data;
            currentNode.data = value;
        }
        else{ //Key was not found: assigning the new node to the last node
            lastNode = getLastNode(hash);
            lastNode.next = new Node<>(key, value);
            addedNode = true;
        }

        if(addedNode){
            size++;
            if(needRehash())
                rehash();
        }

        return oldData;
    }

    public DataType remove(KeyType key) {
        int hash = hash(key);

        Node<KeyType, DataType> currentNode = getNode(hash, key);
        if(currentNode == null)
            return null;

        Node<KeyType, DataType> prevNode = getPrevNode(hash, key);

        if(prevNode == null) //currentNode is the first Node
            map[hash] = currentNode.next;
        else
            prevNode.next = currentNode.next;

        size--;
        return currentNode.data;
    }

    private Node<KeyType, DataType> getNode(int hash, KeyType key) {

        Node<KeyType, DataType> currentNode = map[hash];
        while (currentNode != null) {
            if (currentNode.key.equals(key)) {
                return currentNode;
            }
            currentNode = currentNode.next;
        }

        return null;
    }

    private Node<KeyType, DataType> getPrevNode(int hash, KeyType key){

        Node<KeyType, DataType> currentNode = map[hash];
        if(currentNode == null)
            return null;

        while(currentNode.next != null)
        {
            if(currentNode.next.key.equals(key))
            {
                return currentNode;
            }
            currentNode = currentNode.next;
        }

        return null;
    }

    private Node<KeyType, DataType> getLastNode(int hash)
    {
        Node<KeyType, DataType> currentNode = map[hash];

        if(currentNode == null)
            return null;

        while(currentNode.next != null)
        {
            currentNode = currentNode.next;
        }

        return currentNode;
    }

    public void clear() {
        map = new Node[capacity];
        size = 0;
    }

    static class Node<KeyType, DataType> {
        final KeyType key;
        DataType data;
        Node<KeyType, DataType> next; // Pointer to the next node within a Linked List

        Node(KeyType key, DataType data)
        {
            this.key = key;
            this.data = data;
            next = null;
        }
    }

    @Override
    public Iterator<KeyType> iterator() {
        return new HashMapIterator();
    }

    private class HashMapIterator implements Iterator<KeyType> {
        private int currentBucket = 0;
        private Node<KeyType, DataType> currentNode = null;
        private int iteratedNodes = 0;

        public boolean hasNext() {
            return iteratedNodes < size;
        }

        public KeyType next() {
            if (!hasNext())
                throw new java.util.NoSuchElementException();

            findNextBucket();
            if (currentNode == null) {
                currentNode = map[currentBucket];
            } else if(currentNode.next != null) {
                currentNode = currentNode.next;
            }
            else {
                currentBucket++;
                currentNode = null;
                findNextBucket();
                currentNode = map[currentBucket];
            }
            iteratedNodes++;
            return currentNode.key;

        }

        private void findNextBucket()
        {
            while (map[currentBucket] == null) {
                currentBucket++;
            }
        }
    }
}

// Structure choice: my own deque (double-ended queue), built from scratch with Node.
// Used for: Up next (MusicPlayer.upNext) and Recently played (MusicPlayer.recent).

import java.util.Iterator;

class MyDeque<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    // Approach: make a new node and put it at the front.
    // the new node points to the old head, and the old head points back to it.
    void addFirst(T value) {
        Node<T> newNode = new Node<>(value);
        newNode.next = head;
        if (head != null) head.prev = newNode;
        else tail = newNode;
        head = newNode;
        size++;
    }

    // Approach: make a new node and put it at the back.
    // the new node points back to the old tail, and the old tail points to it.
    void addLast(T value) {
        Node<T> newNode = new Node<>(value);
        newNode.prev = tail;
        if (tail != null) tail.next = newNode;
        else head = newNode;
        tail = newNode;
        size++;
    }

    // Approach: take the node at the front and move head to the next node.
    // return null if the deque is empty.
    T pollFirst() {
        if (head == null) return null;
        Node<T> firstNode = head;
        head = firstNode.next;
        if (head != null) head.prev = null;
        else tail = null;
        size--;
        return firstNode.value;
    }

    // Approach: take the node at the back and move tail to the previous node.
    // return null if the deque is empty.
    T pollLast() {
        if (tail == null) return null;
        Node<T> lastNode = tail;
        tail = lastNode.prev;
        if (tail != null) tail.next = null;
        else head = null;
        size--;
        return lastNode.value;
    }

    // Approach: look at the head node without removing it.
    // return null if the deque is empty. O(1).
    T peekFirst() {
        if (head == null) return null;
        return head.value;
    }

    // Approach: start at head and walk node by node until we find the value.
    // compare with == so it only matches the exact same object type
    boolean remove(T value) {
        Node<T> currentNode = head;
        while (currentNode != null) {
            if (currentNode.value == value) {
                if (currentNode.prev != null) currentNode.prev.next = currentNode.next;
                else head = currentNode.next;
                if (currentNode.next != null) currentNode.next.prev = currentNode.prev;
                else tail = currentNode.prev;
                size--;
                return true;
            }
            currentNode = currentNode.next;
        }
        return false;
    }

    // Approach: the size counter tells us if there is anything inside. O(1).
    boolean isEmpty() {
        return size == 0;
    }

    // Approach: return the size counter.
    int size() {
        return size;
    }

    // Approach: return an iterator that starts at head and follows next until null.
    // this is what lets MusicPlayer use a for-each loop on the deque (front to back).
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> currentNode = head;

            // Approach: there is more to read as long as we are not past the tail.
            @Override
            public boolean hasNext() {
                return currentNode != null;
            }

            // Approach: save the current value, move to the next node, then return the saved value.
            @Override
            public T next() {
                T nextValue = currentNode.value;
                currentNode = currentNode.next;
                return nextValue;
            }
        };
    }
}
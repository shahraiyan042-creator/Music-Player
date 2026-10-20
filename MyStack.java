// Structure choice: my own stack (last in, first out), built from scratch with linked nodes.
// Used for: History (MusicPlayer.history) and Undo actions (MusicPlayer.actions).

class MyStack<T> {

    // Approach: one node in the stack. "next" points to the node underneath it.
    static class MyStackNode<T> {
        final T value;
        final MyStackNode<T> next;

        // Approach: save the value and the node that sits below it.
        MyStackNode(T value, MyStackNode<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    MyStackNode<T> top;
    private int size;

    // Approach: make a new node that points to the old top, then make it the new top.
    // if the stack was empty, the old top is null
    void push(T value) {
        top = new MyStackNode<>(value, top);
        size++;
    }

    // Approach: take the value from the top node, then move top down to the next node.
    // return null if the stack is empty
    T pop() {
        if (top == null) return null;
        T poppedValue = top.value;
        top = top.next;
        size--;
        return poppedValue;
    }

    // Approach: look at the top value without removing it.
    // return null if the stack is empty. O(1).
    T peek() {
        if (top == null) return null;
        return top.value;
    }

    // Approach: the size counter tells us if there is anything inside. O(1).
    boolean isEmpty() {
        return size == 0;
    }

    // Approach: return the size counter.
    int size() {
        return size;
    }
}
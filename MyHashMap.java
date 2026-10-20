// Structure choice: my own hash map, built from scratch with an array of buckets (separate chaining).
// Used for: Library (MusicPlayer.library, id -> node) and Play counts (MusicPlayer.playCounts, id -> count).

class MyHashMap<T> {

    // Approach: one key/value pair. "next" links to the next entry in the same bucket.
    private static class Entry<T> {
        String key;
        T value;
        Entry<T> next;

        // Approach: save the key and the value. next starts as null (no entry after it yet).
        Entry(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry<T>[] table;
    private int size;

    // Approach: start with an empty table of 30 buckets.
    // 30 is just a random size, resize() method makes it bigger when it gets full.
    MyHashMap() {
        table = new Entry[30];
    }

    // Approach: turn a key into a bucket number from 0 to length - 1.
    // hashCode() turns the String into a number, then % length keeps it inside the table.
    private int indexFor(String key, int length) {
        return Math.abs(key.hashCode() % length);
    }

    // Approach: find the entry with this key, or return null if it isn't there.
    // jump straight to the right bucket, then walk down comparing keys with equals() method.
    private Entry<T> findEntry(String key) {
        if (key == null) return null;

        // jump straight to the right bucket
        Entry<T> currentEntry = table[indexFor(key, table.length)];

        // walk down the chain until we find a matching key
        while (currentEntry != null) {
            if (currentEntry.key.equals(key)) return currentEntry;
            currentEntry = currentEntry.next;
        }
        return null;
    }

    // Approach: use findEntry, then take the value out of the entry.
    // return null if the key isn't in the map
    T get(String key) {
        Entry<T> foundEntry = findEntry(key);
        if (foundEntry == null) return null;
        return foundEntry.value;
    }

    // Approach: the key is in the map if findEntry found an entry for it.
    boolean containsKey(String key) {
        return findEntry(key) != null;
    }

    // Approach: add a key/value pair. If the key already exists, replace its value.
    // a null key is not allowed, so we throw an error for it.
    // a new entry goes at the front of its bucket
    // resize: if the table is more than 3/4 full, make it bigger so chains stay short.
    void put(String key, T value) {
        if (key == null) throw new IllegalArgumentException("null key");

        // if the key is already here, just update its value and stop
        Entry<T> existingEntry = findEntry(key);
        if (existingEntry != null) {
            existingEntry.value = value;
            return;
        }

        // otherwise make a new entry and put it at the front of its bucket
        int bucketIndex = indexFor(key, table.length);
        Entry<T> newEntry = new Entry<>(key, value);
        newEntry.next = table[bucketIndex];
        table[bucketIndex] = newEntry;
        size++;

        // if the table is more than 3/4 full, make it bigger so chains stay short
        if (size > table.length * 3 / 4) resize();
    }

    // Approach: remove a key and return its value, or return null if the key isn't there.
    // walk the bucket's chain and keep track of the entry before the current one (previousEntry).
    // when we find the key, skip over it by pointing previousEntry at currentEntry.next.
    T remove(String key) {
        if (key == null) return null;

        int bucketIndex = indexFor(key, table.length);
        Entry<T> previousEntry = null;              // the entry just before currentEntry in the chain
        Entry<T> currentEntry = table[bucketIndex]; // the entry we are looking at

        while (currentEntry != null) {
            if (currentEntry.key.equals(key)) {
                // found it. skip over it so the chain no longer includes it.
                if (previousEntry == null) table[bucketIndex] = currentEntry.next;   // it was the first in the bucket
                else previousEntry.next = currentEntry.next;                         // it was in the middle or end
                size--;
                return currentEntry.value;
            }
            previousEntry = currentEntry;
            currentEntry = currentEntry.next;
        }
        return null;
    }

    // Approach: return the size counter.
    // put adds 1 and remove subtracts 1
    int size() {
        return size;
    }

    // Approach: make the table twice as big and move every entry into it.
    // the bucket number depends on the table length, so every entry must get a new index.
    private void resize() {
        Entry<T>[] oldTable = table;
        table = new Entry[oldTable.length * 2];

        // go through every old bucket
        for (int oldIndex = 0; oldIndex < oldTable.length; oldIndex++) {
            Entry<T> currentEntry = oldTable[oldIndex];

            // go through every entry in that bucket's chain
            while (currentEntry != null) {
                Entry<T> nextEntry = currentEntry.next;   // save it, because we're about to change currentEntry.next

                // work out the entry's bucket in the bigger table and put it at the front
                int newIndex = indexFor(currentEntry.key, table.length);
                currentEntry.next = table[newIndex];
                table[newIndex] = currentEntry;

                currentEntry = nextEntry;
            }
        }
    }
}
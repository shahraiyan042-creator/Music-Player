import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * CISC 3130 Homework: Build a Music Player
 *
 * Data-structure choices for this implementation:
 * 1. Playlist: a doubly linked list built with the Node class below.
 * 2. Library: Java's HashMap, mapping each song ID to its playlist node.
 * 3. Up next: Java's ArrayDeque, used as a deque at both ends.
 * 4. History: Java's ArrayDeque, used as a stack (push/pop at the front).
 * 5. Play counts: Java's HashMap, mapping each song ID to its play count.
 *
 * The small main method at the bottom prints the assignment's expected output.
 * Keeping it here lets the whole solution stay in this one Java source file.
 */
public class MusicPlayer {

    /** A song's information. A Song object is never changed after creation. */
    public static class Song {
        private final String id;
        private final String title;
        private final String artist;

        public Song(String id, String title, String artist) {
            this.id = id;
            this.title = title;
            this.artist = artist;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getArtist() {
            return artist;
        }

        /** Print a song as its title, which keeps the demo output readable. */
        @Override
        public String toString() {
            return title;
        }
    }

    /**
     * One playlist node. The previous and next links make it possible to
     * unlink a known song in constant time.
     */
    private static class Node {
        private Song song;
        private Node previous;
        private Node next;

        private Node(Song song) {
            this.song = song;
        }
    }

    // The playlist is a doubly linked list with direct references to both ends.
    private Node head;
    private Node tail;

    // The library stores nodes, not just songs, so removal and stepping are fast.
    private final Map<String, Node> library = new HashMap<String, Node>();

    // The front of this deque is the next song that will be played.
    private final Deque<Song> upNext = new ArrayDeque<Song>();

    // The front of this deque is the top of the history stack.
    private final Deque<Song> history = new ArrayDeque<Song>();

    // Counts are removed when a song is removed from the library.
    private final Map<String, Integer> playCounts = new HashMap<String, Integer>();

    // The song currently playing, or null when playback has not started.
    private Song current;

    /**
     * Add one song to the end of the playlist and add its node to the library.
     * Return false when another song already uses the same ID.
     */
    public boolean addSong(String id, String title, String artist) {
        if (library.containsKey(id)) {
            return false;
        }

        Song song = new Song(id, title, artist);
        Node newNode = new Node(song);

        // If the playlist is empty, this node is both its first and last node.
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            // Attach the new node after the current tail.
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
        }

        // Keep the node so later lookup, removal, and next-song steps are fast.
        library.put(id, newNode);
        return true;
    }

    /** Find a song by ID using the library hash map. */
    public Song findSong(String id) {
        Node node = library.get(id);
        if (node == null) {
            return null;
        }
        return node.song;
    }

    /** Return all playlist titles in their current order. */
    public List<String> showPlaylist() {
        List<String> titles = new ArrayList<String>();

        // Walk from the first node to the last node.
        for (Node node = head; node != null; node = node.next) {
            titles.add(node.song.title);
        }

        return titles;
    }

    /**
     * Start the song with this ID. The previous current song goes onto history
     * before the new song starts. Unknown IDs leave the player unchanged.
     */
    public Song play(String id) {
        Node node = library.get(id);
        if (node == null) {
            return null;
        }

        if (current != null) {
            history.push(current);
        }

        return startSong(node.song);
    }

    /** Return the song currently playing, or null if playback has not started. */
    public Song nowPlaying() {
        return current;
    }

    /** Return the number of times this song has started playing. */
    public int playCount(String id) {
        Integer count = playCounts.get(id);
        if (count == null) {
            return 0;
        }
        return count;
    }

    /** Add a known song to the back of the up-next deque. */
    public boolean queueLater(String id) {
        Node node = library.get(id);
        if (node == null) {
            return false;
        }

        upNext.addLast(node.song);
        return true;
    }

    /** Add a known song to the front of the up-next deque. */
    public boolean queueNext(String id) {
        Node node = library.get(id);
        if (node == null) {
            return false;
        }

        upNext.addFirst(node.song);
        return true;
    }

    /**
     * Return up-next titles from front to back. Removed songs are filtered out
     * because their old Song objects can still be waiting in the deque.
     */
    public List<String> showUpNext() {
        List<String> titles = new ArrayList<String>();

        // ArrayDeque's iterator visits entries from the front toward the back.
        for (Iterator<Song> iterator = upNext.iterator(); iterator.hasNext();) {
            Song song = iterator.next();
            if (isSongInLibrary(song)) {
                titles.add(song.title);
            }
        }

        return titles;
    }

    /**
     * Start the first valid queued song. If none is queued, start the song
     * after current in the playlist. If neither exists, change nothing.
     */
    public Song next() {
        Song songToStart = null;
        boolean cameFromQueue = false;

        // Find a usable queued song before changing current or history.
        for (Iterator<Song> iterator = upNext.iterator(); iterator.hasNext();) {
            Song queuedSong = iterator.next();
            if (isSongInLibrary(queuedSong)) {
                songToStart = queuedSong;
                cameFromQueue = true;
                break;
            }
        }

        // If no queued song is usable, use the playlist node after current.
        if (songToStart == null && current != null) {
            Node currentNode = library.get(current.id);
            if (currentNode != null && currentNode.next != null) {
                songToStart = currentNode.next.song;
            }
        }

        // There is no next song, so current and history stay as they were.
        if (songToStart == null) {
            return null;
        }

        if (cameFromQueue) {
            // Drop removed entries before the chosen song, then consume it.
            Song removedFromQueue;
            do {
                removedFromQueue = upNext.pollFirst();
            } while (removedFromQueue != songToStart);
        }

        // A normal forward move saves the old current song for the back button.
        if (current != null) {
            history.push(current);
        }

        return startSong(songToStart);
    }

    /**
     * Return to the newest still-existing history song. The current song is
     * placed at the front of up-next so next() can return to it.
     */
    public Song back() {
        Song songToStart = null;

        // Peek through history first, skipping songs that have been removed.
        for (Iterator<Song> iterator = history.iterator(); iterator.hasNext();) {
            Song historySong = iterator.next();
            if (isSongInLibrary(historySong)) {
                songToStart = historySong;
                break;
            }
        }

        // No valid history song means playback and the queue stay unchanged.
        if (songToStart == null) {
            return null;
        }

        // Pop removed entries and the selected song from the stack.
        Song removedFromHistory;
        do {
            removedFromHistory = history.pop();
        } while (removedFromHistory != songToStart);

        // Do not push the old current song onto history during a back action.
        if (current != null) {
            upNext.addFirst(current);
        }

        return startSong(songToStart);
    }

    /** Return history titles from newest to oldest, hiding removed songs. */
    public List<String> showHistory() {
        List<String> titles = new ArrayList<String>();

        // The ArrayDeque iterator starts at the stack's top (most recent item).
        for (Iterator<Song> iterator = history.iterator(); iterator.hasNext();) {
            Song song = iterator.next();
            if (isSongInLibrary(song)) {
                titles.add(song.title);
            }
        }

        return titles;
    }

    /**
     * Remove a known non-current song from the playlist and library. Old
     * references in up-next or history are left for those methods to skip.
     */
    public boolean removeSong(String id) {
        Node node = library.get(id);
        if (node == null || (current != null && current == node.song)) {
            return false;
        }

        // Reconnect the previous node, or move the head when removing the head.
        if (node.previous == null) {
            head = node.next;
        } else {
            node.previous.next = node.next;
        }

        // Reconnect the next node, or move the tail when removing the tail.
        if (node.next == null) {
            tail = node.previous;
        } else {
            node.next.previous = node.previous;
        }

        library.remove(id);
        playCounts.remove(id);
        return true;
    }

    /**
     * One place for starting songs: set current and add one to the play count.
     * play(), next(), and back() all use this helper.
     */
    private Song startSong(Song song) {
        current = song;

        Integer oldCount = playCounts.get(song.id);
        if (oldCount == null) {
            playCounts.put(song.id, 1);
        } else {
            playCounts.put(song.id, oldCount + 1);
        }

        return song;
    }

    /**
     * Check both the ID and object identity. The identity check prevents an old
     * queued song from appearing valid if its ID is later reused.
     */
    private boolean isSongInLibrary(Song song) {
        Node node = library.get(song.id);
        return node != null && node.song == song;
    }

    /** Print the assignment's example run so its output can be copied. */
    public static void main(String[] args) {
        MusicPlayer player = new MusicPlayer();

        System.out.println("--- 1. Build the playlist ---");
        System.out.println(player.addSong("s1", "Night Drive", "Artist 1"));
        System.out.println(player.addSong("s2", "Slow Tide", "Artist 2"));
        System.out.println(player.addSong("s3", "Copper Sky", "Artist 3"));
        System.out.println(player.addSong("s4", "Static Bloom", "Artist 4"));
        System.out.println(player.addSong("s5", "Low Orbit", "Artist 5"));
        System.out.println(player.addSong("s6", "Glass Rivers", "Artist 6"));
        System.out.println("duplicate s3: " + player.addSong("s3", "Duplicate", "Artist X"));
        System.out.println("playlist: " + player.showPlaylist());

        System.out.println("--- 2. Look up songs ---");
        System.out.println("find s4: " + player.findSong("s4"));
        System.out.println("find s9: " + player.findSong("s9"));

        System.out.println("--- 3. Play and move forward ---");
        System.out.println("play s1: " + player.play("s1"));
        System.out.println("play s9: " + player.play("s9"));
        System.out.println("next: " + player.next());
        System.out.println("next: " + player.next());
        System.out.println("history: " + player.showHistory());

        System.out.println("--- 4. Up next ---");
        System.out.println(player.queueLater("s1"));
        System.out.println(player.queueLater("s5"));
        System.out.println(player.queueLater("s6"));
        System.out.println("queue s9: " + player.queueLater("s9"));
        System.out.println("up next: " + player.showUpNext());
        System.out.println("next: " + player.next());
        System.out.println("now playing: " + player.nowPlaying());
        System.out.println("history: " + player.showHistory());

        System.out.println("--- 5. Back button ---");
        System.out.println("back: " + player.back());
        System.out.println("up next: " + player.showUpNext());
        System.out.println("history: " + player.showHistory());

        System.out.println("--- 6. Remove songs ---");
        System.out.println("remove s5: " + player.removeSong("s5"));
        System.out.println("remove current (s3): " + player.removeSong("s3"));
        System.out.println("remove s9: " + player.removeSong("s9"));
        System.out.println("playlist: " + player.showPlaylist());
        System.out.println("up next: " + player.showUpNext());

        System.out.println("--- 7. Removed songs are skipped ---");
        System.out.println("next: " + player.next());
        System.out.println("next: " + player.next());
        System.out.println("next at end: " + player.next());
        System.out.println("now playing: " + player.nowPlaying());
        System.out.println("back: " + player.back());
        System.out.println("up next: " + player.showUpNext());

        System.out.println("--- 8. Play counts ---");
        System.out.println("s1: " + player.playCount("s1"));
        System.out.println("s3: " + player.playCount("s3"));
        System.out.println("s6: " + player.playCount("s6"));
        System.out.println("s5 (removed): " + player.playCount("s5"));

        System.out.println("--- 9. Empty player ---");
        MusicPlayer emptyPlayer = new MusicPlayer();
        System.out.println("next: " + emptyPlayer.next());
        System.out.println("back: " + emptyPlayer.back());
        System.out.println("now playing: " + emptyPlayer.nowPlaying());
        System.out.println("playlist: " + emptyPlayer.showPlaylist());
    }
}

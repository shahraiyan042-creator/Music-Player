// Structure choices (all Built from scratch except for ArrayList and List ):
// Playlist    : Built my own doubly linked list (Node, head/tail inside MusicPlayer)
// Library     : Built my own hash map (MyHashMap), id -- playlist node
// Up next     : Built my own deque (MyDeque) using Node
// History     : Built my own stack (MyStack) using linked nodes
// Play counts : Built my own hash map (MyHashMap), id -- count

import java.util.ArrayList;
import java.util.List;
import java.util.Random; //used for Shuffle Method

public class MusicPlayer {
    private static final int RECENT_LIMIT = 10;

    // Remembers one add/remove so undo() can reverse it.
    // We save the node itself so undo can put it back in the same spot.
    private static class Action {
        final boolean wasAdded;
        final Song song;
        final Node<Song> songNode;
        final int savedPlayCount;

        Action(boolean wasAdded, Song song, Node<Song> songNode, int savedPlayCount) {
            this.wasAdded = wasAdded;
            this.song = song;
            this.songNode = songNode;
            this.savedPlayCount = savedPlayCount;
        }
    }

    private Node<Song> head;
    private Node<Song> tail;
    private final MyHashMap<Node<Song>> library = new MyHashMap<>();
    private final MyDeque<Song> upNext = new MyDeque<>();
    private final MyStack<Song> history = new MyStack<>();
    private final MyHashMap<Integer> playCounts = new MyHashMap<>();
    private Song current;

    private final MyStack<Action> actions = new MyStack<>();
    private final MyDeque<Song> recent = new MyDeque<>();
    private boolean repeat;
    private final Random random = new Random();

    // Approach: a song is "live" if the library still holds this exact song. if null return false
    private boolean isLive(Song song) {
        Node<Song> libraryNode = library.get(song.id);
        if (libraryNode == null) {
            return false;
        }
        return libraryNode.value == song;
    }

    // Approach: look the id up in the play counts map.
    // a song that was never played has no entry, so we return 0 for it.
    public int playCount(String id) {
        Integer savedCount = playCounts.get(id);
        if (savedCount == null) {
            return 0;
        }
        return savedCount;
    }

    // Approach: make this song current, add 1 to its count, update recent.
    // remove then addFirst: if the song is already in recent, this moves it to the front.
    // if too many songs Remove the one at the back (the one played longest ago) using pollLast.
    private void start(Song song) {
        current = song;
        playCounts.put(song.id, playCount(song.id) + 1);
        recent.remove(song);
        recent.addFirst(song);
        if (recent.size() > RECENT_LIMIT) {
            recent.pollLast(); //pollLast() method from MyDeque.java
        }
    }

    // Approach: skip over this node by pointing its neighbors at each other.
    // the  is doubly linked, so we reach both neighbors from the node
    private void unlink(Node<Song> songNode) {
        if (songNode.prev != null) {
            songNode.prev.next = songNode.next;
        } else {
            head = songNode.next;
        }

        if (songNode.next != null) {
            songNode.next.prev = songNode.prev;
        } else {
            tail = songNode.prev;
        }
    }

    // Approach: the opposite of unlink. Point the neighbors back at this node.
    // unlink left the node's prev/next alone, so it knows its position
    private void relink(Node<Song> songNode) {
        if (songNode.prev != null) {
            songNode.prev.next = songNode;
        } else {
            head = songNode;
        }

        if (songNode.next != null) {
            songNode.next.prev = songNode;
        } else {
            tail = songNode;
        }
    }

    // Approach: add a new node at the end of the play.
    // Using tail pointer , adding at the end takes O(1).
    // MyHashMap<Node<Song song>> library: saves id -> node so every other method can find the song
    public boolean addSong(String id, String title, String artist) {
        if (library.containsKey(id)) {
            return false;
        }
        Song newSong = new Song(id, title, artist);
        Node<Song> newNode = new Node<>(newSong);
        newNode.prev = tail;
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        library.put(id, newNode);
        actions.push(new Action(true, newSong, newNode, 0));
        return true;
    }

    // Approach: one lookup in the library map.
    // the map stores the node, so we take the song out of it.
    public Song findSong(String id) {
        Node<Song> songNode = library.get(id);
        if (songNode == null) {
            return null;
        }
        return songNode.value;
    }

    // Approach: start at head and follow next until the end.
    // the play is a linked , so traverse one by one O(n).
    public List<String> showPlaylist() {
        List<String> titles = new ArrayList<>();
        Node<Song> currentNode = head;
        while (currentNode != null) {
            titles.add(currentNode.value.title);
            currentNode = currentNode.next;
        }
        return titles;
    }

    // Approach: find the song, save the old current song to history, start the new one.
    // history: back() needs the previous songs, and a stack gives the new song.
    public Song play(String id) {
        Node<Song> songNode = library.get(id);
        if (songNode == null) {
            return null;
        }
        if (current != null) {
            history.push(current);
        }
        start(songNode.value);
        return songNode.value;
    }

    // Approach:  return the current song variable.
    public Song nowPlaying() {
        return current;
    }

    // Approach: find the song, then add it to the back of up next.
    //  up next is a queue, so add to the end of the line.
    public boolean queueLater(String id) {
        Node<Song> songNode = library.get(id);
        if (songNode == null) {
            return false;
        }
        upNext.addLast(songNode.value);
        return true;
    }

    // Approach: find the song, then add it to the front of up next.
    // next() takes from the front, so this song plays first.
    public boolean queueNext(String id) {
        Node<Song> songNode = library.get(id);
        if (songNode == null) {
            return false;
        }
        upNext.addFirst(songNode.value);
        return true;
    }

    // Approach: go through up next and only keep songs that are still live.
    // removed songs are still in the deque, so we skip them here.
    public List<String> showUpNext() {
        List<String> titles = new ArrayList<>();
        for (Song queuedSong : upNext) {
            if (isLive(queuedSong)) {
                titles.add(queuedSong.title);
            }
        }
        return titles;
    }

    // Approach: pick the next song in two steps.
    // Step 1: take from the front of up next, skipping removed songs.
    // Step 2: if up next gave nothing, use the node after the current one.
    // HashMap<Node<Song song>> library: it gives us the current song's node, and node.next is the next song.
    // repeat: at the end of the playlist, repeat sends us back to the head pointer.
    public Song next() {
        Song nextSong = null;

        while (nextSong == null && !upNext.isEmpty()) {
            Song queuedSong = upNext.pollFirst();
            if (isLive(queuedSong)) {
                nextSong = queuedSong;
            }
        }

        if (nextSong == null && current != null) {
            Node<Song> nodeAfterCurrent = library.get(current.id).next;
            if (nodeAfterCurrent != null) {
                nextSong = nodeAfterCurrent.value;
            } else if (repeat) {
                nextSong = head.value;
            }
        }

        if (nextSong == null) {
            return null;
        }
        if (current != null) {
            history.push(current);
        }
        start(nextSong);
        return nextSong;
    }

    // Approach: pop history until we find a live song, then go back to it.
    // removed songs are skipped
    // addFirst: the song we leave goes to the front of up next, so next() brings us right back to it.
    public Song back() {
        Song previousSong = null;

        while (previousSong == null && !history.isEmpty()) {
            Song historySong = history.pop();
            if (isLive(historySong)) {
                previousSong = historySong;
            }
        }

        if (previousSong == null) {
            return null;
        }
        upNext.addFirst(current);
        start(previousSong);
        return previousSong;
    }

    // Approach: walk the stack from the top (newest) down, keep only live songs.
    // the stack top is the most recent song, so this prints newest first.
    public List<String> showHistory() {
        List<String> titles = new ArrayList<>();
        MyStack.MyStackNode<Song> currentNode = history.top;
        while (currentNode != null) {
            if (isLive(currentNode.value)) {
                titles.add(currentNode.value.title);
            }
            currentNode = currentNode.next;
        }
        return titles;
    }

    // Approach: find the node, cut it out of the list, and delete its map entries.
    //save the play count: undo can bring the count back.
    // leave up next and history alone: isLive() skips the removed song there.
    public boolean removeSong(String id) {
        Node<Song> songNode = library.get(id);
        if (songNode == null) {
            return false;
        }
        if (current != null && current.id.equals(id)) {
            return false;
        }
        int savedPlayCount = playCount(id);
        unlink(songNode);
        library.remove(id);
        playCounts.remove(id);
        recent.remove(songNode.value);
        actions.push(new Action(false, songNode.value, songNode, savedPlayCount));
        return true;
    }

    // Approach: look at the last action on the stack and reverse 1 step.
    // stack: the newest action is on top, so we undo in reverse order.
    public boolean undo() {
        Action lastAction = actions.peek();
        if (lastAction == null) {
            return false;
        }

        if (lastAction.wasAdded) {
            if (current == lastAction.song) {
                return false;
            }
            unlink(lastAction.songNode);
            library.remove(lastAction.song.id);
            playCounts.remove(lastAction.song.id);
            recent.remove(lastAction.song);
        } else {
            relink(lastAction.songNode);
            library.put(lastAction.song.id, lastAction.songNode);
            if (lastAction.savedPlayCount > 0) {
                playCounts.put(lastAction.song.id, lastAction.savedPlayCount);
            }
        }

        actions.pop();
        return true;
    }

    // Approach: save the on/off value. next() checks it at the end of the playlist.
    public void setRepeat(boolean repeatOn) {
        repeat = repeatOn;
    }

    // Approach: list the recent deque from front to back.
    // start() keeps the newest song at the front
    public List<String> recentlyPlayed() {
        List<String> titles = new ArrayList<>();
        for (Song recentSong : recent) {
            titles.add(recentSong.title);
        }
        return titles;
    }

    // Approach: 3 steps.
    // Step 1: collect the songs that were played at least once.
    // Step 2: bubble sort them so the most played come first.
    // Step 3: keep only the first "limit" titles.
    public List<String> topSongs(int limit) {
        List<Song> playedSongs = new ArrayList<>();
        Node<Song> currentNode = head;
        while (currentNode != null) {
            if (playCount(currentNode.value.id) > 0) {
                playedSongs.add(currentNode.value);
            }
            currentNode = currentNode.next;
        }

        int lastIndex = playedSongs.size() - 1;
        while (lastIndex > 0) {
            int index = 0;
            while (index < lastIndex) {
                Song firstSong = playedSongs.get(index);
                Song secondSong = playedSongs.get(index + 1);
                if (playCount(firstSong.id) < playCount(secondSong.id)) {
                    playedSongs.set(index, secondSong);
                    playedSongs.set(index + 1, firstSong);
                }
                index++;
            }
            lastIndex--;
        }

        List<String> titles = new ArrayList<>();
        int position = 0;
        while (position < playedSongs.size() && position < limit) {
            titles.add(playedSongs.get(position).title);
            position++;
        }
        return titles;
    }

    // Approach: empty up next, copy the playlist into a list, shuffle the list,
    // then add every song to up next.
    // copy: we shuffle the copy so the real playlist order never changes.
    // from the end, swap each spot with a random earlier spot.
    public void shuffleUpNext() {
        while (!upNext.isEmpty()) {
            upNext.pollFirst();
        }

        List<Song> songs = new ArrayList<>();
        Node<Song> currentNode = head;
        while (currentNode != null) {
            songs.add(currentNode.value);
            currentNode = currentNode.next;
        }

        int lastPosition = songs.size() - 1;
        while (lastPosition > 0) {
            int randomPosition = random.nextInt(lastPosition + 1);
            Song swapSong = songs.get(lastPosition);
            songs.set(lastPosition, songs.get(randomPosition));
            songs.set(randomPosition, swapSong);
            lastPosition--;
        }

        for (Song shuffledSong : songs) {
            upNext.addLast(shuffledSong);
        }
    }
}

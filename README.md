# Music Player

## Overview

This Java project models a music player using linked data structures and hash maps. It supports an ordered playlist, song lookup, an “up next” queue, playback history, and play counts. It models player behavior but does not play audio files.

## Features

- Add songs, look them up by ID, and display the playlist.
- Play a song, move to the next song, or go back to a previous song.
- Add songs to the front or back of “up next.”
- Remove songs, skip removed songs still in the queue or history, and track play counts.
- Extra features: undo, repeat mode, recently played, top songs, and shuffle.

## Data Structures

| Job | Structure |
|---|---|
| Playlist | Custom doubly linked list using `Node<Song>` |
| Song library | Custom `MyHashMap`, mapping IDs to playlist nodes |
| Up next | Custom linked `MyDeque<Song>` |
| History | Custom linked `MyStack<Song>` |
| Play counts | Custom `MyHashMap`, mapping IDs to counts |

The undo actions use another `MyStack`, and recently played songs use a `MyDeque`. `ArrayList` is used for returned title lists and for copying songs before a shuffle.

## Big-O

The table gives expected or amortized time. Let **n** be the number of songs and **k** be the number of removed songs skipped during a `next()` or `back()` call.

| Method | Structures used | Time | Reason |
|---|---|---:|---|
| `addSong` | Playlist, library, undo stack | O(1) expected/amortized | Appends at the tail, inserts in the map, and records the action. |
| `findSong` | Library | O(1) expected | Looks up the ID in the hash map. |
| `play` | Library, history, play counts, recent deque | O(1) expected | Uses map lookups and constant-time stack/deque updates. |
| `queueLater` | Library, up-next deque | O(1) expected | Looks up the song and adds it to the back. |
| `queueNext` | Library, up-next deque | O(1) expected | Looks up the song and adds it to the front. |
| `next` | Up-next deque, library, playlist, history, play counts | O(k + 1) expected | May skip k removed songs in the queue before selecting a song. |
| `back` | History, library, up-next deque, play counts | O(k + 1) expected | May skip k removed songs in history before finding one to play. |
| `removeSong` | Library, playlist, play counts, recent deque, undo stack | O(1) expected | Finds and unlinks the node, updates the maps, and records the action. |
| `showPlaylist` | Playlist | O(n) | Visits each playlist node to collect its title. |

`MyHashMap` uses separate chaining and resizes as it fills. A long collision chain can make a map operation O(n), and a resize takes O(n); map operations are expected O(1), with insertion amortized O(1).

## Design Choices

**Playlist:** I used a custom doubly linked list and stored each song’s node in the library. This lets the player append a song or remove a known node in O(1), and `next()` can follow the node’s `next` link. With a plain array, removing an item would shift later items and take O(n). Java’s `LinkedList` does not expose its internal nodes, so removing by ID would require a search unless a separate index were added.

**Library:** I used a custom hash map for song lookup and play counts. With separate chaining, lookups and updates take expected O(1), while searching an unsorted array by ID would take O(n). Java’s built-in `HashMap` offers similar expected lookup time with less implementation work; I built this map to handle buckets, collisions, and resizing directly.

## Compile and Run

Run these commands from the project folder:

```bash
javac *.java
java Test
```

To run the extra-feature examples:

```bash
java ExtraTest
```

`Test.java` covers the required player behavior, and `ExtraTest.java` covers the additional features.

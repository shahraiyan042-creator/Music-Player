# Music Player

A Java project that acts like the engine of a music player. It keeps an ordered playlist, an "up next" list, a back button, and fast song lookup. It doesn't play real audio. It only models how a player behaves.

## Table of contents

- [Features](#features)
- [Getting started](#getting-started)
- [Project structure](#project-structure)
- [Data structures](#data-structures)
- [Big-O](#big-o)
- [Design choices](#design-choices)

## Features

- Add songs, find them by ID, and print the playlist
- Play a song, go to the next one, or go back
- Add songs to the front or back of "up next"
- Remove songs, skip removed ones that are still in the queue or history, and keep play counts
- Extras: undo, repeat mode, recently played, top songs, shuffle

## Getting started

### Prerequisites

- A JDK with `javac` and `java` available

### Compile and run

From the project folder:

```bash
javac *.java
java Test
```

To run the extra-feature examples:

```bash
java ExtraTest
```

> [!NOTE]
> `Test.java` covers the required behavior. `ExtraTest.java` covers the extra credit methods

## Project structure

```text
.
├── MusicPlayer.java   # the player and its five structures
├── MyHashMap.java     # hash map with separate chaining
├── MyDeque.java       # linked deque
├── MyStack.java       # linked stack
├── Test.java          # required behavior
└── ExtraTest.java     # extra features
```


## Data structures

| Job | What I used |
|---|---|
| Playlist | My own doubly linked list (`Node<Song>`) |
| Song library | My own `MyHashMap`, ID → playlist node |
| Up next | My own linked `MyDeque<Song>` |
| History | My own linked `MyStack<Song>` |
| Play counts | `MyHashMap`, ID → count |

Undo uses a second `MyStack`, and recently played uses another `MyDeque`. I did use `ArrayList` in two places: for the lists of titles that the show methods return, and for copying songs before a shuffle.

## Big-O

Here **n** is the number of songs, and **k** is how many removed songs `next()` or `back()` has to skip over. Times are expected or amortized.

| Method | Structures | Time | Why |
|---|---|---:|---|
| `addSong` | Playlist, library, undo stack | O(1) | Adds at the tail, puts it in the map, logs the action |
| `findSong` | Library | O(1) | One hash map lookup |
| `play` | Library, history, play counts, recent deque | O(1) | Only map lookups and push/add operations |
| `queueLater` | Library, up next | O(1) | Look up the song, add to the back |
| `queueNext` | Library, up next | O(1) | Look up the song, add to the front |
| `next` | Up next, library, playlist, history, play counts | O(k + 1) | Might skip k removed songs before it finds one to play |
| `back` | History, library, up next, play counts | O(k + 1) | Might skip k removed songs in history |
| `removeSong` | Library, playlist, play counts, recent deque, undo stack | O(1) | Get the node from the map and unlink it |
| `showPlaylist` | Playlist | O(n) | Has to walk every node |

> [!NOTE]
> `MyHashMap` uses separate chaining and resizes as it fills up. A bad collision chain could make one operation O(n), and a resize is O(n), but on average the operations are O(1) and inserts are amortized O(1).

## Design choices

### Playlist

I built a doubly linked list and made the library store each song's node. That way adding to the end or removing a node I already have is O(1), and `next()` just follows the node's `next` pointer. A plain array would be worse because removing from the middle shifts everything after it, which is O(n). Java's `LinkedList` doesn't let you hold onto its nodes, so removing by ID would mean searching for it first, unless I added a separate index.

### Library

I wrote my own hash map for song lookup and for play counts. With chaining, lookups and updates are O(1) on average, while searching an unsorted array by ID is O(n). Java's `HashMap` would give about the same speed with a lot less work. 

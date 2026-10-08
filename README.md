CISC 3130 Homework: Build a Music Player
Big-O table and design justifications

Let n be the number of playlist songs, q the number of up-next entries, and h
the number of history entries. HashMap operations below are average/expected.

Method       Structures it touches              Big-O       One-line reason
addSong      playlist, library                 O(1) avg.   Append at tail; add one map entry.
findSong     library                           O(1) avg.   Look up the ID in the HashMap.
play         library, history, play counts     O(1) avg.   Map lookup, stack push, and count update.
queueLater   library, up-next                  O(1) avg.   Map lookup and deque addLast.
queueNext    library, up-next                  O(1) avg.   Map lookup and deque addFirst.
next         up-next, playlist, history,       O(q)       May scan queued entries to skip removed songs;
             library, play counts                         the playlist step and start update are O(1).
back         history, up-next, library,        O(h)       May scan history to skip removed songs;
             play counts                                  the deque and start updates are O(1).
removeSong   playlist, library, play counts    O(1) avg.   Find its node, unlink it, and delete map entries.
showPlaylist playlist                           O(n)       Visit every playlist node once.

Design choice 1: Doubly linked playlist

I used a doubly linked list to keep songs in order and move from the current
song to the next one. The library points directly to each song's node, so
removing a known song or finding the next playlist song takes O(1) time. With a
plain array, finding a song by ID would require a scan, and removing it would
shift later elements, taking O(n) time.

Design choice 2: HashMap library

I used Java's HashMap to map each song ID to its playlist node. This makes
findSong an average O(1) lookup and gives removeSong and next() the node they
need without scanning the playlist. A plain array would need an O(n) ID search,
and storing only a song in the map would still leave removal needing a scan.
The assignment requires a hash map, and Java's built-in provides the lookup
without requiring me to implement hashing myself.

//This is a separate file for testing the methods in extra credit column.
// Includes Extra Credit Methods.

public class ExtraTest {
    static MusicPlayer create() {
        MusicPlayer player = new MusicPlayer();
        player.addSong("s1", "Night Drive", "Vela");
        player.addSong("s2", "Slow Tide", "Marlow");
        player.addSong("s3", "Copper Sky", "Vela");
        player.addSong("s4", "Static Bloom", "Orrin");
        return player;
    }

    public static void main(String[] args) {
        MusicPlayer player = create();

        System.out.println("--- Undo ---");
        System.out.println("undo add s4: " + player.undo());
        System.out.println("playlist: " + player.showPlaylist());
        System.out.println("find s4: " + player.findSong("s4"));
        player.play("s1");
        System.out.println("remove s2: " + player.removeSong("s2"));
        System.out.println("remove s3: " + player.removeSong("s3"));
        System.out.println("playlist: " + player.showPlaylist());
        System.out.println("undo remove s3: " + player.undo());
        System.out.println("undo remove s2: " + player.undo());
        System.out.println("playlist: " + player.showPlaylist());
        System.out.println("undo add s3: " + player.undo());
        System.out.println("undo add s2: " + player.undo());
        System.out.println("undo add s1 (playing): " + player.undo());
        System.out.println("playlist: " + player.showPlaylist());

        System.out.println("--- Repeat mode ---");
        player = create();
        player.setRepeat(true);
        player.play("s3");
        System.out.println("next: " + player.next());
        System.out.println("next: " + player.next());
        player.setRepeat(false);
        player.play("s4");
        System.out.println("next, repeat off: " + player.next());

        System.out.println("--- Recently played ---");
        player = new MusicPlayer();
        for (int songNumber = 1; songNumber <= 12; songNumber++) player.addSong("s" + songNumber, "Song " + songNumber, "A");
        for (int songNumber = 1; songNumber <= 12; songNumber++) player.play("s" + songNumber);
        player.play("s5");
        System.out.println(player.recentlyPlayed());

        System.out.println("--- Top songs ---");
        player = create();
        player.play("s2");
        player.play("s1");
        player.play("s2");
        player.play("s3");
        player.play("s2");
        player.play("s1");
        System.out.println("top 2: " + player.topSongs(2));
        System.out.println("top 10: " + player.topSongs(10));

        System.out.println("--- Shuffle ---");
        player = create();
        player.queueLater("s1");
        player.shuffleUpNext();
        System.out.println("up next size: " + player.showUpNext().size());
        System.out.println("up next: " + player.showUpNext());
        System.out.println("playlist unchanged: " + player.showPlaylist());
    }
}

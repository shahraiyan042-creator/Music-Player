public class Test {
    public static void main(String[] args) {
        MusicPlayer player = new MusicPlayer();

        System.out.println("--- 1. Build the playlist ---");
        System.out.println(player.addSong("s1", "Night Drive", "Vela"));
        System.out.println(player.addSong("s2", "Slow Tide", "Marlow"));
        System.out.println(player.addSong("s3", "Copper Sky", "Vela"));
        System.out.println(player.addSong("s4", "Static Bloom", "Orrin"));
        System.out.println(player.addSong("s5", "Low Orbit", "Marlow"));
        System.out.println(player.addSong("s6", "Glass Rivers", "Orrin"));
        System.out.println("duplicate s3: " + player.addSong("s3", "Copper Sky", "Vela"));
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
        System.out.println(player.queueLater("s5"));
        System.out.println(player.queueLater("s6"));
        System.out.println(player.queueNext("s1"));
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
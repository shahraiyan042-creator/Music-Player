class Song {
    final String id;
    final String title;
    final String artist;
    //Constructor @param id, title, artist
    Song(String id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }
    //Custom toString : returns song title
    @Override
    public String toString() {
        return title;
    }
}

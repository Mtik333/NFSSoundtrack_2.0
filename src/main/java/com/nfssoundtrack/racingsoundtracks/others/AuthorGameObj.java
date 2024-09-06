package com.nfssoundtrack.racingsoundtracks.others;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Author;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;

import java.util.Map;

/**
 * object used to display table in countryInfo endpoint
 */
public class AuthorGameObj {

    private Author author;
    private int countSongs;
    private Map<Game, Integer> songsPerGame;

    public AuthorGameObj(Author author, int countSongs, Map<Game, Integer> songsPerGame) {
        this.author = author;
        this.countSongs = countSongs;
        this.songsPerGame = songsPerGame;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getCountSongs() {
        return countSongs;
    }

    public void setCountSongs(int countSongs) {
        this.countSongs = countSongs;
    }

    public Map<Game, Integer> getSongsPerGame() {
        return songsPerGame;
    }

    public void setSongsPerGame(Map<Game, Integer> songsPerGame) {
        this.songsPerGame = songsPerGame;
    }
}

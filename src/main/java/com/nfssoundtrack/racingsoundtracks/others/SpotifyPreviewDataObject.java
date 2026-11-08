package com.nfssoundtrack.racingsoundtracks.others;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Author;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;

public class SpotifyPreviewDataObject {

    private Long songId;
    private String fullAuthorName;
    private Long mainAuthorId;
    private String songTitle;
    private String spotifyId;
    private String srcId;

    public SpotifyPreviewDataObject(Song song, Author author) {
        this.songId = song.getId();
        this.fullAuthorName = author.getName();
        this.mainAuthorId = author.getId();
        this.songTitle = song.getOfficialDisplayTitle();
        this.spotifyId = song.getSpotifyId();
        this.srcId = song.getSrcId();
    }

    public SpotifyPreviewDataObject(Long songId, String fullAuthorName, Long mainAuthorId, String songTitle,
                                    String spotifyId, String srcId) {
        this.songId = songId;
        this.fullAuthorName = fullAuthorName;
        this.mainAuthorId = mainAuthorId;
        this.songTitle = songTitle;
        this.spotifyId = spotifyId;
        this.srcId = srcId;
    }

    public Long getSongId() {
        return songId;
    }

    public String getFullAuthorName() {
        return fullAuthorName;
    }

    public Long getMainAuthorId() {
        return mainAuthorId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public String getSrcId() {
        return srcId;
    }
}

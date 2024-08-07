package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorSong;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Role;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongGenre;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.List;

@JsonComponent
public class SongSerializer extends JsonSerializer<Song> {

    public static final String A_HREF = "<a href='";
    public static final String HREF_AUTHOR = "<a class='table_link' href='/author/";
    public static final String COMMASPAN_SPAN = "<span class=\"commaspan\">, </span>";

    @Override
    public void serialize(Song song, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("officialArtist", song.getOfficialDisplayBand());
        jsonGenerator.writeStringField("officialTitle", song.getOfficialDisplayTitle());
        if (song.getBaseSong() != null) {
            jsonGenerator.writeStringField("baseSongId",
                    "<a class='table_link' href='/song/" + song.getBaseSong().getId() + "'>"
                            + song.getBaseSong().getOfficialDisplayBand() + " - " +
                            song.getBaseSong().getOfficialDisplayTitle() + "</a>");
        }
        jsonGenerator.writeFieldName("composers");
        jsonGenerator.writeStartArray();
        song.getAuthorSongList().stream().filter(authorSong -> authorSong.getRole().equals(Role.COMPOSER)).forEach(
                authorSong -> {
                    try {
                        jsonGenerator.writeString(
                                HREF_AUTHOR + authorSong.getAuthorAlias().getAuthor().getId()
                                        + "'>" + authorSong.getAuthorAlias().getAlias() + "</a>");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        jsonGenerator.writeEndArray();
        jsonGenerator.writeFieldName("subcomposers");
        jsonGenerator.writeStartArray();
        List<AuthorSong> subcomposers = song.getAuthorSongList().stream().filter(
                authorSong -> authorSong.getRole().equals(Role.SUBCOMPOSER)).toList();
        for (int i = 0; i < subcomposers.size(); i++) {
            StringBuilder stringToWrite = new StringBuilder();
            if (i != subcomposers.size() - 1) {
                stringToWrite.append(COMMASPAN_SPAN);
            }
            stringToWrite.append(HREF_AUTHOR)
                    .append(subcomposers.get(i).getAuthorAlias().getAuthor().getId())
                    .append("'>")
                    .append(subcomposers.get(i).getAuthorAlias().getAlias()).append("</a>");
            jsonGenerator.writeString(stringToWrite.toString());
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeFieldName("remixers");
        jsonGenerator.writeStartArray();
        List<AuthorSong> remixers = song.getAuthorSongList().stream().filter(
                authorSong -> authorSong.getRole().equals(Role.REMIX)).toList();
        for (int i = 0; i < remixers.size(); i++) {
            StringBuilder stringToWrite = new StringBuilder();
            if (i != remixers.size() - 1) {
                stringToWrite.append(COMMASPAN_SPAN);
            }
            stringToWrite.append(HREF_AUTHOR).append(
                    remixers.get(i).getAuthorAlias().getAuthor().getId()).append("'>").append(
                    remixers.get(i).getAuthorAlias().getAlias()).append("</a>");
            jsonGenerator.writeString(stringToWrite.toString());
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeFieldName("featArtists");
        jsonGenerator.writeStartArray();
        List<AuthorSong> feats = song.getAuthorSongList().stream().filter(
                authorSong -> authorSong.getRole().equals(Role.FEAT)).toList();
        for (int i = 0; i < feats.size(); i++) {
            StringBuilder stringToWrite = new StringBuilder();
            if (i != feats.size() - 1) {
                stringToWrite.append(COMMASPAN_SPAN);
            }
            stringToWrite.append(HREF_AUTHOR).append(
                    feats.get(i).getAuthorAlias().getAuthor().getId()).append("'>").append(
                    feats.get(i).getAuthorAlias().getAlias()).append("</a>");
            jsonGenerator.writeString(stringToWrite.toString());
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeFieldName("genres");
        jsonGenerator.writeStartArray();
        List<SongGenre> songGenres = song.getSongGenreList();
        for (int i = 0; i < songGenres.size(); i++) {
            StringBuilder stringToWrite = new StringBuilder();
            if (i > 0) {
                stringToWrite.append(COMMASPAN_SPAN);
            }
            stringToWrite.append("<a class='table_link' href='/genre/").append(
                    songGenres.get(i).getGenre().getId()).append("'>").append(
                    songGenres.get(i).getGenre().getGenreName()).append("</a>");
            jsonGenerator.writeString(stringToWrite.toString());
        }
        jsonGenerator.writeEndArray();
        if (song.getSpotifyId() != null) {
            jsonGenerator.writeStringField("spotify", A_HREF + song.getSpotifyId() +
                    "'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/spotify_big.png'></a>");
        }
        if (song.getDeezerId() != null) {
            jsonGenerator.writeStringField("deezer", A_HREF + song.getDeezerId() +
                    "'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/deezer_big.png'></a>");
        }
        if (song.getItunesLink() != null) {
            jsonGenerator.writeStringField("itunes", A_HREF + song.getItunesLink() +
                    "' target='_blank'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/itunes_big.png'></a>");
        }
        if (song.getTidalLink() != null) {
            jsonGenerator.writeStringField("tidal", A_HREF + song.getTidalLink() +
                    "' target='_blank'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/tidal_big.png'></a>");
        }
        if (song.getSoundcloudLink() != null) {
            jsonGenerator.writeStringField("soundcloud", A_HREF + song.getSoundcloudLink() +
                    "' target='_blank'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/soundcloud_big.png'></a>");
        }
        if (song.getSrcId() != null) {
            jsonGenerator.writeStringField("youtube", "<a href='https://www.youtube.com/watch?v=" + song.getSrcId() +
                    "' target='_blank'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/youtube_big.png'></a>");
        }
        if (song.getBaseSong() != null) {
            jsonGenerator.writeStringField("remixOf", String.valueOf(song.getBaseSong().getId()));
        }
        jsonGenerator.writeEndObject();
    }
}

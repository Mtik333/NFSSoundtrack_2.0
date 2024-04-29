package com.nfssoundtrack.NFSSoundtrack_20.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorSong;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Role;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongGenre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.List;

@JsonComponent
public class SongSerializer extends JsonSerializer<Song> {

    private static final Logger logger = LoggerFactory.getLogger(SongSerializer.class);

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
                                "<a class='table_link' href='/author/" + authorSong.getAuthorAlias().getAuthor().getId()
                                        + "'>" + authorSong.getAuthorAlias().getAlias() + "</a>");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
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
                stringToWrite.append("<span>, </span>");
            }
            stringToWrite.append("<a class='table_link' href='/author/")
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
                stringToWrite.append("<span>, </span>");
            }
            stringToWrite.append("<a class='table_link' href='/author/").append(
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
                stringToWrite.append("<span>, </span>");
            }
            stringToWrite.append("<a class='table_link' href='/author/").append(
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
                stringToWrite.append("<span>, </span>");
            }
            stringToWrite.append("<a class='table_link' href='/genre/").append(
                    songGenres.get(i).getGenre().getId()).append("'>").append(
                    songGenres.get(i).getGenre().getGenreName()).append("</a>");
            jsonGenerator.writeString(stringToWrite.toString());
        }
        jsonGenerator.writeEndArray();
        if (song.getSpotifyId() != null) {
            jsonGenerator.writeStringField("spotify", "<a href='" + song.getSpotifyId() +
                    "'><img class='img-responsive-song-info' src='/images/fullres/spotify_big.png'></a>");
        }
        if (song.getDeezerId() != null) {
            jsonGenerator.writeStringField("deezer", "<a href='" + song.getDeezerId() +
                    "'><img class='img-responsive-song-info' src='/images/fullres/deezer_big.png'></a>");
        }
        if (song.getItunesLink() != null) {
            jsonGenerator.writeStringField("itunes", "<a href='" + song.getItunesLink() +
                    "' target='_blank'><img class='img-responsive-song-info' src='/images/fullres/itunes_big.png'></a>");
        }
        if (song.getTidalLink() != null) {
            jsonGenerator.writeStringField("tidal", "<a href='" + song.getTidalLink() +
                    "' target='_blank'><img class='img-responsive-song-info' src='/images/fullres/tidal_big.png'></a>");
        }
        if (song.getSoundcloudLink() != null) {
            jsonGenerator.writeStringField("soundcloud", "<a href='" + song.getSoundcloudLink() +
                    "' target='_blank'><img class='img-responsive-song-info' src='/images/fullres/soundcloud_big.png'></a>");
        }
        if (song.getSrcId() != null) {
            jsonGenerator.writeStringField("youtube", "<a href='https://www.youtube.com/watch?v=" + song.getSrcId() +
                    "' target='_blank'><img class='img-responsive-song-info' src='/images/fullres/youtube_big.png'></a>");
        }
        if (song.getBaseSong() != null) {
            jsonGenerator.writeStringField("remixOf", String.valueOf(song.getBaseSong().getId()));
        }
        jsonGenerator.writeEndObject();
    }
}

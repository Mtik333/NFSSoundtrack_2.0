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

/**
 * used to render modal with song info (click on 'i' icon next to song row)
 */
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
            // rarely we have this but we can go from remix to main song easily
            jsonGenerator.writeStringField("baseSongId",
                    "<a class='table_link' href='/song/" + song.getBaseSong().getId() + "'>"
                            + song.getBaseSong().getOfficialDisplayBand() + " - " +
                            song.getBaseSong().getOfficialDisplayTitle() + "</a>");
        }
        jsonGenerator.writeFieldName("composers");
        jsonGenerator.writeStartArray();
        // again for info page we render composers as clickable links
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
        writeComposers("subcomposers", jsonGenerator, song, Role.SUBCOMPOSER);
        writeComposers("remixers", jsonGenerator, song, Role.REMIX);
        writeComposers("featArtists", jsonGenerator, song, Role.FEAT);
        jsonGenerator.writeFieldName("genres");
        jsonGenerator.writeStartArray();
        // genres follow the same logic of render
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
        //for music streaming services we build a href with images
        writeMusicLink(song.getSpotifyId(), jsonGenerator, "spotify", A_HREF + song.getSpotifyId() +
                "'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/spotify_big.png'></a>");
        writeMusicLink(song.getDeezerId(), jsonGenerator, "deezer", A_HREF + song.getDeezerId() +
                "'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/deezer_big.png'></a>");
        writeMusicLink(song.getItunesLink(), jsonGenerator, "itunes", A_HREF + song.getItunesLink() +
                "' target='_blank'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/itunes_big.png'></a>");
        writeMusicLink(song.getTidalLink(), jsonGenerator, "tidal", A_HREF + song.getTidalLink() +
                "' target='_blank'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/tidal_big.png'></a>");
        writeMusicLink(song.getSoundcloudLink(), jsonGenerator, "soundcloud", A_HREF + song.getSoundcloudLink() +
                "' target='_blank'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/soundcloud_big.png'></a>");
        writeMusicLink(song.getSrcId(), jsonGenerator, "youtube", "<a href='https://www.youtube.com/watch?v=" + song.getSrcId() +
                "' target='_blank'><img class='img-responsive-song-info' src='https://racingsoundtracks.com/images/fullres/youtube_big.png'></a>");
        if (song.getBaseSong() != null) {
            jsonGenerator.writeStringField("remixOf", String.valueOf(song.getBaseSong().getId()));
        }
        jsonGenerator.writeEndObject();
    }

    private void writeMusicLink(String linkNull, JsonGenerator jsonGenerator,
                                String fieldName, String linkText) throws IOException {
        if (linkNull != null) {
            jsonGenerator.writeStringField(fieldName, linkText);
        }
    }

    private void writeComposers(String fieldName, JsonGenerator jsonGenerator,
                                Song song, Role role) throws IOException {
        // we make a href and then text representing the link
        jsonGenerator.writeFieldName(fieldName);
        jsonGenerator.writeStartArray();
        List<AuthorSong> composers = song.getAuthorSongList().stream().filter(
                authorSong -> authorSong.getRole().equals(role)).toList();
        for (int i = 0; i < composers.size(); i++) {
            StringBuilder stringToWrite = new StringBuilder();
            if (i != composers.size() - 1) {
                stringToWrite.append(COMMASPAN_SPAN);
            }
            stringToWrite.append(HREF_AUTHOR).append(
                    composers.get(i).getAuthorAlias().getAuthor().getId()).append("'>").append(
                    composers.get(i).getAuthorAlias().getAlias()).append("</a>");
            jsonGenerator.writeString(stringToWrite.toString());
        }
        jsonGenerator.writeEndArray();
    }
}

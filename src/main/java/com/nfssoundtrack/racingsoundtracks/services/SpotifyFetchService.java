package com.nfssoundtrack.racingsoundtracks.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Author;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorSong;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.others.SpotifyPreviewDataObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
public class SpotifyFetchService {

    private final SongService songService;
    private final AuthorService authorService;

    @Value("${spotify.clientId:}")
    private String spotifyClientId;

    @Value("${spotify.clientSecret:}")
    private String spotifyClientSecret;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String spotifyToken;
    private long spotifyTokenExpiresAt;

    public SpotifyFetchService(SongService songService, AuthorService authorService) {
        this.songService = songService;
        this.authorService = authorService;
    }


    public List<SpotifyPreviewDataObject> getSpotifyDataPreview(String spotifyId, boolean isAlbum) {
        List<SpotifyPreviewDataObject> dataObjects = new ArrayList<>();
        String uriToUse;
        if (isAlbum){
            uriToUse = "https://api.spotify.com/v1/albums/"+spotifyId+"/tracks";
        } else {
            uriToUse = "https://api.spotify.com/v1/playlists/"+spotifyId+"/items";
        }
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(uriToUse))
                    .header("Authorization", "Bearer " + getSpotifyToken())
                    .timeout(Duration.ofSeconds(15))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ResourceNotFoundException("Error connecting to Spotify API");
            }
            JsonNode json = objectMapper.readTree(response.body());
            if (isAlbum){
                dataObjects = getDataFromAlbum(json);
            } else {
                dataObjects = getDataFromPlaylist(json);
            }
        } catch (IOException | InterruptedException | ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
        return dataObjects;
    }

    private List<SpotifyPreviewDataObject> getDataFromPlaylist(JsonNode rootJson) throws ResourceNotFoundException {
        List<SpotifyPreviewDataObject> dataObjects = new ArrayList<>();
        JsonNode playlistTracks = rootJson.get("items");
        if (!(playlistTracks instanceof ArrayNode itemsArrayNode)) {
            throw new ResourceNotFoundException("No items on playlist");
        }
        Iterator<JsonNode> itemsIterator = itemsArrayNode.elements();
        while (itemsIterator.hasNext()) {
            JsonNode itemNode = itemsIterator.next();
            JsonNode actualItem = itemNode.get("item");
            String songTitle = actualItem.get("name").asText();
            String songUri = actualItem.get("uri").asText();
            ArrayNode artists = (ArrayNode) actualItem.get("artists");
            List<String> authors = new ArrayList<>();
            Iterator<JsonNode> artistsIterator = artists.elements();
            while (artistsIterator.hasNext()) {
                JsonNode artist = artistsIterator.next();
                String authorName = artist.get("name").asText();
                authors.add(authorName);
            }
            List<Song> existingSongs = songService.findBySpotifyId(songUri);
            SpotifyPreviewDataObject previewDataObject;
            if (!existingSongs.isEmpty()){
                Song firstSong = existingSongs.get(0);
                List<AuthorSong> authorSongs = firstSong.getAuthorSongList();
                AuthorSong authorSong = authorSongs.get(0);
                Author author = authorSong.getAuthorAlias().getAuthor();
                previewDataObject = new SpotifyPreviewDataObject(firstSong, author);
            } else {
                songTitle = cutUselessThingsFromTitle(songTitle);
                List<Song> potentialSongs = songService.findByOfficialDisplayBandAndOfficialDisplayTitleContains(
                        authors.get(0), songTitle);
                if (!potentialSongs.isEmpty()){
                    Song firstLookUpSong = potentialSongs.get(0);
                    List<AuthorSong> authorSongs = firstLookUpSong.getAuthorSongList();
                    AuthorSong authorSong = authorSongs.get(0);
                    Author author = authorSong.getAuthorAlias().getAuthor();
                    previewDataObject = new SpotifyPreviewDataObject(firstLookUpSong.getId(),author.getName(),
                            author.getId(),firstLookUpSong.getOfficialDisplayTitle(),firstLookUpSong.getSpotifyId(),
                            firstLookUpSong.getSrcId());
                } else {
                    Optional<Author> authorOptional = authorService.findByName(authors.get(0));
                    Long authorId=-1L;
                    if (authorOptional.isPresent()){
                        authorId=authorOptional.get().getId();
                    }
                    previewDataObject = new SpotifyPreviewDataObject(-1L,
                            authors.get(0),authorId,songTitle,songUri, "-1");
                }
            }
            dataObjects.add(previewDataObject);
        }
        return dataObjects;
    }

    private List<SpotifyPreviewDataObject> getDataFromAlbum(JsonNode rootJson) throws ResourceNotFoundException {
        List<SpotifyPreviewDataObject> dataObjects = new ArrayList<>();
        JsonNode playlistTracks = rootJson.get("items");
        if (!(playlistTracks instanceof ArrayNode itemsArrayNode)) {
            throw new ResourceNotFoundException("No items on playlist");
        }
        Iterator<JsonNode> itemsIterator = itemsArrayNode.elements();
        while (itemsIterator.hasNext()) {
            JsonNode itemNode = itemsIterator.next();
            String songTitle = itemNode.get("name").asText();
            String songUri = itemNode.get("uri").asText();
            ArrayNode artists = (ArrayNode) itemNode.get("artists");
            List<String> authors = new ArrayList<>();
            Iterator<JsonNode> artistsIterator = artists.elements();
            while (artistsIterator.hasNext()) {
                JsonNode artist = artistsIterator.next();
                String authorName = artist.get("name").asText();
                authors.add(authorName);
            }
            List<Song> existingSongs = songService.findBySpotifyId(songUri);
            SpotifyPreviewDataObject previewDataObject;
            if (!existingSongs.isEmpty()){
                Song firstSong = existingSongs.get(0);
                List<AuthorSong> authorSongs = firstSong.getAuthorSongList();
                AuthorSong authorSong = authorSongs.get(0);
                Author author = authorSong.getAuthorAlias().getAuthor();
                previewDataObject = new SpotifyPreviewDataObject(firstSong, author);
            } else {
                songTitle = cutUselessThingsFromTitle(songTitle);
                List<Song> potentialSongs = songService.findByOfficialDisplayBandAndOfficialDisplayTitleContains(
                        authors.get(0), songTitle);
                if (!potentialSongs.isEmpty()){
                    Song firstLookUpSong = potentialSongs.get(0);
                    List<AuthorSong> authorSongs = firstLookUpSong.getAuthorSongList();
                    AuthorSong authorSong = authorSongs.get(0);
                    Author author = authorSong.getAuthorAlias().getAuthor();
                    previewDataObject = new SpotifyPreviewDataObject(firstLookUpSong.getId(),author.getName(),
                            author.getId(),firstLookUpSong.getOfficialDisplayTitle(),firstLookUpSong.getSpotifyId(),
                            firstLookUpSong.getSrcId());
                } else {
                    Optional<Author> authorOptional = authorService.findByName(authors.get(0));
                    Long authorId=-1L;
                    if (authorOptional.isPresent()){
                        authorId=authorOptional.get().getId();
                    }
                    previewDataObject = new SpotifyPreviewDataObject(-1L,
                            authors.get(0),authorId,songTitle,songUri, "-1");
                }
            }
            dataObjects.add(previewDataObject);
        }
        return dataObjects;
    }

    private synchronized String getSpotifyToken() throws IOException, InterruptedException {
        if (spotifyToken != null && System.currentTimeMillis() < spotifyTokenExpiresAt) {
            return spotifyToken;
        }
        String credentials = Base64.getEncoder().encodeToString(
                (spotifyClientId + ":" + spotifyClientSecret).getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic " + credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .timeout(Duration.ofSeconds(15))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("spotify token request failed: " + response.statusCode() + " " + response.body());
        }
        JsonNode json = objectMapper.readTree(response.body());
        spotifyToken = json.path("access_token").asText();
        //refresh a minute before it really expires
        spotifyTokenExpiresAt = System.currentTimeMillis() + (json.path("expires_in").asLong(3600) - 60) * 1000;
        return spotifyToken;
    }

    private String cutUselessThingsFromTitle(String songTitle){
        String newTitle = songTitle;
        if (songTitle.contains("Remastered")){
            newTitle = newTitle.substring(0,songTitle.indexOf("Remastered"));
            newTitle = newTitle.substring(0,songTitle.lastIndexOf("-"));
        }
        return newTitle.trim();
    }

}

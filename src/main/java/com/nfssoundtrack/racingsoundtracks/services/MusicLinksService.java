package com.nfssoundtrack.racingsoundtracks.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.util.*;

/**
 * finds streaming links for songs
 * with spotify id: spotify gives us ISRC -> exact lookup on deezer, itunes search verified by duration,
 * then odesli.co embed page fills whatever is still missing (tidal, soundcloud etc.)
 * with youtube id: only odesli.co, youtube does not expose ISRC
 */
@Service
public class MusicLinksService {

    private static final Logger logger = LoggerFactory.getLogger(MusicLinksService.class);

    //our apple affiliate token, appended to every itunes link
    private static final String APPLE_AFFILIATE = "&at=11lJZP";
    //itunes search allows ~20 calls per minute
    private static final long ITUNES_MIN_INTERVAL_MS = 3000;
    private static final long DURATION_TOLERANCE_MS = 2000;

    @Value("${spotify.clientId:}")
    private String spotifyClientId;

    @Value("${spotify.clientSecret:}")
    private String spotifyClientSecret;

    @Value("${musiclinks.itunes.countries:us,pl,gb}")
    private String itunesCountries;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String spotifyToken;
    private long spotifyTokenExpiresAt;
    private long lastItunesCall;

    /**
     * links found during a single lookup, null means not found
     */
    private static class FoundLinks {
        String spotifyId;
        String deezerId;
        String itunesLink;
        String tidalLink;
        String soundcloudLink;

        boolean isComplete() {
            return spotifyId != null && deezerId != null && itunesLink != null
                    && tidalLink != null && soundcloudLink != null;
        }

        void fillMissingFrom(FoundLinks other) {
            if (spotifyId == null) spotifyId = other.spotifyId;
            if (deezerId == null) deezerId = other.deezerId;
            if (itunesLink == null) itunesLink = other.itunesLink;
            if (tidalLink == null) tidalLink = other.tidalLink;
            if (soundcloudLink == null) soundcloudLink = other.soundcloudLink;
        }

        //same behaviour as before - we overwrite what was found, never clear what was not
        void applyTo(SongSubgroup songSubgroup) {
            if (spotifyId != null) songSubgroup.setSpotifyId(spotifyId);
            if (deezerId != null) songSubgroup.setDeezerId(deezerId);
            if (itunesLink != null) songSubgroup.setItunesLink(itunesLink);
            if (tidalLink != null) songSubgroup.setTidalLink(tidalLink);
            if (soundcloudLink != null) songSubgroup.setSoundcloudLink(soundcloudLink);
        }
    }

    private record SpotifyTrack(String id, String isrc, String title, String artist, long durationMs) {
    }

    /**
     * @param spotifyId spotify:track:ID, open.spotify.com link or raw id
     */
    public void setLinksFromSpotify(String spotifyId, SongSubgroup songSubgroup) {
        String trackId = extractSpotifyTrackId(spotifyId);
        FoundLinks found = new FoundLinks();
        if (trackId != null) {
            found.spotifyId = "spotify:track:" + trackId;
            SpotifyTrack track = getSpotifyTrack(trackId);
            if (track != null) {
                if (track.isrc() != null) {
                    found.deezerId = findDeezerByIsrc(track.isrc());
                }
                found.itunesLink = findItunesLink(track);
                logger.info("spotify {} isrc {}: deezer {}, itunes {}", trackId, track.isrc(),
                        found.deezerId, found.itunesLink);
            }
        }
        if (!found.isComplete()) {
            String odesliInput = trackId != null ? "spotify:track:" + trackId : spotifyId;
            found.fillMissingFrom(getLinksFromOdesli(odesliInput));
        }
        found.applyTo(songSubgroup);
    }

    public void setLinksFromYoutube(String youtubeId, SongSubgroup songSubgroup) {
        getLinksFromOdesli("https://www.youtube.com/watch?v=" + youtubeId).applyTo(songSubgroup);
    }

    static String extractSpotifyTrackId(String spotifyId) {
        if (spotifyId == null || spotifyId.isBlank()) {
            return null;
        }
        String value = spotifyId.trim();
        int trackPos = value.indexOf("track:");
        if (trackPos > -1) {
            value = value.substring(trackPos + "track:".length());
        } else {
            trackPos = value.indexOf("track/");
            if (trackPos > -1) {
                value = value.substring(trackPos + "track/".length());
            }
        }
        int queryPos = value.indexOf('?');
        if (queryPos > -1) {
            value = value.substring(0, queryPos);
        }
        return value.matches("[A-Za-z0-9]{22}") ? value : null;
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

    private SpotifyTrack getSpotifyTrack(String trackId) {
        if (spotifyClientId.isBlank() || spotifyClientSecret.isBlank()) {
            logger.warn("spotify.clientId / spotify.clientSecret not set, using only odesli.co");
            return null;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.spotify.com/v1/tracks/" + trackId))
                    .header("Authorization", "Bearer " + getSpotifyToken())
                    .timeout(Duration.ofSeconds(15))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.warn("spotify track {} lookup failed: {} {}", trackId, response.statusCode(), response.body());
                return null;
            }
            JsonNode json = objectMapper.readTree(response.body());
            String isrc = json.path("external_ids").path("isrc").asText(null);
            String artist = json.path("artists").path(0).path("name").asText("");
            return new SpotifyTrack(trackId, isrc, json.path("name").asText(""), artist,
                    json.path("duration_ms").asLong());
        } catch (IOException e) {
            logger.error("spotify track {} lookup failed: {}", trackId, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private String findDeezerByIsrc(String isrc) {
        try {
            JsonNode json = getJson("https://api.deezer.com/track/isrc:" + isrc);
            if (json != null && json.has("id")) {
                return "deezer://www.deezer.com/track/" + json.path("id").asText();
            }
        } catch (IOException e) {
            logger.error("deezer isrc {} lookup failed: {}", isrc, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * itunes search has no ISRC filter, so we search by artist + title
     * and accept only the result with same artist, same title and duration within 2 seconds
     */
    private String findItunesLink(SpotifyTrack track) {
        String wantedArtist = normalize(track.artist());
        String wantedTitle = normalize(stripVersionInfo(track.title()));
        if (wantedArtist.isEmpty() || wantedTitle.isEmpty()) {
            return null;
        }
        String term = URLEncoder.encode(track.artist() + " " + stripVersionInfo(track.title()), StandardCharsets.UTF_8);
        for (String country : itunesCountries.split(",")) {
            country = country.trim().toLowerCase(Locale.ROOT);
            try {
                throttleItunes();
                JsonNode json = getJson("https://itunes.apple.com/search?entity=song&limit=25&country=" + country
                        + "&term=" + term);
                if (json == null) {
                    continue;
                }
                JsonNode best = null;
                long bestDiff = Long.MAX_VALUE;
                for (JsonNode result : json.path("results")) {
                    String artist = normalize(result.path("artistName").asText(""));
                    String title = normalize(stripVersionInfo(result.path("trackName").asText("")));
                    long diff = Math.abs(result.path("trackTimeMillis").asLong() - track.durationMs());
                    boolean artistMatches = !artist.isEmpty()
                            && (artist.contains(wantedArtist) || wantedArtist.contains(artist));
                    if (artistMatches && title.equals(wantedTitle) && diff <= DURATION_TOLERANCE_MS && diff < bestDiff) {
                        best = result;
                        bestDiff = diff;
                    }
                }
                if (best != null) {
                    return "https://geo.music.apple.com/" + country + "/album/_/" + best.path("collectionId").asText()
                            + "?i=" + best.path("trackId").asText() + APPLE_AFFILIATE;
                }
            } catch (IOException e) {
                logger.error("itunes search for {} ({}) failed: {}", track.id(), country, e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    private synchronized void throttleItunes() throws InterruptedException {
        long wait = lastItunesCall + ITUNES_MIN_INTERVAL_MS - System.currentTimeMillis();
        if (wait > 0) {
            Thread.sleep(wait);
        }
        lastItunesCall = System.currentTimeMillis();
    }

    /**
     * odesli public api is deprecated (401 PUBLIC_API_ACCESS_DEPRECATED), so we read
     * the json embedded in their embed page (__NEXT_DATA__) instead
     */
    private FoundLinks getLinksFromOdesli(String input) {
        FoundLinks found = new FoundLinks();
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://odesli.co/embed?url="
                            + URLEncoder.encode(input, StandardCharsets.UTF_8)))
                    .timeout(Duration.ofSeconds(20))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.warn("odesli.co returned {} for {}, maybe API limit violated?", response.statusCode(), input);
                return found;
            }
            Element nextData = Jsoup.parse(response.body()).getElementById("__NEXT_DATA__");
            if (nextData == null) {
                logger.warn("odesli.co page for {} has no __NEXT_DATA__, page format changed?", input);
                return found;
            }
            JsonNode sections = objectMapper.readTree(nextData.data())
                    .path("props").path("pageProps").path("pageData").path("sections");
            Map<String, String> urlByPlatform = new HashMap<>();
            for (JsonNode section : sections) {
                for (JsonNode link : section.path("links")) {
                    String url = link.path("url").asText(null);
                    if (url != null) {
                        urlByPlatform.putIfAbsent(link.path("platform").asText(), url);
                    }
                }
            }
            String spotify = urlByPlatform.get("spotify");
            if (spotify != null && extractSpotifyTrackId(spotify) != null) {
                found.spotifyId = "spotify:track:" + extractSpotifyTrackId(spotify);
            }
            String deezer = urlByPlatform.get("deezer");
            if (deezer != null) {
                found.deezerId = "deezer://" + stripQuery(deezer).replaceFirst("^https?://", "");
            }
            String apple = urlByPlatform.getOrDefault("appleMusic", urlByPlatform.get("itunes"));
            if (apple != null) {
                found.itunesLink = toAppleAffiliateLink(apple);
            }
            String tidal = urlByPlatform.get("tidal");
            if (tidal != null) {
                found.tidalLink = stripQuery(tidal);
            }
            found.soundcloudLink = urlByPlatform.get("soundcloud");
        } catch (IOException e) {
            logger.error("odesli.co lookup for {} failed: {}", input, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return found;
    }

    //keeps only the ?i=trackId parameter and adds our affiliate token
    private static String toAppleAffiliateLink(String url) {
        int queryPos = url.indexOf('?');
        if (queryPos < 0) {
            return url + "?" + APPLE_AFFILIATE.substring(1);
        }
        String base = url.substring(0, queryPos);
        for (String param : url.substring(queryPos + 1).split("&")) {
            if (param.startsWith("i=")) {
                return base + "?" + param + APPLE_AFFILIATE;
            }
        }
        return base + "?" + APPLE_AFFILIATE.substring(1);
    }

    private static String stripQuery(String url) {
        int queryPos = url.indexOf('?');
        return queryPos > -1 ? url.substring(0, queryPos) : url;
    }

    private JsonNode getJson(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(15)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            logger.warn("{} returned {}", url, response.statusCode());
            return null;
        }
        return objectMapper.readTree(response.body());
    }

    //"Song - 2004 Remaster" / "Song (Radio Edit)" / "Song [feat. X]" -> "Song"
    static String stripVersionInfo(String title) {
        String stripped = title.replaceAll("\\s*[(\\[].*?[)\\]]", "");
        int dashPos = stripped.indexOf(" - ");
        if (dashPos > 0) {
            stripped = stripped.substring(0, dashPos);
        }
        return stripped.trim();
    }

    static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replace("&", "and")
                .replaceAll("[^\\p{L}\\p{N}]", "");
    }
}

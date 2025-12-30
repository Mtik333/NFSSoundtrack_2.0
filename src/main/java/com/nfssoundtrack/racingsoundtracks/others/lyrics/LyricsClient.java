package com.nfssoundtrack.racingsoundtracks.others.lyrics;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class LyricsClient {
    private final Config config = ConfigFactory.load();
    private final HashMap<String, Lyrics> cache = new HashMap<>();
    private final OutputSettings noPrettyPrint = new OutputSettings().prettyPrint(false);
    private final Safelist newlineSafelist = Safelist.none().addTags("br", "p");
    private final String defaultSource, userAgent;
    private final int timeout;
    private static final Logger LOGGER = LoggerFactory.getLogger(LyricsClient.class);

    /**
     * Constructs a new {@link LyricsClient} using all defaults
     */
    public LyricsClient() {
        this(null, null);
    }

    /**
     * Constructs a new {@link LyricsClient}, specifying the default source
     * for lyrics
     *
     * @param defaultSource the default source for lyrics
     */
    public LyricsClient(String defaultSource) {
        this(defaultSource, null);
    }

    /**
     * Constructs a new {@link LyricsClient}, specifying an {@link Executor}
     * to be used for making {@link CompletableFuture}s
     *
     * @param executor the executor to use internally
     */
    public LyricsClient(Executor executor) {
        this(null, executor);
    }

    /**
     * Constructs a new {@link LyricsClient}, specifying the default source
     * for lyrics as well as an {@link Executor} to be used for making
     * {@link CompletableFuture}s
     *
     * @param defaultSource the default source for lyrics
     * @param executor      the executor to use internally
     */
    public LyricsClient(String defaultSource, Executor executor) {
        this.defaultSource = defaultSource == null ? config.getString("lyrics.default") : defaultSource;
        this.userAgent = config.getString("lyrics.user-agent");
        this.timeout = config.getInt("lyrics.timeout");
    }

    /**
     * Gets the lyrics for the provided search from the default source. To get lyrics
     * asynchronously, call {@link CompletableFuture#thenAccept(java.util.function.Consumer)}.
     * To block and return lyrics, use {@link CompletableFuture#get()}.
     *
     * @param search the song info to search for
     * @return a {@link CompletableFuture} to access the lyrics. The Lyrics object may be null if no lyrics were found.
     */
    public Lyrics getLyrics(String search) {
        return getLyrics(search, defaultSource);
    }

    public Lyrics getLrcLibLyrics(String search, Song song, String searchUrl) {
        String cacheKey = "LrcLib" + "||" + search;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            String lrcLibUrl = "https://lrclib.net/api/search";
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("user-agent", "RacingSoundtracks v1.0 (https://racingsoundtracks.com)");
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(lrcLibUrl)
                    .queryParam("track_name", URLEncoder.encode(
                            JustSomeHelper.removeVariousSpecialCharactersFromString(song.getOfficialDisplayTitle()),
                            StandardCharsets.UTF_8))
                    .queryParam("artist_name", URLEncoder.encode(
                            JustSomeHelper.removeVariousSpecialCharactersFromString(song.getOfficialDisplayBand()),
                            StandardCharsets.UTF_8));
            HttpEntity<String> stringHttpEntity = restTemplate.exchange(
                    builder.toUriString(), HttpMethod.GET, entity, String.class);
            String body = stringHttpEntity.getBody();
            JSONArray listOfFoundEntries = new JSONArray(body);
            if (listOfFoundEntries.isEmpty()) {
                return null;
            }
            JSONObject json = listOfFoundEntries.getJSONObject(0);
            String title = json.getString("trackName");
            String artist = json.getString("artistName");
            String songLyrics = json.optString("plainLyrics");
            if (songLyrics.isEmpty()){
                songLyrics = "Instrumental";
            }
            Lyrics lyrics = new Lyrics(title, artist,
                    songLyrics, searchUrl, "LrcLib");
            cache.put(cacheKey, lyrics);
            return lyrics;
        } catch (NullPointerException | JSONException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Gets the lyrics for the provided search from the provided source. To get lyrics
     * asynchronously, call {@link CompletableFuture#thenAccept(java.util.function.Consumer)}.
     * To block and return lyrics, use {@link CompletableFuture#get()}.
     *
     * @param search the song info to search for
     * @param source the source to use (must be defined in config)
     * @return a {@link CompletableFuture} to access the lyrics. The Lyrics object may be null if no lyrics were found.
     */
    public Lyrics getLyrics(String search, String source) {
        String cacheKey = source + "||" + search;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        try {
            String futureToken;
            boolean jsonSearch = config.getBoolean("lyrics." + source + ".search.json");
            String select = config.getString("lyrics." + source + ".search.select");
            String titleSelector = config.getString("lyrics." + source + ".parse.title");
            String authorSelector = config.getString("lyrics." + source + ".parse.author");
            String contentSelector = config.getString("lyrics." + source + ".parse.content");
            if (config.hasPath("lyrics." + source + ".token")) {
                futureToken = getToken(source);
            } else {
                futureToken = "";
            }
            String searchUrl = String.format(config.getString("lyrics." + source + ".search.url"), search, futureToken);
            Document doc;
            Connection connection = Jsoup.connect(searchUrl).userAgent(userAgent).timeout(timeout);
            if (jsonSearch) {
                String body = connection.ignoreContentType(true).execute().body();
                JSONObject json = new JSONObject(body);
                doc = Jsoup.parse(XML.toString(json));
            } else {
                doc = connection.get();
            }
            Element urlElement = doc.selectFirst(select);
            String url;
            if (jsonSearch) {
                url = urlElement.text();
            } else {
                url = urlElement.attr("abs:href");
            }
            if (url == null || url.isEmpty()) {
                return null;
            }
            doc = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get();
            Lyrics lyrics = new Lyrics(doc.selectFirst(titleSelector).ownText(),
                    doc.selectFirst(authorSelector).ownText(),
                    cleanWithNewlines(doc.selectFirst(contentSelector)),
                    url,
                    source);
            cache.put(cacheKey, lyrics);
            return lyrics;
        } catch (ConfigException ex) {
            LOGGER.error(ex.getMessage());
            throw new IllegalArgumentException(String.format("Source '%s' does not exist or is not configured correctly", source));
        } catch (Exception ignored) {
            LOGGER.error(ignored.getMessage());
            return null;
        }
    }

    private String getToken(String source) {
        try {
            String tokenUrl = config.getString("lyrics." + source + ".token.url");
            String select = config.getString("lyrics." + source + ".token.select");
            boolean textSearch = config.getBoolean("lyrics." + source + ".token.text");
            Pattern pattern = null;
            // Optional regex for post-processing
            // Helpful if token is not accessible using HTML accessors (e.g, inlined in a JS file)
            if (config.hasPath("lyrics." + source + ".token.regex")) {
                String regexPattern = config.getString("lyrics." + source + ".token.regex");
                pattern = Pattern.compile(regexPattern);
            }
            Connection connection = Jsoup.connect(tokenUrl).userAgent(userAgent).timeout(timeout);
            String body;
            if (textSearch) {
                body = connection.ignoreContentType(true).execute().body();
            } else {
                // HTML -- apply selectors to derive body string
                Document doc = connection.get();
                body = doc.selectFirst(select).ownText();
            }
            if (pattern != null) {
                Matcher matcher = pattern.matcher(body);
                if (matcher.find()) {
                    return matcher.group();
                }
            }
            return null;
        } catch (ConfigException ex) {
            throw new IllegalArgumentException(String.format("Source '%s' does not exist or is not configured correctly", source));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String cleanWithNewlines(Element element) throws JSONException {
        if (this.defaultSource.equals("Genius")) {
            Element irrelevantElem = element.firstElementChild();
            if (irrelevantElem == null) {
                throw new JSONException("what?");
            }
            Element attribution = irrelevantElem.selectFirst("button[class*=ContributorsCreditSong__Container]");
            if (attribution != null) {
                irrelevantElem.remove();
            }
        }
        return Jsoup.clean(Jsoup.clean(element.html(), newlineSafelist), "", Safelist.none(), noPrettyPrint);
    }
}
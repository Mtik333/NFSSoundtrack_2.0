package com.nfssoundtrack.racingsoundtracks.others;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * for this one i had to register my app through discogs (and obviously
 * create account there
 */
public class DiscoGSAPISearch {

    private static final Logger logger = LoggerFactory.getLogger(DiscoGSAPISearch.class);

    public static void main(String[] args) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        //basic URL to api is like this
        String uri = "https://api.discogs.com/database/search"; // or any other uri
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        //needed as in guides from discogs
        headers.add("user-agent", "NFSSoundtrack/1.0 +https://nfssoundtrack.com");
        Properties properties = new Properties();
        InputStream inputStream =
                DiscoGSAPISearch.class.getClassLoader().getResourceAsStream("application.properties");
        properties.load(inputStream);
        //for calling discogs we need to get key and secret password
        String discogsKey = properties.getProperty("discogs.key");
        String discogsSecret = properties.getProperty("discogs.secret");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        //we only use endpoint for purpose of artists
        //making 1 value return is shorter to handle
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("type", "artist")
                .queryParam("q", URLEncoder.encode("Sound Banks", StandardCharsets.UTF_8))
                .queryParam("key", discogsKey)
                .queryParam("secret", discogsSecret)
                .queryParam("per_page", 1)
                .queryParam("page", 1);
        HttpEntity<String> response1 = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, entity, String.class);
        String body1 = response1.getBody();
        ObjectMapper objectMapper1 = new ObjectMapper();
        Map<?, ?> something1 = objectMapper1.readValue(body1, Map.class);
        //at this point in argument 'results' we have what
        List<?> results = (List<?>) something1.get("results");
        if (!results.isEmpty()) {
            LinkedHashMap<?, ?> resultsMap = (LinkedHashMap<?, ?>) results.get(0);
            String artistName = String.valueOf(resultsMap.get("title"));
            Integer id = (Integer) resultsMap.get("id");
            //id can go up to 8 or 9 digits
            logger.debug("{}{}", id, artistName);
        }
        //125246
        String uri2 = "https://api.discogs.com/artists/346242"; // or any other uri
        UriComponentsBuilder builder2 = UriComponentsBuilder.fromUriString(uri2);
        //here having the url to artist, we can get all the details about him
        HttpEntity<String> response = restTemplate.exchange(
                builder2.toUriString(),
                HttpMethod.GET, entity, String.class);
        String body = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> something = objectMapper.readValue(body, Map.class);
        String linkToArtist = String.valueOf(something.get("uri"));
        String profile = String.valueOf(something.get("profile"));
        logger.debug("{}{}{}", response, linkToArtist, profile);
    }
}

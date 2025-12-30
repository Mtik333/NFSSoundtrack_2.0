package com.nfssoundtrack.racingsoundtracks.others.lyrics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;

/**
 * some prototype to submit lyrics to LrcLib
 */
public class LyricsSender {

    private static final String URL_TO_REQUEST_CHALLENGE = "https://lrclib.net/api/publish";
    private static final String URL_TO_SEND_LYRICS = "https://lrclib.net/api/request-challenge";
    private static final Logger LOGGER = LoggerFactory.getLogger(LyricsSender.class);

    public static void submitLyrics(String lyrics) throws MalformedURLException, URISyntaxException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> response = restTemplate.exchange(
                new URL(URL_TO_SEND_LYRICS).toURI(),
                HttpMethod.POST, null, String.class);
        String body = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> something = objectMapper.readValue(body, Map.class);
        String prefix = String.valueOf(something.get("prefix"));
        String target = String.valueOf(something.get("target"));
        String nonce = solveChallenge(prefix, target);
        if (nonce==null){
            return;
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Publish-Token", prefix + ":" + nonce);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        JSONObject object = new JSONObject();
        //TODO un-hardcode it in the future
        object.put("artistName", "Fury 66");
        object.put("trackName", "Blue Strip");
        object.put("albumName", "For Lack of a Better Word");
        object.put("duration", 160);
        object.put("plainLyrics", lyrics);
        HttpEntity<String> send = new HttpEntity<>(object.toString(), httpHeaders);
        HttpEntity<String> response2 = restTemplate.exchange(
                new URL(URL_TO_REQUEST_CHALLENGE).toURI(), HttpMethod.POST, send, String.class);
        LOGGER.info("response status of submitting lyrics: {}", response2);
    }

    private static boolean verifyNonce(byte[] result, byte[] target) {
        if (result.length != target.length) {
            return false;
        }
        for (int i = 0; i < result.length - 1; i++) {
            int r = Byte.toUnsignedInt(result[i]);
            int t = Byte.toUnsignedInt(target[i]);
            if (r > t) {
                return false;
            } else if (r < t) {
                break;
            }
        }
        return true;
    }

    private static String solveChallenge(String prefix, String targetHex) {
        int nonce = 0;
        byte[] target = hexStringToByteArray(targetHex.toUpperCase(Locale.ROOT));
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        while (true) {
            digest.reset();
            String input = prefix + nonce;
            byte[] hashed = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            if (verifyNonce(hashed, target)) {
                break;
            } else {
                nonce++;
            }
        }
        return Integer.toString(nonce);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}

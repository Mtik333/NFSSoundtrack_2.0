package com.nfssoundtrack.NFSSoundtrack_20.others;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

public class DiscoGSAPISearch {

	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
		String uri = "https://api.discogs.com/database/search"; // or any other uri
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("user-agent", "NFSSoundtrack/1.0 +https://nfssoundtrack.com");
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
				.queryParam("artist", "Nirvana");
		HttpEntity<String> response = restTemplate.exchange(
				builder.toUriString(),
				HttpMethod.GET, entity, String.class);
		System.out.println(response);
	}
}

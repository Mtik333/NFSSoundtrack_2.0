package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.controllers.WebsiteViewsController;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.others.DiscoGSObj;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorRepository;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.security.auth.login.LoginException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    @Autowired
    AuthorRepository authorRepository;

    @Value("${discogs.key}")
    private String discogsKey;

    @Value("${discogs.secret}")
    private String discogsSecret;

    @Value("${admin.discord.id}")
    private String adminId;
    @Autowired
    Map<Author, DiscoGSObj> discoGSObjMap;

    @Value("${bot.token}")
    private String botSecret;

    public Optional<Author> findById(int authorId) {
        return authorRepository.findById(authorId);
    }

    public Optional<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    public List<Author> findByNameContains(String name) {
        return authorRepository.findByNameContains(name);
    }

    public void delete(Author author) {
        authorRepository.delete(author);
    }

    public void deleteAll(List<Author> authors) {
        authorRepository.deleteAll(authors);
    }

    public Author save(Author author) {
        return authorRepository.save(author);
    }


    @CachePut(value = "discoGSMap")
    public DiscoGSObj fetchInfoFromMap(Author author) throws JsonProcessingException, LoginException, InterruptedException {
        Optional<Author> authorAlreadyThere =
                discoGSObjMap.keySet().stream().filter(author::equals).findFirst();
        DiscoGSObj discoGSObj;
        if (authorAlreadyThere.isPresent()) {
            discoGSObj = discoGSObjMap.get(authorAlreadyThere.get());
            if (discoGSObj == null) {
                Integer artistDiscogsId = retrieveArtistId(author.getName());
                if (artistDiscogsId != null) {
                    discoGSObj = obtainArtistLinkAndProfile(author.getName(), artistDiscogsId);
                    if (discoGSObj == null) {
                        discoGSObj = new DiscoGSObj(false, artistDiscogsId, null, "There was some internal error " +
                                "- you might reach out to admin so he can double check");
                    } else {
                        discoGSObjMap.put(author, discoGSObj);
                    }
                } else {
                    discoGSObj = new DiscoGSObj(true, 0, null,
                            author.getName() + " not found in DiscoGS database");
                    discoGSObjMap.put(author, discoGSObj);
                }
            } else {
                if (!discoGSObj.isNotInDiscogs()) {
                    discoGSObj = obtainArtistLinkAndProfile(author.getName(), discoGSObj.getArtistId());
                    if (discoGSObj == null) {
                        discoGSObj = new DiscoGSObj(null, "There was some internal error " +
                                "- you might reach out to admin so he can double check");
                    } else {
                        discoGSObjMap.put(author, discoGSObj);
                    }
                }
            }
        } else {
            Integer artistDiscogsId = retrieveArtistId(author.getName());
            if (artistDiscogsId != null) {
                discoGSObj = obtainArtistLinkAndProfile(author.getName(), artistDiscogsId);
                if (discoGSObj == null) {
                    discoGSObj = new DiscoGSObj(false, artistDiscogsId, null, "There was some internal error " +
                            "- you might reach out to admin so he can double check");
                }
                discoGSObjMap.put(author, discoGSObj);
            } else {
                discoGSObj = new DiscoGSObj(true, 0, null,
                        author.getName() + " not found in DiscoGS database");
                discoGSObjMap.put(author, discoGSObj);
            }
        }
        return discoGSObj;
    }

    private Integer retrieveArtistId(String authorName) throws JsonProcessingException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String uri = "https://api.discogs.com/database/search"; // or any other uri
            HttpEntity<String> entity = entityToGet();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
                    .queryParam("q", URLEncoder.encode(authorName, StandardCharsets.UTF_8))
                    .queryParam("type", "artist")
                    .queryParam("key", discogsKey)
                    .queryParam("secret", discogsSecret)
                    .queryParam("per_page", 5)
                    .queryParam("page", 1);
            HttpEntity<String> response1 = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET, entity, String.class);
            String body1 = response1.getBody();
            ObjectMapper objectMapper1 = new ObjectMapper();
            Map<?, ?> something1 = objectMapper1.readValue(body1, Map.class);
            List<?> results = (List<?>) something1.get("results");
            if (!results.isEmpty()) {
                LinkedHashMap<?, ?> resultsMap = (LinkedHashMap<?, ?>) results.get(0);
                String artistName = String.valueOf(resultsMap.get("title"));
                if (artistName.contains(")")) {
                    artistName = artistName.replaceAll("\\((.+?)\\)", "").trim();
                }
                if (artistName.contentEquals(authorName)) {
                    return (Integer) resultsMap.get("id");
                }
            } else {
                return null;
            }
        } catch (Exception exp) {
            logger.error("what exception? " + exp.getMessage());
            exp.printStackTrace();
        }
        return null;
    }
    private DiscoGSObj obtainArtistLinkAndProfile(String authorName, Integer id) throws JsonProcessingException, InterruptedException, LoginException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String uri2 = "https://api.discogs.com/artists/" + id;
            HttpEntity<String> entity = entityToGet();
            UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(uri2);
            HttpEntity<String> response = restTemplate.exchange(
                    builder2.toUriString(),
                    HttpMethod.GET, entity, String.class);
            String body = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<?, ?> something = objectMapper.readValue(body, Map.class);
            DiscoGSObj discoGSObj = new DiscoGSObj();
            String linkToArtist = String.valueOf(something.get("uri"));
            discoGSObj.setUri(linkToArtist);
            String profile = String.valueOf(something.get("profile"));
            if (profile==null || profile.isEmpty()) {
                profile = authorName + " has no profile description at DiscoGS";
            } else {
                profile = profile.replaceAll("\n","<br/>");
            }
            discoGSObj.setProfile(profile);
            List<String> urls = (List<String>) something.get("urls");
            if (urls != null && !urls.isEmpty()) {
                for (String localUrl : urls) {
                    if (localUrl.contains("facebook")) {
                        discoGSObj.setFacebook(localUrl);
                    } else if (localUrl.contains("twitter")) {
                        discoGSObj.setTwitter(localUrl);
                    } else if (localUrl.contains("instagram")) {
                        discoGSObj.setInstagram(localUrl);
                    } else if (localUrl.contains("soundcloud")) {
                        discoGSObj.setSoundcloud(localUrl);
                    } else if (localUrl.contains("myspace")) {
                        discoGSObj.setMyspace(localUrl);
                    } else if (localUrl.contains("wikipedia")) {
                        discoGSObj.setWikipedia(localUrl);
                    }
                }
            }
            return discoGSObj;
        } catch (Exception exp) {
            logger.error("what exception? " + exp.getMessage());
            if (WebsiteViewsController.JDA == null) {
                WebsiteViewsController.JDA = JDABuilder.createDefault(botSecret).build();
                WebsiteViewsController.JDA.awaitReady();
            }
            RestAction<Member> memberRestAction = WebsiteViewsController.JDA.getGuilds().get(0).retrieveMemberById(adminId);
            memberRestAction.queue(member -> {
                member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Error trying to obtainArtistLinkAndProfile on " +
                                "authorName " + authorName + " id + " + id + " WHY??? " + exp.getMessage()).queue());
                member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage(Arrays.toString(exp.getStackTrace()).substring(0,1800)).queue());
            });
            return null;
        }
    }

    private HttpEntity<String> entityToGet() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "NFSSoundtrack/1.0 +https://nfssoundtrack.com");
        return new HttpEntity<>("parameters", headers);
    }
}

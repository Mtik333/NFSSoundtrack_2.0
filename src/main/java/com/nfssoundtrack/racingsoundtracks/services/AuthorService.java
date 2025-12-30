package com.nfssoundtrack.racingsoundtracks.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.racingsoundtracks.controllers.WebsiteViewsController;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Author;
import com.nfssoundtrack.racingsoundtracks.others.AuthorToDiscoGSObj;
import com.nfssoundtrack.racingsoundtracks.others.DiscoGSObj;
import com.nfssoundtrack.racingsoundtracks.repository.AuthorRepository;
import com.nfssoundtrack.racingsoundtracks.serializers.AuthorToDiscoGSSerializer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);
    public static final String NOT_FOUND_IN_DISCO_GS_DATABASE = " not found in DiscoGS database";
    public static final String WHAT_EXCEPTION = "what exception? {}";
    public static final String RETRY_IN_2_MINUTES = "Seems there were too many requests to DiscoGS. Please retry in 2 minutes";

    private final AuthorRepository authorRepository;
    private final Map<Long, DiscoGSObj> discoGSObjMap;

    @Value("${discogs.key}")
    private String discogsKey;

    @Value("${discogs.secret}")
    private String discogsSecret;

    @Value("${admin.discord.id}")
    private String adminId;

    @Value("${bot.token}")
    private String botSecret;

    public AuthorService(AuthorRepository authorRepository, Map<Long, DiscoGSObj> discoGSObjMap) {
        this.authorRepository = authorRepository;
        this.discoGSObjMap = discoGSObjMap;
    }

    private static Instant lastError;

    public Optional<Author> findById(int authorId) {
        return authorRepository.findById(authorId);
    }

    public Optional<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    public List<Author> findAllByName(String name) {
        return authorRepository.findAllByName(name);
    }

    public List<Author> findByNameContains(String name) {
        return authorRepository.findByNameContains(name);
    }

    public List<Author> findByNameStartingWith(String name) {
        return authorRepository.findByNameStartingWith(name);
    }

    public void delete(Author author) {
        authorRepository.delete(author);
    }

    public void deleteAll(List<Author> authors) {
        authorRepository.deleteAll(authors);
    }

    public Author saveUpdate(Author author) {
        return authorRepository.save(author);
    }

    public Author save(Author author) {
        return authorRepository.save(author);
    }

    /**
     * a bit complicated method to handle fetching info about author from discogs (or local cache)
     *
     * @param author author we want to find in discogs
     * @return discogs representation of author
     * @throws LoginException
     * @throws InterruptedException
     */
    @CachePut(value = "discoGSMap")
    public DiscoGSObj fetchInfoFromMap(Author author) throws InterruptedException {
        //this map will keep info about authors already fetched from discogs
        Optional<Long> authorIdAlreadyThere = discoGSObjMap.keySet().stream().filter(aLong ->
                aLong.equals(author.getId())).findFirst();
        //if we want to ignore discogs for this author, we just return 'bland value'
//        if (Boolean.TRUE.equals(author.getSkipDiscogs())) {
//            discoGSObjMap.remove(author.getId());
//            return new DiscoGSObj(true, 0, null,
//                    author.getName() + NOT_FOUND_IN_DISCO_GS_DATABASE);
//        }
        DiscoGSObj discoGSObj;
        if (authorIdAlreadyThere.isPresent()) {
            //otherwise, if author is already in map, we check how discogs looks like
            discoGSObj = discoGSObjMap.get(authorIdAlreadyThere.get());
            if (discoGSObj == null) {
                discoGSObj = handleAuthorNotAlreadyThere(author);
            } else {
                //assuming author is in discogs
                if (!discoGSObj.isNotInDiscogs()) {
                    if (discoGSObj.getDiscogsId() == null) {
                        //i think i was notifying here the admin to manually put author info via admim panel
                        RestAction<Member> memberRestAction = WebsiteViewsController.getJda().getGuilds().get(0).retrieveMemberById(adminId);
                        DiscoGSObj finalDiscoGSObj = discoGSObj;
                        memberRestAction.queue(member -> member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                                .sendMessage("discoGSObj " + finalDiscoGSObj).queue()));
                        discoGSObjMap.remove(author.getId());
                    } else {
                        discoGSObj = handleApiOveruse(discoGSObj, author);
                    }
                }
            }
        } else {
            discoGSObj = handleAuthorNotThereYet(author);
        }
        return discoGSObj;
    }

    /**
     * logic to store author in map but with a message that will try to get the data again
     * when someone clicks on this author as maybe we overused the api threshold
     *
     * @param discoGSObj non-entity representing author
     * @param author     author entity
     * @return updated non-entity representing author
     * @throws LoginException
     * @throws InterruptedException
     */
    private DiscoGSObj handleApiOveruse(DiscoGSObj discoGSObj, Author author) throws InterruptedException {
        //in this case we just return discogs info as we have it already
        if (discoGSObj.getUri() != null) {
            return discoGSObj;
        }
        if (!checkIfCanQueryDiscoGS()) {
            discoGSObj = new DiscoGSObj(false, 0, null, RETRY_IN_2_MINUTES);
            return discoGSObj;
        }
        //we obtain the discogs info again? not sure if this is needed here
        discoGSObj = obtainArtistLinkAndProfile(author.getName(), discoGSObj.getDiscogsId());
        if (discoGSObj == null) {
            discoGSObj = new DiscoGSObj(null, "There was some internal error " +
                    "- you might reach out to admin so he can double check");
            updateLastError(Instant.now());
        } else {
            updateDiscoGSObj(author.getId(), discoGSObj, author.getSkipDiscogs());
        }
        return discoGSObj;
    }

    /**
     * method to handle situation when we stored this api overuse in the map but we try again to get
     * info about the author from discogs
     *
     * @param author author entity
     * @return updated non-entity representing author
     * @throws LoginException
     * @throws InterruptedException
     */
    private DiscoGSObj handleAuthorNotAlreadyThere(Author author) throws InterruptedException {
        DiscoGSObj discoGSObj;
        //in the earlier days people were clicking on authors causing discogs api to be overloaded
        if (!checkIfCanQueryDiscoGS()) {
            discoGSObj = new DiscoGSObj(false, 0, null, RETRY_IN_2_MINUTES);
            return discoGSObj;
        }
        //otherwise we try to find discogs id of author based on name we have
        Integer artistDiscogsId = retrieveArtistId(author.getName());
        if (artistDiscogsId != null) {
            //if we found id in discogs db, then we go for fetching the whole info about author from discogs
            discoGSObj = obtainArtistLinkAndProfile(author.getName(), artistDiscogsId);
            if (discoGSObj == null) {
                discoGSObj = new DiscoGSObj(false, artistDiscogsId, null, RETRY_IN_2_MINUTES);
                updateLastError(Instant.now());
            } else {
                updateDiscoGSObj(author.getId(), discoGSObj, author.getSkipDiscogs());
            }
        } else {
            //otherwise, means author is not in the discogs database
            //we store this info in map and json too
            discoGSObj = new DiscoGSObj(true, 0, null,
                    author.getName() + NOT_FOUND_IN_DISCO_GS_DATABASE);
            updateDiscoGSObj(author.getId(), discoGSObj, author.getSkipDiscogs());
        }
        return discoGSObj;
    }

    /**
     * first time we ask discogs about this author
     *
     * @param author author entity
     * @return updated non-entity representing author
     * @throws LoginException
     * @throws InterruptedException
     */
    private DiscoGSObj handleAuthorNotThereYet(Author author) throws InterruptedException {
        DiscoGSObj handledDiscoGSObj;
        //here author was not yet obtained from discogs db so we do our first look-up here
        if (!checkIfCanQueryDiscoGS()) {
            handledDiscoGSObj = new DiscoGSObj(false, 0, null, RETRY_IN_2_MINUTES);
            return handledDiscoGSObj;
        }
        //same story as above, we try to find discogs entity for author by his name
        Integer artistDiscogsId = retrieveArtistId(author.getName());
        if (artistDiscogsId != null) {
            //if it's there then we try to fetchh all the links and bio
            handledDiscoGSObj = obtainArtistLinkAndProfile(author.getName(), artistDiscogsId);
            if (handledDiscoGSObj == null) {
                //in case of unexpected error we will just store it in our discogs cache
                handledDiscoGSObj = new DiscoGSObj(false, artistDiscogsId, null, "There was some internal error " +
                        "- you might reach out to admin so he can double check");
                updateLastError(Instant.now());
            }
            updateDiscoGSObj(author.getId(), handledDiscoGSObj, author.getSkipDiscogs());
        } else {
            //otherwise, well cannot find author by his name
            //so admin will probably have to look up manually
            handledDiscoGSObj = new DiscoGSObj(true, 0, null,
                    author.getName() + NOT_FOUND_IN_DISCO_GS_DATABASE);
            updateDiscoGSObj(author.getId(), handledDiscoGSObj, author.getSkipDiscogs());
        }
        return handledDiscoGSObj;
    }

    public DiscoGSObj manuallyFetchDiscogsInfo(String authorName, Integer artistDiscogsId) throws InterruptedException {
        return obtainArtistLinkAndProfile(authorName, artistDiscogsId);
    }

    /**
     * to avoid problems with overuse of API we try to limit requests by a single minute
     *
     * @return true if we can again query discogs
     */
    private boolean checkIfCanQueryDiscoGS() {
        if (lastError != null) {
            Instant currentInstant = Instant.now();
            Duration timeElapsed = Duration.between(lastError, currentInstant);
            if (timeElapsed.toMinutes() > 1) {
                updateLastError(null);
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * method to get discogs id of author based on the name as input
     *
     * @param authorName
     * @return
     */
    private Integer retrieveArtistId(String authorName) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String uri = "https://api.discogs.com/database/search"; // or any other uri
            HttpEntity<String> entity = entityToGet();
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                    .queryParam("q", URLEncoder.encode(authorName, StandardCharsets.UTF_8))
                    .queryParam("type", "artist")
                    .queryParam("key", discogsKey)
                    .queryParam("secret", discogsSecret)
                    .queryParam("per_page", 5)
                    .queryParam("page", 1);
            HttpEntity<String> response1 = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET, entity, String.class);
            //no other clever way to do this, we use such uri builder
            String body1 = response1.getBody();
            ObjectMapper objectMapper1 = new ObjectMapper();
            Map<?, ?> something1 = objectMapper1.readValue(body1, Map.class);
            List<?> results = (List<?>) something1.get("results");
            if (!results.isEmpty()) {
                LinkedHashMap<?, ?> resultsMap = (LinkedHashMap<?, ?>) results.get(0);
                String artistName = String.valueOf(resultsMap.get("title"));
                //i don't remember the case but you know there can be some trims
                //and other stupid characters that would stop associating discogs entry with our author
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
            logger.error(WHAT_EXCEPTION, exp.getMessage());
            exp.printStackTrace();
        }
        return null;
    }

    /**
     * now we finally have id of author and we can get full info or author from discogs
     *
     * @param authorName name of author
     * @param id         id of author in discogs db
     * @return
     * @throws InterruptedException
     * @throws LoginException
     */
    public DiscoGSObj obtainArtistLinkAndProfile(String authorName, Integer id) throws InterruptedException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String uri2 = "https://api.discogs.com/artists/" + id;
            HttpEntity<String> entity = entityToGet();
            UriComponentsBuilder builder2 = UriComponentsBuilder.fromUriString(uri2);
            HttpEntity<String> response = restTemplate.exchange(
                    builder2.toUriString(),
                    HttpMethod.GET, entity, String.class);
            String body = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<?, ?> something = objectMapper.readValue(body, Map.class);
            DiscoGSObj discoGSObj = new DiscoGSObj();
            String linkToArtist = String.valueOf(something.get("uri"));
            discoGSObj.setUri(linkToArtist);
            discoGSObj.setDiscogsId(id);
            String profile = String.valueOf(something.get("profile"));
            //it is possible that author does not have profile description at discogs
            //so we put his name in front of such information
            if (profile == null || profile.isEmpty()) {
                profile = authorName + " has no profile description at DiscoGS";
            } else {
                profile = profile.replace("\n", "<br/>");
            }
            discoGSObj.setProfile(profile);
            List<String> urls = (List<String>) something.get("urls");
            //urls is actually a list of various social media links
            if (urls != null && !urls.isEmpty()) {
                discoGSObj.setLinks(urls, authorName);
            }
            return discoGSObj;
        } catch (Exception exp) {
            //if there's an error, then we tell admin about it
            logger.error(WHAT_EXCEPTION, exp.getMessage());
            WebsiteViewsController.rebuildJda(botSecret);
            RestAction<Member> memberRestAction = WebsiteViewsController.getJda().getGuilds().get(0).retrieveMemberById(adminId);
            memberRestAction.queue(member -> {
                member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Error trying to obtainArtistLinkAndProfile on " +
                                "authorName " + authorName + " id + " + id + " WHY??? " + exp.getMessage()).queue());
                member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage(Arrays.toString(exp.getStackTrace()).substring(0, 1800)).queue());
            });
            return null;
        }
    }

    /**
     * not sure why i extracted it this way
     *
     * @return
     */
    private HttpEntity<String> entityToGet() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "NFSSoundtrack/1.0 +https://nfssoundtrack.com");
        return new HttpEntity<>("parameters", headers);
    }

    /**
     * here we build json with all info about artist using discogs info
     *
     * @param artistId   id of artist
     * @param discoGSObj representation of author from discogs
     * @throws InterruptedException
     * @throws LoginException
     */
    private void createArtistJson(Long artistId, DiscoGSObj discoGSObj,
                                  Boolean ignoredForDiscoGs) throws InterruptedException {
        try {
            File folderFile = new File("discogs" + File.separator + artistId);
            if (!folderFile.exists()) {
                logger.debug("creating folder {}", folderFile.mkdir());
            }
            //so for each author (identified by id) we will create json file with all the info to render in profile
            //since we don't want to fetch this info on and on, we will cache on local disk and just read when needed
            try (RandomAccessFile randomDiscoGsFile = new RandomAccessFile(folderFile.getPath()
                    + File.separator + "discogs.json", "rw")) {
                File discoGsFile = new File(folderFile.getPath()
                        + File.separator + "discogs.json");
                if (discoGsFile.canWrite()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    SimpleModule simpleModule = new SimpleModule();
                    simpleModule.addSerializer(AuthorToDiscoGSObj.class, new AuthorToDiscoGSSerializer());
                    objectMapper.registerModule(simpleModule);
                    AuthorToDiscoGSObj authorToDiscoGSObj = new AuthorToDiscoGSObj(artistId, discoGSObj);
                    if (ignoredForDiscoGs==null){
                        authorToDiscoGSObj.setIgnoredByDiscogs(Boolean.FALSE);
                    } else {
                        authorToDiscoGSObj.setIgnoredByDiscogs(ignoredForDiscoGs);
                    }
                    String valueAsString = objectMapper.writeValueAsString(authorToDiscoGSObj);
                    randomDiscoGsFile.write(valueAsString.getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (IOException exp) {
            logger.error(WHAT_EXCEPTION, exp.getMessage());
            WebsiteViewsController.rebuildJda(botSecret);
            //not sure when and how this can happen but just for the record let's inform the admin
            RestAction<Member> memberRestAction = WebsiteViewsController.getJda().getGuilds().get(0).retrieveMemberById(adminId);
            memberRestAction.queue(member -> {
                member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Error trying to update map with discogs info - WHY??? " + exp.getMessage()).queue());
                member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage(Arrays.toString(exp.getStackTrace()).substring(0, 1800)).queue());
            });
        }
    }

    public void updateDiscoGSObj(Long artistId, DiscoGSObj updatedDiscoGSObj, Boolean ignoredByDiscoGs) throws InterruptedException {
        discoGSObjMap.put(artistId, updatedDiscoGSObj);
        createArtistJson(artistId, updatedDiscoGSObj, ignoredByDiscoGs);
    }

    private static void updateLastError(Instant instant) {
        lastError = instant;
    }

}

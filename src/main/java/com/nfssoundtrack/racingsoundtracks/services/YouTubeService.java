package com.nfssoundtrack.racingsoundtracks.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.nfssoundtrack.racingsoundtracks.controllers.WebsiteViewsController;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class YouTubeService {

    @Value("${admin.discord.id}")
    private String adminId;

    @Value("${bot.token}")
    private String botSecret;

    private static final String CLIENT_SECRETS = "/client_secret2.json";
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");
    private static final String APPLICATION_NAME = "ijustwantyoutubeapi";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static Credential credential;
    public static GoogleAuthorizationCodeFlow flow;

    public Credential authorize(final NetHttpTransport httpTransport,
                                String botSecret, String adminId) throws IOException, InterruptedException {
        // Load client secrets.
        InputStream in = YouTubeService.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .setAccessType("offline")
                        .setApprovalPrompt("force")
                        .build();
        String url = flow.newAuthorizationUrl().setRedirectUri("https://racingsoundtracks.com/googleCallback").build();
        WebsiteViewsController.rebuildJda(botSecret);
        RestAction<Member> memberRestAction = WebsiteViewsController.getJda().getGuilds().get(0).retrieveMemberById(adminId);
        memberRestAction.queue(member -> {
            member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                    .sendMessage("Please open the following address in your browser:" + url).queue());
        });
        while (credential==null){

        }
        return credential;
    }

    public YouTube getService(String botSecret, String adminId) throws GeneralSecurityException, IOException, InterruptedException {
        final NetHttpTransport httpTransport = new NetHttpTransport();
        if (credential==null){
            credential = authorize(httpTransport, botSecret, adminId);
        }
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Set<Song> createSongsFromYTPlaylist(String ytLink, String tempAuthor,
                                                String botSecret, String adminId) throws Exception {
        Set<Song> preparedSongs = new HashSet<>();
        YouTube youtubeService = getService(botSecret, adminId);
        YouTube.PlaylistItems.List playlistRequest = youtubeService.playlistItems()
                .list(Collections.singletonList("contentDetails,snippet,id,status"));
        playlistRequest.setMaxResults(300L);
        PlaylistItemListResponse playlistResponse = playlistRequest.setPlaylistId(ytLink).execute();
        Integer totalResults = playlistResponse.getPageInfo().getTotalResults();
        while (totalResults>0){
            SSLContext sslContext = SSLContext.getInstance("TLS");
            final Properties props = System.getProperties();
            props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
            sslContext.init(null, getTrustManager(), new SecureRandom());
            List<PlaylistItem> playlistItems = playlistResponse.getItems();
            if (playlistItems.size() == 0) {
                throw new Exception("that's not right");
            }
            for (PlaylistItem item : playlistItems){
                PlaylistItemStatus status = item.getStatus();
                if ("private".equals(status.getPrivacyStatus())){
                    continue;
                }
                PlaylistItemSnippet snippet = item.getSnippet();
                PlaylistItemContentDetails contentDetails = item.getContentDetails();
                String videoTitle = snippet.getTitle();
                int firstBracketOf = videoTitle.indexOf(")");
                if (firstBracketOf!=-1){
                    videoTitle = videoTitle.substring(firstBracketOf+1).trim();
                }
                int firstMinusOf = videoTitle.indexOf("-");
                if (firstMinusOf==-1){
                    continue;
                }
                String songTitle = videoTitle.substring(firstMinusOf+1).trim();
                Song song = new Song(tempAuthor, songTitle, contentDetails.getVideoId(), null);
                preparedSongs.add(song);
            }
            totalResults = totalResults-playlistItems.size();
            if (playlistResponse.getNextPageToken()!=null){
                playlistRequest.setPageToken(playlistResponse.getNextPageToken());
                playlistResponse = playlistRequest.setPlaylistId(ytLink).execute();
            }
        }
        return preparedSongs;
    }

    public Song createSongFromYTLink(String ytLink, String tempAuthor,
                                     String botSecret, String adminId) throws Exception {

        YouTube youtubeService = getService(botSecret, adminId);
        YouTube.Videos.List request = youtubeService.videos()
                .list(Collections.singletonList("snippet,statistics,contentDetails,status,topicDetails,recordingDetails"));
        VideoListResponse response = request.setId(Collections.singletonList(ytLink)).execute();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        final Properties props = System.getProperties();
        props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        sslContext.init(null, getTrustManager(), new SecureRandom());
        List<Video> videos = response.getItems();
        if (videos.size() != 1) {
            throw new Exception("that's not right");
        }
        Video video = videos.get(0);
        VideoSnippet snippet = video.getSnippet();
        String videoTitle = snippet.getTitle();
        int firstBracketOf = videoTitle.indexOf(")");
        if (firstBracketOf!=-1){
            videoTitle = videoTitle.substring(firstBracketOf+1).trim();
        }
        String songTitle = videoTitle.split("-")[1].trim();
        Song song = new Song(tempAuthor, songTitle, ytLink, null);
        return song;
    }

    public static TrustManager[] getTrustManager() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
    }
}
